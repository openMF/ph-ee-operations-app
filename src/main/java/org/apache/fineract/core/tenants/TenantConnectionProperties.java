package org.apache.fineract.core.tenants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties
public class TenantConnectionProperties {
    String name;
    String schemaServer;
    String schemaName;
    String schemaServerPort;
    String schemaUsername;
    String schemaPassword;
    String driverClass;
    String jdbcProtocol;
    String jdbcSubProtocol;
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
