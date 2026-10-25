package mirunq_png.perfumeapp.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import mirunq_png.perfumeapp.db.RefreshTokenRepository;
import mirunq_png.perfumeapp.db.UserRepository;
import mirunq_png.perfumeapp.model.RefreshToken;
import mirunq_png.perfumeapp.model.User;
import mirunq_png.perfumeapp.model.dto.AuthResponse;
import mirunq_png.perfumeapp.model.dto.LoginRequest;
import mirunq_png.perfumeapp.security.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          RefreshTokenRepository refreshTokenRepository)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request)
    {
        try
        {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password."));
        }

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String accessToken = jwtUtils.generateAccessToken(user.getUsername());
        String rawRefreshToken = issueRefreshToken(user.getId(), UUID.randomUUID());

        ResponseCookie refreshCookie = buildRefreshCookie(rawRefreshToken, 60L * 60 * 24 * 30); // 30 days

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("accessToken", accessToken, "username", user.getUsername()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = false) String rawRefreshToken)
    {
        if (rawRefreshToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(rawRefreshToken)).orElse(null);

        if (stored == null || stored.getExpiresAt().isBefore(Instant.now()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (stored.isUsed()) // reuse detected: kill the whole chain
        {
            refreshTokenRepository.deleteByFamilyId(stored.getFamilyId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Session invalidated. Please log in again."));
        }

        User user = userRepository.findById(stored.getUserId()).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        stored.setUsed(true);
        refreshTokenRepository.save(stored);

        String newAccessToken = jwtUtils.generateAccessToken(user.getUsername());
        String newRawRefreshToken = issueRefreshToken(user.getId(), stored.getFamilyId());

        ResponseCookie refreshCookie = buildRefreshCookie(newRawRefreshToken, 60L * 60 * 24 * 30);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("accessToken", newAccessToken, "username", user.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String rawRefreshToken)
    {
        if (rawRefreshToken != null)
        {
            refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                    .ifPresent(t -> refreshTokenRepository.deleteByFamilyId(t.getFamilyId()));
        }
        ResponseCookie cleared = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(false).path("/api/auth/refresh").maxAge(0).sameSite("Lax").build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .body(Map.of("message", "Logged out successfully."));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request)
    {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already taken."));

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully!"));
    }

    private String issueRefreshToken(Integer userId, UUID familyId)
    {
        String raw = UUID.randomUUID() + "." + UUID.randomUUID(); // opaque token for refresh
        RefreshToken rt = new RefreshToken();
        rt.setTokenHash(hash(raw));
        rt.setUserId(userId);
        rt.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        rt.setFamilyId(familyId);
        refreshTokenRepository.save(rt);
        return raw;
    }

    private ResponseCookie buildRefreshCookie(String value, long maxAgeSeconds)
    {
        return ResponseCookie.from("refresh_token", value)
                .httpOnly(true)
                .secure(false) // will be changed later
                .path("/api/auth/refresh")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")
                .build();
    }

    private String hash(String raw)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }
}
