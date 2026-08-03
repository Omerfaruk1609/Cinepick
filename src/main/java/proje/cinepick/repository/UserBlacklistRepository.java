package proje.cinepick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proje.cinepick.entity.UserBlacklist;

import java.util.Optional;

@Repository
public interface UserBlacklistRepository extends JpaRepository<UserBlacklist, Long> {
    Optional<UserBlacklist> findByUserId(Long userId);
}
