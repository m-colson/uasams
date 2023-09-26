public class Response {
    private int code;
    private String body;

    public Response(String body) {
        this.code = 200;
        this.body = body;
    }

    public Response(Object body) {
        this.code = 200;
        this.body = body.toString();
    }

    public Response(String body, int code) {
        this.code = code;
        this.body = body;
    }

    public Response(Object body, int code) {
        this.code = code;
        this.body = body.toString();
    }

    public int getCode() {
        return this.code;
    }

    public String getBody() {
        return this.body;
    }
}
