package org.alsception.pegasus.services;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.alsception.pegasus.dto.PopularProduct;
import org.alsception.pegasus.dto.PopularProductsWrapper;
import org.alsception.pegasus.entities.PGSProduct;
import org.alsception.pegasus.entities.PGSReview;
import org.alsception.pegasus.repositories.ProductRepository;
import org.alsception.pegasus.repositories.ReviewRepository;
import org.alsception.pegasus.utils.ProductValidator;
import org.springframework.stereotype.Service;

@Service
public class ProductService 
{
    private static final int MAX_RATING = 5;
    
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final HnbApiService hnbApiService;
    private final Random random = new Random();

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository, HnbApiService hnbApiService) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.hnbApiService = hnbApiService;
    }
    
    public List<PGSProduct> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<PGSProduct> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<PGSProduct> findProducts(String code, String name) 
    {
        if(code==null && name == null)
        { 
            return productRepository.findAll();
        }
        else
        {
            return productRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(code, name);
        }
    }
    
    public PGSProduct createProduct(PGSProduct product) 
    {       
        //This checks for code and other business logic constraints. Will thorw exception if product not valid  
        ProductValidator.validate(product);
        
        calculateUsdPrice(product);          
        
        final List<PGSReview> reviews = product.getReviews();
        
        //New product with reviews (needs to be saved separately)
        if(product.getId() == null && reviews!=null && !reviews.isEmpty())
        {
            return createProductAndReviews(product);
        }
        else
        {
            return productRepository.save(product);
        }        
    }
    
    public PGSProduct updateProduct(Long id, PGSProduct updatedProduct) 
    {
        ProductValidator.validate(updatedProduct);
        
        calculateUsdPrice(updatedProduct);       
        
        return productRepository.findById(id).map(existingProduct -> {

            // Keep existing reviews if updatedProduct does not explicitly contain reviews
            if (updatedProduct.getReviews() == null) {
                updatedProduct.setReviews(existingProduct.getReviews());
            }

            // If updatedProduct has an empty list of reviews, remove all reviews
            if (updatedProduct.getReviews() != null && updatedProduct.getReviews().isEmpty()) {
                existingProduct.getReviews().clear();
            }

            // Update other fields
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCode(updatedProduct.getCode());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPriceEur(updatedProduct.getPriceEur());
            existingProduct.setPriceUsd(updatedProduct.getPriceUsd());

            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private PGSProduct createProductAndReviews(PGSProduct product) 
    {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        // Ensure reviews is never null to avoid NullPointerException
        List<PGSReview> reviews = Optional.ofNullable(product.getReviews()).orElse(Collections.emptyList());
        product.setReviews(null);

        // Save product first
        final PGSProduct savedProduct = productRepository.save(product);

        // Associate reviews with saved product and batch save
        reviews.forEach(r -> r.setProduct(savedProduct));
        if (!reviews.isEmpty()) {
            reviewRepository.saveAll(reviews);
        }

        // Reload product with reviews from DB
        return productRepository.getReferenceById(savedProduct.getId());
    }
    
    public void deleteProduct(Long id) 
    {
        productRepository.deleteById(id);
    }

    public boolean existsById(Long id) 
    {
        return productRepository.existsById(id);
    } 
    
    private void calculateUsdPrice(PGSProduct product) 
    {
        BigDecimal exchangeRate = hnbApiService.getUsdPrice();  // Get exchange rate (BigDecimal)

        // Ensure product price is not null to avoid NullPointerException
        BigDecimal priceEur = product.getPriceEur() != null ? product.getPriceEur() : BigDecimal.ZERO;

        // Convert price using BigDecimal multiplication and set scale for precision
        BigDecimal priceUsd = priceEur.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        product.setPriceUsd(priceUsd);  // Set the converted USD price
    }

    public PopularProductsWrapper getPopularProducts() 
    {
        List<Object[]> pproducts = productRepository.getPopularProducts();
    
        List<PopularProduct> output = pproducts.stream()
        .map(obj -> new PopularProduct(
            (String) obj[1],    // Product Name
            (Double) obj[2]     // Average Rating
        ))
        .collect(Collectors.toList());
        
        return new PopularProductsWrapper(output);
    }
    
    public void generateProducts(int count) 
    {
        if (productRepository.count() == 0) 
        { // Prevent duplicate insertion
            int errors = 0;
            for (int i = 1; i <= count; i++) {
                try{
                    PGSProduct product = new PGSProduct();
                    product.setCode(generateValidCode());
                    product.setName("Product " + i);
                    product.setPriceEur(BigDecimal.valueOf(random.nextInt(100) + 10)); // Price: 10 - 109 EUR
                    product.setPriceUsd(BigDecimal.ZERO); // USD price calculated later
                    product.setDescription("Description for product " + i);
                    generateRandomReviews(product);
                    createProduct(product);
                }catch(Exception e){
                    System.err.println("Error generationg product "+i);
                    System.err.println(e);
                    errors++;
                }
            }
            
            System.out.println("✅ " + count + " Sample products initialized.");
            System.out.println("Errors: "+errors);
        }
    }

    private void generateRandomReviews(PGSProduct product) 
    {
        int reviewCount = random.nextInt(4) + 2; // Generates a random number between 2 and 5
        List<PGSReview> reviews = new ArrayList<>();

        for (int i = 0; i < reviewCount; i++) {
            PGSReview review = new PGSReview();
            review.setReviewer("Reviewer " + (i + 1));
            review.setText("This is a review " + (i + 1) + " for " + product.getName());
            review.setRating(random.nextInt(MAX_RATING) + 1); // Random rating from 1 to 5
            review.setProduct(product);
            reviews.add(review);
        }
        
        product.setReviews(reviews);
    }

    private String generateValidCode() {
        StringBuilder sb = new StringBuilder("PROD-");
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10)); // Generate 15-digit numeric code
        }
        return sb.toString();
    }
}