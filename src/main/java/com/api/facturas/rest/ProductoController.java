package com.api.facturas.rest;

import com.api.facturas.dto.ProductoDTO;
import com.api.facturas.pojo.Producto;
import com.api.facturas.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @PostMapping("/add")
    ResponseEntity<Producto> agregarProducto(@RequestBody Producto producto, @RequestParam(name = "categoriaID")Integer id) {
        return productoService.addNewProduct(id,producto);
    }
    @GetMapping
    /*Page: Pagina en la que nos situamos actualmente
    * Size: Tamano de la pagina actual*/
    ResponseEntity<?>obtenerProductos(@RequestParam(defaultValue = "0")int page) {
        return productoService.getAllProducts(page);
    }

    @PutMapping("/update/{productID}")
    ResponseEntity<?> actualizarProducto(@PathVariable Integer productID, @RequestParam(name = "categoryID")Integer categoryID,
                                         @RequestBody ProductoDTO producto) {
        return productoService.updateProduct(productID,categoryID,producto);
    }
    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> borrarProducto(@PathVariable Integer id) {
        return productoService.deleteProduct(id);
    }
    @GetMapping("/{id}")
    ResponseEntity<?> obtenerProducto_porID(@PathVariable Integer id) {
        return productoService.getProductById(id);
    }
    @GetMapping("/category")
    ResponseEntity<?> obtenerProductos_porCategoria(@RequestParam("id")Integer categoryID) {
        return productoService.getProductsByCategory(categoryID);
    }
}