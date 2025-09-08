package hansanhha.classes.built_in.object;

import java.util.Objects;

// equals/hashCode, clone, toString, finalize example
public class Person implements Cloneable {

    String name;
    String job;
    int age;

    public Person(String name, String job, int age) {
        this.name = name;
        this.job = job;
        this.age = age;
    }

    // equals overriding for equality comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Person person = (Person) obj; // down casting
        return Objects.equals(name, person.name)
                && Objects.equals(job, person.job)
                && age == person.age;
    }

    // hashCode overriding according to equals overriding
    @Override
    public int hashCode() {
        return Objects.hash(name, job, age);
    }

    @Override
    public String toString() {
        String objectToStringValue = super.toString();
        return
                """
                object toString(): %s
                overriding toString()
                - name: %s
                - job: %s
                - age: %s
                """.formatted(objectToStringValue, name, job, age);
    }

    // Object.clone method basically performs a shallow copy
    @Override
    public Person clone() {
        try {
            return (Person) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }
}
