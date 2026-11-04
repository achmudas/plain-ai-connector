package co.kurapka.ai.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OllamaGenerateRequest(String model,
                                    String prompt,
                                    boolean stream,
                                    OllamaRequestOptions options,
                                    String system,
                                    boolean think) {
}
