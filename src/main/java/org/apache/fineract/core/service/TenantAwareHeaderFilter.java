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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.fineract.core.tenants.TenantsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;


public class TenantAwareHeaderFilter extends GenericFilterBean {
    private static final String TENANT_IDENTIFIER_REQUEST_HEADER = "Platform-TenantId";
    public static final String TENANT_IDENTIFIER_REQUEST_PARAM = "tenantIdentifier";
    private static final List<String> EXCLUDED_URL = List.of("/oauth2/authorize", "/oauth2/revoke", "/favicon.ico", "/login", "/oauth2/token", "/logout");
    private static final List<String> EXCLUDED_PREFIX = List.of("/actuator", "/css", "/images");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TenantsService tenantService;
    public static ThreadLocal<String> tenant = new ThreadLocal<>();

    public TenantAwareHeaderFilter(TenantsService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        final StopWatch task = new StopWatch();
        task.start();

        try {
            if (!EXCLUDED_URL.contains(request.getServletPath()) && !EXCLUDED_PREFIX.stream().anyMatch(pref -> request.getServletPath().startsWith(pref)) &&
                    !request.getServletPath().contains("swagger") && !request.getServletPath().contains("api-docs") && !"OPTIONS".equalsIgnoreCase(request.getMethod())) {
                String tenantIdentifier = request.getHeader(TENANT_IDENTIFIER_REQUEST_HEADER);
                if (tenantIdentifier == null || tenantIdentifier.length() < 1) {
                    tenantIdentifier = request.getParameter(TENANT_IDENTIFIER_REQUEST_PARAM);
                }

                if (tenantIdentifier == null || tenantIdentifier.length() < 1) {
                    System.out.println(request.getServletPath());
                    System.out.println(request.getMethod());
                    throw new RuntimeException(
                            String.format("No tenant identifier found! Add request header: %s or request param: %s", TENANT_IDENTIFIER_REQUEST_HEADER, TENANT_IDENTIFIER_REQUEST_PARAM));
                }
                tenant.set(tenantIdentifier);
                ThreadLocalContextUtil.setTenantDataSource(tenantService.getTenantDataSource(tenantIdentifier));
            }
            chain.doFilter(request, res);
        } catch (Exception e) {
            logger.error("Error when executing request!", e);
            SecurityContextHolder.getContext().setAuthentication(null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            task.stop();
            ThreadLocalContextUtil.clear();
        }
    }
}
