package hansanhha.classes.record;

/*
    compact canonical constructor(축약형 표준 생성자)를 선언한 레코드
 */
public record Banana(String name, int amount) implements Fruit {

    private static final int DEFAULT_BANANA_AMOUNT = 500;

    public Banana {
        if (amount < 0) amount = DEFAULT_BANANA_AMOUNT;

        System.out.println("created Banana(record) name: " +  name + ", amount: " + amount);
    }

}
