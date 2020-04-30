package javax0.blog;

import java.io.IOException;
import java.util.function.Function;

/**
 * The code quality can be measured by the number of WFTs that fly through the room during the code review process.
 */
public class CountWtfs {
    private final Lister lister;

    public CountWtfs(String dir) {
        lister = new FileLister(dir);
    }

    Function<String, WorkFile> workFileProvider = WorkFile::new;

    CountWtfs(String dir, Lister lister, Function<String, WorkFile> workFileProvider) {
        this.lister = lister;
        this.workFileProvider = workFileProvider;
    }

    public int count() throws IOException {
        final var files = lister.list();
        int sum = 0;
        for (String fileName : files) {
            WorkFile file = workFileProvider.apply(fileName);
            String content = file.getContent();
            int counter;
            try {
                counter = counter(content);
            } catch (FileTooShort ftsh) {
                throw new NamedFileTooShort(ftsh, fileName);
            }
            sum += counter;
        }
        return sum;
    }

    public static final String WTF = "wtf";
    public static final int WTF_LEN = WTF.length();

    int counter(String content) {
        int index = 0;
        int count = 0;

        if (content.length() == 0) {
            throw new FileTooShort();
        }

        while (index != -1) {
            index = content.indexOf(WTF, index);
            if (index != -1) {
                count++;
                index += WTF_LEN;
            }
        }
        return count;
    }

}
