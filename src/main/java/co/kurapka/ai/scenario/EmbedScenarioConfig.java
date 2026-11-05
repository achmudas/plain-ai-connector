package co.kurapka.ai.scenario;

import co.kurapka.ai.pojo.OllamaMessage;
import co.kurapka.ai.pojo.OllamaRequestOptions;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmbedScenarioConfig(String model,
                                  OllamaRequestOptions options,
                                  List<EmbedGame> games) {
}
