package com.api.rest.model;

import jakarta.persistence.*;
import org.hibernate.mapping.ToOne;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Lancamento")
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    private String descricao;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    private BigDecimal valor;

    private String observacao;

    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;

    @ManyToOne // Um lançamento tem uma categoria ToOne porém uma categoria pode estar em varios relacionamentos então acrescenta o Many na frente.
    @JoinColumn(name ="codigo_categoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "codigo_pessoa")
    private Pessoa pessoa;
}
