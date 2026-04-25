package com.aulaclick.dto;

import com.aulaclick.entity.Departamento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartamentoDTO {

    private Long idDepartamento;
    private String nombre;

    public static DepartamentoDTO fromEntity(Departamento d) {
        if (d == null) return null;
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setIdDepartamento(d.getIdDepartamento());
        dto.setNombre(d.getNombre());
        return dto;
    }
}
