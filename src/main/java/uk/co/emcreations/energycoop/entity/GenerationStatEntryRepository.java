package uk.co.emcreations.energycoop.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDateTime;

public interface GenerationStatEntryRepository extends JpaRepository<GenerationStatEntry, Long> {
    GenerationStatEntry findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(
            Site site, LocalDateTime start, LocalDateTime end);
}
