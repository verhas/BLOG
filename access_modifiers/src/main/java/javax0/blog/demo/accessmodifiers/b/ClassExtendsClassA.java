package javax0.blog.demo.accessmodifiers.b;

import javax0.blog.demo.accessmodifiers.a.ClassA;

public class ClassExtendsClassA extends ClassA {

    public static void main(String[] argv) {
        var sut = new ClassExtendsClassA();
        sut.publicMethod();
        //sut.packagePrivateMethod();
        sut.protectedMethod();
        //sut.privateMethod();
    }

    public void callerMethod() {
        var sut1 = new ClassA();
        sut1.publicMethod();
        //sut1.protectedMethod();
    }

}
