package hansanhha.classes.enums;

public enum Coin {

    PENNY(1),
    NICKEL(5),
    DIME(10),
    QUARTER(25);

    {
        System.out.println("coin enum class initiating...");
    }

    static {
        System.out.println("coin enum class loading...");
    }

    // 생성자는 public, protected 접근 제어자를 가질 수 없으며
    // 자동으로 private 접근 제어자 범위를 가진다
    Coin(int value) {
        this.value = value;
    }

    private final int value;

    public int value() {
        return value;
    }

}
