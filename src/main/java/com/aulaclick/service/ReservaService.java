package com.aulaclick.service;

import com.aulaclick.entity.Reserva;
import com.aulaclick.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public Reserva createReserva(Reserva nuevaReserva) {
        if (nuevaReserva.getRecurso() == null || nuevaReserva.getRecurso().getIdRecurso() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere el ID del recurso");
        }

        LocalDate fecha = nuevaReserva.getFecha();
        LocalTime horaInicio = nuevaReserva.getHoraInicio();
        LocalTime horaFin = nuevaReserva.getHoraFin();

        // 1. Viajes en el tiempo
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a hoy.");
        }
        if (fecha.isEqual(LocalDate.now()) && horaInicio.isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("La hora de inicio no puede ser en el pasado.");
        }

        // 2. Lógica de horas
        if (!horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }

        // 3. Fines de semana
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No se pueden hacer reservas en fines de semana.");
        }

        // 4. Horario de apertura (08:00 a 21:00)
        if (horaInicio.isBefore(LocalTime.of(8, 0)) || horaFin.isAfter(LocalTime.of(21, 0))) {
            throw new IllegalArgumentException("La reserva debe estar dentro del horario de apertura (08:00 - 21:00).");
        }

        // 5. Solapamientos
        long solapamientos = reservaRepository.contarSolapamientos(
                nuevaReserva.getRecurso().getIdRecurso(),
                fecha,
                horaInicio,
                horaFin
        );

        if (solapamientos > 0) {
            throw new IllegalArgumentException("La sala ya está reservada en ese horario");
        }

        return reservaRepository.save(nuevaReserva);
    }
}
