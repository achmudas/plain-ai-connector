package co.kurapka.ai.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OllamaEmbedRequest(String model,
                                 List<String> input,
                                 OllamaRequestOptions options) {
}
