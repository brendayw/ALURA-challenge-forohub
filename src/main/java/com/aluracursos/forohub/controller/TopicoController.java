package com.aluracursos.forohub.controller;

import com.aluracursos.forohub.controller.exceptions.TopicoNotFoundException;
import com.aluracursos.forohub.model.DatosActualizarTopico;
import com.aluracursos.forohub.model.DatosListadoTopicos;
import com.aluracursos.forohub.model.DatosRegistroTopico;
import com.aluracursos.forohub.model.Topico;
import com.aluracursos.forohub.repository.TopicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {
    @Autowired
    private TopicoRepository repository;

    @Transactional
    @PostMapping
    public void registrar(@RequestBody @Valid DatosRegistroTopico datos) {
        repository.save(new Topico(datos));
    }

    @GetMapping
    public  ResponseEntity<Page<DatosListadoTopicos>> listar(@PageableDefault(size=10, sort={"fechaCreacion"})Pageable paginacion) {
        Page<DatosListadoTopicos> pagina = repository.findAllByActivoTrue(paginacion)
                .map(DatosListadoTopicos::new);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosListadoTopicos> obtenerPorId(@PathVariable Long id) {
        var topico = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topico no encontrado."));
        DatosListadoTopicos datos = new DatosListadoTopicos(topico);
        return ResponseEntity.ok(datos);
    }

    @Transactional
    @PutMapping
    public ResponseEntity<String> actualizar(@RequestBody @Valid DatosActualizarTopico datos) throws TopicoNotFoundException {
        Optional<Topico> topicoOpcional = repository.findByIdAndActivoTrue(datos.id());
        if (topicoOpcional.isPresent()) {
            Topico topico = topicoOpcional.get();
            topico.actualizarInformacion(datos);
        } else {
            throw new TopicoNotFoundException("Topico no encontrado");
        }
        return ResponseEntity.ok("Topico actualizado correctamente.");
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) throws TopicoNotFoundException {
        Optional<Topico> topicoOpcional = repository.findById(id);
        if (topicoOpcional.isPresent()) {
            topicoOpcional.get().eliminar();
        } else {
            throw new TopicoNotFoundException("Topico no encontrado");
        }
        return ResponseEntity.ok("Topico eliminado exitosamente.");
    }
}
