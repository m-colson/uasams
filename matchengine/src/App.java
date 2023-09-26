import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;

public class App {

    public static JSONObject match(String userid, Integer top, Context locals) {
        List<ScholarshipModel> scholarshipModels = locals.scholarships.getAll(locals.db);

        List<JSONObject> items = new ArrayList<>(top);

        for (int i = 0; i < top && i < scholarshipModels.size(); i++) {
            items.add(scholarshipModels.get(i).toJSON());
        }

        JSONObject out = new JSONObject();
        out.put("top", items.size());
        out.put("items", items);
        return out;
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(7002), 0);

        server.createContext("/api/v1/match", new SimpleHandler() {
            // ?userid=...
            // &top=50
            @Override
            public Response doit(HashMap<String, String> queryParams, Context locals) {
                String userid = queryParams.get("userid");
                if (userid == null)
                    return new Response("missing userid", 400);

                String top = queryParams.get("top");
                if (top == null)
                    top = "50";

                Integer topInt = null;
                try {
                    topInt = Integer.parseInt(top);
                } catch (NumberFormatException e) {
                    return new Response("top was not a number", 400);
                }

                return new Response(match(userid, topInt, locals));
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Started server");
    }
}
