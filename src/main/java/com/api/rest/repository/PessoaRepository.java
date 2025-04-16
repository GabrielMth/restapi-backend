package com.api.rest.repository;

import com.api.rest.dto.PaginacaoDTO;
import com.api.rest.model.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    @Query(
            "SELECT p FROM Pessoa p " +
            "WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))"
    )
    Page<Pessoa> filtrarPorNome(String nome,Pageable pageable);

}
