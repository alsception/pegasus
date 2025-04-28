package org.alsception.pegasus.features.bootboard.controllers;

import java.util.List;
import java.util.Optional;
import org.alsception.pegasus.features.bootboard.entities.BBCard;
import org.alsception.pegasus.features.bootboard.error.BadRequestException;
import org.alsception.pegasus.features.bootboard.repositories.BBCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cards")
public class CardsController {

    @Autowired
    private BBCardRepository repository;
    
    @PostMapping
    public BBCard create(@RequestBody BBCard entry) throws BadRequestException 
    {
        try
        {
            return repository.create(entry);            
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }  
    }

    @GetMapping("/{id}")
    public Optional<BBCard> get(@PathVariable Long id) 
    {
        return repository.findById(id);
    }

    @GetMapping
    public List<BBCard> getAll() 
    {
        return repository.findAll();
    }
    
    //TODO: search by text and pagination
    
    @PutMapping()
    public Optional<BBCard> update(@RequestBody BBCard card) 
    {
        if(card==null)
            System.out.println("card is null.");
        
        if(null==card.getId()){
            throw new BadRequestException("Missing id");
        }
        try
        {
            repository.update(card);
            return repository.findById(card.getId());
        }
        catch(Exception ex)
        {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) 
    {
        try
        {
            int result = repository.delete(id);
            if(result == 1)
                return "Card deleted";
            else 
                return "No card found";
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }  
    }  
    
    @GetMapping("/swap/{id1}/{id2}")
    public String swap(@PathVariable Long id1, @PathVariable Long id2) 
    {
                
        if( this.repository.swapPositions(id1, id2) )
        {
            System.out.println("swaped ids: "+id1+","+id2);
            return "ok";
        }else{
            return "Error happened while swapping cards.";
        }
        
    }
    
}
