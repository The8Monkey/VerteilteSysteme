public class Fibonacci {
    public static void main(String[] args) {
        System.out.println("this is figabuette:");
        for(int i = 1; i < 11; i++){
            System.out.printf("value f%3d: %7d %n", i, fibonacci(i));
        }
    }

    public static int fibonacci(int count){
        return fib(0,1, count);
    }

    private static int fib(int lastvalue, int value, int count){
        if(count <= 1){
            return value;
        } else {
            return fib(value, lastvalue + value, --count);
        }
    }
}
