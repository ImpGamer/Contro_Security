package com.api.facturas.dto;

import com.api.facturas.pojo.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private float precio;
    private boolean status;
    private Categoria categoria;

    public ProductoDTO(String nombre,String descripcion,float precio) {
        this.nombre = nombre;
        this.descripcion =descripcion;
        this.precio = precio;
    }
}
