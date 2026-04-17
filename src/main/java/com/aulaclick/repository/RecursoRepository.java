package com.aulaclick.repository;

import com.aulaclick.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    @Query("SELECT DISTINCT r.imagenUrl FROM Recurso r WHERE r.imagenUrl IS NOT NULL AND r.imagenUrl != ''")
    List<String> findDistinctImagenes();
}
