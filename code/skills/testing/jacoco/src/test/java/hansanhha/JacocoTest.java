package hansanhha;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JacocoTest {

    private final OrderService orderService = new OrderService();

    @Test
    void 할인_적용_후_최종_가격_계산() {
        double totalPrice = orderService.calculateTotalPrice(100.0, 0.1);
        assertEquals(90.0, totalPrice);
    }

    @Test
    void 할인율이_1_초과면_예외_발생() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.calculateTotalPrice(100.0, 1.5);
        });
    }

    @Test
    void 가격이_음수이면_예외_발생() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.calculateTotalPrice(-50.0, 0.1);
        });
    }

    @Test
    void 쿠폰이_없으면_원래_가격_유지() {
        double price = orderService.applyCoupon(100.0, null);
        assertEquals(100.0, price);
    }

    @Test
    void DISCOUNT10_쿠폰_적용_시_10퍼센트_할인() {
        double price = orderService.applyCoupon(100.0, "DISCOUNT10");
        assertEquals(90.0, price);
    }

    @Test
    void DISCOUNT20_쿠폰_적용_시_20퍼센트_할인() {
        double price = orderService.applyCoupon(100.0, "DISCOUNT20");
        assertEquals(80.0, price);
    }

    @Test
    void 알_수_없는_쿠폰_입력_시_가격_유지() {
        double price = orderService.applyCoupon(100.0, "UNKNOWN");
        assertEquals(100.0, price);
    }
}
