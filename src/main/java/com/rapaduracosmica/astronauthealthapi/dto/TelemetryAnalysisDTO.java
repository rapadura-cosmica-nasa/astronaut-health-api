package com.rapaduracosmica.astronauthealthapi.dto;

import java.time.LocalDateTime;

public class TelemetryAnalysisDTO {
    private String astronautId;
    private LocalDateTime timestamp;
    private int heartRate;
    private int spo2;
    private double radiationDoseMsv;
    private double sleepHours;
    private String riskLevel;
    private String alertMessage;
    private String recommendedAction;

    public String getAstronautId() { return astronautId; }
    public void setAstronautId(String astronautId) { this.astronautId = astronautId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }

    public int getSpo2() { return spo2; }
    public void setSpo2(int spo2) { this.spo2 = spo2; }

    public double getRadiationDoseMsv() { return radiationDoseMsv; }
    public void setRadiationDoseMsv(double radiationDoseMsv) { this.radiationDoseMsv = radiationDoseMsv; }

    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
}