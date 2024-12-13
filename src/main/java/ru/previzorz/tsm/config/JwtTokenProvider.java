package ru.previzorz.tsm.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.exception.JwtValidationException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(User user) {
        long expirationTime = 86400000;

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", roles)
                .claim("tokenVersion", user.getTokenVersion())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey())
                .compact();
    }

    public String extractUsername(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        try {
            return Jwts.parser()
                    .verifyWith(secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtValidationException("JWT token is unsupported", e);
        } catch (MalformedJwtException e) {
            throw new JwtValidationException("JWT token is malformed", e);
        } catch (SecurityException e) {
            throw new JwtValidationException("JWT token signature validation failed", e);
        } catch (JwtException e) {
            throw new JwtValidationException("JWT token processing error", e);
        }
    }

    public boolean isTokenValid(String token, User user) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            Integer tokenVersion = (Integer) claims.get("tokenVersion");

            return username.equals(user.getUsername())
                    && tokenVersion != null
                    && tokenVersion.equals(user.getTokenVersion())
                    && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
