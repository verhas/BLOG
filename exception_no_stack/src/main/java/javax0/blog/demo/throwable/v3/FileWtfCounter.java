package javax0.blog.demo.throwable.v3;

public class FileWtfCounter {
    private final FileReader fileReader;

    public FileWtfCounter(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public int count() {
        final var lines = fileReader.list();
        int sum = 0;
        int lineNr = 1;
        for (final var line : lines) {
            try {
                sum += new LineWtfCounter(line).count();
            }catch(LineEmpty le){
                throw new NumberedLineEmpty(lineNr,le);
            }
            lineNr ++;
        }
        return sum;
    }

}
