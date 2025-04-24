package org.alsception.pegasus.features.products;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alsception.pegasus.features.products.PGSReview;

@Data //Lombook for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "pgs_products")
public class PGSProduct 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 15, nullable = false)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(precision = 19, scale = 2) // 19 digits total, 2 decimal places
    private BigDecimal priceEur;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal priceUsd;
    
    @Lob
    private String description;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PGSReview> reviews;    
}
