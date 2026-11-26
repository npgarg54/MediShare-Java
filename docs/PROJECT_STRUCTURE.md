# Project Structure and Architecture

```text
MediShare-Phase1/
├── src/main/java/com/medishare/
│   ├── Main.java
│   ├── dao/          # JDBC queries, repository contract and persistence
│   ├── enums/        # UserRole, FacilityType
│   ├── exception/    # Application and validation exceptions
│   ├── model/        # User, Facility, Medicine, InventoryBatch, summaries
│   ├── service/      # Business rules and use-case orchestration
│   ├── ui/           # Console menus and safe input handling
│   └── util/         # Password hashing and date helpers
├── src/test/java/com/medishare/AppSelfTest.java
├── database/schema.sql
├── config/db.properties.example
├── docs/
├── pom.xml
└── README.md
```

## Request flow

`Main -> ConsoleMenu -> Service -> DAO -> JDBC -> MySQL`

- UI classes collect and display information only.
- Service classes validate input and apply business rules.
- DAO classes contain SQL and map `ResultSet` rows into model objects.
- Model classes represent application data.
- Utility classes provide shared functions such as date formatting and password hashing.

## Main class responsibilities

| Class | Responsibility |
|---|---|
| `Main` | Startup, database connection and demo-data seeding |
| `User` | Abstract common user model |
| `Admin`, `FacilityManager` | Role-specific user types |
| `AuthService` / `UserDAO` | Login and role resolution |
| `FacilityDAO` | Facility listing |
| `MedicineDAO` / `MedicineService` | Catalogue search and admin additions |
| `InventoryRepository` / `InventoryDAO` | Persistence contract and facility-scoped JDBC batch queries / updates |
| `InventoryService` | Batch validation and inventory operations |
| `AlertService` | Expiry-window filtering and date sorting; low-stock is queried through InventoryDAO |
| `DashboardService` | Dashboard metrics |
| `ConsoleMenu` | Welcome / login flow and role-based menu routing |
| `AdminMenu`, `FacilityManagerMenu` | Role-specific menus |
| `InputHelper` | Safe console input parsing |

## Phase 2 extension points

- Add `TransferRequest` model and `TransferStatus` enum.
- Add `TransferDAO` and `TransferService`.
- Add `StockMovement` model and DAO support.
- Add surplus quantity fields to inventory batches.
- Complete transfer approval and fulfillment inside a JDBC transaction.
- Add report generation and CSV export.


## Beta v0.2 additions

- `exception/OperationCancelledException.java` and `exception/LogoutRequestedException.java`: global navigation signals used by console input.
- `util/MedicineInputRules.java`: normalization and validation for medicine codes, names, strength units, dosage forms and batch numbers.
- `FacilityDAO.search(...)` and `MedicineDAO.search(...)`: partial, case-insensitive search implementation.
- `InventoryRepository.deleteBatch(...)` and `InventoryDAO.deleteBatch(...)`: facility-scoped batch removal.
- `MedicineDAO.setActive(...)`: catalogue deactivation/restoration while preserving referenced inventory rows.
