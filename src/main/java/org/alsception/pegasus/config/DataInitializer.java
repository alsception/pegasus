package org.alsception.pegasus.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.alsception.pegasus.entities.PGSProduct;
import org.alsception.pegasus.entities.PGSReview;
import org.alsception.pegasus.repositories.ProductRepository;
import org.alsception.pegasus.repositories.ReviewRepository;
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
