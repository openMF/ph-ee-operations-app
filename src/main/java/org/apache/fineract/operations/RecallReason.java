package org.apache.fineract.operations;

import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Optional;

public enum RecallReason {

    DUPL("Bank"),
    TECH("Bank"),
    FRAD("Bank"),
    CUST("Customer"),
    AM09("Customer"),
    AC03("Customer");

    private static final RecallReason[] VALUES = values();

    @NotNull
    private final String recallerType;

    RecallReason(String recallerType) {
        this.recallerType = recallerType;
    }

    public static String getRecallerType(String reasonCode) {
        Optional<RecallReason> result = Arrays.stream(VALUES).filter(r -> r.name().equals(reasonCode)).findFirst();
        if (result.isPresent()) {
            return result.get().recallerType;
        }
        return "Other";
    }
}
