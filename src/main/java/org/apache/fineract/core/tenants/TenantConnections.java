package org.apache.fineract.core.tenants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties("tenants")
@Data
public class TenantConnections {

    List<TenantConfigProperties> connections;

    @Data
    public static class TenantConfigProperties {
        String name;
        String schemaServer;
        String schemaName;
        String schemaServerPort;
        String schemaUsername;
        String schemaPassword;
        String autoUpdate;
        String poolInitialSize;
        String poolValidationInterval;
        String poolRemoveAbandoned;
        String poolRemoveAbandonedTimeout;
        String poolLogAbandoned;
        String poolAbandonWhenPercentageFull;
        String poolTestOnBorrow;
        String poolMaxActive;
        String poolMinIdle;
        String poolMaxIdle;
        String poolSuspectTimeout;
        String poolTimeBetweenEvictionRunsMillis;
        String poolMinEvictableIdleTimeMillis;
        String deadlockMaxRetries;
        String deadlockMaxRetryInterval;
        String schemaConnectionParameters;
    }
}
