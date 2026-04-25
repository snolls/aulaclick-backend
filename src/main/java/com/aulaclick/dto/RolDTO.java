package com.aulaclick.dto;

import com.aulaclick.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RolDTO {
    private Long idRol;
    private String nombre;

    public static RolDTO fromEntity(Role r) {
        return new RolDTO(r.getIdRol(), r.getNombreRol());
    }
}
