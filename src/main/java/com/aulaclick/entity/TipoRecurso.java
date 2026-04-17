package com.aulaclick.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "Tipos_Recurso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_recurso")
    private Long idTipoRecurso;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "tipoRecurso")
    private List<Recurso> recursos;
}
