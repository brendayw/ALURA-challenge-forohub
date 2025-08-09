package com.aluracursos.forohub.model.topico;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DatosListadoTopicos(
        @NotNull Long id,
        String titulo,
        String mensaje,
        LocalDate fechaCreacion,
        Estado status,
        String autor,
        String curso) {

    public DatosListadoTopicos(Topico topico) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso()
        );
    }
}
