package com.api.facturas.service;

import com.api.facturas.constants.FacturaConstantes;
import com.api.facturas.dto.ProductoDTO;
import com.api.facturas.mappers.ProductoMapper;
import com.api.facturas.pojo.Categoria;
import com.api.facturas.pojo.Producto;
import com.api.facturas.repository.CategoriaRepository;
import com.api.facturas.repository.ProductoRepository;
import com.api.facturas.security.jwt.JwtFilter;
import com.api.facturas.util.FacturaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private ProductoMapper mapper;

    public ResponseEntity<Producto> addNewProduct(Integer categoriaID,Producto producto) {
        try {
            if(jwtFilter.isAdmin() && validateProduct(categoriaID,producto)) {
                Optional<Categoria> categoriaAdd = categoriaRepository.findById(categoriaID);
                if(categoriaAdd.isPresent()) {
                    producto.setCategoria(categoriaAdd.get());
                    return ResponseEntity.ok(productoRepository.save(producto));
                }
            }
        }catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(new Producto(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new Producto(),HttpStatus.UNAUTHORIZED);
    }
    public ResponseEntity<?> getAllProducts(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Producto> productoPage = productoRepository.findAll(pageable);

        List<ProductoDTO> productos = productoPage.stream()
                .map(producto -> mapper.entity_a_DTO(producto))
                .toList();

        return productos.isEmpty() ? new ResponseEntity<>("No se encuentran productos registrados", HttpStatus.NO_CONTENT)
                : ResponseEntity.ok(productos);
    }
    public ResponseEntity<?> getProductById(Integer id) {
        try {
            if(jwtFilter.isAdmin()) {
                Optional<Producto> productoBD = productoRepository.findById(id);
                return productoBD.isPresent()?ResponseEntity.ok(mapper.entity_a_DTO(productoBD.get())):
                        new ResponseEntity<>("Producto con ID: "+id+" no encontrado. Verifique sus datos.",HttpStatus.NOT_FOUND);
            }
        }catch (Exception ex) {
            log.error(ex.getMessage());
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
    }
    public ResponseEntity<?> getProductsByCategory(Integer categoryID) {
        try {
            if(jwtFilter.isAdmin()) {
                List<ProductoDTO> productos_por_categoria = productoRepository.getProductosByCategoria(categoryID)
                        .stream().map(producto -> mapper.entity_a_DTO(producto)).toList();
                Optional<Categoria> categoriaBD = categoriaRepository.findById(categoryID);
                return productos_por_categoria.isEmpty()?new ResponseEntity<>("No se encontraron productos de la categoria "+categoriaBD.get().getNombre()+
                        "Verifique sus datos",HttpStatus.NO_CONTENT):ResponseEntity.ok(productos_por_categoria);
            }
        }catch (Exception ex) {
            log.error(ex.getMessage());
            FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
    }
    public ResponseEntity<?> updateProduct(Integer id,Integer categoriaID,ProductoDTO productoDTO) {
        try {
            if(jwtFilter.isAdmin()) {
                Optional<Categoria> categoriaBBDD = categoriaRepository.findById(categoriaID);
                Optional<Producto> productoBBDD = productoRepository.findById(id);
                if (categoriaBBDD.isPresent() && productoBBDD.isPresent()) {
                    Producto productoSave = productoBBDD.get();
                    productoSave.setCategoria(categoriaBBDD.get());
                    productoSave.setStatus(productoDTO.isStatus());
                    productoSave.setPrecio(productoDTO.getPrecio());
                    productoSave.setNombre(productoDTO.getNombre());
                    productoSave.setDescripcion(productoDTO.getDescripcion());
                    return ResponseEntity.ok(mapper.entity_a_DTO(productoRepository.save(productoSave)));
                } else {
                    return new ResponseEntity<>("Categoria y Producto no encontrado",HttpStatus.NOT_FOUND);
                }
            }
        }catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
    }
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if(jwtFilter.isAdmin()) {
                Optional<Producto> productoDB = productoRepository.findById(id);
                if(productoDB.isPresent()) {
                    productoRepository.delete(productoDB.get());
                    return ResponseEntity.ok("Producto eliminado correctamente");
                }
                return FacturaUtils.getResponseEntity("Producto no encontrado. Verifique sus datos.",HttpStatus.NOT_FOUND);
            }
        }catch (Exception ex) {
            log.error(ex.getMessage());
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
    }

    private boolean validateProduct(Integer categoriaID,Producto producto) {
        Optional<Categoria> categoriaExis = categoriaRepository.findById(categoriaID);
        if(categoriaExis.isPresent()) {
            return producto.getPrecio() > 0 && !producto.getDescripcion().isBlank() && !producto.getNombre().isBlank()
                    && !producto.getDescripcion().isEmpty() && !producto.getNombre().isEmpty();
        }
        return false;
    }
}
