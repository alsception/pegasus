package org.alsception.pegasus.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.alsception.pegasus.services.ProductService;

@Component
public class DataInitializer implements CommandLineRunner 
{
    private final ProductService productService;

    public DataInitializer(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) 
    {        
        productService.generateProducts(5); // ✅ Calls service method to generate products
    }

}
