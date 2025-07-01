package hansanhha.classes.sealed_classes;

public sealed class Hydralisk extends Zerg permits UpgradedHydralisk {

    @Override
    public void burrow() {
        System.out.println("hydralisk burrow");
    }

    @Override
    public void evolve() {
        System.out.println("hydralisk evolves into lurker");
    }

}
