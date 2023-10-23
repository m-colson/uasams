import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class Db {
    private static class Context {
        private final HttpClient client = HttpClient.newHttpClient();

        private Context() {
        }

        private static Context instance = null;

        public static HttpClient getClient() {
            if (instance == null) {
                instance = new Context();
            }
            return instance.client;
        }
    }

    public class Collection extends ArrayList<Cpsn> {

    }

    public class CollectionBuilder {
        public static class BuildException extends Exception {
            public BuildException(Exception inner) {
                super(inner);
            }
        }

        private java.util.List<String> filters = new ArrayList<String>();
        private java.util.List<String> sorts = new ArrayList<String>();
        private int perPage = 1000;
        private int page = 1;
        private String name = null;

        public CollectionBuilder filter(String filter) {
            filters.add(filter);
            return this;
        }

        public CollectionBuilder sort(String sort) {
            sorts.add(sort);
            return this;
        }

        public CollectionBuilder perPage(int perPage) {
            this.perPage = perPage;
            return this;
        }

        public CollectionBuilder page(int page) {
            this.page = page;
            return this;
        }

        public CollectionBuilder(String name) {
            this.name = name;
        }

        private String getQuery() {
            java.util.Map<String, String> queryParams = new HashMap<String, String>();
            if (filters.size() > 0) {
                queryParams.put("filter", String.join("%26", filters));
            }

            if (sorts.size() > 0) {
                queryParams.put("sort", String.join("%26", sorts));
            }

            queryParams.put("perPage", Integer.toString(perPage));

            queryParams.put("page", Integer.toString(page));

            String query = "?";
            boolean first = true;
            for (String key : queryParams.keySet()) {
                if (!first) {
                    query += "&";
                }
                first = false;
                query += key + "=" + queryParams.get(key);
            }

            return query;
        }

        public Collection build() throws BuildException {
            try {
                String body = Context.getClient().send(HttpRequest.newBuilder()
                        .uri(baseUrl.resolve(
                                "/api/collections/" +
                                        this.name +
                                        "/records" +
                                        this.getQuery()))
                        .GET()
                        .build(), HttpResponse.BodyHandlers.ofString())
                        .body();

                Cpsn response = Cpsn.fromJSON(body);

                Cpsn.List items = response.getOrThrow("items");

                Collection collection = new Collection();
                for (Cpsn item : items.value) {
                    collection.add(item);
                }

                return collection;
            } catch (IOException e) {
                throw new BuildException(e);
            } catch (InterruptedException e) {
                throw new BuildException(e);
            } catch (Cpsn.GetFailedException e) {
                throw new BuildException(e);
            }
        }
    }

    private final URI baseUrl;
    public String authorization;

    public Db(URI baseUrl, String authorization) {
        this.baseUrl = baseUrl;
        this.authorization = authorization;
    }
}