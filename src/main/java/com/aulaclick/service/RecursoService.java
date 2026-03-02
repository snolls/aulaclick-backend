package com.aulaclick.service;

import com.aulaclick.entity.Recurso;
import com.aulaclick.repository.RecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecursoService {

    private final RecursoRepository recursoRepository;

    public List<Recurso> findAll() {
        return recursoRepository.findAll();
    }

    public Recurso save(Recurso recurso) {
        return recursoRepository.save(recurso);
    }
}
