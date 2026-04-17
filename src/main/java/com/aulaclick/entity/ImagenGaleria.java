package com.aulaclick.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "imagenes_galeria")
@Getter
@Setter
@NoArgsConstructor
public class ImagenGaleria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idImagen;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

    public ImagenGaleria(String url) {
        this.url = url;
    }
}
