package javax0.blog.demo.throwable.v4;

public class ExceptionStructurePrettyPrinter {
    final private Throwable throwable;

    public ExceptionStructurePrettyPrinter(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getMessage(int maxLevel) {
        final var sb = new StringBuilder();
        return getMessage("", throwable, sb, 0, maxLevel);
    }

    private static String getMessage(String prefix, Throwable t, StringBuilder sb, int tab, int level) {
        if (level == 0) {
            return sb.toString();
        }
        if (t.getMessage() != null) {
            sb.append(" ".repeat(tab))
                    .append(prefix)
                    .append(t.getClass().getSimpleName())
                    .append("(\"").append(t.getMessage()).append("\")")
                    .append("\n");
        }
        if (t.getSuppressed() != null) {
            for (final var suppressed : t.getSuppressed()) {
                getMessage("Suppressed: ", suppressed, sb, tab + 2, level - 1);
            }
        }
        if (t.getCause() != null) {
            getMessage("Caused by:", t.getCause(), sb, tab + 2, level - 1);
        }
        return sb.toString();
    }

}
