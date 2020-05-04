// snippet FileWtfCounter_v1
package javax0.blog.demo.throwable.v1;

import java.io.FileNotFoundException;

public class FileWtfCounter {
    private final FileReader fileReader;

    public FileWtfCounter(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public int count() throws FileNotFoundException {
        final var lines = fileReader.list();
        int sum = 0;
        for (final var line : lines) {
            sum += new LineWtfCounter(line).count();
        }
        return sum;
    }

}

// end snippet
