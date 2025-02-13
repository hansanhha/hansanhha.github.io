package hansanhha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NestedTest {

    BankAccount account;

    @BeforeEach
    void setUp() {
        account = new BankAccount(100); // 초기 잔액 100
    }

    @Test
    void initialBalanceShouldBeSetCorrectly() {
        Assertions.assertEquals(100, account.getBalance());
    }

    @Nested
    class DepositTests {
        @Test
        void depositShouldIncreaseBalance() {
            account.deposit(50);
            Assertions.assertEquals(150, account.getBalance());
        }
    }

    @Nested
    class WithdrawTests {
        @Test
        void withdrawShouldDecreaseBalance() {
            account.withdraw(30);
            Assertions.assertEquals(70, account.getBalance());
        }

        @Test
        void withdrawMoreThanBalanceShouldThrowException() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> account.withdraw(200));
        }
    }

    // 테스트 대상 클래스
    static class BankAccount {
        private int balance;

        BankAccount(int balance) {
            this.balance = balance;
        }

        void deposit(int amount) {
            balance += amount;
        }

        void withdraw(int amount) {
            if (amount > balance) {
                throw new IllegalArgumentException("잔액 부족");
            }
            balance -= amount;
        }

        int getBalance() {
            return balance;
        }
    }
}
