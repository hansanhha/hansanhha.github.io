package hansanhha.classes;

public class Inheritance {

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

        public void boom() {
            System.out.println("vehicle: boom");
        }
    }

    public static class Car extends Vehicle {

        String name;
        int gear;

        public Car(String name, int speed, int gear) {
            super(speed);
            this.gear = gear;
            this.name = name;
        }

        public void boom() {
            System.out.println("car: boom");
        }
    }

    public static void main(String[] args) {
        Car car = new Car("car", 100, 1);

        System.out.println(car.name);
        System.out.println(((Vehicle)car).name);

        car.boom();

        if (car instanceof Vehicle) {
            System.out.println("car is instanceof vehicle");
        }

        if (Vehicle.class.isAssignableFrom(Car.class)) {
            System.out.println("car is subtype of vehicle");
        }

    }

}
