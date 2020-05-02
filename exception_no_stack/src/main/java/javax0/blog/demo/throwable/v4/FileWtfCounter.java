package javax0.blog.demo.throwable.v4;

import javax0.blog.demo.throwable.Counter;

import java.io.FileNotFoundException;

public class FileWtfCounter implements Counter {
    private final FileReader fileReader;

    public FileWtfCounter(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public int count() throws FileNotFoundException {
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
