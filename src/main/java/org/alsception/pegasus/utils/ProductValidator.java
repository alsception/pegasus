package org.alsception.pegasus.utils;

import org.alsception.pegasus.entities.PGSProduct;
import org.alsception.pegasus.exception.ProductValidationException;

public class ProductValidator {

    public static void validate(PGSProduct product) 
    {
        if (product == null) {
            throw new ProductValidationException("Product cannot be null");
        }
        
        validateCode(product);
    }

    private static void validateCode(PGSProduct product) throws IllegalArgumentException 
    {
        String code = product.getCode();
        if (code == null || code.length() != 15) 
        {
            throw new ProductValidationException("Product code must be exactly 15 characters long (you have " + (code==null?"0":code.length())+")");
        }
    }
    
    //TODO: validate review 1-5
}
