package javax0.blog.demo.throwable.v2;

public class FileNumberedLineEmpty extends NumberedLineEmpty {
    final protected String fileName;

    public FileNumberedLineEmpty(String fileName, NumberedLineEmpty cause) {
        super(cause.lineNr, cause);
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        return fileName + ":" + lineNr + " is empty";
    }
}
