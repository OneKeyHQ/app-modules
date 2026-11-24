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

Command input compatibility (Implemented in source; physical-card acceptance
open): `JUB_GPC_BuildAPDU` and
`JUB_GPC_BuildSafeAPDU` accept ASCII hexadecimal data of either parity. An odd
number of digits is decoded exactly as if one `0` were prepended to the whole
input; even input is unchanged. Plain command data remains bounded to 65535
decoded bytes and safe command data to 65511 decoded bytes, including that
leading nibble. Empty command data remains supported by the core. Invalid
characters and oversized inputs fail with bad arguments and a null output.
This compatibility applies only to command construction. Certificate, TLV,
handshake and response decoding retain strict even-length validation, including
protected response length, receipt and MAC checks.

Before any PIN setup/verification, reset or backup write, native `setMnemonic`
rejects null, empty, non-ASCII-hex (including embedded NUL) and oversized backup
input without constructing a safe APDU or advancing secure-channel state. Both
parities are allowed. V1 permits at most 65535 decoded bytes; V2 permits 65511.
The iOS manager checks syntax and the V1 limit before retaining inputs or starting
NFC, then checks the detected version's limit before identity or write phases.
It completes invalid input once with nil card and existing generic write error
(2001). Android checks the known session version before any write phases and
throws existing execution failure (4000). These checks do not infer mnemonic
format, word count or metadata. No public ABI or new dependency is introduced.

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

Card information crosses the React Native boundary with `isNewCard` as a
JavaScript boolean on both platforms: `true` for a new card and `false` for an
activated card. iOS must box this field as `BOOL`, not an integer comparison
result (`0`/`1`), so callers can validate its type before requesting a PIN.
The host iOS regression checks the production V1/V2 card-info payloads for both
states using the same NSNumber boolean-class distinction as React Native.
Physical-card and end-to-end React Native acceptance remain open.

`CardInfo.hasBackup` is `boolean | null`: Android reports its known backup
state, while iOS returns `null` for unknown state. Callers must preserve this
distinction instead of interpreting unknown as confirmed absence of a backup.
NFC event `code` fields are numbers, matching the existing Android event payloads
and the public connection listener. These declarations describe existing native
values; they do not change native event payloads or operation ordering. Wrapper
tests cover nullable/boolean backup states and numeric connection event codes.

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

Android operation ownership (source implementation; physical-card acceptance is
open): each operation owns one `Connection` and its freshly read `CardState`.
The identity check, PIN setup/verification, reset and backup use that same
connection. A process-wide coroutine mutex shared by all native module instances
owns the GPChannel context from connection construction through the entire
operation and finalization. Construction failure, command failure and coroutine
cancellation release that ownership in `finally`; cancellation cannot interrupt
an already running synchronous NFC command. Permission checks, waiting for a
tag and bridge callbacks do not hold the mutex. Callback state belongs to the
completed operation rather than a module-level current-card slot.
The existing connected UI event is queued through React Native's asynchronous
event emitter after initialization while ownership is held, before PIN/write
commands. Completion callbacks and terminal UI cleanup happen after release.

Passive Android discovery shares the same mutex, skips a busy context without
queuing an old discovery, and never replaces an operation's connection. Only a
successfully queued tag may be inspected. Consumed tags are marked before active
acquisition; passive inspection and its event discard consumed discoveries.
Freshly delivered tags may connect during acquisition; only reuse of the previous
tag requires an existing connection. Cancellation before acquiring a tag retains
the existing null-channel behavior and releases the operation UI once.

The native method adds nullable expectedSerial before callback. This changes the
TurboModule ABI and requires generated bindings and rebuilt iOS/Android apps to
ship with the wrapper. It cannot be delivered through JS-only OTA.

PIN changes expose `changePin(oldPin, newPin, expectedSerial?: string)` with the
same optional identity contract. The wrapper forwards omitted values as native
null and preserves supplied empty strings. Before any PIN verification or
mutation, iOS refreshes card information and Android checks its acquired
operation-owned card state. A different or unavailable serial returns existing
DeviceMismatch (2003); a failed card read retains the existing connection/read
error. Neither case dispatches the PIN change or retries without the guard.
The expectation is cleared after dispatch or cancellation and is never logged
or persisted. iOS claims the pending completion and identity snapshot together;
a connection arriving after cancellation cannot dispatch a PIN change, while
an already claimed operation owns its terminal result. Omitting the expectation
retains the existing unguarded API behavior. This
also changes the TurboModule ABI and requires native rebuilds. Host regressions
exercise production operation code with fake card/transport adapters; real NFC
and physical-card acceptance remain open.

## Native diagnostic logging

Status: Implemented in source; native build and physical-card acceptance are
reported separately. This section scopes diagnostics only.

Command diagnostics distinguish build failure, raw status words, response byte
counts, parser presence/return status and effective status words.

Native build and input diagnostics (Implemented in source; host adapter checks
verified; physical-card acceptance open): the existing native
`GPChannelSDKCoreVersionString` contains `source-v1:<SHA256>`, generated from a
fixed package-relative manifest of source/configuration content. The manifest
covers SDK sources/dependencies, native iOS/Android sources, command routing and
build configuration. Directory entries select source suffixes only and exclude
hidden/cache/build directories. It identifies
the compiled native source snapshot, not the JS package or binary hash. Keys,
test data, generated output, environment values, absolute paths, users and hosts
do not participate. CMake tracks manifest/content changes for reconfiguration;
CocoaPods regenerates a private header before compilation on every native build. No new C,
JNI or React Native ABI is introduced.
The source-identity shell phase disables Xcode environment-variable logging;
build-time environment values must not be dumped by this added phase.

Input preflight records only input presence, backup UTF-16 character count/parity,
whether the card version is known, numeric version, overwrite, accepted boolean
and a fixed reason (`missing`, `empty`, `too_large`, `non_hex`, `accepted`). iOS
distinguishes entry (unknown version) and detected-version validation; Android
records its known session version. It never hashes or logs the input, a prefix,
PIN length or backup metadata values. Actual command build logs use the native source
ID, safe/plain boolean, the return code from that same build call and output
presence. They do not reconstruct a command or consult a stale global error.
The log-only source ID display is `source-v1:` followed by eight groups of eight
lowercase hex digits separated by `-`, so the complete digest survives the
logger's generic 64-hex redaction. Only private zero-argument formatters reading
the compiled native symbol may produce this display; an unexpected symbol shape
logs fixed `unavailable`. The symbol retains its canonical 64-hex value and ABI.
Global logger sanitization remains unchanged: raw canonical IDs and unrelated
key, mnemonic and token canaries must still be redacted.
Logging is best effort: logger failures must not change SDK results or clear a
previously pending JNI exception. Only exceptions raised by logger lookup/calls
may be cleared by diagnostics.
Android consumer rules preserve only the existing logger class and static
`info(String, String)` entry used by JNI under shrinking. This wiring is source
reviewed; a minified Android application logging run remains unverified.

### API, lifecycle, and compatibility

Except for command input compatibility/preflight, the identity guard, fail-closed
state reads, card-info boolean correction and Android operation ownership above,
card commands, callback payloads, validation, operation ordering, cancellation,
retry and timeout behavior
remain unchanged. No server logs, new
dependencies, or cross-layer correlation parameters are added.

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

New logs allow only native source IDs, temporary local IDs, fixed
operations/stages/reasons, wall/elapsed times, counts, state booleans, known error
codes, and fixed error
categories or recognized-domain booleans. They must never include PINs,
mnemonics/encoded backups, card serials, wallet identifiers, APDU/response bodies,
certificates/keys, arbitrary exception messages/descriptions/stacks, input hashes
or fragments, local paths/users/hosts, or payloads.
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
`node tests/android-session.cjs` compiles the production Android session,
identity guard and operation methods against fake transport/lifecycle adapters.
It covers all write phases sharing one connection, two clients sharing the
process gate, busy/consumed passive discoveries, unconnected tag delivery,
cancelled waiters, initialization/command failure cleanup and completion reentry.
The fake transport does not prove Android NFC, React Native event delivery,
module lifecycle or physical-card behavior. The module keeps tag waits outside
ownership and terminal UI cleanup in its single operation `finally`.

Command compatibility/preflight regressions:

- `sdk/tests/safety.cpp` calls the production C ABI with raw odd/even/invalid
  strings and decoded-byte limits. A synthetic 45-digit backup is encrypted and
  checked against independently constructed plaintext/padding and CMAC, followed
  by authenticated response decryption and tampered/short/odd response rejection.
  The same CTest target retains its JNI and malformed-input coverage in CI.
- `sdk/tests/verify.py` (and the Android adapter in `android_verify.py`) preserves
  raw command strings across the ABI/JNI boundary, compares leading-zero plain
  commands and chained safe exchanges against an independent crypto oracle, and
  optionally compares the original vendor library. Differential checks require a
  separately supplied reference; no reference binary is committed.
- `node tests/android-session.cjs` executes production `setMnemonic` validation
  for V1/V2, both write modes, invalid syntax and byte limits before fake PIN,
  verify/reset and backup phases. Valid odd/even inputs retain the write sequence.
- `node tests/ios-input.cjs` compiles the production iOS manager and V1/V2 card
  classes on macOS with fake CoreNFC/card adapters. It covers syntax rejection before session creation,
  detected-version byte limits before identity/card-operation dispatch, one
  existing generic-error callback, cleared input properties and unchanged valid
  input forwarding for V1/V2 and both write modes. It does not execute a real
  card's PIN/backup commands or React Native callback mapping. The runner is part
  of the existing native CI job.
- The same iOS runner now compiles the actual SDK bridge, core and LCLogger
  adapter and captures the existing logger endpoint. It checks exact metadata
  fields and synthetic sensitive-data absence, actual build codes 0/3/6 and
  unchanged callbacks/results when diagnostics throw. Android's operation
  harness captures the actual preflight call; the C++ JNI harness captures the
  production JNI logger call, including unavailable/throwing loggers and pending
  exception preservation. These are adapter tests, not device log-file evidence.
- `node tests/native-log-sanitization.cjs <jni-capture> <bridge-capture>` sends
  those actual build messages through the complete unchanged production
  OneKeyLog Swift/Kotlin formatting and sanitization code. Only external types
  are stubbed. A temporary same-file Swift test entry and Kotlin reflection
  access private formatting without logger configuration or file IO. The full
  grouped digest must survive and restore the canonical compiled value; raw
  canonical IDs, unrelated 64-hex keys, 12-word text and Bearer/token canaries
  must remain redacted independently and on the same line as a grouped ID.
  This does not prove device file output, deduplication or rate limiting.
- `node tests/source-id.cjs` executes both generators and the real podspec phase
  using declaration adapters, compiles its generated version symbol, checks
  directory/environment/ignored-file independence, and verifies a source edit
  changes the identity through incremental CMake rebuilding. Package archive
  validation must also confirm the installed package can generate the same ID.
  This does not prove a complete CocoaPods/Xcode application build or a binary
  hash; external toolchains and dependency builds are not identified by the ID.

Physical iOS/Android card runs must confirm entry-to-callback timelines,
PIN-to-NFC handoff, first backup failure, cancellation, and successful backup.
Source inspection/builds do not prove those device outcomes or identify the
underlying failure cause.
