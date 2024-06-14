package com.api.facturas.rest;

import com.api.facturas.dto.CategoriaDTO;
import com.api.facturas.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("/add")
    ResponseEntity<String> agregarNuevaCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        return categoriaService.addNewCategory(categoriaDTO);
    }
    @GetMapping("/get")
    ResponseEntity<List<CategoriaDTO>> obtenerCategorias() {
        return categoriaService.getAllCategories();
    }
    @PutMapping("/update/{id}")
    ResponseEntity<?> actualizarCategoria(@PathVariable Integer id,@RequestParam(name = "nuevoNombre")String nombre) {
        return categoriaService.updateCategory(id,nombre);
    }
}