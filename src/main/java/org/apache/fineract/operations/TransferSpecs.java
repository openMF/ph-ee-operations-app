package org.apache.fineract.operations;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.metamodel.SingularAttribute;

import java.math.BigDecimal;
import java.util.Date;

import static org.springframework.data.jpa.domain.Specification.where;

public class TransferSpecs {

    private static final String DATE_TRUNC = "DATE_TRUNC";
    private static final String SEC = "second";
    private static final String DAY = "day";

    public static Specification<Transfer> greaterThanOrEqualTo(SingularAttribute<Transfer, BigDecimal> attribute, BigDecimal input) {
        return where((root, query, builder) -> builder.greaterThanOrEqualTo(root.get(attribute), input));
    }

    public static Specification<Transfer> lessThanOrEqualTo(SingularAttribute<Transfer, BigDecimal> attribute, BigDecimal input) {
        return where((root, query, builder) -> builder.lessThanOrEqualTo(root.get(attribute), input));
    }

    public static Specification<Transfer> between(SingularAttribute<Transfer, Date> attribute, Date from, Date to) {
        return where((root, query, builder) -> builder.and(
                builder.greaterThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(SEC), root.get(attribute)), from),
                builder.lessThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(SEC), root.get(attribute)), to)
        ));
    }

    public static Specification<Transfer> later(SingularAttribute<Transfer, Date> attribute, Date from) {
        return where((root, query, builder) -> builder.greaterThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(SEC), root.get(attribute)), from));
    }

    public static Specification<Transfer> earlier(SingularAttribute<Transfer, Date> attribute, Date to) {
        return where((root, query, builder) -> builder.lessThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(SEC), root.get(attribute)), to));
    }

    public static Specification<Transfer> betweenDay(SingularAttribute<Transfer, Date> attribute, Date from, Date to) {
        return where((root, query, builder) -> builder.and(
                builder.greaterThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(DAY), root.get(attribute)), from),
                builder.lessThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(DAY), root.get(attribute)), to)
        ));
    }

    public static Specification<Transfer> laterDay(SingularAttribute<Transfer, Date> attribute, Date from) {
        return where((root, query, builder) -> builder.greaterThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(DAY), root.get(attribute)), from));
    }

    public static Specification<Transfer> earlierDay(SingularAttribute<Transfer, Date> attribute, Date to) {
        return where((root, query, builder) -> builder.lessThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(DAY), root.get(attribute)), to));
    }

    public static <T> Specification<Transfer> match(SingularAttribute<Transfer, T> attribute, T input) {
        return where((root, query, builder) -> builder.equal(root.get(attribute), input));
    }

    public static <T> Specification<Transfer> like(SingularAttribute<Transfer, String> attribute, String input) {
        return where((root, query, builder) -> builder.like(root.get(attribute), input));
    }

    public static <T> Specification<Transfer> multiMatch(SingularAttribute<Transfer, T> attribute1, SingularAttribute<Transfer, T> attribute2, T input) {
        return where((root, query, builder) -> builder.or(
                builder.equal(root.get(attribute1), input),
                builder.equal(root.get(attribute2), input)
        ));
    }
}
