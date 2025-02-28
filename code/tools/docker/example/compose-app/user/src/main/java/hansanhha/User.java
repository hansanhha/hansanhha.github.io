package hansanhha;

public record User(
        String name) {

    @Override
    public String toString() {
        return name + "user";
    }
}
