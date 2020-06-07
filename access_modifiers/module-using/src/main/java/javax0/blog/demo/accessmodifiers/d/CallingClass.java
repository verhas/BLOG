package javax0.blog.demo.accessmodifiers.d;

import javax0.blog.demo.accessmodifiers.a.ClassA;
import javax0.blog.demo.accessmodifiers.b.ClassB;

public class CallingClass {

    public static void main(String[] argv) {
        var suta = new ClassA();
        suta.publicMethod();
        var sutb = new ClassB();
    }
}
