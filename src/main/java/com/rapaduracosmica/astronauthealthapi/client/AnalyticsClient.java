package com.rapaduracosmica.astronauthealthapi.client;


import com.rapaduracosmica.astronauthealthapi.dto.TelemetryAnalysisDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AnalyticsClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_ANALYZE_URL = "http://localhost:5000/api/v1/analyze";

    public TelemetryAnalysisDTO analyzeTelemetry(TelemetryAnalysisDTO payload) {
        try {
            return restTemplate.postForObject(PYTHON_ANALYZE_URL, payload, TelemetryAnalysisDTO.class);
        } catch (Exception e) {

            payload.setRiskLevel("UNKNOWN");
            payload.setAlertMessage("Serviço de análise Python indisponível.");
            return payload;
        }
    }
}