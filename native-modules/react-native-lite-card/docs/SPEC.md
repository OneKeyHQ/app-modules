# OneKey Lite native module contract

## Status and scope

The React Native / iOS / Android implementation builds the recovered vendor core
in `sdk/` from source. CocoaPods compiles the core into the iOS pod; Android CMake
builds `gpchannelNDK` alongside the existing `keys` library. Precompiled GPChannel
frameworks and JNI libraries are removed. The recovered core ABI remains
unchanged. The optional write identity guard below changes the native bridge ABI
and requires rebuilt apps. Cryptographic review and physical-card acceptance
remain open.

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

## Write identity and state reads

Status: Implemented in source; native runtime and physical-card acceptance remain
open. State reads distinguish a failed or empty read from a confirmed empty/new
card and return existing read errors on failure. Android V1 infers backup from
valid PIN status; Android V2 requires its separate backup-status read. Retry-count
sentinels stay unchanged. iOS rejects failed/empty PIN status and retry reads,
and clears its status before resynchronizing so a failure cannot reuse activated
state.

The JS wrapper adds `setMnemonic(mnemonic, pwd, overwrite = false,
expectedSerial?: string)`. Omitted values become native null. Supplied values,
including empty strings, must exactly equal the freshly read card serial before
any PIN verification/setup, reset or backup write. After otherwise successful
card acquisition, a mismatching or unavailable identity returns existing
DeviceMismatch (2003) without unguarded retry. A failed card info read instead
returns the existing read/connection error. The expectation
is neither persisted nor logged. iOS syncs card info before checking identity.

The native method adds nullable expectedSerial before callback. This changes the
TurboModule ABI and requires generated bindings and rebuilt iOS/Android apps to
ship with the wrapper. It cannot be delivered through JS-only OTA.

## Native diagnostic logging

Status: Implemented in source; native build and physical-card acceptance are
reported separately. This section scopes diagnostics only.

Command diagnostics distinguish build failure, raw status words, response byte
counts, parser presence/return status and effective status words.

### API, lifecycle, and compatibility

Except for the identity guard and fail-closed state reads above, card commands,
callback payloads, validation, operation ordering, cancellation, retry and timeout
behavior remain unchanged. No server logs, new dependencies, or cross-layer
correlation parameters are added.

- iOS records native method entry, SDK checks, callback error codes and elapsed
  time through LCLogger / OneKeyLog. Manager/session lifecycle records begin,
  active, tag detection, connect result, and invalidation ownership.
- Android records acquisition source (channel, last connection, wait), connection
  availability, card state booleans, overwrite, and write phases: set PIN,
  verify PIN, and backup. Backup command results record SW1/SW2 and parse/success
  booleans. Transport failures use fixed categories.
- IDs are temporary local counters, reset with their owner/process. Wall time,
  operation names, and local IDs support approximate timeline correlation;
  concurrent JS and native operations cannot be paired strictly across layers.

### Safety and performance

New logs allow only temporary local IDs, fixed operations/stages/reasons,
wall/elapsed times, counts, state booleans, known error codes, and fixed error
categories or recognized-domain booleans. They must never include PINs,
mnemonics/encoded backups, card serials, wallet identifiers, APDU/response bodies,
certificates/keys, arbitrary exception messages/descriptions/stacks, or payloads.
Existing verbose APDU debug gating is unchanged; diagnostics do not enable it.
Logging is restricted to operation/session boundaries and command stages, with
no polling or frame logs. iOS responseDataBytes excludes SW; Android
rawResponseBytes includes SW and is -1 for the transport sentinel. iOS raw SW is
separate from effective SW, including 9000 changed to 0000 after parse failure.
Android lastSdkError reads the existing process-wide error slot immediately only
after a null parser result; it cannot strictly correlate concurrent operations.
iOS records the parser return code directly. State logs carry readSucceeded,
so state booleans after failure cannot confirm absence of backup or PIN.

### Conformance and acceptance

Source owners: iOS ReactNativeLiteCard.mm, OKNFCManager.m, OKLiteV1.m,
OKLiteCommandTool.m and OKNFCBridge.mm; Android ReactNativeLiteCardModule.kt,
OnekeyLiteCard.kt, CommandGenerator.kt and Connection.kt. Review new log
interpolations against the allowlist and compile the affected native sources.
No storage/schema migration is introduced. The wrapper argument and native ABI
change are described above. Wrapper tests prove optional/empty serial forwarding
and mismatch-result preservation, not native identity checks or card writes.
Physical iOS/Android card runs must confirm entry-to-callback timelines,
PIN-to-NFC handoff, first backup failure, cancellation, and successful backup.
Source inspection/builds do not prove those device outcomes or identify the
underlying failure cause.
