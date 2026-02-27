package com.aulaclick.service;

import com.aulaclick.entity.Reserva;
import com.aulaclick.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public Reserva createReserva(Reserva nuevaReserva) {
        if (nuevaReserva.getRecurso() == null || nuevaReserva.getRecurso().getIdRecurso() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere el ID del recurso");
        }

        List<Reserva> reservasExistentes = reservaRepository.findByRecurso_IdRecursoAndFecha(
                nuevaReserva.getRecurso().getIdRecurso(),
                nuevaReserva.getFecha());

        for (Reserva existente : reservasExistentes) {
            boolean solapa = nuevaReserva.getHoraInicio().isBefore(existente.getHoraFin()) &&
                    nuevaReserva.getHoraFin().isAfter(existente.getHoraInicio());

            if (solapa) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Existe solapamiento con otra reserva para el mismo recurso en esa fecha y hora.");
            }
        }

        return reservaRepository.save(nuevaReserva);
    }
}
