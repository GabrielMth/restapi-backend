package com.api.rest.resources;


import com.api.rest.event.RecursoCriadoEvent;
import com.api.rest.model.Lancamento;
import com.api.rest.repository.LancamentoRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResources {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping
    public ResponseEntity<Lancamento> criar (@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
        Lancamento lancamentoSalvo = lancamentoRepository.save(lancamento);

        publisher.publishEvent(new RecursoCriadoEvent(this,response,lancamentoSalvo.getCodigo())); //adicionando o header

        return ResponseEntity.status(HttpStatus.CREATED).body(lancamento);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Lancamento> obterPorCodigo (@PathVariable Long codigo) {
        Optional<Lancamento> lancamento = lancamentoRepository.findById(codigo);

        return lancamento.isPresent() ? ResponseEntity.ok(lancamento.get()) : ResponseEntity.notFound().build();
    }


}
