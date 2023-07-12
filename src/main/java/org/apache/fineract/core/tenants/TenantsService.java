package org.apache.fineract.core.tenants;

import jakarta.annotation.PostConstruct;
import org.apache.fineract.core.service.migrate.TenantDatabaseUpgradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class TenantsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment environment;

    private List<String> tenants;

    @Autowired
    TenantConnections tenantConnections;

    @Autowired
    private TenantDatabaseUpgradeService tenantDatabaseUpgradeService;

    @PostConstruct
    public void setup() {
        String[] activeProfiles = environment.getActiveProfiles();
        this.tenants = Stream.of(activeProfiles)
                .map(profile -> profile.startsWith("tenant-") ? profile.substring("tenant-".length()) : null)
                .filter(Objects::nonNull)
                .toList();
        logger.info("Loaded tenants from configuration: {}", tenants);

        tenantDatabaseUpgradeService.generateTenantsConnections(tenants);

        if (List.of(activeProfiles).contains("migrate")) {
            logger.info("Running in migration mode, migrating tenants");
            tenantDatabaseUpgradeService.migrateTenants();
            logger.info("Migration finished, exiting");
            System.exit(0);
        }

        System.err.println(tenantConnections.getConnections());
    }
}
