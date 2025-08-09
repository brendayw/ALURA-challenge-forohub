package com.aluracursos.forohub.model.topico;

public record DatosDetalleTopico(
        Long id,
        String titulo,
        String mensaje,
        Estado status,
        String autor,
        String curo
) {
    public DatosDetalleTopico(Topico topico) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso()
        );
    }
}
