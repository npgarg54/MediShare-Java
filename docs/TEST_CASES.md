# Phase 1 Test Cases

Use fictional demo data only. Run the dependency-free checks with `mvn test-compile` followed by `java -cp target/classes:target/test-classes com.medishare.AppSelfTest` (use `;` as the classpath separator on Windows Command Prompt).

| ID | Area | Test | Expected result |
|---|---|---|---|
| T01 | Password | Correct demo password | Login accepted for active demo user |
| T02 | Password | Incorrect password | Login rejected; no menu opened |
| T03 | Role | Admin login | Admin menu is shown |
| T04 | Role | Facility manager login | Manager menu shows the linked facility |
| T05 | Access | Manager attempts to adjust a batch belonging to another facility | No row updated; validation message shown |
| T06 | Catalogue | Search by code / medicine name | Matching active medicines returned |
| T07 | Catalogue | Enter duplicate medicine code in add form | Duplicate warning appears immediately at code prompt; remaining fields are not requested |
| T08 | Inventory | Add valid batch | Batch persists and appears in facility inventory |
| T09 | Inventory | Add duplicate facility + medicine + batch number | Duplicate rejected by unique key |
| T10 | Inventory | Add negative quantity | Input validation rejects it |
| T11 | Inventory | Add batch with expiry on / before received date | Input validation rejects it |
| T12 | Inventory | Adjust quantity to zero | Update succeeds; quantity remains non-negative |
| T13 | Inventory | Adjust with an unknown / foreign batch ID | No row updated; facility-scope message shown |
| T14 | Expiry | Expiry date is yesterday | Batch appears in expired list |
| T15 | Expiry | Expiry date is today | Batch appears in near-expiry list |
| T16 | Expiry | Expiry date is today + 60 days | Batch appears in near-expiry list |
| T17 | Expiry | Expiry date is today + 61 days | Batch does not appear in near-expiry list |
| T18 | Low stock | Usable quantity is below reorder level | Medicine appears in low-stock list |
| T19 | Low stock | Some quantity exists only in expired batches | Expired quantity is not counted as usable |
| T20 | Persistence | Restart application after an update | Updated quantity remains in MySQL |

## Current automated self-test coverage

`AppSelfTest` checks password hashing / verification, expiry boundary logic, negative quantity rejection, expiry-alert sorting, input normalization, strength validation, dosage forms and navigation commands. It does not connect to MySQL. Database and full menu test cases should be run manually against the configured MySQL database before evaluation.


## Beta v0.2 improvements

| ID | Test | Expected result |
|---|---|---|
| UX-01 | Enter `:cancel` during add medicine or add batch | Form stops; no partial record is saved; role dashboard returns |
| UX-02 | Enter `:logout` during any form | Current role menu ends and login screen returns |
| CAT-01 | Enter an existing medicine code | Duplicate warning appears immediately before remaining fields are requested |
| CAT-02 | Search `para` in medicine catalogue | Paracetamol appears in results |
| CAT-03 | Add medicine using mixed-case name/category | Stored display values are normalized to title case |
| CAT-04 | Enter strength `500` | Validation rejects the value because it has no unit |
| CAT-05 | Enter strength `500 MG` | Value is accepted and stored as `500 mg` |
| CAT-06 | Choose dosage form from numbered list | Selected canonical dosage form is stored |
| CAT-07 | Deactivate a medicine | Medicine is hidden from active catalogue but its existing batches remain visible and monitored |
| CAT-08 | Restore an inactive medicine | Medicine reappears in active catalogue |
| INV-01 | Search inventory using partial text | Matching medicine names/codes/batch numbers appear, case-insensitively |
| INV-02 | Delete a batch and choose option 2 | Deletion is cancelled; database row remains |
| INV-03 | Delete a batch and choose option 1 | Only the selected batch belonging to the logged-in facility is deleted |
| ADM-01 | Search facilities by partial city/name/code | All matching active facilities appear |
| LOW-01 | Deactivate a medicine that has existing batches | Existing stock remains included in low-stock monitoring |
