package co.kurapka.ai.scenario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import co.kurapka.ai.pojo.OllamaMessage;
import co.kurapka.ai.pojo.OllamaRequestOptions;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatScenarioConfig(String model,
                                 OllamaRequestOptions options,
                                 String system,
                                 boolean think,
                                 List<OllamaMessage> messages) {
}
