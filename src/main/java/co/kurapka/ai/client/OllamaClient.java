package co.kurapka.ai.client;

public interface OllamaClient<T, R> {
    R call(T request);
}
