package org.alsception.pegasus.features.users;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<ABAUser, Long> 
{
    Optional<ABAUser> findByUsername(String username);
}