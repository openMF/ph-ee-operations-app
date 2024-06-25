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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.fineract.core.tenants.TenantConnectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Service
public class DataSourcePerTenantService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DataSource createNewDataSourceFor(TenantConnectionProperties tenant) {
        HikariConfig config = new HikariConfig();
        config.setUsername(tenant.getSchemaUsername());
        config.setPassword(tenant.getSchemaPassword());
        config.setJdbcUrl(createJdbcUrl(tenant.getJdbcProtocol(), tenant.getJdbcSubProtocol(), tenant.getSchemaServer(), Integer.parseInt(tenant.getSchemaServerPort()), tenant.getSchemaName()));
        config.setAutoCommit(true);
        config.setConnectionInitSql("SELECT 1");
        config.setValidationTimeout(30000);
        config.setConnectionTestQuery("SELECT 1");
        config.setConnectionTimeout(30000);
        config.setDriverClassName(tenant.getDriverClass());
        config.setIdleTimeout(600000);
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setPoolName(tenant.getSchemaName() + "Pool");
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        return new HikariDataSource(config);
    }

    private String createJdbcUrl(String jdbcProtocol, String jdbcSubprotocol, String hostname, int port, String dbName) {
        return new StringBuilder()
                .append(jdbcProtocol)
                .append(':')
                .append(jdbcSubprotocol)
                .append("://")
                .append(hostname)
                .append(':')
                .append(port)
                .append('/')
                .append(dbName)
                .toString();
    }

}