package co.kurapka.ai.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OllamaEmbedResponse(
        String model,
        List<List<Double>> embeddings,
        @JsonProperty("total_duration") long totalDuration,
        @JsonProperty("load_duration") long loadDuration,
        @JsonProperty("prompt_eval_count") int promptEvalCount) {
}
