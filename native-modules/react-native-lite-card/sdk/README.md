# OneKey Lite GPChannel SDK source recovery

A standalone, buildable reconstruction of the binary GPChannel core used by
`@onekeyfe/react-native-lite-card`. The native NFC transport and React Native
wrapper already have source in the parent package. This directory restores the
missing C++ core, its JNI adapter, source dependencies and documentation.

**Status:** source recovery integrated into the package's native builds and
release file list. CocoaPods compiles the core on iOS; Android CMake builds the
JNI library. Source, simulator/JNI and cryptographic vector evidence are separate
from physical-card and wallet-app acceptance.

See the package [behavior contract](../docs/SPEC.md).

## Build from source

Prerequisites: CMake 3.16+, a C/C++ compiler, Xcode for Apple targets and an Android
NDK for Android targets. All SDK source dependencies are checked in; no vendor
`.a`, `.so`, framework or network download participates in compilation.
Run these commands from this directory, with output directories of your choice.

Host library and focused native checks:

```sh
cmake -S . -B build-host -DCMAKE_BUILD_TYPE=Release
cmake --build build-host -j 4
ctest --test-dir build-host --output-on-failure
```

Android (set `ANDROID_NDK` to your installed NDK directory; repeat for
`armeabi-v7a` when needed):

```sh
cmake -S . -B build-android-arm64 \
  -DCMAKE_TOOLCHAIN_FILE="$ANDROID_NDK/build/cmake/android.toolchain.cmake" \
  -DANDROID_ABI=arm64-v8a -DANDROID_PLATFORM=android-24 \
  -DCMAKE_BUILD_TYPE=Release -DBUILD_TESTING=OFF
cmake --build build-android-arm64 -j 4
```

`libgpchannelNDK.so` embeds the recovered core and retains the original JNI class,
method names, signatures and JSON fields. It does not depend on a second
`libGPChannelSDKCore.so`. The latter is also produced for standalone core users.
Strip copies of release artifacts as needed; preserve unstripped files for debug
symbols. The React Native package builds `keys` and `gpchannelNDK` directly from
its Android CMake entry point.

Apple frameworks (device and both simulator architectures):

```sh
cmake -S . -B build-ios-device -G Xcode \
  -DCMAKE_SYSTEM_NAME=iOS -DCMAKE_OSX_SYSROOT=iphoneos \
  -DCMAKE_OSX_ARCHITECTURES=arm64 -DCMAKE_OSX_DEPLOYMENT_TARGET=15.0 \
  -DBUILD_TESTING=OFF
cmake --build build-ios-device --config Release -j 4
cmake -S . -B build-ios-simulator -G Xcode \
  -DCMAKE_SYSTEM_NAME=iOS -DCMAKE_OSX_SYSROOT=iphonesimulator \
  '-DCMAKE_OSX_ARCHITECTURES=arm64;x86_64' \
  -DCMAKE_OSX_DEPLOYMENT_TARGET=15.0 -DBUILD_TESTING=OFF
cmake --build build-ios-simulator --config Release -j 4
xcodebuild -create-xcframework \
  -framework build-ios-device/Release-iphoneos/GPChannelSDKCore.framework \
  -framework build-ios-simulator/Release-iphonesimulator/GPChannelSDKCore.framework \
  -output build-artifacts/GPChannelSDKCore.xcframework
```

Use an output path that does not already exist for `-create-xcframework`.
`GPChannelSDK.h` retains the vendor ABI, including its C++ reference parameter;
include it from C++/Objective-C++, not a pure C translation unit.

## API and lifecycle

| API | Contract |
| --- | --- |
| `JUB_GPC_TLVDecode` | Decode one supported vendor TLV; return numeric tag and hex value. |
| `JUB_GPC_ParseCertificate` | Parse the ASCII serial and hex subject identifier. Parsing does not authenticate a certificate or check its dates. |
| `JUB_GPC_Initialize` | Configure SCP11c, OCE certificate/key, host/group identifiers; generate a fresh ephemeral key pair. The recovered entry point accepts the existing AES-128 parameters. |
| `JUB_GPC_BuildMutualAuthData` | Build the mutual-authentication data after initialization. |
| `JUB_GPC_OpenSecureChannel` | Derive session keys and verify the card receipt; open only on success. |
| `JUB_GPC_BuildAPDU` / `JUB_GPC_ParseAPDUResponse` | Encode a plain command / separate response data and the status word. |
| `JUB_GPC_BuildSafeAPDU` / `JUB_GPC_ParseSafeAPDUResponse` | Encrypt/authenticate a command and authenticate/decrypt its response using the session state. |
| `JUB_GPC_Finalize` | Clear/release the session. Repeated finalization succeeds. |
| `JUB_FreeMemory` | Release an SDK output exactly once, including empty strings. |

Library return code `0` means the SDK operation succeeded. An APDU status word is
separate: callers must still check `wRet`, including PIN-retry status words.
All hexadecimal outputs use lowercase. The public header defines the error codes:
`1` operation error, `3` bad arguments, `5` null pointer, `6` unavailable session,
`8` invalid allocation pointer, and `10` response buffer too small.

The normal operation sequence is:

1. Read the card certificate using the existing transport. Parse it and check the
   device serial/identity using the existing caller policy.
2. Initialize and send the OCE certificate with the existing PSO command.
3. Build/send the mutual-authentication command; open using the returned receipt.
4. Call safe command/response functions in pairs. They mutate shared MAC/counter
   state. Serialize entire card sessions, not merely individual SDK calls.
5. Finalize on completion, NFC disconnect, cancellation or a SELECT command.
   After a failed operation, finalize and reopen before starting a new operation.

The native core owns one process-wide session per loaded library instance. It is
shared underneath separate main/background JS runtimes on iOS and Android.
The API has per-call locking; it does not support concurrent independent sessions.
There is no persistent cache. Returned allocations remain caller-owned across
finalization and must be freed separately.

The existing native `GPChannelSDKCoreVersionString` reports
`source-v1:<SHA256>` for the package source/configuration manifest in
`source-id-manifest.txt`. CMake regenerates its private header when those inputs
change; CocoaPods runs the equivalent Ruby generator before compilation. The
identity is independent of checkout location, keys, user data and build
environment. It identifies the selected source snapshot, not the binary hash or
external toolchains. Command-build diagnostics read that compiled native symbol
and the actual call's return code without logging command data.

## Preserved platform differences

These were observed against the installed binaries and retained explicitly:

| Behavior | iOS / host reference | Android |
| --- | --- | --- |
| Plain response with 1–5 data bytes plus SW | Rejected by the vendor parser | Accepted |
| SCP03 ICV counter encoding | Low eight bits in the last byte | Full 32-bit big-endian value in the last four bytes |
| Non-success protected SW | Advance counter; recovered output is empty | Require valid MAC and decrypt the authenticated payload |

The recovery does not silently change the legacy iOS counter behavior. Reference
checks include exchanges across counter 255/256 to prevent collapsing the two
platform implementations into an incorrect common behavior. A future protocol
change requires its own card compatibility and cryptographic review.

## Deliberate safety differences

These are reconstruction decisions, not claims of byte-for-byte original source:

- Missing contexts and null pointers return errors instead of assertion aborts.
- Hex and TLV lengths are bounded and validated before memory reads. Certificate
  inputs are limited to 1024 bytes, plain command data to 65535 bytes, and safe
  command data to 65511 bytes. Parsed plaintext uses the vendor's 1024-byte buffer.
- Incomplete certificates/receipts, unsupported public TLV tags and malformed
  protected responses are rejected. A non-success SW is returned only according to the platform authentication rules above.
- On iOS a safe error-only response returns empty data; the iOS vendor wrapper could
  instead return its entire zero-filled 1024-byte scratch buffer.
- Empty outputs enter the allocation registry and can be freed. Finalization and
  reinitialization release context objects instead of losing their pointers.
- JNI UTF strings have scoped ownership; the initialization key-length string
  remains alive for the complete native call.
- Debug logging is always disabled. Interrupted/short entropy reads are handled;
  entropy failure terminates instead of returning partially initialized bytes.

The upstream cryptographic primitives remain unchanged. Existing vendor protocol
choices, certificate trust policy and sensitive-memory clearing still require a
separate production security review; passing vectors do not prove those policies.

## Verification

`tests/verify.py` checks each SDK transcript against an independent implementation
of P-256 ECDH, SHA-1 preprocessing, KDF2/SHA-256, AES-CMAC and AES-CBC. It compares
plain APDU/TLV/certificate outputs, fresh sessions, bad receipts, modified response
MACs, encrypted payload boundaries, response limits and counter rollover. Reference
and recovery use independent random ephemeral keys; protocol code and entropy
functions in the reference are not patched.

```sh
python3 -m venv .venv
.venv/bin/pip install cryptography
.venv/bin/python tests/verify.py \
  --library build-host/libGPChannelSDKCore.dylib \
  --reference /path/to/disposable-host-reference.dylib \
  --report /path/to/verification.json
```

On Linux use the `.so` filename. Omit `--reference` for oracle-only verification.
The Python dependency is a test tool, not an application/SDK dependency.
`tests/safety.cpp` exercises 5000 malformed inputs and allocation/lifecycle checks;
run it with AddressSanitizer/UBSan compiler and linker flags when supported.
With host JNI enabled it also captures the actual JNI logger calls, checks the
metadata allowlist and synthetic sensitive-data absence, and verifies logger
failure does not change build results. From the package root,
`node tests/ios-input.cjs` covers the production manager/bridge/LCLogger path and
`node tests/source-id.cjs` checks source identity generation and incremental
native rebuilding. These checks do not prove physical NFC or minified app logs.

`tests/android/GPChannelProxy.java` and `tests/android_verify.py` run the same oracle
against real Android ART/JNI and a separately loaded original `.so` in an isolated
emulator.

Physical Lite cards, NFC transport, real PIN/backup operations, the React Native
app flow and Android armv7 runtime remain separate acceptance requirements. No
production credentials are included or read by these tests.
