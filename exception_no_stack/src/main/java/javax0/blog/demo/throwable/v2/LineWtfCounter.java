package javax0.blog.demo.throwable.v2;

public class LineWtfCounter {
    private final String line;

    public LineWtfCounter(String line) {
        this.line = line;
    }

    public static final String WTF = "wtf";
    public static final int WTF_LEN = WTF.length();

    public int count() {
        if (line.length() == 0) {
            throw new LineEmpty();
        }
        int index = 0;
        int count = 0;

        while (index != -1) {
            index = line.indexOf(WTF, index);
            if (index != -1) {
                count++;
                index += WTF_LEN;
            }
        }
        return count;
    }
}