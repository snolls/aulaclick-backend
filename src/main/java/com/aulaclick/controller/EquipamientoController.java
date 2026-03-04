package com.aulaclick.controller;

import com.aulaclick.entity.Equipamiento;
import com.aulaclick.repository.EquipamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
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
        return equipamientoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Equipamiento> crearEquipamiento(@RequestBody Equipamiento equipamiento) {
        Equipamiento saved = equipamientoRepository.save(equipamiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEquipamiento(@PathVariable Long id) {
        try {
            if (!equipamientoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
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
