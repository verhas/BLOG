package javax0.blog.demo.throwable.v3;

public class NumberedLineEmpty extends LineEmpty {
    final protected int lineNr;

    public NumberedLineEmpty(int lineNr, LineEmpty cause) {
        super(cause);
        this.setStackTrace(cause.getStackTrace());
        this.lineNr = lineNr;
    }

    @Override
    public String getMessage() {
        return "line " + lineNr + ". has zero length";
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
