# OneKey Lite native module contract

## Status and scope

The React Native / iOS / Android implementation builds the recovered vendor core
in `sdk/` from source. CocoaPods compiles the core into the iOS pod; Android CMake
builds `gpchannelNDK` alongside the existing `keys` library. Precompiled GPChannel
frameworks and JNI libraries are removed. Public React Native and native SDK APIs
remain unchanged. Cryptographic review and physical-card acceptance remain open.

## Ownership and runtime

Targets: iOS and Android. Main and background JavaScript runtimes have separate
heaps and initialize independently; a hex string crossing the bridge is a copy.
The native GPChannel library owns one process-wide context, allocation registry,
SCP11 session and SCP03 counter per loaded library instance. These native
resources are not isolated by JS runtime. Serialize whole card operations;
per-call locking does not make multiple interleaved sessions safe. No JS-runtime
readiness or single deserialization assumption is introduced.

## Recovered core contract

Preserve the existing `GPChannelSDK.h` ABI, hexadecimal encoding, status words,
SCP11c handshake and SCP03 secure messaging for valid caller inputs. Library
outputs belong to the library and must be released with `JUB_FreeMemory`.
Lifecycle: parse card certificate and check its serial at the caller, initialize,
send the OCE certificate, mutual authenticate, open, exchange paired safe
command/response calls, finalize. Finalize on disconnect, SELECT or cancellation
and reopen before another secure operation. No persistent cache or migration.

## Failure and safety

No production keys, PINs or mnemonics enter recovery tests. Debug logging is
disabled in recovered builds, including Debug configurations. Invalid caller
pointers, malformed inputs and missing contexts must not trigger the original
assertion aborts. Necessary memory-safety deviations are documented in
`sdk/README.md`; valid protocol behavior must be checked against the old binary.
There is no fallback from a rejected MAC or receipt to plaintext.

## Platforms and acceptance

The recovered core has standalone CMake targets for host, iOS and Android, and
the package publishes the source needed by CocoaPods and Android CMake.
The native NFC transport and React Native adapter remain in their existing
platform implementations. Desktop and web consumers are unchanged.

Source recovery and host differential checks do not prove physical-card behavior,
Android runtime parity, cancellation or app integration. Acceptance must record
source provenance, build targets, valid/invalid protocol vectors, state reuse,
receipt/MAC rejection and the remaining hardware gaps. No performance or bounded
production concurrency claim is made. Build and verification commands are
documented in `sdk/README.md`.

### Observed vendor platform differences

Android accepts short plain responses, encodes a 32-bit ICV counter and requires
MAC verification/decryption even for non-success protected status words. iOS
rejects 1-5-byte plain payloads, uses an 8-bit ICV counter and advances the counter
without protected-payload verification for non-success SW; recovery returns empty
data in that iOS error path. These existing differences must remain explicit.
