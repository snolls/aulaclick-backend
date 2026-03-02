package com.aulaclick.controller;

import com.aulaclick.entity.Departamento;
import com.aulaclick.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
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
        return departamentoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Departamento> crearDepartamento(@RequestBody Departamento departamento) {
        Departamento saved = departamentoRepository.save(departamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
