package org.apache.fineract.infrastructure.security.domain;

import org.apache.fineract.infrastructure.security.exception.NoAuthorizationException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;

public class PlatformJwt extends Jwt {

    public PlatformJwt(Jwt jwt) {
        super(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getHeaders(), jwt.getClaims());
    }

    public String getUserName() {
        return this.getClaimAsString("sub");
    }

    private boolean hasNotAnyPermission(final List<String> permissions) {
        return !hasAnyPermission(permissions);
    }

    public boolean hasAnyPermission(String... permissions) {
        return hasAnyPermission(Arrays.asList(permissions));
    }

    public boolean hasAnyPermission(final List<String> permissions) {
        boolean hasAtLeastOneOf = false;

        for (final String permissionCode : permissions) {
            if (hasPermissionTo(permissionCode)) {
                hasAtLeastOneOf = true;
                break;
            }
        }

        return hasAtLeastOneOf;
    }

    public void validateHasPermissionTo(final String function) {
        if (hasNotPermissionTo(function)) {
            final String authorizationMessage = "User has no authority to: " + function;
            throw new NoAuthorizationException(authorizationMessage);
        }
    }

    public void validateHasReadPermission(final String resourceType) {
        validateHasPermission("READ", resourceType);
    }

    private void validateHasPermission(final String prefix, final String resourceType) {
        final String authorizationMessage = "User has no authority to " + prefix + " " + resourceType.toLowerCase() + "s";
        final String matchPermission = prefix + "_" + resourceType.toUpperCase();

        if (!hasNotPermissionForAnyOf("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ", matchPermission)) {
            return;
        }

        throw new NoAuthorizationException(authorizationMessage);
    }

    public boolean hasNotPermissionForAnyOf(final String... permissionCodes) {
        boolean hasNotPermission = true;
        for (final String permissionCode : permissionCodes) {
            final boolean checkPermission = hasPermissionTo(permissionCode);
            if (checkPermission) {
                hasNotPermission = false;
                break;
            }
        }
        return hasNotPermission;
    }

    private boolean hasPermissionTo(final String permissionCode) {
        boolean hasPermission = hasAllFunctionsPermission();
        if (!hasPermission) {
            for (final String claim : this.getClaimAsStringList("scope")) {
                if (permissionCode.equalsIgnoreCase(claim)) {
                    hasPermission = true;
                    break;
                }
            }
        }
        return hasPermission;
    }

    private boolean hasNotPermissionTo(final String permissionCode) {
        return !hasPermissionTo(permissionCode);
    }

    private boolean hasAllFunctionsPermission() {
        boolean match = false;
        for (final String claim : this.getClaimAsStringList("scope")) {
            if (claim.equalsIgnoreCase("ALL_FUNCTIONS")) {
                match = true;
                break;
            }
        }
        return match;
    }

    public boolean isCheckerSuperUser() {
        boolean match = false;
        for (final String claim : this.getClaimAsStringList("scope")) {
            if (claim.equalsIgnoreCase("CHECKER_SUPER_USER")) {
                match = true;
                break;
            }
        }
        return match;
    }

    public void validateHasCheckerPermissionTo(final String function) {
        final String checkerPermissionName = function.toUpperCase() + "_CHECKER";
        if (hasNotPermissionTo("CHECKER_SUPER_USER") && hasNotPermissionTo(checkerPermissionName)) {
            final String authorizationMessage = "User has no authority to be a checker for: " + function;
            throw new NoAuthorizationException(authorizationMessage);
        }
    }
}
