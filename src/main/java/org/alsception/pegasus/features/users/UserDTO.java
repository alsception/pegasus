package org.alsception.pegasus.features.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Lombook for getters and setters
@NoArgsConstructor
public class UserDTO 
{
    private Long id;
    private String username;
    private String role;    
    private LocalDateTime created;    
    private String firstName;    
    private String lastName;    
    private Boolean active;    

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;    

    public UserDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public UserDTO(ABAUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().toString();
        this.created = user.getCreated();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.active = user.getActive();
    }
}
