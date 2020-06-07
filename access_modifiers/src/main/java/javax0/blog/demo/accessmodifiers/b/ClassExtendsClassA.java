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

    @Override
    protected void protectedMethodToOverride(){
        System.out.println("protectedMethodToOverride... overridden");
    }
}
