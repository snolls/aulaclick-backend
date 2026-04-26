package com.aulaclick.dto;

import lombok.Data;

@Data
public class ActualizarRolSedeDTO {
    private Long   idRol;
    private Long   idSede;
    private String nombreCompleto;
    private String email;
}
