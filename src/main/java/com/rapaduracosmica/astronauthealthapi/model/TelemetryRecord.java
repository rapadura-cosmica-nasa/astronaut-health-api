package com.rapaduracosmica.astronauthealthapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_telemetria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String astronautId;
    private LocalDateTime timestamp;
    private int heartRate;
    private int spo2;
    private double radiationDoseMsv;
    private double sleepHours;
    private String riskLevel;
    private String recommendedAction;

    @Column(length = 500)
    private String alertMessage;
}
