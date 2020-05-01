package javax0.blog.demo.throwable.v3;

import java.io.FileNotFoundException;
import java.util.List;

public class FileReader {
    final String fileName;

    public FileReader(String fileName) {
        this.fileName = fileName;
    }

    public List<String> list() throws FileNotFoundException {
        if (fileName.equals("a.txt")) {
            return List.of("wtf wtf", "wtf something", "nothing");
        }
        if (fileName.equals("b.txt")) {
            return List.of("wtf wtf wtf", "wtf something wtf", "nothing wtf");
        }
        if (fileName.equals("c.txt")) {
            return List.of("wtf wtf wtf", "wtf something wtf", "nothing wtf", "");
        }
        throw new FileNotFoundException(fileName);
    }

}
