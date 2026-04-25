package com.aulaclick.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7; // 7 días

    public JwtUtil(@Value("${jwt.secret:aulaclick-secret-key-change-in-production-2024}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(Long idUsuario, String email, String rol, Long sedeId) {
        var builder = Jwts.builder()
                .setSubject(email)
                .claim("idUsuario", idUsuario)
                .claim("rol", rol);
        if (sedeId != null) {
            builder.claim("sedeId", sedeId);
        }
        return builder
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parsearClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean esValido(String token) {
        try {
            parsearClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
