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
import jakarta.servlet.http.HttpServletResponse;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;
import org.apache.fineract.organisation.role.Role;
import org.apache.fineract.organisation.role.RoleRepository;
import org.apache.fineract.organisation.user.AppUser;
import org.apache.fineract.organisation.user.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.apache.fineract.api.AssignmentAction.ASSIGN;

@RestController
@SecurityRequirement(name = "auth")
@RequestMapping("/api/v1/users")
@Tag(name = "Users API")
public class UsersApi {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository appuserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventService eventService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppUser> retrieveAll() {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve all users")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            return this.appuserRepository.findAll();
        });
    }

    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppUser retrieveOne(@PathVariable("userId") Long userId, HttpServletResponse response) {
        return eventService.auditedEvent(event -> event
                .setEvent("retrieve user")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(userId))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {

            Optional<AppUser> user = appuserRepository.findById(userId);
            if (user.isPresent()) {
                return user.get();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        });
    }

    @GetMapping(path = "/{userId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Role> retrieveRoles(@PathVariable("userId") Long userId, HttpServletResponse response) {
        return eventService.auditedEvent(event -> event
                        .setEvent("retrieve roles for user")
                        .setEventLogLevel(EventLogLevel.INFO)
                        .setSourceModule("operations-app")
                        .setPayload(String.valueOf(userId))
                        .setPayloadType("string")
                        .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
                    Optional<AppUser> user = appuserRepository.findById(userId);
                    if (user.isPresent()) {
                        return user.get().getRoles();
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return null;
                    }
                }
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void create(@RequestBody AppUser appUser, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("create user")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(appUser.getUsername())
                .setPayloadType("string"), event -> {
            AppUser existing = appuserRepository.findAppUserByName(appUser.getUsername());
            if (existing == null) {
                // TODO enforce password policy
                appUser.setId(null);
                appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
                appuserRepository.saveAndFlush(appUser);
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            return null;
        });
    }

    @PutMapping(path = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable("userId") Long userId, @RequestBody AppUser appUser, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("update user")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(userId))
                .setPayloadType("string"), event -> {
            AppUser existing = appuserRepository.findById(userId).get();
            if (existing != null) {
                appUser.setId(userId);
                appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
                appUser.setRoles(existing.getRoles());
                appuserRepository.saveAndFlush(appUser);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return null;
        });
    }

    @DeleteMapping(path = "/{userId}", produces = MediaType.TEXT_HTML_VALUE)
    public void delete(@PathVariable("userId") Long userId, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("delete user")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(userId))
                .setPayloadType("string"), event -> {
            if (appuserRepository.findById(userId).isPresent()) {
                appuserRepository.deleteById(userId);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return null;
        });
    }

    @PutMapping(path = "/{userId}/roles", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void userAssignment(@PathVariable("userId") Long userId, @RequestParam("action") AssignmentAction action,
                               @RequestBody EntityAssignments assignments, HttpServletResponse response) {
        eventService.auditedEvent(event -> event
                .setEvent("update user assignments")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(String.valueOf(userId))
                .setPayloadType("string"), event -> {
            Optional<AppUser> existingUser = appuserRepository.findById(userId);
            if (existingUser.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            AppUser user = existingUser.get();
            Collection<Role> rolesToAssign = user.getRoles();
            List<Long> existingRoleIds = rolesToAssign.stream()
                    .map(Role::getId)
                    .toList();
            List<Role> deltaRoles = assignments.getEntityIds().stream()
                    .filter(id -> {
                        if (ASSIGN.equals(action)) {
                            return !existingRoleIds.contains(id);
                        } else { // revoke
                            return existingRoleIds.contains(id);
                        }
                    })
                    .map(id -> {
                        Role r = roleRepository.findById(id).get();
                        if (r == null) {
                            throw new RuntimeException("Invalid role id: " + id + " can not continue assignment!");
                        } else {
                            return r;
                        }
                    }).collect(toList());

            if (!deltaRoles.isEmpty()) {
                if (ASSIGN.equals(action)) {
                    rolesToAssign.addAll(deltaRoles);
                } else { // revoke
                    rolesToAssign.removeAll(deltaRoles);
                }
                user.setRoles(rolesToAssign);
                appuserRepository.saveAndFlush(user);
            }
            return null;
        });
    }
}
