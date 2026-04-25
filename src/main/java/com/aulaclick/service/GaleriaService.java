package com.aulaclick.service;

import com.aulaclick.dto.ImagenRequestDTO;
import com.aulaclick.entity.ImagenGaleria;
import com.aulaclick.repository.ImagenGaleriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<ImagenGaleria> registrarImagenesMasivo(List<ImagenRequestDTO> dtos) {
        List<ImagenGaleria> entidades = dtos.stream()
                .map(dto -> new ImagenGaleria(dto.getUrl()))
                .collect(Collectors.toList());
        return imagenGaleriaRepository.saveAll(entidades);
    }

    public void eliminarImagenesMasivo(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        List<ImagenGaleria> imagenes = imagenGaleriaRepository.findAllById(ids);
        for (ImagenGaleria img : imagenes) {
            cloudinaryService.eliminarDeCloudinary(img.getUrl());
        }
        imagenGaleriaRepository.deleteAll(imagenes);
    }
}
