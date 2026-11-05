package co.kurapka.ai.scenario;

import co.kurapka.ai.client.OllamaClient;
import co.kurapka.ai.client.OllamaHttpClient;
import co.kurapka.ai.exception.OllamaException;
import co.kurapka.ai.pojo.OllamaEmbedRequest;
import co.kurapka.ai.pojo.OllamaEmbedResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class EmbeddingScenario implements Scenario<OllamaEmbedResponse> {

    private final OllamaClient<OllamaEmbedRequest, OllamaEmbedResponse> client;
    private final String scenarioFile;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmbeddingScenario(String baseUrl, String scenarioFile) {
        this.client = new OllamaHttpClient<>(baseUrl, "/api/embed", OllamaEmbedResponse.class);
        this.scenarioFile = scenarioFile;
    }

    @Override
    public OllamaEmbedResponse run() {
        try {
            EmbedScenarioConfig config = objectMapper.readValue(new File(scenarioFile), EmbedScenarioConfig.class);
            List<String> input = config.games().stream().map(EmbedGame::description).toList();
            OllamaEmbedRequest request = new OllamaEmbedRequest(config.model(), input, config.options());
            return client.call(request);
        } catch (JacksonException e) {
            throw new OllamaException("Failed to read scenario file: " + scenarioFile, e);
        }
    }
}
