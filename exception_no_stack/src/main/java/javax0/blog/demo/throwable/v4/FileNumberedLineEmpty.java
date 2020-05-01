package javax0.blog.demo.throwable.v4;

public class FileNumberedLineEmpty extends RuntimeException {
    final protected String fileName;

    public FileNumberedLineEmpty(String fileName, NumberedLinesAreEmpty cause) {
        super(cause);
        this.setStackTrace(cause.getStackTrace());
        cause.setStackTrace(new StackTraceElement[0]);
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        return fileName;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
