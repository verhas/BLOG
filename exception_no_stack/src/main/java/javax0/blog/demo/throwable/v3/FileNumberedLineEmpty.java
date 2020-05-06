// snippet FileNumberedLineEmpty_v3
package javax0.blog.demo.throwable.v3;

public class FileNumberedLineEmpty extends NumberedLineEmpty {
    final protected String fileName;

    public FileNumberedLineEmpty(String fileName, NumberedLineEmpty cause) {
        super(cause.lineNr, cause);
        this.setStackTrace(cause.getStackTrace());
        this.fileName = fileName;
    }

    // getMessage(), same as in v2
// skip
    @Override
    public String getMessage() {
        return fileName + ":" + lineNr + " is empty";
    }
// skip end

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
// end snippet
