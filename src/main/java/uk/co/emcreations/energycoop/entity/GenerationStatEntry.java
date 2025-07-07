package uk.co.emcreations.energycoop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uk.co.emcreations.energycoop.model.Site;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class GenerationStatEntry implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private double wattsGenerated;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Site site;
}
