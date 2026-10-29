package com.zarastyle.repository;

import com.zarastyle.model.Category;
import com.zarastyle.model.Garment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GarmentRepository extends JpaRepository<Garment, Long> {
    Optional<Garment> findByRefCode(String refCode);
    List<Garment> findByCategory(Category category);
    List<Garment> findByNameContainingIgnoreCaseOrRefCodeContainingIgnoreCase(String name, String refCode);
}
