package com.rapaduracosmica.astronauthealthapi.service;

import com.rapaduracosmica.astronauthealthapi.client.AnalyticsClient; 
import com.rapaduracosmica.astronauthealthapi.dto.TelemetryAnalysisDTO;
import com.rapaduracosmica.astronauthealthapi.model.TelemetryRecord;
import com.rapaduracosmica.astronauthealthapi.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final TelemetryRepository repository;
    private final AnalyticsClient analyticsClient; 

    public TelemetryRecord saveTelemetry(TelemetryRecord record){
        return repository.save(record);
    }

    public TelemetryRecord getLatestTelemetry(String astronautId) {
        if (astronautId != null && !astronautId.isBlank()) {
            return repository.findTopByAstronautIdOrderByIdDesc(astronautId).orElse(null);
        }
        return repository.findTopByOrderByIdDesc().orElse(null);
    }

    public TelemetryRecord processAndSaveTelemetry(TelemetryAnalysisDTO inputDto) {
        
        TelemetryAnalysisDTO analyzedDto = analyticsClient.analyzeTelemetry(inputDto);
        
        TelemetryRecord record = new TelemetryRecord();
        record.setAstronautId(analyzedDto.getAstronautId());
        record.setTimestamp(analyzedDto.getTimestamp());
        record.setHeartRate(analyzedDto.getHeartRate());
        record.setSpo2(analyzedDto.getSpo2());
        record.setRadiationDoseMsv(analyzedDto.getRadiationDoseMsv());
        record.setSleepHours(analyzedDto.getSleepHours());
        record.setRiskLevel(analyzedDto.getRiskLevel());         
        record.setAlertMessage(analyzedDto.getAlertMessage());   
        record.setRecommendedAction(analyzedDto.getRecommendedAction());

        return repository.save(record);
    }

    public List<TelemetryRecord> getAllTelemetry() {
        return repository.findAll();
    }
}