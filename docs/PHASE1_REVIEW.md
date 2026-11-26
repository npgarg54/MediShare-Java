# Phase 1 Build and Review Notes

## Source verification completed

- Main source compiled with `javac --release 17 -Xlint:all`.
- Test source compiled with `javac --release 17 -Xlint:all`.
- Dependency-free `AppSelfTest` completed successfully: **26/26 checks passed** in the current build environment.
- PDF blueprint rendered and visually reviewed at 150 DPI across all 10 pages; no visible clipping or overlap was found.

## What the self-test covers

- PBKDF2 password hash creation and verification.
- Incorrect-password rejection.
- Expiry-date boundary classification.
- Negative quantity validation.
- Near-expiry sorting by earliest date.
- Medicine code, name and batch-number normalization.
- Strength units, percentage/compound strengths and controlled `Not specified` value.
- Dosage-form normalization.
- `:cancel` and `:logout` input navigation for text, numeric and date prompts.

## Environment-dependent verification still required

A live MySQL server and Maven dependency download were not available in the build environment used to prepare this ZIP. Therefore, a live JDBC connection, SQL execution against MySQL, and the full menu flow have **not** been claimed as end-to-end tested here.

Before the Phase 1 evaluation, the team must:

1. Install/start MySQL Server 8.0+.
2. Execute `database/schema.sql`.
3. Configure `config/db.properties`.
4. Run `mvn clean compile exec:java` on the team's machine.
5. Run the manual cases in `TEST_CASES.md`, especially access control, CRUD, duplicate handling, dashboard calculations and persistence.

The source is intentionally compiled without a compile-time import of the MySQL driver; Maven supplies the official Connector/J driver at runtime.


## Beta v0.2 revision

The revision adds universal `:cancel` / `:back` and `:logout` navigation, partial case-insensitive search for medicines/facilities/inventory, immediate medicine-code uniqueness feedback, input normalization, strength-unit validation, numbered dosage-form selection, administrator medicine deactivation/restoration, and facility-scoped inventory-batch deletion with a 1. Confirm / 2. Cancel prompt. Existing batches remain visible and are included in expiry/low-stock monitoring after a medicine is deactivated.

Phase 2 transfer matching, requests, approvals, stock transfers, movement history, exports and concurrency allocation remain out of scope.
