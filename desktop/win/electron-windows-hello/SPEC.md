# Windows Hello

## Source and ownership

Maintained in OneKeyHQ/app-modules. Imported from the independently developed
MIT-licensed huhuanming/electron-windows-hello commit
3fd42e8eacb2f90e80be616d957c27cad438b4bc, not a fork of passport-desktop.
The native implementation and public API are unchanged by this migration.
The existing npm electron-windows-hello@0.1.0 publication is untouched.

## Contract

- Windows x64 and ARM64; Node-API 8, Node >=20.17.
- Availability probes IUserConsentVerifierInterop at runtime on Windows 10/11.
  Missing interface/API errors reject; unsupported platforms return unavailable.
- Verification requires the owner's fresh eight-byte HWND and explanatory text.
  Only Verified authorizes. Cancellation/failure never automatically retries.
- Concurrent calls return DeviceBusy; requests never share authorization.
- Reject invalid/destroyed HWNDs. Initialize and await WinRT on worker threads.
- Grant foreground permission only to a specific PID. The caller must restore
  and focus the owner first and dispatch from Electron's main process to the
  utility process. Windows remains responsible for foreground policy and UI.
- No PIN collection, credential storage, signing, key generation or account API.
- No unowned-dialog fallback. Applications retain wallet-password fallback.

## Build and package

Use the compiler pinned in ../../release.json, Cargo.lock, and package-lock.json.
Run cargo fmt, clippy, the locked release build, JS/native/type tests, and the
noninteractive Electron HWND/utility-process smoke test for each architecture.
Package index.cjs, index.d.ts, package.json, README.md, LICENSE and the
two .node binaries, plus desktop-win.json and its desktop-win.sigstore.json
portable proof. The signed inventory hashes exactly the seven runtime files.
The release tooling stamps the packaged version from the desktop baseline
without rewriting private source package metadata. Only the dedicated desktop
workflow publishes the combined runtime to npm, with provenance and no install
scripts. See ../../SPEC.md for the source and package verification policy.

The smoke test must not prompt for credentials. Availability may be unavailable
in CI; an API error is reported, not silently reclassified as a test pass.
Interactive owner/focus tests remain a manual release check, especially Win10.
