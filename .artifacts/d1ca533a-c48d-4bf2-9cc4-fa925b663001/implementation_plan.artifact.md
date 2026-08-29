# Add Brand Logo Support

This plan outlines the steps to add Brand Logo support to the AquaLog app, including storage, UI for selection/preview, and integration into the PDF invoice generator.

## User Review Required

> [!IMPORTANT]
> The image selection and storage mechanism will differ between Android and iOS. For Android, we will use `ActivityResultLauncher` in the Compose layer and a platform helper to persist the image to internal storage. For iOS, a similar platform-specific approach will be used.

## Proposed Changes

### Domain & Data Layer

#### [MODIFY] [BusinessProfile.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/domain/model/BusinessProfile.kt)
- Add `val logoPath: String? = null` to `BusinessProfile`.

#### [MODIFY] [BusinessProfileEntity.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/data/local/entity/BusinessProfileEntity.kt)
- Add `val logoPath: String? = null` to `BusinessProfileEntity`.

#### [MODIFY] [BusinessProfileMapper.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/data/local/mapper/BusinessProfileMapper.kt)
- Map `logoPath` in both `toDomain` and `toEntity` functions.

---

### Platform Helpers (Image Storage)

#### [NEW] [ImageStorage.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/core/platform/ImageStorage.kt)
- Define `expect` class or functions for saving images to internal storage and retrieving them.

#### [NEW] [ImageStorage.android.kt](file:///D:/AquaLog/shared/src/androidMain/kotlin/com/waterdelivery/app/core/platform/ImageStorage.android.kt)
- Implement `ImageStorage` to save images from a `Uri` to the app's `filesDir`.

#### [NEW] [ImageStorage.ios.kt](file:///D:/AquaLog/shared/src/iosMain/kotlin/com/waterdelivery/app/core/platform/ImageStorage.ios.kt)
- Implement `ImageStorage` for iOS.

---

### Presentation Layer

#### [MODIFY] [SettingsUiState.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/settings/SettingsUiState.kt)
- Add `logoPath: String? = null`.

#### [MODIFY] [SettingsViewModel.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/settings/SettingsViewModel.kt)
- Add `onLogoSelected(path: String)` and ensure it's saved in `saveSettings`.

#### [MODIFY] [SettingsScreen.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/settings/SettingsScreen.kt)
- Add a "Brand Logo" card with a preview and "Upload / Change Logo" button.
- Integrate Android's `rememberLauncherForActivityResult` for image picking.

#### [MODIFY] [InvoicePreviewScreen.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/invoice_preview/InvoicePreviewScreen.kt)
- Render the logo image in the `InvoiceDocument` header if available.

---

### PDF Generation

#### [MODIFY] [PdfInvoiceGenerator.android.kt](file:///D:/AquaLog/shared/src/androidMain/kotlin/com/waterdelivery/app/core/platform/PdfInvoiceGenerator.android.kt)
- Load the bitmap from `logoPath` and draw it in `drawTitleAndLogo`.

#### [MODIFY] [PdfInvoiceGenerator.ios.kt](file:///D:/AquaLog/shared/src/iosMain/kotlin/com/waterdelivery/app/core/platform/PdfInvoiceGenerator.ios.kt)
- (Optional/Stub) Implement logo rendering for iOS.

## Verification Plan

### Automated Tests
- N/A (UI and PDF generation are primarily manual verification)

### Manual Verification
1.  Open Settings and upload a logo.
2.  Verify the logo preview shows correctly in Settings.
3.  Save settings and reopen to ensure the path persisted.
4.  Generate an invoice and verify the logo appears in the Invoice Preview.
5.  Generate a PDF and share it; verify the logo appears in the PDF header.
