package com.zarastyle.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GarmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration: List all garments with size stock matrices")
    void testGetAllGarments() throws Exception {
        mockMvc.perform(get("/api/garments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].refCode").exists())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @DisplayName("Integration: Scan RFID barcode and return exact garment & stock")
    void testScanRfidBarcode_Success() throws Exception {
        mockMvc.perform(get("/api/garments/scan").param("barcode", "ZARA-7941-PAR-M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rfidBarcode").value("ZARA-7941-PAR-M"))
                .andExpect(jsonPath("$.size").value("M"))
                .andExpect(jsonPath("$.garmentRef").value("ZARA-7941-BLK"));
    }

    @Test
    @DisplayName("Integration: Scan invalid RFID barcode returns 404 Not Found")
    void testScanRfidBarcode_NotFound() throws Exception {
        mockMvc.perform(get("/api/garments/scan").param("barcode", "NON-EXISTENT-BARCODE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Integration: Move backroom stock to sales floor")
    void testReplenishFloorStock_Success() throws Exception {
        Map<String, Object> payload = Map.of(
                "stockId", 1,
                "quantity", 2
        );

        mockMvc.perform(post("/api/garments/replenish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sales floor replenished successfully"));
    }
}
