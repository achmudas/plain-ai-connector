package co.kurapka.ai;

import co.kurapka.ai.pojo.OllamaEmbedResponse;
import co.kurapka.ai.pojo.OllamaResponse;
import co.kurapka.ai.scenario.ChatScenario;
import co.kurapka.ai.scenario.EmbeddingScenario;
import co.kurapka.ai.scenario.GenerateScenario;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    private static final String BASE_URL = "http://localhost:11434";
    private static final String SCENARIOS_DIR = "scenarios";

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        String endpoint = args[0];
        String scenarioName = args[1];

        if (!List.of("chat", "prompt", "embed").contains(endpoint)) {
            System.err.println("Unknown endpoint: " + endpoint);
            System.err.println();
            printUsage();
            System.exit(1);
        }

        String scenarioFile = "scenarios/" + endpoint + "/" + scenarioName + ".json";

        if (!new File(scenarioFile).exists()) {
            System.err.println("Scenario not found: " + scenarioName);
            System.err.println("Available " + endpoint + " scenarios: " + String.join(", ", scenarios(endpoint)));
            System.exit(1);
        }

        long startTime = System.nanoTime();
        switch (endpoint) {
            case "chat" -> printTextResponse(new ChatScenario(BASE_URL, scenarioFile).run(), startTime);
            case "prompt" -> printTextResponse(new GenerateScenario(BASE_URL, scenarioFile).run(), startTime);
            case "embed" -> printEmbedResponse(new EmbeddingScenario(BASE_URL, scenarioFile).run(), startTime);
        }
    }

    private static void printTextResponse(OllamaResponse res, long startTime) {
        long totalMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        long llmMs = TimeUnit.NANOSECONDS.toMillis(res.totalDurationInModel());
        double tokensPerSec = res.tokensResponse() / (res.evalDurationInModel() / 1_000_000_000.0);
        String content = res.message() != null ? res.message().content() : res.response();

        System.out.println("Model:    " + res.model());
        System.out.println("Response: " + content);
        System.out.println();
        System.out.printf("Tokens:   in=%-6d out=%-6d speed=%.1f t/s%n",
                res.tokensInRequest(), res.tokensResponse(), tokensPerSec);
        System.out.printf("Duration: llm=%dms  total=%dms%n", llmMs, totalMs);
    }

    private static void printEmbedResponse(OllamaEmbedResponse res, long startTime) {
        long totalMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        long loadMs = TimeUnit.NANOSECONDS.toMillis(res.loadDuration());
        int count = res.embeddings() != null ? res.embeddings().size() : 0;
        int dims = (count > 0) ? res.embeddings().get(0).size() : 0;

        System.out.println("Model:      " + res.model());
        System.out.printf("Embeddings: %d vectors × %d dimensions%n", count, dims);
        System.out.println();
        System.out.printf("Tokens:     in=%-6d%n", res.promptEvalCount());
        System.out.printf("Duration:   load=%dms  total=%dms%n", loadMs, totalMs);
    }

    private static void printUsage() {
        System.err.println("Usage: pac <endpoint> <scenario>");
        System.err.println();
        System.err.println("  Endpoints and available scenarios:");
        for (String endpoint : List.of("chat", "prompt", "embed")) {
            List<String> scenarios = scenarios(endpoint);
            String list = scenarios.isEmpty() ? "(none)" : String.join(", ", scenarios);
            System.err.printf("    %-8s → scenarios/%s/   (%s)%n", endpoint, endpoint, list);
        }
        System.err.println();
        System.err.println("  Example: pac chat basketball");
    }

    private static List<String> scenarios(String endpoint) {
        File dir = new File(SCENARIOS_DIR + "/" + endpoint);
        if (!dir.exists() || !dir.isDirectory()) return List.of();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) return List.of();
        return Arrays.stream(files)
                .map(f -> f.getName().replace(".json", ""))
                .sorted()
                .collect(Collectors.toList());
    }
}
