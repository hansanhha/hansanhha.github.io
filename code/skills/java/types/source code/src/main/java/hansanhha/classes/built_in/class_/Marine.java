package hansanhha.classes.built_in.class_;

@AttackUnit
public class Marine extends TerranUnit<Small> {

    @Override
    public void attack() {
        System.out.println("marine attacked");
    }
}
