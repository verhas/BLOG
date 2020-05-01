package javax0.blog.demo.throwable.v4;

public class NumberedLineEmpty extends RuntimeException {
    final protected int lineNr;

    public NumberedLineEmpty(int lineNr, LineEmpty cause) {
        super(cause);
        this.setStackTrace(cause.getStackTrace());
        cause.setStackTrace(new StackTraceElement[0]);
        this.lineNr = lineNr;
    }

    @Override
    public String getMessage() {
        return "line " + lineNr + ".";
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
