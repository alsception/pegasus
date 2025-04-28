package org.alsception.pegasus.features.bootboard.controllers;

import java.util.List;
import org.alsception.pegasus.features.bootboard.error.BadRequestException;
import org.alsception.pegasus.features.bootboard.entities.BBUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.alsception.pegasus.features.bootboard.repositories.BBUserRepository;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    @Autowired
    private BBUserRepository repository;
    
    @PostMapping
    public int createUser(@RequestBody BBUser user) throws BadRequestException 
    {
        if(user.getUsername() == null || "".equals(user.getUsername()))
        {
            throw new BadRequestException("Username is mandatory");
        }
        user.setActive(true);
        try
        {
            int result = repository.create(user);
            return result;
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }  
    }

    @GetMapping
    public List<BBUser> getUsers(
            @RequestParam(required = false) String name) 
    {
        //If name is null or empty -> return all users
        //Pageable pageable = PageRequest.of(page, size);
        return repository.findAll();
    }
    
}
