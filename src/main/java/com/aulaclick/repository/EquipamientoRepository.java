package com.aulaclick.repository;

import com.aulaclick.entity.Equipamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipamientoRepository extends JpaRepository<Equipamiento, Long> {
    List<Equipamiento> findBySedeId(Long sedeId);
}
