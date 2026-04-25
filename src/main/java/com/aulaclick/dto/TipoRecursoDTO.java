package com.aulaclick.dto;

import com.aulaclick.entity.TipoRecurso;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoRecursoDTO {

    private Long idTipoRecurso;
    private String nombre;

    public static TipoRecursoDTO fromEntity(TipoRecurso t) {
        if (t == null) return null;
        TipoRecursoDTO dto = new TipoRecursoDTO();
        dto.setIdTipoRecurso(t.getIdTipoRecurso());
        dto.setNombre(t.getNombre());
        return dto;
    }
}
