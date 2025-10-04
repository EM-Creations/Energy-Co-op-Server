package uk.co.emcreations.energycoop.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import uk.co.emcreations.energycoop.model.Site;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
public class SavingsRate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Site site;

    @Column(nullable = false)
    private double ratePerKWH;

    @Column(nullable = false)
    private LocalDate effectiveDate;
}
