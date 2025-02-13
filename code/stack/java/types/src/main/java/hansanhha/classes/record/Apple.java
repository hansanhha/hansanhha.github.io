package hansanhha.classes.record;

/*
    normal canonical constructor(일반 표준 생성자)를 선언한 레코드 클래스
 */
public record Apple(String name, int amount) implements Fruit {

    private static final int DEFAULT_APPLE_AMOUNT;


    static {
        DEFAULT_APPLE_AMOUNT = 1000;
    }

    public Apple(String name, int amount) {
        if (amount < 0) amount = DEFAULT_APPLE_AMOUNT;

        this.name = name;
        this.amount = amount;

        System.out.println("created Apple(record) name: " +  name + ", amount: " + amount);
    }

    public Apple(String name) {
        this(name, DEFAULT_APPLE_AMOUNT);
        System.out.println("use default apple amount");
    }

}
