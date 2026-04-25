package com.aulaclick.dto;

import com.aulaclick.entity.Equipamiento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipamientoDTO {

    private Long idEquipamiento;
    private String nombre;

    public static EquipamientoDTO fromEntity(Equipamiento e) {
        if (e == null) return null;
        EquipamientoDTO dto = new EquipamientoDTO();
        dto.setIdEquipamiento(e.getIdEquipamiento());
        dto.setNombre(e.getNombre());
        return dto;
    }
}
