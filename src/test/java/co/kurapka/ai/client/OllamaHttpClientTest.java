package co.kurapka.ai.client;

import co.kurapka.ai.exception.OllamaException;
import co.kurapka.ai.pojo.OllamaChatRequest;
import co.kurapka.ai.pojo.OllamaGenerateRequest;
import co.kurapka.ai.pojo.OllamaMessage;
import co.kurapka.ai.pojo.OllamaRequestOptions;
import co.kurapka.ai.pojo.OllamaResponse;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

class OllamaHttpClientTest {

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private <T> OllamaHttpClient<T, OllamaResponse> client(String path) {
        return new OllamaHttpClient<>("http://localhost:" + wm.getPort(), path, OllamaResponse.class);
    }

    // --- /api/chat ---

    @Test
    void chat_returnsDeserializedResponse() {
        wm.stubFor(post("/api/chat").willReturn(okJson("""
                {
                  "model": "llama3.2",
                  "message": {"role": "assistant", "content": "Stephen Curry"},
                  "total_duration": 1000000000,
                  "eval_duration": 500000000,
                  "eval_count": 10,
                  "prompt_eval_count": 5
                }
                """)));

        OllamaResponse response = client("/api/chat").call(new OllamaChatRequest(
                "llama3.2",
                List.of(new OllamaMessage("user", "Who is best?")),
                false,
                new OllamaRequestOptions(0.7, 100),
                false));

        assertEquals("llama3.2", response.model());
        assertEquals("Stephen Curry", response.message().content());
        assertEquals(10, response.tokensResponse());
        assertEquals(5, response.tokensInRequest());
    }

    @Test
    void chat_sendsSystemMessageFirst() {
        wm.stubFor(post("/api/chat").willReturn(okJson("""
                {
                  "model": "llama3.2",
                  "message": {"role": "assistant", "content": "ok"},
                  "total_duration": 0, "eval_duration": 1, "eval_count": 1, "prompt_eval_count": 1
                }
                """)));

        client("/api/chat").call(new OllamaChatRequest(
                "llama3.2",
                List.of(
                        new OllamaMessage("system", "Be concise"),
                        new OllamaMessage("user", "Hello")),
                false,
                new OllamaRequestOptions(0.7, 100),
                false));

        wm.verify(postRequestedFor(urlEqualTo("/api/chat"))
                .withRequestBody(matchingJsonPath("$.messages[0].role", equalTo("system")))
                .withRequestBody(matchingJsonPath("$.messages[0].content", equalTo("Be concise")))
                .withRequestBody(matchingJsonPath("$.messages[1].role", equalTo("user"))));
    }

    @Test
    void chat_sendsModelAndOptions() {
        wm.stubFor(post("/api/chat").willReturn(okJson("""
                {
                  "model": "llama3.2",
                  "message": {"role": "assistant", "content": "ok"},
                  "total_duration": 0, "eval_duration": 1, "eval_count": 1, "prompt_eval_count": 1
                }
                """)));

        client("/api/chat").call(new OllamaChatRequest(
                "llama3.2",
                List.of(new OllamaMessage("user", "hi")),
                false,
                new OllamaRequestOptions(0.5, 200),
                false));

        wm.verify(postRequestedFor(urlEqualTo("/api/chat"))
                .withRequestBody(matchingJsonPath("$.model", equalTo("llama3.2")))
                .withRequestBody(matchingJsonPath("$.options.temperature", equalTo("0.5")))
                .withRequestBody(matchingJsonPath("$.options.num_predict", equalTo("200"))));
    }

    @Test
    void chat_throwsOllamaExceptionOnNon200() {
        wm.stubFor(post("/api/chat").willReturn(serverError().withBody("Internal error")));

        assertThrows(OllamaException.class, () -> client("/api/chat").call(new OllamaChatRequest(
                "llama3.2",
                List.of(new OllamaMessage("user", "hi")),
                false,
                new OllamaRequestOptions(0.7, 100),
                false)));
    }

    // --- /api/generate ---

    @Test
    void generate_returnsDeserializedResponse() {
        wm.stubFor(post("/api/generate").willReturn(okJson("""
                {
                  "model": "gemma4:e2b",
                  "response": "The sky is HUGE!",
                  "total_duration": 2000000000,
                  "eval_duration": 1000000000,
                  "eval_count": 15,
                  "prompt_eval_count": 8
                }
                """)));

        OllamaResponse response = client("/api/generate").call(new OllamaGenerateRequest(
                "gemma4:e2b",
                "How big is the sky?",
                false,
                new OllamaRequestOptions(0.7, 200),
                "Answer like a 3 year old",
                false));

        assertEquals("gemma4:e2b", response.model());
        assertEquals("The sky is HUGE!", response.response());
        assertEquals(15, response.tokensResponse());
        assertEquals(8, response.tokensInRequest());
    }

    @Test
    void generate_sendsPromptAndSystem() {
        wm.stubFor(post("/api/generate").willReturn(okJson("""
                {
                  "model": "gemma4:e2b",
                  "response": "ok",
                  "total_duration": 0, "eval_duration": 1, "eval_count": 1, "prompt_eval_count": 1
                }
                """)));

        client("/api/generate").call(new OllamaGenerateRequest(
                "gemma4:e2b",
                "How big is the sky?",
                false,
                new OllamaRequestOptions(0.7, 200),
                "Answer like a 3 year old",
                false));

        wm.verify(postRequestedFor(urlEqualTo("/api/generate"))
                .withRequestBody(matchingJsonPath("$.prompt", equalTo("How big is the sky?")))
                .withRequestBody(matchingJsonPath("$.system", equalTo("Answer like a 3 year old")))
                .withRequestBody(matchingJsonPath("$.model", equalTo("gemma4:e2b"))));
    }

    @Test
    void generate_doesNotSendNullSystemField() {
        wm.stubFor(post("/api/generate").willReturn(okJson("""
                {
                  "model": "gemma4:e2b",
                  "response": "ok",
                  "total_duration": 0, "eval_duration": 1, "eval_count": 1, "prompt_eval_count": 1
                }
                """)));

        client("/api/generate").call(new OllamaGenerateRequest(
                "gemma4:e2b", "Hello", false, new OllamaRequestOptions(0.7, 100), null, false));

        wm.verify(postRequestedFor(urlEqualTo("/api/generate"))
                .withRequestBody(notMatching(".*\"system\".*")));
    }

    @Test
    void generate_throwsOllamaExceptionOnNon200() {
        wm.stubFor(post("/api/generate").willReturn(serverError().withBody("Internal error")));

        assertThrows(OllamaException.class, () -> client("/api/generate").call(new OllamaGenerateRequest(
                "gemma4:e2b", "Hello", false, new OllamaRequestOptions(0.7, 100), null, false)));
    }
}
