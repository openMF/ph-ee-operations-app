/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.security.service;

import org.apache.fineract.commands.domain.CommandWrapper;

import org.apache.fineract.infrastructure.security.domain.PlatformJwt;
import org.apache.fineract.organisation.user.AppUser;
import org.apache.fineract.useradministration.exception.UnAuthenticatedUserException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Wrapper around spring security's {@link SecurityContext} for extracted the current authenticated {@link AppUser}.
 */

@Service
public class SpringSecurityPlatformSecurityContext implements PlatformSecurityContext {

    @Override
    public PlatformJwt jwt() {

        Jwt jwt = null;
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            final Authentication auth = context.getAuthentication();
            if (auth != null) {
                jwt = (Jwt) auth.getPrincipal();
            }
        }

        if (jwt == null) {
            throw new UnAuthenticatedUserException();
        }

        return new PlatformJwt(jwt);
    }

    @Override
    public PlatformJwt getJwtIfPresent() {

        Jwt jwt = null;
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            final Authentication auth = context.getAuthentication();
            if (auth != null) {
                jwt = (Jwt) auth.getPrincipal();
            }
        }

        if (jwt == null) {
            return null;
        }

        return new PlatformJwt(jwt);
    }

    @Override
    public PlatformJwt jwt(CommandWrapper commandWrapper) {

        Jwt jwt = null;
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            final Authentication auth = context.getAuthentication();
            if (auth != null) {
                jwt = (Jwt) auth.getPrincipal();
            }
        }

        if (jwt == null) {
            throw new UnAuthenticatedUserException();
        }

        return new PlatformJwt(jwt);

    }

}
