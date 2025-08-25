package uk.co.emcreations.energycoop.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDate;
import java.util.Optional;

public interface MemberOwnershipRepository extends JpaRepository<MemberOwnership, Long> {
    Optional<MemberOwnership> findTopByUserIdAndSiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            final String userId, final Site site, final LocalDate date);
}
