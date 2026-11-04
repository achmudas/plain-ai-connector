# plain-ai-connector

A minimal Java CLI for running prompt scenarios against a local [Ollama](https://ollama.com) instance.

## Requirements

- Java 21+
- Ollama running on `http://localhost:11434`

## Build & run

```bash
chmod +x pac
./pac chat basketball
```

The `pac` script builds the project automatically on first run.

## Usage

```
./pac <endpoint> <scenario>
```

| Endpoint | Ollama API      | Description                        |
|----------|-----------------|------------------------------------|
| `chat`   | `/api/chat`     | Multi-turn conversation with roles |
| `prompt` | `/api/generate` | Single-turn raw prompt completion  |

Running `./pac` with no arguments prints all available endpoints and scenarios.

## Scenarios

Scenarios are JSON files under `scenarios/<endpoint>/`. Add a new file to create a new scenario — no code changes needed.

The included examples are based on the [Anthropic prompt engineering interactive tutorial](https://github.com/anthropics/prompt-eng-interactive-tutorial).

**Chat scenario** (`scenarios/chat/my-scenario.json`):
```json
{
  "model": "llama3.2:latest",
  "options": { "temperature": 0.7, "num_predict": 200 },
  "system": "Optional system prompt",
  "messages": [
    { "role": "user", "content": "Your question here" }
  ]
}
```

**Prompt scenario** (`scenarios/prompt/my-scenario.json`):
```json
{
  "model": "gemma4:e2b",
  "options": { "temperature": 0.7, "num_predict": 200 },
  "system": "Optional system prompt",
  "think": false,
  "prompt": "Your prompt here"
}
```

The `system` field is optional in both. For chat, it is prepended as the first message with role `system`.

## Running tests

```bash
./gradlew test
```
