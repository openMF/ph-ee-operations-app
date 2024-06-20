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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.permission.Permission;
import org.apache.fineract.organisation.role.Role;
import org.apache.fineract.useradministration.data.PermissionData;
import org.apache.fineract.useradministration.data.RoleData;
import org.apache.fineract.useradministration.data.RolePermissionsData;
import org.apache.fineract.useradministration.domain.PermissionRepository;
import org.apache.fineract.useradministration.domain.RoleRepository;
import org.apache.fineract.useradministration.service.PermissionReadPlatformService;
import org.apache.fineract.useradministration.service.RoleReadPlatformService;
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
@Tag(name = "Roles API")
@SecurityRequirement(name = "api")
public class RolesApi {

    public static final String DISABLE = "disable";
    public static final String ENABLE = "enable";

    private static final String RESOURCE_NAME_FOR_PERMISSIONS = "ROLE";

    @Autowired
    private PlatformSecurityContext context;

    @Autowired
    private RoleReadPlatformService roleReadPlatformService;

    @Autowired
    private PermissionReadPlatformService permissionReadPlatformService;

    @Autowired
    private PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    private EventService eventService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<RoleData> retrieveAll() {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve all roles")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            this.context.jwt().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
            return this.roleReadPlatformService.retrieveAll();
        });
    }

    @GetMapping(path = "{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RoleData retrieveOne(@PathVariable("roleId") Long roleId, HttpServletResponse response) {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            this.context.jwt().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
            return this.roleReadPlatformService.retrieveOne(roleId);
        });
    }

    @GetMapping(path = "{roleId}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public RolePermissionsData retrievePermissions(@PathVariable("roleId") final Long roleId) {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve permissions for role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            this.context.jwt().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);

            final RoleData role = this.roleReadPlatformService.retrieveOne(roleId);
            final Collection<PermissionData> permissionUsageData = this.permissionReadPlatformService.retrieveAllRolePermissions(roleId);
            return role.toRolePermissionData(permissionUsageData);
        });
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommandProcessingResult create(@RequestBody final String apiRequestBodyAsJson) {
       return eventService.auditedEvent(event -> event
                .setEvent("create role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .createRole() //
                    .withJson(apiRequestBodyAsJson) //
                    .build();

            return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        });
    }

    @PostMapping(path = "/{roleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommandProcessingResult actionsOnRoles(@PathVariable("roleId") final Long roleId,
                                          @RequestParam("command") final String commandParam,
                                          @RequestBody final String apiRequestBodyAsJson) {
        return eventService.auditedEvent(event -> event
                .setEvent(commandParam + " role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
            CommandProcessingResult result = null;
            if (is(commandParam, DISABLE)) {
                final CommandWrapper commandRequest = builder.disableRole(roleId).build();
                result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            } else if (is(commandParam, ENABLE)) {
                final CommandWrapper commandRequest = builder.enableRole(roleId).build();
                result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            }
            return result;
        });
    }

    @PutMapping(path = "/{roleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommandProcessingResult update(@PathVariable("roleId") final Long roleId, @RequestBody final String apiRequestBodyAsJson) {
        return eventService.auditedEvent(event -> event
                .setEvent("update role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .updateRole(roleId) //
                    .withJson(apiRequestBodyAsJson) //
                    .build();
            return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        });
    }

    @DeleteMapping(path = "/{roleId}")
    public CommandProcessingResult delete(@PathVariable("roleId") final Long roleId) {
        return eventService.auditedEvent(event -> event
                .setEvent("delete role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .deleteRole(roleId) //
                    .build();
            return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        });
    }


    @PutMapping(path = "/{roleId}/permissions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommandProcessingResult permissionAssignment(@PathVariable("roleId") Long roleId,
                                     @RequestBody final String apiRequestBodyAsJson) {
        return eventService.auditedEvent(event -> event
                .setEvent("assign permissions to role")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(roleId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .updateRolePermissions(roleId) //
                    .withJson(apiRequestBodyAsJson) //
                    .build();
            return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        });
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}
