package hansanhha;

import java.util.ArrayList;
import java.util.List;

public class GenericType {

    /**
     * {@link RowType}과 달리 Bank 클래스에 제네릭을 적용함으로써
     * 통화를 추가해도 동일한 클래스를 사용해서 로직을 이용할 수 있다
     */
    public static void main(String[] args) {
        Bank<Dollar> dollarBank = new Bank<>();
        Bank<Pound> poundBank = new Bank<>();

        dollarBank.save(new Dollar(5));
        dollarBank.save(new Dollar(5));

        poundBank.save(new Pound(10));
        poundBank.save(new Pound(10));

        System.out.println(dollarBank.getBalance());
        System.out.println(poundBank.getBalance());
    }

    public static class Bank<M extends Money> {

        private final List<M> monies = new ArrayList<>();

        public void save(M money) {
            monies.add(money);
        }

        public String getBalance() {
            return "balance: " + monies.stream().mapToInt(M::value).sum();
        }
    }

    public interface Money {
        int value();
    }

    public record Dollar(int value) implements Money {}
    public record Pound(int value) implements Money {}
}
