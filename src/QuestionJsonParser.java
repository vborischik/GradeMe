import org.json.*;
import java.util.*;

public class QuestionJsonParser {

    public List<Question> parse(String json) {
        List<Question> result = new ArrayList<>();

        json = clean(json);

        JSONArray array = new JSONArray(json);

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            String q = obj.getString("question");
            JSONArray optsJson = obj.getJSONArray("options");

            if (optsJson.length() != 4) continue;

            String[] opts = new String[4];
            for (int j = 0; j < 4; j++) {
                opts[j] = optsJson.getString(j).trim();
                if (opts[j].isEmpty()) continue;
            }

            String ans = obj.getString("answer").trim();

            boolean valid = false;
            for (String o : opts) {
                if (o.equals(ans)) valid = true;
            }

            if (!valid) continue;

            result.add(new Question(q, opts, ans));
        }

        if (result.isEmpty())
            throw new RuntimeException("No valid questions found");

        return result;
    }

    private String clean(String s) {
        return s.replace("```json", "")
                .replace("```", "")
                .trim();
    }
}