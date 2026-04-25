package com.aulaclick.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioPasswordDTO {
    private String passwordActual;
    private String nuevaPassword;
}
