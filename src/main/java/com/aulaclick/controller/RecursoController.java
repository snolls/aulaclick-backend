package com.aulaclick.controller;

import com.aulaclick.dto.ImagenRequestDTO;
import com.aulaclick.dto.RecursoCrearDTO;
import com.aulaclick.dto.RecursoDTO;
import com.aulaclick.entity.*;
import com.aulaclick.repository.*;
import com.aulaclick.security.SecurityUtils;
import com.aulaclick.service.GaleriaService;
import com.aulaclick.service.RecursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {

    private final RecursoService recursoService;
    private final GaleriaService galeriaService;
    private final ImagenGaleriaRepository imagenGaleriaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final TipoRecursoRepository tipoRecursoRepository;
    private final EquipamientoRepository equipamientoRepository;
    private final SedeRepository sedeRepository;

    @GetMapping
    public List<RecursoDTO> getAllRecursos() {
        String rol = SecurityUtils.getRol();
        List<Recurso> recursos;

        if ("ADMIN".equals(rol)) {
            recursos = recursoService.findAll();
        } else {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            recursos = recursoService.findBySede(sedeId);
        }

        return recursos.stream()
                .map(recursoService::toRecursoDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoDTO> getRecursoById(@PathVariable Long id) {
        return recursoService.findById(id)
                .map(recursoService::toRecursoDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RecursoDTO> crearRecurso(@RequestBody RecursoCrearDTO dto) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Recurso recurso = buildRecursoFromDTO(dto);

        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            Sede sede = sedeRepository.findById(sedeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Sede no encontrada."));
            recurso.setSede(sede);
        } else if (dto.getIdSede() != null) {
            sedeRepository.findById(dto.getIdSede()).ifPresent(recurso::setSede);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recursoService.toRecursoDTO(recursoService.save(recurso)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoDTO> actualizarRecurso(@PathVariable Long id, @RequestBody RecursoCrearDTO dto) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Recurso recurso = recursoService.findById(id)
                .orElse(null);
        if (recurso == null) return ResponseEntity.notFound().build();

        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            if (recurso.getSede() == null || !sedeId.equals(recurso.getSede().getIdSede())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        recurso.setNombre(dto.getNombre());
        recurso.setCapacidad(dto.getCapacidad());
        recurso.setEstado(dto.getEstado());
        recurso.setPermiteFinesSemana(dto.getPermiteFinesSemana() != null ? dto.getPermiteFinesSemana() : false);
        recurso.setHoraApertura(dto.getHoraApertura() != null ? dto.getHoraApertura() : LocalTime.of(8, 0));
        recurso.setHoraCierre(dto.getHoraCierre() != null ? dto.getHoraCierre() : LocalTime.of(21, 0));

        if (dto.getIdImagen() != null) {
            imagenGaleriaRepository.findById(dto.getIdImagen()).ifPresent(recurso::setImagen);
        }

        try {
            TipoRecurso tipo = tipoRecursoRepository.findById(dto.getIdTipoRecurso()).orElseThrow();
            Departamento depto = departamentoRepository.findById(dto.getIdDepartamento()).orElseThrow();
            List<Equipamiento> equip = equipamientoRepository.findAllById(
                    dto.getIdsEquipamientos() == null ? List.of() : dto.getIdsEquipamientos());
            recurso.setTipoRecurso(tipo);
            recurso.setDepartamento(depto);
            recurso.setEquipamientos(equip);
            return ResponseEntity.ok(recursoService.toRecursoDTO(recursoService.actualizarRecurso(recurso)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurso(@PathVariable Long id) {
        String rol = SecurityUtils.getRol();
        if (!"ADMIN".equals(rol) && !"ADMIN_SEDE".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("ADMIN_SEDE".equals(rol)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            Recurso recurso = recursoService.findById(id).orElse(null);
            if (recurso == null || recurso.getSede() == null || !sedeId.equals(recurso.getSede().getIdSede())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        try {
            recursoService.eliminarRecurso(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // --- Galería ---

    @GetMapping("/imagenes")
    public ResponseEntity<List<ImagenGaleria>> getImagenesExistentes() {
        return ResponseEntity.ok(imagenGaleriaRepository.findAll());
    }

    @PostMapping("/imagenes")
    public ResponseEntity<ImagenGaleria> guardarImagen(@RequestBody ImagenRequestDTO dto) {
        String url = dto.getUrl();
        return imagenGaleriaRepository.findByUrl(url)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ImagenGaleria guardada = imagenGaleriaRepository.save(new ImagenGaleria(url));
                    return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
                });
    }

    @PostMapping("/imagenes/batch")
    public ResponseEntity<List<ImagenGaleria>> registrarImagenesMasivo(@RequestBody List<ImagenRequestDTO> dtos) {
        return ResponseEntity.ok(galeriaService.registrarImagenesMasivo(dtos));
    }

    @DeleteMapping("/imagenes/{id}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long id) {
        galeriaService.eliminarImagen(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/imagenes")
    public ResponseEntity<Void> eliminarImagenes(@RequestParam List<Long> ids) {
        galeriaService.eliminarImagenesMasivo(ids);
        return ResponseEntity.noContent().build();
    }

    // --- Helpers ---

    private Recurso buildRecursoFromDTO(RecursoCrearDTO dto) {
        Recurso recurso = new Recurso();
        recurso.setNombre(dto.getNombre());
        recurso.setCapacidad(dto.getCapacidad());
        recurso.setEstado(dto.getEstado());
        recurso.setPermiteFinesSemana(dto.getPermiteFinesSemana() != null ? dto.getPermiteFinesSemana() : false);
        recurso.setHoraApertura(dto.getHoraApertura() != null ? dto.getHoraApertura() : LocalTime.of(8, 0));
        recurso.setHoraCierre(dto.getHoraCierre() != null ? dto.getHoraCierre() : LocalTime.of(21, 0));

        if (dto.getIdImagen() != null) {
            imagenGaleriaRepository.findById(dto.getIdImagen()).ifPresent(recurso::setImagen);
        }

        TipoRecurso tipo = tipoRecursoRepository.findById(dto.getIdTipoRecurso()).orElseThrow();
        Departamento depto = departamentoRepository.findById(dto.getIdDepartamento()).orElseThrow();
        List<Equipamiento> equip = equipamientoRepository.findAllById(
                dto.getIdsEquipamientos() == null ? List.of() : dto.getIdsEquipamientos());
        recurso.setTipoRecurso(tipo);
        recurso.setDepartamento(depto);
        recurso.setEquipamientos(equip);
        return recurso;
    }
}
