import java.time.OffsetDateTime;
import java.util.HashMap;

import org.json.JSONObject;

public class ScholarshipModel {
    private String name;
    private String description;
    private OffsetDateTime deadline;
    private DBRelation owner;
    private JSONObject match_criteria;

    private ScholarshipModel(
            String name,
            String description,
            OffsetDateTime deadline,
            DBRelation owner,
            JSONObject match_criteria) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.owner = owner;
        this.match_criteria = match_criteria;
    }

    public static ScholarshipModel fromJSON(JSONObject obj) {
        if(!(obj.get("match_criteria") instanceof HashMap)) throw new RuntimeException("match_criteria is not a HashMap");

        HashMap<String, JSONObject> criteriaObj = (HashMap<String, JSONObject>) obj.get("match_criteria");

        JSONObject criteria = new JSONObject();

        for (String key : criteriaObj.keySet()) {
            criteria.put(key, criteriaObj.get(key));
        }

        return new ScholarshipModel(
                obj.getString("name"),
                obj.getString("description"),
                OffsetDateTime.parse(String.join("T", obj.getString("deadline").split(" "))),
                new DBRelation(obj.getString("owner")),
                criteria);
    }

    public static ScholarshipModel from(HashMap<String, JSONObject> obj) {
        JSONObject out = new JSONObject();

        for (String key : obj.keySet()) {
            out.put(key, obj.get(key));
        }

        return fromJSON(out);
    }

    public JSONObject toJSON() {
        JSONObject out = new JSONObject();
        out.put("name", name);
        out.put("description", description);
        out.put("deadline", deadline.toString());
        out.put("owner", owner.inner);
        out.put("match_criteria", match_criteria);
        return out;
    }
}
