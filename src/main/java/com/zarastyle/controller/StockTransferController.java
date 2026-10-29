package com.zarastyle.controller;

import com.zarastyle.model.Garment;
import com.zarastyle.model.Size;
import com.zarastyle.model.StockTransfer;
import com.zarastyle.model.StoreBranch;
import com.zarastyle.repository.GarmentRepository;
import com.zarastyle.repository.StoreBranchRepository;
import com.zarastyle.service.StockTransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class StockTransferController {

    private final StockTransferService transferService;
    private final GarmentRepository garmentRepository;
    private final StoreBranchRepository branchRepository;

    public StockTransferController(StockTransferService transferService,
                                  GarmentRepository garmentRepository,
                                  StoreBranchRepository branchRepository) {
        this.transferService = transferService;
        this.garmentRepository = garmentRepository;
        this.branchRepository = branchRepository;
    }

    @GetMapping
    public ResponseEntity<List<StockTransfer>> getAllTransfers() {
        return ResponseEntity.ok(transferService.getAllTransfers());
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestTransfer(@RequestBody Map<String, Object> payload) {
        try {
            Long garmentId = Long.valueOf(payload.get("garmentId").toString());
            Size size = Size.valueOf(payload.get("size").toString());
            Long fromBranchId = Long.valueOf(payload.get("fromBranchId").toString());
            Long toBranchId = Long.valueOf(payload.get("toBranchId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            Garment garment = garmentRepository.findById(garmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Garment not found"));
            StoreBranch fromBranch = branchRepository.findById(fromBranchId)
                    .orElseThrow(() -> new IllegalArgumentException("From branch not found"));
            StoreBranch toBranch = branchRepository.findById(toBranchId)
                    .orElseThrow(() -> new IllegalArgumentException("To branch not found"));

            StockTransfer transfer = transferService.requestTransfer(garment, size, fromBranch, toBranch, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeTransfer(@PathVariable Long id) {
        try {
            StockTransfer transfer = transferService.completeTransfer(id);
            return ResponseEntity.ok(transfer);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
