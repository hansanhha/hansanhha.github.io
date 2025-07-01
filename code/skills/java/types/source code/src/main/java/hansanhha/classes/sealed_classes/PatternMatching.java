package hansanhha.classes.sealed_classes;

public class PatternMatching {

    public static void main(String[] args) {

        Hydralisk hydralisk = new Hydralisk();
        UpgradedHydralisk upgradedHydralisk = new UpgradedHydralisk();
        Mutalisk mutalisk = new Mutalisk();

        burrowZergUnit(hydralisk);
        burrowZergUnit(upgradedHydralisk);
        burrowZergUnit(mutalisk);

    }

    private static void burrowZergUnit(Zerg zerg) {
        switch (zerg) {
            case UpgradedHydralisk upgradedHydralisk -> upgradedHydralisk.burrow();
            case Hydralisk hydralisk -> hydralisk.burrow();
            case Mutalisk mutalisk -> mutalisk.burrow();
        }
    }
}
