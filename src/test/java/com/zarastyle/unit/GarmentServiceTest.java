package com.zarastyle.unit;

import com.zarastyle.model.*;
import com.zarastyle.repository.GarmentRepository;
import com.zarastyle.repository.GarmentStockRepository;
import com.zarastyle.service.GarmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GarmentServiceTest {

    @Mock
    private GarmentRepository garmentRepository;

    @Mock
    private GarmentStockRepository garmentStockRepository;

    @InjectMocks
    private GarmentService garmentService;

    private Garment garment;
    private GarmentStock stock;

    @BeforeEach
    void setUp() {
        garment = new Garment("ZARA-TEST-01", "Wool Trench Coat", "100% Wool", "FW26", new BigDecimal("100.00"), Category.OUTERWEAR, "img.jpg");
        garment.setId(1L);

        StoreBranch branch = new StoreBranch("PAR-01", "Paris Champs-Élysées", "Paris", false);
        branch.setId(10L);

        stock = new GarmentStock(garment, branch, Size.M, 2, 8, "ZARA-TEST-PAR-M");
        stock.setId(100L);
    }

    @Test
    @DisplayName("Should move stock from backroom to sales floor when backroom stock is sufficient")
    void testReplenishFloorStock_Success() {
        when(garmentStockRepository.findById(100L)).thenReturn(Optional.of(stock));
        when(garmentStockRepository.save(any(GarmentStock.class))).thenReturn(stock);

        GarmentStock updated = garmentService.replenishFloorStock(100L, 5);

        assertThat(updated.getFloorStock()).isEqualTo(7); // 2 + 5
        assertThat(updated.getBackroomStock()).isEqualTo(3); // 8 - 5
        verify(garmentStockRepository).save(stock);
    }

    @Test
    @DisplayName("Should throw exception when attempting to replenish more than available backroom stock")
    void testReplenishFloorStock_InsufficientBackroomStock() {
        when(garmentStockRepository.findById(100L)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> garmentService.replenishFloorStock(100L, 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient backroom stock");
    }

    @Test
    @DisplayName("Should calculate markdown discount accurately (e.g. 25% off 100.00 = 75.00)")
    void testApplyMarkdownDiscount_Success() {
        when(garmentRepository.findById(1L)).thenReturn(Optional.of(garment));
        when(garmentRepository.save(any(Garment.class))).thenReturn(garment);

        Garment updated = garmentService.applyMarkdownDiscount(1L, new BigDecimal("25.00"));

        assertThat(updated.getDiscountedPrice()).isEqualByComparingTo("75.00");
        verify(garmentRepository).save(garment);
    }

    @Test
    @DisplayName("Should reject invalid markdown discount percentage greater than 90%")
    void testApplyMarkdownDiscount_InvalidPercentage() {
        assertThatThrownBy(() -> garmentService.applyMarkdownDiscount(1L, new BigDecimal("95.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 0% and 90%");
    }
}
