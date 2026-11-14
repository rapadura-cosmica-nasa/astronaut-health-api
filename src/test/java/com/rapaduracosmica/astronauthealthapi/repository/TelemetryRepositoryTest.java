package com.rapaduracosmica.astronauthealthapi.repository;

import com.rapaduracosmica.astronauthealthapi.model.TelemetryRecord;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TelemetryRepositoryTest {

    private TelemetryRecord record;

    @Autowired
    private TelemetryRepository telemetryRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        record = new TelemetryRecord();
        record.setAstronautId("ASTRO-01");
        record.setTimestamp(LocalDateTime.now());
        record.setHeartRate(115);
        record.setSpo2(98);
        record.setRadiationDoseMsv(0.3);
        record.setSleepHours(8.0);
        record.setRiskLevel("WARNING");
        record.setAlertMessage("Frequência cardíaca elevada.");
    }

    @Test
    void deveSalvarEBuscarTelemetryRecordPorId() {

        // Act (ação)
        TelemetryRecord savedRecord =
                telemetryRepository.save(record);

        Optional<TelemetryRecord> found =
                telemetryRepository.findById(savedRecord.getId());

        // Assert (verificação)
        assertThat(savedRecord).isNotNull();
        assertThat(savedRecord.getId()).isNotNull();

        assertThat(found).isPresent();

        assertThat(found.get().getAstronautId())
                .isEqualTo("ASTRO-01");

        assertThat(found.get().getHeartRate())
                .isEqualTo(115);

        assertThat(found.get().getRiskLevel())
                .isEqualTo("WARNING");

        assertThat(found.get().getAlertMessage())
                .isEqualTo("Frequência cardíaca elevada.");
    }


    @Test
    void devePersistirERecuperarTelemetryRecordDoBancoH2() {

        // Act - Salvar no H2
        TelemetryRecord savedRecord =
                telemetryRepository.save(record);

        Long savedId = savedRecord.getId();

        // Forçar gravação no banco
        telemetryRepository.flush();

        // Limpar o contexto de persistência do JPA
        entityManager.clear();

        // Buscar novamente no H2
        Optional<TelemetryRecord> found =
                telemetryRepository.findById(savedId);

        // Assert - Verificar dados recuperados
        assertThat(found).isPresent();

        assertThat(found.get().getAstronautId())
                .isEqualTo("ASTRO-01");

        assertThat(found.get().getHeartRate())
                .isEqualTo(115);

        assertThat(found.get().getRiskLevel())
                .isEqualTo("WARNING");

        assertThat(found.get().getAlertMessage())
                .isEqualTo("Frequência cardíaca elevada.");
    }


    @Test
    void deveBuscarHistoricoDoAstronautaOrdenadoPorTimestamp() {

        // Arrange - Primeiro registro criado pelo @BeforeEach
        record.setTimestamp(
                LocalDateTime.of(2026, 11, 14, 10, 0)
        );
        record.setHeartRate(80);

        telemetryRepository.save(record);

        // Segunda medição, mais recente
        TelemetryRecord segunda = new TelemetryRecord();

        segunda.setAstronautId("ASTRO-01");
        segunda.setTimestamp(
                LocalDateTime.of(2026, 11, 14, 11, 0)
        );
        segunda.setHeartRate(115);
        segunda.setSpo2(96);
        segunda.setRadiationDoseMsv(0.4);
        segunda.setSleepHours(7.0);
        segunda.setRiskLevel("WARNING");
        segunda.setAlertMessage("Frequência cardíaca elevada.");

        telemetryRepository.save(segunda);

        // Outro astronauta, que não deve aparecer no resultado
        TelemetryRecord outro = new TelemetryRecord();

        outro.setAstronautId("ASTRO-02");
        outro.setTimestamp(
                LocalDateTime.of(2026, 11, 14, 12, 0)
        );
        outro.setHeartRate(90);
        outro.setSpo2(98);
        outro.setRadiationDoseMsv(0.2);
        outro.setSleepHours(8.0);
        outro.setRiskLevel("NORMAL");

        telemetryRepository.saveAndFlush(outro);

        // Limpa o contexto de persistência
        entityManager.clear();

        // Act - Buscar o histórico do ASTRO-01
        List<TelemetryRecord> historico =
                telemetryRepository
                        .findByAstronautIdOrderByTimestampDesc("ASTRO-01");

        // Assert - Deve retornar somente dois registros
        assertThat(historico).hasSize(2);

        // A medição das 11h deve vir primeiro
        assertThat(historico.get(0).getHeartRate())
                .isEqualTo(115);

        assertThat(historico.get(0).getTimestamp())
                .isEqualTo(LocalDateTime.of(2026, 11, 14, 11, 0));

        // A medição das 10h deve vir depois
        assertThat(historico.get(1).getHeartRate())
                .isEqualTo(80);

        assertThat(historico.get(1).getTimestamp())
                .isEqualTo(LocalDateTime.of(2026, 11, 14, 10, 0));

        // Nenhum registro de outro astronauta
        assertThat(historico)
                .allMatch(item -> item.getAstronautId().equals("ASTRO-01"));
    }


    @Test
    void deveRetornarListaVaziaQuandoAstronautaNaoPossuiMedicoes() {

        // Act
        List<TelemetryRecord> historico =
                telemetryRepository
                        .findByAstronautIdOrderByTimestampDesc("ASTRO-99");

        // Assert
        assertThat(historico).isEmpty();
    }


}