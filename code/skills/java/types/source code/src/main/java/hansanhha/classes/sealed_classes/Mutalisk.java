package hansanhha.classes.sealed_classes;

public final class Mutalisk extends Zerg {

    @Override
    public void burrow() {
        System.out.println("mutalisk cannot burrow");
    }

    @Override
    public void evolve() {
        System.out.println("mutalisk evolves into guardian");
    }
}
