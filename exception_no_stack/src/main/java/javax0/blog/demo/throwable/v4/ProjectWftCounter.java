package javax0.blog.demo.throwable.v4;

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
        FileNumberedLinesAreEmpty exceptionCollector = null;
        int sum = 0;
        for (final var fileName : fileNames) {
            try {
                sum += new FileWtfCounter(new FileReader(fileName)).count();
            } catch (NumberedLinesAreEmpty nle) {
                final var fnlre = new FileNumberedLineEmpty(fileName,nle);
                if( exceptionCollector == null ){
                    exceptionCollector = new FileNumberedLinesAreEmpty();
                }
                exceptionCollector.addSuppressed(fnlre);
            }
        }
        if( exceptionCollector != null ){
            throw exceptionCollector;
        }
        return sum;
    }
}
