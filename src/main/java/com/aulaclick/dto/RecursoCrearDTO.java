package com.aulaclick.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RecursoCrearDTO {
    private String nombre;
    private Integer idTipoRecurso;
    private Integer capacidad;
    private String ubicacion;
    private String estado;
    private List<Integer> idsEquipamiento;
    private Integer idDepartamento;
}
