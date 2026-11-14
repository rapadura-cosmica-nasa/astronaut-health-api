package com.rapaduracosmica.astronauthealthapi.repository;

import com.rapaduracosmica.astronauthealthapi.model.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelemetryRepository
        extends JpaRepository<TelemetryRecord, Long> {

    // Busca a última medição pelo maior ID do astronauta
    Optional<TelemetryRecord> findTopByAstronautIdOrderByIdDesc(
            String astronautId
    );

    // Busca a última medição cadastrada no sistema pelo maior ID
    Optional<TelemetryRecord> findTopByOrderByIdDesc();

    // Busca todas as medições do astronauta por data decrescente
    List<TelemetryRecord> findByAstronautIdOrderByTimestampDesc(
            String astronautId
    );
}
