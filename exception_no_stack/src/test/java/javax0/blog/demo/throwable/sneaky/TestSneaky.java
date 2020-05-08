// snippet SneakyThrowTest
package javax0.blog.demo.throwable.sneaky;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javax0.blog.demo.throwable.sneaky.SneakyThrower.throwSneaky;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestSneaky {

    @DisplayName("Can throw checked exception without declaring it")
    @Test
    void canThrowChecked() {
        class FlameThrower {
            void throwExceptionDeclared() throws Exception {
                throw new Exception();
            }

            void throwExceptionSecretly() {
                throwSneaky(new Exception());
            }
        }
        final var sut = new FlameThrower();
        assertThat(catchThrowable(() -> sut.throwExceptionDeclared())).isInstanceOf(Exception.class);
        assertThat(catchThrowable(() -> sut.throwExceptionSecretly())).isInstanceOf(Exception.class);
    }

    int doesNotReturn() {
        throw throwSneaky(new Exception());
        // no need for a return command
    }

}
// end snippet
