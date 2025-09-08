package hansanhha;

public class OrderService {

    public double calculateTotalPrice(double originalPrice, double discountRate) {
        if (originalPrice < 0) {
            throw new IllegalArgumentException("가격은 음수가 될 수 없습니다.");
        }
        if (discountRate < 0.0 || discountRate > 1.0) {
            throw new IllegalArgumentException("할인율은 0 ~ 1 사이여야 합니다.");
        }

        double discountedPrice = originalPrice * (1 - discountRate);
        return Math.max(discountedPrice, 0.0);
    }

    public double applyCoupon(double price, String couponCode) {
        if (couponCode == null || couponCode.isBlank()) {
            return price;
        }

        return switch (couponCode) {
            case "DISCOUNT10" ->
                    price * 0.9;
            case "DISCOUNT20" ->
                    price * 0.8;
            default ->
                    price;
        };
    }
}

