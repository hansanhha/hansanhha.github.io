package hansanhha;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class ArraysTest {

    static int size = 100_000_000;
    static int[] array;

    @BeforeAll
    static void init() {
        array = IntStream.range(0, size).toArray();
    }

    @Test
    void zxcv() {
        System.out.println(array[size - 1]);
    }
}
