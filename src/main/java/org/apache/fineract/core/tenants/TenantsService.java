package org.apache.fineract.core.tenants;

import jakarta.annotation.PostConstruct;
import org.apache.fineract.core.service.DataSourcePerTenantService;
import org.apache.fineract.core.service.migrate.TenantDatabaseUpgradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TenantsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment environment;

    @Autowired
    TenantConnections tenantConnectionList;

    @Autowired
    DataSourcePerTenantService dataSourcePerTenantService;

    @Autowired
    private TenantDatabaseUpgradeService tenantDatabaseUpgradeService;

    private Map<String, TenantConnectionProperties> tenantConnectionProperties;

    private Map<String, DataSource> tenantDataSources = new HashMap<>();


    @PostConstruct
    public void setup() {
        String[] activeProfiles = environment.getActiveProfiles();
        List<String> tenants = Stream.of(activeProfiles)
                .map(profile -> profile.startsWith("tenant-") ? profile.substring("tenant-".length()) : null)
                .filter(Objects::nonNull)
                .toList();
        logger.info("Loaded tenants from configuration: {}", tenants);

        this.tenantConnectionProperties = tenantConnectionList.getConnections().stream()
                .collect(Collectors.toMap(TenantConnectionProperties::getName, it -> it));
        logger.info("loaded {} tenant config properties: {}", tenantConnectionProperties.size(), tenantConnectionProperties);

        tenantConnectionProperties.forEach((name, properties) -> {
            logger.info("Creating datasource for tenant {}", name);
            tenantDataSources.put(name, dataSourcePerTenantService.createNewDataSourceFor(properties));
        });

        if (List.of(activeProfiles).contains("migrate")) {
            logger.info("Running in migration mode, migrating tenants");
            tenantDatabaseUpgradeService.migrateTenants(tenantDataSources, tenantConnectionProperties);
            logger.info("Migration finished, exiting");
            System.exit(0);
        }
    }


    public Connection getTenantConnection(String tenantIdentifier) throws SQLException {
        return tenantDataSources.get(tenantIdentifier).getConnection();
    }

    // for initializing JPA repositories
    public Connection getAnyConnection() throws SQLException {
        return tenantDataSources.values().iterator().next().getConnection();
    }
}
