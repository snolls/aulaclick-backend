package com.aulaclick.dto;

import com.aulaclick.entity.Departamento;
import com.aulaclick.entity.Equipamiento;
import com.aulaclick.entity.Recurso;
import com.aulaclick.entity.TipoRecurso;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RecursoDTO {

    private Long idRecurso;
    private String nombre;
    private TipoRecurso tipoRecurso;
    private Integer capacidad;
    private String estado;
    private List<Equipamiento> equipamientos;
    private Departamento departamento;
    private String imagenUrl;
    private Boolean permiteFinesSemana;
    private LocalTime horaApertura;
    private LocalTime horaCierre;

    public static RecursoDTO fromEntity(Recurso r) {
        RecursoDTO dto = new RecursoDTO();
        dto.setIdRecurso(r.getIdRecurso());
        dto.setNombre(r.getNombre());
        dto.setTipoRecurso(r.getTipoRecurso());
        dto.setCapacidad(r.getCapacidad());
        dto.setEstado(r.getEstado());
        dto.setEquipamientos(r.getEquipamientos());
        dto.setDepartamento(r.getDepartamento());
        dto.setImagenUrl(r.getImagen() != null ? r.getImagen().getUrl() : null);
        dto.setPermiteFinesSemana(r.getPermiteFinesSemana());
        dto.setHoraApertura(r.getHoraApertura());
        dto.setHoraCierre(r.getHoraCierre());
        return dto;
    }
}
