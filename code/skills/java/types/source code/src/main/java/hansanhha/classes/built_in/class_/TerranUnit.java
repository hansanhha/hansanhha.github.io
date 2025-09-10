package hansanhha.classes.built_in.class_;

public abstract class TerranUnit<T extends Type> implements Unit<T> {

    int hp;
    int damage;

    protected void fix() {
        System.out.println("unit fixed");
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public int getDamage() {
        return damage;
    }
}
