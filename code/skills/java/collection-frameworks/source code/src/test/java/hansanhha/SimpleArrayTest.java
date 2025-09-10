package hansanhha;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleArrayTest {

    @Test
    @DisplayName("int 배열은 0으로 초기화")
    void numberArrayInit() {
        var array = new int[5];
        assertThat(array).containsOnly(0);
        Arrays.stream(array).forEach(System.out::println);
    }

    @Test
    @DisplayName("문자열 배열은 null로 초기화")
    void stringArrayInit() {
        var array = new String[5];
        assertThat(array).containsOnlyNulls();
        Arrays.stream(array).forEach(System.out::println);
    }

    @Test
    @DisplayName("객체 배열은 null로 초기화")
    void objectArrayInit() {
        var array = new Object[5];
        assertThat(array).containsOnlyNulls();
        Arrays.stream(array).forEach(System.out::println);
    }

    @Test
    @DisplayName("배열은 랜덤 접근")
    void randomAccess() {
        var array = new int[]{1,2,3,4,5};
        var index = ThreadLocalRandom.current().nextInt(0, array.length);

        var element = array[index];

        assertThat(array).contains(element);
        System.out.println(element);
    }

    @Test
    @DisplayName("""
            특정 인덱스에서 요소 삽입 시, 뒤의 요소들 이동 필요
            마지막 요소는 삭제됨
            """)
    void insert() {
        var array = new int[]{1,2,4,5,6};
        var insertIndex = 2;

        for (int i = array.length - 1; i > insertIndex; i--) {
            array[i] = array[i - 1];
        }

        array[insertIndex] = 3;

        assertThat(array).containsOnly(1,2,3,4,5);
        Arrays.stream(array).forEach(System.out::println);
    }

    @Test
    @DisplayName("""
            배열 삭제 시, 삭제할 인덱스 이후의 요소를 앞으로 이동
            맨 마지막 요소는 특별한 수정이 필요가 없는 의미 없는 요소가 됨
            """)
    void remove() {
        var array = new int[]{1,2,6,3,4,5};
        var removeIndex = 2;

        for (int i = removeIndex; i < array.length-1; i++) {
            array[i] = array[i + 1];
        }

        assertThat(array).containsOnly(1,2,3,4,5);
        assertThat(array.length).isEqualTo(6);
        Arrays.stream(array).forEach(System.out::println);
    }

    @Test
    @DisplayName("""
            배열의 길이는 불변이므로
            확장하려면 더 긴 길이를 가진 배열을 생성하고
            루프를 돌아 원래 배열의 값들을 새로 생성한 배열에 복사해야 됨
            """)
    void expand() {
        var originalArray = new int[]{1, 2, 3, 4, 5};
        var expandLength = 5;

        var expandedArray = new int[originalArray.length + expandLength];

        for (int i = 0; i < originalArray.length; i++) {
            expandedArray[i] = originalArray[i];
        }

        assertThat(expandedArray).contains(1,2,3,4,5);
        Arrays.stream(expandedArray).forEach(System.out::println);
    }
}
