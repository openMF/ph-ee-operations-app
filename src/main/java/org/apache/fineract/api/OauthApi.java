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
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.fineract.organisation.role.Role;
import org.apache.fineract.organisation.user.AppUser;
import org.apache.fineract.organisation.user.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/oauth")
@Tag(name = "Oauth API")
public class OauthApi {

/*    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EventService eventService;


    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String oauthToken(HttpServletRequest request, HttpServletResponse response) {
        return eventService.auditedEvent(event -> event
                .setEvent("login")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(request.getParameter("username"))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {

            String grantType = request.getParameter("grant_type");
            String jti = UUID.randomUUID().toString();

            try {
                if (grantType.equals("password")) {
                    String userName = request.getParameter("username");
                    AppUser appUser = appUserRepository.findAppUserByName(userName);
                    checkUserExists(appUser);
                    List<String> userRoles = appUser.getRoles().stream().map(Role::getName).toList();
                    String password = request.getParameter("password");
                    if (!passwordEncoder.matches(password, appUser.getPassword())) {
                        throw new RuntimeException("Login failed!");
                    }
                    String scopeParameter = request.getParameter("scope");
                    List<String> scope = scopeParameter.contains(",") ? Arrays.asList(scopeParameter.split(",")) : List.of(scopeParameter);
                    String clientId = request.getParameter("client_id");
                    return generateResponse(userName, clientId, scope, jti, userRoles);
                } else if (grantType.equals("refresh_token")) {
                    String refreshToken = request.getParameter("refresh_token");
                    SignedJWT signedJwt = JwtUtil.getSignedJwt(refreshToken);
                    String userName = JwtUtil.getUsername(signedJwt);
                    AppUser appUser = appUserRepository.findAppUserByName(userName);
                    checkUserExists(appUser);
                    List<String> userRoles = appUser.getRoles().stream().map(Role::getName).toList();
                    String clientId = JwtUtil.getClientId(signedJwt);
                    List<String> scope = JwtUtil.getScope(signedJwt);
                    return generateResponse(userName, clientId, scope, jti, userRoles);
                } else {
                    throw new RuntimeException("Unsupported grant!");
                }
            } catch (IOException | JOSEException | URISyntaxException | ParseException | InvalidKeySpecException |
                     NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String generateResponse(Authentication authentication, String clientId) {
        AppUser appUser = (AppUser) authentication.getPrincipal();
        List<String> roles = appUser.getRoles().stream().map(Role::getName).toList();
        List<String> scope = appUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        List<String> finalScope = scope == null || scope.isEmpty() ? List.of("all") : scope;

        String jti = UUID.randomUUID().toString();
        Instant now = Instant.now();
        JwtClaimsSet payload = JwtClaimsSet.builder()
                .claim("userName", appUser.getUsername())
                .claim("authorities", List.of("ALL_FUNCTIONS"))
                .claim("clientId", clientId)
                .claim("scope", finalScope)
                .claim("roles", roles)
                .id(jti)
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .build();

        String jwt = this.encoder.encode(JwtEncoderParameters.from(payload)).getTokenValue();

        String responseBody = String.format("{\n" +
                "  \"access_token\": \"%s\",\n" +
                "  \"token_type\": \"bearer\",\n" +
                "  \"refresh_token\": \"%s\",\n" +
                "  \"expires_in\": %s,\n" +
                "  \"scope\": \"all\",\n" +
                "  \"jti\": \"%s\"\n" +
                "}\n", jwt, jwt, 3600, jti);
        return responseBody;
    }*/

}
