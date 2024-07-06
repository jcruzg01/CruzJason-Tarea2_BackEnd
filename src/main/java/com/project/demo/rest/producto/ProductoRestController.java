package com.project.demo.rest.producto;

import com.project.demo.logic.entity.categoria.Categoria;
import com.project.demo.logic.entity.categoria.CategoriaRepository;
import com.project.demo.logic.entity.producto.Producto;
import com.project.demo.logic.entity.producto.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoRestController {
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE', 'USER')")
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Producto createProducto(@RequestBody Producto producto) {
        var categoriaProductoNuevo = categoriaRepository.findById(producto.getCategoriaProducto().getId());
        producto.setCategoriaProducto(categoriaProductoNuevo.get());
        return productoRepository.save(producto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Producto updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return productoRepository.findById(id)
                .map(existeProducto -> {
                    existeProducto.setNombre(producto.getNombre());
                    existeProducto.setDescripcion(producto.getDescripcion());
                    existeProducto.setCantEnStock(producto.getCantEnStock());
                    var categoriaProductoActualizar = categoriaRepository.findById(producto.getCategoriaProducto().getId());
                    existeProducto.setCategoriaProducto(categoriaProductoActualizar.get());

                    return productoRepository.save(existeProducto);
                })
                .orElseGet(() -> {
                    producto.setId(id);
                    var categoriaProductoActualizar = categoriaRepository.findById(producto.getCategoriaProducto().getId());
                    producto.setCategoriaProducto(categoriaProductoActualizar.get());
                    return productoRepository.save(producto);
                });
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public void deleteProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }
}
