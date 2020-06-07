package javax0.blog.demo.accessmodifiers.a;

class ClassA {

        public void publicMethod() {
            System.out.println("publicMethod");
        }
    public static void main(String[] argv){
        var sut = new ClassA();
        sut.publicMethod();
    }

    public static ClassA makeA(){
        return new ClassA();
    }
}
