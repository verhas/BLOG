package javax0.blog.demo.accessmodifiers.a;

import java.util.HashMap;
import java.util.Map;

public class EqualsImplementingClass {
    private int a;
    private String b;
    protected Object q;
    public final Map<String,String> z = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EqualsImplementingClass that)) return false;

        if (a != that.a) return false;
        if (!b.equals(that.b)) return false;
        if (!q.equals(that.q)) return false;
        return z.equals(that.z);
    }

    @Override
    public int hashCode() {
        int result = a;
        result = 31 * result + b.hashCode();
        result = 31 * result + q.hashCode();
        result = 31 * result + z.hashCode();
        return result;
    }
}
