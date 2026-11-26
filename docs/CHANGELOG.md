# MediShare changelog

## Beta v0.2

- Added `:cancel` / `:back` to cancel the active form and return to the role dashboard.
- Added `:logout` from text, numeric, date, confirmation and pause prompts.
- Added partial, case-insensitive search across medicine code/name/strength/form/category, facility code/name/type/address/city, and facility inventory medicine code/name/batch number.
- Added immediate duplicate medicine-code checking before collecting the remaining medicine details.
- Normalized medicine codes and batch numbers to uppercase; normalized names, categories and custom dosage forms to title case.
- Added strength-unit validation and canonical formatting; `Not specified` is the controlled exception for non-numeric specifications.
- Replaced free-text dosage-form selection with a numbered list and a custom `Other` option.
- Added facility-scoped inventory batch deletion with a 1. Confirm / 2. Cancel prompt.
- Added medicine deactivation/restoration instead of physical medicine deletion. Existing inventory batches remain visible and included in expiry and low-stock monitoring.
- Changed console branding to `MediShare Beta v0.2`.
- Kept transfer matching, requests, approvals, inter-facility stock movement, movement history, redistribution reports and concurrency allocation in Phase 2.
