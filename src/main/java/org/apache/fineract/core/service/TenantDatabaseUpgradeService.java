/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.core.service;

import com.googlecode.flyway.core.Flyway;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.fineract.organisation.tenant.TenantServerConnection;
import org.apache.fineract.organisation.tenant.TenantServerConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.fineract.config.ResourceServerConfig.IDENTITY_PROVIDER_RESOURCE_ID;

@Service
public class TenantDatabaseUpgradeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TenantServerConnectionRepository repository;

    @Autowired
    private DataSourcePerTenantService dataSourcePerTenantService;

    @Value("${fineract.datasource.core.host}")
    private String hostname;

    @Value("${fineract.datasource.core.port}")
    private int port;

    @Value("${fineract.datasource.core.username}")
    private String username;

    @Value("${fineract.datasource.core.password}")
    private String password;

    @Value("${fineract.datasource.common.protocol}")
    private String jdbcProtocol;

    @Value("${fineract.datasource.common.subprotocol}")
    private String jdbcSubprotocol;

    @Value("${fineract.datasource.common.driverclass_name}")
    private String driverClass;

    @Value("${fineract.datasource.core.schema}")
    private String tenantsSchema;

    @Value("${token.user.access-validity-seconds}")
    private String userTokenAccessValiditySeconds;

    @Value("${token.user.refresh-validity-seconds}")
    private String userTokenRefreshValiditySeconds;

    @Value("${token.client.access-validity-seconds}")
    private String clientAccessTokenValidity;

    @Value("${token.client.channel.secret}")
    private String channelClientSecret;

    @Value("#{'${tenants}'.split(',')}")
    private List<String> tenants;

    @Value("${spring.liquibase.contexts}")
    private String liquibaseContexts;


    @PostConstruct
    public void setupEnvironment() throws Exception {
        migrateTenantsSchema();
        insertTenants();
//        flywayTenants();
    }

    private void migrateTenantsSchema() throws LiquibaseException, ClassNotFoundException, SQLException {
        Class.forName(driverClass);
        String jdbcUrl = String.format("%s:%s://%s:%s/%s", jdbcProtocol, jdbcSubprotocol, hostname, port, tenantsSchema);
        logger.info("connecting to JDBC URL {}", jdbcUrl);
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        String changeLogFile = "/db/changelog/db.changelog-master.xml";
//        String changeLogFile = "/db/changelog/tenant-store/initial-switch-changelog-tenant-store.xml";
        logger.info("starting Liquibase migrations using {} on database {}", changeLogFile, database);
        Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
//        liquibase.setChangeLogParameter("fineract.tenant.host", hostname);
//        liquibase.setChangeLogParameter("fineract.tenant.port", port);
//        liquibase.setChangeLogParameter("fineract.tenant.username", username);
//        liquibase.setChangeLogParameter("fineract.tenant.password", password);
//        liquibase.setChangeLogParameter("fineract.tenant.parameters", "");
//        liquibase.setChangeLogParameter("fineract.tenant.schema-name", tenant);
        liquibase.update(liquibaseContexts);
    }

    private void insertTenants() {
        for (String tenant : tenants) {
            logger.info("validating tenant '{}'", tenant);
            TenantServerConnection existingTenant = repository.findOneBySchemaName(tenant);
            if (existingTenant == null) {
                TenantServerConnection tenantServerConnection = new TenantServerConnection();
                tenantServerConnection.setSchemaName(tenant);
                tenantServerConnection.setSchemaServer(hostname);
                tenantServerConnection.setSchemaServerPort(String.valueOf(port));
                tenantServerConnection.setSchemaUsername(username);
                tenantServerConnection.setSchemaPassword(password);
                tenantServerConnection.setAutoUpdateEnabled(true);
                repository.saveAndFlush(tenantServerConnection);
            }
        }
    }

    private void flywayTenants() {
        for (TenantServerConnection tenant : repository.findAll()) {
            if (tenant.isAutoUpdateEnabled()) {
                try {
                    ThreadLocalContextUtil.setTenant(tenant);
                    final Flyway fw = new Flyway();
                    fw.setDataSource(dataSourcePerTenantService.retrieveDataSource());
                    fw.setLocations("sql/migrations/tenant");
                    fw.setInitOnMigrate(true);
                    fw.setOutOfOrder(true);
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("tenantDatabase", tenant.getSchemaName()); // add tenant as aud claim
                    placeholders.put("userAccessTokenValidity", userTokenAccessValiditySeconds);
                    placeholders.put("userRefreshTokenValidity", userTokenRefreshValiditySeconds);
                    placeholders.put("clientAccessTokenValidity", clientAccessTokenValidity);
                    placeholders.put("channelClientSecret", channelClientSecret);
                    placeholders.put("identityProviderResourceId", IDENTITY_PROVIDER_RESOURCE_ID); // add identity provider as aud claim
                    fw.setPlaceholders(placeholders);
                    fw.migrate();
                } catch (Exception e) {
                    logger.error("Error when running flyway on tenant: {}", tenant.getSchemaName(), e);
                } finally {
                    ThreadLocalContextUtil.clear();
                }
            }
        }
    }
}
