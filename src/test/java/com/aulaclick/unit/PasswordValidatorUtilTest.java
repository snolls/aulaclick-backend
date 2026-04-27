package com.aulaclick.unit;

import com.aulaclick.util.PasswordValidatorUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link PasswordValidatorUtil}.
 *
 * <p>Verifica que la validación de contraseñas rechaza entradas que incumplen
 * las reglas de seguridad y acepta contraseñas correctamente formadas.</p>
 *
 * <ul>
 *   <li>Mínimo 12 caracteres</li>
 *   <li>Al menos un dígito</li>
 *   <li>Al menos una letra</li>
 *   <li>Sin '@' ni comillas</li>
 *   <li>Al menos un símbolo especial permitido</li>
 *   <li>No contiene el usuario del email ni partes del nombre</li>
 * </ul>
 */
class PasswordValidatorUtilTest {

    private static final String EMAIL  = "usuario@aulaclick.edu";
    private static final String NOMBRE = "Ana Garcia Lopez"; // sin tildes para que el matching de substring funcione en tests

    // ─────────────────────────────────────────────────────────────────────────
    // T01 – Contraseña válida
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T01: Una contraseña que cumple todos los requisitos no debe lanzar ninguna excepción.
     *
     * <p>Entrada: "Segura123!" (13 chars, número, letra, símbolo, sin datos personales)</p>
     * <p>Salida esperada: sin excepción</p>
     */
    @Test
    @DisplayName("T01 – Contraseña válida no lanza excepción")
    void T01_contraseniaValida_sinExcepcion() {
        assertDoesNotThrow(() ->
                PasswordValidatorUtil.validar("Securidad123!", NOMBRE, EMAIL));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T02 – Longitud mínima
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T02: Una contraseña de menos de 12 caracteres debe rechazarse.
     *
     * <p>Entrada: "Abc1!" (5 chars)</p>
     * <p>Salida esperada: {@link ResponseStatusException} con mensaje sobre 12 caracteres</p>
     */
    @Test
    @DisplayName("T02 – Contraseña de menos de 12 caracteres lanza excepción")
    void T02_contraseniaMuyCorta_lanzaExcepcion() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                PasswordValidatorUtil.validar("Abc1!", NOMBRE, EMAIL));
        assertTrue(ex.getReason().contains("12 caracteres"),
                "El mensaje debe indicar el requisito de 12 caracteres");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T03 – Dígito obligatorio
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T03: Una contraseña sin ningún dígito debe rechazarse.
     *
     * <p>Entrada: "SecurePass!!!" (solo letras y símbolos)</p>
     * <p>Salida esperada: {@link ResponseStatusException} con mensaje sobre número</p>
     */
    @Test
    @DisplayName("T03 – Contraseña sin dígito lanza excepción")
    void T03_contraseniaSinNumero_lanzaExcepcion() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                PasswordValidatorUtil.validar("SecurePass!!!", NOMBRE, EMAIL));
        assertTrue(ex.getReason().contains("número"),
                "El mensaje debe indicar el requisito de al menos un número");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T04 – Letra obligatoria
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T04: Una contraseña formada solo por dígitos y símbolos debe rechazarse.
     *
     * <p>Entrada: "123456789012!" (sin letras)</p>
     * <p>Salida esperada: {@link ResponseStatusException} con mensaje sobre letra</p>
     */
    @Test
    @DisplayName("T04 – Contraseña sin letra lanza excepción")
    void T04_contraseniaSinLetra_lanzaExcepcion() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                PasswordValidatorUtil.validar("123456789012!", NOMBRE, EMAIL));
        assertTrue(ex.getReason().contains("letra"),
                "El mensaje debe indicar el requisito de al menos una letra");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T05 – Carácter '@' prohibido
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T05: Una contraseña que contiene el carácter '@' debe rechazarse,
     * ya que podría confundirse con un correo electrónico.
     *
     * <p>Entrada: "Secure123@!!" (contiene '@')</p>
     * <p>Salida esperada: {@link ResponseStatusException} con mención a '@'</p>
     */
    @Test
    @DisplayName("T05 – Contraseña con '@' lanza excepción")
    void T05_contraseniaConArroba_lanzaExcepcion() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                PasswordValidatorUtil.validar("Secure123@!!", NOMBRE, EMAIL));
        assertTrue(ex.getReason().contains("'@'"),
                "El mensaje debe prohibir explícitamente el carácter '@'");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T06 – Símbolo especial obligatorio
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T06: Una contraseña sin ningún símbolo especial debe rechazarse.
     *
     * <p>Entrada: "SecurePass123" (solo letras y dígitos)</p>
     * <p>Salida esperada: {@link ResponseStatusException} con mensaje sobre símbolo</p>
     */
    @Test
    @DisplayName("T06 – Contraseña sin símbolo especial lanza excepción")
    void T06_contraseniaSinSimbolo_lanzaExcepcion() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                PasswordValidatorUtil.validar("SecurePass123", NOMBRE, EMAIL));
        assertTrue(ex.getReason().contains("símbolo"),
                "El mensaje debe indicar el requisito de símbolo especial");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T07 – Nombre de usuario del email prohibido
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T07: Una contraseña que incluye la parte local del email (antes de '@')
     * debe rechazarse para evitar contraseñas predecibles.
     *
     * <p>Entrada: "usuario123!ABC" con email "usuario@aulaclick.edu"</p>
     * <p>Salida esperada: {@link ResponseStatusException} con mención a nombre de usuario</p>
     */
    @Test
    @DisplayName("T07 – Contraseña que contiene el usuario del email lanza excepción")
    void T07_contraseniaContieneEmailUsuario_lanzaExcepcion() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                PasswordValidatorUtil.validar("usuario123!ABC", NOMBRE, EMAIL));
        assertTrue(ex.getReason().contains("nombre de usuario"),
                "El mensaje debe impedir que la contraseña contenga el usuario del email");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T08 – Partes del nombre personal prohibidas
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T08: Una contraseña que contiene un apellido del usuario (> 2 chars)
     * debe rechazarse para aumentar la seguridad.
     *
     * <p>Entrada: "Garcia123!XY" con nombre "Ana García López"</p>
     * <p>Salida esperada: {@link ResponseStatusException} con mención a nombre o apellidos</p>
     */
    @Test
    @DisplayName("T08 – Contraseña que contiene el apellido del usuario lanza excepción")
    void T08_contraseniaContieneNombre_lanzaExcepcion() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                PasswordValidatorUtil.validar("garcia123!XY", NOMBRE, EMAIL));
        assertTrue(ex.getReason().contains("nombre o apellidos"),
                "El mensaje debe impedir que la contraseña contenga apellidos del usuario");
    }
}
