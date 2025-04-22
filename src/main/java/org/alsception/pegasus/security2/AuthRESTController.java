package org.alsception.pegasus.security2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.alsception.pegasus.security2.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRESTController {

    
    
    /*
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO newUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // Trigger HTTP Basic Auth credentials removal
        response.setHeader("WWW-Authenticate", "Basic realm=\"Andromeda\"");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }end old*/
    
    @Autowired    
    private CustomUserDetailsService userService;
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthRESTController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                          CustomUserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) 
    {
        // Save user with encrypted password (this should be done in a service layer)
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        UserDetails user = User.withUsername(userDTO.getUsername())
                               .password(encryptedPassword)
                               .roles("USER")
                               .build();

        try {
           // UserDTO newUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) 
    {
        System.out.println("AuthRESTController: authenticating user: " + userDTO.getUsername() + " pass: " + userDTO.getPassword());

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
            );
        } catch (Exception e) {
            System.out.println("AuthRESTController: authentication failed: " + e.getMessage());
            return ResponseEntity
                    .status(401)
                    .body("Authentication failed: " + e.getMessage());
        }

        System.out.println("AuthRESTController: loading user: " + userDTO.getUsername());
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getUsername());
        String token = jwtUtils.generateJwtToken(userDetails);
        System.out.println("AuthRESTController: created token: " + token);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    
}
