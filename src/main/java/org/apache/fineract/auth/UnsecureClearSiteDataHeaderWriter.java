package org.apache.fineract.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

/**
 * This class is the copy of org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter
 * except one method. The UnsecureRequestMatcher's matches method always returns true. This way this class
 * can clear site data without https.
 */
public final class UnsecureClearSiteDataHeaderWriter implements HeaderWriter {
    private static final String CLEAR_SITE_DATA_HEADER = "Clear-Site-Data";
    private final Log logger = LogFactory.getLog(this.getClass());
    private final RequestMatcher requestMatcher;
    private String headerValue;

    public UnsecureClearSiteDataHeaderWriter(Directive... directives) {
        Assert.notEmpty(directives, "directives cannot be empty or null");
        this.logger.warn("WARNING - UnsecureClearSiteDataHeaderWriter Injected. Logout will only work in http environment!");
        this.requestMatcher = new UnsecureRequestMatcher();
        this.headerValue = this.transformToHeaderValue(directives);
    }

    public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (this.requestMatcher.matches(request) && !response.containsHeader("Clear-Site-Data")) {
            response.setHeader("Clear-Site-Data", this.headerValue);
        }

        this.logger.debug(LogMessage.format("Not injecting Clear-Site-Data header since it did not match the requestMatcher %s", this.requestMatcher));
    }

    private String transformToHeaderValue(Directive... directives) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < directives.length - 1; ++i) {
            sb.append(directives[i].headerValue).append(", ");
        }

        sb.append(directives[directives.length - 1].headerValue);
        return sb.toString();
    }

    public String toString() {
        String var10000 = this.getClass().getName();
        return var10000 + " [headerValue=" + this.headerValue + "]";
    }

    /**
     * This class is intended to operate without https.
     * The matches function instead of returning request.isSecure() it will always return true.
     */
    private static final class UnsecureRequestMatcher implements RequestMatcher {
        private UnsecureRequestMatcher() {
        }

        public boolean matches(HttpServletRequest request) {
            return true;
        }

        public String toString() {
            return "Is not Secure";
        }
    }

    public static enum Directive {
        CACHE("cache"),
        COOKIES("cookies"),
        STORAGE("storage"),
        EXECUTION_CONTEXTS("executionContexts"),
        ALL("*");

        private final String headerValue;

        private Directive(String headerValue) {
            this.headerValue = "\"" + headerValue + "\"";
        }

        public String getHeaderValue() {
            return this.headerValue;
        }
    }
}