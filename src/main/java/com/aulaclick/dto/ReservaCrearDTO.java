package com.aulaclick.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaCrearDTO {
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String motivo;
    private Long idRecurso;
    private Long idUsuario;
}
