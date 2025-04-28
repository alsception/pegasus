package org.alsception.pegasus.features.bootboard.controllers;

import java.util.List;
import java.util.Optional;
import org.alsception.pegasus.features.bootboard.entities.BBBoard;
import org.alsception.pegasus.features.bootboard.error.BadRequestException;
import org.alsception.pegasus.features.bootboard.repositories.BBBoardRepository;
import org.alsception.pegasus.features.bootboard.services.TemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/boards")
public class BoardsController {

    @Autowired
    private BBBoardRepository repository;    
    
    private final TemplateEngine templateEngine;

    @Autowired
    public BoardsController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
    
    @PostMapping
    public BBBoard create(@RequestBody BBBoard entry) throws BadRequestException 
    {
        try
        {
            if(entry != null){
                if( entry.getType() != null ){
                    if( entry.getType().equalsIgnoreCase("CREATE_TEMPLATE_T001_DAILY_BOARD")){
                        return templateEngine.createTemplate_DailyBoard(entry);
                    }
                }
            }
            
            return repository.create(entry);            
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }  
    }

    @GetMapping("/{id}")
    public Optional<BBBoard> get(@PathVariable Long id) 
    {
        return repository.findById(id);
    }
    
    @GetMapping("/{id}/lists")
    public Optional<BBBoard> getWithLists(@PathVariable Long id) 
    {
        return repository.findByIdWithLists(id);
    }
    
    @GetMapping()
    public Optional<List<BBBoard>> getAll() 
    {
        return repository.findAll();
    }
    
    @GetMapping("/{id}/next")
    public Optional<BBBoard> getNext(@PathVariable Long id) 
    {
        return repository.findNext(id);
    }
    
    @GetMapping("/{id}/prev")
    public Optional<BBBoard> getPrev(@PathVariable Long id) 
    {
        System.out.println("get prev");
        return repository.findPrev(id);
    }
    
    @GetMapping("/lists")
    public /*List<BBBoard>*/void getAllWithLists() 
    {
        System.out.println("get all with lists");
        //return repository.findAllWithCards();
    }
    
    //TODO: search by text and pagination
    
    @GetMapping("/search/{text}")
    public Optional<List<BBBoard>> search(@PathVariable String text)
    {        
        return repository.findByTitle(text);
    }
    
    
    @PutMapping()
    public Optional<BBBoard> update(@RequestBody BBBoard board) 
    {
        if(board==null)
            System.out.println("board is null...");
        
        if(null==board.getId()){
            throw new BadRequestException("Missing id");
        }
        try
        {
            repository.update(board);
            return repository.findById(board.getId());
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
            if(result >= 1)
                return "Board deleted and "+ (--result)+" children lists and cards";
            else 
                return "No board found";
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }  
    }       
    
}
