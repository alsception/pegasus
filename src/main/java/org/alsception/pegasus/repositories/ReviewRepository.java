package org.alsception.pegasus.repositories;


import java.util.List;
import org.alsception.pegasus.entities.PGSReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<PGSReview, Long> {
    List<PGSReview> findByProductId(Long productId);
}