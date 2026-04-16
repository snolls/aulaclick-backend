package com.aulaclick.service;

import com.aulaclick.dto.ReservaCrearDTO;
import com.aulaclick.entity.Recurso;
import com.aulaclick.entity.Reserva;
import com.aulaclick.entity.Usuario;
import com.aulaclick.repository.RecursoRepository;
import com.aulaclick.repository.ReservaRepository;
import com.aulaclick.repository.UsuarioRepository;
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
    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;

    public Reserva createReserva(ReservaCrearDTO dto) {
        if (dto.getIdRecurso() == null || dto.getIdUsuario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere el ID del recurso y del usuario");
        }

        LocalDate fecha;
        LocalTime horaInicio;
        LocalTime horaFin;

        try {
            fecha = LocalDate.parse(dto.getFecha());
            horaInicio = LocalTime.parse(dto.getHoraInicio());
            horaFin = LocalTime.parse(dto.getHoraFin());
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha u hora inválido. Se espera YYYY-MM-DD y HH:MM");
        }

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

        Recurso recurso = recursoRepository.findById(dto.getIdRecurso())
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));
                
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        boolean permiteFinesSemana = recurso.getPermiteFinesSemana() != null ? recurso.getPermiteFinesSemana() : false;
        LocalTime horaApertura = recurso.getHoraApertura() != null ? recurso.getHoraApertura() : LocalTime.of(8, 0);
        LocalTime horaCierre = recurso.getHoraCierre() != null ? recurso.getHoraCierre() : LocalTime.of(21, 0);

        // 3. Fines de semana
        boolean esFinDeSemana = fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY;
        if (!permiteFinesSemana && esFinDeSemana) {
            throw new IllegalArgumentException("Este recurso no permite reservas en fines de semana.");
        }

        // 4. Horario de apertura dinámico
        if (horaInicio.isBefore(horaApertura) || horaFin.isAfter(horaCierre)) {
            throw new IllegalArgumentException("La reserva debe estar dentro del horario de apertura del recurso (" + horaApertura + " - " + horaCierre + ").");
        }

        // 5. Solapamientos
        long solapamientos = reservaRepository.contarSolapamientos(
                recurso.getIdRecurso(),
                fecha,
                horaInicio,
                horaFin
        );

        if (solapamientos > 0) {
            throw new IllegalArgumentException("La sala ya está reservada en ese horario");
        }

        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setFecha(fecha);
        nuevaReserva.setHoraInicio(horaInicio);
        nuevaReserva.setHoraFin(horaFin);
        nuevaReserva.setMotivo(dto.getMotivo());
        nuevaReserva.setRecurso(recurso);
        nuevaReserva.setUsuario(usuario);

        return reservaRepository.save(nuevaReserva);
    }

    public List<Reserva> getReservasByRecurso(Long idRecurso) {
        return reservaRepository.findByRecurso_IdRecurso(idRecurso);
    }
}
