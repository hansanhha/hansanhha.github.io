package hansanhha.classes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NestedClassTest {

    static String outerClassStaticField = "outer class static field";
    String outerClassInstanceField = "outer class instance field";

    static class StaticNested {
        void display() {
            System.out.println("static nested class");
            System.out.println(outerClassStaticField + " can access");

            // static이 아닌 멤버에는 접근 불가
//            System.out.println(outerClassInstanceField + " can't access");
        }
    }

    class Inner {
        void display() {
            System.out.println("inner nested class");
            System.out.println(outerClassInstanceField + " can access");
            System.out.println(outerClassInstanceField + " can access");
        }
    }

    @Test
    @DisplayName("정적 중첩 클래스와 비정적 중첩 클래스 인스턴스화")
    void test1() {
        StaticNested staticNested = new StaticNested();
        staticNested.display();

        NestedClassTest outerClass = new NestedClassTest();
        Inner inner = outerClass.new Inner();

        staticNested.display();
        inner.display();

        System.out.println(outerClass.getClass().getTypeName());
        System.out.println(staticNested.getClass().getTypeName());
        System.out.println(inner.getClass().getTypeName());
    }

}
