package mirunq_png.perfumeapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mirunq_png.perfumeapp.db.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter
{
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtils jwtUtils, UserRepository userRepository)
    {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException
//    {
//        String token = extractTokenFromCookie(request);
//
//        if (token != null && jwtUtils.isTokenValid(token))
//        {
//            String username = jwtUtils.extractUsername(token);
//            userRepository.findByUsername(username).ifPresent(user ->
//            {
//                UsernamePasswordAuthenticationToken auth =
//                        new UsernamePasswordAuthenticationToken(
//                                user,        // principal — this is what getPrincipal() returns later
//                                null,        // credentials — null since we already validated the token
//                                List.of()    // authorities — empty for now, no roles yet
//                        );
//                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            });
//        }
//
//        filterChain.doFilter(request, response);
//    }
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException
{
    // debug to be deleted
//    System.out.println("=== JwtFilter === " + request.getRequestURI());
//    Cookie[] cookies = request.getCookies();
//    if (cookies == null) {
//        System.out.println("No cookies received");
//    } else {
//        for (Cookie c : cookies)
//            System.out.println("Cookie: " + c.getName());
//    }
//    String tokenn = extractTokenFromCookie(request);
//    System.out.println("Token extracted: " + (tokenn != null ? "yes" : "no"));
//    if (tokenn != null) {
//        System.out.println("Token valid: " + jwtUtils.isTokenValid(tokenn));
//    }
    try
    {
        String token = extractTokenFromCookie(request);
        if (token != null && jwtUtils.isTokenValid(token))
        {
            String username = jwtUtils.extractUsername(token);
            userRepository.findByUsername(username).ifPresent(user ->
            {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user, null, List.of()
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }
        filterChain.doFilter(request, response);
    }
    catch (Exception e)
    {
        System.err.println("JwtFilter error: " + e.getMessage());
        e.printStackTrace();
        filterChain.doFilter(request, response); // continue even on error
    }
}

    private String extractTokenFromCookie(HttpServletRequest request)
    {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies())
            if ("jwt".equals(cookie.getName()))
                return cookie.getValue();
        return null;
    }


}
