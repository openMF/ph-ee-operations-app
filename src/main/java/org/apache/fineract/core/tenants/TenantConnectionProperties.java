package org.apache.fineract.core.tenants;

import lombok.Data;

@Data
public class TenantConnectionProperties {
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
