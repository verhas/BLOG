// snippet SneakyThrower
package javax0.blog.demo.throwable.sneaky;

public class SneakyThrower {
    public static <E extends Throwable> E throwSneaky(Throwable e) throws E {
        throw (E) e;
    }
}
// end snippet
