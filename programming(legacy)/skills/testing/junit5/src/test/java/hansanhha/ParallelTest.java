package hansanhha;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

public class ParallelTest {

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void test1() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " - 실행: test1");
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void test2() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " - 실행: test2");
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void test3() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " - 실행: test3");
    }

    @Test
    // 순차 실행
    @Execution(ExecutionMode.SAME_THREAD)
    void sequentialTest() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " - 순차 실행됨");
    }

}
