import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

public class Database {
    private String basePath;

    public Database(String basePath) {
        this.basePath = basePath;
    }

    public JSONObject get(String path) throws IOException {
        URL finalPath = null;

        try {
            finalPath = new URL(basePath + path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        StringBuffer stringBuf = new StringBuffer();

        HttpURLConnection conn = (HttpURLConnection) finalPath.openConnection();
        conn.setRequestMethod("GET");

        // Taken from
        // https://stackoverflow.com/questions/1485708/how-do-i-do-a-http-get-in-java
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null;) {
                stringBuf.append(line);
            }
        }

        return new JSONObject(stringBuf.toString());
    }
}
