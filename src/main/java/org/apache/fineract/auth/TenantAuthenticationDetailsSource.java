package org.apache.fineract.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

public class TenantAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, TenantAuthenticationDetails> {
    @Override
    public TenantAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new TenantAuthenticationDetails(context);
    }
}
