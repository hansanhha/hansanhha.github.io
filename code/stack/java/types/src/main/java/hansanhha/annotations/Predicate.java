package hansanhha.annotations;

public @ interface Predicate {

    /*
        compile-time error if the declaration of an annotation interface T
        contains an element of type T, either directly or indirectly.
     */
    // Predicate value();

    String[] status()     default {"requested", "approved", "rejected"};

}
