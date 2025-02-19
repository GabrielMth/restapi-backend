package com.api.rest.service;

import com.api.rest.model.Lancamento;
import com.api.rest.model.Pessoa;
import com.api.rest.repository.LancamentoRepository;
import com.api.rest.repository.PessoaRepository;
import com.api.rest.service.exceptionDeRegraDeNegocio.PessoaInexistenteOuInativaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LancamentoService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    public Lancamento salvar(Lancamento lancamento) {
        Pessoa pessoa = pessoaRepository.findById(lancamento.getPessoa().getId())
                .filter(p -> !p.isInativo())
                .orElseThrow(PessoaInexistenteOuInativaException::new);

        return lancamentoRepository.save(lancamento);
    }
}
