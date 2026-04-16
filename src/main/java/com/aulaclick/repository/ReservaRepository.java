package com.aulaclick.repository;

import com.aulaclick.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByRecurso_IdRecursoAndFecha(Long idRecurso, LocalDate fecha);

    List<Reserva> findByRecurso_IdRecurso(Long idRecurso);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.recurso.idRecurso = :idRecurso AND r.fecha = :fecha AND (r.horaInicio < :horaFin AND r.horaFin > :horaInicio)")
    long contarSolapamientos(@Param("idRecurso") Long idRecurso, @Param("fecha") LocalDate fecha, @Param("horaInicio") LocalTime horaInicio, @Param("horaFin") LocalTime horaFin);
}
