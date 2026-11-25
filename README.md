# MediShare Beta v0.2

**Medicine Inventory & Redistribution Management System**  
Console application | Java 17+ | JDBC | MySQL

## Phase 1 scope

This is the Phase 1 prototype, branded as MediShare Beta v0.2. It includes:

1. Login and facility access: System Admin and Facility Manager roles with pre-seeded demo accounts; each manager is linked to one facility. Admin can view and search registered facilities.
2. Medicine catalogue: list, partial-text search, add, deactivate, and restore medicine master records (admin only for catalogue changes). Duplicate medicine codes are checked immediately after code entry.
3. Consistent medicine data: codes and batch numbers are normalized to uppercase; medicine names and categories use title case; dosage form uses numbered choices; numeric strengths require units.
4. Inventory batch management: view, partial-text search, add, adjust, and delete batches belonging to the logged-in facility. Deletion displays batch details and requires a 1. Confirm / 2. Cancel choice.
5. Navigation: enter `:cancel` (or `:back`) to abandon the current form and return to the role dashboard; enter `:logout` to return to the login screen.
6. Expiry monitoring: separate expired batches and batches expiring within the next 60 days.
7. Low-stock monitoring: compare a facility's usable stock (non-expired batches) with the medicine's reorder level, including existing batches for deactivated catalogue medicines.
8. Inventory dashboard with batch count, usable units, expired batches, near-expiry batches, and low-stock medicine count.

**Not included in Phase 1:** surplus listing, inter-facility availability matching, transfer requests, approvals, stock transfer, transfer history, and redistribution reports. Those are planned for Phase 2.

## Requirements

- JDK 17 or later
- MySQL Server 8.0 or later
- Maven 3.8+
- Internet access on the first Maven build to download MySQL Connector/J and Maven plugins

The project uses the official MySQL Connector/J 9.7.0 Maven dependency. This connector line supports MySQL Server 8.0 and later; Maven downloads the JDBC driver automatically, so it is not bundled in this ZIP.

## Setup (Windows / VS Code)

1. Install and start MySQL Server.
2. Open `database/schema.sql` in MySQL Workbench and execute the whole script. This creates the `medishare` database and the four Phase 1 tables.
3. Copy `config/db.properties.example` to `config/db.properties`.
4. Edit `config/db.properties` and enter your MySQL username and password.
5. Open the project folder in VS Code (or a terminal) and run:

   ```bash
   mvn clean compile exec:java
   ```

6. On first successful run, the application inserts demo facilities, demo users, medicines, and sample batches. The sample expiry dates are generated relative to the current date, so the expiry alerts remain useful when the demo is run later.

### macOS / Linux

The same commands work in a terminal. Ensure `mvn` is on your PATH.

## Demo login credentials

| Role | Username | Password | Facility |
|---|---|---|---|
| System Admin | `admin` | `admin123` | System-wide |
| Facility Manager | `manager_citycare` | `demo123` | City Care Clinic |
| Facility Manager | `manager_greenlife` | `demo123` | GreenLife Pharmacy |
| Facility Manager | `manager_sunrise` | `demo123` | Sunrise Community Hospital |

Passwords are stored as salted PBKDF2 hashes, not plain text. Change demo credentials before any real deployment. This academic prototype is not configured for production deployment.

## Main console workflow

### System Admin
- View and search registered facilities (partial text matches are supported).
- View and search the active medicine catalogue.
- Add medicines with early duplicate-code checking, text normalization, unit validation and numbered dosage-form selection.
- Deactivate medicines while preserving existing inventory records.
- View and restore inactive medicines.

### Facility Manager
- View dashboard.
- View and partially search their facility's inventory only.
- Add an inventory batch for an active medicine in the master catalogue.
- Adjust the quantity of one of their own batches.
- Delete one of their own batches after reviewing its details and choosing `1. Confirm deletion` or `2. Cancel`.
- Search the medicine catalogue using partial text (for example, `para` finds `Paracetamol`).
- View expired / near-expiry alerts.
- View low-stock alerts, including existing batches of deactivated medicines.
- Log out.

### Navigation commands
- `:cancel` or `:back`: cancel the current form/prompt and return to the role dashboard.
- `:logout`: leave the current role menu and return to the login screen.
- Commands work during text, number, date, confirmation and pause prompts.

## Business rules in Phase 1

- A facility manager can access only the inventory associated with their own facility ID.
- Batch quantity cannot be negative.
- New batches cannot have an expiry date earlier than or equal to the received date.
- A duplicate batch number for the same medicine at the same facility is rejected.
- Deactivating a medicine prevents new inventory batches but preserves existing batches for expiry and low-stock monitoring.
- Inventory batch deletion is restricted to the logged-in facility and requires explicit confirmation.
- Medicine search is partial-text and case-insensitive across code, name, strength, dosage form and category.
- Numeric medicine strengths must include a recognized unit; the controlled value `Not specified` is available for non-numeric specifications.
- Medicine codes are unique across both active and inactive catalogue records.
- A batch is considered expired when its expiry date is before today's date.
- A batch is considered near expiry when its expiry date is from today through the next 60 days (inclusive).
- Low-stock calculations exclude expired batches and compare total usable quantity for a medicine with the reorder level in the medicine catalogue.
- Phase 1 quantity adjustment sets the batch's current quantity. A complete stock movement audit trail is planned for Phase 2.

## Database tables in Phase 1

- `facilities`
- `users`
- `medicines`
- `inventory_batches`

Phase 2 adds `transfer_requests` and `stock_movements`, plus the required transfer-related columns / constraints.

## Self-test (no MySQL required)

After compiling, run the pure Java checks:

```bash
mvn test-compile
java -cp target/classes:target/test-classes com.medishare.AppSelfTest
```

On Windows Command Prompt, use `;` instead of `:` in the classpath:

```bat
java -cp target\classes;target\test-classes com.medishare.AppSelfTest
```

The self-test checks password hashing/verification, inventory quantity validation, expiry-window boundaries, sorting, medicine text normalization, strength-unit validation, dosage forms, and navigation commands. It does not replace an end-to-end JDBC test against your MySQL installation.

## Notes and limitations

- The app is an academic coordination prototype, not a clinical or pharmacy dispensing system.
- No patient, prescription, medicine-selection, or dosage advice is provided.
- Real medicine redistribution requires verification of authorization, product integrity, storage conditions, batch/expiry compliance, and all applicable laws and facility policies. These checks are not fully automated in this Phase 1 prototype.
- MySQL credentials are stored locally in the ignored `config/db.properties`; do not commit that file.
- Phase 2 transfer approval and inventory updates must be implemented as a single database transaction with appropriate row locking to prevent over-allocation.

## Suggested Git workflow

- `main`: stable reviewed versions only.
- `phase-1-dev`: integration branch for Phase 1.
- `member-1-foundation`, `member-2-inventory`, `member-3-testing`: individual contribution branches.
- `phase-2-dev`: integration branch for Phase 2.

Use pull requests to merge individual branches. Do not commit `config/db.properties`, `target/`, compiled `.class` files, or local database dumps containing real data.

## Folder structure

See `docs/PROJECT_STRUCTURE.md` and `docs/TEAM_ALLOCATION.md`.
