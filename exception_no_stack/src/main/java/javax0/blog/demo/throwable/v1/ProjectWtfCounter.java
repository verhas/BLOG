// snippet ProjectWtfCounter_v1
package javax0.blog.demo.throwable.v1;

import javax0.blog.demo.throwable.FileLister;

public class ProjectWtfCounter {
// skip
    private final FileLister fileLister;

    public ProjectWtfCounter(FileLister fileLister) {
        this.fileLister = fileLister;
    }

    // skip end
    // fileLister injection is not listed
    public int count() {
        final var fileNames = fileLister.list();
        int sum = 0;
        for (final var fileName : fileNames) {
            sum += new FileWtfCounter(new FileReader(fileName)).count();
        }
        return sum;
    }
}

// end snippet
