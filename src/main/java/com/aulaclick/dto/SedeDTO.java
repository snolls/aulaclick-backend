package com.aulaclick.dto;

import com.aulaclick.entity.Sede;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SedeDTO {
    private Long id;
    private String nombre;
    private String ciudad;
    private String direccion;

    public static SedeDTO fromEntity(Sede s) {
        return new SedeDTO(s.getIdSede(), s.getNombre(), s.getCiudad(), s.getDireccion());
    }
}
