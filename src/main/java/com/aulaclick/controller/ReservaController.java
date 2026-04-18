package com.aulaclick.controller;

import com.aulaclick.dto.ReservaDTO;
import com.aulaclick.entity.Reserva;
import com.aulaclick.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aulaclick.dto.ReservaCrearDTO;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaDTO> createReserva(@RequestBody ReservaCrearDTO dto) {
        ReservaDTO nuevaReserva = reservaService.createReserva(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
    }

    @GetMapping("/recurso/{id}")
    public ResponseEntity<?> getReservasByRecurso(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.getReservasByRecurso(id));
    }

    @GetMapping("/mis-reservas/{idUsuario}")
    public ResponseEntity<?> getMisReservas(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(reservaService.getMisReservas(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaDTO>> obtenerMisReservas(@PathVariable("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(reservaService.getMisReservas(idUsuario));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaDTO> cancelarReserva(@PathVariable("id") Long idReserva) {
        return ResponseEntity.ok(reservaService.cancelarReserva(idReserva));
    }
}
