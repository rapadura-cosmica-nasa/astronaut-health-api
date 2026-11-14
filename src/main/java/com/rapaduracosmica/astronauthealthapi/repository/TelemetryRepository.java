package com.rapaduracosmica.astronauthealthapi.repository;

import com.rapaduracosmica.astronauthealthapi.model.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryRecord, Long> {
    Optional<TelemetryRecord> findTopByAstronautIdOrderByIdDesc(String astronautId);
    Optional<TelemetryRecord> findTopByOrderByIdDesc();
}