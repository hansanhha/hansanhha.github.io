package hansanhha;

import org.junit.jupiter.api.*;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("MyArrayList 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SimpleArrayListTest {

    List<Integer> list;

    @BeforeEach
    void init() {
        list = new SimpleArrayList<>();
    }

    @Test
    void 요소만_삽입() {
        // given/when
        list.add(1);
        list.add(2);
        list.add(3);

        // then
        assertThat(list.size()).isEqualTo(3);
        assertThat(list).containsSequence(1, 2, 3);
    }

    @Test
    void 인덱스_지정_삽입() {
        // given
        list.add(1);
        list.add(2);
        list.add(4);
        list.add(5);

        // when
        list.add(2, 3);

        // then
        assertThat(list.size()).isEqualTo(5);
        assertThat(list).containsSequence(1, 2, 3, 4, 5);
    }

    @Test
    void 컬렉션_삽입() {
        // given
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        var other = new LinkedList<Integer>();
        other.add(6);
        other.add(7);
        other.add(8);
        other.add(9);
        other.add(10);

        // when
        list.addAll(other);

        // then
        assertThat(list.size()).isEqualTo(10);
        assertThat(list).containsSequence(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    void 컬렉션_인덱스_지정_삽입() {
        // given
        list.add(1);
        list.add(2);
        list.add(10);

        var other = new LinkedList<Integer>();
        other.add(3);
        other.add(4);
        other.add(5);
        other.add(6);
        other.add(7);
        other.add(8);
        other.add(9);

        // when
        list.addAll(2, other);

        // then
        assertThat(list.size()).isEqualTo(10);
        assertThat(list).containsSequence(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    void 맨_처음_삽입() {
        // given
        list.add(2);
        list.add(3);
        list.add(4);

        // when
        list.addFirst(1);

        // then
        assertThat(list.size()).isEqualTo(4);
        assertThat(list).containsSequence(1, 2, 3, 4);
    }

    @Test
    void 맨_마지막_삽입() {
        // given
        list.add(1);
        list.add(2);
        list.add(3);

        // when
        list.addLast(4);

        // then
        assertThat(list.size()).isEqualTo(4);
        assertThat(list).containsSequence(1, 2, 3, 4);
    }

    @Test
    void 인덱스_기반_삭제() {
        // given
        Integer removed = 99999;

        list.add(1);
        list.add(2);
        list.add(removed);
        list.add(3);
        list.add(4);
        list.add(5);

        // when
        list.remove(2);

        // then
        assertThat(list.size()).isEqualTo(5);
        assertThat(list).containsSequence(1, 2, 3, 4, 5);
        assertThat(list.contains(removed)).isFalse();
    }

    @Test
    void 객체_기반_삭제() {
        // given
        Integer removed = 99999;

        list.add(1);
        list.add(2);
        list.add(removed);
        list.add(3);
        list.add(4);
        list.add(5);

        // when
        list.remove(removed);

        // then
        assertThat(list.size()).isEqualTo(5);
        assertThat(list).containsExactly(1, 2, 3, 4, 5);
        assertThat(list.contains(removed)).isFalse();
    }

    @Test
    void 맨_처음_삭제() {
        // given
        Integer removed = 99999;

        list.add(removed);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        // when
        list.removeFirst();

        // then
        assertThat(list.size()).isEqualTo(5);
        assertThat(list).containsExactly(1, 2, 3, 4, 5);
        assertThat(list.contains(removed)).isFalse();
    }

    @Test
    void 맨_마지막_삭제() {
        // given
        Integer removed = 99999;

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(removed);

        // when
        list.remove(removed);

        // then
        assertThat(list.size()).isEqualTo(5);
        assertThat(list).containsExactly(1, 2, 3, 4, 5);
        assertThat(list.contains(removed)).isFalse();
    }

    @Test
    void 컬렉션_기반_삭제_removeAll() {
        // given
        var removedAll = List.of(3, 4, 5, 6, 7);

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(10);

        // when
        list.removeAll(removedAll);

        // then
        assertThat(list.size()).isEqualTo(5);
        assertThat(list).containsExactly(1, 2, 8, 9, 10);
        assertThat(list.containsAll(removedAll)).isFalse();
    }

    @Test
    void 컬렉션_기반_삭제_retainAll() {
        // given
        var retainedAll = List.of(3, 4, 5, 6, 7);

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(10);

        // when
        list.retainAll(retainedAll);

        // then
        assertThat(list.size()).isEqualTo(5);
        assertThat(list).containsExactly(retainedAll.toArray(new Integer[0]));
        assertThat(list.containsAll(retainedAll)).isTrue();
    }

    @Test
    void 전체_삭제_clear() {
        // given
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        // when
        list.clear();

        // then
        assertThat(list.size()).isZero();
        assertThat(list.containsAll(List.of(1, 2, 3, 4, 5))).isFalse();
    }

    @Test
    void 복제_clone() {
        // given
        int changedValue = 99999;

        // when
        var clone = ((SimpleArrayList<Integer>) list).clone();
        clone.set(0, changedValue);

        assertThat(clone).isNotSameAs(list);
        assertThat(clone).containsExactly(changedValue, 2, 3, 4, 5);
        assertThat(list).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void clone은_얕은_복사() {
        // given, Sushi 클래스만 Cloneable 명시, clone() 오버라이딩함
        var chickenGOAT = new SimpleArrayList<Chicken>();
        Chicken BBQ황금올리브 = new Chicken(25_000);
        Chicken 교촌레드콤보 = new Chicken(20_000);
        Chicken BHC뿌링클 = new Chicken(24_000);
        chickenGOAT.add(BBQ황금올리브);
        chickenGOAT.add(교촌레드콤보);
        chickenGOAT.add(BHC뿌링클);

        // when
        var chickenGOATCopy = chickenGOAT.clone();

        Chicken chickenGOATCopyFirst = chickenGOATCopy.getFirst();
        chickenGOATCopyFirst.setPrice(0);

        // then
        assertThat(chickenGOATCopy.getFirst()).isSameAs(BBQ황금올리브);
        assertThat(chickenGOAT.getFirst()).isSameAs(chickenGOATCopy.getFirst());
    }

    @Test
    void 배열_변환_toArray() {
        // given
        var myArrayList = new SimpleArrayList<String>();

        myArrayList.add("a");
        myArrayList.add("b");
        myArrayList.add("c");
        myArrayList.add("d");
        myArrayList.add("e");

        // when
        var toArray = myArrayList.toArray();

        // then
        assertThat(toArray.length).isEqualTo(5);
        assertThat(toArray).containsExactly("a", "b", "c", "d", "e");
    }

    @Test
    void toArray_매개변수_배열이_ArrayList_size_보다_큰_경우() {
        // given
        var strings = new String[10];
        var myArrayList = new SimpleArrayList<String>();

        myArrayList.add("a");
        myArrayList.add("b");
        myArrayList.add("c");
        myArrayList.add("d");
        myArrayList.add("e");

        // when
        String[] toArray = myArrayList.toArray(strings);

        // then
        assertThat(toArray).isEqualTo(strings);
        assertThat(toArray.length).isEqualTo(10);
        assertThat(toArray).containsOnly("a", "b", "c", "d", "e", null);
    }

    @Test
    void toArray_매개변수_배열이_ArrayList_size_보다_작은_경우() {
        // given (배열이 ArrayList의 size 보다 작은 경우)
        var strings = new String[1];
        var myArrayList = new SimpleArrayList<String>();

        myArrayList.add("a");
        myArrayList.add("b");
        myArrayList.add("c");
        myArrayList.add("d");
        myArrayList.add("e");

        // when
        String[] toArray = myArrayList.toArray(strings);

        // then
        assertThat(toArray).isNotEqualTo(strings);
        assertThat(toArray.length).isEqualTo(5);
        assertThat(toArray).containsOnly("a", "b", "c", "d", "e");
    }

    static class Chicken {

        private int price;

        public Chicken(int price) {
            this.price = price;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }
    }
}