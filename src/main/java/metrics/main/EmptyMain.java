package metrics.main;

@SuppressWarnings("unused")
public class EmptyMain {

    public static void main(String[] args) {
        System.out.println("Hello World");
        while (true) {
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
            }
        }
    }
}
