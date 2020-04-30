package javax0.blog;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WorkFile {
    private final String absoluteFileName;

    public WorkFile(String fileName) {
        absoluteFileName = new File(fileName).getAbsolutePath();
    }

    protected String content = null;

    protected void read() throws IOException {
        if (content == null) {
            byte[] encoded = Files.readAllBytes(Paths.get(absoluteFileName));
            content = new String(encoded, StandardCharsets.UTF_8);
        }
    }

    public String getAbsoluteFileName() {
        return absoluteFileName;
    }

    public String getContent() throws IOException {
        read();
        return content;
    }
}
