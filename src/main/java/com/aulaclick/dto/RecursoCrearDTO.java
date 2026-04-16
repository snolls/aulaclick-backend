package com.aulaclick.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RecursoCrearDTO {
    private String nombre;
    private Long idTipoRecurso;
    private Integer capacidad;
    private String estado;
    private List<Long> idsEquipamientos;
    private Long idDepartamento;
    private String imagenUrl;
    private Boolean permiteFinesSemana;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
}
