package co.kurapka.ai.scenario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmbedGame(String game, String description) {
}
