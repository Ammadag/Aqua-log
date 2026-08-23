# Fix FATAL EXCEPTION: main (InvocationTargetException) during startup

The crash is likely caused by Koin failing to resolve the `AppDatabase` dependency, which is required by `DatabaseModule` but not provided anywhere in the Koin graph. Additionally, the Room database initialization is missing.

## Proposed Changes

### Shared Module - Data & DI

#### [MODIFY] [DatabaseModule.kt](file:///D:/AquaLog/shared/src/commonMain/kotlin/com/waterdelivery/app/di/DatabaseModule.kt)
- Provide the `AppDatabase` instance using the platform-specific `RoomDatabase.Builder`.
- Use `BundledSQLiteDriver` for consistent behavior across platforms.

#### [MODIFY] [PlatformModule.android.kt](file:///D:/AquaLog/shared/src/androidMain/kotlin/com/waterdelivery/app/di/PlatformModule.android.kt)
- Provide the Android-specific `RoomDatabase.Builder<AppDatabase>`.

#### [MODIFY] [PlatformModule.ios.kt](file:///D:/AquaLog/shared/src/iosMain/kotlin/com/waterdelivery/app/di/PlatformModule.ios.kt)
- Provide the iOS-specific `RoomDatabase.Builder<AppDatabase>`.

## Verification Plan

### Automated Tests
- Build the project to ensure all dependencies are correctly resolved.
- Run `gradlew :androidApp:assembleDebug` to verify compilation.

### Manual Verification
- Deploy the app to an Android emulator/device and verify that it no longer crashes at startup.
