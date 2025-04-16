package com.api.rest.repository;


import com.api.rest.dto.LancamentoResumoDTO;
import com.api.rest.model.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(
            "SELECT l FROM Lancamento l WHERE "
            + "(COALESCE(:descricao, '') = '' OR l.descricao LIKE %:descricao%) "
            + "AND (:dataVencimentoDe IS NULL OR l.dataVencimento >= :dataVencimentoDe) "
            + "AND (:dataVencimentoAte IS NULL OR l.dataVencimento <= :dataVencimentoAte)"
          )
    Page<Lancamento> filtrar(Pageable pageable,
                             @Param("descricao") String descricao,
                             @Param("dataVencimentoDe") LocalDate dataVencimentoDe,
                             @Param("dataVencimentoAte") LocalDate dataVencimentoAte);


    @Query("""
            SELECT new com.api.rest.dto.LancamentoResumoDTO(
            l.codigo,
            l.descricao,
            l.dataVencimento,
            l.dataPagamento,
            l.valor,
            l.pessoa.nome,
            l.tipo,
            l.categoria.nome
            )
                FROM Lancamento l
                WHERE (:descricao IS NULL OR LOWER(l.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')))
                AND (:dataDe IS NULL OR l.dataVencimento >= :dataDe)
                AND (:dataAte IS NULL OR l.dataVencimento <= :dataAte)
    """)
    Page<LancamentoResumoDTO> resumir(
            @Param("descricao") String descricao,
            @Param("dataDe") LocalDate dataVencimentoDe,
            @Param("dataAte") LocalDate dataVencimentoAte,
            Pageable pageable
    );




}
