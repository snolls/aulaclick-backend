package com.aulaclick.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EstadisticasDTO {
    private long totalUsuarios;
    private long totalRecursos;
    private long totalReservasActivas;
    private long totalReservasCanceladas;
}
