package com.rapaduracosmica.astronauthealthapi.service;

import com.rapaduracosmica.astronauthealthapi.model.TelemetryRecord;
import com.rapaduracosmica.astronauthealthapi.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final TelemetryRepository repository;

    public TelemetryRecord saveTelemetry(TelemetryRecord record){
        return repository.save(record);
    }
    public TelemetryRecord getLatestTelemetry(String astronautId) {
        if (astronautId != null && !astronautId.isBlank()) {
            return repository.findTopByAstronautIdOrderByIdDesc(astronautId).orElse(null);
        }
        return repository.findTopByOrderByIdDesc().orElse(null);
    }
}
