package javax0.blog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestWtfCounter {

    @Test
    @DisplayName("Counts the number of WTFs in a string")
    void test() {
        final var string = "wtf is a wtf even if it means the end of the workday, that is Wednesday, Thursday, Friday";
        final var result = new CountWtfs(null).counter(string);
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("Throws up for a zero length line")
    void testThrowing() {
        Throwable thrown = catchThrowable(() ->
                new CountWtfs(null, () -> List.of("a", "b", "c"), NullWorkFile::new)
                        .count());
        thrown.printStackTrace();
        assertThat(thrown).isInstanceOf(NamedFileTooShort.class);
    }

}
