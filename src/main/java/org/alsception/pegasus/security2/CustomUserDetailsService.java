package org.alsception.pegasus.security2;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import org.alsception.pegasus.features.users.ABAUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.alsception.pegasus.features.users.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

       private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
//        System.out.println("CustomUserDetailsService: ---------------------- loadUserByUsername: "+username);
//        // Load your user from database, i come here
//        ABAUser user = repository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//        
        
        System.out.println("Trying to find user: " + username);
        Optional<ABAUser> user = repository.findByUsername(username);
        if (user.isEmpty()) {
            System.err.println("User not found in DB: " + username);
        } else {
            System.out.println("Found user in DB: " + user.get().getUsername());
            
            System.out.println("user: ");
            System.out.println(user.toString());
        }
        
        
        
        
        

        //System.out.println("user found");// i dont come here 
        // Map your User entity to Spring's UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.get().getUsername())
                .password(user.get().getPassword()) // should already be bcrypt encoded!
                .roles(user.get().getRole().toString()) // or authorities
                .build();
    }
}
