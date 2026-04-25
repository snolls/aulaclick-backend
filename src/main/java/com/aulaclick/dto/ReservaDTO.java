package com.aulaclick.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaDTO {

    private Long idReserva;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaFin;

    private String motivo;

    private Long idUsuario;
    private String nombreUsuario;

    private Long idRecurso;
    private String nombreRecurso;
    private String imagenUrl;

    private String estado;
}
