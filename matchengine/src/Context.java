import java.time.Duration;

public class Context {
    public Database db;
    public ScholarshipModelList scholarships;

    private Context(Database db) {
        this.db = db;
        this.scholarships = new ScholarshipModelList(Duration.ofMinutes(1));
    }

    public static Context create(String baseUrl) {
        return new Context(new Database(baseUrl));
    }
}
