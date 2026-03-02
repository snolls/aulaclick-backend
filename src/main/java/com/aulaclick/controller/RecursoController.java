package com.aulaclick.controller;

import com.aulaclick.entity.Recurso;
import com.aulaclick.service.RecursoService;
import lombok.RequiredArgsConstructor;
import com.aulaclick.dto.RecursoCrearDTO;
import com.aulaclick.entity.Departamento;
import com.aulaclick.repository.DepartamentoRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {

    private final RecursoService recursoService;
    private final DepartamentoRepository departamentoRepository;

    @GetMapping
    public List<Recurso> getAllRecursos() {
        return recursoService.findAll();
    }

    @PostMapping
    public ResponseEntity<Recurso> crearRecurso(@RequestBody RecursoCrearDTO dto) {
        Recurso recurso = new Recurso();
        recurso.setNombre(dto.getNombre());
        recurso.setTipo(dto.getTipo());
        recurso.setCapacidad(dto.getCapacidad());
        recurso.setUbicacion(dto.getUbicacion());
        recurso.setEstado(dto.getEstado());

        Departamento departamento = departamentoRepository.findById(Long.valueOf(dto.getIdDepartamento()))
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
        recurso.setDepartamento(departamento);

        Recurso recursoGuardado = recursoService.save(recurso);
        return ResponseEntity.status(HttpStatus.CREATED).body(recursoGuardado);
    }
}
