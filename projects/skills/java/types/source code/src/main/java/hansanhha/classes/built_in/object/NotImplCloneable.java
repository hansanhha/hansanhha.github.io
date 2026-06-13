package hansanhha.classes.built_in.object;

public class NotImplCloneable {

    // Cloneable 인터페이스를 구현하지 않으면 런타임에 CloneNotSupportedException checked 예외가 발생한다
    public NotImplCloneable doClone() throws CloneNotSupportedException {
        return (NotImplCloneable) this.clone();
    }
}
