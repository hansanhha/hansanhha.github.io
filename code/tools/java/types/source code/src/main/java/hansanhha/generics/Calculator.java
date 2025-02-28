package hansanhha.generics;

public interface Calculator<@Operator T> {
    T add(T t1, T t2);
}
