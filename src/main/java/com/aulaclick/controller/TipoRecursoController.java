package com.aulaclick.controller;

import com.aulaclick.entity.TipoRecurso;
import com.aulaclick.repository.TipoRecursoRepository;
import com.aulaclick.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-recurso")
@RequiredArgsConstructor
public class TipoRecursoController {

    private final TipoRecursoRepository tipoRecursoRepository;

    @GetMapping
    public List<TipoRecurso> getAllTiposRecurso() {
        String rol = SecurityUtils.getRol();
        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            return tipoRecursoRepository.findBySedeId(sedeId);
        }
        return tipoRecursoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoRecurso> getTipoRecursoById(@PathVariable Long id) {
        return tipoRecursoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoRecurso> crearTipoRecurso(@RequestBody TipoRecurso tipoRecurso) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if ("ADMIN_SEDE".equals(rol)) {
            tipoRecurso.setSedeId(SecurityUtils.getSedeIdOrForbidden());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoRecursoRepository.save(tipoRecurso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoRecurso> actualizarTipoRecurso(@PathVariable Long id,
                                                               @RequestBody TipoRecurso tipoRecursoDetalles) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return tipoRecursoRepository.findById(id)
                .map(tipo -> {
                    if ("ADMIN_SEDE".equals(rol)) {
                        Long sedeId = SecurityUtils.getSedeIdOrForbidden();
                        if (!sedeId.equals(tipo.getSedeId())) {
                            throw new org.springframework.web.server.ResponseStatusException(
                                    HttpStatus.FORBIDDEN, "Solo puedes editar tipos de tu propia sede.");
                        }
                    }
                    tipo.setNombre(tipoRecursoDetalles.getNombre());
                    tipo.setImagenUrl(tipoRecursoDetalles.getImagenUrl());
                    return ResponseEntity.ok(tipoRecursoRepository.save(tipo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTipoRecurso(@PathVariable Long id) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        TipoRecurso tipo = tipoRecursoRepository.findById(id).orElse(null);
        if (tipo == null) return ResponseEntity.notFound().build();
        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            if (!sedeId.equals(tipo.getSedeId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        try {
            tipoRecursoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el tipo de recurso está en uso.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
