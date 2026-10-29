package com.zarastyle.config;

import com.zarastyle.model.*;
import com.zarastyle.repository.GarmentRepository;
import com.zarastyle.repository.GarmentStockRepository;
import com.zarastyle.repository.StoreBranchRepository;
import com.zarastyle.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(StoreBranchRepository branchRepository,
                               GarmentRepository garmentRepository,
                               GarmentStockRepository stockRepository,
                               UserService userService) {
        return args -> {
            // 1. Branches
            StoreBranch parisStore = branchRepository.findByBranchCode("PAR-01").orElseGet(() ->
                    branchRepository.save(new StoreBranch("PAR-01", "Paris Flagship - Champs-Élysées", "Paris", false)));

            StoreBranch centralWarehouse = branchRepository.findByBranchCode("BCN-WH").orElseGet(() ->
                    branchRepository.save(new StoreBranch("BCN-WH", "Arteixo Distribution Hub", "A Coruña / Barcelona", true)));

            StoreBranch londonStore = branchRepository.findByBranchCode("LDN-01").orElseGet(() ->
                    branchRepository.save(new StoreBranch("LDN-01", "London Flagship - Oxford Street", "London", false)));

            // 2. Staff Accounts
            userService.createStaffIfMissing("manager@zara.com", "Camille Laurent (Store Manager)", "ZaraManager123!", Role.ROLE_MANAGER, parisStore);
            userService.createStaffIfMissing("associate@zara.com", "Lucas Martin (Floor Associate)", "ZaraStaff123!", Role.ROLE_ASSOCIATE, parisStore);
            userService.createStaffIfMissing("logistics@zara.com", "Mateo Morales (Central Logistics)", "ZaraLogistics123!", Role.ROLE_LOGISTICS, centralWarehouse);

            // 3. Zara Fast-Fashion Catalog & Size Matrices
            if (garmentRepository.count() == 0) {
                Garment coat = new Garment(
                        "ZARA-7941-BLK",
                        "Tailored Double-Breasted Wool Blend Coat",
                        "75% Manteco Wool, 25% Polyamide",
                        "FW26",
                        new BigDecimal("189.00"),
                        Category.OUTERWEAR,
                        "https://images.unsplash.com/photo-1539533018447-63fcce667883?w=500"
                );
                garmentRepository.save(coat);

                Garment dress = new Garment(
                        "ZARA-4120-EMR",
                        "Satin Midi Dress with Draped Neckline",
                        "100% Viscose Silk Touch",
                        "FW26",
                        new BigDecimal("79.95"),
                        Category.DRESSES,
                        "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=500"
                );
                garmentRepository.save(dress);

                Garment knit = new Garment(
                        "ZARA-3318-ECR",
                        "100% Cashmere Ribbed Turtleneck Sweater",
                        "100% Inner Mongolian Cashmere",
                        "FW26",
                        new BigDecimal("129.00"),
                        Category.KNITWEAR,
                        "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=500"
                );
                garmentRepository.save(knit);

                Garment blazer = new Garment(
                        "ZARA-6091-NVY",
                        "Structured Oversized Fluid Blazer",
                        "98% Wool, 2% Elastane",
                        "FW26",
                        new BigDecimal("149.00"),
                        Category.OUTERWEAR,
                        "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=500"
                );
                garmentRepository.save(blazer);

                // Add size matrices for Coat across XS, S, M, L, XL
                Size[] sizes = {Size.XS, Size.S, Size.M, Size.L, Size.XL};
                int[] parisFloor = {2, 5, 8, 4, 1};
                int[] parisBack = {5, 12, 18, 10, 3};
                for (int i = 0; i < sizes.length; i++) {
                    stockRepository.save(new GarmentStock(
                            coat, parisStore, sizes[i], parisFloor[i], parisBack[i],
                            "ZARA-7941-PAR-" + sizes[i].name()
                    ));
                    // Also seed central warehouse with bulk stock
                    stockRepository.save(new GarmentStock(
                            coat, centralWarehouse, sizes[i], 0, 150,
                            "ZARA-7941-WH-" + sizes[i].name()
                    ));
                }

                // Add size matrices for Dress
                int[] dressFloor = {3, 6, 4, 2, 0};
                int[] dressBack = {8, 14, 10, 5, 2};
                for (int i = 0; i < sizes.length; i++) {
                    stockRepository.save(new GarmentStock(
                            dress, parisStore, sizes[i], dressFloor[i], dressBack[i],
                            "ZARA-4120-PAR-" + sizes[i].name()
                    ));
                    stockRepository.save(new GarmentStock(
                            dress, centralWarehouse, sizes[i], 0, 200,
                            "ZARA-4120-WH-" + sizes[i].name()
                    ));
                }
            }
        };
    }
}
