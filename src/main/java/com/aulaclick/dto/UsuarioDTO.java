package com.aulaclick.dto;

import com.aulaclick.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nombreCompleto;
    private String rol;
    private Long idRol;
    private Long sedeId;
    private String sedeNombre;

    public static UsuarioDTO fromEntity(Usuario u) {
        return new UsuarioDTO(
                u.getIdUsuario(),
                u.getEmail(),
                u.getNombreCompleto(),
                u.getRole() != null ? u.getRole().getNombreRol() : null,
                u.getRole() != null ? u.getRole().getIdRol() : null,
                u.getSede() != null ? u.getSede().getIdSede() : null,
                u.getSede() != null ? u.getSede().getNombre() : null
        );
    }
}
