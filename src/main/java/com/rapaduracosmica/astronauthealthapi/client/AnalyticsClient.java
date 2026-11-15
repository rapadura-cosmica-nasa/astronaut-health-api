package com.rapaduracosmica.astronauthealthapi.client;

import com.rapaduracosmica.astronauthealthapi.dto.TelemetryAnalysisDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AnalyticsClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${analytics.service.url}")
    private String pythonAnalyzeUrl;

    public TelemetryAnalysisDTO analyzeTelemetry(TelemetryAnalysisDTO payload) {
        try {
            return restTemplate.postForObject(pythonAnalyzeUrl, payload, TelemetryAnalysisDTO.class);
        } catch (Exception e) {
            payload.setRiskLevel("UNKNOWN");
            payload.setAlertMessage("Serviço de análise Python indisponível.");
            payload.setRecommendedAction("Verificar conexão com o motor de análise clínica.");
            return payload;
        }
    }
}