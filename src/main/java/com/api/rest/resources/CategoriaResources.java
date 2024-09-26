package com.api.rest.resources;

import com.api.rest.event.RecursoCriadoEvent;
import com.api.rest.model.Categoria;
import com.api.rest.repository.CategoriaRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

//META DADOS. - ANOTAÇÕES.
@RestController
@RequestMapping("/categorias")
public class CategoriaResources {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    public List<Categoria> listar() {
        // public ResponseEntity<?> listar() {
        // List<Categoria> categorias = categoriaRepository.findAll();         // não fica bom mas é possível trocar de acordo com um estado da list.
        // return !categorias.isEmpty() ? ResponseEntity.ok(categorias) : ResponseEntity.noContent().build();
        return categoriaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
        Categoria categoriaSalva = categoriaRepository.save(categoria);

        publisher.publishEvent(new RecursoCriadoEvent(this,response,categoriaSalva.getCodigo()));

        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Categoria> buscarPeloId(@PathVariable Long codigo) {
        Optional<Categoria> categoria = categoriaRepository.findById(codigo);

        if (!categoria.isEmpty()) {
            return ResponseEntity.ok(categoria.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
