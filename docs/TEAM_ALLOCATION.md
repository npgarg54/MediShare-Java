# Three-Member Team Allocation

The split is by module ownership. Before parallel implementation, all members should agree on package names, database columns, shared model fields and service method signatures. All members should review the architecture, database schema and end-to-end workflow.

## Member 1 - Foundation, Authentication and Facility Management

**Phase 1 / Beta v0.2 ownership**
- `Main.java` - startup orchestration and application initialization.
- `dao/DatabaseConnection.java` - shared JDBC connection configuration.
- `model/User.java`, `model/Admin.java`, `model/FacilityManager.java`, `model/Facility.java`.
- `enums/UserRole.java`, `enums/FacilityType.java`.
- `dao/UserDAO.java`, `dao/FacilityDAO.java`.
- `service/AuthService.java`, `service/FacilityService.java`, `service/DataSeeder.java`.
- Authentication logic, role resolution, facility identity and idempotent demo-data setup.

**Phase 2 ownership**
- Admin / facility administration refinements.
- Authentication and access-control review; integration validation.

## Member 2 - Medicine Inventory and Monitoring

**Phase 1 / Beta v0.2 ownership**
- `model/Medicine.java`, `model/InventoryBatch.java`, `model/StockSummary.java`, `model/DashboardSummary.java`.
- `dao/MedicineDAO.java`, `dao/InventoryDAO.java`, `dao/InventoryRepository.java`.
- `service/MedicineService.java`, `service/InventoryService.java`, `service/InventoryRules.java`, `service/AlertService.java`, `service/DashboardService.java`.
- Inventory batch management, facility-scoped queries, medicine catalogue operations, expiry and low-stock rules.

**Phase 2 ownership**
- Surplus quantity and stock-eligibility rules.
- Source / receiving-facility inventory updates required by approved transfers.

## Member 3 - Console UI, Testing, Documentation and Redistribution

**Phase 1 / Beta v0.2 ownership**
- `ui/ConsoleMenu.java`, `ui/AdminMenu.java`, `ui/FacilityManagerMenu.java`, `ui/InputHelper.java`, `ui/TablePrinter.java`.
- `util/` and `exception/` shared utilities.
- `src/test/java/com/medishare/AppSelfTest.java`, additional test cases, setup guide, architecture documentation, demo script, GitHub contribution tracking.

**Phase 2 ownership**
- `TransferRequest` and `StockMovement` models / DAO / service modules.
- Transfer request, approval and fulfillment workflow.
- Reports / CSV export, concurrency tests and final integration evidence.

## Integration rules

1. Agree on database columns and public method signatures before parallel work.
2. Member 1 establishes the base project and commits the schema / connection foundation first.
3. Merge individual feature branches into `phase-1-dev` through pull requests.
4. Run compilation, self-tests and the MySQL demonstration after every integration.
5. Keep `main` stable; merge Phase 2 only after review.
6. Commits should describe one coherent change, e.g. `Add facility-scoped inventory search`.

The allocation identifies primary ownership, not exclusive knowledge. Each member should be able to explain the complete application during viva.
