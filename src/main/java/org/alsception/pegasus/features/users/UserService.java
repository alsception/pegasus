package org.alsception.pegasus.features.users;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;    

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDTO createUser(UserDTO userDTO) {
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

    public List<UserDTO> getAllUsers() {
        return repository.findAll()
                         .stream()
                         .map(UserDTO::new) // Convert User -> UserDTO
                         .collect(Collectors.toList());
    }
}
