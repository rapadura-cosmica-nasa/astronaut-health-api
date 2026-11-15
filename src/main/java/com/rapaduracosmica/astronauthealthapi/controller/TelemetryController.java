package com.rapaduracosmica.astronauthealthapi.controller;

import com.rapaduracosmica.astronauthealthapi.dto.TelemetryAnalysisDTO;
import com.rapaduracosmica.astronauthealthapi.model.TelemetryRecord;
import com.rapaduracosmica.astronauthealthapi.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telemetry")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TelemetryController {
    private final TelemetryService service;

    @PostMapping
    public ResponseEntity<TelemetryRecord> createTelemetry(@RequestBody TelemetryAnalysisDTO dto) {
        TelemetryRecord savedRecord = service.processAndSaveTelemetry(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
    }

    @GetMapping("/latest")
    public ResponseEntity<TelemetryRecord> getLatestTelemetry(
            @RequestParam(required = false) String astronautId) {

        TelemetryRecord latest = service.getLatestTelemetry(astronautId);
        if (latest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(latest);
    }
}