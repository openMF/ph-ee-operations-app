package org.apache.fineract.utils;

import com.nimbusds.jwt.SignedJWT;

import java.util.List;

public class JwtUtil {

    public static SignedJWT getSignedJwt(String jwtToken) {
        try {
            return SignedJWT.parse(jwtToken.replace("Bearer ", ""));

        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUsername(String jwtToken) {
        try {
            SignedJWT jwt = SignedJWT.parse(jwtToken.replace("Bearer ", ""));
            return jwt.getJWTClaimsSet().getStringClaim("userName");
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUsername(SignedJWT signedJWT) {
        try {
            return signedJWT.getJWTClaimsSet().getStringClaim("userName");
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getRoles(SignedJWT signedJWT) {
        try {
            return signedJWT.getJWTClaimsSet().getStringListClaim("roles");
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getClientId(SignedJWT signedJWT) {
        try {
            return signedJWT.getJWTClaimsSet().getStringClaim("clientId");
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getScope(SignedJWT signedJWT) {
        try {
            return signedJWT.getJWTClaimsSet().getStringListClaim("scope");
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
