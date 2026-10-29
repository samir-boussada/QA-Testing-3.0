package com.zarastyle.repository;

import com.zarastyle.model.Garment;
import com.zarastyle.model.GarmentStock;
import com.zarastyle.model.Size;
import com.zarastyle.model.StoreBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GarmentStockRepository extends JpaRepository<GarmentStock, Long> {
    Optional<GarmentStock> findByRfidBarcode(String rfidBarcode);
    List<GarmentStock> findByGarmentAndBranch(Garment garment, StoreBranch branch);
    Optional<GarmentStock> findByGarmentAndBranchAndSize(Garment garment, StoreBranch branch, Size size);
    List<GarmentStock> findByBranch(StoreBranch branch);
    List<GarmentStock> findByFloorStockLessThan(int threshold);
}
