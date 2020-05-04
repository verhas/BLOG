// snippet NumberedLineEmpty_v2
package javax0.blog.demo.throwable.v2;

public class NumberedLineEmpty extends LineEmpty {
    final protected int lineNr;

    public NumberedLineEmpty(int lineNr, LineEmpty cause) {
        super(cause);
        this.lineNr = lineNr;
    }

    @Override
    public String getMessage() {
        return "line " + lineNr + ". has zero length";
    }
}
// end snippet
