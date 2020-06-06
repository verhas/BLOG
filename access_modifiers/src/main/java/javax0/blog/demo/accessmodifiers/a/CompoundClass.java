package javax0.blog.demo.accessmodifiers.a;

public class CompoundClass {
    protected static class Inner1 {
        private void privateMethod() {
            System.out.println("privateMethod");
        }

        void packagePrivateMethod() {
            System.out.println("packagePrivateMethod");
        }

        protected void protectedMethod() {
            System.out.println("protectedMethod");
        }

        protected void protectedMethod2() {
            System.out.println("protectedMethod2");
        }

        public void publicMethod() {
            System.out.println("publicMethod");
        }
    }

    protected static class Inner2 {
        private void privateMethod() {
            System.out.println("privateMethod");
        }

        void packagePrivateMethod() {
            System.out.println("packagePrivateMethod");
        }

        protected void protectedMethod() {
            System.out.println("protectedMethod");
        }

        protected void protectedMethod2() {
            System.out.println("protectedMethod2");
        }

        public void publicMethod() {
            System.out.println("publicMethod");
        }
        public static void main(String[] argv){
            var sut = new Inner1();
            sut.publicMethod();
            sut.protectedMethod();
            sut.packagePrivateMethod();
            sut.privateMethod();
        }
    }

    public static void main(String[] argv){
        var sut = new Inner1();
        sut.publicMethod();
        sut.protectedMethod();
        sut.packagePrivateMethod();
        sut.privateMethod();
    }


}
