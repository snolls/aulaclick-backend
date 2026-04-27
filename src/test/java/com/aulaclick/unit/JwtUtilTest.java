package com.aulaclick.unit;

import com.aulaclick.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link JwtUtil}.
 *
 * <p>Verifica la generación, validación y extracción de claims de tokens JWT
 * sin necesidad de contexto Spring. Se instancia {@link JwtUtil} directamente
 * con una clave secreta de prueba.</p>
 */
class JwtUtilTest {

    /** Clave secreta de prueba (mínimo 256 bits para HS256). */
    private static final String SECRET =
            "aulaclick-test-secret-key-change-in-production-2024-extra-padding";

    private static final Long   ID_USUARIO = 42L;
    private static final String EMAIL      = "test@aulaclick.edu";
    private static final String ROL        = "USUARIO";
    private static final Long   SEDE_ID    = 1L;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T09 – Subject del token
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T09: El subject del token generado debe ser el email del usuario.
     *
     * <p>Entrada: idUsuario=42, email="test@aulaclick.edu", rol="USUARIO", sedeId=1</p>
     * <p>Salida esperada: {@code claims.getSubject()} == "test@aulaclick.edu"</p>
     */
    @Test
    @DisplayName("T09 – El token generado almacena el email como subject")
    void T09_tokenGenerado_sujetoCorrecto() {
        String token  = jwtUtil.generarToken(ID_USUARIO, EMAIL, ROL, SEDE_ID);
        Claims claims = jwtUtil.parsearClaims(token);
        assertEquals(EMAIL, claims.getSubject(),
                "El subject del JWT debe ser el email del usuario");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T10 – Token válido
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T10: Un token recién generado con la misma clave debe ser considerado válido.
     *
     * <p>Entrada: token generado con {@link JwtUtil#generarToken}</p>
     * <p>Salida esperada: {@code esValido(token)} == true</p>
     */
    @Test
    @DisplayName("T10 – Token recién generado es válido")
    void T10_tokenValido_retornaTrue() {
        String token = jwtUtil.generarToken(ID_USUARIO, EMAIL, ROL, SEDE_ID);
        assertTrue(jwtUtil.esValido(token),
                "Un token recién emitido debe pasar la validación");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T11 – Token manipulado
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T11: Un token con la firma alterada debe ser rechazado como inválido.
     *
     * <p>Se añade un carácter extra al final del token para romper la firma HMAC.</p>
     * <p>Entrada: token válido + "X" al final</p>
     * <p>Salida esperada: {@code esValido(tokenManipulado)} == false</p>
     */
    @Test
    @DisplayName("T11 – Token con firma alterada no es válido")
    void T11_tokenManipulado_retornaFalse() {
        String token           = jwtUtil.generarToken(ID_USUARIO, EMAIL, ROL, SEDE_ID);
        String tokenManipulado = token + "X";
        assertFalse(jwtUtil.esValido(tokenManipulado),
                "Un token con la firma alterada debe ser rechazado");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T12 – Claim de rol
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T12: Los claims extraídos deben contener el rol correcto.
     *
     * <p>Entrada: token generado con rol "USUARIO"</p>
     * <p>Salida esperada: {@code claims.get("rol")} == "USUARIO"</p>
     */
    @Test
    @DisplayName("T12 – Los claims del token incluyen el rol correcto")
    void T12_claimsContienenRolCorrecto() {
        String token  = jwtUtil.generarToken(ID_USUARIO, EMAIL, ROL, SEDE_ID);
        Claims claims = jwtUtil.parsearClaims(token);
        assertEquals(ROL, claims.get("rol", String.class),
                "El claim 'rol' debe coincidir con el rol con el que se generó el token");
    }
}
