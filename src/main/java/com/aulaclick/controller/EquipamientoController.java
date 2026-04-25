package com.aulaclick.controller;

import com.aulaclick.entity.Equipamiento;
import com.aulaclick.repository.EquipamientoRepository;
import com.aulaclick.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipamientos")
@RequiredArgsConstructor
public class EquipamientoController {

    private final EquipamientoRepository equipamientoRepository;

    @GetMapping
    public List<Equipamiento> getAllEquipamientos() {
        String rol = SecurityUtils.getRol();
        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            return equipamientoRepository.findBySedeId(sedeId);
        }
        return equipamientoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipamiento> getEquipamientoById(@PathVariable Long id) {
        return equipamientoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Equipamiento> crearEquipamiento(@RequestBody Equipamiento equipamiento) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if ("ADMIN_SEDE".equals(rol)) {
            equipamiento.setSedeId(SecurityUtils.getSedeIdOrForbidden());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(equipamientoRepository.save(equipamiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEquipamiento(@PathVariable Long id) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Equipamiento equipamiento = equipamientoRepository.findById(id).orElse(null);
        if (equipamiento == null) return ResponseEntity.notFound().build();
        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            if (!sedeId.equals(equipamiento.getSedeId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        try {
            equipamientoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el equipamiento está en uso.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
