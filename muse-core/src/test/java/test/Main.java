package test;

public class Main {

    public static void main(String[] args) throws Exception {
        foo();
    }
    
    public static void foo() throws Exception {
        try {
            bar();
        } catch (Exception e) {
            throw e;
        }
        
    }
    
    public static void bar() throws Exception {
        throw new Exception("xxx");
    }
}
