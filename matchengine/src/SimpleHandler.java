import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class SimpleHandler implements HttpHandler {
    public static HashMap<String, String> parseQuery(URI uri) {
        HashMap<String, String> queryParams = new HashMap<String, String>();

        String query = uri.getRawQuery();
        if (query == null) {
            return queryParams;
        }

        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length < 2) {
                kv = new String[] { kv[0], "" };
            }

            queryParams.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
        }

        return queryParams;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        HashMap<String, String> queryParams = parseQuery(t.getRequestURI());

        Response response = null;

        try {
            response = this.doit(queryParams, ContextSingleton.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            response = new Response("Internal Server Error", 500);
        }

        t.sendResponseHeaders(
                response.getCode(),
                response.getBody().length());

        OutputStream os = t.getResponseBody();
        os.write(response.getBody().getBytes());
        os.close();
    }

    public abstract Response doit(HashMap<String, String> queryParams, Context locals);
}