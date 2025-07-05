package uk.co.emcreations.energycoop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import uk.co.emcreations.energycoop.model.Site;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GenerationStatEntry implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private LocalDate timestamp;

    @Column(nullable = false)
    private int wattsGenerated;

    @Column(nullable = false)
    private Site site;
}
