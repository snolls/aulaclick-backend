package com.aulaclick.controller;

import com.aulaclick.dto.EstadisticasDTO;
import com.aulaclick.repository.RecursoRepository;
import com.aulaclick.repository.ReservaRepository;
import com.aulaclick.repository.UsuarioRepository;
import com.aulaclick.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;
    private final ReservaRepository reservaRepository;

    @GetMapping("/estadisticas")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public ResponseEntity<EstadisticasDTO> getEstadisticas() {
        String rol = SecurityUtils.getRol();
        EstadisticasDTO dto = new EstadisticasDTO();

        if ("ADMIN".equals(rol)) {
            dto.setTotalUsuarios(usuarioRepository.count());
            dto.setTotalRecursos(recursoRepository.count());
            dto.setTotalReservasActivas(reservaRepository.countByEstado("ACTIVA"));
            dto.setTotalReservasCanceladas(reservaRepository.countByEstado("CANCELADA"));
        } else {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            dto.setTotalUsuarios(usuarioRepository.countBySedeIdSede(sedeId));
            dto.setTotalRecursos(recursoRepository.countBySedeIdSede(sedeId));
            dto.setTotalReservasActivas(reservaRepository.countByEstadoAndRecurso_Sede_IdSede("ACTIVA", sedeId));
            dto.setTotalReservasCanceladas(reservaRepository.countByEstadoAndRecurso_Sede_IdSede("CANCELADA", sedeId));
        }

        return ResponseEntity.ok(dto);
    }
}
