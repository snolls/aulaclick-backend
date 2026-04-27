package com.aulaclick.unit;

import com.aulaclick.dto.ReservaCrearDTO;
import com.aulaclick.entity.Recurso;
import com.aulaclick.entity.Usuario;
import com.aulaclick.repository.RecursoRepository;
import com.aulaclick.repository.ReservaRepository;
import com.aulaclick.repository.UsuarioRepository;
import com.aulaclick.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para las validaciones de negocio de {@link ReservaService}.
 *
 * <p>Se emplean dobles de prueba (mocks) de Mockito para aislar el servicio
 * de la capa de persistencia. Cada test verifica que el servicio lanza
 * {@link IllegalArgumentException} ante entradas que violan las reglas de negocio.</p>
 *
 * <p>Reglas comprobadas:</p>
 * <ul>
 *   <li>La fecha no puede ser anterior al día actual.</li>
 *   <li>La hora de fin debe ser posterior a la hora de inicio.</li>
 *   <li>Si el recurso no permite fines de semana, se rechaza la reserva en sábado o domingo.</li>
 *   <li>La reserva debe estar dentro del horario de apertura del recurso.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceValidacionTest {

    @Mock private ReservaRepository   reservaRepository;
    @Mock private RecursoRepository   recursoRepository;
    @Mock private UsuarioRepository   usuarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    /** DTO base reutilizado en cada test; se personaliza según el caso. */
    private ReservaCrearDTO dto;

    /** Recurso ficticio con apertura 08:00–21:00 y fin de semana permitido. */
    private Recurso recurso;

    /** Usuario ficticio mínimo para satisfacer la consulta al repositorio. */
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // DTO con IDs y motivo válidos; fecha y horas se fijan en cada test
        dto = new ReservaCrearDTO();
        dto.setIdRecurso(1L);
        dto.setIdUsuario(1L);
        dto.setMotivo("Reunión de equipo");

        // Recurso configurado con valores por defecto permisivos
        recurso = new Recurso();
        recurso.setIdRecurso(1L);
        recurso.setPermiteFinesSemana(true);
        recurso.setHoraApertura(LocalTime.of(8, 0));
        recurso.setHoraCierre(LocalTime.of(21, 0));

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T13 – Fecha en el pasado
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T13: Reservar en una fecha anterior al día de hoy debe lanzar excepción.
     *
     * <p>Esta validación protege el sistema de reservas fantasma y se realiza
     * antes de consultar la base de datos.</p>
     *
     * <p>Entrada: fecha = "2020-01-01", horaInicio = "09:00", horaFin = "10:00"</p>
     * <p>Salida esperada: {@link IllegalArgumentException}</p>
     */
    @Test
    @DisplayName("T13 – Fecha en el pasado lanza IllegalArgumentException")
    void T13_fechaPasada_lanzaExcepcion() {
        dto.setFecha("2020-01-01");
        dto.setHoraInicio("09:00");
        dto.setHoraFin("10:00");

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.createReserva(dto),
                "Debe rechazar reservas con fecha anterior al día de hoy");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T14 – Hora fin anterior a hora inicio
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T14: La hora de fin debe ser estrictamente posterior a la hora de inicio.
     *
     * <p>Entrada: fecha mañana, horaInicio = "11:00", horaFin = "09:00"</p>
     * <p>Salida esperada: {@link IllegalArgumentException}</p>
     */
    @Test
    @DisplayName("T14 – Hora fin anterior a hora inicio lanza IllegalArgumentException")
    void T14_horaFinAntesInicio_lanzaExcepcion() {
        dto.setFecha(LocalDate.now().plusDays(1).toString());
        dto.setHoraInicio("11:00");
        dto.setHoraFin("09:00");

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.createReserva(dto),
                "Debe rechazar reservas donde la hora fin es anterior a la hora inicio");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T15 – Fin de semana no permitido
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T15: Si el recurso no admite reservas en fin de semana,
     * una reserva en sábado debe lanzar excepción.
     *
     * <p>Se calcula dinámicamente el próximo sábado para que el test sea
     * independiente de la fecha de ejecución.</p>
     *
     * <p>Entrada: recurso con permiteFinesSemana=false, fecha=próximo sábado</p>
     * <p>Salida esperada: {@link IllegalArgumentException}</p>
     */
    @Test
    @DisplayName("T15 – Reserva en sábado con recurso sin permiso de fin de semana lanza excepción")
    void T15_findeSemanaNoPermitido_lanzaExcepcion() {
        recurso.setPermiteFinesSemana(false);
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Calcula el próximo sábado a partir de mañana (siempre futuro)
        LocalDate sabado = LocalDate.now().plusDays(1);
        while (sabado.getDayOfWeek() != DayOfWeek.SATURDAY) {
            sabado = sabado.plusDays(1);
        }

        dto.setFecha(sabado.toString());
        dto.setHoraInicio("09:00");
        dto.setHoraFin("10:00");

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.createReserva(dto),
                "Debe rechazar reservas en fin de semana cuando el recurso no lo permite");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T16 – Fuera del horario de apertura
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T16: Una reserva cuya hora de inicio sea anterior a la apertura del recurso
     * (08:00) debe rechazarse.
     *
     * <p>Entrada: horaInicio = "06:00", horaApertura del recurso = 08:00</p>
     * <p>Salida esperada: {@link IllegalArgumentException}</p>
     */
    @Test
    @DisplayName("T16 – Reserva fuera del horario de apertura lanza IllegalArgumentException")
    void T16_fueraDeHorario_lanzaExcepcion() {
        when(recursoRepository.findById(anyLong())).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));

        dto.setFecha(LocalDate.now().plusDays(1).toString());
        dto.setHoraInicio("06:00"); // Antes de la apertura a las 08:00
        dto.setHoraFin("07:30");

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.createReserva(dto),
                "Debe rechazar reservas cuya hora inicio es anterior a la apertura del recurso");
    }
}
