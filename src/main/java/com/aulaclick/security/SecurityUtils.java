package com.aulaclick.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtils {

    public static String getRol() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().isEmpty()) return null;
        return auth.getAuthorities().iterator().next().getAuthority();
    }

    public static Long getSedeId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof Claims)) return null;
        Claims claims = (Claims) auth.getDetails();
        return claims.get("sedeId", Long.class);
    }

    public static Long getSedeIdOrForbidden() {
        Long sedeId = getSedeId();
        if (sedeId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no tiene sede asignada.");
        }
        return sedeId;
    }

    public static boolean esAdmin() {
        return "ADMIN".equals(getRol());
    }
}
