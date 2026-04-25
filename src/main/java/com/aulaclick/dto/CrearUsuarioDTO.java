package com.aulaclick.dto;

import lombok.Data;

@Data
public class CrearUsuarioDTO {
    private String email;
    private String nombreCompleto;
    private String password;
    private Long idRol;
    private Long idSede;
}
