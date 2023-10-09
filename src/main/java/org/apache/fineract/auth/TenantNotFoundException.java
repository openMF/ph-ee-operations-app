package org.apache.fineract.auth;

import org.springframework.security.core.AuthenticationException;

public class TenantNotFoundException extends AuthenticationException {

    public TenantNotFoundException(String msg) {
        super(msg);
    }
    public TenantNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
