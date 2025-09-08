package hansanhha.classes.built_in.object;

// deep copy example
public class DeepOrder implements Cloneable {

    String orderNumber;
    DeepAddress deepAddress;

    public DeepOrder(String orderNumber, DeepAddress deepAddress) {
        this.orderNumber = orderNumber;
        this.deepAddress = deepAddress;
    }

    public DeepOrder doClone() {
        try {
            DeepOrder cloned = (DeepOrder) super.clone();
            cloned.deepAddress = (DeepAddress) deepAddress.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    public DeepAddress getDeepAddress() {
        return deepAddress;
    }
}
