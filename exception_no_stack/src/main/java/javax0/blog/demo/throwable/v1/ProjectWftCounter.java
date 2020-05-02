// snippet ProjectWftCounter_v1
package javax0.blog.demo.throwable.v1;

import javax0.blog.demo.throwable.Counter;
import javax0.blog.demo.throwable.FileLister;

import java.io.FileNotFoundException;

public class ProjectWftCounter implements Counter {

    private final FileLister fileLister;

    public ProjectWftCounter(FileLister fileLister) {
        this.fileLister = fileLister;
    }


    public int count() throws FileNotFoundException {
        final var fileNames = fileLister.list();
        int sum = 0;
        for (final var fileName : fileNames) {
            sum += new FileWtfCounter(new FileReader(fileName)).count();
        }
        return sum;
    }
}

// end snippet
