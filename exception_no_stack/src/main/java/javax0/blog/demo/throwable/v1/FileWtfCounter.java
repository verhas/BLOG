package javax0.blog.demo.throwable.v1;

import javax0.blog.demo.throwable.Counter;
import javax0.blog.demo.throwable.LineWtfCounter;

import java.io.FileNotFoundException;

public class FileWtfCounter implements Counter {
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
