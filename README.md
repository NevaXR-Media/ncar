# NCar

## Purpose

`NCar` is an Android Automotive library and demo application for reading and, where supported, writing vehicle data through the Android Car API and Vehicle HAL (VHAL).

This repository currently focuses on a TOGG implementation. The codebase defines a normalized application-facing data layer on top of raw vehicle properties so that downstream features can consume consistent state objects instead of vendor-specific payloads.

## Scope

The current implementation covers four responsibilities:

1. Identify the connected vehicle profile.
2. Read raw values from Android Automotive car properties.
3. Transform raw values into normalized domain models.
4. Expose those models as observable state and limited writable controls.

## Data Acquisition Flow

Vehicle data is collected through the following flow:

1. `NCarService` initializes the configured property providers and attempts to identify the active vehicle spec.
2. `NCarSpecTogg` identifies TOGG by reading standard car info fields such as make and model.
3. Once the TOGG spec is selected, the service creates an `NCar` instance that exposes the supported properties.
4. Each property is backed by `NVhalProvider`, which uses Android's `CarPropertyManager` to read values and subscribe to updates.
5. Raw VHAL values are mapped into typed application models such as `Measurement`, `MeasurementRanged`, `NCarDoorState`, `NCarWindowState`, and `NCarHvacTemperatureState`.

For TOGG, `NVhalProvider` is built with `forceInitialRead = true`. This is an implementation detail added because some vendors do not immediately emit the current value for `ON_CHANGE` subscriptions. In this repository, TOGG is explicitly treated as one of those cases.

## How Data Is Formatted

The library does not expose raw `CarPropertyValue` objects to the rest of the app. Instead, it formats data into a small set of stable domain types.

### Primitive and Enumerated Values

- Boolean flags are exposed as `Boolean`.
- Numeric scalar values are exposed as `Float` or `Int` only when no richer model is required.
- Discrete vehicle states are mapped into enums such as `NCarGear` and `NCarAmbientColor`.

### Measured Values

Measured values are wrapped in typed units:

- `Measurement<U>`: value plus unit.
- `MeasurementRanged<U>`: value plus unit and expected operating range.
- `MeasurementUnitRange<U>`: declared min/max interval for normalization and UI scaling.

This allows the application to:

- keep source units explicit,
- convert units safely when required,
- format values for display with unit symbols,
- normalize ranged values for charts, sliders, and dashboards.

### Composite Vehicle State

Some VHAL properties arrive as repeated events scoped by area ID. Those are reduced into application-level objects:

- HVAC temperatures become `NCarHvacTemperatureState`.
- Seat occupancy becomes `NCarSeatOccupancyState`.
- Door positions become `NCarDoorState`.
- Window positions become `NCarWindowState`.

This means the application consumes one coherent state object per feature area instead of manually combining multiple area-specific events.

## Current TOGG Data Model

The TOGG spec currently implements the following data categories.

| Category | Application Field | Source Type | Notes |
| --- | --- | --- | --- |
| Vehicle identity | `deviceId`, `brand`, `model` | Standard Android car properties | Used for identification and diagnostics |
| Motion | `speed`, `gear` | Standard Android car properties | Speed is wrapped as `MeasurementRanged<UnitSpeed>` |
| Energy | `battery`, `batteryCapacity`, `evChargingRate` | Standard Android car properties | EV-oriented read access |
| Climate | `hvacStatus`, `hvacDualStatus`, `hvacMaxStatus`, `hvacFanSpeed`, `hvacPassengerSpeed`, `hvacTemperature`, `hvacExteriorTemperature` | Standard Android car properties | Mixed boolean, ranged, and composite state |
| Climate | `hvacInteriorTemperature` | TOGG vendor property | Vendor-specific cabin temperature source |
| Seating | `seatOccupancy` | Standard Android car properties | Reduced by seat area IDs |
| Closures | `doorState`, `trunkState`, `trunkAngle`, `frunkState`, `frunkAngle`, `windowState` | Standard Android car properties | Reduced from door/window area-based updates |
| Powertrain proxy | `engine` | Standard Android car property | Present in API surface, even if not meaningful for all EV contexts |
| Steering | `steeringWheelAngle` | Constant placeholder | Not yet backed by an actual TOGG property in current code |
| Acceleration | `acceleration` | Constant placeholder | Not yet backed by an actual TOGG property in current code |
| Driving mode | `drivingMode` | TOGG vendor property | Mapped to `ECO`, `COMFORT`, `SPORT`, `UNKNOWN` |
| Ambient light read | `ambientLight` | TOGG vendor property | Mapped to `NCarAmbientColor` |
| Ambient light write | `ambientLightControl` | TOGG vendor property | Writable control path for changing cabin ambient color |

## Source and Transformation Rules

The current transformation strategy in the repository is:

- Standard Android Automotive properties are referenced through `NVhalKey`.
- TOGG vendor-only properties are declared in `NCarSpecTogg` with explicit property IDs and `android.car.permission.CAR_VENDOR_EXTENSION`.
- Raw values are transformed close to the source definition so the mapping remains local to the property declaration.
- Area-based values are reduced into multi-field state objects.
- UI-facing formatting is handled by measurement format helpers instead of duplicating string formatting logic in screens.

This keeps the source-of-truth for each field close to its property declaration and reduces ambiguity about where a displayed value came from.

## How To Set Ambient Lights

Ambient light control is currently implemented only through the TOGG vendor property write path.

The writable property is exposed as `ambientLightControl`, and the accepted values are the `NCarAmbientColor` enum:

- `NONE`
- `TURQUOISE`
- `ORANGE`
- `YELLOW`
- `PURPLE`
- `RED`
- `BLUE`
- `GREEN`
- `WHITE`

To change the ambient light, call `setProperty` on the active `NCar` instance or use the helper already defined in the demo `CarState`.

Example through the demo state object:

```kotlin
suspend fun setAmbientLight(color: NCarAmbientColor) {
    car.setProperty(car.spec.ambientLightControl, color)
}
```

Example usage:

```kotlin
carState.setAmbientLight(NCarAmbientColor.BLUE)
```

Technical mapping details:

- Read source: `VendorKeys.AMBIENT_LIGHT_READ`
- Write source: `VendorKeys.AMBIENT_LIGHT_WRITE`
- Permission: `android.car.permission.CAR_VENDOR_EXTENSION`
- Output type: `NCarAmbientColor`

Operational note:

- Ambient light control should be treated as vendor-restricted functionality.
- Even if the permission is declared in the manifest, successful write access may still require a TOGG-approved signed APK or platform-level privilege grant.

## Permissions Model

Access is permission-driven at two levels:

1. The application manifest declares the Android Automotive permissions that may be needed.
2. Each subscribed property contributes its own required permission set through `requiredPermissions`.

At runtime, the demo app gathers the permissions required by the actively used properties and requests any missing permissions before starting subscriptions.

This is an important distinction:

- Declaring a permission in the manifest does not guarantee that the vehicle platform will grant access.
- Access to some properties, especially vendor extensions, may depend on platform signature, OEM privilege configuration, or both.

## Signed APK and TOGG Access Boundary

The repository already separates unsigned and signed application variants:

- Debug build: unsigned test variant with application ID suffix `.unsigned`
- Release build: signed variant with application ID suffix `.signed`

The signed release variant is the correct place to validate privileged or vendor-restricted TOGG properties. However, this repository does not yet contain the final verified matrix of what is readable or writable on TOGG when the APK is signed with the required key.

Use the following section as the project record for that validation.

### TOGG Signed APK Validation Matrix

This section is intentionally prepared for later completion.

| Data / Capability | Source Class | Access with Unsigned APK | Access with Signed APK | Notes / Evidence |
| --- | --- | --- | --- | --- |
| Standard Android Automotive properties | `NVhalKey` based | Pending validation | Pending validation | Fill after device test |
| TOGG vendor drive mode | `VendorKeys.DRIVE_MODE_PROPERTY` | Pending validation | Pending validation | Requires vendor extension permission |
| TOGG cabin current temperature | `VendorKeys.CABIN_CURRENT_TEMP_DEG_PROPERTY` | Pending validation | Pending validation | Requires vendor extension permission |
| TOGG ambient light read | `VendorKeys.AMBIENT_LIGHT_READ` | Pending validation | Pending validation | Requires vendor extension permission |
| TOGG ambient light write | `VendorKeys.AMBIENT_LIGHT_WRITE` | Pending validation | Pending validation | Requires vendor extension permission and write-path validation |

Recommended completion rule for this table:

- mark each row as `Available`, `Unavailable`, or `Not yet tested`,
- state the exact build used,
- record whether the result was observed on emulator, bench device, or production head unit,
- include log evidence or a reproducible test note.

## What Each Team Member Needs

The documentation should be used differently depending on the reader.

### Android Developers

Need to know:

- which spec defines each field,
- which Android or vendor permission protects the field,
- the exact normalized output type,
- whether the field is read-only or writable,
- whether the field is confirmed on unsigned builds, signed builds, or both.

### QA and Validation

Need to know:

- which build variant was tested,
- on which vehicle or hardware image the test was executed,
- expected value ranges and enum outputs,
- what failure looks like when permission or property access is missing.

### Product and Integration Stakeholders

Need to know:

- which business-visible signals are already available,
- which signals are vendor-specific and therefore higher risk,
- which controls are writable,
- which items are still blocked pending signature or OEM enablement.

## Known Gaps in the Current Codebase

The current implementation still contains a few areas that should be documented as provisional:

- `acceleration` is currently a constant placeholder.
- `steeringWheelAngle` is currently a constant placeholder.
- A vendor battery property is declared in code but not yet surfaced as a public field in the TOGG spec output model.
- The signed APK access matrix is not yet filled with validated TOGG hardware results.

## Recommended Documentation Update Process

When new TOGG findings are available, update this README using the following order:

1. Record the tested build variant and signing status.
2. Record the tested vehicle, software version, or head unit environment.
3. Update the signed APK validation matrix.
4. Update the current TOGG data model if a new property becomes supported.
5. Mark provisional fields as confirmed, blocked, or removed.

## Reference Implementation Files

The main implementation points for this document are:

- [`NCarService.kt`](/Users/utkuyildiz/Library/Mobile%20Documents/com~apple~CloudDocs/Projects/Android/nfoundation-car/foundation-car/src/main/java/com/nevaxr/foundation/car/NCarService.kt)
- [`NVhalProvider.kt`](/Users/utkuyildiz/Library/Mobile%20Documents/com~apple~CloudDocs/Projects/Android/nfoundation-car/foundation-car/src/main/java/com/nevaxr/foundation/car/NVhalProvider.kt)
- [`NVhalProperty.kt`](/Users/utkuyildiz/Library/Mobile%20Documents/com~apple~CloudDocs/Projects/Android/nfoundation-car/foundation-car/src/main/java/com/nevaxr/foundation/car/NVhalProperty.kt)
- [`NCarSpecTogg.kt`](/Users/utkuyildiz/Library/Mobile%20Documents/com~apple~CloudDocs/Projects/Android/nfoundation-car/foundation-car/src/main/java/com/nevaxr/foundation/car/NCarSpecTogg.kt)
- [`MainActivity.kt`](/Users/utkuyildiz/Library/Mobile%20Documents/com~apple~CloudDocs/Projects/Android/nfoundation-car/app/src/main/java/com/nevaxr/foundation/car/demo/MainActivity.kt)
- [`app/src/main/AndroidManifest.xml`](/Users/utkuyildiz/Library/Mobile%20Documents/com~apple~CloudDocs/Projects/Android/nfoundation-car/app/src/main/AndroidManifest.xml)
