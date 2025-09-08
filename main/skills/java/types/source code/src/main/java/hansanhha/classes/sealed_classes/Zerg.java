package hansanhha.classes.sealed_classes;

public sealed abstract class Zerg permits Mutalisk, Hydralisk {

    abstract void burrow();
    abstract void evolve();

}
