package hansanhha.classes.record;

public record FruitBox<F extends Fruit>(
        F fruit,
        int size) {

    static {
        System.out.println("FruitBox(record) class loading..");
    }

    public static <F extends Fruit> FruitBox<F> of(F fruit, int size) {
        return new FruitBox<>(fruit, size);
    }

    public int getTotalAmount() {
        return fruit().amount() * size;
    }


    public static class Orange implements Fruit {

        private final String name;
        private int amount;

        public Orange(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int amount() {
            return amount;
        }

        public void setAmount(int amount) {
            if (amount < 0) amount = 0;
            this.amount = amount;
        }

    }

}
