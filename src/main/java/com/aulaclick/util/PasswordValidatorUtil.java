package com.aulaclick.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PasswordValidatorUtil {

    public static void validar(String password, String nombreCompleto, String email) {
        if (password == null || password.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 12 caracteres.");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe incluir al menos un número.");
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe incluir al menos una letra (sin acentos ni eñes).");
        }
        if (password.contains("@") || password.contains("\"")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede contener '@' ni comillas (\").");
        }
        if (!password.matches(".*[!#$%&'()*+,-./:;<=>?\\[\\]^_`{|}~].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe incluir al menos un símbolo especial permitido.");
        }

        String passLower = password.toLowerCase();
        if (email != null && passLower.contains(email.split("@")[0].toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede contener tu nombre de usuario/email.");
        }
        if (nombreCompleto != null) {
            for (String parte : nombreCompleto.toLowerCase().split(" ")) {
                if (parte.length() > 2 && passLower.contains(parte)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede contener tu nombre o apellidos.");
                }
            }
        }
    }
}
