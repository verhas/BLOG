package javax0.blog.demo.throwable.v2;

public class LineEmpty extends RuntimeException {
    public LineEmpty(String message) {
        super(message);
    }

    public LineEmpty() {
        this("There is a zero length line");
    }

    public LineEmpty(Throwable cause) {
        super(cause);
    }
}
