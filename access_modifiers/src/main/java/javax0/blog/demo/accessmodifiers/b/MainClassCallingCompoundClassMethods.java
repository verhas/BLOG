package javax0.blog.demo.accessmodifiers.b;

import javax0.blog.demo.accessmodifiers.a.CompoundClass;

public class MainClassCallingCompoundClassMethods  {

    class InnerClassExtendingCompoundClassInner1Class extends CompoundClass.Inner2 {

    }

    public static void main(String[] args) {
        var sut = new CompoundClass.Inner1();
    }
}
