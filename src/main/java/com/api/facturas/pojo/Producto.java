package com.api.facturas.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String nombre;

    //Diferencia entre LAZY-EAGER
    /*Lazy solamente me traera el atributo ID de la categoria en casos especificos que nosotros se lo pidamos,
    * es decir por defecto no lo va a traer, solamente mediante una peticion especifica.
    * Eager es lo contrario, no importa en que tipo de peticion, incluso cuando no es necesaria, estara el atributo
    * ID de la categoria en todo momento, es mas recomendable con relaciones pequenas (no @ManyToMany o Arrays)*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id",nullable=false)
    private Categoria categoria;

    @Column
    private String descripcion;
    @Column(nullable = false)
    private float precio;

    @Column(nullable = false)
    private boolean status;
}
