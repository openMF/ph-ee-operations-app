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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.data.AppUserData;
import org.apache.fineract.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@SecurityRequirement(name = "auth")
@RequestMapping("/api/v1/users")
@Tag(name = "Users API")
public class UsersApi {

    private static final String RESOURCE_NAME_FOR_PERMISSIONS = "USER";

    @Autowired
    private  PlatformSecurityContext context;

    @Autowired
    private AppUserReadPlatformService readPlatformService;

    @Autowired
    private PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    private EventService eventService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<AppUserData> retrieveAll() {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve all users")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            this.context.jwt().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
            return this.readPlatformService.retrieveAllUsers();
        });
    }

    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppUserData retrieveOne(@PathVariable("userId") Long userId) {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve user")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(userId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            //this.context.jwt().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS, userId); TODO:Mernemmeszelazanyadba
            return this.readPlatformService.retrieveUser(userId);
        });
    }

    @GetMapping(path = "/template", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppUserData template() {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve template of user")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            this.context.jwt().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
            return this.readPlatformService.retrieveNewUserDetails();
        });
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommandProcessingResult create(@RequestBody final String apiRequestBodyAsJson) {
        return eventService.auditedEvent(event -> event
                .setEvent("create user")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app"), event -> {
            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .createUser() //
                    .withJson(apiRequestBodyAsJson) //
                    .build();
            return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        });
    }

    @PutMapping(path = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommandProcessingResult update(@PathVariable("userId") Long userId, @RequestBody final String apiRequestBodyAsJson) {
        return eventService.auditedEvent(event -> event
                .setEvent("update user")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(userId))
                .setPayloadType("string"), event -> {
            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .updateUser(userId) //
                    .withJson(apiRequestBodyAsJson) //
                    .build();
            return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        });
    }

    @DeleteMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommandProcessingResult delete(@PathVariable("userId") final Long userId) {
            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .deleteUser(userId) //
                    .build();

            return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }
}
