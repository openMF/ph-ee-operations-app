package org.apache.fineract.operations;


import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class BatchSpecs {

    public static <T> Specification<Batch> match(SingularAttribute<Batch, T> attribute, T input) {
        return where((root, query, builder) -> builder.equal(root.get(attribute), input));
    }
}
