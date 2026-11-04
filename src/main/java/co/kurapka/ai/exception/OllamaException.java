package co.kurapka.ai.exception;

public class OllamaException extends RuntimeException {
    public OllamaException(String message) {
        super(message);
    }

    public OllamaException(String message, Throwable cause) {
        super(message, cause);
    }
}
