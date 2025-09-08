package hansanhha.classes.sealed_classes;

public class NarrowingReferenceConversion {

    public static void main(String[] args) {
        Rectangle rectangle = new Rectangle();
        Triangle triangle = new Triangle();
        UtahTeapot utahTeapot = new UtahTeapot();

        // Triangle 클래스는 Rectangle 인터페이스를 구현했기에 런타임의 타입 변환이 정상적으로 수행된다
        Polygon p1 = (Polygon) rectangle;

        // Triangle 클래스는 Rectangle 인터페이스를 구현하지 않지만 컴파일러가 오류를 잡지 못한다
        // 따라서 런타임에 캐스팅을 시도하다 ClassCastException 예외가 발생한다
        Polygon p2= (Polygon) triangle;

        // UtahTeapot 클래스는 final 키워드를 명시하여 Polygon의 자식이 될 수 없음을 명시했기 때문에
        // 두 타입은 서로소 타입(disjoint type)임을 컴파일러가 알 수 있으므로 컴파일 시 타입 변환 오류를 발생시켜 런타임 예외를 방지한다
//        Polygon p3 = (Polygon) utahTeapot;

        Tank tank = new Tank();
        Terran t = (Terran) tank;

        // UtahTeapot 클래스는 final 키워드를 명시하여 Terran의 자식이 될 수 없음을 명시했기 때문에
        // 컴파일러는 두 타입이 서로소 타입인 걸 컴파일 시점에 인지하고 타입 변환 오류를 발생시킨다
//        Terran t2 = (Terran) utahTeapot;
    }

}

interface Polygon { }

class Rectangle implements Polygon { }

class Triangle { }

final class UtahTeapot { }

sealed interface Terran permits Tank {}

non-sealed class Tank implements Terran {}
