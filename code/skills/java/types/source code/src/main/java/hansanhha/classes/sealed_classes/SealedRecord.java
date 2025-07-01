package hansanhha.classes.sealed_classes;

public record SealedRecord() implements Something {

    @Override
    public void doSomething() {
        System.out.println("sealed record");
    }
}
