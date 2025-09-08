package hansanhha.classes;

public class BasicConcetps {

    /*
        필드 초기화
        0. 클래스 최초 로드 시 static 초기화 블록 호출
        1. 필드 자료형 기본값 할당
        2. 명시적 값 할당
        3. 초기화 블록 호출
        4. 생성자 호출
     */

    static boolean easy = false;

    static {
        easy = true;
    }

    String name = "hello";
    int value = 10;

    {
        name = "initiation block";
        value = 100;
    }

    {
        name = "initiation block 2";
        value = 200;
    }

    /*
        생성자
        - 생성자를 명시하지 않으면 컴파일러가 기본 생성자를 추가한다
        - 접근 제어자를 활용하여 생성자의 접근을 제한할 수 있다 (e.g 싱글톤 패턴)
        - this를 활용하여 다른 생성자를 호출할 수 있다
     */

    public BasicConcetps() {
    }

    public BasicConcetps(String name) {
        this(name, 1);
    }

    public BasicConcetps(int value) {
        this("test", value);
    }

    public BasicConcetps(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String print() {
        return "name: " + name + " value: " + value;
    }


    /*
        this 키워드
        - 필드와 지역 변수를 구분한다
        - 현재 객체의 메서드를 호출한다
        - 현재 객체의 다른 생성자를 호출한다
     */

    public void setName(String name) {
        this.name = name;
        System.out.println(this.print());
    }

    public static void main(String[] args) {
        BasicConcetps b1 = new BasicConcetps();
        BasicConcetps b2 = new BasicConcetps("test", 1);

        System.out.println(b1.print());
        System.out.println(b2.print());

    }

}

// package-private: 접근 제한자를 명시하지 않으면 같은 패키지에서만 접근할 수 있다
class AnotherClass {

}
