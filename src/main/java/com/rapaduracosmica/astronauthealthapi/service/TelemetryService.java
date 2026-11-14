package com.rapaduracosmica.astronauthealthapi.service;

import com.rapaduracosmica.astronauthealthapi.client.AnalyticsClient; // 1. Importa o Client
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
    private final AnalyticsClient analyticsClient; // 2. Injeta o AnalyticsClient aqui

    public TelemetryRecord saveTelemetry(TelemetryRecord record){
        return repository.save(record);
    }

    public TelemetryRecord getLatestTelemetry(String astronautId) {
        if (astronautId != null && !astronautId.isBlank()) {
            return repository.findTopByAstronautIdOrderByIdDesc(astronautId).orElse(null);
        }
        return repository.findTopByOrderByIdDesc().orElse(null);
    }

    // 3. Orquestra: Recebe os dados, manda pro Python analisar e salva no H2
    public TelemetryRecord processAndSaveTelemetry(TelemetryAnalysisDTO inputDto) {
        // 3. Usa o analyticsClient injetado para mandar os dados para o Python
        TelemetryAnalysisDTO analyzedDto = analyticsClient.analyzeTelemetry(inputDto);

        // Converte para a entidade do banco de dados
        TelemetryRecord record = new TelemetryRecord();
        record.setAstronautId(analyzedDto.getAstronautId());
        record.setTimestamp(analyzedDto.getTimestamp());
        record.setHeartRate(analyzedDto.getHeartRate());
        record.setSpo2(analyzedDto.getSpo2());
        record.setRadiationDoseMsv(analyzedDto.getRadiationDoseMsv());
        record.setSleepHours(analyzedDto.getSleepHours());
        record.setRiskLevel(analyzedDto.getRiskLevel());         // Veio do Python
        record.setAlertMessage(analyzedDto.getAlertMessage());   // Veio do Python

        // Salva no H2
        return repository.save(record);
    }

    // 4. Retorna a lista completa de histórico para os gráficos
    public List<TelemetryRecord> getAllTelemetry() {
        return repository.findAll();
    }
}