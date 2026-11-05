package co.kurapka.ai.scenario;

import co.kurapka.ai.pojo.OllamaEmbedResponse;

import java.util.List;

public record EmbedResult(OllamaEmbedResponse response, List<EmbedGame> games) {}
