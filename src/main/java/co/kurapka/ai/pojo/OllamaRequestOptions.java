package co.kurapka.ai.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OllamaRequestOptions(double temperature, @JsonProperty("num_predict") int maxTokens) {
}
