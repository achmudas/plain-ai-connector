package co.kurapka.ai.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OllamaChatRequest(String model,
                                List<OllamaMessage> messages,
                                boolean stream,
                                OllamaRequestOptions options,
                                boolean think) {
}
