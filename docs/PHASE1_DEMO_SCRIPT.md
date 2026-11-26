# Phase 1 Console Demonstration Script (Suggested 5-7 minutes)

1. **Start-up:** Run `mvn clean compile exec:java`. Point out that the application connects to MySQL and inserts fictional demo records only if the demo records are not already present.
2. **Admin login:** Use `admin / admin123`. Show registered facilities and the medicine catalogue. Add one clearly fictional catalogue entry and show that the catalogue updates.
3. **Facility manager login:** Log out and sign in as `manager_citycare / demo123`. Point out the facility code in the manager menu.
4. **Dashboard:** Show batch count, usable units, expired batches, batches expiring within 60 days and low-stock medicine count.
5. **Inventory:** Show all batches, search for a medicine, and add a new batch by selecting a medicine ID from the catalogue.
6. **Quantity adjustment:** Adjust the quantity of a batch and show the updated row.
7. **Alerts:** Open expiry alerts and low-stock alerts. Explain that expired quantity is excluded from usable stock.
8. **Validation:** Attempt a duplicate batch number or a negative quantity and show the controlled validation message.
9. **Close:** Explain that Phase 2 extends this same codebase with surplus listing, availability search, transfer requests, approval, atomic stock transfer, transfer history, reports and CSV export.

## Demo accounts

- Admin: `admin` / `admin123`
- City Care Clinic manager: `manager_citycare` / `demo123`
- GreenLife Pharmacy manager: `manager_greenlife` / `demo123`
- Sunrise Community Hospital manager: `manager_sunrise` / `demo123`

All facilities, users, medicines and inventory data are fictional demonstration records.


## Additional Beta v0.2 demonstration points

1. As Admin, search facilities with a partial term such as `Ludh` and medicines with `para`.
2. Start adding a medicine, enter an already-used code, and show that the duplicate warning is displayed immediately.
3. Show the numbered dosage-form menu and demonstrate that a unitless strength such as `500` is rejected while `500 mg` is accepted.
4. Enter `:cancel` during a partially completed form and confirm that no record is saved.
5. Deactivate a medicine with existing batches, then sign in as its facility manager and verify that those batches and their alerts remain visible.
6. Delete a batch, choose `2. Cancel`, and verify that the row remains; repeat and choose `1. Confirm deletion` to demonstrate the facility-scoped delete.
