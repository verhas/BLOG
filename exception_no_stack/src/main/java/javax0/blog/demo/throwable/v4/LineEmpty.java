package javax0.blog.demo.throwable.v4;

public class LineEmpty extends RuntimeException {
    public LineEmpty(String message) {
        super(message);
    }

    public LineEmpty() {
        this("There is a zero length line");
    }
}
