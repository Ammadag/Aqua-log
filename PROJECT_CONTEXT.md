# Water Delivery Management App — Agent Instructions & Context

## 1. Executive Summary & Target User
This is a local-first, offline-first cross-platform mobile application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. 

- **Primary Persona:** Delivery worker operating a water filtration and 19L bottle delivery business.
- **Core Product Goal:** High-speed data entry, clear transaction history, and rapid PDF invoice generation/sharing via WhatsApp share sheets.
- **UX Priority:** Optimized for fast, one-handed operation on the road with large touch targets and minimal navigation depth.

---

## 2. Core Business Rules (Crucial)
1. **Transaction-Based Ledger:** Every delivery/return event MUST be saved as an independent `Delivery` transaction (`deliveredQuantity`, `returnedQuantity`, `pricePerBottle`, `date`). NEVER mutate history or store only current total bottle balances.
2. **Immutable Price Snapshots:** Always save `pricePerBottle` on each delivery record. Customer bottle prices change over time; historical transactions and past invoices must reflect the actual price applied on that date.
3. **Immutable Invoices:** Invoices are stored as immutable `InvoiceEntity` snapshots (`invoiceNumber`, date range, item totals). Edits to future deliveries or default prices must NEVER alter already generated invoices.
4. **Offline First:** Local Room Database is the sole source of truth. No active backend, remote auth, or network sync required in V1.

---

## 3. Design System & Visual Source of Truth (`aqua_log_design/`)
The visual design, color tokens, typography scale, spacing rules, and layout structures are pre-built and placed inside the **`aqua_log_design/`** directory in the project root.

### Folder Mapping by Screen
When implementing or editing any UI screen, you MUST inspect and reference the corresponding subfolder:

- **Screen 1 — Dashboard:** `aqua_log_design/stitch_aqualog_delivery_pro_dashboard/`
- **Screen 2 — Customers List:** `aqua_log_design/stitch_aqualog_delivery_pro_customers/`
- **Screen 3 — Customer Details:** `aqua_log_design/stitch_aqualog_delivery_pro_customer_detail/`
- **Screen 4 — Add Customer:** `aqua_log_design/stitch_aqualog_delivery_pro_add_contact/`
- **Screen 5 — Select Contact:** `aqua_log_design/stitch_aqualog_delivery_pro_select_contact/`
- **Screen 6 — Add Delivery:** `aqua_log_design/stitch_aqualog_delivery_pro_add_delivery/`
- **Screen 7 — Invoices List:** `aqua_log_design/stitch_aqualog_delivery_pro_invoice/`
- **Screen 8 — Invoice Preview:** `aqua_log_design/stitch_aqualog_delivery_pro_invoice_preview/`
- **Screen 9 — Settings:** `aqua_log_design/stitch_aqualog_delivery_pro_settings/`

### Asset Breakdown per Folder
Inside each screen directory, use the following files to guide implementation:
- **`code.html`:** Layout hierarchy reference. Analyze flex/grid containers to build corresponding Compose `Column`, `Row`, `Box`, `LazyColumn`, and `Surface` structures.
- **`DESIGN.md`:** Specific screen guidelines, font weights, colors, padding rules, and element constraints.
- **`screen.png`:** Visual design mock reference for visual verification and layout proportions.

Do NOT redesign screens or introduce custom visual themes. Replicate the Stitch design assets faithfully using standard Material 3 Compose Multiplatform components.

---

## 4. Technology Stack & Architecture Guidelines
- **Language & Framework:** Kotlin Multiplatform (KMP), Compose Multiplatform
- **Database:** Room KMP with SQLite
- **Dependency Injection:** Koin
- **Architecture Pattern:** Clean Architecture + Repository Pattern + MVVM/MVI
  - `UI (Composables)` ➔ `ViewModel` ➔ `Use Cases` ➔ `Repository` ➔ `Room Database`

---

## 5. Development Roadmap & Incremental Strategy
We follow an incremental build phase. Do not jump ahead or generate unrequested modules.

- **Phase 1: Domain Layer** — Pure Kotlin domain models (`Customer`, `Delivery`, `Invoice`, `BusinessProfile`).
- **Phase 2: Data Layer** — Room entities, DAOs, Database, and Repository implementations.
- **Phase 3: Domain Use Cases** — Core calculation & transactional business logic.
- **Phase 4: Design System & Theme** — Color palette, typography, spacing, and reusable Composables based on Stitch design tokens.
- **Phase 5: Screen Implementations** — Incremental UI screen builds utilizing the `aqua_log_design/` assets.
- **Phase 6: Native Platform Capabilities** — Contacts import picker, PDF document generation, and platform share sheets.

---

## 6. Coding Principles for AI Agent
- **No Over-Engineering:** Follow SOLID principles without introducing unnecessary interface abstractions for single implementations.
- **Clean Composables:** Keep business logic, mathematical computations, and database queries strictly OUT of `@Composable` UI code.
- **State Management:** Use Kotlin `StateFlow` and immutable UI state representations in ViewModels.
- **Standard Styling:** Reuse tokens from `WaterDeliveryTheme` instead of hardcoding raw color hexes or random DP paddings.