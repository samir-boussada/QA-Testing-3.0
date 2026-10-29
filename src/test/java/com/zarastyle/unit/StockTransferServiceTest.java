package com.zarastyle.unit;

import com.zarastyle.model.*;
import com.zarastyle.repository.GarmentStockRepository;
import com.zarastyle.repository.StockTransferRepository;
import com.zarastyle.service.StockTransferService;
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
class StockTransferServiceTest {

    @Mock
    private StockTransferRepository transferRepository;

    @Mock
    private GarmentStockRepository stockRepository;

    @InjectMocks
    private StockTransferService transferService;

    private Garment garment;
    private StoreBranch warehouse;
    private StoreBranch store;
    private GarmentStock warehouseStock;

    @BeforeEach
    void setUp() {
        garment = new Garment("ZARA-8800", "Linen Shirt", "100% Linen", "SS26", new BigDecimal("49.95"), Category.SHIRTS, "shirt.jpg");
        garment.setId(2L);

        warehouse = new StoreBranch("WH-01", "Arteixo Distribution Center", "Arteixo", true);
        warehouse.setId(1L);

        store = new StoreBranch("STORE-01", "Paris Rivoli", "Paris", false);
        store.setId(2L);

        warehouseStock = new GarmentStock(garment, warehouse, Size.L, 0, 50, "ZARA-8800-WH-L");
    }

    @Test
    @DisplayName("Should successfully request stock transfer when warehouse backroom has sufficient quantity")
    void testRequestTransfer_Success() {
        when(stockRepository.findByGarmentAndBranchAndSize(garment, warehouse, Size.L))
                .thenReturn(Optional.of(warehouseStock));
        when(transferRepository.save(any(StockTransfer.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        StockTransfer transfer = transferService.requestTransfer(garment, Size.L, warehouse, store, 15);

        assertThat(transfer.getQuantity()).isEqualTo(15);
        assertThat(transfer.getStatus()).isEqualTo(TransferStatus.PENDING);
        assertThat(transfer.getTransferRef()).startsWith("TRF-");
        verify(transferRepository).save(any(StockTransfer.class));
    }

    @Test
    @DisplayName("Should throw exception when source and destination branches are identical")
    void testRequestTransfer_SameBranch_ThrowsException() {
        assertThatThrownBy(() -> transferService.requestTransfer(garment, Size.L, warehouse, warehouse, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be the same");
    }

    @Test
    @DisplayName("Should complete transfer and move inventory from warehouse to store backroom")
    void testCompleteTransfer_Success() {
        StockTransfer transfer = new StockTransfer("TRF-12345", garment, Size.L, warehouse, store, 20);
        transfer.setId(99L);

        GarmentStock storeStock = new GarmentStock(garment, store, Size.L, 2, 5, "ZARA-8800-STR-L");

        when(transferRepository.findById(99L)).thenReturn(Optional.of(transfer));
        when(stockRepository.findByGarmentAndBranchAndSize(garment, warehouse, Size.L)).thenReturn(Optional.of(warehouseStock));
        when(stockRepository.findByGarmentAndBranchAndSize(garment, store, Size.L)).thenReturn(Optional.of(storeStock));
        when(transferRepository.save(any(StockTransfer.class))).thenReturn(transfer);

        StockTransfer completed = transferService.completeTransfer(99L);

        assertThat(completed.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(warehouseStock.getBackroomStock()).isEqualTo(30); // 50 - 20
        assertThat(storeStock.getBackroomStock()).isEqualTo(25); // 5 + 20
        verify(stockRepository).save(warehouseStock);
        verify(stockRepository).save(storeStock);
    }
}
