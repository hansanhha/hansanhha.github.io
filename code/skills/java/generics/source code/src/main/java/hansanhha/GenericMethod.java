package hansanhha;

public class GenericMethod<T> {

    T t;

    public GenericMethod(T t) {
        this.t = t;
    }

    public T identity() {
        return t;
    }

    // 클래스의 타입 매개변수 T와 별개로 동작한다
    public static <T> T identity(T value) {
        return value;
    }

    public static void main(String[] args) {

        // 제네릭 메서드 사용
        String genericMethod = identity("Hello");
        Integer genericMethod2 = identity(100);

        System.out.println(genericMethod); // Hello
        System.out.println(genericMethod2); // 100


        // 클래스 메서드 사용
        GenericMethod<String> genericClass = new GenericMethod<>("Hello");
        GenericMethod<Integer> genericClass2 = new GenericMethod<>(100);

        System.out.println(genericClass); // Hello
        System.out.println(genericClass2); // 100
    }

}
