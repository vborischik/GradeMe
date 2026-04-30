import java.util.List;

public interface DataLoader<T> {
    List<T> load(String source) throws Exception;
}