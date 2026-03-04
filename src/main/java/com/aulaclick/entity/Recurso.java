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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_recurso", nullable = false)
    @JsonIgnoreProperties("recursos")
    private TipoRecurso tipoRecurso;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "estado", nullable = false)
    private String estado;

    @ManyToMany
    @JoinTable(name = "recurso_equipamiento", joinColumns = @JoinColumn(name = "id_recurso"), inverseJoinColumns = @JoinColumn(name = "id_equipamiento"))
    @JsonIgnoreProperties("recursos")
    private List<Equipamiento> equipamientos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", nullable = false)
    @JsonIgnoreProperties("recursos")
    private Departamento departamento;

    @OneToMany(mappedBy = "recurso")
    @JsonIgnore
    private List<Reserva> reservas;
}
