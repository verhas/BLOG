package javax0.blog;

public class NamedFileTooShort extends FileTooShort {

    public NamedFileTooShort(FileTooShort lts, String fileName) {
        super("File " + fileName + " has a zero length line");
        System.out.println("Stack trace vvvvv in constructor");
        printStackTrace();
        System.out.println("Stack trace ^^^^^ in constructor");
        super.setStackTrace(lts.getStackTrace());
    }

    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
