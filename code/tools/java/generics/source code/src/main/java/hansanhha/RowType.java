package hansanhha;

import java.util.ArrayList;
import java.util.List;

public class RowType {

    // 통화를 추가할 때마다 그에 맞는 Bank 클래스도 같이 추가해줘야 한다
    // 동일한 로직을 가진 Bank 클래스가 많아질수록 유지보수성이 떨어진다
    public static void main(String[] args) {
        DollarBank dollarBank = new DollarBank();
        PoundBank poundBank = new PoundBank();

        dollarBank.save(new Dollar(5));
        dollarBank.save(new Dollar(5));

        poundBank.save(new Pound(10));
        poundBank.save(new Pound(10));

        System.out.println(dollarBank.getBalance());
        System.out.println(poundBank.getBalance());
    }

    public static class DollarBank {
        private final List<Dollar> dollars = new ArrayList<>();

        public void save(Dollar dollar) {
            dollars.add(dollar);
        }

        public String getBalance() {
            return "balance: " + dollars.stream().mapToInt(Dollar::value).sum();
        }
    }

    public static class PoundBank {
        private final List<Pound> pounds = new ArrayList<>();

        public void save(Pound pound) {
            pounds.add(pound);
        }

        public String getBalance() {
            return "balance: " + pounds.stream().mapToInt(Pound::value).sum();
        }
    }


    public record Dollar(int value) {}
    public record Pound(int value) {}
}
