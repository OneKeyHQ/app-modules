# electron-windows-hello

Windows Hello verification associated with an Electron `BrowserWindow`.
Independent Node-API implementation for Windows x64 and ARM64, with no runtime npm dependencies.

```sh
npm install <the-reviewed-GitHub-Release-tarball-URL>
```

## API

```ts
import {
  Availability,
  VerificationResult,
  allowSetForegroundWindow,
  checkAvailability,
  requestVerification,
} from "electron-windows-hello";

const availability = await checkAvailability();
const result = await requestVerification({
  windowHandle: mainWindow.getNativeWindowHandle(),
  message: "OneKey requests verification to unlock your wallet",
});
const verified = result === VerificationResult.Verified;
```

`checkAvailability()` resolves to a value in `Availability`: `Available`, `DeviceNotPresent`, `NotConfiguredForUser`, `DisabledByPolicy`, or `DeviceBusy`.
It first probes the window-owned `IUserConsentVerifierInterop` interface at runtime, without showing a verification dialog. It returns `DeviceNotPresent` on unsupported operating systems and architectures, without loading a native binary. An unavailable interop interface or Windows API failure rejects the promise; applications should offer their normal password authentication instead.

`requestVerification({ windowHandle, message })` requires a fresh, 8-byte `Buffer` from `BrowserWindow.getNativeWindowHandle()` and a non-empty message. It resolves to a value in `VerificationResult`: `Verified`, `Canceled`, `DeviceBusy`, `DeviceNotPresent`, `NotConfiguredForUser`, `DisabledByPolicy`, or `RetriesExhausted`.
Only `Verified` authorizes the operation. An overlapping request returns `DeviceBusy` and does not share another request's authorization.
Invalid or destroyed owner windows and Windows API errors reject the promise. Native errors retain the Windows HRESULT in the error message.

`allowSetForegroundWindow(processId)` grants a specific process permission to set the foreground window, when Windows permits the caller to grant it. It returns whether Windows accepted the grant. Call it from the foreground Electron main process before dispatching verification to a utility process. Zero, wildcard, and non-integer process IDs are rejected.

## Electron utility process and focus

In the main process, restore and activate the actual owner window before dispatching:

```ts
if (mainWindow.isMinimized()) mainWindow.restore();
mainWindow.show();
mainWindow.focus();
allowSetForegroundWindow(helloProcess.pid);
helloProcess.postMessage({
  message: "OneKey requests verification to unlock your wallet",
  windowHandle: Array.from(mainWindow.getNativeWindowHandle()),
});
```

Wait for the owner to be focused before dispatching. If `mainWindow.isFocused()` is false, defer verification until a user-initiated window activation. `show()` and `focus()` alone do not guarantee that Windows grants foreground permission.

In the utility process, reconstruct the handle with `Buffer.from(data.windowHandle)` and call `requestVerification` with that buffer. Always send a result or error back to the caller.
Do not convert the handle to a JavaScript number or discover the owner by querying an unrelated foreground window.

WinRT is initialized and awaited on a libuv worker, so verification does not block Electron's JavaScript thread. Windows controls foreground policy, title, icon, taskbar presentation, and the authentication method. An owner handle and foreground grant do not guarantee a particular icon or force focus against operating-system policy.

## Support

- Windows x64 and ARM64; both native binaries ship in the same package and are selected using `process.arch`.
- Node.js 20.17 or later, including Electron runtimes with Node-API 8 support.
- Windows 10 and 11 use the same window-owned `IUserConsentVerifierInterop::RequestVerificationForWindowAsync` path, selected by runtime interface availability rather than an OS version check. Microsoft's method documentation lists build 22000, while its [CsWinRT implementation](https://github.com/microsoft/CsWinRT/blob/master/src/cswinrt/strings/ComInteropHelpers.cs) and [Electron sample](https://github.com/microsoft/dynwinrt/tree/main/samples/js/windows-hello) indicate Windows 10 support. Windows 11 interaction has been manually verified; Windows 10 interaction testing is still pending, so no minimum Windows 10 build is claimed.
- There is no silent fallback to an unowned verification dialog. If availability fails, offer the application's normal password authentication. Cancellation, unsuccessful verification, and native errors never cause an automatic retry; a new Hello attempt must be explicitly initiated by the user.
- No credential storage, account management, key generation, signing, PIN collection, or custom authentication UI.

## Development and release

Use the Rust compiler pinned in ../../release.json and the Windows MSVC toolchain, then run:

```sh
npm ci --ignore-scripts
npm run build
npm test
npm run test:types
```

Build a specific target with `npm run build -- x86_64-pc-windows-msvc` or `npm run build -- aarch64-pc-windows-msvc`.
The dedicated desktop-win-release workflow builds and tests each architecture on a native Windows runner using the desktop baseline. It packs the combined runtime as a GitHub Release asset and generates build attestations. Only the runtime entrypoint, declarations, native binaries, README, license, and package metadata are included. See ../../SPEC.md and SPEC.md for the release and consumer policy.

With Electron installed in the development environment, `npm run test:electron` checks the real HWND and utility-process path without an authentication prompt. `npm run test:electron -- --interactive` opens a visible owner window and requests Windows Hello; verify focus, cancellation, multi-monitor placement, and taskbar presentation in a logged-in desktop session. Headless CI cannot establish those interaction results.

This module is independently released through GitHub Releases, not npm. The existing public npm version is not modified. Source package versions are internal metadata; the runtime tarball is stamped with the reviewed desktop release version.

## References

The API choice was informed by [Microsoft's desktop interoperability documentation](https://learn.microsoft.com/en-us/windows/win32/api/userconsentverifierinterop/nf-userconsentverifierinterop-iuserconsentverifierinterop-requestverificationforwindowasync) and investigation of [passport-desktop](https://github.com/MarkusJx/passport-desktop). This project has its own source, API, tests, and release pipeline; it does not include Passport account or cryptographic APIs.
