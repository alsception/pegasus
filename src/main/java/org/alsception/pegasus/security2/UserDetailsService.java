//package org.alsception.pegasus.security2;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import org.alsception.pegasus.security2.UserDTO;
//import org.alsception.pegasus.entities.ABAUser;
//import org.alsception.pegasus.entities.ABAUserRole;
//import org.alsception.pegasus.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserDetailsService {
//
//    private final UserRepository repository;
//    
//    @Autowired
//    private PasswordEncoder passwordEncoder;    
//
//    @Autowired
//    public UserDetailsService(UserRepository repository) {
//        this.repository = repository;
//    }
//
//    public UserDTO createUser(UserDTO userDTO) {
//        // Convert DTO to Entity
//        ABAUser user = new ABAUser();
//        user.setUsername(userDTO.getUsername());
//        System.out.println("encoding password: "+userDTO.getPassword());
//        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//        user.setRole(ABAUserRole.USER);
//        
//        // Save the user and convert it back to DTO
//        ABAUser savedUser = repository.save(user);
//        return new UserDTO(savedUser);  // Convert the saved entity to DTO
//    }
//
//    public List<UserDTO> getAllUsers() {
//        return repository.findAll()
//                         .stream()
//                         .map(UserDTO::new) // Convert User -> UserDTO
//                         .collect(Collectors.toList());
//    }
//    
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        
//        System.out.println("loadUserByUsername: "+username);
//        // Load your user from database
//        ABAUser user = repository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//        // Map your User entity to Spring's UserDetails
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUsername())
//                .password(user.getPassword()) // should already be bcrypt encoded!
//                .roles(user.getRole().toString()) // or authorities
//                .build();
//    }
//}
