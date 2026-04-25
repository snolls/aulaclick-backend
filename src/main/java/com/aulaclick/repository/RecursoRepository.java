package com.aulaclick.repository;

import com.aulaclick.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    List<Recurso> findBySedeIdSede(Long idSede);

    long countBySedeIdSede(Long idSede);
}
