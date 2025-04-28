package org.alsception.pegasus.features.users;

import java.util.List;
import java.util.stream.Collectors;
import org.alsception.pegasus.core.config.security.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;    
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDTO createUser(UserDTO userDTO) 
    {
        // Convert DTO to Entity
        ABAUser user = new ABAUser();
        user.setUsername(userDTO.getUsername());
        System.out.println("encoding password: "+userDTO.getPassword());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(ABAUserRole.USER);
        
        // Save the user and convert it back to DTO
        ABAUser savedUser = repository.save(user);
        return new UserDTO(savedUser);  // Convert the saved entity to DTO
    }
    
    public ABAUser saveUser(ABAUser user) 
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(ABAUserRole.USER);//TODO: take from frontend as param
        user.setActive(Boolean.TRUE);
        
        ABAUser savedUser = repository.save(user);
        
        logger.info("User created: "+user.getUsername());
        return savedUser; 
    }

    public List<UserDTO> getAllUsers() {
        return repository.findAll()
                         .stream()
                         .map(UserDTO::new) // Convert User -> UserDTO
                         .collect(Collectors.toList());
    }
}
