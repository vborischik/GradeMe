import java.net.URI;
import java.net.http.*;
import java.util.*;
import org.json.*;

public class AiLoader implements DataLoader<Question> {

    private final String apiKey;
    private final QuestionJsonParser parser = new QuestionJsonParser();

    public AiLoader(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<Question> load(String topic) throws Exception {
        String raw = callApi(topic);
        String content = extractContent(raw);
        return parser.parse(content);
    }

    private String callApi(String topic) throws Exception {

        String prompt = """
Generate 5 intermediate level multiple-choice questions about java.

Return ONLY JSON:
[
  {
    "question": "string",
    "options": ["A", "B", "C", "D"],
    "answer": "must match one option"
  }
]
""".formatted(topic);

        JSONObject body = new JSONObject()
                .put("model", "qwen/qwen3.5-122b-a10b")
                .put("messages", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("content", prompt)))
                .put("max_tokens", 16384)
                .put("temperature", 1.0)
                .put("top_p", 0.95)
                .put("stream", false)
                .put("chat_template_kwargs", new JSONObject()
                        .put("enable_thinking", false));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://integrate.api.nvidia.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response =
                HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: " + response.body());
        }

        return response.body();
    }

    private String extractContent(String json) {
        JSONObject obj = new JSONObject(json);
        return obj.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}