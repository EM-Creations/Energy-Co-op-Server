package uk.co.emcreations.energycoop.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findTop30BySiteOrderByCreatedAtDesc(Site site);

    Optional<Alert> findFirstBySiteAndCreatedAtBetweenOrderByCreatedAtDesc(Site site, LocalDateTime start, LocalDateTime end);
}
