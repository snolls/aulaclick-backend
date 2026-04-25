package com.aulaclick.repository;

import com.aulaclick.entity.TipoRecurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoRecursoRepository extends JpaRepository<TipoRecurso, Long> {
    List<TipoRecurso> findBySedeId(Long sedeId);
}
