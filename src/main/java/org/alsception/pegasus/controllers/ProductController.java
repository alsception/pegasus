package org.alsception.pegasus.controllers;

import java.util.List;
import java.util.Optional;
import org.alsception.pegasus.dto.PopularProduct;
import org.alsception.pegasus.dto.PopularProductsWrapper;
import org.alsception.pegasus.entities.PGSProduct;
import org.alsception.pegasus.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    /*@GetMapping
    public List<PGSProduct> getAllProducts() {
        return productService.getAllProducts();
    }*/

    @GetMapping("/{id}")
    public Optional<PGSProduct> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/popular")
    public PopularProductsWrapper getPopularProducts() {
        return productService.getPopularProducts();
    }
    
    @PostMapping
    public PGSProduct createProduct(@RequestBody PGSProduct product) {
        return productService.saveProduct(product);
    }
    
    @GetMapping
    public List<PGSProduct> getProducts(
        @RequestParam(required = false) String code,
        @RequestParam(required = false) String name
    ) {
        
        //If no code or name: find all //TODO
        
        return productService.findProducts(code, name);
    }
}
