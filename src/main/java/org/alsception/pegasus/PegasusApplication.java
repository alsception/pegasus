package org.alsception.pegasus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PegasusApplication {

	public static void main(String[] args) 
        {
            // Manually set default profile if none is set
            System.setProperty("spring.profiles.active", "local");
           
            SpringApplication.run(PegasusApplication.class, args);
	}

}
