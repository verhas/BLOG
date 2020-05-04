// snippet NumberedLineEmpty_v3
package javax0.blog.demo.throwable.v3;

public class NumberedLineEmpty extends LineEmpty {
    final protected int lineNr;

    public NumberedLineEmpty(int lineNr, LineEmpty cause) {
        super(cause);
        this.setStackTrace(cause.getStackTrace());
        this.lineNr = lineNr;
    }

    // getMessage() same as in v2
// skip
    @Override
    public String getMessage() {
        return "line " + lineNr + ". has zero length";
    }
// skip end

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
// end snippet
