package hansanhha.classes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hansanhha.classes.built_in.object.*;
import org.junit.jupiter.api.Test;

public class BuiltInClass_ObjectTests {

    /*
    ---------------------------------------
               equals, hashCode
    ---------------------------------------
     */

    @Test
    void objectIdentityTest() {
        Object o1 = new Object();
        Object o2 = new Object();

        Object o1_other = o1;

        // false: identity of two different objects
        assertThat(o1 == o2).isFalse();

        // true: identity of two variable that has same reference
        assertThat(o1 == o1_other).isTrue();
    }

    @Test
    void objectEqualityTest() {
        Person p1 = new Person("hansanhha", "developer", 10);
        Person p2 = new Person("hansanhha", "developer", 10);

        // false: identity of two different objects
        assertThat(p1 == p2).isFalse();

        // true: equality of two different objects that has same state
        assertThat(p1.equals(p2)).isTrue();
    }

    /*
    ---------------------------------------
               toString
    ---------------------------------------
     */

    @Test
    void toStringMethodTest() {
        Person person = new Person("hansanhha", "developer", 10);

        System.out.println(person);
    }

    /*
    ---------------------------------------
               getClass
    ---------------------------------------
     */

    @Test
    void getClassMethodTest() {
        Person p1 = new Person("hansanhha", "developer", 10);

        assertThat(p1.getClass().getName()).isEqualTo("hansanhha.classes.built_in.object.Person");
        assertThat(p1.getClass().getSimpleName()).isEqualTo("Person");
    }

    /*
    ---------------------------------------
               clone
    ---------------------------------------
     */

    @Test
    void cloneExceptionTest() {
        NotImplCloneable notImplCloneable = new NotImplCloneable();

        assertThatThrownBy(notImplCloneable::doClone, "Cloneable 인터페이스를 구현하지 않으면 런타임에 CloneNotSupportedException checked 예외가 발생한다").isExactlyInstanceOf(CloneNotSupportedException.class);
    }

    @Test
    void cloneMethodTest() {
        Person person = new Person("hansanhha", "developer", 10);
        Person clone = person.clone();

        // Person 클래스는 toString을 오버라이딩하여 동등성을 만족한다
        assertThat(person).isEqualTo(clone);
    }

    /*
    ---------------------------------------
        clone: shallow copy, deep copy
    ---------------------------------------
     */

    @Test
    void shallowCopyReferenceTypeTest() {
        ShallowAddress seoul = new ShallowAddress("seoul");
        ShallowOrder order = new ShallowOrder("12345", seoul);
        ShallowOrder cloneOrder = order.doClone();

        // Order 클래스는 Object.toString을 호출하는데, 이 메서드는 객체의 참조 주소값을 사용한다
        // 복제된 클래스는 기존 객체와 다른 메모리 주소를 가지기 때문에 동일성을 만족하지 못한다
        assertThat(order).isNotEqualTo(cloneOrder);

        // clone 메서드는 shallow copy로 동작하여 참조 타입의 필드에 대해 메모리 주소만 복제한다
        // 원본 객체와 복제 객체 모두 객체를 공유하기 때문에 해당 참조의 상태를 변경하면 다른 객체에 모두 영향을 준다
        cloneOrder.getAddress().setCity("busan");
        assertThat(cloneOrder.getAddress().getCity()).isEqualTo(order.getAddress().getCity());
    }

    @Test
    void deepCopyReferenceTypeTest() {
        DeepAddress seoul = new DeepAddress("seoul");
        DeepOrder order = new DeepOrder("12345", seoul);
        DeepOrder cloneOrder = order.doClone();

        // deep copy는 참조 타입 필드까지 새로운 객체를 생성하기 때문에 원본 객체와 복제 객체가 동일한 객체를 공유하지 않는다
        cloneOrder.getDeepAddress().setCity("busan");
        assertThat(cloneOrder.getDeepAddress().getCity()).isNotEqualTo(order.getDeepAddress().getCity());
    }

    /*
    ---------------------------------------
               wait, notify
    ---------------------------------------
     */
    @Test
    void waitAndNotifyMethodTest() throws InterruptedException {
        SharedResource resource = new SharedResource();

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(10);
                String data = resource.consume();
                System.out.println(data);
            } catch (InterruptedException ignored) {
            }
        });

        Thread producer = new Thread(() -> {
            try {
                resource.produce("hello");
            } catch (InterruptedException ignored) {
            }
        });

        consumer.start();
        producer.start();

        consumer.join();
        producer.join();
    }

}
