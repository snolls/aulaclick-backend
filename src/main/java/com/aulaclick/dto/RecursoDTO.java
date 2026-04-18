package com.aulaclick.dto;

import com.aulaclick.entity.Recurso;
import com.aulaclick.entity.Reserva;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RecursoDTO {

    private Long idRecurso;
    private String nombre;
    private TipoRecursoDTO tipoRecurso;
    private Integer capacidad;
    private String estado;
    private List<EquipamientoDTO> equipamientos;
    private DepartamentoDTO departamento;
    private String imagenUrl;
    private Boolean permiteFinesSemana;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private List<ReservaDTO> reservas;

    private static ReservaDTO reservaToDTO(Reserva r) {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(r.getIdReserva());
        dto.setFecha(r.getFecha());
        dto.setHoraInicio(r.getHoraInicio());
        dto.setHoraFin(r.getHoraFin());
        dto.setMotivo(r.getMotivo());
        dto.setEstado(r.getEstado());
        if (r.getUsuario() != null) {
            dto.setIdUsuario(r.getUsuario().getIdUsuario());
            dto.setNombreUsuario(r.getUsuario().getNombreCompleto());
        }
        return dto;
    }

    public static RecursoDTO fromEntity(Recurso r) {
        RecursoDTO dto = new RecursoDTO();
        dto.setIdRecurso(r.getIdRecurso());
        dto.setNombre(r.getNombre());
        dto.setTipoRecurso(TipoRecursoDTO.fromEntity(r.getTipoRecurso()));
        dto.setCapacidad(r.getCapacidad());
        dto.setEstado(r.getEstado());
        dto.setEquipamientos(r.getEquipamientos() == null ? List.of() :
                r.getEquipamientos().stream().map(EquipamientoDTO::fromEntity).collect(Collectors.toList()));
        dto.setDepartamento(DepartamentoDTO.fromEntity(r.getDepartamento()));
        dto.setImagenUrl(r.getImagen() != null ? r.getImagen().getUrl() : null);
        dto.setPermiteFinesSemana(r.getPermiteFinesSemana());
        dto.setHoraApertura(r.getHoraApertura());
        dto.setHoraCierre(r.getHoraCierre());
        if (r.getReservas() != null) {
            dto.setReservas(r.getReservas().stream().map(RecursoDTO::reservaToDTO).collect(Collectors.toList()));
        }
        return dto;
    }
}
