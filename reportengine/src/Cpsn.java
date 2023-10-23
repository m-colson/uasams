import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public abstract class Cpsn {
    public static Cpsn fromJSON(String json) {
        JSONTokener toker = new JSONTokener(json);
        Object obj = toker.nextValue();
        return from(obj);
    }

    public static Cpsn from(Object o) {
        if (o instanceof Boolean) {
            return from((Boolean) o);
        } else if (o instanceof Double) {
            return from((Double) o);
        } else if (o instanceof Integer) {
            return from((Integer) o);
        } else if (o instanceof Long) {
            return from((Long) o);
        } else if (o instanceof String) {
            return from((String) o);
        } else if (o instanceof JSONObject) {
            return from((JSONObject) o);
        } else if(o instanceof JSONArray) {
            return from((JSONArray) o);
        } else {
            throw new RuntimeException("Expected JSONObject got " + o.getClass().getSimpleName());
        }
    }

    public static Cpsn from(Boolean b) {
        if (b) {
            return new Cpsn.Value("true");
        } else {
            return new Cpsn.Value("");
        }
    }

    public static Cpsn from(Double d) {
        return new Cpsn.Value(d.toString());
    }

    public static Cpsn from(Integer i) {
        return new Cpsn.Value(i.toString());
    }

    public static Cpsn from(JSONArray arr) {
        Cpsn.List out = new Cpsn.List();
        for (int i = 0; i < arr.length(); i++) {
            out.add(from(arr.get(i)));
        }
        return out;
    }

    public static Cpsn from(JSONObject obj) {
        Cpsn.Map out = new Cpsn.Map();
        for (String key : obj.keySet()) {
            out.put(key, from(obj.get(key)));
        }
        return out;
    }

    public static Cpsn from(Long l) {
        return new Cpsn.Value(l.toString());
    }

    public static Cpsn from(String json) {
        return new Cpsn.Value(json);
    }

    public static class Source implements AutoCloseable, Iterable<Cpsn> {
        public String name;
        public InputStream file;

        public static Source fromStdin() {
            Source out = new Source();
            out.name = "<<stdin>>";
            out.file = System.in;

            return out;
        }

        public void close() throws IOException {
            file.close();
        }

        public Iterator<Cpsn> iterator() {
            return new Parser(this);
        }
    }

    private enum CharCategory {
        WHITESPACE,
        EOF,
        OPEN_PAREN,
        CLOSE_PAREN,
        OPEN_BRACE,
        CLOSE_BRACE,
        OPEN_BRACKET,
        CLOSE_BRACKET,
        HASH,
        SEMICOLON,
        COLON,
        BACKTICK,
        BACKSLASH,
        PERCENT,
        QUOTE,
        OTHER;

        public static CharCategory fromChar(byte b) {
            switch (b) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    return WHITESPACE;
                case '(':
                    return OPEN_PAREN;
                case ')':
                    return CLOSE_PAREN;
                case '{':
                    return OPEN_BRACE;
                case '}':
                    return CLOSE_BRACE;
                case '[':
                    return OPEN_BRACKET;
                case ']':
                    return CLOSE_BRACKET;
                case '#':
                    return HASH;
                case ';':
                    return SEMICOLON;
                case ':':
                    return COLON;
                case '`':
                    return BACKTICK;
                case '\\':
                    return BACKSLASH;
                case '%':
                    return PERCENT;
                case '"':
                    return QUOTE;
                default:
                    return OTHER;
            }
        }
    }

    public static class Token {
        public TokenKind kind;
        public String data;

        @Override
        public String toString() {
            return String.format("CpsnToken(%s, %s)", kind, data);
        }
    }

    public enum TokenKind {
        EOF,
        INVALID,
        KEY,
        VALUE,
        OPEN_PAREN,
        CLOSE_PAREN,
        OPEN_BRACE,
        CLOSE_BRACE,
        OPEN_BRACKET,
        CLOSE_BRACKET,
        HASH,
        SEMICOLON,
        COLON,
        TEMPLATE_JOIN,
        BACKSLASH;

        public Token with() {
            Token token = new Token();
            token.kind = this;
            token.data = null;
            return token;
        }

        public Token with(String data) {
            Token token = new Token();
            token.kind = this;
            token.data = data;
            return token;
        }
    }

    public static class Tokenizer implements Iterator<Token> {
        private static enum StateKind {
            DEFAULT,
            DONE,
            IN_KEY,
            IN_LIT_KEY,
            IN_STRING,
            IN_COMMENT;

            public State with() {
                State state = new State();
                state.kind = this;
                state.data = null;
                state.pattern = null;
                return state;
            }

            public State with(StringBuilder data) {
                State state = new State();
                state.kind = this;
                state.data = data;
                state.pattern = null;
                return state;
            }
        }

        private static class State {
            public StateKind kind;
            public StringBuilder data;
            public StringBuilder pattern;
        }

        private State state;
        private Source source;
        private Queue<Token> buffer;

        public Tokenizer(Source source) {
            this.source = source;
            this.state = StateKind.DEFAULT.with();
            this.buffer = new ArrayDeque<Token>();
        }

        public Parser parser() {
            return new Parser(this);
        }

        private void update(byte b) {
            switch (this.state.kind) {
                case DEFAULT:
                    switch (CharCategory.fromChar(b)) {
                        case EOF:
                            this.state.kind = StateKind.DONE;
                            this.buffer.add(TokenKind.EOF.with());
                            break;
                        case WHITESPACE:
                            break;
                        case OPEN_PAREN:
                            this.buffer.add(TokenKind.OPEN_PAREN.with());
                            break;
                        case CLOSE_PAREN:
                            this.buffer.add(TokenKind.CLOSE_PAREN.with());
                            break;
                        case OPEN_BRACE:
                            this.buffer.add(TokenKind.OPEN_BRACE.with());
                            break;
                        case CLOSE_BRACE:
                            this.buffer.add(TokenKind.CLOSE_BRACE.with());
                            break;
                        case OPEN_BRACKET:
                            this.buffer.add(TokenKind.OPEN_BRACKET.with());
                            break;
                        case CLOSE_BRACKET:
                            this.buffer.add(TokenKind.CLOSE_BRACKET.with());
                            break;
                        case HASH:
                            this.buffer.add(TokenKind.HASH.with());
                            break;
                        case SEMICOLON:
                            this.buffer.add(TokenKind.SEMICOLON.with());
                            break;
                        case BACKSLASH:
                            this.buffer.add(TokenKind.BACKSLASH.with());
                            break;
                        case QUOTE:
                            this.state = StateKind.IN_STRING.with(new StringBuilder(""));
                            break;
                        case BACKTICK:
                            this.state = StateKind.IN_LIT_KEY.with(new StringBuilder(""));
                            break;
                        case COLON:
                            this.buffer.add(TokenKind.COLON.with());
                        case PERCENT:
                            this.state = StateKind.IN_COMMENT.with(new StringBuilder(""));
                            break;
                        case OTHER:
                            this.state = StateKind.IN_KEY.with(new StringBuilder());
                            this.state.data.append((char) b);
                            break;
                        default:
                            this.buffer.add(TokenKind.INVALID.with());
                            break;
                    }
                    break;
                case IN_KEY:
                    switch (CharCategory.fromChar(b)) {
                        case OTHER:
                            this.state.data.append((char) b);
                            break;
                        default:
                            this.buffer.add(TokenKind.KEY.with(this.state.data.toString()));
                            this.state.kind = StateKind.DEFAULT;
                            this.update(b);
                            break;
                    }
                    break;
                case IN_LIT_KEY:
                    switch (CharCategory.fromChar(b)) {
                        case BACKTICK:
                            this.buffer.add(TokenKind.KEY.with(this.state.data.toString()));
                            this.state.kind = StateKind.DEFAULT;
                            break;
                        default:
                            this.state.data.append((char) b);
                            break;
                    }
                    break;
                case IN_STRING:
                    if (state.pattern == null) {
                        switch (CharCategory.fromChar(b)) {
                            case BACKSLASH:
                                this.state.pattern = new StringBuilder("");
                                break;
                            case QUOTE:
                                this.buffer.add(TokenKind.VALUE.with(this.state.data.toString()));
                                this.state.kind = StateKind.DEFAULT;
                                break;
                            default:
                                this.state.data.append((char) b);
                                break;
                        }
                    } else {
                        switch ((char) b) {
                            case '"':
                            case '\\':
                                this.state.data.append((char) b);
                                this.state.pattern = null;
                                break;
                        }
                    }
                case IN_COMMENT:
                    switch ((char) b) {
                        case '\n':
                        case '\0':
                            this.state = StateKind.DEFAULT.with();
                            break;
                        default:
                            break;
                    }
                case DONE:
                    break;
                default:
                    this.buffer.add(TokenKind.INVALID.with());
                    break;
            }
        }

        public void fill() throws IOException {
            while (this.buffer.isEmpty()) {
                byte b = (byte) this.source.file.read();
                this.update(b);
            }
        }

        public Token peek() {
            try {
                fill();
                return this.buffer.peek();
            } catch (Exception e) {
                throw new NoSuchElementException(e);
            }
        }

        public Token next() {
            try {
                fill();
                return this.buffer.remove();
            } catch (Exception e) {
                throw new NoSuchElementException(e);
            }
        }

        public boolean hasNext() {
            return !this.state.kind.equals(StateKind.DONE);
        }
    }

    public static class Parser implements Iterator<Cpsn> {
        private Tokenizer tokenizer;

        public Parser(Source source) {
            this.tokenizer = new Tokenizer(source);
        }

        public Parser(Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
        }

        public Cpsn next() {
            Token token = tokenizer.next();
            switch (token.kind) {
                case EOF:
                    throw new RuntimeException("Unexpected EOF");
                case INVALID:
                    throw new RuntimeException("Invalid token " + token.data);
                case OPEN_BRACKET: {
                    java.util.List<Cpsn> out = new ArrayList<Cpsn>();

                    while (true) {
                        if (tokenizer.peek().kind.equals(TokenKind.CLOSE_BRACKET)) {
                            tokenizer.next();
                            break;
                        }

                        out.add(this.next());
                    }

                    return new Cpsn.List(out);
                }
                case KEY: {
                    java.util.Map<String, Cpsn> out = new HashMap<String, Cpsn>();

                    String firstKey = token.data;
                    Cpsn firstValue = this.next();

                    out.put(firstKey, firstValue);
                    while (true) {
                        Token nextToken = tokenizer.next();

                        if (nextToken.kind.equals(TokenKind.SEMICOLON)) {
                            break;
                        }

                        Token key = nextToken;
                        if (!key.kind.equals(TokenKind.KEY)) {
                            throw new RuntimeException("Expected key got " + key.kind);
                        }

                        Cpsn value = this.next();
                        out.put(key.data, value);
                    }

                    return new Cpsn.Map(out);
                }
                case VALUE:
                    return new Cpsn.Value(token.data);
                case SEMICOLON:
                    return new Cpsn.Map();
                default:
                    throw new RuntimeException("Unexpected token " + token.kind);
            }
        }

        public boolean hasNext() {
            return tokenizer.hasNext();
        }
    }

    public static class GetFailedException extends Exception {
        public GetFailedException(String message) {
            super(message);
        }
    }

    public static Cpsn from(Tokenizer tokenizer) {
        Parser parser = new Parser(tokenizer);
        if (!parser.hasNext()) {
            return null;
        }
        return parser.next();
    }

    public abstract Cpsn get(int key);

    public abstract Cpsn get(String key);

    public abstract <T> T getOrThrow(String key) throws GetFailedException;

    public abstract void put(String key, Cpsn value);

    public abstract void add(Cpsn value);

    public static class List extends Cpsn {
        public final java.util.List<Cpsn> value;

        public List() {
            this.value = new java.util.ArrayList<Cpsn>();
        }

        public List(java.util.List<Cpsn> value) {
            this.value = value;
        }

        public Cpsn get(int key) {
            return this.value.get(key);
        }

        public Cpsn get(String key) {
            throw new RuntimeException("Can't index a list with a string");
        }

        public <T> T getOrThrow(String key) throws GetFailedException {
            throw new GetFailedException("Can't index a list with a string");
        }

        public void put(String key, Cpsn value) {
            throw new RuntimeException("Can't put to list with a string");
        }

        public void add(Cpsn value) {
            this.value.add(value);
        }

        public String toString() {
            StringBuilder out = new StringBuilder();

            out.append("[");
            for (int i = 0; i < this.value.size(); i++) {
                if (i > 0) {
                    out.append(" ");
                }

                out.append(this.value.get(i).toString());
            }
            out.append("]");

            return out.toString();
        }
    }

    public static class Map extends Cpsn {
        public final java.util.Map<String, Cpsn> value;

        public Map() {
            this.value = new java.util.HashMap<String, Cpsn>();
        }

        public Map(java.util.Map<String, Cpsn> value) {
            this.value = value;
        }

        public Cpsn get(int key) {
            throw new RuntimeException("Can't index a map with an integer");
        }

        public Cpsn get(String key) {
            return this.value.get(key);
        }

        public <T> T getOrThrow(String key) throws GetFailedException {
            Cpsn out = this.value.get(key);
            if (out == null) {
                throw new GetFailedException("Key " + key + " not found");
            }
            try {
                return (T) out;
            } catch (ClassCastException e) {
                throw new GetFailedException("Key " + key + " cast failed (" + e.getMessage() + ")");
            }
        }

        public void put(String key, Cpsn value) {
            this.value.put(key, value);
        }

        public void add(Cpsn value) {
            throw new RuntimeException("Can't add to map");
        }

        public String toString() {
            StringBuilder out = new StringBuilder();
            boolean first = true;
            for (String key : this.value.keySet()) {
                if (!first) {
                    out.append(" ");
                }
                first=false;
                out.append(key);
                out.append(" ");
                out.append(this.value.get(key).toString());
            }
            out.append(";");

            return out.toString();
        }
    }

    public static class Value extends Cpsn {
        public final String value;

        public Value(String value) {
            this.value = value;
        }

        public Cpsn get(int key) {
            throw new RuntimeException("Can't index a value with an integer");
        }

        public Cpsn get(String key) {
            throw new RuntimeException("Can't index a value with a string");
        }

        public <T> T getOrThrow(String key) throws GetFailedException {
            throw new GetFailedException("Can't index a value with a string");
        }

        public void put(String key, Cpsn value) {
            throw new RuntimeException("Can't put to value with a string");
        }

        public void add(Cpsn value) {
            throw new RuntimeException("Can't add to value");
        }

        public String toString() {
            StringBuilder out = new StringBuilder();
            out.append('\"');

            for (int i = 0; i < this.value.length(); i++) {
                char c = this.value.charAt(i);

                if (c == '\"' || c == '\\') {
                    out.append('\\');
                }

                out.append(c);
            }

            out.append('\"');

            return out.toString();
        }
    }
}
