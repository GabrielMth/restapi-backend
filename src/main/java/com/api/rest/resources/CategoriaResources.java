package com.api.rest.resources;

import com.api.rest.model.Categoria;
import com.api.rest.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//META DADOS. - ANOTAÇÕES.
@RestController
@RequestMapping("/categorias")
public class CategoriaResources {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }
}
