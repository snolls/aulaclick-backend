package com.aulaclick.controller;

import com.aulaclick.entity.Departamento;
import com.aulaclick.repository.DepartamentoRepository;
import com.aulaclick.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
public class DepartamentoController {

    private final DepartamentoRepository departamentoRepository;

    @GetMapping
    public List<Departamento> getAllDepartamentos() {
        String rol = SecurityUtils.getRol();
        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            return departamentoRepository.findBySedeId(sedeId);
        }
        return departamentoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Departamento> crearDepartamento(@RequestBody Departamento departamento) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if ("ADMIN_SEDE".equals(rol)) {
            departamento.setSedeId(SecurityUtils.getSedeIdOrForbidden());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(departamentoRepository.save(departamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDepartamento(@PathVariable Long id) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Departamento departamento = departamentoRepository.findById(id).orElse(null);
        if (departamento == null) return ResponseEntity.notFound().build();
        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            if (!sedeId.equals(departamento.getSedeId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        try {
            departamentoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el departamento está en uso.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
