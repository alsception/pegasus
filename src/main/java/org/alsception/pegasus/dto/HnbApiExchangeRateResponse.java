package org.alsception.pegasus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Lombook for getters and setters
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HnbApiExchangeRateResponse 
{
    private String datum_primjene;    
    private String drzava;
    private String drzava_iso;
    private String srednji_tecaj;
    private String valuta;
    
    public double getSrednjiTecajDouble() {
        try{
            return Double.parseDouble(srednji_tecaj.replace(",", "."));
        }catch(Exception e){
            return Double.NaN;
        }
    }
    
}