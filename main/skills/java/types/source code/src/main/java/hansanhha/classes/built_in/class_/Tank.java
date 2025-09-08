package hansanhha.classes.built_in.class_;

@AttackUnit
public class Tank extends TerranUnit<Large> {

    @Override
    public void attack() {
        System.out.println("tank attacked");
    }
}
