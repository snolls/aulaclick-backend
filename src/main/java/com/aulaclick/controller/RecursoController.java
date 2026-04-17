package com.aulaclick.controller;

import com.aulaclick.entity.ImagenGaleria;
import com.aulaclick.entity.Recurso;
import com.aulaclick.repository.ImagenGaleriaRepository;
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

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {

    private final RecursoService recursoService;
    private final ImagenGaleriaRepository imagenGaleriaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final TipoRecursoRepository tipoRecursoRepository;
    private final EquipamientoRepository equipamientoRepository;

    @GetMapping
    public List<Recurso> getAllRecursos() {
        return recursoService.findAll();
    }

    @GetMapping("/imagenes")
    public ResponseEntity<List<String>> getImagenesExistentes() {
        List<String> imagenes = imagenGaleriaRepository.findAll()
                .stream()
                .map(ImagenGaleria::getUrl)
                .collect(Collectors.toList());
        return ResponseEntity.ok(imagenes);
    }

    @PostMapping("/imagenes")
    public ResponseEntity<String> guardarImagen(@RequestBody String url) {
        String urlLimpia = url.trim().replaceAll("^\"|\"$", "");
        if (imagenGaleriaRepository.findByUrl(urlLimpia).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La imagen ya existe en la galería");
        }
        imagenGaleriaRepository.save(new ImagenGaleria(urlLimpia));
        return ResponseEntity.status(HttpStatus.CREATED).body(urlLimpia);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recurso> getRecursoById(@PathVariable Long id) {
        return recursoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
        recurso.setImagenUrl(dto.getImagenUrl());
        recurso.setPermiteFinesSemana(dto.getPermiteFinesSemana() != null ? dto.getPermiteFinesSemana() : false);
        recurso.setHoraApertura(dto.getHoraApertura() != null ? dto.getHoraApertura() : LocalTime.of(8, 0));
        recurso.setHoraCierre(dto.getHoraCierre() != null ? dto.getHoraCierre() : LocalTime.of(21, 0));

        TipoRecurso tipo = tipoRecursoRepository.findById(dto.getIdTipoRecurso()).orElseThrow();
        Departamento depto = departamentoRepository.findById(dto.getIdDepartamento()).orElseThrow();
        List<Equipamiento> equip = equipamientoRepository.findAllById(
                dto.getIdsEquipamientos() == null ? List.of()
                        : dto.getIdsEquipamientos());
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
        recurso.setEstado(dto.getEstado());
        recurso.setImagenUrl(dto.getImagenUrl());
        recurso.setPermiteFinesSemana(dto.getPermiteFinesSemana() != null ? dto.getPermiteFinesSemana() : false);
        recurso.setHoraApertura(dto.getHoraApertura() != null ? dto.getHoraApertura() : LocalTime.of(8, 0));
        recurso.setHoraCierre(dto.getHoraCierre() != null ? dto.getHoraCierre() : LocalTime.of(21, 0));

        try {
            TipoRecurso tipo = tipoRecursoRepository.findById(dto.getIdTipoRecurso())
                    .orElseThrow(() -> new RuntimeException("Tipo Recurso no encontrado"));
            Departamento depto = departamentoRepository.findById(dto.getIdDepartamento())
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
            List<Equipamiento> equip = equipamientoRepository.findAllById(
                    dto.getIdsEquipamientos() == null ? List.of()
                            : dto.getIdsEquipamientos());

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
