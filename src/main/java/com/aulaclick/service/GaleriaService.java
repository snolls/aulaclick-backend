package com.aulaclick.service;

import com.aulaclick.entity.ImagenGaleria;
import com.aulaclick.repository.ImagenGaleriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GaleriaService {

    private final ImagenGaleriaRepository imagenGaleriaRepository;
    private final CloudinaryService cloudinaryService;

    public void eliminarImagen(long id) {
        ImagenGaleria imagen = imagenGaleriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        cloudinaryService.eliminarDeCloudinary(imagen.getUrl());
        imagenGaleriaRepository.delete(imagen);
    }
}
