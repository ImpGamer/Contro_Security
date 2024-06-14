package com.api.facturas.mappers;

import com.api.facturas.dto.CategoriaDTO;
import com.api.facturas.pojo.Categoria;
import org.springframework.beans.BeanUtils;
public class CategoriaMapper {
    public static Categoria DTO_a_categoria(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria();
        BeanUtils.copyProperties(categoriaDTO,categoria);
        return categoria;
    }
    public static CategoriaDTO categoria_a_DTO(Categoria categoria) {
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        BeanUtils.copyProperties(categoria,categoriaDTO);
        return categoriaDTO;
    }
}
