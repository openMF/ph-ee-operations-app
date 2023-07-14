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
package org.apache.fineract.core.service.migrate;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.fineract.core.service.DataSourcePerTenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TenantDatabaseUpgradeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    private String connectionsSchema;

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


    public void migrateTenants(Map<String, DataSource> tenants) {
        List<String> errors = new ArrayList<>();
        for (String name : tenants.keySet()) {
            DataSource dataSource = tenants.get(name);
            try {
                logger.debug("migrating tenant {}", name);
                migrate("/db/changelog/tenant/initialize-tenant.xml", dataSource.getConnection());

            } catch (Exception e) {
                logger.error("Error migrating tenant {}: {}", name, e);
                errors.add(e.getMessage());
            }
        }
        if (errors.size() > 0) {
            throw new RuntimeException("Failed to migrate " + errors.size() + " tenants, errors were: " + errors);
        }
    }

    private Connection createConnection(String hostname, int port, String username, String password, String schema) throws SQLException {
        String jdbcUrl = String.format("%s:%s://%s:%s/%s", jdbcProtocol, jdbcSubprotocol, hostname, port, schema);
        logger.info("connecting to JDBC URL {}", jdbcUrl);
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private void migrate(String changeLogFile, Connection connection) throws LiquibaseException, ClassNotFoundException, SQLException {
        Class.forName(driverClass);
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        logger.info("starting Liquibase migrations using {} on database {}", changeLogFile, database);
        Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
        liquibase.update(liquibaseContexts);
    }
}
