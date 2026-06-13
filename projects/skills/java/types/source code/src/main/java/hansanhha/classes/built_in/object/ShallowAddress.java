package hansanhha.classes.built_in.object;

// shallow copy example
public class ShallowAddress {

    String city;

    public ShallowAddress(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
