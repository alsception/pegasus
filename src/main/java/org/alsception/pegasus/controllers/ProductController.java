package org.alsception.pegasus.controllers;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.alsception.pegasus.dto.PopularProductsWrapper;
import org.alsception.pegasus.entities.PGSProduct;
import org.alsception.pegasus.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController 
{
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public List<PGSProduct> getProducts(@RequestParam(required = false) String code, @RequestParam(required = false) String name)
    {        
        return productService.findProducts(code, name);
    }    

    @GetMapping("/{id}")
    public ResponseEntity<PGSProduct> getProductById(@PathVariable Long id) 
    {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/popular")
    public PopularProductsWrapper getPopularProducts() 
    {
        return productService.getPopularProducts();
    }
    
    @PostMapping
    public PGSProduct createProduct(@RequestBody PGSProduct product) 
    {
        return productService.createProduct(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PGSProduct> updateProduct(@PathVariable Long id, @RequestBody PGSProduct updatedProduct) 
    {
        try {
            PGSProduct product = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
        
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) 
    {
        if (productService.existsById(id)) 
        {        
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
