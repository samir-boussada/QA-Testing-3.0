package com.zarastyle.service;

import com.zarastyle.model.*;
import com.zarastyle.repository.GarmentRepository;
import com.zarastyle.repository.GarmentStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GarmentService {

    private final GarmentRepository garmentRepository;
    private final GarmentStockRepository garmentStockRepository;

    public GarmentService(GarmentRepository garmentRepository, GarmentStockRepository garmentStockRepository) {
        this.garmentRepository = garmentRepository;
        this.garmentStockRepository = garmentStockRepository;
    }

    @Transactional(readOnly = true)
    public List<Garment> getAllGarments() {
        return garmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Garment> getGarmentById(Long id) {
        return garmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Garment> getGarmentByRefCode(String refCode) {
        return garmentRepository.findByRefCode(refCode);
    }

    @Transactional(readOnly = true)
    public List<Garment> searchGarments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return garmentRepository.findAll();
        }
        return garmentRepository.findByNameContainingIgnoreCaseOrRefCodeContainingIgnoreCase(query.trim(), query.trim());
    }

    @Transactional(readOnly = true)
    public Optional<GarmentStock> findStockByRfidBarcode(String rfidBarcode) {
        return garmentStockRepository.findByRfidBarcode(rfidBarcode.trim());
    }

    public GarmentStock replenishFloorStock(Long stockId, int quantityToMove) {
        if (quantityToMove <= 0) {
            throw new IllegalArgumentException("Quantity to move must be positive");
        }

        GarmentStock stock = garmentStockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Garment stock record not found: " + stockId));

        if (stock.getBackroomStock() < quantityToMove) {
            throw new IllegalStateException("Insufficient backroom stock. Available: " + stock.getBackroomStock());
        }

        stock.setBackroomStock(stock.getBackroomStock() - quantityToMove);
        stock.setFloorStock(stock.getFloorStock() + quantityToMove);
        return garmentStockRepository.save(stock);
    }

    public Garment applyMarkdownDiscount(Long garmentId, BigDecimal discountPercentage) {
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || discountPercentage.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0% and 90%");
        }

        Garment garment = garmentRepository.findById(garmentId)
                .orElseThrow(() -> new IllegalArgumentException("Garment not found: " + garmentId));

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal newPrice = garment.getBasePrice().multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);

        garment.setDiscountedPrice(newPrice);
        return garmentRepository.save(garment);
    }

    public Garment saveGarment(Garment garment) {
        return garmentRepository.save(garment);
    }
}
