package hansanhha.annotations;

public @interface AlmostAttributeAnnotation {

    // 어노테이션에서 선언할 수 있는 타입
    // primitive, String, Class, Enum, Annotation 및 허용 가능한 타입의 배열
    int number();
    String text();
    Class<?> clazz();
    MyEnum enumValue();
    MyAnnotation annotation();
    int[] numbers();

    enum MyEnum{
    }
}
