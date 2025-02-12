package com.api.rest.service;

import com.api.rest.model.Pessoa;
import com.api.rest.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa atualizar(Long id,Pessoa pessoa) {
        Pessoa pessoaSalva = pessoaRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(1));

        BeanUtils.copyProperties(pessoa, pessoaSalva, "id"); // Mantém o ID original
        return pessoaRepository.save(pessoaSalva); // Salva a pessoa atualizada
    }
}
