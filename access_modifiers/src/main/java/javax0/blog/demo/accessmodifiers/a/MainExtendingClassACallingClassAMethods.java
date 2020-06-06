package javax0.blog.demo.accessmodifiers.a;

public class MainExtendingClassACallingClassAMethods extends ClassA {

    public static void main(String[] argv){
        var sut = new ClassA();
        sut.publicMethod();
        sut.packagePrivateMethod();
        sut.protectedMethod();
        //sut.privateMethod();
    }

}
