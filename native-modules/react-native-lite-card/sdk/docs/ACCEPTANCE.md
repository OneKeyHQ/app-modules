# Recovery acceptance evidence

Date: 2026-11-03. Branch: `recovery/onekey-lite-sdk` in the isolated
`app-modules-lite-sdk-recovery-20261103` worktree. Original app/native-package
checkouts and their vendor binaries were not changed. These checks preceded PR
preparation. The PR replaces this package's vendor binaries with native source
builds; no package publication or consumer application update was performed.

## Completed checks

| Layer | Result | What it proves |
| --- | --- | --- |
| iOS reference identity | PASS | The installed binary and the recovered dSYM share UUID `6CE8D6E1-5AAC-3B76-AAEB-2005E6ED2A75`; the historical reference has the same 193016-byte instruction section. |
| Host build from source | PASS | CMake builds the complete core without linking a vendor binary. |
| iOS-core reference/oracle comparison on macOS | **1718 checks passed** | Plain APDU, TLV/certificate fields, independent ECDH/KDF/receipt/AES/MAC calculation, fresh sessions, bad receipt/MAC rejection, payload boundaries, counter rollover and error-status state transitions. This is host execution of the iOS reference, not an iOS NFC/device run. |
| Android arm64 ART/JNI | **1719 checks passed** | Actual JNI registration and output schema plus the same independent cryptographic oracle against the installed original Android `.so` in a separate ART process. Preserves short-response, full-counter and authenticated-error differences. C allocation ownership is not directly inspected through JNI. |
| Native ASan + UBSan | PASS | The standalone C++ harness completes 5000 malformed-input cases plus lifecycle, null/missing-context and allocation ownership checks with halt-on-error enabled. |
| iOS framework build | PASS | arm64 device and arm64/x86_64 simulator framework binaries, public headers and module maps generated from source. |
| Apple packaging | PASS | `xcodebuild -create-xcframework` packages the two platforms; simulator architecture inspection confirms both slices. |
| Objective-C++ consumer link | PASS | A device-target consumer imports `GPChannelSDKCore/GPChannelSDKCore.h`, calls `JUB_GPC_BuildAPDU`/`JUB_FreeMemory` and links against the recovered framework. |
| Android native builds | PASS | NDK 27.1.12297006, API 24, arm64-v8a and armeabi-v7a. The JNI `.so` needs only system `libm`, `libdl` and `libc`; no second SDK `.so` or vendor static library. |
| Package iOS source integration | PASS | CocoaPods installs the actual package and compiles `ReactNativeLiteCard`, including the recovered core, for arm64 iOS devices and arm64/x86_64 simulators without a vendored GPChannel framework. |
| Package Android source integration | PASS | The actual package's Gradle `assembleRelease` builds its Kotlin/Java/JNI code. The AAR contains source-built `keys` and `gpchannelNDK` for arm64-v8a, armeabi-v7a, x86 and x86_64, with no second SDK library. JNI exports and 16 KB load-segment alignment are verified. |
| npm source package | PASS | `npm pack --ignore-scripts` includes the required SDK sources and no precompiled SDK libraries. Extracting the tarball into a new directory, building its host SDK and running CTest succeeds. |
| Python syntax / recovery-owned whitespace | PASS | Recovery harnesses parse; recovery-owned changes pass `git diff --check`. Imported upstream files retain their original whitespace. |

The Android run uses an isolated Android 14/API 34 arm64 emulator, with its AVD
and user data under the recovery worktree on the external volume. It does not use
the attached physical phone or install/read the wallet application. The emulator
is stopped after testing; its task-specific state and evidence remain available.

## Explicit limits

- No physical Lite card, NFC operation, actual PIN/mnemonic, wallet-app flow,
  cancellation/reconnect acceptance or Android armv7 runtime test was performed.
- The host-reference copy changes platform metadata and signing only; original
  protocol instructions and entropy generation remain unchanged. This is a
  comparison technique, not a production platform conversion.
- The restored implementation includes documented input/memory-safety differences;
  it does not claim identical undefined behavior, aborts or allocator leaks.
- The vendor's iOS and Android protected-error rules differ. Android rejects a
  status-only unauthenticated error and accepts an authenticated/decrypted error;
  iOS returns the non-success status without decrypting that payload. These paths
  and the subsequent counter state are explicitly tested.
- An attempt to host the Python oracle under ASan stopped before protocol tests
  because the runtime could not install its interceptors in that Python launcher.
  It is not counted as a sanitizer pass. The separate native ASan/UBSan executable
  passed; the full Python cryptographic oracle passed in the normal host process.
- JuBiter/GPChannel source licensing, certificate trust policy, sensitive-memory
  clearing and a full production cryptographic review remain outside this source
  recovery acceptance. No real secrets were read, copied or logged.
- A full staged `git diff --check` reports 139 inherited whitespace diagnostics
  in imported upstream files. They are retained for source fidelity; the
  reconstruction, tests and newly written documentation pass the scoped check.

## Reproduction and artifacts

Run commands are in [the SDK README](../README.md). `tests/verify.py` accepts a
recovered library and an optional independent reference; its report names the
oracle and explicitly marks physical-card verification false.

The task evidence directory is
`/Volumes/T7Shield/Project/onekey-lite-sdk-recovery-evidence-20261103/`.
It retains pinned upstream checkouts, matching dSYM, Ghidra projects/exports,
build logs, original-reference copies, JSON reports and rebuilt artifacts.
The `artifacts/` directory contains the source archive, Apple XCFramework,
Android ABI libraries and SHA-256/source manifests. Unstripped Android binaries
remain in the per-ABI build directories for debugging.
`source-integration/` retains the isolated CocoaPods/Gradle consumers, build logs
and package-extraction checks for replacing the package's vendor binaries.
