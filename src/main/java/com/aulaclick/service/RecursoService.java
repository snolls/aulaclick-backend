package com.aulaclick.service;

import com.aulaclick.dto.RecursoDTO;
import com.aulaclick.entity.Recurso;
import com.aulaclick.entity.Reserva;
import com.aulaclick.repository.RecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecursoService {

    private final RecursoRepository recursoRepository;

    @Lazy
    @Autowired
    private ReservaService reservaService;

    @Transactional(readOnly = true)
    public RecursoDTO toRecursoDTO(Recurso r) {
        RecursoDTO dto = RecursoDTO.fromEntity(r);
        if (r.getReservas() != null) {
            dto.setReservas(r.getReservas().stream()
                    .map(reservaService::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<Recurso> findAll() {
        return recursoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Recurso> findBySede(Long idSede) {
        return recursoRepository.findBySedeIdSede(idSede);
    }

    public Recurso save(Recurso recurso) {
        return recursoRepository.save(recurso);
    }

    @Transactional
    public Recurso actualizarRecurso(Recurso recurso) {
        if (recurso.getEstado() != null && recurso.getEstado().toLowerCase().contains("no disponible")) {
            if (recurso.getReservas() != null) {
                for (Reserva reserva : recurso.getReservas()) {
                    if ("ACTIVA".equalsIgnoreCase(reserva.getEstado())) {
                        reserva.setEstado("CANCELADA");
                        reserva.setMotivo("Cancelación automática: El recurso ha pasado a estado No Disponible.");
                    }
                }
            }
        }
        return recursoRepository.save(recurso);
    }

    @Transactional(readOnly = true)
    public Optional<Recurso> findById(Long id) {
        return recursoRepository.findById(id);
    }

    @Transactional
    public void eliminarRecurso(Long id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        if (recurso.getEquipamientos() != null) {
            recurso.getEquipamientos().clear();
        }
        recursoRepository.delete(recurso);
    }

}
