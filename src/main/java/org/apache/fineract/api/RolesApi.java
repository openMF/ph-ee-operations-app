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
import org.apache.fineract.organisation.role.Role;
import org.apache.fineract.organisation.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.apache.fineract.api.AssignmentAction.ASSIGN;

@RestController
@SecurityRequirement(name = "auth")
@RequestMapping("/api/v1/roles")
public class RolesApi {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EventService eventService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Role> retrieveAll() {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve all roles")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            return this.roleRepository.findAll();
        });
    }

    @GetMapping(path = "{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Role retrieveOne(@PathVariable("roleId") Long roleId, HttpServletResponse response) {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            Optional<Role> role = roleRepository.findById(roleId);
            if (role.isPresent()) {
                return role.get();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        });
    }

    @GetMapping(path = "{roleId}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Permission> retrievePermissions(@PathVariable("roleId") Long roleId, HttpServletResponse response) {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve permissions for role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            Optional<Role> role = roleRepository.findById(roleId);
            if (role.isPresent()) {
                return role.get().getPermissions();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        });
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void create(@RequestBody Role role, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("create role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(role.getName())
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            Role existing = roleRepository.getRoleByName(role.getName());
            if (existing == null) {
                role.setId(null);
                role.setDisabled(false);
                roleRepository.saveAndFlush(role);
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            return null;
        });
    }

    @PutMapping(path = "/{roleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable("roleId") Long roleId, @RequestBody Role role, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("update role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            Optional<Role> existing = roleRepository.findById(roleId);
            if (existing.isPresent()) {
                role.setId(roleId);
                role.setAppUsers(existing.get().getAppusers());
                role.setPermissions(existing.get().getPermissions());
                roleRepository.saveAndFlush(role);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return null;
        });
    }


    @DeleteMapping(path = "/{roleId}")
    public void delete(@PathVariable("roleId") Long roleId, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("delete role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {

            if (roleRepository.existsById(roleId)) {
                roleRepository.deleteById(roleId);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return null;
        });
    }


    @PutMapping(path = "/{roleId}/permissions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void permissionAssignment(@PathVariable("roleId") Long roleId, @RequestParam("action") AssignmentAction action,
                                     @RequestBody EntityAssignments assignments, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("assign permissions to role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            Optional<Role> existingRole = roleRepository.findById(roleId);
            if (existingRole.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            Role role = existingRole.get();
            Collection<Permission> permissionToAssign = role.getPermissions();
            List<Long> existingPermissionIds = permissionToAssign.stream()
                    .map(Permission::getId)
                    .toList();
            List<Permission> deltaPermissions = assignments.getEntityIds().stream()
                    .filter(id -> {
                        if (ASSIGN.equals(action)) {
                            return !existingPermissionIds.contains(id);
                        } else { // revoke
                            return existingPermissionIds.contains(id);
                        }
                    })
                    .map(id -> {
                        Optional<Permission> p = permissionRepository.findById(id);
                        if (p.isEmpty()) {
                            throw new RuntimeException("Invalid permission id: " + id + " can not continue assignment!");
                        } else {
                            return p.get();
                        }
                    }).toList();

            if (!deltaPermissions.isEmpty()) {
                if (ASSIGN.equals(action)) {
                    permissionToAssign.addAll(deltaPermissions);
                } else { // revoke
                    permissionToAssign.removeAll(deltaPermissions);
                }
                role.setPermissions(permissionToAssign);
                roleRepository.saveAndFlush(role);
            }
            return null;
        });
    }
}
