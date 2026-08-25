# Implementation Plan - Delivery History Screen

Implement a dedicated Delivery History screen for customers with date-based filtering (Weekly, Monthly, Custom).

## User Review Required

> [!IMPORTANT]
> - The "Custom" date filter will use the Material 3 `DateRangePicker` dialog.
> - The "Weekly" filter will show the last 7 days from today.
> - The "Monthly" filter will show the current calendar month.

## Proposed Changes

### [Navigation]

#### [MODIFY] [Screens.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/navigation/Screens.kt)
Add `DeliveryHistory` route to the `Screen` sealed class.

#### [MODIFY] [AppNavHost.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/navigation/AppNavHost.kt)
Register the `DeliveryHistoryScreen` in the `NavHost`.

### [Domain Layer]

#### [NEW] [GetDeliveryHistoryUseCase.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/domain/usecase/GetDeliveryHistoryUseCase.kt)
Create a use case to fetch deliveries for a specific customer within a given date range.

### [Presentation Layer]

#### [NEW] [DeliveryHistoryUiState.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/delivery_history/DeliveryHistoryUiState.kt)
Define `DeliveryHistoryUiState` and `DateFilter` sealed class.

#### [NEW] [DeliveryHistoryViewModel.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/delivery_history/DeliveryHistoryViewModel.kt)
Implement logic to calculate date ranges for "Weekly" and "Monthly" and fetch data from the use case.

#### [NEW] [DeliveryHistoryScreen.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/delivery_history/DeliveryHistoryScreen.kt)
Create the UI with:
- `AppTopBar` with back button.
- Filter chips for Weekly, Monthly, and Custom.
- `LazyColumn` using the existing `DeliveryCard` component.
- `EmptyState` when no deliveries are found.

#### [MODIFY] [CustomerDetailScreen.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/presentation/customer_detail/CustomerDetailScreen.kt)
Update the "View All" button click to navigate to the new `DeliveryHistoryScreen`.

## Verification Plan

### Automated Tests
- No new automated tests requested, but will ensure the build is successful.

### Manual Verification
1. Navigate to Customer Details.
2. Click "View All" in the Delivery History section.
3. Verify the list shows the current month's deliveries by default.
4. Toggle to "Weekly" and verify the list updates.
5. Toggle to "Custom", pick a range, and verify the list updates.
