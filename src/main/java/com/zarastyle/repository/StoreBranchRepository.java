package com.zarastyle.repository;

import com.zarastyle.model.StoreBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StoreBranchRepository extends JpaRepository<StoreBranch, Long> {
    Optional<StoreBranch> findByBranchCode(String branchCode);
}
