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

public class EmbeddingScenario implements Scenario<EmbedResult> {

    private final OllamaClient<OllamaEmbedRequest, OllamaEmbedResponse> client;
    private final String scenarioFile;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String model;

    public EmbeddingScenario(String baseUrl, String scenarioFile) {
        this.client = new OllamaHttpClient<>(baseUrl, "/api/embed", OllamaEmbedResponse.class);
        this.scenarioFile = scenarioFile;
    }

    @Override
    public EmbedResult run() {
        try {
            EmbedScenarioConfig config = objectMapper.readValue(new File(scenarioFile), EmbedScenarioConfig.class);
            model = config.model();
            List<String> input = config.games().stream().map(EmbedGame::description).toList();
            OllamaEmbedResponse response = client.call(new OllamaEmbedRequest(model, input, config.options()));
            return new EmbedResult(response, config.games());
        } catch (JacksonException e) {
            throw new OllamaException("Failed to read scenario file: " + scenarioFile, e);
        }
    }

    public List<Double> queryEmbed(String query) {
        OllamaEmbedResponse response = client.call(new OllamaEmbedRequest(model, List.of(query), null));
        return response.embeddings().get(0);
    }
}
