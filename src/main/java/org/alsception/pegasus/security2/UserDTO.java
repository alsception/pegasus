package org.alsception.pegasus.security2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alsception.pegasus.features.users.ABAUser;

@Data //Lombook for getters and setters
@NoArgsConstructor
public class UserDTO 
{
    private Long id;
    private String username;
    private String role;    

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
    }
}
