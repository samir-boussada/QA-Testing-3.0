package com.zarastyle.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StockTransferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Security: Store Associate cannot access stock transfer management API")
    @WithMockUser(username = "associate@zara.com", authorities = {"ROLE_ASSOCIATE"})
    void testTransferApi_ForbiddenForAssociate() throws Exception {
        mockMvc.perform(get("/api/transfers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Security: Store Manager can access stock transfer API")
    @WithMockUser(username = "manager@zara.com", authorities = {"ROLE_MANAGER"})
    void testTransferApi_AllowedForManager() throws Exception {
        mockMvc.perform(get("/api/transfers"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Integration: Request stock transfer between warehouse and store")
    @WithMockUser(username = "manager@zara.com", authorities = {"ROLE_MANAGER"})
    void testRequestTransfer_Success() throws Exception {
        Map<String, Object> payload = Map.of(
                "garmentId", 1,
                "size", "M",
                "fromBranchId", 2, // Warehouse
                "toBranchId", 1,   // Paris Store
                "quantity", 5
        );

        mockMvc.perform(post("/api/transfers/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transferRef").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
