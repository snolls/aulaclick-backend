package com.aulaclick.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        System.out.println("=== DEBUG DE SEGURIDAD JWT ===");
        String header = request.getHeader("Authorization");
        System.out.println("Cabecera Authorization recibida: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.esValido(token)) {
                Claims claims = jwtUtil.parsearClaims(token);
                String rol = claims.get("rol", String.class);
                String username = claims.getSubject();
                var authorities = List.of(new SimpleGrantedAuthority(rol));
                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );
                auth.setDetails(claims);
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("Token validado para el usuario: " + username);
                System.out.println("Autoridades/Roles inyectados en Spring: " + authorities);
            } else {
                System.out.println("Token INVÁLIDO o expirado.");
            }
        } else {
            System.out.println("Sin cabecera Authorization o formato incorrecto.");
        }
        System.out.println("=================================");

        chain.doFilter(request, response);
    }
}
