package org.alsception.pegasus.features.products;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a data transfer object (dto) for PGSProduct entity,
 * used to deliver data to frontend, without exposing all data.
 */

@Data //Lombook for getters and setters
@NoArgsConstructor
public class PGSProductDTO 
{
    private Long id;    
    private String code;    
    private String name;    
    private BigDecimal priceEur;    
    private BigDecimal priceUsd;        
    private String description;    
    private List<PGSReview> reviews;   
    
     public PGSProductDTO(PGSProduct product) {
        this.id = product.getId();
        this.name = product.getName();
        this.code = product.getCode();
        this.priceEur = product.getPriceEur();
        this.priceUsd = product.getPriceUsd();
        this.description = product.getDescription();
    }
}
