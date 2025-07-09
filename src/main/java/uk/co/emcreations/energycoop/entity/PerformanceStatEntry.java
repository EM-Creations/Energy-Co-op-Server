package uk.co.emcreations.energycoop.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import uk.co.emcreations.energycoop.model.Site;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class PerformanceStatEntry implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Site site;

    @Column(nullable = false)
    private LocalDateTime forDate;

    @Column(nullable = false)
    private double wattsGenerated;

    @Column(nullable = false)
    private double availability;

    @Column(nullable = false)
    private double averagePower;

    @Column(nullable = false)
    private double maxPower;

    @Column(nullable = false)
    private double averageWind;

    @Column(nullable = false)
    private double maxWind;

    @Column(nullable = false)
    private double powerProductionTime;

    @Column(nullable = false)
    private double lowWindTime;

    @Column(nullable = false)
    private double errorTime;

    @Column(nullable = false)
    private double serviceTime;

    @Column(nullable = false)
    private double iceTime;

    @Column(nullable = false)
    private double stormTime;

    @Column(nullable = false)
    private double shadowTime;

    @Column(nullable = false)
    private double twistTime;

    @Column(nullable = false)
    private double gridFailureTime;

    @Column(nullable = false)
    private double commFailureTime;

    @Column(nullable = false)
    private double visitTime;

    @Column(nullable = false)
    private double serverStopTime;

    @Column(nullable = false)
    private double fireTime;

    @Column(nullable = false)
    private double batMonitoringTime;

    @Column(nullable = false)
    private double nightShutdownTime;
}
