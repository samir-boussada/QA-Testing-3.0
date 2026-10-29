package com.zarastyle.controller;

import com.zarastyle.model.Garment;
import com.zarastyle.model.GarmentStock;
import com.zarastyle.service.GarmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/garments")
public class GarmentController {

    private final GarmentService garmentService;

    public GarmentController(GarmentService garmentService) {
        this.garmentService = garmentService;
    }

    @GetMapping
    public ResponseEntity<List<Garment>> getGarments(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(garmentService.searchGarments(search));
        }
        return ResponseEntity.ok(garmentService.getAllGarments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Garment> getGarmentById(@PathVariable Long id) {
        return garmentService.getGarmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/scan")
    public ResponseEntity<?> scanRfidBarcode(@RequestParam String barcode) {
        return garmentService.findStockByRfidBarcode(barcode)
                .map(stock -> {
                    return ResponseEntity.ok(Map.of(
                            "stockId", stock.getId(),
                            "rfidBarcode", stock.getRfidBarcode(),
                            "garmentRef", stock.getGarment().getRefCode(),
                            "garmentName", stock.getGarment().getName(),
                            "size", stock.getSize().name(),
                            "branch", stock.getBranch().getName(),
                            "floorStock", stock.getFloorStock(),
                            "backroomStock", stock.getBackroomStock(),
                            "totalStock", stock.getTotalStock(),
                            "price", stock.getGarment().getDiscountedPrice()
                    ));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "No item found for barcode: " + barcode)));
    }

    @PostMapping("/replenish")
    public ResponseEntity<?> replenishFloorStock(@RequestBody Map<String, Object> payload) {
        try {
            Long stockId = Long.valueOf(payload.get("stockId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            GarmentStock updated = garmentService.replenishFloorStock(stockId, quantity);
            return ResponseEntity.ok(Map.of(
                    "message", "Sales floor replenished successfully",
                    "stockId", updated.getId(),
                    "floorStock", updated.getFloorStock(),
                    "backroomStock", updated.getBackroomStock()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/markdown")
    public ResponseEntity<?> applyMarkdown(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            BigDecimal percentage = new BigDecimal(payload.get("percentage").toString());
            Garment updated = garmentService.applyMarkdownDiscount(id, percentage);
            return ResponseEntity.ok(Map.of(
                    "message", "Markdown applied successfully",
                    "garmentId", updated.getId(),
                    "basePrice", updated.getBasePrice(),
                    "discountedPrice", updated.getDiscountedPrice()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
