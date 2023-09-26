import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import org.json.JSONObject;

import java.util.HashMap;

public class ScholarshipModelList {
    private List<ScholarshipModel> cache;
    private Duration cacheFor;

    private OffsetDateTime lastUpdated = null;

    public ScholarshipModelList(Duration cacheFor) {
        this.cacheFor = cacheFor;
    }

    public void forceUpdate(Database db) throws IOException {

        JSONObject data = db.get("/api/collections/scholarships/records?page=1&pageSize=1000&filter=(deadline%20>%20@now)&sort=deadline");

        cache = data
                .getJSONArray("items")
                .toList()
                .stream()
                .map(v -> {
                    return ScholarshipModel.from((HashMap<String, JSONObject>) v);
                })
                .toList();

        lastUpdated = OffsetDateTime.now();
    }

    public List<ScholarshipModel> getAll(Database db) {
        if (lastUpdated == null ||
                OffsetDateTime.now().isAfter(lastUpdated.plus(cacheFor))) {
            try {
                forceUpdate(db);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return cache;
    }
}
