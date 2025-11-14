package com.elearning.common.util;

import com.elearning.common.components.properties.JwtProperties;
import com.elearning.common.enums.AuthProvider;
import com.elearning.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtConfig;
    private final JwtDecoder jwtDecoder;

    // Token blacklist for logout functionality
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();



    public long getExpireIn(){
        return jwtConfig.expiration().getSeconds();
    }

    public String doGenerateToken(UserPrincipal securityUser) {

        Instant now = Instant.now();

        String scope = securityUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(securityUser.getUsername())
                .issuer(AuthProvider.USER.getValue())
                .issuedAt(now)
                .expiresAt(now.plus(jwtConfig.expiration().getSeconds(), ChronoUnit.SECONDS))
                .claim("type", "access")
                .claim("scope", scope)
                .claim("userId",securityUser.getUserId())
                .claim("role", securityUser.getRole()) // Add the role name
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String doGenerateRefreshToken(UserPrincipal securityUser) {

        Instant now = Instant.now();

//        String scope = securityUser.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(securityUser.getUsername())
                .issuer(AuthProvider.USER.getValue())
                .issuedAt(now)
                .expiresAt(now.plus(jwtConfig.refresh().getSeconds(), ChronoUnit.SECONDS))
                .claim("type", "refresh")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    /**
     * Validate a refresh token
     */
    public boolean validateRefreshToken(String token) {
        try {
            if (!validateToken(token)) {
                return false;
            }

            Jwt jwt = jwtDecoder.decode(token);

            // Verify it's a refresh token
            String type = jwt.getClaim("type");
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate a token and check if it's been blacklisted
     */
    public boolean validateToken(String token) {
        try {

            if (blacklistedTokens.contains(token)) {
                return false;
            }

            // Decode and validate the token
            Jwt jwt = jwtDecoder.decode(token);

            // Check if token is expired
            Instant expiration = jwt.getExpiresAt();
            boolean isExpired = expiration != null && expiration.isBefore(Instant.now());
            return !isExpired;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract username from refresh token
     */
    public String getUsernameFromRefreshToken(String token) {
        return getUsernameFromToken(token);
    }
    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Invalidate token (for logout)
     */
    public void invalidateToken(String token) {
        if (token != null && !token.isEmpty()) {
            blacklistedTokens.add(token);
        }
    }

}
