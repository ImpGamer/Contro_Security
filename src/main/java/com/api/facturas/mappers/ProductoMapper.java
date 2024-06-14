package com.api.facturas.mappers;

import com.api.facturas.dto.ProductoDTO;
import com.api.facturas.pojo.Producto;
import com.api.facturas.repository.CategoriaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoMapper {
    @Autowired
    private CategoriaRepository categoriaRepository;
    public Producto DTO_a_entity(ProductoDTO productoDTO) {
        Producto producto = new Producto();
        BeanUtils.copyProperties(productoDTO,producto);
        return producto;
    }
    public ProductoDTO entity_a_DTO(Producto producto) {
        ProductoDTO productoDTO = new ProductoDTO();
        BeanUtils.copyProperties(producto,productoDTO);
        return productoDTO;
    }
}
