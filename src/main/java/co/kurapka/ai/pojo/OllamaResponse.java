package co.kurapka.ai.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;

public record OllamaResponse(String model,
                             String response,
                             OllamaMessage message,
                             @JsonProperty("total_duration") long totalDurationInModel,
                             @JsonProperty("eval_duration") long evalDurationInModel,
                             @JsonProperty("eval_count") int tokensResponse,
                             @JsonProperty("prompt_eval_count") int tokensInRequest) {
}
