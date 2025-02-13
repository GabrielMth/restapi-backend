package com.api.rest.resources;


import com.api.rest.event.RecursoCriadoEvent;
import com.api.rest.model.Pessoa;
import com.api.rest.repository.PessoaRepository;
import com.api.rest.service.PessoaService;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pessoas")
public class PessoaResources {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public List<Pessoa> listar() {
        return pessoaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> obterPessoaId(@PathVariable Long id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);

        return pessoa.isPresent() ? ResponseEntity.ok(pessoa.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);

//        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}").buildAndExpand(pessoaSalva.getId()).toUri(); criamos um evento.
//        response.setHeader("Location", uri.toASCIIString()); setar location no header de forma manual porém o created já retorna
        publisher.publishEvent(new RecursoCriadoEvent(this,response,pessoa.getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        if (!pessoaRepository.existsById(id)) {
            throw new EmptyResultDataAccessException(1); // Lança exceção sem mensagem
        }
        pessoaRepository.deleteById(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable Long id, @Valid @RequestBody Pessoa pessoa) {
        Pessoa pessoaSalva = pessoaService.atualizar(id,pessoa);
        return ResponseEntity.ok(pessoaSalva);
    }

    @PutMapping("/{id}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarPropriedadeAtivo(@PathVariable Long id, @RequestBody Boolean ativo) {
        pessoaService.atualizarPropriedadeAtivo(id,ativo);
    }






}
