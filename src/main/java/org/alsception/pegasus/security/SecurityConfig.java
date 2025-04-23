package org.alsception.pegasus.security;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public SecurityConfig(PasswordEncoder passwordEncoder,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService customUserDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostConstruct
    public void printProfile() {
        if (activeProfile == null || activeProfile.isEmpty()) {
            logger.warn("⚠️ No active profile set. Running with default settings.");
        } else {
            logger.info("✅ Active Spring profile: {}", activeProfile);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Creating security filter chain");
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                // Allow unauthenticated access to index.html
                .requestMatchers("/", "/index.html", "/static/**", "/assets/**", "/favicon.ico").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() 
    {
        logger.info("Creating CorsConfigurationSource");
        
        CorsConfiguration configuration = new CorsConfiguration();

        // Dynamic CORS based on profile
        if ("production".equals(activeProfile)) 
        {
            //TODO: LOAD THIS FROM APP.PROPERTIES
            logger.info("Whitelisting production: https://your-frontend.com");
            
            configuration.setAllowedOrigins(Collections.singletonList("https://your-frontend.com"));
        } 
        else 
        {
            logger.info("Whitelisting dev server: http://localhost:5173");
            
            configuration.setAllowedOrigins(
                    Arrays.asList(
                            "http://localhost:5173",    //Local dev
                            "http://192.168.1.70:8080"  //Local dev network
                    )
            );
        }
        logger.trace("Setting up CORS details:");
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        logger.trace("Allowed methods set: GET, POST, PUT, DELETE, OPTIONS");
        
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        logger.trace("Allowed headers set: " + "Authorization " + "Cache-Control " + "Content-Type ");
        
        configuration.setAllowCredentials(true);
        logger.trace("Allowed credentials set");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}
