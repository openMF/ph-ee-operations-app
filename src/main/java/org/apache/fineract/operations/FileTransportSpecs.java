package org.apache.fineract.operations;

import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Date;

import static org.springframework.data.jpa.domain.Specification.where;

public class FileTransportSpecs {

    private static final String DATE_TRUNC = "DATE_TRUNC";
    private static final String SEC = "second";
    private static final String DAY = "day";

    public static Specification<FileTransport> greaterThanOrEqualTo(SingularAttribute<FileTransport, BigDecimal> attribute, BigDecimal input) {
        return where((root, query, builder) -> builder.greaterThanOrEqualTo(root.get(attribute), input));
    }

    public static Specification<FileTransport> lessThanOrEqualTo(SingularAttribute<FileTransport, BigDecimal> attribute, BigDecimal input) {
        return where((root, query, builder) -> builder.lessThanOrEqualTo(root.get(attribute), input));
    }

    public static Specification<FileTransport> between(SingularAttribute<FileTransport, Date> attribute, Date from, Date to) {
        return where((root, query, builder) -> builder.and(
                builder.greaterThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(SEC), root.get(attribute)), from),
                builder.lessThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(SEC), root.get(attribute)), to)
        ));
    }

    public static Specification<FileTransport> later(SingularAttribute<FileTransport, Date> attribute, Date from) {
        return where((root, query, builder) -> builder.greaterThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(SEC), root.get(attribute)), from));
    }

    public static Specification<FileTransport> earlier(SingularAttribute<FileTransport, Date> attribute, Date to) {
        return where((root, query, builder) -> builder.lessThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(SEC), root.get(attribute)), to));
    }

    public static Specification<FileTransport> betweenDay(SingularAttribute<FileTransport, Date> attribute, Date from, Date to) {
        return where((root, query, builder) -> builder.and(
                builder.greaterThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(DAY), root.get(attribute)), from),
                builder.lessThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(DAY), root.get(attribute)), to)
        ));
    }

    public static Specification<FileTransport> laterDay(SingularAttribute<FileTransport, Date> attribute, Date from) {
        return where((root, query, builder) -> builder.greaterThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(DAY), root.get(attribute)), from));
    }

    public static Specification<FileTransport> earlierDay(SingularAttribute<FileTransport, Date> attribute, Date to) {
        return where((root, query, builder) -> builder.lessThanOrEqualTo(builder.function(DATE_TRUNC, Date.class, builder.literal(DAY), root.get(attribute)), to));
    }

    public static <T> Specification<FileTransport> match(SingularAttribute<FileTransport, T> attribute, T input) {
        return where((root, query, builder) -> builder.equal(root.get(attribute), input));
    }

    public static <T> Specification<FileTransport> like(SingularAttribute<FileTransport, String> attribute, String input) {
        return where((root, query, builder) -> builder.like(root.get(attribute), input));
    }

    public static <T> Specification<FileTransport> multiMatch(SingularAttribute<FileTransport, T> attribute1, SingularAttribute<FileTransport, T> attribute2, T input) {
        return where((root, query, builder) -> builder.or(
                builder.equal(root.get(attribute1), input),
                builder.equal(root.get(attribute2), input)
        ));
    }
}
