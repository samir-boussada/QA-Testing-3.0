# 📋 Master Test Plan (MTP) — ZaraStyle Inventory & Store Logistics

**Project:** ZaraStyle Enterprise Fashion Inventory Management  
**Author:** Samir Boussada (QA & SDET Engineer)  
**Target Environment:** Localhost / Apache Tomcat 10+ / Spring Boot 3.3.5 / Playwright  
**System Type:** Retail Supply Chain & Multi-Branch Inventory Tracking  

---

## 1. Testing Objective & Scope
The objective is to verify that Zara's core retail logic operates with high data integrity across stores and distribution centers:
* **Size Matrices (XS, S, M, L, XL):** Ensuring inventory counts remain accurate per branch and size variant.
* **RFID Barcode Lookup:** Verifying sub-second barcode scans accurately match physical items.
* **Store Floor Replenishment:** Ensuring stock movement from Backroom $\to$ Sales Floor updates both balances without race conditions.
* **Inter-Branch Stock Transfers:** Validating transfers between the Central Arteixo Warehouse and retail stores (Paris, London).
* **Role Security:** Ensuring Store Associates cannot approve transfers or alter pricing markdowns.

---

## 2. Test Architecture & Execution Levels

| Level | Focus | Framework & Tooling |
| :--- | :--- | :--- |
| **Unit Testing** | Replenishment math, markdown percentage logic, transfer validation | JUnit 5, AssertJ, Mockito |
| **Integration Testing** | Spring Security roles, RFID scan REST API, transfer lifecycle | Spring Boot Test, MockMvc |
| **E2E Automation** | Scanning barcodes, clicking size matrix pills, replenishing floor stock | Playwright, TypeScript |
| **Manual QA Testing** | Edge cases: 0-stock transfers, exceeding backroom limits, UI responsiveness | Exploratory, BVA, ECP |

---

## 3. Exit Criteria
* 100% of automated Unit & Integration tests passing (`mvn test`).
* 100% of Playwright E2E scenarios passing on Chromium and Firefox.
* Zero open Critical/Blocker inventory discrepancies.
