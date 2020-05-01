package javax0.blog.demo.throwable.v4;

public class FileNumberedLinesAreEmpty extends RuntimeException {
    @Override
    public String getMessage(){
        return "There are empty lines";
    }
}
