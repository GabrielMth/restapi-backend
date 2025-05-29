package com.api.rest.service;

import com.api.rest.model.Lancamento;
import com.api.rest.model.Pessoa;
import com.api.rest.repository.LancamentoRepository;
import com.api.rest.repository.PessoaRepository;
import com.api.rest.service.exceptionDeRegraDeNegocio.LancamentoInexistente;
import com.api.rest.service.exceptionDeRegraDeNegocio.PessoaInexistenteOuInativaException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Lancamento att (Long codigo, Lancamento lancamento) {
        Optional<Lancamento> lancamentoSalvoOpt = lancamentoRepository.findById(codigo);

        if (lancamentoSalvoOpt.isEmpty()) {
            throw new LancamentoInexistente();
        }

        Lancamento lancamentoSalvo = lancamentoSalvoOpt.get();


        if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
            Pessoa pessoaAtualizada = validarPessoa(lancamento);
            lancamento.setPessoa(pessoaAtualizada);
        }

        BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

        return lancamentoRepository.save(lancamentoSalvo);
    }

    public Pessoa validarPessoa(Lancamento lancamento) {

        if (lancamento.getPessoa().getId() != null) {
            Optional<Pessoa> pessoaOpt = pessoaRepository.findById(lancamento.getPessoa().getId());

            if (pessoaOpt.isEmpty() || pessoaOpt.get().isInativo()) {
                throw new PessoaInexistenteOuInativaException();
            }

            return pessoaOpt.get();
        }

        throw new PessoaInexistenteOuInativaException();
    }

}
