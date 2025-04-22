package org.alsception.pegasus.features.users;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController 
{
    @Autowired
    private UserService userService;
    
    @GetMapping
    public List<UserDTO> getUsers(@RequestParam(required = false) String search, @RequestParam(required = false) String code, @RequestParam(required = false) String name)
    {        
        return userService.getAllUsers();
    }    
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ABAUser> getUserById(@PathVariable Long id) 
//    {
//        return userService.getUserById(id)
//            .map(ResponseEntity::ok)
//            .orElse(ResponseEntity.notFound().build());
//    }
//    
//    @PostMapping
//    public ABAUser createUser(@RequestBody ABAUser user) 
//    {
//        return userService.createUser(user);
//    }
//    
//    @PutMapping("/{id}")
//    public ResponseEntity<ABAUser> updateUser(@PathVariable Long id, @RequestBody ABAUser updatedUser) 
//    {
//        try {
//            ABAUser user = userService.updateUser(id, updatedUser);
//            return ResponseEntity.ok(user);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//        
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) 
//    {
//        if (userService.existsById(id)) 
//        {        
//            userService.deleteUser(id);
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
//    }
}
