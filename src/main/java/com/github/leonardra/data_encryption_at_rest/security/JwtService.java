package com.github.leonardra.data_encryption_at_rest.security;


import com.github.leonardra.data_encryption_at_rest.dtos.responses.JWTResponse;
import com.github.leonardra.data_encryption_at_rest.enums.AuthTokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String bearerTokenSecretKey;
    @Value("${application.security.jwt.refresh-token-secret-key}")
    private String refreshTokenSecretKey;
    @Value("${application.security.jwt.expiration}")
    private long bearerTokenExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public String extractSubject(String token, AuthTokenType tokenType) {
        if (tokenType == AuthTokenType.BEARER) {
            return extractClaim(token, Claims::getSubject, bearerTokenSecretKey);
        } else {
            return extractClaim(token, Claims::getSubject, refreshTokenSecretKey);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    public JWTResponse generateToken(UserDetails userDetails, AuthTokenType authTokenType) {
        return generateToken(new HashMap<>(), userDetails, authTokenType);
    }

    public JWTResponse generateToken(Map<String, Object> extraClaims, UserDetails userDetails, AuthTokenType authTokenType) {
        if (authTokenType == AuthTokenType.BEARER) {
            return buildToken(extraClaims, userDetails, bearerTokenExpiration, bearerTokenSecretKey);
        } else {
            return buildToken(extraClaims, userDetails, refreshTokenExpiration, refreshTokenSecretKey);
        }
    }

    private JWTResponse buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration, String secretKey) {

        long expiredAt = System.currentTimeMillis() + expiration;
        String jwt = Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(expiredAt))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256).compact();

        return JWTResponse.builder().jwt(jwt).expiredAt(expiredAt).build();
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public boolean isTokenValid(String token, UserDetails userDetails, AuthTokenType authTokenType) {
        final String username = extractSubject(token, authTokenType);

        boolean isBearerTokenExpired = isTokenExpired(token, AuthTokenType.BEARER);
        return (username.equalsIgnoreCase(userDetails.getUsername())) && !isBearerTokenExpired;
    }

    public boolean isTokenExpired(String token, AuthTokenType authTokenType) {
        String secretKey = authTokenType == AuthTokenType.BEARER ? bearerTokenSecretKey : refreshTokenSecretKey;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(secretKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        } catch (Exception exception) {
            log.error("Exception ---> {}", exception.getMessage());
            return true;
        }
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey(secretKey)).build().parseClaimsJws(token).getBody();
    }
}
