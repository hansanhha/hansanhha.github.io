package hansanhha;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("MyLinkedList 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SimpleLinkedListTest {

    List<Integer> list;

    @BeforeEach
    void init() {
        list = new SimpleLinkedList<>();
    }

    @Nested
    class LinkedList_기능_테스트 {

        @Test
        void 요소_삽입() {
            // given
            int val = 1;

            // when
            list.add(val);

            // then
            assertThat(list.size()).isEqualTo(1);
        }

        @Test
        void 인덱스_요소_삽입() {
            // given
            list.add(1);
            list.add(2);
            list.add(4);
            list.add(5);

            // when
            list.add(2, 3);

            // then
            assertThat(list.size()).isEqualTo(5);
        }

        @Test
        void 컬렉션_삽입() {
            // given
            var collection = List.of(1,2,3,4,5);

            // when
            list.addAll(collection);

            // then
            assertThat(list.size()).isEqualTo(collection.size());
        }

        @Test
        void 인덱스_컬렉션_삽입() {
            // given
            list.add(1);
            list.add(2);
            list.add(3);
            list.add(9);
            list.add(10);

            var collection = List.of(4,5,6,7,8);

            // when
            list.addAll(collection);

            // then
            assertThat(list.size()).isEqualTo(10);
        }

        @Test
        void 맨_처음_삽입() {
            // given
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            list.addFirst(1);

            // then
            assertThat(list.size()).isEqualTo(5);
        }

        @Test
        void 맨_마지막_삽입() {
            // given
            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);

            // when
            list.addLast(5);

            // then
            assertThat(list.size()).isEqualTo(5);
        }

        @Test
        void 요소_삭제() {
            // given
            Integer removed = 1;

            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            list.remove(removed);

            // then
            assertThat(list.size()).isEqualTo(4);
            assertThat(list.contains(removed)).isFalse();
        }

        @Test
        void 인덱스_삭제() {
            // given
            int removed = 5;
            int removeIndex = 5;

            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(removed);

            // when
            list.remove(removeIndex);

            // then
            assertThat(list.size()).isEqualTo(4);
            assertThat(list.contains(removed)).isFalse();
        }

        @Test
        void 맨_처음_삭제() {
            // given
            int removed = 1;

            list.add(removed);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            list.removeFirst();

            // then
            assertThat(list.size()).isEqualTo(4);
            assertThat(list.contains(removed)).isFalse();
        }

        @Test
        void 맨_마지막_삭제() {
            // given
            int removed = 5;

            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(removed);

            // when
            list.removeLast();

            // then
            assertThat(list.size()).isEqualTo(4);
            assertThat(list.contains(removed)).isFalse();
        }

        @Test
        void 요소_조회() {
            // given
            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            var i = list.get(2);

            // then
            assertThat(i).isEqualTo(3);
        }

        @Test
        void 맨_처음_요소_조회() {
            // given
            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            var first = list.getFirst();

            // then
            assertThat(first).isEqualTo(1);
        }

        @Test
        void 맨_마지막_요소_조회() {
            // given
            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            var last = list.getLast();

            // then
            assertThat(last).isEqualTo(5);
        }

        @Test
        void 인덱스_조회() {
            // given
            Integer first = 1;

            list.add(first);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            var index = list.indexOf(first);

            // then
            assertThat(index).isEqualTo(0);
            assertThat(list.get(index)).isEqualTo(first);
        }

        @Test
        void last_인덱스_조회() {
            // given
            Integer middle = 3;

            list.add(1);
            list.add(2);
            list.add(middle);
            list.add(4);
            list.add(5);

            // when
            var index = list.indexOf(middle);

            // then
            assertThat(index).isEqualTo(2);
            assertThat(list.get(index)).isEqualTo(middle);
        }

        @Test
        void 포함_여부_확인() {
            // given
            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            boolean contains = list.contains(1);
            boolean contains2 = list.contains(100);

            // then
            assertThat(contains).isTrue();
            assertThat(contains2).isFalse();
        }

        @Test
        void 컬렉션_포함_여부_확인() {
            // given
            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);
            list.add(5);

            // when
            boolean contains = list.containsAll(List.of(1,2,3));
            boolean contains2 = list.containsAll(List.of(100,200));

            // then
            assertThat(contains).isTrue();
            assertThat(contains2).isFalse();
        }
    }

    @Nested
    class Deque_기능_테스트 {

    }

    @Nested
    class Stack_기능_테스트 {

    }

    // iterator 구현 필요
    @Test
    void removeAll_은_AbstractCollection_메서드_사용() {
//        // given
//        var removed = List.of(1, 2, 3);
//
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        list.add(4);
//        list.add(5);
//
//        // when
//        list.removeAll(removed);
//
//        // then
//        assertThat(list.size()).isEqualTo(2);
    }

    // iterator 구현 필요
    @Test
    void removeIf_는_Collection_메서드_사용() {
//        // given
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        list.add(4);
//        list.add(5);
//
//        // when
//        list.removeIf(i -> i < 3);
//
//        // then
//        assertThat(list.size()).isEqualTo(3);
    }

}