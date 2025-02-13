package hansanhha;

import java.util.Objects;

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

    public Product(String name, int quantity, int amount) {
        this(0L, name, quantity, amount);
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

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Product other)) return false;
        return Objects.equals(name, other.name);
    }
}
