// snippet FileWtfCounter_v4
package javax0.blog.demo.throwable.v4;

public class FileWtfCounter {
    private final FileReader fileReader;

    public FileWtfCounter(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public int count() {
        final var lines = fileReader.list();
        NumberedLinesAreEmpty exceptionCollector = null;
        int sum = 0;
        int lineNr = 1;
        for (final var line : lines) {
            try {
                sum += new LineWtfCounter(line).count();
            }catch(LineEmpty le){
                final var nle = new NumberedLineEmpty(lineNr,le);
                if( exceptionCollector == null ){
                    exceptionCollector = new NumberedLinesAreEmpty();
                }
                exceptionCollector.addSuppressed(nle);
            }
            lineNr ++;
        }
        if( exceptionCollector != null ){
            throw exceptionCollector;
        }
        return sum;
    }

}
// end snippet
