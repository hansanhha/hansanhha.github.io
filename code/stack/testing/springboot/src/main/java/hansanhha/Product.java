package hansanhha;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Product {

    @Id
    @GeneratedValue
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

    protected Product() {

    }

    public Product(String name, int quantity, int amount) {
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

    public void setName(String name) {
        this.name = name;
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
