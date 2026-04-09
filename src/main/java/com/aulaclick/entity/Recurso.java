package com.aulaclick.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Recursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recurso")
    private Long idRecurso;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_recurso", nullable = false)
    private TipoRecurso tipoRecurso;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "estado", nullable = false)
    private String estado;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "recurso_equipamiento", joinColumns = @JoinColumn(name = "id_recurso"), inverseJoinColumns = @JoinColumn(name = "id_equipamiento"))
    private List<Equipamiento> equipamientos;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", nullable = false)
    private Departamento departamento;


    @JsonIgnore
    @OneToMany(mappedBy = "recurso")
    private List<Reserva> reservas;
}
