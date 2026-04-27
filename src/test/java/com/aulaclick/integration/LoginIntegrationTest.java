package com.aulaclick.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración del endpoint de autenticación {@code POST /api/usuarios/login}.
 *
 * <p>Levanta el contexto completo de Spring Boot contra una base de datos H2
 * en memoria (perfil "test"). Verifica que la cadena completa
 * Controller → Service → Repository produce las respuestas HTTP esperadas
 * sin necesidad de una base de datos externa.</p>
 *
 * <h3>Mecanismo</h3>
 * <ul>
 *   <li>{@link SpringBootTest} arranca el contexto completo de la aplicación.</li>
 *   <li>{@link AutoConfigureMockMvc} inyecta un cliente HTTP simulado (MockMvc).</li>
 *   <li>El perfil "test" activa {@code application-test.properties} con H2.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ─────────────────────────────────────────────────────────────────────────
    // IT01 – Usuario no registrado
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * IT01: El login con un email que no existe en la base de datos
     * debe devolver HTTP 404 Not Found.
     *
     * <p>Flujo: MockMvc → UsuarioController → UsuarioService →
     * UsuarioRepository (H2 vacía) → lanza ResponseStatusException(NOT_FOUND)</p>
     *
     * <p>Entrada: JSON con email inexistente y contraseña arbitraria</p>
     * <p>Salida esperada: HTTP 404</p>
     */
    @Test
    @DisplayName("IT01 – Login con email no registrado devuelve 404")
    void IT01_loginUsuarioNoExistente_retorna404() throws Exception {
        String body = """
                {
                  "email": "noexiste@aulaclick.edu",
                  "password": "cualquierCosa123"
                }
                """;

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // IT02 – Cuerpo vacío
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * IT02: Una petición de login sin cuerpo JSON debe devolver HTTP 400 Bad Request.
     *
     * <p>Spring no puede deserializar un body vacío en {@code LoginRequest},
     * por lo que el framework lanza {@code HttpMessageNotReadableException}
     * antes de llegar al controlador.</p>
     *
     * <p>Entrada: petición POST sin body</p>
     * <p>Salida esperada: HTTP 400</p>
     */
    @Test
    @DisplayName("IT02 – Login sin cuerpo JSON devuelve 400")
    void IT02_loginSinCuerpo_retorna400() throws Exception {
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
