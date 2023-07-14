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

import org.apache.fineract.core.tenants.TenantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Service
public class RoutingDataSource extends AbstractDataSource implements ApplicationListener<ContextStartedEvent> {

    @Autowired
    TenantsService tenantsService;
    private boolean initialized;

    @Override
    public Connection getConnection() throws SQLException {
        DataSource tenantDataSource = ThreadLocalContextUtil.getTenantDataSource();
        if (tenantDataSource == null && !initialized) {
            logger.warn("No tenant connection found in threadlocal context, returning the first connection to let JPA repositories initialize");
            return tenantsService.getAnyDataSource().getConnection();
        }
        return tenantDataSource.getConnection();
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return getConnection();
    }

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        this.initialized = true;
    }
}
