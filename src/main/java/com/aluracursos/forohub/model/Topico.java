package com.aluracursos.forohub.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    private String mensaje;
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;
    @Enumerated(EnumType.STRING)
    private Estado status;
    private String autor;
    private String curso;

    public Topico(DatosRegistroTopico datos) {
        this.id = null;
        this.titulo = datos.titulo();
        this.mensaje = datos.mensaje();
        this.fechaCreacion = LocalDate.now();
        this.status = Estado.ABIERTO;
        this.autor = datos.autor();
        this.curso = datos.curso();
    }

    public void actualizarInformacion(@Valid DatosActualizarTopico datos) {
        if (datos.titulo() != null) {
            this.titulo = datos.titulo();
        }
        if(datos.mensaje() != null) {
            this.mensaje = datos.mensaje();
        }
    }
}
