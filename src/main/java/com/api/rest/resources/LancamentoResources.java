package com.api.rest.resources;


import com.api.rest.dto.LancamentoResumoDTO;
import com.api.rest.dto.PaginacaoDTO;
import com.api.rest.event.RecursoCriadoEvent;
import com.api.rest.model.Lancamento;
import com.api.rest.repository.LancamentoRepository;
import com.api.rest.service.LancamentoService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResources {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private MessageSource messageSource;

    @PostMapping
    public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
        Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);

        publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo())); //adicionando o header

        return ResponseEntity.status(HttpStatus.CREATED).body(lancamento);
    }

    @GetMapping
    public PaginacaoDTO<Lancamento> pesquisar(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataVencimentoDe,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataVencimentoAte,
            Pageable pageable) {

        Page<Lancamento> paginaLancamentos = lancamentoRepository.filtrar(pageable, descricao, dataVencimentoDe, dataVencimentoAte);

        return new PaginacaoDTO<>(paginaLancamentos);
    }

    @GetMapping("/resumo")
    public PaginacaoDTO<LancamentoResumoDTO> resumo(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataVencimentoDe,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataVencimentoAte,
            Pageable pageable) {

        Page<LancamentoResumoDTO> paginaResumo = lancamentoRepository.resumir(descricao, dataVencimentoDe, dataVencimentoAte, pageable);

        return new PaginacaoDTO<>(paginaResumo);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Lancamento> obterPorCodigo(@PathVariable Long codigo) {
        Optional<Lancamento> lancamento = lancamentoRepository.findById(codigo);

        return lancamento.isPresent() ? ResponseEntity.ok(lancamento.get()) : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        if (!lancamentoRepository.existsById(id)) {
            throw new EmptyResultDataAccessException(1); // Lança exceção sem mensagem
        }
        lancamentoRepository.deleteById(id);
    }

}
