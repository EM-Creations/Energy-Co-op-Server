package uk.co.emcreations.energycoop.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDate;
import java.util.Optional;

public interface SavingsRateRepository extends JpaRepository<SavingsRate, Long> {
    Optional<SavingsRate> findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            final Site site, final LocalDate date);
}
