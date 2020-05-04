// snippet FileWtfCounter_v1
package javax0.blog.demo.throwable.v1;

public class FileWtfCounter {
// skip
    private final FileReader fileReader;

    public FileWtfCounter(FileReader fileReader) {
        this.fileReader = fileReader;
    }
//skip end
    // fileReader injection is not listed
    public int count() {
        final var lines = fileReader.list();
        int sum = 0;
        for (final var line : lines) {
            sum += new LineWtfCounter(line).count();
        }
        return sum;
    }

}

// end snippet
