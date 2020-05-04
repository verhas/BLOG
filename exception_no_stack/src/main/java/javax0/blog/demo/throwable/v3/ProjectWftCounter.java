package javax0.blog.demo.throwable.v3;

import javax0.blog.demo.throwable.FileLister;

import java.io.FileNotFoundException;

public class ProjectWftCounter {

    private final FileLister fileLister;

    public ProjectWftCounter(FileLister fileLister) {
        this.fileLister = fileLister;
    }


    public int count() throws FileNotFoundException {
        final var fileNames = fileLister.list();
        int sum = 0;
        for (final var fileName : fileNames) {
            try {
                sum += new FileWtfCounter(new FileReader(fileName)).count();
            } catch (NumberedLineEmpty nle) {
                throw new FileNumberedLineEmpty(fileName, nle);
            }
        }
        return sum;
    }
}
