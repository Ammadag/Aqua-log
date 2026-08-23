# Update App Icon

The goal is to replace the current app logo with the new design provided in the `aqua_log_design` folder.

## User Review Required

> [!NOTE]
> The new logo will be applied to both the Android launcher icon and the in-app Splash Screen, as they both share the `app_logo` resource.

## Proposed Changes

### Assets

#### [MODIFY] [app_logo.png](file:///D:/AquaLog/androidApp/src/main/res/drawable/app_logo.png)
Replace with the new logo from [screen.png](file:///D:/AquaLog/aqua_log_design/stitch_aqualog_delivery_pro_logo/screen.png).

#### [MODIFY] [app_logo.png](file:///D:/AquaLog/shared/src/commonMain/composeResources/drawable/app_logo.png)
Replace with the new logo from [screen.png](file:///D:/AquaLog/aqua_log_design/stitch_aqualog_delivery_pro_logo/screen.png) to ensure the splash screen is updated in the KMP shared module.

## Verification Plan

### Manual Verification
- Deploy the app to a device/emulator and verify the launcher icon has changed.
- Open the app and verify the splash screen shows the new logo.
- I will perform a build to ensure the resources are correctly recognized.
