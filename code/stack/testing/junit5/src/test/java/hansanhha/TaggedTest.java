package hansanhha;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class TaggedTest {

    @Test
    @Tag("group a")
    void a1() {
        System.out.println("그룹 a 테스트 실행");
    }

    @Test
    @Tag("group a")
    void a2() {
        System.out.println("그룹 a 테스트 실행");
    }

    @Test
    @Tag("group b")
    void b1() {
        System.out.println("그룹 b 테스트 실행");
    }

    @Test
    @Tag("group b")
    void b2() {
        System.out.println("그룹 b 테스트 실행");
    }
}
