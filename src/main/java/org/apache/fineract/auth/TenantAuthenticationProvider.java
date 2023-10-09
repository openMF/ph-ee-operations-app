package org.apache.fineract.auth;

import org.apache.fineract.core.tenants.TenantsService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class TenantAuthenticationProvider extends DaoAuthenticationProvider {

    private final TenantsService tenantsService;

    public TenantAuthenticationProvider(TenantsService tenantsService) {
        this.tenantsService = tenantsService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            if (!super.getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug("Failed to authenticate since password does not match stored value");
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        }
        TenantAuthenticationDetails details = (TenantAuthenticationDetails) authentication.getDetails();
        if (tenantsService.getTenantDataSource(details.getTenant()) == null) {
            throw new TenantNotFoundException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.invalidTenant", "Tenant not found"));
        }
    }
}
