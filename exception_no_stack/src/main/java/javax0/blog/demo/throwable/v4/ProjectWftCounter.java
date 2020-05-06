// snippet ProjectWftCounter_v4
package javax0.blog.demo.throwable.v4;

import javax0.blog.demo.throwable.FileLister;

public class ProjectWftCounter {

    private final FileLister fileLister;

    public ProjectWftCounter(FileLister fileLister) {
        this.fileLister = fileLister;
    }


    public int count() {
        final var fileNames = fileLister.list();
        FileNumberedLinesAreEmpty exceptionCollector = null;
        int sum = 0;
        for (final var fileName : fileNames) {
            try {
                sum += new FileWtfCounter(new FileReader(fileName)).count();
            } catch (NumberedLinesAreEmpty nle) {
                if( exceptionCollector == null ){
                    exceptionCollector = new FileNumberedLinesAreEmpty();
                }
                exceptionCollector.addSuppressed(nle);
            }
        }
        if( exceptionCollector != null ){
            throw exceptionCollector;
        }
        return sum;
    }
}
// end snippet
