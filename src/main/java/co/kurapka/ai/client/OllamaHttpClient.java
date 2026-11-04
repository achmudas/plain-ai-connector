package co.kurapka.ai.client;

import co.kurapka.ai.exception.OllamaException;
import co.kurapka.ai.pojo.OllamaResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OllamaHttpClient<T> implements OllamaClient<T> {

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String url;

    public OllamaHttpClient(String baseUrl, String path) {
        this.url = baseUrl + path;
    }

    @Override
    public OllamaResponse call(T request) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request)))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new OllamaException("Ollama returned " + response.statusCode() + ": " + response.body());
            }
            return objectMapper.readValue(response.body(), OllamaResponse.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Interrupted while calling Ollama API", e);
        } catch (IOException | URISyntaxException e) {
            throw new OllamaException("Error calling Ollama API", e);
        }
    }
}
