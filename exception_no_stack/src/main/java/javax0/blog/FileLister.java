package javax0.blog;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileLister implements Lister {

    private final File root;

    public FileLister(String rootName) {
        root = new File(rootName);
    }

    FileLister() {
        root = null;
    }

    public List<String> list() {
        return Arrays.asList(
                Objects.requireNonNull(
                        Objects.requireNonNull(root).list((dir, name) -> name.endsWith(".txt"))
                )
        );
    }
}
