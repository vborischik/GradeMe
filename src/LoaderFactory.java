public class LoaderFactory {

    public static DataLoader<Question> create(String mode, String apiKey) {

        if ("ai".equalsIgnoreCase(mode)) {
            return new AiLoader(apiKey);
        }

        return new JsonFileLoader();
    }
}