package com.aulaclick.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecursoCrearDTO {
    private String nombre;
    private String tipo;
    private Integer capacidad;
    private String ubicacion;
    private String estado;
    private Integer idDepartamento;
}
