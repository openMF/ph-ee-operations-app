package org.apache.fineract.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;

import java.io.Serializable;
import java.util.Objects;

public class TenantAuthenticationDetails implements Serializable {
    private final String remoteAddress;
    private final String sessionId;
    private final String tenant;

    private final Boolean rememberMe;

    public TenantAuthenticationDetails(HttpServletRequest request) {
        this(request.getRemoteAddr(), extractSessionId(request), extractTenant(request), extractRememberMe(request));
    }

    public TenantAuthenticationDetails(String remoteAddress, String sessionId, String tenant, Boolean rememberMe) {
        this.remoteAddress = remoteAddress;
        this.sessionId = sessionId;
        this.tenant = tenant;
        this.rememberMe = rememberMe;
    }

    private static String extractSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? session.getId() : null;
    }

    private static String extractTenant(HttpServletRequest request) {
        return request.getParameter(TenantAwareHeaderFilter.TENANT_IDENTIFIER_REQUEST_PARAM);
    }

    private static Boolean extractRememberMe(HttpServletRequest request) {
        return Boolean.valueOf(request.getParameter(TenantAwareHeaderFilter.REMEMBER_ME_REQUEST_PARAM));
    }

    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getTenant() { return this.tenant; }

    public Boolean getRememberMe() { return this.rememberMe; }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            TenantAuthenticationDetails that = (TenantAuthenticationDetails)o;
            return Objects.equals(this.remoteAddress, that.remoteAddress) && Objects.equals(this.sessionId, that.sessionId);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.remoteAddress, this.sessionId});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" [");
        sb.append("RemoteIpAddress=").append(this.getRemoteAddress()).append(", ");
        sb.append("SessionId=").append(this.getSessionId()).append(",");
        sb.append("tenant=").append(this.getTenant()).append(",");
        sb.append("rememberMe=").append(this.getRememberMe()).append("]");
        return sb.toString();
    }
}

