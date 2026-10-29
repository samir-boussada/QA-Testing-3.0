package com.zarastyle.repository;

import com.zarastyle.model.StockTransfer;
import com.zarastyle.model.StoreBranch;
import com.zarastyle.model.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    Optional<StockTransfer> findByTransferRef(String transferRef);
    List<StockTransfer> findByToBranchOrFromBranch(StoreBranch toBranch, StoreBranch fromBranch);
    List<StockTransfer> findByStatus(TransferStatus status);
}
