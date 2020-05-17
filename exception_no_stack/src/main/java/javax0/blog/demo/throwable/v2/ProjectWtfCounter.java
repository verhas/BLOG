// snippet ProjectWtfCounter_v2
package javax0.blog.demo.throwable.v2;

import javax0.blog.demo.throwable.FileLister;

public class ProjectWtfCounter {
// skip
    private final FileLister fileLister;

    public ProjectWtfCounter(FileLister fileLister) {
        this.fileLister = fileLister;
    }
// skip end
    // some lines deleted ...
    public int count() {
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
// end snippet
