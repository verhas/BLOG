package javax0.blog.demo.throwable.v3;

import javax0.blog.demo.throwable.FileLister;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestWtfCounter {

    @Test
    @DisplayName("Throws up for a zero length line")
    void testThrowing() {
        Throwable thrown = catchThrowable(() ->
                new ProjectWftCounter(new FileLister())
                        .count());
        assertThat(thrown).isInstanceOf(FileNumberedLineEmpty.class);
        thrown.printStackTrace();
    }

}
