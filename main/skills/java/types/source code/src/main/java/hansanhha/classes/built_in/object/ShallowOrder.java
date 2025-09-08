package hansanhha.classes.built_in.object;

// shallow copy example
public class ShallowOrder implements Cloneable {

    String orderNumber;
    ShallowAddress shallowAddress;

    public ShallowOrder(String orderNumber, ShallowAddress shallowAddress) {
        this.orderNumber = orderNumber;
        this.shallowAddress = shallowAddress;
    }

    public ShallowOrder doClone() {
        try {
            return (ShallowOrder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    public ShallowAddress getAddress() {
        return shallowAddress;
    }
}
