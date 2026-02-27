package com.aulaclick.repository;

import com.aulaclick.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByRecurso_IdRecursoAndFecha(Long idRecurso, LocalDate fecha);
}
