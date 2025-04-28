package org.alsception.pegasus.features.bootboard.controllers;

import java.util.List;
import java.util.Optional;
import org.alsception.pegasus.features.bootboard.entities.BBList;
import org.alsception.pegasus.features.bootboard.error.BadRequestException;
import org.alsception.pegasus.features.bootboard.repositories.BBListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lists")
public class ListsController {

    @Autowired
    private BBListRepository listRepository;
    
    @PostMapping
    public BBList create(@RequestBody BBList entry) throws BadRequestException 
    {
        try
        {
            return listRepository.create(entry);            
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }  
    }

    @GetMapping("/{id}")
    public Optional<BBList> get(@PathVariable Long id) 
    {
        return listRepository.findById(id);
    }
    
    @GetMapping("/{id}/cards")
    public Optional<BBList> getWithCards(@PathVariable Long id) 
    {
        return listRepository.findByIdWithCards(id);
    }
    
    @GetMapping()
    public Optional<List<BBList>> getAll() 
    {
        return listRepository.findAll();
    }
    
    @GetMapping("/cards")
    public List<BBList> getAllWithCards() 
    {
        return listRepository.findAllWithCards();
    }
    
    //TODO: search by text and pagination
    
    @PutMapping()
    public BBList update(@RequestBody BBList list) 
    {
        if(list==null)
            System.out.println("List is null...");
        
        if(null==list.getId()){
            throw new BadRequestException("Missing id");
        }
        try
        {
            //TODO: update frontend to return only list without cards here
            //For now we need it here
            
            listRepository.update(list);
            
            return listRepository.findByIdWithCards(list.getId()).get();
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
            int result = listRepository.delete(id);
            if(result >= 1)
                return "Deleted list + "+(result-1)+" cards";
            else 
                return "No list found";
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }  
    }
    
    
    
}
