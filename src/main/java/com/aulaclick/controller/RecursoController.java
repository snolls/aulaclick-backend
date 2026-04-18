package com.aulaclick.controller;

import com.aulaclick.dto.ImagenRequestDTO;
import com.aulaclick.dto.RecursoDTO;
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
    public List<RecursoDTO> getAllRecursos() {
        return recursoService.findAll().stream()
                .map(RecursoDTO::fromEntity)
                .collect(Collectors.toList());
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<RecursoDTO> getRecursoById(@PathVariable Long id) {
        return recursoService.findById(id)
                .map(RecursoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RecursoDTO> crearRecurso(@RequestBody RecursoCrearDTO dto, @RequestHeader(value = "X-Rol-Usuario", required = false) String rolUsuario) {
        if (rolUsuario == null || (!rolUsuario.equals("1") && !rolUsuario.equalsIgnoreCase("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Recurso recurso = new Recurso();
        recurso.setNombre(dto.getNombre());
        recurso.setCapacidad(dto.getCapacidad());
        recurso.setEstado(dto.getEstado());
        recurso.setPermiteFinesSemana(dto.getPermiteFinesSemana() != null ? dto.getPermiteFinesSemana() : false);
        recurso.setHoraApertura(dto.getHoraApertura() != null ? dto.getHoraApertura() : LocalTime.of(8, 0));
        recurso.setHoraCierre(dto.getHoraCierre() != null ? dto.getHoraCierre() : LocalTime.of(21, 0));

        if (dto.getIdImagen() != null) {
            ImagenGaleria imagen = imagenGaleriaRepository.findById(dto.getIdImagen())
                    .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
            recurso.setImagen(imagen);
        } else {
            recurso.setImagen(null);
        }

        TipoRecurso tipo = tipoRecursoRepository.findById(dto.getIdTipoRecurso()).orElseThrow();
        Departamento depto = departamentoRepository.findById(dto.getIdDepartamento()).orElseThrow();
        List<Equipamiento> equip = equipamientoRepository.findAllById(
                dto.getIdsEquipamientos() == null ? List.of()
                        : dto.getIdsEquipamientos());
        recurso.setTipoRecurso(tipo);
        recurso.setDepartamento(depto);
        recurso.setEquipamientos(equip);

        return ResponseEntity.status(HttpStatus.CREATED).body(RecursoDTO.fromEntity(recursoService.save(recurso)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurso(@PathVariable Long id, @RequestHeader(value = "X-Rol-Usuario", required = false) String rolUsuario) {
        if (rolUsuario == null || (!rolUsuario.equals("1") && !rolUsuario.equalsIgnoreCase("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            recursoService.eliminarRecurso(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoDTO> actualizarRecurso(@PathVariable Long id, @RequestBody RecursoCrearDTO dto, @RequestHeader(value = "X-Rol-Usuario", required = false) String rolUsuario) {
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
        recurso.setPermiteFinesSemana(dto.getPermiteFinesSemana() != null ? dto.getPermiteFinesSemana() : false);
        recurso.setHoraApertura(dto.getHoraApertura() != null ? dto.getHoraApertura() : LocalTime.of(8, 0));
        recurso.setHoraCierre(dto.getHoraCierre() != null ? dto.getHoraCierre() : LocalTime.of(21, 0));

        if (dto.getIdImagen() != null) {
            ImagenGaleria imagen = imagenGaleriaRepository.findById(dto.getIdImagen())
                    .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
            recurso.setImagen(imagen);
        } else {
            recurso.setImagen(null);
        }

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

            return ResponseEntity.ok(RecursoDTO.fromEntity(recursoService.save(recurso)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
