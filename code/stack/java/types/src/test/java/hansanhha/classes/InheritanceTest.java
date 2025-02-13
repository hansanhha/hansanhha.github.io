package hansanhha.classes;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InheritanceTest {

    // 부모 클래스
    public static class Vehicle {
        String name = "vehicle";
        int speed;

        public Vehicle(String name, int speed) {
            this.name = name;
            this.speed = speed;
        }

        public Vehicle(int speed) {
            this.speed = speed;
        }

        public String boom() {
            return "vehicle: boom";
        }
    }

    // 자식 클래스
    public static class Car extends Vehicle {
        String name;
        int gear;

        public Car(String name, int speed, int gear) {
            super(speed);
            this.gear = gear;
            this.name = name;
        }

        @Override
        public String boom() {
            return "car: boom";
        }
    }

    @Test
    @DisplayName("자식 참조 타입과 부모 참조 타입 동일 필드 접근(정적 바인딩)과 동일 메서드 접근(동적 바인딩)")
    void test1() {
        Car car = new Car("car", 100, 1);

        // 동일한 이름으로 정의된 필드 접근은 정적 바인딩(compile-time)이므로 부모 필드가 감춰진다(hiding)
        assertThat(car.name).isNotEqualTo(((Vehicle) car).name);

        // 반면 메서드는 오버라이딩되어 실제 객체의 타입을 기준으로 실행된다 (동적 바인딩)
        assertThat(car.boom()).isEqualTo(((Vehicle) car).boom());
    }

    @Test
    @DisplayName("instanceof와 Class.isAssignableFrom()")
    void test2() {
        Car car = new Car("car", 100, 1);

        assertThat(car instanceof Vehicle).isTrue();
        assertThat(Vehicle.class.isAssignableFrom(Car.class)).isTrue();
    }

}
