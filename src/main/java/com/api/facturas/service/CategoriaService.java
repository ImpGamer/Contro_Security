package com.api.facturas.service;

import com.api.facturas.constants.FacturaConstantes;
import com.api.facturas.dto.CategoriaDTO;
import com.api.facturas.mappers.CategoriaMapper;
import com.api.facturas.pojo.Categoria;
import com.api.facturas.repository.CategoriaRepository;
import com.api.facturas.security.jwt.JwtFilter;
import com.api.facturas.util.FacturaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private JwtFilter jwtFilter;

    public ResponseEntity<String> addNewCategory(CategoriaDTO categoriaDTO) {
        try {
            if(jwtFilter.isAdmin()) {
                categoriaRepository.save(CategoriaMapper.DTO_a_categoria(categoriaDTO));
                return ResponseEntity.ok("Categoria guardada correctamente");
            }
            return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch (Exception ex) {
            log.error(ex.getMessage());
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<List<CategoriaDTO>> getAllCategories() {
        try {
            if(jwtFilter.isAdmin()) {
                List<CategoriaDTO> categoriasDTO = categoriaRepository.findAll().stream()
                        .map(CategoriaMapper::categoria_a_DTO).toList();
                return ResponseEntity.ok(categoriasDTO);
            }
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<?> updateCategory(Integer id,String nombre) {
        try {
            if(jwtFilter.isAdmin()) {
                Optional<Categoria> categoriaBBDD = categoriaRepository.findById(id);
                if(categoriaBBDD.isPresent()) {
                    Categoria categoriaSave = categoriaBBDD.get();
                    categoriaSave.setNombre(nombre);
                    return ResponseEntity.ok(categoriaRepository.save(categoriaSave));
                } else {
                    return FacturaUtils.getResponseEntity("Categoria no encontrada ID colocado: "+id,HttpStatus.NOT_FOUND);
                }
            }
        }catch (Exception ex) {
            log.error(ex.getMessage());
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
    }
}