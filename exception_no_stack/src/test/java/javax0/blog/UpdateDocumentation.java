package javax0.blog;

import javax0.geci.docugen.AdocSegmentSplitHelper;
import javax0.geci.docugen.JavaDocSegmentSplitHelper;
import javax0.geci.docugen.MarkdownSegmentSplitHelper;
import javax0.geci.docugen.Register;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UpdateDocumentation {

    @Test
    @DisplayName("Update the blog article")
    void updateArticle() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci
                        .source("..", ".")
                        .ignoreBinary()
                        .ignore("\\.git", "\\.idea", "\\.iml$", "target")
                        .log(Geci.MODIFIED)
                        .register(Register.allSnippetHandlers())
                        .splitHelper("adoc", new AdocSegmentSplitHelper())
                        .splitHelper("md", new MarkdownSegmentSplitHelper())
                        .splitHelper("java", new JavaDocSegmentSplitHelper())
                        .generate(),
                geci.failed());
    }
}
