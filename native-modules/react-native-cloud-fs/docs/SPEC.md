# Cloud filesystem authorization contract

Status: Implemented for the Android permission changes in OK-64212. Device
verification is still required.

## Purpose and scope

This TurboModule provides Google Drive file access on Android and iCloud file
access on iOS. The Android consumer uses hidden application data for backups.
OneKey ID and Keyless identity authentication belong to the consuming app.

## Definitions and ownership

- `hidden` selects Google Drive's `appDataFolder`; `visible` selects ordinary
  Drive storage. The app's backup callers use `hidden`.
- Each `RNCloudFsModule` instance owns its Drive helper and pending activity
  result promises. Google Sign-In owns the app's persisted Google account state.
- Independent React Native runtimes have separate JavaScript objects and can
  have separate native module instances. Consumers must not assume that another
  runtime has initialized this instance's Drive helper.

## Public API and defaults

The bridge API is declared in `src/NativeCloudFs.ts`. This change does not alter
method signatures or return values.

List/upload methods default to `visible` when no scope is supplied. Backup
callers must explicitly pass `hidden`; the existing appdata-only credentials do
not authorize ordinary Drive storage. This existing limitation is unchanged.

- `loginIfNeeded()` reuses an initialized helper or initializes one from the
  last signed-in Google account. If no account exists, it opens Google Sign-In.
- The backup sign-in fallback must request `DriveScopes.DRIVE_APPDATA`, without
  `DriveScopes.DRIVE_FILE`.
- `logout()` clears this instance's Drive helper and signs out of Google. Its
  sign-in client configuration must not include a Drive scope.
- Account information reads do not request Drive access.

## Lifecycle and concurrency

Drive credentials request only `DriveScopes.DRIVE_APPDATA`. Creating credentials
for an existing account does not display consent; actual Drive access can raise
`UserRecoverableAuthIOException` and open the existing authorization activity.
Pending list/upload operations use the existing activity-result retry flow.
Cancellation rejects a pending promise. This change preserves that flow and its
existing single-pending-operation model; it introduces no parallel operations.

## Data, cache, and identity

The selected account comes from Google Sign-In when the Drive helper is created.
Consumers must sign out before replacing an initialized backup account. The
helper is held in memory until logout or module disposal. The OAuth application
identity, backup filenames, file contents, and `appDataFolder` storage location
remain compatible with existing backups.

## Platform contract

| Platform | Storage and authorization |
| --- | --- |
| Android | Google Drive app data authorization at backup access; backup fallback requests appdata only. |
| iOS | Existing iCloud implementation; Google Sign-In and Drive permissions do not apply. |
| Web | No native implementation in this package. |

## Failure, fallback, and safety

Missing Google accounts use the existing sign-in fallback. Sign-in cancellation
or failure rejects the sign-in promise. Existing recoverable Drive authorization
errors keep their retry/cancellation handling. Consumers must not request Drive
permissions during identity-only sign-in, or add a duplicate consent flow.
No encryption, validation, authentication checks, file limits, or error codes
are changed.

## Performance and resource budget

Only sign-in client options change. Drive I/O continues to run on the existing
helper executor. No additional bridge calls, retained data, or startup I/O are
introduced; no new performance claim is made.

## Conformance and acceptance

- `android/src/main/java/com/rncloudfs/RNCloudFsModule.kt` owns sign-in options,
  appdata credentials, authorization activity results, and logout.
- `android/src/main/java/com/rncloudfs/DriveServiceHelper.kt` owns hidden-folder
  reads and writes.
- Verify that no `DRIVE_FILE` request remains, both credential paths still use
  `DRIVE_APPDATA`, and native compilation succeeds.
- On Android with a Google account without historical grants: identity login
  requests no Drive scope; first backup/list/restore requests appdata; cancelling
  consent ends the operation and leaves the app's identity session usable.
- Verify subsequent authorized access, existing backup/restore, account switch,
  sign-out/re-login, and agreement between displayed and actual storage account.

Source inspection and compilation do not prove Google consent behavior. The
device cases above remain required before release acceptance.

The changed native source compiled successfully with
`:react-native-cloud-fs:compileDebugKotlin` in the consuming OneKey app using its
locked `@onekeyfe/react-native-cloud-fs@3.0.156` dependencies. This is build
evidence, not device verification.
