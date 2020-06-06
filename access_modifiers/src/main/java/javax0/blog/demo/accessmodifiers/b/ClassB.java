package javax0.blog.demo.accessmodifiers.b;

import javax0.blog.demo.accessmodifiers.a.ClassA;

public class ClassB {

    public static void main(String[] argv){
        var sut = new ClassA();
        sut.publicMethod();
        //sut.packagePrivateMethod();
        //sut.protectedMethod();
        //sut.privateMethod();

        var sutE = new ClassExtendsClassA();
        sutE.protectedMethod();
        //sutE.protectedMethod2();
    }
}
