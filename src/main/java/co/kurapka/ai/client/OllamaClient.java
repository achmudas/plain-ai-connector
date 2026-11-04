package co.kurapka.ai.client;

import co.kurapka.ai.pojo.OllamaResponse;

public interface OllamaClient<T> {
    OllamaResponse call(T request);
}
