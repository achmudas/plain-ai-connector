package co.kurapka.ai.scenario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import co.kurapka.ai.pojo.OllamaRequestOptions;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GenerateScenarioConfig(String model,
                                     String prompt,
                                     OllamaRequestOptions options,
                                     String system,
                                     boolean think) {
}
