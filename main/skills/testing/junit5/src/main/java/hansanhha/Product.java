package hansanhha;

public class Product {

    private Long id;
    private String name;
    private int quantity;
    private int amount;

    public void decrease(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("주문 수량이 1보다 작습니다");
        }

        if (this.quantity - quantity < 0) {
            throw new IllegalArgumentException("주문 수량이 보유 수량보다 큽니다");
        }

        this.quantity -= quantity;
    }

    public Product(Long id, String name, int quantity, int amount) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAmount() {
        return amount;
    }
}
