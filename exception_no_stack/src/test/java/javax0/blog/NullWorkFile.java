package javax0.blog;

public class NullWorkFile extends WorkFile {
    public NullWorkFile(String fileName) {
        super(fileName);
    }

    public void read() {
        if (content == null) {
            content = "";
        }
    }
}
