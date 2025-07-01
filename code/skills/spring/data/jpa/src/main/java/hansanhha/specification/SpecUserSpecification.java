package hansanhha.specification;

import org.springframework.data.jpa.domain.Specification;

public class SpecUserSpecification {

    public static Specification<SpecUser> hasLastName(String lastName) {
        return (root, query, builder) ->
                builder.equal(root.get("lastName"), lastName);
    }

    public static Specification<SpecUser> hasAgeGreaterThan(int age) {
        return (root, query, builder) ->
                builder.greaterThan(root.get("age"), age);
    }
}
