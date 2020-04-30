package javax0.blog;

public class FileTooShort extends RuntimeException {
    public FileTooShort(String message) {
        super(message);
    }

    public FileTooShort() {
        this("There is a zero length file");
    }
}
