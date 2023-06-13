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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.organisation.user.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/oauth")
@Tag(name = "Oauth API")
public class OauthApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository appuserRepository;

    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String oauthToken(HttpServletRequest request, HttpServletResponse response) {


        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String grantType = request.getParameter("grant_type");
        String scope = request.getParameter("scope");
        String clientId = request.getParameter("client_id");
        logger.info("username:" + username);
        logger.info("password:" + password);
        logger.info("grantType:" + grantType);
        logger.info("scope:" + scope);
        logger.info("clientId:" + clientId);
        String jti = UUID.randomUUID().toString();

        try {
            if (grantType.equals("password")) {

                return generateResponse(username, scope, jti);
            } else if (grantType.equals("refresh_token")) {
                return generateResponse(username, scope, jti);
            } else {
                throw new RuntimeException("Unsupported grant!");
            }
        } catch (IOException | JOSEException | URISyntaxException | ParseException | InvalidKeySpecException |
                 NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateResponse(String username, String scope, String jti) throws IOException, URISyntaxException, ParseException, JOSEException, InvalidKeySpecException, NoSuchAlgorithmException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();

        List<String> finalScope = StringUtils.isEmpty(scope) ? List.of("all") : (scope.contains(",") ? List.of(scope.split(",")) : List.of(scope));

        JWTClaimsSet payload = new JWTClaimsSet.Builder()
                .claim("user_name", username)
                .claim("authorities", List.of("ALL_FUNCTIONS"))
                .claim("client_id", "community-app")
                .claim("scope", finalScope)
                .jwtID(jti)
                .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                .build();

        String privateKey = getPemContent("jwt_pkcs8.pem")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "");

        byte[] key = Base64.decodeBase64(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        PrivateKey finalKey = keyFactory.generatePrivate(keySpec);
        SignedJWT signedJWT = new SignedJWT(header, payload);
        signedJWT.sign(new RSASSASigner(finalKey));
        String jwt = signedJWT.serialize();


        String responseBody = String.format("{\n" +
                "  \"access_token\": \"%s\",\n" +
                "  \"token_type\": \"bearer\",\n" +
                "  \"refresh_token\": \"%s\",\n" +
                "  \"expires_in\": %s,\n" +
                "  \"scope\": \"all\",\n" +
                "  \"jti\": \"%s\"\n" +
                "}\n", jwt, jwt, 3600, jti);
        return responseBody;
    }


    private String getPemContent(String file) throws IOException, URISyntaxException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource(file).getInputStream()))) {
            return bufferedReader.lines().collect(Collectors.joining(""));
        }
    }
}
