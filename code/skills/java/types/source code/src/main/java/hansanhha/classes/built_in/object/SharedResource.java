package hansanhha.classes.built_in.object;

// wait, notify example
public class SharedResource {

    private String data;
    private boolean lock;

    public synchronized void produce(String value) throws InterruptedException {
        while (lock) {
            wait();
        }
        data = value;
        lock = true;
        System.out.println("produced: " + data);
        notify();
    }

    public synchronized String consume() throws InterruptedException {
        while (!lock) {
            wait();
        }
        lock = false;
        System.out.println("consumed: " + data);
        notify();
        return data;
    }
}
