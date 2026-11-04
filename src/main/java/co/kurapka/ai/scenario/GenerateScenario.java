package co.kurapka.ai.scenario;

import co.kurapka.ai.client.OllamaClient;
import co.kurapka.ai.client.OllamaHttpClient;
import co.kurapka.ai.exception.OllamaException;
import co.kurapka.ai.pojo.OllamaGenerateRequest;
import co.kurapka.ai.pojo.OllamaResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

public class GenerateScenario implements Scenario {

    private final OllamaClient<OllamaGenerateRequest> client;
    private final String scenarioFile;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GenerateScenario(String baseUrl, String scenarioFile) {
        this.client = new OllamaHttpClient<>(baseUrl, "/api/generate");
        this.scenarioFile = scenarioFile;
    }

    @Override
    public OllamaResponse run() {
        try {
            GenerateScenarioConfig config = objectMapper.readValue(new File(scenarioFile), GenerateScenarioConfig.class);
            OllamaGenerateRequest request = new OllamaGenerateRequest(
                    config.model(), config.prompt(), false, config.options(), config.system(), config.think());
            return client.call(request);
        } catch (JacksonException e) {
            throw new OllamaException("Failed to read scenario file: " + scenarioFile, e);
        }
    }
}
