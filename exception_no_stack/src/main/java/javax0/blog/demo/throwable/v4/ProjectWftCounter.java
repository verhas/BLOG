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
