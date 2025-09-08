package hansanhha.classes;

import hansanhha.classes.built_in.class_.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Class 내장 객체 테스트")
public class BuiltInClass_ClassTests {

    @Test
    void getClassObjectTest() throws ClassNotFoundException {
        Class<String> clazz1 = String.class;

        Class<?> clazz2 = Class.forName("java.lang.String");

        String str = "hello";
        Class<? extends String> clazz3 = str.getClass();

        assertThat(clazz1).isNotNull();
        assertThat(clazz2).isNotNull();
        assertThat(clazz3).isNotNull();
    }

    @Test
    void classInfoTest() {
        // interface
        Class<Unit> unitClass = Unit.class;

        // abstract class
        Class<TerranUnit> abstractUnitClass = TerranUnit.class;

        // normal class
        Class<Marine> marinClass = Marine.class;

        // record
        Class<Mineral> mineralClass = Mineral.class;

        // annotation
        Class<AttackUnit> attackUnitClass = AttackUnit.class;

        // enum
        Class<Race> raceClass = Race.class;

        printClassInfo(unitClass);
        printClassInfo(abstractUnitClass);
        printClassInfo(marinClass);
        printClassInfo(mineralClass);
        printClassInfo(attackUnitClass);
        printClassInfo(raceClass);
    }

    @Test
    void classLoadingCheckTest() throws ClassNotFoundException {
        Class<String> clazz = String.class;
        assertThat(clazz.isInstance("hello")).isTrue();
    }

    @Test
    void methodManipulationTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<Marine> marineClass = Marine.class;

        Method attack = marineClass.getDeclaredMethod("attack");
        Marine marine = new Marine();
        attack.invoke(marine);
    }

    @Test
    void instanceDynamicCreationTest() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<Marine> marineClass = Marine.class;

        Constructor<Marine> constructor = marineClass.getDeclaredConstructor();
        Marine marine = constructor.newInstance();
        marine.attack();

        assertThat(marine).isNotNull();
    }


    private void printClassInfo(Class<?> clazz) {
        String classId = "Class<" + clazz.getSimpleName() + ">";

        System.out.printf("\n\n============== %s info ====================\n\n", classId);

        System.out.println("-------------------- class type --------------------");
        System.out.println(classId + " super class: " + clazz.getSuperclass());
        System.out.println(classId + " isInterface: " + clazz.isInterface());
        System.out.println(classId + " isAnnotation: " + clazz.isAnnotation());
        System.out.println(classId + " isEnum: " + clazz.isEnum());
        System.out.println(classId + " isRecord: " + clazz.isRecord());
        System.out.println();

        System.out.println("-------------------- class name --------------------");
        System.out.println(classId + " simple name: " + clazz.getSimpleName());
        System.out.println(classId + " name: " + clazz.getName());
        System.out.println(classId + " canonical name: " + clazz.getCanonicalName());
        System.out.println(classId + " typename: " + clazz.getTypeName());
        System.out.println();

        System.out.println("-------------------- package --------------------");
        System.out.println(classId + " package: " + clazz.getPackage());
        System.out.println(classId + " package name: " + clazz.getPackageName());
        System.out.println();

        System.out.println("-------------------- generic --------------------");
        System.out.println(classId + " type parameters: " + Arrays.toString(clazz.getTypeParameters()));
        System.out.println(classId + " generic super class: " + clazz.getGenericSuperclass());
        System.out.println(classId + " generic interfaces: " + Arrays.toString(clazz.getGenericInterfaces()));
        System.out.println();

        System.out.println("-------------------- modifier --------------------");
        System.out.println(classId + " modifier: " + clazz.getModifiers());
        System.out.println();

        System.out.println("-------------------- class loader --------------------");
        System.out.println(classId + " class loader: " + clazz.getClassLoader());
        System.out.println();

        System.out.println("-------------------- enclosing --------------------");
        System.out.println(classId + " enclosing class: " + clazz.getEnclosingClass());
        System.out.println(classId + " enclosing constructor: " + clazz.getEnclosingConstructor());
        System.out.println(classId + " enclosing method: " + clazz.getEnclosingMethod());
        System.out.println();

        System.out.println("-------------------- component type --------------------");
        System.out.println(classId + " component type: " + clazz.getComponentType());
        System.out.println();

        System.out.println("-------------------- enum constants --------------------");
        System.out.println(classId + " enum constants: " + Arrays.toString(clazz.getEnumConstants()));
        System.out.println();

        System.out.println("-------------------- annotation --------------------");
        System.out.println(classId + " annotations: " + Arrays.toString(clazz.getAnnotations()));
        System.out.println(classId + " declared annotations: " + Arrays.toString(clazz.getDeclaredAnnotations()));
        System.out.println();

        System.out.println("-------------------- methods --------------------");
        System.out.println(classId + " methods: " + Arrays.toString(clazz.getMethods()));
        System.out.println();

        System.out.println("-------------------- fields --------------------");
        System.out.println(classId + " fields: " + Arrays.toString(clazz.getFields()));
        System.out.println(classId + " declared fields: " + Arrays.toString(clazz.getDeclaredFields()));
        System.out.println();
    }
}
