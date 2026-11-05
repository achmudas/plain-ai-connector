package co.kurapka.ai.scenario;

import co.kurapka.ai.client.OllamaClient;
import co.kurapka.ai.client.OllamaHttpClient;
import co.kurapka.ai.exception.OllamaException;
import co.kurapka.ai.pojo.OllamaChatRequest;
import co.kurapka.ai.pojo.OllamaMessage;
import co.kurapka.ai.pojo.OllamaResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatScenario implements Scenario<OllamaResponse> {

    private final OllamaClient<OllamaChatRequest, OllamaResponse> client;
    private final String scenarioFile;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatScenario(String baseUrl, String scenarioFile) {
        this.client = new OllamaHttpClient<>(baseUrl, "/api/chat", OllamaResponse.class);
        this.scenarioFile = scenarioFile;
    }

    @Override
    public OllamaResponse run() {
        try {
            ChatScenarioConfig config = objectMapper.readValue(new File(scenarioFile), ChatScenarioConfig.class);
            List<OllamaMessage> messages = new ArrayList<>();
            if (config.system() != null) {
                messages.add(new OllamaMessage("system", config.system()));
            }
            messages.addAll(config.messages());
            OllamaChatRequest request = new OllamaChatRequest(config.model(), messages, false, config.options(), config.think());
            return client.call(request);
        } catch (JacksonException e) {
            throw new OllamaException("Failed to read scenario file: " + scenarioFile, e);
        }
    }
}
