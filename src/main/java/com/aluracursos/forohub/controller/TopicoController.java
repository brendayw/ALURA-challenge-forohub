package com.aluracursos.forohub.controller;

import com.aluracursos.forohub.infra.exceptions.TopicoNotFoundException;
import com.aluracursos.forohub.model.topico.*;
import com.aluracursos.forohub.repository.TopicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {
    @Autowired
    private TopicoRepository repository;

    @Transactional
    @PostMapping
    public ResponseEntity registrar(@RequestBody @Valid DatosRegistroTopico datos, UriComponentsBuilder uriComponentsBuilder) {
        var topico = new Topico(datos);
        repository.save(topico);
        var uri = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DatosDetalleTopico(topico));
    }

    @GetMapping
    public  ResponseEntity<Page<DatosListadoTopicos>> listar(@PageableDefault(size=10, sort={"fechaCreacion"})Pageable paginacion) {
        Page<DatosListadoTopicos> pagina = repository.findAllByActivoTrue(paginacion)
                .map(DatosListadoTopicos::new);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity detallar(@PathVariable Long id) {
        var topico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

    @Transactional
    @PutMapping
    public ResponseEntity<String> actualizar(@RequestBody @Valid DatosActualizarTopico datos)  {
        Optional<Topico> topicoOpcional = repository.findByIdAndActivoTrue(datos.id());
        if (topicoOpcional.isPresent()) {
            Topico topico = topicoOpcional.get();
            topico.actualizarInformacion(datos);
        }
        return ResponseEntity.ok("Topico actualizado correctamente.");
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminar(@PathVariable Long id) throws TopicoNotFoundException {
        Optional<Topico> topicoOpcional = repository.findById(id);
        if (topicoOpcional.isPresent()) {
            topicoOpcional.get().eliminar();
        }
        return ResponseEntity.noContent().build();
    }
}
