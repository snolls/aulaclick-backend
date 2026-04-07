package com.aulaclick.controller;

import com.aulaclick.entity.Recurso;
import com.aulaclick.service.RecursoService;
import lombok.RequiredArgsConstructor;
import com.aulaclick.dto.RecursoCrearDTO;
import com.aulaclick.entity.Departamento;
import com.aulaclick.entity.TipoRecurso;
import com.aulaclick.entity.Equipamiento;
import com.aulaclick.repository.DepartamentoRepository;
import com.aulaclick.repository.TipoRecursoRepository;
import com.aulaclick.repository.EquipamientoRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {

    private final RecursoService recursoService;
    private final DepartamentoRepository departamentoRepository;
    private final TipoRecursoRepository tipoRecursoRepository;
    private final EquipamientoRepository equipamientoRepository;

    @GetMapping
    public List<Recurso> getAllRecursos() {
        return recursoService.findAll();
    }

    @PostMapping
    public ResponseEntity<Recurso> crearRecurso(@RequestBody RecursoCrearDTO dto, @RequestHeader(value = "X-Rol-Usuario", required = false) String rolUsuario) {
        if (rolUsuario == null || (!rolUsuario.equals("1") && !rolUsuario.equalsIgnoreCase("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Recurso recurso = new Recurso();
        recurso.setNombre(dto.getNombre());
        recurso.setCapacidad(dto.getCapacidad());
        recurso.setEstado(dto.getEstado());

        TipoRecurso tipo = tipoRecursoRepository.findById(dto.getIdTipoRecurso().longValue()).orElseThrow();
        Departamento depto = departamentoRepository.findById(dto.getIdDepartamento().longValue()).orElseThrow();
        List<Equipamiento> equip = equipamientoRepository.findAllById(
                dto.getIdsEquipamiento() == null ? List.of()
                        : dto.getIdsEquipamiento().stream().map(Integer::longValue).toList());
        recurso.setTipoRecurso(tipo);
        recurso.setDepartamento(depto);
        recurso.setEquipamientos(equip);

        Recurso recursoGuardado = recursoService.save(recurso);
        return ResponseEntity.status(HttpStatus.CREATED).body(recursoGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurso(@PathVariable Long id, @RequestHeader(value = "X-Rol-Usuario", required = false) String rolUsuario) {
        if (rolUsuario == null || (!rolUsuario.equals("1") && !rolUsuario.equalsIgnoreCase("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            recursoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recurso> actualizarRecurso(@PathVariable Long id, @RequestBody RecursoCrearDTO dto, @RequestHeader(value = "X-Rol-Usuario", required = false) String rolUsuario) {
        if (rolUsuario == null || (!rolUsuario.equals("1") && !rolUsuario.equalsIgnoreCase("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Recurso recurso = recursoService.findById(id).orElse(null);
        if (recurso == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        recurso.setNombre(dto.getNombre());
        recurso.setCapacidad(dto.getCapacidad());

        try {
            TipoRecurso tipo = tipoRecursoRepository.findById(dto.getIdTipoRecurso().longValue())
                    .orElseThrow(() -> new RuntimeException("Tipo Recurso no encontrado"));
            Departamento depto = departamentoRepository.findById(dto.getIdDepartamento().longValue())
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
            List<Equipamiento> equip = equipamientoRepository.findAllById(
                    dto.getIdsEquipamiento() == null ? List.of()
                            : dto.getIdsEquipamiento().stream().map(Integer::longValue).toList());

            recurso.setTipoRecurso(tipo);
            recurso.setDepartamento(depto);
            recurso.setEquipamientos(equip);

            Recurso recursoActualizado = recursoService.save(recurso);
            return ResponseEntity.ok(recursoActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
