package com.zarastyle.service;

import com.zarastyle.model.*;
import com.zarastyle.repository.GarmentStockRepository;
import com.zarastyle.repository.StockTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StockTransferService {

    private final StockTransferRepository transferRepository;
    private final GarmentStockRepository stockRepository;

    public StockTransferService(StockTransferRepository transferRepository, GarmentStockRepository stockRepository) {
        this.transferRepository = transferRepository;
        this.stockRepository = stockRepository;
    }

    public StockTransfer requestTransfer(Garment garment, Size size, StoreBranch fromBranch, StoreBranch toBranch, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be greater than zero");
        }
        if (fromBranch.getId().equals(toBranch.getId())) {
            throw new IllegalArgumentException("Source and destination branches cannot be the same");
        }

        GarmentStock sourceStock = stockRepository.findByGarmentAndBranchAndSize(garment, fromBranch, size)
                .orElseThrow(() -> new IllegalStateException("Source branch does not carry this garment size"));

        if (sourceStock.getBackroomStock() < quantity) {
            throw new IllegalStateException("Source branch has insufficient backroom stock. Available: " + sourceStock.getBackroomStock());
        }

        String ref = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        StockTransfer transfer = new StockTransfer(ref, garment, size, fromBranch, toBranch, quantity);
        return transferRepository.save(transfer);
    }

    public StockTransfer completeTransfer(Long transferId) {
        StockTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found: " + transferId));

        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            throw new IllegalStateException("Transfer is already completed");
        }

        GarmentStock sourceStock = stockRepository.findByGarmentAndBranchAndSize(transfer.getGarment(), transfer.getFromBranch(), transfer.getSize())
                .orElseThrow(() -> new IllegalStateException("Source stock record missing"));

        if (sourceStock.getBackroomStock() < transfer.getQuantity()) {
            throw new IllegalStateException("Insufficient source stock to complete transfer");
        }

        // Deduct from source branch backroom
        sourceStock.setBackroomStock(sourceStock.getBackroomStock() - transfer.getQuantity());
        stockRepository.save(sourceStock);

        // Add to destination branch backroom
        GarmentStock destStock = stockRepository.findByGarmentAndBranchAndSize(transfer.getGarment(), transfer.getToBranch(), transfer.getSize())
                .orElseGet(() -> {
                    String barcode = "ZARA-" + transfer.getGarment().getId() + "-" + transfer.getToBranch().getBranchCode() + "-" + transfer.getSize().name();
                    return new GarmentStock(transfer.getGarment(), transfer.getToBranch(), transfer.getSize(), 0, 0, barcode);
                });

        destStock.setBackroomStock(destStock.getBackroomStock() + transfer.getQuantity());
        stockRepository.save(destStock);

        transfer.setStatus(TransferStatus.COMPLETED);
        return transferRepository.save(transfer);
    }

    @Transactional(readOnly = true)
    public List<StockTransfer> getAllTransfers() {
        return transferRepository.findAll();
    }
}
