package mirunq_png.perfumeapp.db;

import jakarta.transaction.Transactional;
import mirunq_png.perfumeapp.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>
{
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    @Transactional
    void deleteByFamilyId(UUID familyId);
}
