import java.nio.file.*;
import java.util.*;

public class JsonFileLoader implements DataLoader<Question> {

    private final QuestionJsonParser parser = new QuestionJsonParser();

    @Override
    public List<Question> load(String filePath) throws Exception {
        String content = Files.readString(Path.of(filePath));
        return parser.parse(content);
    }
}