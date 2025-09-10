package hansanhha.classes.built_in.object;

// deep copy example
public class DeepAddress implements Cloneable {

    String city;

    public DeepAddress(String city) {
        this.city = city;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new DeepAddress(city);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
