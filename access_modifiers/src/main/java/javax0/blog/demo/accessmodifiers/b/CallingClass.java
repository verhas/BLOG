package javax0.blog.demo.accessmodifiers.b;

public class CallingClass {

    public static void main(String[] args) {
        var sut = new ClassExtendsClassA();
        sut.protectedMethod();
        sut.protectedMethodToOverride();
    }
}
