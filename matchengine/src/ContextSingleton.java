public class ContextSingleton {
    private static Context instance = null;

    public static Context getInstance() {
        if (instance == null) {
            instance = Context.create("http://localhost:7001");
        }
        return instance;
    }
}
