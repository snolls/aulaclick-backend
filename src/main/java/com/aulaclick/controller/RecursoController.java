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
    public ResponseEntity<Recurso> crearRecurso(@RequestBody RecursoCrearDTO dto) {
        Recurso recurso = new Recurso();
        recurso.setNombre(dto.getNombre());
        recurso.setCapacidad(dto.getCapacidad());
        recurso.setUbicacion(dto.getUbicacion());
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
}
