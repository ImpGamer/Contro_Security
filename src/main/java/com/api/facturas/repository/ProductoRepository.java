package com.api.facturas.repository;

import com.api.facturas.pojo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto,Integer>, PagingAndSortingRepository<Producto,Integer> {
    @Query(value = "SELECT p FROM Producto p WHERE p.categoria.id = :categoriaID")
    List<Producto> getProductosByCategoria(@Param("categoriaID")Integer categoriaID);
}
