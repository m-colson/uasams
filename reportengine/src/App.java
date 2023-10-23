import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static URI baseUrl = null;

    public static class Request {
        public String message;
        public Map<String, String> args;
        public String userId = null;
        public String jwt = null;

        public static Request from(Cpsn c) throws Cpsn.GetFailedException {
            Cpsn.Value message = c.getOrThrow("message");

            Cpsn.Map args = c.getOrThrow("args");

            Map<String, String> argStrings = new HashMap<String, String>();
            for (String key : args.value.keySet()) {
                Cpsn.Value arg = args.getOrThrow(key);

                argStrings.put(key, arg.value);
            }

            Cpsn.Value authorization = c.getOrThrow("authorization");
            String[] parts = authorization.value.split(":");

            Request r = new Request();
            if (parts.length == 2) {
                r.userId = parts[0];
                r.jwt = parts[1];
            }

            r.message = message.value;
            r.args = argStrings;
            return r;
        }

        public String toString() {
            return "Request(message=" + message + ", args=" + args + ")";
        }
    }

    public static Cpsn generateApplicantReport(String scholarshipId, String jwt) throws Exception {
        Db.Collection collection = new Db(baseUrl, jwt).new CollectionBuilder("applications")
                .filter("scholarship.id%3d\"" + scholarshipId + "\"").build();

        Cpsn.List report = new Cpsn.List();
        for (Cpsn c : collection) {
            report.add(c);
        }
        return report;
    }

    public static Cpsn process(Cpsn inp) throws Exception {
        Request request = Request.from(inp);

        if (request.message.equals("ping")) {
            return new Cpsn.Value("pong");
        } else if (request.message.equals("applicantReport")) {
            String scholarshipId = request.args.get("for");
            return generateApplicantReport(scholarshipId, request.jwt);
        } else {
            throw new RuntimeException("unknown message: " + request.message);
        }
    }

    public static void main(String[] args) {
        try {
            baseUrl = new URI("http://localhost:7001");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Cpsn o : Cpsn.Source.fromStdin()) {
            Cpsn.Value status = new Cpsn.Value("failed");
            Cpsn result = null;

            try {
                result = process(o);
                status = new Cpsn.Value("success");
            } catch (Exception e) {
                result = new Cpsn.Value(e.getClass().getSimpleName() + ": " + e.getMessage());
            } finally {
                Cpsn out = new Cpsn.Map();
                out.put("status", status);
                out.put("result", result);
                System.out.println(out);
            }
        }
    }
}
