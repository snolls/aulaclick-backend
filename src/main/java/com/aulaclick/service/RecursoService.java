package com.aulaclick.service;

import com.aulaclick.entity.Recurso;
import com.aulaclick.repository.RecursoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecursoService {

    private final RecursoRepository recursoRepository;

    @Transactional(readOnly = true)
    public List<Recurso> findAll() {
        return recursoRepository.findAll();
    }

    public Recurso save(Recurso recurso) {
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
