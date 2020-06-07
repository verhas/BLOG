package javax0.blog.demo.accessmodifiers.b;

import javax0.blog.demo.accessmodifiers.a.ClassA;

public class CallingClass {

    public static void main(String[] argv) {
        ClassA sut = new ClassA();
        sut.publicMethod();
    }
}
