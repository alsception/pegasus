package org.alsception.pegasus.features.users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Lombook for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "aba_users")
public class ABAUser 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private ABAUserRole role;
    
    @Column
    private String firstName;
    
    @Column
    private String lastName;
    
    @Column(name = "active")
    private Boolean active;    
    
//    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<ABAPost> posts = new ArrayList<>();
//    
    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
    }
}
