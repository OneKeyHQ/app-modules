# @onekeyfe/electron-windows-process-hardening

Private local candidate for synchronous Windows **current-process** hardening in Node.js or the Electron main process. Importing this module has no side effects.

```ts
import { hardenCurrentProcess } from "@onekeyfe/electron-windows-process-hardening";

// The consumer decides production timing and any dev/opt-out behavior.
const result = hardenCurrentProcess();
if (result.dacl.status === "failed") console.error(result.dacl);
if (result.extensionPoint.status === "failed") console.error(result.extensionPoint);
```

The DACL stage replaces the current process DACL with a protected ACL: `OWNER RIGHTS` denies `WRITE_DAC`; the current token user and `SYSTEM` receive `0x00101001`. The independent extension-point stage sets `ProcessExtensionPointDisablePolicy` to DWORD `1`. Each stage returns `applied` or `failed`, with `win32Error` when Windows supplies a code. On unsupported platforms or architectures, both return `unsupported` without loading a native library. A native binding load error returns two `failed` results. Electron renderer and utility processes cannot call this API.

Call from the main process only after confirming that a crash handler has acquired the process handles and registration it needs, and before creating the first application window. A return from `crashReporter.start()` or Sentry initialization alone does not prove that handshake is complete. This package does not resolve Crashpad lifecycle or handler restarts, and it does not configure Electron bootstrap, development-mode behavior or opt-out. A restricted process DACL can prevent a subsequently launched crash handler from opening the process.

Build with the Rust compiler pinned in `../../release.json` and the Windows MSVC toolchain:

```sh
npm ci --ignore-scripts
npm run build -- x86_64-pc-windows-msvc
npm test
npm run test:types
npm run test:electron
```

Repeat the build and tests on native ARM64. `npm run build -- aarch64-pc-windows-msvc` emits the ARM64 binary. Both native files are needed for `npm pack` and its package-boundary check. Native tests run in isolated children because process hardening should not affect the test runner. This candidate is private and is not part of `desktop/release.json` or consumer dependency manifests.

For the test-only x64 crash collector, fetch the Crashpad source archive for commit `ad1827ddbc03f1c214030b31b380243ded660b95` from `https://codeload.github.com/chromium/crashpad/tar.gz/ad1827ddbc03f1c214030b31b380243ded660b95` (SHA-256 `1a6d74bc7f4ca57fe6a4fc1ae47486477179d71d1b1b556e8f919a5149b0565d`). Hydrate a separate Crashpad checkout at that commit with `gclient sync --revision crashpad@ad1827ddbc03f1c214030b31b380243ded660b95`; its `DEPS` pins the fetched dependencies and Clang. Then run from this package on Windows x64:

```powershell
.\scripts\build-capture.ps1 `
  -CrashpadArchive <verified-archive.tar.gz> `
  -DependencyRoot <hydrated-checkout\crashpad> `
  -WorkDirectory <new-short-build-directory> `
  -Ninja <ninja.exe> `
  -Python <python.exe>
```

The script checks the archive, `DEPS`, four patched upstream files, local patch, GN and Clang identities; it creates `<build-directory>\crashpad\out\x64\onekey_capture_test.dll` and a neighboring `.receipt.json`. The compiled third-party inputs are mini_chromium at `e5169551c51f3a52eee36b3b03f219cefe380237`, zlib at `fef58692c1d7bec94c4ed3d030a45a1832a9615d`, and Clang `llvmorg-20-init-17108-g29ed6000-2`; GN is revision `5e19d2fb166fbd4f6f32147fbb2f497091a54ad8`. The script checks fixed tree hashes of those hydrated inputs and buildtools before and after copying them. Each tree hash is SHA-256 of ordinal-sorted `relative/path` + NUL + lowercase file SHA-256 records joined with LF; `.git`, `out`, `__pycache__`, and `.cache` trees are excluded. The receipt records those tree hashes, actual DLL/PDB hashes, and tool versions. A different DLL hash is a new candidate and needs the focused runtime cases below; no byte-for-byte cross-machine result is assumed. Keep the receipt adjacent to the DLL when setting `ONEKEY_CRASHPAD_CAPTURE_DLL`. The standard x64 build copies both into the module directory and writes `candidate-build.receipt.json` binding the crash-capable `.node`, helper, DLL, and verified Electron headers. `npm run prepack` checks that same set before packing; a later x64 build without the DLL input fails this check. Both receipts are local build evidence and are excluded from `package.json` runtime files. To keep existing frozen binaries intact, make a fresh source-only copy of this package under `.tmp`, run `npm ci --ignore-scripts`, build ARM64 in that copy, then run `npm run build -- x86_64-pc-windows-msvc` with the DLL input and `npm run prepack`. Set `CARGO_TARGET_DIR` to a dedicated directory for that copy; the build honors it. The x64 collector remains double test-gated, and ARM64 collector startup remains unsupported.

For a new x64 candidate, the bounded consumer harness cases are `normal`, `knownchain`, `create-gate`, `hang-scrub`, `suspend-read`, and `suspend-unwind`. Preserve the old candidate, symbols, and manifest before substituting new binaries into its fixed harness path. Record the new hashes and keep behavior results distinct from complete Phase A acceptance. The 30-second native budget and 35-second exit gates are unchanged.

The 2026-10-22 private closeout candidate was built from the pinned source and dependency set above. Its x64 `.node`, helper, and capture DLL SHA-256 values are respectively `b7b01bd55cf258380c4493a6941e99d14bd1af9254db45713a753d1815d602f4`, `c45a64db30f86438750e69ec48d4e6a5b530f48f019ac46817b20fc96ba1260a`, and `d0ff1d4345b8ec08da910198380dc3f8a9b0f67da0ea5a5065b52e7b3a0bc14b`. The local x64 candidate receipt is under `.tmp/candidate-closeout-module-v2/candidate-build.receipt.json` in the native worktree. The earlier `fb1aae...`/`807465...`/`bc7cf...` candidate and its symbols remain under `.tmp/p1-phase-a/candidate-history`; these hashes are historical evidence, not a required output of a future rebuild.

The fixed consumer harness ran once per case against this new candidate and the unchanged Electron 44.5.1 production-main fixture. Every case reported `behaviorCompleted=true`, `acceptanceComplete=false`:

| Case | Kernel exit (ms) | Main signal (ms) |
| --- | ---: | ---: |
| `normal` | 191 | 339 |
| `knownchain` | 100 | 200 |
| `create-gate` | 105 | 167 |
| `hang-scrub` | 30024 | 30076 |
| `suspend-read` | 30023 | 30059 |
| `suspend-unwind` | 30015 | 30085 |

These are bounded local behavior results, not complete Phase A acceptance. Ordinary-account inherited-handle inventory remained unverified in all six runs because its target QLI anchor/layout check failed; that failure is not proof of an `ACCESS_DENIED` open. The main process's high-risk thread and token rights remain outside this helper-focused protection scope, and the helper's `CreateProcess`-to-`AssignJob` interval has an inferred parent-death cleanup window. This candidate only aims to prevent new high-risk process opens against the protected helper; it does not establish complete resistance to same-user malware. No consumer production code, release inventory, ACL, native deadline, or exit ordering was changed by this closeout.

The focused harness source is in `test/p1-phase-a/` and writes runs only below this module's `.tmp/p1-phase-a/harness-runs`. With the candidate copied to this module's `.tmp/p1-phase-a/candidate`, an unchanged built consumer main bundle, and its official Electron executable at `<consumer-root>/.tmp/process-hardening-runtime/node_modules/electron/dist/OneKey.exe`, run one case at a time:

```powershell
node test/p1-phase-a/run.cjs normal <absolute-candidate-manifest.json> <absolute-consumer-root>
```

The manifest must specify `candidateDirectory` (the fixed module `.tmp/p1-phase-a/candidate` path) and the SHA-256 fields `electronSha256`, `mainBundleSha256`, `nativeSha256`, `helperSha256`, `captureSha256`, `indexSha256`, `typesSha256`, and `packageSha256`; compute them from the actual files before running. Preserve its identity and each run's `run.json`. The harness itself asserts the existing 35-second kernel/signal gates and records the ordinary-account handle inventory result. The original six-case evidence used the pre-existing consumer `.tmp` harness; after this source was placed here, the relocated `normal` entry completed behavior with the same inventory limitation. This later entry check does not replace the original six-case matrix.

The native source and collector can be rebuilt from this module plus the pinned external inputs. The six-case integration harness additionally requires a reviewed companion consumer checkout with its built production main bundle and official OneKey executable; that consumer source/bundle is not included here. The manifest binds the exact consumer bundle and executable hashes so a different consumer build is an explicitly different validation target.
