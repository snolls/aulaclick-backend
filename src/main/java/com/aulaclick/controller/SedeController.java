package com.aulaclick.controller;

import com.aulaclick.dto.SedeDTO;
import com.aulaclick.entity.Sede;
import com.aulaclick.repository.RecursoRepository;
import com.aulaclick.repository.SedeRepository;
import com.aulaclick.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeRepository sedeRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public List<SedeDTO> listarSedes() {
        return sedeRepository.findAll().stream()
                .map(SedeDTO::fromEntity)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SedeDTO> crearSede(@RequestBody SedeDTO dto) {
        Sede sede = new Sede();
        sede.setNombre(dto.getNombre());
        sede.setCiudad(dto.getCiudad());
        sede.setDireccion(dto.getDireccion());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SedeDTO.fromEntity(sedeRepository.save(sede)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SedeDTO> actualizarSede(@PathVariable Long id, @RequestBody SedeDTO dto) {
        Optional<Sede> optionalSede = sedeRepository.findById(id);
        if (optionalSede.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Sede sede = optionalSede.get();
        sede.setNombre(dto.getNombre());
        sede.setCiudad(dto.getCiudad());
        sede.setDireccion(dto.getDireccion());

        return ResponseEntity.ok(SedeDTO.fromEntity(sedeRepository.save(sede)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> eliminarSede(@PathVariable Long id) {
        Optional<Sede> optionalSede = sedeRepository.findById(id);
        if (optionalSede.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        long usuariosCount = usuarioRepository.countBySedeIdSede(id);
        long recursosCount = recursoRepository.countBySedeIdSede(id);

        if (usuariosCount > 0 || recursosCount > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se puede eliminar la sede porque tiene usuarios (" + usuariosCount + ") o recursos (" + recursosCount + ") vinculados."));
        }

        sedeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
