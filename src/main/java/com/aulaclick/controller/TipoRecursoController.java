package com.aulaclick.controller;

import com.aulaclick.entity.TipoRecurso;
import com.aulaclick.repository.TipoRecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
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
        TipoRecurso saved = tipoRecursoRepository.save(tipoRecurso);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoRecurso> actualizarTipoRecurso(@PathVariable Long id, @RequestBody TipoRecurso tipoRecursoDetalles) {
        return tipoRecursoRepository.findById(id)
                .map(tipo -> {
                    tipo.setNombre(tipoRecursoDetalles.getNombre());
                    tipo.setImagenUrl(tipoRecursoDetalles.getImagenUrl());
                    return ResponseEntity.ok(tipoRecursoRepository.save(tipo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTipoRecurso(@PathVariable Long id) {
        try {
            if (!tipoRecursoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
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
