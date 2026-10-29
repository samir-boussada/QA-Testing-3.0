# 🗺️ Requirements Traceability Matrix (RTM)

**Project:** ZaraStyle Inventory & Store Logistics System  
**Document ID:** RTM-ZARA-001  

| Req ID | Requirement Description | Unit Test | Integration Test | E2E Test | Manual Case | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :---: |
| **REQ-SIZ-01** | Garments must track independent stock per size variant (XS to XL) and branch. | `GarmentServiceTest` | `GarmentControllerIntegrationTest.testGetAllGarments` | `rfid-scan-and-replenish.spec.ts` | `TC_SIZ_001` | ✅ Passed |
| **REQ-RFID-01** | Scanning an RFID barcode returns exact garment, size, and store location. | N/A | `GarmentControllerIntegrationTest.testScanRfidBarcode_Success` | `rfid-scan-and-replenish.spec.ts` | `TC_RFID_001` | ✅ Passed |
| **REQ-RFID-02** | Invalid or unassigned RFID scans return HTTP 404 Not Found. | N/A | `GarmentControllerIntegrationTest.testScanRfidBarcode_NotFound` | N/A | `TC_RFID_002` | ✅ Passed |
| **REQ-REP-01** | Moving backroom stock to sales floor decrements backroom and increments floor. | `GarmentServiceTest.testReplenishFloorStock_Success` | `GarmentControllerIntegrationTest.testReplenishFloorStock_Success` | `rfid-scan-and-replenish.spec.ts` | `TC_REP_001` | ✅ Passed |
| **REQ-REP-02** | System blocks replenishing more than available backroom inventory. | `GarmentServiceTest.testReplenishFloorStock_InsufficientBackroomStock` | N/A | N/A | `TC_REP_002` | ✅ Passed |
| **REQ-TRF-01** | Inter-store transfers require source branch to have sufficient backroom stock. | `StockTransferServiceTest.testRequestTransfer_Success` | `StockTransferIntegrationTest.testRequestTransfer_Success` | N/A | `TC_TRF_001` | ✅ Passed |
| **REQ-TRF-02** | Completing transfer moves units from source backroom to destination backroom. | `StockTransferServiceTest.testCompleteTransfer_Success` | `StockTransferService.completeTransfer` | N/A | `TC_TRF_002` | ✅ Passed |
| **REQ-SEC-01** | Only Store Managers and Central Logistics can access the transfer management API. | N/A | `StockTransferIntegrationTest.testTransferApi_ForbiddenForAssociate` | N/A | `TC_SEC_001` | ✅ Passed |
| **REQ-MKD-01** | Applying seasonal markdown reduces garment price by specified percentage. | `GarmentServiceTest.testApplyMarkdownDiscount_Success` | `GarmentController.applyMarkdown` | `markdown-pricing.spec.ts` | `TC_MKD_001` | ✅ Passed |
