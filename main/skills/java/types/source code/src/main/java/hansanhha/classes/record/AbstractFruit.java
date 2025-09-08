package hansanhha.classes.record;


public class AbstractFruit implements Fruit {

    // 레코드는 다른 클래스를 상속할 수 없다
    // extends 키워드를 사용하면 컴파일 오류가 발생한다
//    record watermelon() extends AbstarctFruit{
//
//    }

    private final String name;
    private int amount;

    public AbstractFruit(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int amount() {
        return amount;
    }
}
