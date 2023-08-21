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
package org.apache.fineract.api;

import com.baasflow.commons.events.EventLogLevel;
import com.baasflow.commons.events.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;
import org.apache.fineract.organisation.permission.Permission;
import org.apache.fineract.organisation.permission.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@SecurityRequirement(name = "auth")
@RequestMapping("/api/v1")
public class PermissionsApi {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EventService eventService;


    @GetMapping(path = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Permission> retrieveAll() {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve all permissions")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            return this.permissionRepository.findAll();
        });
    }

    @GetMapping(path = "/permission/{permissionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Permission retrieveOne(@PathVariable("permissionId") Long permissionId, HttpServletResponse response) {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve permission")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(permissionId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            Optional<Permission> permission = permissionRepository.findById(permissionId);
            if (permission.isPresent()) {
                return permission.get();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        });
    }

    @PostMapping(path = "/permission", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void create(@RequestBody Permission permission, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("create permission")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(permission.getCode())
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            Permission existing = permissionRepository.findOneByCode(permission.getCode());
            if (existing == null) {
                permission.setId(null);
                permissionRepository.saveAndFlush(permission);
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            return null;
        });
    }

    @PutMapping(path = "/permission/{permissionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable("permissionId") Long permissionId, @RequestBody Permission permission, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("update permission")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(permissionId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            Optional<Permission> existing = permissionRepository.findById(permissionId);
            if (existing.isPresent()) {
                permission.setId(permissionId);
                permission.setRoles(existing.get().getRoles());
                permissionRepository.saveAndFlush(permission);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return null;
        });
    }

    @DeleteMapping(path = "/permission/{permissionId}")
    public void delete(@PathVariable("permissionId") Long permissionId, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("delete permission")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(permissionId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            if (permissionRepository.existsById(permissionId)) {
                permissionRepository.deleteById(permissionId);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return null;
        });
    }
}
