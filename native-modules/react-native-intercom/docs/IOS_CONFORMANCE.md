# iOS source recovery conformance

Status: **Recovery in progress. Full SDK NOT RESTORED.**

The matrix below is the current summary. Later milestone sections preserve historical evidence and may describe dependencies that were missing at that earlier snapshot. Full source compilation, strict linking, normal Pod consumption and runtime acceptance are separate gates.

## Foundation model closure contract

Baseline: official Intercom 19.9.1 device arm64 public headers, Objective-C
metadata and disassembly. This closure consists of `IntercomContent`,
`ICMCompany`, `ICMUserAttributes`, five Help Center model classes, the Help Center
error domain/enum, and their custom-data validation helper. It has no networking,
UI, identity storage, React Native bridge or vendor binary dependency.

Preserve constructor/property ownership, validation, omitted versus explicit-null
attributes, numeric registration keys, equality/hash and descriptions as observed
in the original. In particular, constructors can retain mutable inputs despite
copy property declarations; custom null conversion compares sentinel pointers;
rejected setters retain the prior value. No additional hardening is applied.

Host Foundation tests prove the actual recovered Objective-C behavior. iOS
device and simulator source compilation prove target compatibility only. Neither
proves full SDK behavior or equivalence to a running original SDK. Independent
original-process differential execution is pending unless explicitly recorded.

## Recovery ledger

| Area | Source implementation | Compile evidence | Behavior evidence | Original comparison |
| --- | --- | --- | --- | --- |
| Foundation models | Implemented source closure | Host link; iOS 15 arm64; simulator arm64/x86_64 | 50 host assertions; same 50 fixtures passed separately against original and recovered code on iOS 27.0 arm64 simulator | BEHAVIOR_COMPARED for these fixtures/target only |
| UserIdentity/Keychain/token data/persistence | Implemented source closure | Host Swift/ObjC link; iOS 15 device and both simulator architectures compile | 76 injected host assertions; 15 identity differential groups plus helper groups matched | BEHAVIOR_COMPARED for these fixtures on iOS 27.0 arm64 simulator; real Keychain persistence untested; directory migration original differential pending |
| JWT expiry and ErrorProcessor | Implemented source helpers | Host Swift; iOS 15 device and both simulator architectures compile | 21 expiry and 21 error assertions; helper differential groups matched | BEHAVIOR_COMPARED for finite fixtures on iOS 27.0 arm64 simulator |
| AppIdentity/IdentityStore | Genuine source restored with Config/Store/Bridge/Engine chain | Three iOS 15 source targets | Product initialization/login/session runtime pending | Earlier finite identity comparisons remain bounded; current whole source strictly links, initialized runtime integration remains unverified |
| Oreo Engine | Source method inventory restored; current aggregate strictly linked | ObjC source objects for three iOS 15 targets; host slices linked with explicit diagnostic dependencies | 23 request assertions; 28 transport/orchestration assertions around 14 intercepted tasks | Finite request/transport/orchestration and 18 routing fixtures compared on iOS 27.0 arm64 simulator; initialization product integration pending; recorded ObjC reflection fields matched |
| JWT refresher | Source restored; current aggregate strictly linked | Swift 5 warnings-as-errors for three iOS 15 targets; injected host core linked | 30 injected host assertions | 40 default-init fixtures compared on iOS 27.0 arm64 simulator; real Store/Bridge source now exists, integrated runtime pending |
| Network response models / operation helpers | Source restored with genuine fingerprint dependency; current aggregate strictly linked | Three iOS 15 targets; diagnostic host fixtures | 25 model assertions; 14 operation/replay assertions | Same finite fixtures compared on iOS 27.0 arm64 simulator |
| HTTPClient subsets | Source request/mapping/error helpers; current aggregate strictly linked | Three iOS 15 targets; host diagnostic links | Failure16; Help Center16; Sheets22; auth/multipart17 | Independent finite failure16/routing18/Sheet23/auth22 comparisons; HTTPBridge/realtime source now exists, remaining endpoint/parser/UI closures tracked separately |
| Environment / DeviceData | Genuine Environment and DeviceData source restored; current aggregate strictly linked | Three iOS 15 targets; Foundation helpers host linked | Environment37; device helper28; UIKit capture/serializer awaiting runtime | Environment38 original comparison; full DeviceData pending |
| Messenger/Help Center/cards/sheets/media/push | True chat, Survey, Home, Help Center, cards/sheets, notifications and Carousel default graphs have source; Ticket Create default graph now has source | Current frozen whole290ObjC/395Swift compiled and strictly linked on3targets with zero undefined SDK classes and zero input drift. Home/HC16class127own and Carousel15class343own plus8HTTP/category entries separately match on3targets | UIKit/WebKit/full SDK runtime unverified | Finite static reviews and object metadata only; source presence is not UI/privateABI/consumer acceptance |
| RN transport / normal Pod | Original pinned RCT/Turbo/EventEmitter source, real codegen and local SDK Pod | Normal static-library and dynamic-framework Host+Tests build on both simulator architectures; public Swift/ObjC API compiles; selected private ICMConfig rejects | Actual isolated Host launch verifies63 original resource hashes per mode, SDK asset isolation/getter/nil and RN class loading | Bounded normal-consumer PASS; XCTest built but not run, actual JS/session/UI/backend behavior and full private ABI remain unverified |

Current lexical inventory `docs/IOS_FUNCTIONAL_GAPS.json`:564 of595 original runtime names explicitly match bodies,17 functional Swift bodies have unpinned identities,14 names have no lexical body match. These include compiler/nested names; this is not14 proven missing features. Presence is not all-method closure. Actual class-method scans find zero missing bodies across the tracked HTTPBridge/HTTPClient/Presentation/Root/Utils inventories. Other class/instance/private-ABI completeness is tracked separately. Historical milestone counts below describe frozen inputs.

The complete native SDK must not be represented as available from this closure.

## Reproduction and evidence

Run `python3 scripts/ios-verify-models.py` from this package. It compiles only
`ios/Models/*.m` and the synthetic fixture, links Foundation on the host, runs 50
behavior assertions, and independently compiles all model sources for three iOS
targets. `node_modules/.cache/ios-source-models/receipt.json` records every command,
source SHA-256, target and assertion. Expected invalid-input logs are preserved
separately. No original framework appears in any compile or link argument.

Cases include six real content subclasses and overridden getters, copy versus
retain ownership, rejected setters preserving values, fractional timestamps,
null-pointer sentinels, numeric registration keys, custom-attribute shape checks,
and Help Center equality/hash/description behavior. The original unsubscribe
setter only changes its serialization NSNumber, leaving the BOOL getter unchanged;
the independent original runtime test confirmed that behavior. These are model
tests, not live Help Center API or UI tests.

Independent evidence from the Astra acceptance agent is retained outside the
package at
`/Volumes/T7Shield/Project/intercom-native-audit-20261104/independent-review/ios-model-differential/`.
`commands.json`, original/recovered build logs, both run stdout/stderr files, and
`run-receipt.json` record separate processes: the original target alone links
official Intercom; the recovered target compiles the five model `.m` files. Both
exit 0 on the same 50 assertions using the worktree's isolated external simulator
data set. This proves those fixtures on iOS 27.0 arm64 simulator, not every input,
minimum-iOS runtime acceptance or complete SDK behavior.

The affected behavior remains within total SPEC sections 3, 5, 6, 7 and 9. The
total contract does not need expansion. Native Foundation models are process
objects; no JS runtime or heap is involved in this closure. The full SDK's shared
native resource and split JS runtime integration remain unrestored.

## Identity closure contract under recovery

Recover `ICMUserIdentity` dictionary/KVC registration and state transitions,
secure coding, migration of `hmac` and `intercom_user_jwt` between archive and
Keychain, and the original Keychain service/query/status behavior. A failed
Keychain write retains the sensitive archive entry; absent/empty/non-string
values delete the Keychain entry and are removed from the archive regardless of
delete status. Nonempty Keychain values override archived values when loading.
Do not silently change these original persistence semantics. These classes are
not integrated into a working login/session flow yet. Tests use synthetic data
and isolated substitutes for persistence failure injection, never user secrets.

`ICMUtils` logging-state/directory methods are recovered as a dependency subset; other
utility methods remain explicitly unrestored. Compile evidence for that subset
does not make the entire utility class restored.

Run `python3 scripts/ios-verify-identity.py` for this closure. Its 76 host
assertions exercise real recovered identity state, real NSSecureCoding archive
roundtrip and Swift Keychain query/status handling. Test executable definitions
replace all four SecItem entry points. Secure-coding tests temporarily replace
the Swift class's `shared` getter with an isolated test store. No host system
Keychain API executes or default service item is read, written or deleted.
`node_modules/.cache/ios-source-identity/receipt.json` records exact source hashes,
commands and architecture compilation results. This is injected behavior
evidence, not operating-system persistence or complete authentication acceptance.

Original Swift Objective-C runtime class/protocol names are retained with `@objc`.
The Swift module's private mangled entry points are not claimed ABI-identical;
compatibility requires the remaining SDK sources to build together. The internal
Objective-C Keychain declaration has a source alias mapped to the original
runtime class, to link the real reconstructed Swift implementation.

Astra's independent identity comparison is retained at
`/Volumes/T7Shield/Project/intercom-native-audit-20261104/independent-review/ios-identity-differential/`.
Fifteen groups produce equal original/source JSON values, including secure coding
with successful/failed migration and a test-only shared Keychain replacement.
No real default-service persistence was exercised. Separate ABI inspection at
`independent-review/ios-abi-differential/` compares seventeen classes' own method
selectors, encodings, ivars, offsets, sizes and property metadata. After restoring
the content subclasses' direct protocol conformance, all seventeen classes match
these reflected fields (`differences.json` is empty and `receipt.json` is PASS).
This does not prove all private Swift mangled ABI/symbols or the entire SDK ABI.

The further helper comparison is retained at
`/Volumes/T7Shield/Project/intercom-native-audit-20261104/independent-review/ios-helpers-differential/`.
Nine groups match complete original/recovered JSON values in separate iOS 27.0
arm64 simulator processes. Coverage includes nineteen expiry inputs (300-second
grace on both sides, not a frozen exact-equality boundary), eleven NSError
mappings, twenty-seven error classification inputs, device token model/coding,
and real persistence proxy file I/O. Files are synthetic and stay in the
worktree-specific fixture directory. These checks do not authenticate JWTs or
exercise live HTTP, push delivery, real Keychain storage or the whole SDK.

## JWT expiry preflight under recovery

The original `JWTExpiry` helper parses only string values with exactly three
dot-separated components, converts URL-safe base64 in the middle component and
pads it, parses a JSON object, and accepts only a non-Boolean NSNumber `exp`.
Malformed or missing expiry returns nil. `isDefinitivelyExpired` returns true only
when expiry plus the original 300-second grace period is strictly before now.
This parser does not verify JWT signatures and is not a substitute for server
authentication. IdentityStore integration and refresh/retry orchestration remain
unrestored. Preserve these original boundaries and behavior.

## Error mapping under recovery

Recover all fifteen original `ErrorProcessor` Objective-C class entry points,
including fixed error domain/codes/messages and JWT-expired detection. The
detector uses an integer `kIntercomHTTPStatusCode` when present, otherwise the
NSError code, and additionally requires server code `intercom_user_jwt_expired`.
It does not add a domain check. These helpers do not send requests or implement
network retries by themselves.

## Device token data under recovery

Recover the native `ICMDeviceTokenData` model's retained string token, unsigned
integer environment, submitted BOOL, equality and secure coding with the original
three archive keys. This model does not submit tokens, enable notifications or
implement push delivery.

## Persistence proxy under recovery

Recover `ICMPersistenceProxy` real secure archiving and atomic NSData file writes,
class-restricted unarchiving, nil/empty/missing-file handling and original
exception/error-to-failure behavior. It does not create missing parent directories
or repair corrupted archives. Tests write synthetic files only under this
worktree's cache; original SDK identity/cache paths are not accessed.

## App identity source under recovery

`ICMAppIdentity` retains appId/apiKey, lazily creates an ICMOreoEngine under its
instance monitor, and replaces the engine on changed credentials after ignoring
the old engine's pending requests. It archives appId/apiKey only. Its Engine source now exists, but the complete native identity/config/Bridge
closure remains missing; this source is explicitly SOURCE_RESTORED_UNLINKED. Object compilation or fixture-only engine substitutes
do not satisfy product engine/network acceptance. No engine implementation stub
is supplied in product source.

## Identity store source under recovery

`ICMIdentityStore` source preserves the primary/backup archive paths and repair
ordering, original singleton/lazy-state ownership, registration/reset/previous
identity lifecycle, delayed device-token deletion under a UIKit background task,
secure-option changes, fingerprint-scoped stale-JWT replacement and archive
compatibility decoding. This is SOURCE_RESTORED_UNLINKED: its real Config/HTTPBridge and complete Engine/Bridge integration remain missing.
`python3 scripts/ios-verify-session-source.py` compiles the source to objects for
all three iOS 15 targets and records missing dependencies/undefined symbols. No linked
session behavior or real network acceptance is claimed. Product dependency
headers are declarations only and supply no fake implementations.

## Native documents/cache directory dependency under recovery

Recover the original Library/PrivateDocuments/io.intercom.ios documents path,
legacy Documents/io.intercom.ios path, caches path and copy-then-remove migration
semantics. If both directories exist, the original leaves legacy data untouched.
When a migration copy fails, it removes the destination and returns the legacy
path. The document accessor's original synchronization and directory creation
are retained. Tests redirect NSSearchPath calls in the test executable only;
product source retains original paths and behavior.

## Oreo request-construction subset under recovery

Recover original JWT parameter extraction/replacement, three endpoint retry
eligibility checks, unbounded exp2 backoff, Basic authorization formatting,
idempotency-key generation and JSON request creation. Rejected JSON calls the
original two-argument failure callback with nil response and the serialization
error. Preserve URL resolution via relativeToURL and the original random string
format (minimum width two hexadecimal digits, despite the method names).
Engine source now includes the initialization/session/parser/operation/replay
methods described below, but the product dependency closure remains unlinked.
This request-construction fixture alone does not send a network request.

`python3 scripts/ios-verify-oreo-request.py` compiles only this subset and its
fixture. Twenty-three assertions prove real request URL/header/JSON construction,
exact retry path equality, exp2 behavior and JWT parameter operations. The
fixture preserves the original uncaught NSJSONSerialization exception for
nonfinite values; the NSError-return path is exercised by a temporary test-only
serializer replacement. Product source does not add an exception catch.

## Base URL and native logger under recovery

Recover original app-id sanitization/suffix/truncation, Info.plist region and
custom bare-hostname selection, including original fallback logging and malformed
region behavior. No additional host allowlist or input constraints are added.
Recover the six ICMLogger methods with original OSLog subsystem/category,
debug gating, public formatting and the release baseline's intentionally empty
private-log method. Empty private logging is an observed original method, not a
substitute for an unrestored feature. Tests provide synthetic Info.plist values
and do not resolve or contact resulting hosts.

## Multipart task builder under recovery

Recover the three original `ICMMultipartTaskBuilder` Objective-C class methods
and NSObject initialization. Requests use POST, the original 60-second timeout,
UUID boundary, unsorted Swift dictionary iteration, UTF-8 parameter/header text
and exact CRLF framing around unchanged file bytes. Existing headers are applied
first, then Content-Type and byte-based Content-Length replace those fields.
Original field names, filenames and MIME types are interpolated without added
escaping or validation. Upload helpers use the caller's URLSession, default the
field to `file` when omitted, pass body data separately, and return a suspended
upload task without resuming it. This helper does not implement upload response
handling, retries, identity or attachment UI. Tests intercept task creation or
inspect requests without allowing network traffic.

The Engine's multipart request method additionally maps parameter values through
Objective-C `description`, resolves its URL relative to baseURL, invokes the real
Swift builder, and appends its original idempotency header. The mixed ObjC/Swift
host suite links both recovered implementations and inspects the actual body.

## Network response models under recovery

Recover installation fingerprint capture/coding/equality, shutdown period and
fingerprint-based send eligibility, real shutdown archive persistence and original
error response parsing. Singular `error` dictionaries take precedence over
`errors` arrays. Arrays join string type/message fields with ` - `, take code
only from the first dictionary and use the last numeric shutdown period. A type
containing `messenger_shutdown_response` and a nonzero period creates shutdown
state. Original NSError wrapping preserves explicit NSNull sentinels. Fingerprint
initialization depends on the actual identity singleton; until that session
closure links, these source units remain SOURCE_RESTORED_UNLINKED. Fixture-only
identity injection can exercise model behavior but does not satisfy product
session/network acceptance. Production directory/service semantics are unchanged.

Independent `ios-network-helpers-differential` evidence compares eleven JSON
groups against the original 19.9.1 in separate iOS 27.0 arm64 simulator processes.
These include Oreo static/request helpers, BaseURL construction and two multipart
requests after UUID boundary normalization. Both exit successfully with equal
outputs. Upload-task entries, multi-parameter iteration order, logger output,
directory migration and HTTP transport are outside that comparison.

`python3 scripts/ios-verify-network-models.py` passes 25 host assertions and
compiles the three response/fingerprint/shutdown models for all three iOS 15
targets. This fixture supplies only a test identity singleton and redirects
NSSearchPath to this worktree before exercising real archive files. Model source
has no test identity implementation; the product session remains unlinked.

## Oreo transport source under recovery

Recover the real NSURLSession data-task entry and completion parser, with the
original preflight shutdown gate, HTTP 2xx classification, optional JSON parsing,
transport-over-JSON-over-HTTP error precedence, server error code injection and
shutdown persistence. Tasks are returned suspended and are resumed by their
caller. Callback response objects stay unchanged. Errors suppress parsed JSON in
the completion. The original weak engine reference is retained; no new callback
queue or cancellation policy is introduced. This transport slice remains part of
the incomplete Engine/session class. Tests inject only a diagnostic URLSession
whose URLProtocol handles every URL and rejects unknown inputs; product source
uses its actual session property without that injection.

## URL session operation and replay context under recovery

Recover ICMURLSessionTaskOperation's asynchronous NSOperation state, retained task,
manual KVO notification ordering, resume-on-start and cancel forwarding. A
cancelled start sets finished only; finish does nothing unless executing;
taskCompleted finishes only on a transition to true. Preserve the original
pre-start completion behavior rather than adding a new completion policy.
ICMOreoUserJwtReplay copies its JWT, fingerprint array and request-builder block;
array copying remains shallow. These helpers do not implement refresh/retry
orchestration by themselves.

Independent `ios-network-models-differential` runs the same 25 model fixtures
against original and recovered implementations in separate iOS 27.0 arm64
simulator processes. Both exit zero with matching JSON. Original currentIdentity
and document-directory entry points are temporarily replaced in its diagnostic
process only. This shared-fixture comparison does not establish a complete
identity/session closure.

`python3 scripts/ios-verify-oreo-transport.py` passes 28 assertions around fourteen
actual resumed Foundation data tasks, all intercepted by its synthetic protocol,
plus synchronous shutdown rejection. It compiles restored ObjC units for all
three iOS 15 targets. This is actual transport/parser slice evidence with
fixture-only session and identity injection; original dynamic transport comparison is recorded below; real Engine
initialization integration remains pending.

Independent `ios-transport-differential` adapts the same 21 assertions to the
original class in a separate iOS 27.0 arm64 simulator process, using only
diagnostic identity/session/directory injection. Both processes exit zero with
matching JSON and ten protocol-intercepted tasks each. This excludes real
initialization, queue/retry/replay orchestration, JWT refresh and authentication.

`python3 scripts/ios-verify-operations.py` passes fourteen host assertions for
operation/replay helpers, including actual KVO events and NSOperationQueue drain,
and compiles both units for all three iOS 15 targets. Synthetic task objects
record resume/cancel without network traffic. Original dynamic comparison of these helpers is recorded below.

The Engine ignore method increments the original unsigned generation counter and
marks only queued ICMURLSessionTaskOperation objects as ignoreResponse. It does
not cancel tasks or unrelated operations. Generation overflow is preserved. The
queue's real lifecycle is source-recovered below; full product integration
remains unverified.

## Network retry classification under recovery

ICMErrorStatus uses error code alone with no domain check: cancellation -999,
missing app 1000 and missing user 1001 are non-retryable; retryable codes are
-1001 and -1003 through -1010. Other codes, including HTTP 4xx/5xx, are not
retryable. Nil error is treated through Objective-C nil messaging (code zero).
This classification helper does not execute retries or introduce new retry
policies.

## Oreo request-operation orchestration source under recovery

Recover task-backed operation creation, original success/failure callback task
arguments, ignore-response error behavior, identity-dependent queue concurrency,
retry operation creation before delayed enqueue, and retry limits/error-code
classification. Initial calls enqueue only when retries equals zero. Eligible
retry endpoints are still the original three exact paths; retry attempts one
through three use exponential delays on the main dispatch queue. HTTP response
status, when present, overrides transport code for retry classification.

Replay captures ignore generation and weak engine, requests refresh from the
original refresher dependency, then either rebuilds with the returned JWT and
enqueues a zero-retry operation without replay context, calls otherwise, or calls
ignored when the engine disappeared or generation changed. Preserve the original
callback ordering and unguarded mandatory otherwise/ignored blocks. Refresher and Engine initialization source now exist, but complete
identity/config/Bridge dependencies remain unlinked. Test-only refresher
substitutes do not establish authentication or product session completion.

Independent `ios-operation-differential` adapts the same fourteen helper
assertions against original and recovered implementations in separate iOS 27.0
arm64 simulator processes. Both exit zero and produce equal full JSON including
the actual KVO event array. It does not cover Engine retry/provider integration.

## Engine initialization source and supported-language dependency under recovery

Recover original default NSURLSession configuration with self as delegate and
nil delegate queue, retained base URL, captured installation fingerprint, fresh
mutable headers, shared UserJwtRefresher and operation queue initialization.
Credentials produce Basic authorization only when both strings have length.
Headers preserve original JSON MIME fields, SDK agent, optional trimmed messenger
environment, preferred languages joined by comma-space, the exact ordered 47
supported locale identifiers and formatted app version. No new session policy or
resource limits are added. Only TranslationManager.supportedLocales is recovered;
translation loading/rendering remains absent. Engine initialization source still cannot form a linked product until the
complete identity/config/Bridge closure is restored.

## Engine request entries under recovery

POST, DELETE and multipart build the original request and retain a weak-engine
JWT replacement builder. sendRequest attaches replay only when the current JWT
fingerprint exists. A requested preflight refresh returns the original suspended
`intercom-placeholder://` shared-session task; completion first checks engine and
ignore generation, then placeholder cancellation, then sends a rebuilt request
and refreshed replay context, or the original request/context if rebuilding fails.
It does not transfer placeholder cancellation onto an already sent real task.
The original callback nil/task arguments and permissive builder behavior remain.
Wrapper headers map the original four integer cases (Cordova, React Native,
Flutter, Unity); unknown cases format a nil prefix and empty versions leave the
existing header untouched. These are source-recovered entries until linked with
the real JWT refresher and complete identity/session implementation.

## User JWT refresher state machine under recovery

Recover serialized provider/pending/cooldown state and original default iOS
behavior. Nil current JWT returns nil without starting a provider; empty JWT can enter
provider flow. Refresh accepts a newer current JWT without starting a provider;
otherwise same-fingerprint pending calls merge, differing fingerprints cancel old
callbacks, timeout occurs after ten seconds, and cooldown is thirty seconds.
Timeout cooldown is pending.deadline+30; a nil answer uses uptime()+30. Provider
removal does not cancel pending work. Inactive application completion does not
set cooldown. External userJwtWasSet clears cooldown before completing pending.

Only valid answers different from the stale JWT may enter the identity store's
original compare-and-set operation. A timed-out but unhandled provider may still
apply once without waking expired callbacks. Completion rechecks identity and
may return an already newer JWT after CAS failure. Ordering is CAS, callbacks in
array order on the current completion thread, then onApplied only after success.
Preflight expiry uses remaining time <=60 seconds and requires a provider; it
is separate from JWTExpiry's 300-second definitive-expiry helper and is not
signature/authentication verification.

Default dependencies are the actual identity singleton, UIKit application state,
asynchronous main dispatcher, process uptime, Date, default global queue timer
and ICMBridge.refreshedUserJwtWasApplied. Bridge and the complete session closure
remain missing; no fake implementation fills them. Tests inject synthetic state,
time and storage explicitly. The original ApplicationStateProviding descriptor
proves one getter, but WMO deleted its witness and erased its source member name;
its recovered getter spelling is not a verified private Swift ABI contract.

Independent `ios-request-routing-differential` adapts eighteen routing fixtures
against original and recovered Engine entries in separate iOS 27.0 arm64
simulator processes. Outputs match. The fixture supplies a request sink and
synthetic refresher/placeholder; real placeholder factory, provider state machine
and authentication are excluded.

`python3 scripts/ios-verify-refresher.py` passes thirty host assertions for the
actual recovered state-machine core with explicit identity/clock/application/
dispatch/timer injection. Its Swift 5 warnings-as-errors object builds also cover
the actual iOS default initializer for arm64 device and arm64/x86_64 simulator at
iOS 15. Default runtime comparison is recorded below; real Bridge/session remain
unlinked. ApplicationStateProviding source spelling remains a private ABI gap.

Independent `ios-refresher-differential` adapts forty default-initializer fixtures
against original and recovered implementations in separate iOS 27.0 arm64
simulator processes. Full JSON matches, including actual ten-second timeout and
callback thread/order observations. Only the diagnostic identity/Bridge/state
dependencies are replaced; the source state machine, default dispatch/timers and
JWT parser are actual recovered code. The original diagnostic also intercepts
Bridge +initialize to prevent unrelated SDK startup side effects. This does not
verify the real Bridge, production identity store integration or full private
Swift injection ABI.

## Nexus configuration model under recovery

Recover the original four retained fields, zero-number/empty-array defaults,
dictionary KVC parser, snake-case numeric timeout mappings and endpoint transform.
Endpoint strings append `?X-Nexus-Version=ios.5.5.0` literally, even with an existing
query. Only a case-sensitive `http` occurrence at offset zero is replaced with
`ws`; NSURL construction and array insertion preserve original exceptions.
Nonstring endpoint items are skipped; wrong-type endpoint arrays preserve the
previous value. Unknown fields are ignored except the two original numeric
snake-case mappings. Direct setters retain inputs without validation/copy.
Equality performs original field selectors without a class guard, accepts
nil-versus-nil fields and adds no hash override. This does not implement realtime
connection, WebSocket transport or background scheduling.

## HTTPClient failure/identity-admission subset under recovery

Recover four original class methods for network failure reporting, HTTP error
conversion, explicit SDK failure and identity admission. Network failure uses a
present `kIntercomHTTPStatusCode` object's integerValue without a class check;
zero/missing status delivers the original NSError instance. A nonzero status
uses the original ErrorProcessor HTTP factory and copies only the original
server-error-code field into that generated error. Nil/NSNull/type behavior is
not hardened. Logs retain original NSLog formatting. Main-thread failures are
synchronous; background failures dispatch asynchronously to main.

Admission calls the real identity singleton's app identityExists, then user
identityExists, and reports original missing-app/user messages/codes 1000/1001.
It must never create successful identity objects or skip either check. This is a
partial HTTPClient class, with test-only identity state fixtures; its endpoints,
BaseResponse side effects, Config and full product session remain unrestored.

Independent `ios-engine-abi-differential` compares seven additional classes'
recorded Objective-C runtime fields with the original on iOS 27.0 arm64
simulator. Method selectors/type encodings, property attributes, ivar names/types/
offsets and instance sizes all match. This includes Engine 36 instance methods
(including destructor), seven class methods, eight ivars/72 bytes/twelve properties,
and Refresher nine instance methods/one class method/twelve ivars/216 bytes/two
properties. Dependency fixture classes are excluded and unused. This remains
Objective-C reflection evidence, not full private Swift ABI or product closure.

Actual Security persistence comparison remains NOT_RUN in
`ios-keychain-security-differential`: diagnostic hosts compiled and installed in
the worktree-specific simulator but launchd policy 163 prevented execution. No
Security assertions executed; the host/default Keychain namespace was untouched.
Injected query/status and identity archive comparisons remain separate evidence.


Independent `ios-http-failures-differential` and `ios-nexus-differential` pass the
same sixteen and thirteen fixtures respectively in separate original/source
iOS 27.0 arm64 simulator processes. Full JSON matches. HTTP identity conditions
remain diagnostic substitutes; Nexus is the actual Foundation model. Neither
comparison creates a URLSession task or proves whole HTTP/realtime integration.

## HTTPClient Help Center request mapping under recovery

Recover the three original collections, individual collection and search class
methods. Admission runs first. Each request mutable-copies the current user's
dictionary representation, overwrites device_identifier, then obtains the
current app engine separately and POSTs the original relative path. Individual
collection IDs use literal stringWithFormat interpolation without URL encoding.
Search overwrites phrase and article_source in that order; nil deletes those
keys. Success invokes its supplied block directly on the engine callback thread,
without a nil guard, response parsing or dispatch to main. Failure uses the
original fixed HTTPClient class and existing network error helper, including the
original action strings. No BaseResponse/Config side effects are introduced.

The genuine DeviceData.deviceIdentifier method reads standardUserDefaults once,
gets stringForKey `intercom-deviceIdentifier`, and only on nil generates an
NSUUID UUIDString and stores it under that key. Empty strings remain valid;
there is no cache, lock, synchronize or Keychain operation. Only this DeviceData
method is restored. Tests replace defaults/UUID in diagnostic processes and
never access the host's default preference namespace. The request mapping uses
test-only identity and engine sinks until the full product dependency closure
exists; it does not establish Help Center rendering or authenticated service use.


`python3 scripts/ios-verify-helpcenter-requests.py` passes sixteen host assertions
and object-compiles all three source units for iOS 15 device arm64 and simulator
arm64/x86_64. Tests observe original singleton-read order, payload copy/overwrite,
literal paths, nil fields/dictionary, direct callback identity/thread and admission
before device access. Device identifier generation/read/empty-string retention
uses test-only in-memory defaults and fixed UUID. No network task is created.
Original dynamic comparison is pending; full Help Center product integration and
all other DeviceData methods remain absent.


Independent `ios-helpcenter-routing-differential` passes eighteen separately
designed original/source fixtures in separate iOS 27.0 arm64 processes, with full
JSON equal. It covers the routing/device boundaries above plus failure background
to-main delivery and HTTP conversion. Identity, engine, defaults and UUID remain
explicit test-only dependencies. Zero network tasks are created.

## Sheet model and HTTP request subset under recovery

Recover ICMSafeNetworkModel's two original empty KVC nil/undefined handlers
(the original instructions are literal ret, not missing implementation). ICMSheet
inherits that original class and retains one requestBody NSDictionary field.
Its factory always allocates ICMSheet, even when invoked on a subclass; dictionary
parsing accepts only sheet_request_body dictionaries, ignores other keys, and
keeps prior values for wrong types. Direct setters remain unvalidated/retaining.
Equality requires exact runtime class for isEqual:, but isEqualToSheet: directly
uses requestBody selectors, compares pointer then dictionary equality. Hash is
requestBody.hash. No custom defaults are added.

Sheet fetch checks admission, copies current identity dictionary, overwrites
device_identifier then adds caller parameters (which may overwrite those fields),
and POSTs sheets/open. Only a nonnil success callback causes ICMSheet parsing;
parsing occurs before dispatch, with success synchronous on main or asynchronous
to main from background. Sheet submit similarly copies identity/device then sets
sheet_values and uri (nil deletes keys), POSTs sheets/submit and invokes a guarded
no-argument success on main with the same dispatch rule. Both use the fixed
original HTTPClient network error helper and original action strings. This does
not restore sheet UI, remote bridge actions or authenticated product integration.


`python3 scripts/ios-verify-sheets.py` passes twenty-two host assertions and
object-compiles five recovered source units for iOS 15 device arm64 and simulator
arm64/x86_64. Coverage includes exact-class versus typed equality, factory class,
retained mutable dictionary aliases, original KVC handlers, parameter precedence,
nil fields/callbacks, real Sheet parsing, synchronous main and asynchronous main
success, and identity denial. Original differential is pending. HTTP methods use
diagnostic identity/engine/defaults; model code is actual source and no network
or sheet UI/bridge execution occurs.

## Auth-token HTTP mapping and multipart identity fields under recovery

Recover setAuthTokens as the original request mapping, without claiming token
validation or an initialized authenticated session: admission first, mutable-copy
user dictionary, assign auth_tokens (nil removes), POST user_auth_tokens. Success
has no nil guard, runs synchronously on main or dispatches asynchronously to main
from background. Failure first unconditionally generates an HTTP error from the
original status object's integerValue (absent means zero), then passes that error
to failWithNetworkError using the original action string. This loses original
transport code even when status is missing, and is preserved.

The original multipart identity builder allocates a mutable dictionary; includes
nonempty email and userId, considers nonempty anonymousId only when both email
and userId pointers are nil (empty strings suppress anonymous fallback), then adds
nonnull device identifier. It reads dictionaryRepresentation, stringifies nonnil
app_id by description, and retains nonnil hmac/intercom_user_jwt except NSNull
instances. It does not add identity admission, validation or normalize values.
All fixture values are synthetic; no token request reaches a remote service.


Independent `ios-sheet-routing-differential` passes twenty-three independently designed
Sheet model/request fixtures in separate original/source iOS 27.0 arm64
processes, with complete JSON equal and diagnostic identity/engine/defaults only.
`ios-sheet-nexus-abi` also records equal Objective-C reflection fields for
ICMSafeNetworkModel, ICMSheet and IntercomNexusConfiguration. Sheet UI/bridge,
product session and realtime remain excluded.

`python3 scripts/ios-verify-identity-requests.py` passes seventeen host assertions
and source object builds for the three iOS 15 targets. It exercises multipart
nil/empty identifier precedence, secure NSNull/nonstring values, app ID description,
request copy/token assignment, original HTTP-zero error conversion, admission and
main callback dispatch. Only synthetic tokens are used; identity/engine/defaults
are fixture dependencies and no network task or authentication occurs. Original
differential is pending.

## Application environment checker under recovery

Recover the complete nine-method Foundation class. appEnvironment caches the
first findCurrentAppEnvironment result once for the class implementation, using
the first receiving class for dispatch_once's body. Development classification
is exactly unsigned values 4 or 5; token environment strings are development for
those and production for all others. App environment strings use the original
six-entry table (appstore, testflight, enterprise, adhoc, development, simulator),
with unknown outside 0..5. Detection prioritizes simulator, then presence of an
embedded.mobileprovision resource, otherwise sandbox receipt boolean (0 or 1).

Provision detection reads the resource as NSData, appends each signed byte with
%c to an NSMutableString, removes whitespace/newline characters, then checks
literal substring matches in order: get-task-allow true ->4, ProvisionsAllDevices
true ->2, ProvisionedDevices array ->3, otherwise99. No plist/CMS/signature parser,
format normalization or validation is added. Receipt detection checks selector
availability and lastPathComponent exactly sandboxReceipt. Tests supply an
isolated fixture bundle and files; they do not inspect a real app's provisioning.


Independent `ios-auth-multipart-differential` passes twenty-two independently
designed fixtures with full original/source JSON equal on iOS 27.0 arm64. It adds
getter-count/short-circuit observations and confirms no device injection into
auth-token requests. Synthetic Store/Engine/defaults only; zero actual service
requests or server authentication. This remains a finite mapping comparison.

## DeviceData implementation dependencies under recovery

Expand DeviceData to its original 21 readonly properties and exact ivar layout,
initializer, dictionary serializer, machine/SDK identifiers and stored interface
style. Initializer retains original input results directly despite readonly copy
property metadata. It captures actual UIKit device/application/accessibility,
locale, bundle utility, identity device-token and environment values. DeviceData
serialization preserves original optional fields, repeated getters, push-enabled
gate, iPad-prefix multiple-window field, and string YES/NO flags. No new device
tracking fields or validation are introduced. Product linkage must still fail for
unrestored PushNotificationManager/identity dependencies; tests may inject those
only as diagnostic providers.

The supporting Utils subset reads CFBundleName/CFBundleIdentifier without type
validation, recognizes push auto-integration NSNumber bool or exact string NO,
and detects notification-service extension by original nested dictionary lookup.
SDK wrapper discovery resolves all original four classes first, then checks RN
protocol, Cordova superclass, Flutter protocol, Unity presence in that priority,
with native value4. No bridge is synthesized when those classes are absent.


The environment comparison passes thirty-eight shared original/source fixtures on
iOS 27.0 arm64, including independent observation that original simulator
isRunningOnSimulator returns YES. `scripts/ios-verify-environment.py` passes
thirty-seven host assertions and three iOS 15 object builds. Device builds use
the original false simulator branch; minimum-iOS runtime remains untested.

`python3 scripts/ios-verify-device-helpers.py` passes twenty-eight Foundation host
assertions plus complete iOS DeviceData object builds for three iOS 15 targets.
Host coverage is explicitly only actual machine/style/SDK and Utils bundle/wrapper
helpers; UIKit initializer and serializer are excluded from macOS compilation and
remain iOS-runtime-pending. Recovered iOS source retains every original method,
readonly copy property metadata and direct retained initializer assignments.
PushNotificationManager and full IdentityStore closure are declarations/real
unlinked sources, never successful replacements. Existing deviceIdentifier
comparison uses its earlier preserved source snapshot and cannot certify this
expanded class. Earlier Help Center/Sheet/auth host suites now link actual
Utils/Persistence dependencies; no original framework is used.

## Push enabled-status dependency under recovery

Restore the original PushNotificationStatus Swift class, its mutable lazy shared
instance and the two PushNotificationManager status methods only. The class owns
a serial queue labelled com.intercom.push-notification-status, an initially false
enabled bit and an authorization status provider. Initialization requests status
before registering selector refresh for UIApplicationDidBecomeActiveNotification;
deinit removes self from the default notification center. Getter reads and updates
use queue.sync. Only authorization raw values 2 and 3 enable push (ephemeral raw4
remains false). Provider replies weakly reference self; completion executes even
when self has disappeared, after any sync update, directly on provider callback
thread. No coalescing, main dispatch or stale-reply guard is added. Repeated
provider answers therefore update and complete repeatedly as in the original.

Default provider calls actual safeGetAuthorizationStatusWithCompletion:.
That method immediately returns raw0 to its mandatory completion when XCTestCase
is present; otherwise it reads current UNUserNotificationCenter settings and
passes authorizationStatus directly on that callback's thread, without requesting
permissions. isPushNotificationsEnabled delegates to the actual shared status.
Other PushNotificationManager methods remain absent. Host injection tests exclude
iOS default initialization/observer registration; iOS runtime comparison and
source closure are recorded independently. No remote notification or user data is
required for diagnostic fixtures.


Independent `ios-device-helpers-differential` passes the same twenty-eight helper
fixtures in separate original/source iOS 27.0 arm64 processes. It also matches
DeviceData/AppEnvironmentChecker's recorded Objective-C layout/method/property
fields. class_createInstance avoids DeviceData UIKit init/serializer; diagnostic
Store/Push classes abort if called. This evidence does not verify those two iOS
methods, real system data capture, complete Utils or SDK product integration.

`python3 scripts/ios-verify-push-status.py` passes eighteen injected-core host
assertions and compiles the genuine default initializer/observer/PushManager
source for three iOS 15 targets. It covers raw status classification, repeated
and out-of-order replies, sync update before callback, original thread and weak
state lifetime. Host tests exclude UIKit initialization and no OS notification
query occurs. Original default runtime/ObjC reflection comparison is pending.

## BaseResponse response side effects under recovery

Restore the complete original BaseResponse class, retaining its SafeNetworkModel
superclass, seven fields and 64-byte layout. Factory always allocates the concrete
base response, immediately subscripts input config and calls Config's real shared
update entry, parses attributes next, then applies NSNumber-only has_conversations
to the current user's identity. Factory input is not prevalidated before those
subscripts. Config dictionary handling optionally parses real_time_config as
actual Nexus configuration, replaces its backgroundConnectionTimeout with shared
Config.newSessionThreshold, then accepts NSNumber-only is_first_request.

The original unread_conversations/unread_tickets/user/carousel/survey keys accept
dictionaries only and invoke their actual model factories/initializer. Invalid
value types preserve prior fields. Unknown keys pass through super KVC, while
undefined unread snake-case names remap to their camel-case fields. Survey init
returning nil triggers original failSurvey only when both id and progress objects
are nonnil and progress.integerValue is nonzero; this uses the real HTTPBridge
entry with original empty success/failure blocks. No model or HTTP dependency is
replaced by a product stub to make parsing link.

Generic equality requires exact runtime class. Typed equality compares only unread
conversations, user and carousel using nil-or-isEqual checks, then first-request
bit; it deliberately ignores unread tickets, realtime configuration and survey,
and may equal nil when those compared fields/bit are empty. Hash is unsigned
31*unreadConversations.hash + user.hash. Direct retained setters remain unvalidated.
Missing model/config/bridge implementations keep this source product UNLINKED.


`python3 scripts/ios-verify-base-response.py` passes twenty-four host assertions
and source object builds for three iOS 15 targets. It verifies factory update /
parse / identity ordering, genuine Nexus parsing, original validation and KVC,
Survey failure sink conditions, typed/generic equality differences and hash
relation. Config/Store/Unread/Carousel/Survey/User/HTTPBridge are explicit test-only
dependency fixtures; they cannot certify those product implementations or close
the response path. Original comparison is pending. Config is the next required
implementation on the initialized-session path, including its archive/shared
state and original server-response side effects.

## Config response/persistence path under recovery

Restore all 37 original Config fields with their exact metadata ownership and
224-byte layout, concrete defaults/factory, KVC mapping, coding and shared/archive
state. A nonempty NSDictionary update builds a fresh concrete Config and invokes
saveIfNeeded. Only equality with current shared plus its persisted bit skips a
save. Save replaces shared first, archives synchronously, then always schedules
ConfigChanged on main; saveConfig sets persisted YES even when archive returns
false. Deletion removes archive, creates concrete defaults and posts ConfigChanged
synchronously under the receiving class lock. Shared first loads disk once using
the first receiver and falls back to concrete defaults. The original archive path
is Utils.intercomDocumentsDirectory/config.icm.

Defaults are inbound NO; audio/metrics/background YES; duplicate age300; rate
100/period60; new session20; batch/ping/soft-reset1; copied right alignment; genuine
new AttachmentSettings and ConversationStateSyncSettings; empty features and
unpersisted. Other fields retain zero/nil defaults. Coding uses original selector
keys, field order and allowed classes, contains-value compatibility checks, and
real custom setter image side effects. NSNull input is ignored by KVC. Feature
arrays become NSSet, modules dictionaries parse real ConfigModules and immediately
prefetch images. Original setter/mapping type checks and alias names are retained.

Equality preserves the original 36-field order and repeated getter conditions,
excluding persisted bit. Nullable objects compare double-nil or isEqual, except
launcherAlignment directly isEqualToString (double-nil false); doubles use exact
==. Hash is baseColorString.hash plus direct inboundMessages ivar byte. Image/UI
methods and nested model dependencies stay explicitly missing/unlinked until their
actual implementation is restored; they are not replaced with empty callbacks.

ConversationStateSyncSettings is a required genuine Config default/model. Restore
its three immutable fields, original default enabled YES and both intervals10000,
required dictionary casts enabled Bool/sync_interval Double/start_timeout Double,
NSSecureCoding with those snake-case keys and exact-double equality using dynamic
Self casting. Missing/wrong fields return nil; no interval validation or clamping
is added. Decoder unconditionally decodes three scalar keys, so absent fields
become false/zero instead of initializer defaults. It has no custom hash override.

AttachmentSettings is the other genuine Config default. Restore five immutable
booleans, upload byte limit and ordered extension strings. Default flags are true
except voice false, upload limit100MiB, extensions empty. Dictionary construction
requires all seven original keys and Swift Bool/Int/[String] casts; missing or
wrong data returns nil. Direct/dictionary construction preserves original camera
Info.plist presence checks and logs (missing camera disables camera; missing
microphone only logs photo-only info when camera enabled). Presence alone counts,
even empty/nonstring/NSNull values. Voice input OR the original defaults override
isVoiceTranscriptionEnabled requires nonnil microphone description; no permission
request occurs. Decoder reads archived camera bit directly, rechecks only voice,
and decodes extensions using NSCoder.decodeArrayOfObjects(ofClass:NSString.self)
with empty-array fallback. Equality casts dynamic Self and compares all fields in
original order; no extra hash override or upload/extension validation is added.


Independent `ios-base-response-differential` passes twenty-three independently
designed original/source fixture groups and `ios-base-response-abi` matches the
recorded Objective-C fields. Missing dependency fixtures do not establish a
linked response implementation. Independent `ios-config-differential` passes
twenty-eight shared fixtures with actual Config/Persistence/Utils/Safe code and
explicit three nested-model/four image diagnostics in its preserved snapshot.
Its state/archive/notification comparisons do not certify those dependencies.

Current `scripts/ios-verify-config.py` still passes twenty-eight assertions after
replacing Attachment/SyncSettings diagnostics with actual Swift implementations;
ConfigModules and image entries remain test sinks. Dedicated genuine model
suites pass fourteen SyncSettings and twenty-five AttachmentSettings host
assertions, with three iOS 15 target object builds. Independent ios-config-settings-differential passes the Attachment25 shared
and Sync11 independently designed groups plus seven coercion inputs. Camera/voice Info.plist/defaults are synthetic diagnostic providers only;
no media recording, real permissions or host default preference access occurs.

Config image/UI methods must retain original behavior. Launcher load checks URL
cache before requesting; its completion caches under the then-current launcher
URL (no stale-request guard) and posts original logo-loaded notification on that
callback thread. Custom image first checks resized key, otherwise URL cache;
UIImage values stretch to36x36 using original image.scale in legacy graphics
context, encode PNG then decode UIImage (including original scale reset). Other
cached object types pass through and are cached under resized key. GIF prefetch
checks cache then loads with nil completion. Home background prefetch separately
loads light URL and distinct nonnil dark URL, each callback posting original
home-header notification on current thread. No origin restriction or UI approval
is introduced. Conversation admission queries actual DataManager only in the
original prevent-multiple/no-open-server branch; space queries traverse genuine
modules/home/openConfig/spaces. Missing loader/models/DataManager remain UNLINKED.

ITBImageLoader is the original strong-core adapter: concrete once-only default
and long-term loaders use real ImageLoaderCore factory methods, cache clear visits
both, and all other methods forward original arguments/selectors to core. A nil
core preserves Objective-C nil-message behavior. No substitute image decoder,
network downloader or memory cache is installed as its product core.

Config nested-source contract: ConfigModules requires home and customization
dictionaries and successful genuine nested parsers; HomeConfig requires header
and open_config. Both archive required typed children under those server keys
and compare dynamic Self followed by the original child order. HomeHeader
requires background/content and identity ai_agent or brand; optional
header_expanded Bool defaults false. Archive requires the same identity String.
OpenConfig requires String open_to and an array of dictionaries, compact-maps
Space parsers, and maps unknown opening strings to home. Spaces require String
type/label, map unknown type to original unknown3, and optionally parse badge.
SpaceBadge passive ignores label, while unread and unrecognized badge_type
require a String label. Coders preserve these branches and server keys; default
constructors trap as original. These sources do not replace missing models with
fixtures, and remain UNLINKED until Background/Content/Customization close.

Required Config color dependency preserves ICMColor scanner semantics: nil hex
returns systemGrayColor; otherwise NSScanner skips only '#', scans unsigned int
without validating success/length, then uses low RGB24 bits and Float division
by255 before UIColor construction. hexStringFromColor takes the first three raw
CGColor components (missing indices zero), multiplies by255, converts to Float,
and lroundf-formats uppercase minimum-width RGB. It does not normalize grayscale
or alpha. This is a partial original ICMColor class, not a new color algorithm.

HomeHeaderBackground requires recognized light type (solid/colors/image) and a
String color. Optional URL strings use Foundation URL(string:); dictionary
empty gradients become nil after color mapping, while coder empty arrays remain
empty arrays. Coder unrecognized light type falls back solid; missing/unknown
dark type remains nil. Optional flags default false, colors/gradients remain
optional. Coding writes hex strings and URL absolute strings, omitting nil
color/gradient/dark-type keys. Dynamic background and gradients use dark values
when present for dark traits, otherwise light; fade selects the matching bit.

HomeHeaderContent keeps all eight original Swift-only fields, required coder
close-button model, optional greeting/introduction and original default avatar
true for missing/invalid dictionary Bool. Valid logo URL strings trigger real
long-term-loader calls during dictionary/coder initialization, light then dark,
without URL equality deduplication; each completion posts HomeHeaderLogoLoaded
on its current thread. Dictionary close-button parse failure creates black /
systemGray /0.7 details. Text color defaults light; unknown optional dark type
stays nil. Label/close models require their color strings and Double opacities,
archive opacity as NSNumber (not scalar), and retain raw values without clamps.

Customization required color models preserve separate concrete runtime classes:
HeaderCustomization requires background/foreground strings and retains optional
dark variants; ActionColors additionally requires foreground low-contrast and
retains its optional dark variant; ActionContrastColors requires just two colors.
All use original ICMColor conversion, archive hex strings under server keys,
omit nil optional dark keys, compare dynamic Self in declared order, and have
trapping default constructors. Dynamic Header colors / Action primary background
select dark variants for dark traits when present, otherwise their light values.

CustomizationConfig requires successful header/action parsers, optionally parses
white/dark contrast models, defaults unknown/missing theme strings to light,
and parses the three optional URL strings without prefetch. Theme override is
mutable optional Int state, excluded from coding/equality; effective theme reads
it when present. primaryForegroundColor preserves original background-color
selection: light contrast background or action background; dark contrast
background or action dark background or action light background. The resulting
dynamic UIColor captures these two selected colors at getter time.

Aggregate Config gate (`ios-link-config-source.py`) compiles genuine Config,
all fourteen nested models, two Settings, image adapter/color methods and actual
persistence/support together for three iOS15 targets, then performs a normal
strict library link with zero fixtures/vendor inputs. Current link fails on
ICMImageLoaderCore and ICMDataManager only. No missing nested Swift symbol or
duplicate runtime alias was reported. This is UNLINKED, not product acceptance.
Xcode27.2 SDK UIUtilities/SwiftUICore auto-link warnings are retained in logs.

Original image-cache contract: cache keys strip query through URLComponents only
for the default core; nonempty processor keys append directly. Missing/empty URL
returns a new UUID string. Disk filenames are UTF8 FNV1a64 (offset basis
0xcbf29ce484222325, prime0x100000001b3) formatted %016llx, not platform hash.
Memory cache wraps NSCache, observes memory warnings and optionally background
with weak callbacks and queue nil, and removes both observer tokens on deinit.
Disk cache creates its named user Caches directory or returns nil. Reads are
synchronous Data(contentsOf:) and asynchronously touch modification date after
success. Writes/removes/clear use the original serial utility queue and weak self;
writes are atomic and enforce byte limit even if writing failed. Eviction lists
files with both size/date values, sorts oldest modification first, subtracts each
size even on removal failure, and stops at the raw byte limit. Clear removes and
recreates the directory. No added path, MIME, URL-origin or size validation.

Core animated dependency remains genuine ImageIO decoding: AnimatedImage accepts
at least two frames in a GIF/PNG-conforming source and decodable cover frame.
GIF then APNG loop/delay properties retain original precedence; missing delay or
delay<0.02 uses0.1 seconds. It stores frame delays/cumulative durations, decodes
under NSLock into a50,000,000-byte NSCache, and prewarms only once when all raw
frame costs fit that limit. Queue is original serial utility decode queue with
weak ownership. Frame lookup is bounds-checked; play-head lookup returns the
first cumulative duration>=head, clamped to valid range. No frame flattening or
replacement with static UIImage occurs in the recovered Core.

ImageDownloader follows the independently extracted IMAGE_DOWNLOADER contract:
private queue coalesces by key before preflight; subscriber UUID differs from
request UUID; nonnil preflight (including empty Data) queues finish without a
network fallback. Real resumed NSURLSession data tasks validate error/data/HTTP
status and retry original default three times at1/4/16 seconds. Cancellation
removes one subscriber and only cancels/removes the request when none remain.
Result/retry paths compare request UUID; fan-out removes request/all token maps
before unordered dictionary callbacks on the private queue. Weak-self initial
enqueue disappearance completes nil; other weak paths return silently. Original
private injected Swift ABI/configuration behavior remains unproven.

ImageLoaderCore factories use genuine Memory/Disk/Downloader with original
10,000,000/5,000,000-byte limits, default stripping true/long-term false and
AsyncQueue.main. URL/key cache reads are memory-only and normalize via URL;
cacheImage writes its raw String key and ignores nil. Generation requires a
nonempty key, returns cached UIImage only, otherwise invokes optional block and
caches its image. Load checks memory before URL failure, otherwise coalesces via
real downloader with disk preflight and weak self; result checks memory again,
then AnimatedImage/UIImage decode, removes failed disk input, caches genuine
image with cover-image bytesPerRow*height cost, and writes new network bytes to
disk only when preflight did not supply them. Deliver is inline only for main
dispatcher+main thread; otherwise dispatcher.dispatch. Cancellation forwards to
downloader; clear empties memory and disk but leaves downloads running.
Dispatcher/AsyncQueue now share their original implementation file between JWT
refresher and image Core; this is required to avoid duplicate Swift type symbols.

Unread response/mainline recovery contract: UnreadManager owns two strongly
retained (not copied) NSOrderedSet collections and a NotificationCenter. Both
initializers create independent empty sets; the injected initializer retains its
center. Setters post the corresponding IntercomUnreadConversationCountDidChangeNotification
or IntercomUnreadTicketCountDidChangeNotification synchronously only when count
changes, after replacing state. Read of an absent ID does nothing; a present ID
is filtered by SELF != %@, count notification occurs first, then
ICMConversationMarkedAsReadNotification / ICMTicketMarkedAsReadNotification with
the String ID as object. No queue, lock or dispatch is added. These are native
process resources, independent of consumer JavaScript heaps.

UnreadConversationsResponse and UnreadTicketsResponse retain original safe-model
inheritance, concrete factories, empty defaults and copy setters. Dictionary
parsing accepts an ID array only when its first object is a nonempty NSString;
remaining elements are not filtered. Valid dictionaries always run read-state
filtering against fresh DataManager singleton reads. Conversation parsing keeps
only dictionary entries and adds each real Conversation factory result without
a nil-result guard. Generic equality requires exact class; typed equality keeps
nil-field behavior. Conversation response hash XORs the two collection hashes;
ticket response hash is its ID collection hash. Missing Conversation/DataManager
implementations remain unlinked, never successful stand-ins.

DataManager mainline contract: shared dispatch_once captures the first receiver;
constructor registers three original no-colon lifecycle selectors, sets Nexus
topics to {*}, adds itself as observer, resets presence timer, then initializes
conversation/ticket arrays and real UnreadManager in that order. Teardown removes
NotificationCenter/Nexus observation and invalidates direct timer storage.
Active handling updates inbox then tickets only when PresentationManager reports
presented, then always sends presence and resets timer. Resign clears two flags
synchronously and asynchronously invalidates/clears the timer on main. A positive
Nexus heartbeat float schedules replacement repeating timer asynchronously on
main; presence creates actual Nexus event type5 with nil topics. Conversation
marking compares pointer then String equality, stores strongly, marks the first
matching unread cached conversation read, then synchronously posts inbox update
regardless of match. Open-inbound query preserves indexOfObjectPassingTest and
Objective-C nil behavior. Full DataManager/network merge/event handlers and
Nexus/Presentation closure remain unresolved until their true source is present;
partial constructor source does not authorize a working product claim.

DataManager inbox recovery preserves the two request modes. Pagination adds only
new conversations with lastPart and reports copied state plus whether any were
added. First-page updates set received flag, update page availability and merge
only when response conversations are nonempty; empty responses preserve cached
state/page flag. Existing matching conversations merge parts by partId OR client
UUID, retain cached composer state only when its version exceeds new state,
always carry cached footerNotice, and use descending lastPart.createdAt order;
parts sort ascending createdAt. Cancellation error -999 suppresses first-page
failure callback; pagination forwards failures. No extra queue/validation is
added; true HTTPBridge/response/Conversation dependencies remain unresolved.

DataManager tickets page applies nextPage, then optional unread IDs, then removes
existing matching tickets and appends each returned ticket; it replaces tickets
with descending latestStatusUpdatedAt sort, invokes success with mutable live
array, then synchronously posts tickets update. ID lookup/index enumerate all
entries and keep the last match. Detail fetch only replaces an already cached
ticket before callback. Mark-read requires a cached unread ticket, updates unread
manager, calls HTTP mark-read, then reads cached ticket again and marks it read.
No main dispatch or request-admission surrogate is added. Clearing empties array
and sets next page nil. Trigger poll defers via shouldGetUnread flag while app is
nonactive; active path optionally updates inbox if presented, then always fetches
unread conversations. Product HTTP/ticket model closure remains required.

Nexus socket recovery uses real NSURLSession default configuration/delegate nil
queue, immediately creates and resumes webSocketTask in synchronized constructor
flow. Timeout retains supplied NSNumber only when floatValue>0, else original70.
Delegate is weak; timers are weak. Connected requires nonnil task and state0.
Sending parses original Event (including type getter side effects), JSON encodes
options0 and sends binary message only while connected; send errors dispatch main
and500...599 invalidate timeout/schedule reconnect before delegate disconnect.
Receive error returns; success re-arms receive before handling. Handling requires
delegate selector support, prefers message.string UTF8 over data, JSON parses
without type guards, ignores ACK, forwards other parsed values, resets timeout
even empty/invalid data. No validation/hardening changes are applied.

Socket disconnect synchronizes cancel1000 purposeful_disconnection, clears task,
resets timers asynchronously on main, finishes session; reset does not nil timers.
Reconnect requires neither connected nor reconnecting. Scheduling asynchronously
on main increments attempts and uses min(2^attempts,256)+uniform(n), repeating
weak-self timer. Timeout is a one-shot main timer using float interval, cancel1001
Connection timed out. Open sequence clears reconnecting, listens, resets attempts
then timeout then delegate connect. Disconnection clears reconnecting/resets
timers, queues reconnect only for nonpurposeful/app-not-background, then immediately
notifies delegate on caller thread. No endpoints are contacted by compile checks.

Nexus Event factory reads eventName String (unknown->0), nx.Topics NSArray->NSSet
else empty, constructs concrete Event with UUID lowercase GUID/date and strongly
retained original topics, then original KVC parser. Dictionary eventData resets
every recognized field to validated String/NSNumber/NSArray/NSDictionary or nil/NO;
String eventData only sets conversationId; other values are ignored. Missing
assignee input mapping stays absent despite original output key typo assigeeId.
Undefined KVC only accepts String nx.FromUser/nx.ToUser; other keys are ignored.
Ordinary KVC still invokes NSObject, preserving its exceptions/conversions.

Dictionary output returns nil for unknown event types, includes all three Boolean
fields and nonnil known fields, nested eventData plus eventName/eventGuid, optional
topics/users and acknowledgement/created/client metadata. GUID nil retains original
dictionary-construction exception. Original22 event name/type constants are
reconstructed from data literals; reverse table once captures first receiver's
class map. Copy/strong field ownership follows metadata; no authentication or
payload validation is added to this baseline. Socket/Event source compilation
alone does not prove initialized realtime or server compatibility.

Nexus Connection retains original configuration and recreates sockets only when
new endpoint array is unequal to old (including nil messaging). It disconnects
old sockets before constructing immediately-resumed new ones. validSockets only
filters nil; disconnectSockets skips sockets already disconnected, while explicit
background/foreground loops operate on all. Connection state1 is assigned only
when every socket is connected after starting network monitoring; state2 only
when every socket is disconnected. Empty arrays satisfy both checks. Delegate
callbacks precede starting/invalidating ping timer and are not deduplicated.

Lifecycle observation is registered weakly on main after1second; positive double
background timeout starts named UIApplication task immediately and queues an
optional one-shot timer on main. Expiration finishes backgrounding. Foreground
ends task, queues timer teardown then reconnects all sockets. Background finish
queues timer teardown, disconnects all, ends task then queues ping invalidation.
Heartbeat timer is created only for positive interval, repeats, weakly pings via
actual Event23; no extra prior-timer invalidation is added. Network monitor uses
utility serial nexus.reachability queue, path status1 reconnects else disconnects;
start precedes monitoring flag. Dealloc removes observations and cancels monitor.

Event eventData.showAvatar non-NSNumber/missing/null fallback is YES (original0x9e184), unlike the NO defaults of isBot/hasAI. Constructor-only zero initialization remains NO until a dictionary eventData is parsed.

Nexus Client owns original weak pointer-personality observer table, count200 GUID
cache and memory-warning observer on cache itself. Concrete singleton constructor
creates no connection until setConfiguration; setter synchronizes store/update or
construct, posts config change synchronously, then clears connection when nil.
Topics are retained, compared by new.isEqual(old), added/subtracted deltas sent in
subscribe then unsubscribe order. Send caches GUID before synchronized connection
send only for nonzero type and nonnil dictionary. Incoming eventName presence
alone gates factory; last event/time (milliseconds via NSNumber.integerValue)
update before duplicate suppression. Fresh GUID cached before synchronized weak
observer snapshot and optional observedConversationId filtering; events dispatch
async main with observer/event retained. Connect/disconnect separately create
type1/type2 event per observer on main; connect subscribes topics after leaving
lock. Status metadata preserves original state strings initialising/connected/
disconnected and event millisecond timestamps, optional event type/time fields.

DataManager realtime Event integration preserves only original branches6/16/22/24.
Seen16 marks cached conversation read, posts inbox update, then nonnil ID unread
removal. NewComment6 polls only for absent/empty or non-marked conversation ID,
then refreshes tickets when event ticketId is nonnil and differs from ticketToMark.
Content22 entityType NSNumber44 invokes Carousel service,85 instantiates genuine
Survey service; neither is replaced. Title24 requires nonempty conversation ID
and requests detail with reason conversation_title_generated, real Nexus status
metadata and nil admin-part timestamps/IDs; success updates conversation.
Missing UI service/HTTP/Conversation dependencies stay strict aggregate failures.

DataManager conversation update preserves missing-header expansion fallback from
initial conversation, filters only parts whose createdAt pointer is nonnil,
merges only cached composer state, removes first ID match, inserts by strictly
descending last-part date (equal dates stay after), then posts inbox update.
It does not merge old parts in this single-update path. Clearing undelivered
conversations only requires nonnil conversationId. Matched-new returns copy of
initial or genuinely constructs Conversation. Participant matching first builds
id-to-participant dictionary from nonnil IDs then assigns every part's lookup
result, including nil. Suggestions nonnil/nonempty append parts and assign model.

DataManager Open response integration forwards error unchanged. Non-nil response
updates optional team presence, CTA/card, initial Conversation, request/powered/
privacy/recent/resolution/bot fields in original order, optionally inserts recent
conversation, then void success even for nil response. Clear resets nine fields
then shared TeamPresence. Conversation construction uses genuine Conversation,
parts/header/composer/powered/teamintro/special fields, participants, then optional
suggestions. Required Open/NewConversation/Card/TeamPresence model implementations
remain explicit product dependencies. Recent conversations sort/filter by model's
original isEligibleForRecentConversations then limit to caller unsigned count.
Recent tickets use calendar minus7days, strictly later status dates, descending
sort and first3 only when count>=4; original return mutability is preserved.

HTTPBridge response application synchronously stores user, presents carousel then
survey, applies unread conversations then tickets, and only then queues retained
response/class on global priority2 to read/apply realtimeConfig at execution.
Nonnil user updates real IdentityStore. Conversation unread admission is nonnil
conversations pointer (empty accepted), unread IDs setter occurs before actual
Presentation.setUnreadConversations with nil completion. Ticket admission is
response nonnil. Service dispatch is original class method. Realtime config only
applies when input nil OR current Nexus endpoints count is0; then IdentityStore
user config precedes Nexus config. No main redirect, snapshot or unconditional
config overwrite is added. Full HTTPBridge/UI/session closure remains unresolved.

UserUpdateRequest/Deduper mainline contract follows independent ios-http-bridge
CONTRACT.md. Request default attributes empty/newSession NO/background YES;
sessionId copy, attributes strong. Instance add is shallow; class add has only
custom_attributes one-level merge, no old-type guard/fallback for nil root.
Overwrite tests compare only existing keys with present new unequal values.
Batch merge preserves OR new/internal, existing session ID, AND background; delay
uses original Config. Deduper.instance reads disk each time, never cached singleton.
Its synchronized decision retains attribute dictionary, fixed window start,
original <= cache-age (NaN fails), and merges even duplicate updates. Dirty store
clears before asynchronous low-priority archive capturing self. Archive uses
real Persistence/Utils updates.icm; secure coding allows only original five
Foundation classes, excludes dirty flag, and emits fields only when attrs nonnil
and timestamp nonzero. No actual default namespace I/O is used by compile gates.

User update transport preserves original admission and parameters, including real
DeviceData capture and real proactive visibility flags; session_id only with
newSession and nonnil sessionId. POST users success parses BaseResponse only with
caller success, delivers synchronously main or async main, then independently
marks device token submitted when request device_data.device_token is nonnil.
A404 with original user.intercom_id and at least3 user fields retries once with
only intercom_id removed, retaining auth and all other parameters. Retry failure
dispatches main before terminal failWithNetworkError; no recursive fallback.

HTTPBridge.updateUser consults genuine disk deduper, duplicate success uses
original main sync/background async and no HTTP. Otherwise deduper stores before
request, original debug logging retained, success applies full BaseResponse then
actual Presentation show then caller-main completion. Failure first submits real
metrics (still unresolved until its implementation) then caller-main error. No
rollback is invented. ProactiveVisibility has two defaults YES, plain setters
without effects, combined change assigns both then fires internal update only
when user identity exists, treating applicationState!=active as background.

Presentation mainline recovery preserves original process singleton, six ivars and
root-delegate protocol. Init registers read/config observers then resets original
window appearance; it does not create a window. Visibility/padding setters retain
original side effects and callback threading. Launcher scheduling is always main
async and reads unread state at execution. Nil unread input completes immediately;
non-nil input retains original array and routes genuine root update. Window
creation requires original application UIWindowScene rules; the fullscreen
argument is intentionally unused and creation sets fullscreen NO. Sheet and push
ticket wrappers do not ensure a window. Survey sets the original service flag.
Conversation routing preserves root-first/push/fade and immediate completion
semantics. Genuine Window/Root/Appearance/native controllers remain explicit
source/link dependencies; declarations alone are not restored UI. No rendered
UI or product initialization acceptance is claimed by object compilation.

Window preserves original UIAppearance resets, zero-size frame repair, actual
theme provider primary icon, theme-mode lookup, dark preference override, root
class-only setter/error log, host key-window switch, precise raised levels,
teardown and root touch gate. Application windows come from active scene
delegates exposing window, retaining original repeated reads. Root still lacks
a body and theme provider/model remain real unresolved dependencies.

Root's required OpenCallService preserves optional nil/0-loading/1-success/
2-failure state, auto-start getter warning, retry only failed, synchronous state
notifications before actual DataManager open call, weak callback in-flight guards,
JWT-change retry on expired failure and no-notification invalidate/hide clearing.
Default init traps; shared uses real DataManager and NotificationCenter. The
three NotificationCenterInterface requirements follow original dSYM witnesses.
Private DataManager protocol completeness/Swift ABI remain unverified; source
uses real concrete manager until the original protocol surface is complete.

Root lifecycle/host/unread restoration uses original thirteen ivars and ownership,
real host-state and OpenCallService constructor effects, real InApps controller
construction/containment in viewDidLoad and frame updates in layout. Host-window
lookup is weak cached and excludes hidden/Intercom scene windows. Unread routes
only after identity admission; no new dispatch, nil-controller success or lazy
substitute is added. Original modal/destination/teardown selectors not yet
implemented remain inventoried as missing even if the Root class symbol resolves.

Root modal dismissal preserves top-navigation selection, immediate no-presenter
close without caller completion, original will/did notifications, recursive
main-async dismissal and bottom-controller traversal. Content-modal clearing
requires the original single content controller check; window teardown/status
restoration follows actual presented state. Notification names are decoded from
original constant pointers; save/restore uses original per-application status
behavior including the iOS26 branch. No simplified modal/navigation substitute
is supplied for missing original classes.

HostAppState captures top presented controller (self-loop guard), default real
UIViewController when absent, original orientation/rotation and recursive status
child-controller queries. App-level status instead reads the original root's
window scene manager twice. UIViewControllerBasedStatusBarAppearance absent,
CFBoolean true or NSNull means controller-based; other values mean false.
Equality exact class then original four scalar comparisons, typed equality has
no nil guard, hash XORs the original four values. No new defaults are invented.

Root configured destinations retain genuine original controllers and original
error gate. Home routes by signed openConfig.type 0..4, unknown returns; home +
conversation creates both original controllers regardless configured route.
Conversation space directly creates home + space. HelpCenter constructors run
before the wrapper resets article-viewed state and uses non-messenger navigation.
Error gate performs real three translations, repeated identity debug reads and
real ErrorViewController presentation, except identity exists and !tearingDown.
Unavailable original destination and TranslationManager selectors are explicit
dependencies, with no replacement controllers or fallback localized text.

Root remaining content/callback routes preserve genuine ArticleHosting,
Carousel, TicketCreate/Details/ViewModel and Survey controller construction,
metric service calls, content-modal exclusion for ticket controllers and original
0.33-second fade/stack replacement. Conversation reuse reads conversationToMark
and navigation top before fade and checks original first-conversation predicate;
matching existing controller updates composer, otherwise pushes genuine detail.
Presentation callbacks retain original notification order and identity-free
forwarding; hideInApp uses pointer equality, launcher first logs its metric.
UIViewController first-conversation lookup walks presenting parents then window
root presented navigation and first controller, including original nil-view trap.
No missing destination body is treated as functional completion.

Current frozen aggregate gate compiles65 ObjC +24 Swift source files for device
arm64 and simulator arm64/x86_64 with iOS15 deployment. Root original8 protocols
are declared with required/optional metadata scope; primary required bodies and
all120 own selectors are present, as are Manager55, Window9 and Appearance3.
This selector-presence check cannot establish ABI, private Swift typing, UI
rendering or network/identity acceptance. Strict link still reports28 actual
missing classes, while the separate eight-class method inventory reports absent
selectors inside already-defined classes. Receipt source hashes had no drift.
The entire SDK remains NOT_RESTORED and the original native destinations/resources
must be implemented and normally packaged before presentation runtime acceptance.

ICMBridge registration's required SessionManager/StartAttempt preserves the
original synchronized token state machine, not a successful session fixture.
Background records timestamp and whether a token is in flight; new session
threshold uses strict > after currentTimestamp read. Concurrent begin requests
set retryRequested and return token0 attempt. New token wraps NSUInteger exactly,
keeps pending UUID across failures and creates UUID only for nil pending ID.
Success/failure require nonzero matching token; success clears owed/retry, keeps
background record when flagged, records pending ID through genuine Swift
SessionIdentifierManager then clears it. Failure leaves pending ID, sets owed
and returns prior retry flag. Reset keeps lastIssuedToken. Attempt factories
respect receiver and copy sessionId; requestsNewSession is token!=0.
SessionIdentifierManager snapshots currentId at init, removes previousId after
super.init, and record writes currentId without mutating that launch snapshot.
Default init traps; shared uses standard defaults. Validation does not execute
shared/default production persistence; synthetic test namespaces only if needed.

ICMBridge initial lifecycle/register source follows original0xf3b8..0x10444:
load calls real push swizzle unless original Utils flag then scene observation;
initialize checks integration on each receiver, once schedules prepare on main,
then synchronously obtains DataManager. Prepare preserves active ping/diagnostics,
notification registration order, real SessionManager background observer, crash
subscriber gating and config observer. These true missing Push/Utils/Diagnostics
methods remain explicit dependencies, never skipped for linking. Ping identity
gates, begins real session; new-session branch snapshots identity, shows launcher,
updates internal request, success accepts only claimed token+same snapshot+not
already authenticated, failure records/retries original token. Non-new gets unread.
Registration snapshots before begin; its success does not gate authentication on
SessionManager success BOOL, only same identity snapshot, and always calls caller
success; failure invokes caller before retry ping. Complete authentication does
real push registration if enabled, then auth flag and fresh-store save. Snapshot
retains one user then anonymousId/userId/email with NSNull fallback. Fetch reads
identity existence, email and userId from fresh singleton calls, falling back to
anonymousId only for nil userId. No account/network execution is part of compile.

Bridge app/secure options preserve original cleanup and authentication ordering.
Changed app credentials delete Config, clear nonidentity data, clear true Metric
events, clear nonnil intercomId, clear Nexus config, ping then fresh-store save.
Both nonnil HMAC/JWT warn and remove only HMAC before apply (NSNull still counts).
Apply snapshots current JWT, uses actual IdentityStore update result; only changed
NSString JWT invokes real Refresher.userJwtWasSet then main notification, while
any successful secure-options update calls sendSecureOptionsIfNeeded. Refreshed
JWT callback posts synchronously on main or async main, then identified+outstanding
session pings. Set/remove provider only assign real Refresher. Identified secure
send pings if session outstanding, else registers only when unauthenticated.
No new validation, HMAC hardening or main-dispatch wrapper is added.

Bridge identified/unidentified registration preserves existing and previous
identity fast paths. Identified current user rejects unidentified registration
with actual ErrorProcessor error; an anonymousId pointer marks authenticated and
saves before direct success. Fresh anonymous register clears only when Store
returns changed, then always requests/registers/shows/queues Nexus configuration.
Identified registration delegates on actual identityExists; existing matching
registration attributes authenticate/save then optionally update nonidentifying
attrs, previous match uses previous then authenticates/saves/direct success. New
registration clears pending Engine/Nexus only on changed Store AND prior
unidentified flag, but always proceeds to HTTP registration and launcher.
Nexus update always global(priority2) and reads captured Store.userIdentity at
execution. No new registration validation or auth state shortcut is inserted.

Bridge logout clears true Engine generation, real disk deduper, authentication
flag and Store identity, Nexus configuration, Config greeting, actual Carousel
cache/presented ID and Survey flag, then hides UI and clears nonidentity data.
Nonidentity clear invokes genuine composer persistence manager, presentation
userChanged, DataManager conversations/last-viewed/unread sets/tickets, actual
SessionManager reset and DataManager open data in original order. Missing
composer/cache implementations remain link dependencies. User update retains
original dictionary for identified users; anonymous updates make dictionaryWith
Dictionary, log and remove nonnil user_id/email (no added guards), then request
only when remaining count nonzero. Empty updates do not complete either caller
callback. Three error wrappers return actual existing ErrorProcessor errors.

Latest core initialization snapshot: 71 Objective-C +25 Swift units compile
for device arm64 and simulator arm64/x86_64, iOS15 deployment. Bridge34/69 own
selectors now have compiled source; Root120/120, Manager55/55, Window9/9 and
Appearance3/3 remain present. Strict link reports30 actual missing SDK classes
on each target; absent selectors in defined classes remain separate gaps.
Receipt input hashes were rechecked with no drift. No vendor, fixture, scaffold,
SDK initializer, network task or production persistence was used by this gate.
SessionManager/StartAttempt have29 focused host assertions using explicit
Config/recorder fixtures; genuine SessionIdentifierManager is source-compiled
but its standard-default persistence was not exercised. Independent static
SessionManager review found critical original token/notification ordering matches;
this does not establish product session runtime or full SDK acceptance.

Composer persistence preserves the original standard-defaults key
intercom-sdk-composer-persisted-string and dictionary conversationId/string.
Persist and clear synchronize through a fresh standardUserDefaults read. Lookup
checks only stored conversationId NSString and compares via a second read; the
container and returned string are not additionally validated. Nil dictionary
literal values retain their original exception boundary. No production defaults
are touched by compile validation.

Metric storage preserves class-level synchronized access, lazy mutable-set repair,
Config.metricsEnabled and exact event-class gates. Save snapshots a concrete
Metric before locking, mutable-copies valid sets then archives on global
background priority. Clear saves separately after each nonempty category; subtract
requires both stored and supplied counts. Disk class allowlists include original
Metric/Set/Event/OpsEvent; original env.icm filename and secure coder keys persist.
Typed equality uses events/opsEvents getters (including their repair), while hash
uses only events. Event bodies remain separate true dependencies until restored.

Integration checks preserve unconditional original missing-resource/photo warnings
and NSNumber-only IntercomDisableSwizzling interpretation. Photo warning requires
minimumOSVersion.integerValue<=13, absent photo key and development environment,
in that order. Bundle lookup preserves identifier→Utils class bundle→main bundle
resource URLs, then bundleWithURL; intercomBundle caches even nil once. Resource
existence is not forced, and actual SDK resources remain packaging acceptance.

Ops metric factory validates two nonempty NSString values and a nonnil NSNumber,
then allocates without invoking init and strong-stores initial values. Later
property setters copy. Serialization repeats validation and emits type/name/value;
secure coding decodes original concrete types, typed equality compares those three
values with nil equality, and hash XORs them. No trimming or numeric range gate.

MetricEvent accepts nil/dictionary additionalInfo, extracts conversation/part IDs
and 42 ordered typed fields including duplicate reads, merging all input only for
two article contexts. Initializer requires four nonempty strings and JSON-valid
metadata. Environment/user metadata precedes caller merge; SDK/app/device keys
then override it. EventType0..4 owner/name tables match original constant data.
Archive omits type; equality compares uuid/time/metadata/type, excluding owner/name.
Original Conversation and ConversationPart remain required missing bodies.

Bridge token registration refreshes real PushNotificationStatus first, then runs
on main inline or async-main from background. Enabled state sends HTTP registration
then installs swizzling unless disabled; disabled calls actual ErrorProcessor only
when failure exists. Token deletion refresh callback captures current Store before
checking disabled+submitted, then uses its token and user. No fresh state query
is replaced with a fixed authorization result or a successful network fixture.

Push installation preserves process once/manual/extension/stored-token branches,
setter-before-original forwarding, selector-aware target discovery with visited
pointer identities, terminal NSObject/NSProxy/UIResponder roots and per-selector
class sets synchronized on receiver. Launch observer removes itself before callback.
Four notification wrappers retain distinct original forwarding/completion rules:
Intercom remote fetch handles delivery3 then NoData, launch modifies-before-host
then delivery1, response suppresses Dismiss and delivery0, foreground suppresses
with options0. Only remote fetch conditionally adds absent method; other three
and rich extension replace existing methods only. No callback once guard or main
thread wrapper is introduced. Real downstream payload/parking/media behavior is
still an explicit dependency until its source is implemented. No swizzling is
executed by compile/link validation.

Core source milestone:78 ObjC+25 Swift aggregate compilation passes all three
iOS15 targets; strict link29 genuine classes missing, including ConversationPart
newly required by actual metric factory. No source hash drift. Bridge41/69 own
selectors present in compiled objects. Composer/Metric/Ops/Event and Utils
integration/Push installation are actual source, not diagnostic substitutes.
Original evidence: implementation-baseline/ios-bridge/{ICMMetric,ICMMetricEvent,
ICMOpsMetricEvent,ICMLocalComposerPersistManager}.asm.txt and ios-integration-push
CONTRACT/CALLBACKS + raw excerpts. Metric owner/name constant tables decoded from
Mach-O data0x42cd10/0x42cd38; only literal data was transcribed. Push original
payload/parking/media methods, DiagnosticSubscriber, HTTP diagnostics/metrics,
resources, real initialization/login and native UI runtime remain incomplete.

PendingPush strongly retains original userInfo/date and stores signed verdict;
stopWaiting uses repeated observer getters, removes from default center then nils
token, with no implicit dealloc cleanup beyond ARC. PushPayload defaults type -1,
concrete factory accepts only dictionary for KVC application; uri special handling
accepts NSString, unknown snake keys use exact original mappings and validation,
unknown keys ignored. String conversion accepts string or NSNumber.stringValue.
Push-only ID preference is nonempty instance then nonempty conversation; no trim.
All copy property setters and signed type layout are retained.

Push dispatch keeps original identityExists admission before delivery logic.
Delivery0 consumes cold-launch duplicate marker even on mismatch. Original verdict
table: tap0→2, cold1→2 only inactive, manual2→1 only nonactive, fetch3→1 only
inactive, others→0. Nonactive verdict1 cannot replace pending verdict2. Park
replaces real PendingPush after stopping previous observer; activation observer
weakly captures receiver. Present consumes pending before removing observer, drops
verdict1 only age strictly>10s, then main-inline/async foreground callback. No
added lock, timestamp clamp, active recheck or account fallback.

HTTP metrics validates both arrays and JSON before count/admission; empty valid
arrays synchronously succeed without identity. Nonempty metrics uses fresh dict
(device_identifier,metrics,op_metrics) with genuine Engine POST metrics; callbacks
forward on original thread without conversion. Diagnostics differs: identity dict
copy, device_identifier/diagnostics, POST error_reports; success main-inline/async,
failure actual network-error processing. Invalid input retains original400 text
(including metricss typo). Bridge metrics creates background task before snapshots,
KVC eventDictionary set projection, clears only captured sets on success; its end
block never resets Invalid. Diagnostic bridge gates Config flag, creates task then
subscriber snapshot IDs/payloads, ends directly when empty, clears IDs only on
success; its end block resets Invalid after ending. No common cleanup policy.

HTTP token registration stores token/environment and saves even when unchanged,
then success-fastpath only unchanged+alreadySubmitted. Otherwise Config must be
received; when verification enabled, nonnull/nonNSNull hmac admits, else jwt must
exist and not be definitively expired without provider. JWT helper parses expiry
only, no signature claim. Original decision errors and main-inline/async callbacks
remain. Client submit uses actual identity/device_data POST device_tokens, marks
submitted before callback, prepackages HTTP error even code0. Delete uses supplied
user and current app admission, nil token becomes empty string, DELETE same path;
completion is invoked/enqueued before pointer-equal-current-user submitted reset.
Failure uses existing error processor then main completion; admission failure
logs and calls completion synchronously on current thread. No real token or
request is sent by source validation.

Bridge direct application controls forward to true Presentation/Visibility/Engine
and Store state. Bottom padding sets manual flag before value on one manager.
Theme only acts when Config/modules/customization exist; raw0 clears,1→light0,
2→dark1, other→system2 then updates window. Dispatch helper defers only when app
state exactly inactive; active/background execute current-thread even off-main.
Nil helper block is ignored. This original behavior is not generalized into a
new UI-thread policy. Auth tokens use genuine HTTPBridge mapping; logging toggles
original Utils flag, wrapper version uses current Store app Engine.

Latest frozen aggregate supersedes prior counts:86 ObjC+25 Swift compile across
all three iOS15 targets, hashes unchanged, strict link29 genuine missing classes.
Expanded original own-selector gate:Bridge54/69, HTTPBridge15/50, HTTPClient18/60,
Utils27/40, PushManager32/38; Composer5/5, Metric23/23, MetricEvent26/26,
OpsMetricEvent18/18, SessionManager27/27, StartAttempt8/8. Selector presence only,
not runtime ABI/behavior. Correct MetricEvent/Ops original own counts are26/18.
OpsMetricEvent has14 real Foundation host assertions passing constructor-retain
vs later-copy, invalid inputs, dictionary/hash and secure archive; evidence at
node_modules/.cache/ios-source-ops-metric/receipt.json. No original comparison
or SDK initialization/defaults/Keychain/network was involved. Installation/Utils
key branches have independent static review only, no host swizzling execution.

Bridge original presentation wrappers use concrete Bridge inactive-only dispatcher
then genuine Presentation destinations. HelpCenter/Article record original API
metrics after presentation; MessageComposer only receivedConfig+inboundDisabled
falls back to Home with original log, otherwise constructs original Conversation.
Ticket first obtains details through actual HTTPBridge and shows trigger3, logging
errors unconditionally. HelpCenter data wrappers construct real Swift data service
per request and forward original result/error completion unchanged. Event logging
snapshots background state, rejects disabled background requests, otherwise uses
original RateLimiter and HTTPBridge. Missing services remain real dependencies,
not replacement destination controllers or successful facade callbacks.

Event RateLimiter uses concrete process singleton, genuine max-size timestamp queue
and Config change observer. All limit actions and callbacks run under receiver
@synchronized lock; successful admission queues current timestamp before action.
Period exactly0 disables limiting; otherwise admits while size<max or strict
age>fresh Config period. Queue shrinks using original signed count-minus-max
interpretation, enqueue evicts only count==max and first exists, and adds only
count<max. Zero capacity drops items; no clamp/default/range validation added.
Dropped callback precedes original unconditional rate warning.

Frozen Bridge milestone:89 ObjC+25 Swift compile on all three iOS15 targets;
Bridge69/69 original own selector symbols now present, PendingPush8/8, Payload25/25,
MaxSizeQueue9/9, RateLimiter11/11. Actual receipt counts are authoritative for
selector names; presence alone does not establish ABI/behavior/product closure.
Strict link30 actual SDK class gaps now includes genuine HelpCenterDataService
exposed by original wrappers. No replacement helpcenter/callback facade added.
Hash recheck found no drift. Rate/Queue original0x21a1c..0x221bc are retained in
implementation-baseline/ios-bridge extracts. No initialized SDK/session/network,
OS notification swizzle, real/default Keychain or source binary fallback was used.

HTTP event logging admits real identity before parsing name, copies its dictionary,
adds device identifier, builds mutable event with nonnil+length name and any
nonnull metadata, then background/carousel flags and real Engine POST events.
No new string/JSON metadata validation. BaseResponse factory runs only for nonnil
success and callback is main-inline/backgroundasync. Bridge logs success then
handles full response; background rejection preserves original unconditional log.

Inbox routing corrects earlier incomplete declarations to original object ABI:
DataManager/HTTPBridge before is NSDate*, pageSize is signedNSInteger, unread
completion has no arguments. ConversationsResponse contains user+conversationPage,
while ConversationPageResponse owns array/morePages/empty-state. Genuine response
factory updates Config first; user/page parse only dictionaries. Page parses
conversations in order with style1, applies current DataManager conversationToMark
read flag via repeated singleton reads, then copy-stores array. Empty state uses
original Swift MessagesEmptyState, still required; not a nil substitute. Page
equality/hash ignore empty-state. Top response equality compares page typed then
user, hash31*page+user. Original unknown-key/nil handler boundaries preserved.

Unread/inbox/open HTTP entries preserve real identity admission, identity dict
copy, device_identifier and original POST routes. Per-page positive signed value
passes through; nonpositive uses original NSNumber10. Inbox before NSDate becomes
raw epoch double. Success parses only if callback nonnil, then main-inline/async
callback. Unread uses BaseResponse; inbox uses ConversationsResponse, Bridge stores
user then returns only page; Open uses original Swift OpenResponse dictionary init,
which remains a missing real model. Bridge unread handles full response before
no-arg callback. No synthesized Open response or parser substitute is linked.


Frozen inbox milestone:95 Objective-C +25 Swift implementation units compile for device arm64 and simulator arm64/x86_64 targeting iOS15. Strict aggregate link reports32 real missing SDK classes, including OpenResponse and MessagesEmptyState newly exposed by genuine inbox/open parsing. Source SHA recheck has no drift. ConversationPageResponse14/14 and ConversationsResponse12/12 original own selectors are present. Selected selector presence is not ABI/behavior or product closure. Evidence:node_modules/.cache/ios-source-aggregate/receipt.json; Session compile receipt includes78 units. Actual initializer/network/OS push/default Keychain were not executed; fullSDK NOT_RESTORED.


Diagnostic configuration/store source contract: preserve Swift casts and four raw types, no sampling clamp or remote limit override. Store lazily loads diagnostics.json, compact-maps valid id/createdAt/payload dictionaries, keeps insertion order, caps only append via suffix(maxPayloads), retains failed-write cache, prunes age strictly < maxAge on fetch, and atomically saves ISO8601 dates. Remove always rewrites cache/disk, including an empty id set; no directory creation or added rollback. These are genuine dependencies for Subscriber, not a completed diagnostic feature.

BinaryIdentifier uses dladdr for its own loaded image, NSString lastPathComponent then replaces occurrences of .dylib, classifies names with prefix Intercom as dynamic and all other found names static. Missing dladdr uses unknown/nil/dynamic. UUID lookup compares exact dyld image path and traverses original Mach-O ncmds with 64-bit magic header size32, otherwise28; first LC_UUID formatted uppercase with hyphens. Recovery resolves its own source product image rather than the original SDK UUID; no hardcoded attribution.

Diagnostic extraction contract (d5b2c..d67d0): preserves crash→hang→CPU→disk-write iteration order. Each diagnostic reads its real callStackTree.jsonRepresentation. Crash keeps optional NSNumber.integerValue exceptionType/signal/exceptionCode plus optional terminationReason; hang/CPU convert actual measurements to milliseconds; disk-write converts to bytes then Swift Int (original nonfinite/out-of-range trap). Payload timeStampEnd is read after all diagnostic arrays. No synthesized diagnostic or successful fallback. Three current support files compile on three iOS targets;14 host config/store assertions pass, no original-runtime comparison. Host NaN JSON exception evidence is retained separately; no added product validation.

Diagnostic Subscriber lifecycle/process contract: shared builds real Store(100,604800), BinaryIdentifier, Filter/Builder with actual DeviceData context (nil strings become empty), standard defaults and fresh per-cycle Config mapping. Original -init traps; dependency init is usable. Receiving flag precedes MetricKit add; first past payload processing queues weak self, completion still runs without self. Repeat starts complete inline; stop only removes subscriber and flag. Processing asserts not-on-main-queue, extracts all real payloads before config decision, filters timestamps strictly greater than persisted Date/distantPast, resets failure flag only for a nonempty new list. Static attribution uses untouched JSON, dynamic filters before sampling; crash may attach only captured lastLaunchSessionId. JSON size bound is inclusive; append failure pins watermark; otherwise max considered timestamp advances even when all entries were filtered/sampled out. No implicit main hop/cancel/reset or diagnostic disable bypass. Filter/Builder remain required true source dependencies until implemented.

Diagnostic filter exact dynamic boundary: recursively matches binaryName prefix Intercom (static helper branch exact own name); UUID never participates in matching. Dynamic requires at least one attributed stack with nonempty own recursive match set, unions names across all valid stacks, preserves all stacks and unknown JSON fields. Valid non-SDK named frames receive zero UUID, including absent original UUID; wrong/missing binaryName leaves frame UUID unchanged. Subframes recurse only when castable dictionary array. Static subscriber path passes original bytes unchanged with own binary name and unverified attribution; dynamic output confirmed.

Diagnostic payload builder d01b8..d0f00 first parses filtered bytes as dictionary; failure nil. Seven base keys: freshUUID id, platform ios, raw diagnostic_type, shared default ISO8601 timestamp, complete parsed call_stack_tree, injected device_context, sdk_context. SDK context preserves actual version fallbackempty, Array(matchedNames) unsorted, confirmed/unverified attribution, dynamic/static linkage and actual name/UUID fallbackempty. Metadata enum independently selects one corresponding map; missing crash optionals omit keys but retain empty crash_metadata. Numeric metadata unchanged at builder; extraction does original conversions. No rootkey stripping or synthetic UUID attribution.


Diagnostic genuine source milestone supersedes earlier aggregate:95 ObjC+32 Swift compile on device arm64 and simulator arm64/x86_64 targeting iOS15, inputSHA unchanged. Strict aggregate reports31 actual missing SDK classes; DiagnosticSubscriber class gap is now removed by actual MetricKit lifecycle/extraction/filter/payload/store source, not a linkfixture. Remaining fullSDK NOT_RESTORED; runtime initialization/MetricKit delivery/whole diagnostic behavior not verified. Independent limited static contracts in audit implementation-baseline/ios-diagnostics; support host checks:14 config/store assertions,10 filter assertions, actual own-image BinaryIdentifier UUID matches dwarfdump. Receipts node_modules/.cache/ios-source-diagnostic-{store,filter,binary}/receipt.json and ios-source-aggregate/receipt.json. No defaultUserDefaults/hostKeychain/network/SDK initialization by these tests. NaN JSON fixture uncaught Foundation exception retained separately and product behavior unchanged.

OpenResponse original110cb0..111b1c: requires composer_suggestions dictionary first, new_conversation dictionary then successful true NewConversationResponse construction. Only then calls ComposerSuggestions factory. Optional parsing follows requestId→ComposerState→PoweredBy→PrivacyPolicyNotice→recentConversation(style1)→teamPresence dictionary→NSNumber resolution ID→botIntro String. Equality ordered suggestions/newConversation/state/requestId/poweredBy/privacy/teamPresence/resolution/botIntro; recent conversation excluded. Original default init traps. Real parser dependencies remain unlinked until recovered, no successful defaults. PrivacyPolicyNotice requires Bool is_dismissed and String paragraph, preserves empty text, equality compares both with runtime-Self cast, defaultinit trap. MessagesEmptyState requires title and text strings then optional actual Action dictionary parse; no extra nonempty validation or action substitute.

Required empty-state/CTA asset chain: IntercomIcon raw0...5 maps paper_plane/chevron/question_bubble/clock/ai/none and five true ICMAssets getters. Generic image flips RTL; dictionary MessagesEmptyStateAction icon teammate/bot does not flip. Action requires type+label String, help→1, every other String→0; no defaulttypevalidation. CTA requiredtext/optionalsubtitle/icon mapping and real NSSecureCoding keys text/subtitle/icon; constructor -init traps. AssetManager original two selectors restored; approved packaging adaptation changes only asset bundle resolution to existing Utils.intercomBundle (missing SDK bundle returns optionalnil, never arbitrary host assets). Original entire data resources copied byte-identically to SDK bundles, car/manifest inside Intercom.bundle; resource packaging/runtime acceptance remains pending actual Pod consumer. ICMAssets only required five getters+image/tintable restored at this slice; other original selectors remain missing.

AssetManager relocation receiver boundary refinement: only exact AssetManager receiving metatype selects Utils.intercomBundle. Foreign subclasses inheriting either class method retain original Bundle(for:self). Equality compares receiver class identity, not bundles (which alias main under static linking). Missing default SDK bundle returns nil without UIImage nil-bundle host fallback; unknown name retains original optionalnil/logging. No broader Bundle lookup modification.

ComposerState full original26fb8..27520: SafeNetworkModel superclass, BOOL workflow8/signed display16/version24/copy placeholder32. Factory concrete class, dictionary-only bulkKVC, only undefined visibility mapping(enabled0/disabled1/hidden2;unknownpreserve) and workflow_active boolValue, otherundefinedignored. No valueclassguard. Equality exactObjCclass after self shortcut; typed equality first placeholder string or repeated pointer checks then scalar display/workflow/version (no explicitnilguard). Copy allocates receiverclass then setters ordered display/workflow/version/placeholder. Original description punctuation preserved, no added coding/protocol/hash.

ComposerSuggestions27524..28244 truefull source: concrete factory/dictbulk; prompt accepts only nonemptyarray and reads first object's text without classguards; suggestions array constructs each real Swift ReplyOption and addObject preserves nil failure. snapshot_id NSNumber uses original unsafe-unretained assign property; wrongtype falls superKVC. isEmpty tests prompt/suggestions pointers, not lengths. UUIDArray produces dictionaries {uuid:value}; nil throws. conversationParts actualTeamPresence.activeBot.participant then realPart construction, suggestions, twofreshUUIDs,style5/date/participant/id and oneelementarray. Typed equality self/nil, promptpointer/string, suggestions isEqualToArray (double nil remainsfalse), snapshot pointeronly; excludes disabled. Hash31*suggestions+prompt; coding prompt/suggestions(NSArray+ReplyOption)/snapshot only, disabled not archived.

Original DictionaryInitializable/DictionaryDecoder restored as existing original protocol/class, not a new abstraction: JSONSerialization options0→fresh JSONDecoder, custom epochString requires String then Double; invalid throws dataCorrupted(Invalid timestamp string: ...). Caller keyStrategy exact convertFromSnakeCase (GOT4564a8). Failable protocol initializer catches SwiftErrors/logs error and returnsnil; Foundation exceptions not caught or prevalidated. PoweredBy Codable fields icon/text/linkUrl (propertylinkURL), icon decoderrequiresString fin→1/anyotherString→0 but original encoder encodes Int rawvalue, preserving asymmetric Codable contract. Equality runtimeSelf then icon,text,URL; defaultinittrap.
ReplyOption direct instanceFrom dynamically delegates custom dictionary init (not genericdecoder), requiredtext+uuidStrings, pre_actionString exactclose-composer→1 else0. Codable similarlyStringpreaction with optionalmissing→0 and Int rawencoding, preserving asymmetry. Swift privateenumcase spelling is semantic .none/.closeComposer; original spelling not proved by remainingmetadata. Equality text/uuid/preaction; hash combinesuuid→text→rawInt; copydynamicclass; no added NSSecureCoding.

Aggregate Swift/ObjC alias correction: OpenResponse must reference the true recovered Swift PoweredBy type, not an imported Objective-C forward-interface alias of the same runtime class. The alias is retained only for genuine Objective-C consumer declarations; no duplicate runtime class or successful link substitute is permitted.

NewConversationResponse original110024..110780 requires actual CTA, HomeCard and ConversationHeader dictionaries/constructors in order before parsing parts. Parts maps original ConversationPart(style1) and traps on nil factory result; participants compactMaps original Participant DictionaryInitializable initializer; missing/wrong arrays become empty. Optional team_intro/special_notice are Strings. HomeCard requires text, optional subtitle/icon, AvatarDetails via original DictionaryDecoder; no added secure coding. AvatarDetails requires avatarType and avatars:[Avatar], String enum facepile/layered_bubbles/logo, unknown raw value fails decoding.
ConversationHeader originalf8a7c requires Bool use_bot_header, String background_color/foreground_primary_color/foreground_secondary_color and all three actual ICMColor results, Bool display_active_indicator, successful required collapsed dictionary, then Bool hide_greeting. Expanded dictionary is optional and failed expanded construction remains nil. Property storage follows original order. Equality/hash compare useBotHeader, displayActiveIndicator, expanded, collapsed, hideGreeting only; colors excluded. Collapsed requires title String, parses optional subtitle before AvatarDetails generic decoder then IntercomIcon mapping; equality/hash title/subtitle/avatarDetails/icon. Missing avatar_details passes empty dictionary to original failable decoder, preserving original failure logging. No successful default header/destination.

Avatar original12b330..12deb0 seven stored fields shape/label/initials/imageUrl/thumbnails/imageDarkUrl/thumbnailsDark; all decoder fields optional, shape missing→0, othermissing→nil. Coding keys ordered shape,label,initials,imageUrl,imageDarkUrl,thumbnails,thumbnailsDark. Shape String decoder only squircle→1/otherString→0, wrongtype throws; Int raw encoder retained. Thumbnail struct only square128:URL? with optional decode; preserve optional outer wrapper distinct from wrapper containing nil URL in hash/equality. Avatar equality runtimeSelf/allseven, hash same stored order; instanceFrom uses actual DictionaryDecoder and defaultinittraps. Private shape case source spelling remains semantic until proved.

ExpandedConversationHeader originalfabcc requires title, defaults missing/wrong avatar_details to empty dictionary then actual failable AvatarDetails decode; body/footer defaultempty dictionary arrays then compactMap real ConversationHeaderContent decoder. It reads body/footer arrays before avatar decode, then body parse→calendar_url String→footer parse. Equality/hash title/avatarDetails/body/calendarUrl/footer. ConversationHeaderContent requires text String/style enum, optional avatarDetails/color String; six exact String styles h1/paragraph/quote/caption/intro/greeting, unknown fails. No extra text/color normalization or placeholder content.

Participant original12e530..130090/typedbody130184: avatar/email/id/name/firstName optional, BOOLstored isBot/isAdmin/group. Decoder optionalkeys avatar,email,id,name,firstName,type:String,isBot:Bool; admin lowercased type==admin, missingboolfalse, groupfalse. Typedconstructor and decoder derive firstName only when nil from name.split(separator:singleSpace).first, explicit empty preserved. Custom initWith:isGroupParticipant: instead as?-parses camelkeys, case-sensitive type admin, original Avatar failableparser, suppliedgroup; not JSONDecoder. Encoder optionalfields then bool isBot and bool admin to type key, groupnotencoded, preserving original asymmetric type decoder. Equality concreteParticipantcast/all8 fields; hashonly id,email,admin,bot,group. No originalcopy/NSCopying.

Frozen Open parser milestone:98 Objective-C+51 Swift implementation units compile on device arm64 and simulator arm64/x86_64 targeting iOS15. Strict aggregate link29 genuine SDK class gaps remain; OpenResponse/NewConversationResponse/MessagesEmptyState/PoweredBy are now supplied by real parser source. All input SHA rechecks match, no duplicate Swift ObjC runtime class .N symbols. Evidence node_modules/.cache/ios-source-aggregate/receipt.json. Product initialization/login/nativeUI still unlinked; no SDK runtime, network, OS push, MetricKit delivery, real Keychain or resource consumer acceptance claimed. Avatar typed seven-argument initializer remains absent despite parser source; private Swift enum spelling/ABI not fully proved.

TeamPresence original3c98c..3d0f4: SafeNetworkModel fiveivars activeAdmins(copy),lastActive(strong),responseDelayShort/Long(copy),activeBot(copy). Bareinit no defaults. Concrete shared dispatch_once; concrete updateShared setter then synchronous defaultCenter ICMTeamPresenceUpdated nilobject even invalid input. Dict-onlybulk; active_admins array maps each real ParticipatingAdmin initWith and addObject(no nilskip); active_bot dict actualActiveBot factory. Invalid these values preserve old; otherkeys superKVC. Undefined active_admins redirects to activeAdmins, last_active onlyNSNumber→epochDate, delay snakekeys onlyNSString; others ignored. Reset admins/date nil, delays empty, botnil; copy dynamic receiver class and five setters ordered. No addedprotocol/equality/defaultbot.

PartMetadata originalfd79c: optional title/suffix strings, timestamp as? Int→epochDate else nil, optional avatars dictionaryarray compactMaps actualAvatar parser; emptyarray remains some(empty), wrong/missing arraynil. Parser failable declaration but no fieldguard prevents emptydict success. Mutableavatars then readonlytitle/suffix/date in originalstorageorder; concrete class equality title/suffix/date/avatars, hash same order. StreamingPart original132710 requires metadata dictionary and actual successful PartMetadata before typed constructor, compares optionalmetadata after dynamicSelf cast, defaultinittrap. ActiveBot original12addc requiredParticipant genericparser, has_ai Bool fallbackfalse, has_customized_identity Bool fallbacktrue, optional StreamingPart. Originalcopy dynamicclass/shallowfields, noeq/hash. ParticipatingAdmin subclassesParticipant with date/active/title; decoder optionalDouble date then Bool false then String then real superdecoder; custom directsnakekeys dateInt, activeBool, titleString plus parentcamelkeys/adminlowercase. Inherits parent asymmetricencoder/hash; eightargumentinitializer originaltrap; elevenargumentinitializer realstoredfields+super. Equality dynamicSelf+super+date/active/title, original misspelled typed selector lacks dynamicSelfcast.

Frozen TeamPresence milestone:99 ObjC+55 Swift implementation units compile across all three iOS15 targets. Strict aggregate28 genuine SDK class gaps remain; actual TeamPresence, ActiveBot/ParticipatingAdmin/StreamingPart/PartMetadata supply that prior class gap. TeamPresence19/19 original own selector symbols present. All input SHA rechecks match; no duplicate Swift .N ObjC runtime classes. Evidence node_modules/.cache/ios-source-aggregate/receipt.json. No product initialization/runtime/OS notification/real HTTP acceptance; complete SDK NOT_RESTORED. Participant adminAppNameTitle:appName: and Avatar typed initializer remain missing private entries; copy subclass constructor dispatch not independently compared.

ConversationPart incremental recovery is PARTIAL, not a product closure. Originalmetadata43ivars/280bytes,135ownselectors. Forty simple getter/setter pairs strictly checked against matching metadata GOT offset, instruction whitelist/count, nonatomiccopy vs storeStrong vs scalar patterns before using @synthesize. Three logic accessor entries remain manual: state setter propagates1/2 to upload else uploadState0 then clearsupload; displayStyle setter only on changedvalue updates every block excepttype12 with concreteClass.blockAppearanceForPart(self); articleId getter still missing at this slice. Constructor original displayTimestamp/sendFailureAllowsRetry true, seenState1/replyOptions empty/grouping3 and otherexplicitzeros. Copy dynamicObjCclass settersequence, true NSArray(copyItems:YES) for blocks/replyOptions, preserves original omittedfields/nil exceptions; displayStyle last. Remaining ownselector gate includes every original method and will expose all incomplete logic. No class-symbol presence counted as parser/SDK success.

Part classification original31bf8..329bc retains integerblock/style semantics and repeatedgetter ordering. isServerPart uses partId nonnil then pointer inequality with clientAssignedUUID, not string equality. isPersonalPart returnsYES for nilparticipantId, otherwise native currentIdentity intercomId comparison. isSingleMediaPart always evaluates personal then singleimage; if personal&&!image returns singleattachment, otherwise returns personal|image (original unusual branch, not normalized). shouldShowPartStatus always reads partStyle twice, admits0/7 only and excludesstreaming. articleId ignores stored ivar: only style0/singleblock/type12 returns that block.articleId, else nil. Other singleblock/card/media classifications preserve metadata gates and original rawtype constants.

Part raw dictionary block checks require NSDictionary then type NSString, rereadtype and compare exact link/linkList/messengerCard. isArticleCardBlock requires concrete receiver isLinkBlock result then any nonnilarticleId (NSNull counts present). determineAppearanceForBlock checks receiverclass isLinkListBlock before isLinkBlock, returns new genuine appearance object ornil. removeFallbackURL only after receiverclass isMessengerCardBlock, displayStyle0,partStyle0,deliveryOption1 makes NSMutableDictionary(dictionaryWithDictionary:) and removes fallbackUrl; otherwise returns originalargument identity. No genericURL sanitization.

Part blockAppearanceForPart always reads displayStyle then partStyle. DisplayStyle!=1 uses new Post for style2 else InApp; display1/style7 uses AIAnswer; then repeated style2/style1→ContainerCard, link→LinkCard, list→LinkList, messenger→MessengerCard, finally personal→PersonalPart elsePart. Original genuine appearance classes stay required unlinked, no substitute. Factory always concrete Part new→setDisplayStyle→setAttributes→concrete setReplyOptions→concrete setFormAttributes. Parser dispatch follows Astra PARSER_CONTRACT.md; metadata isKindOfClass return is deliberately ignored before true PartMetadata initializer.

Part setAttributes requires dictionary then bulkKVC before concrete originalAppearance initialization. If bodyarray, retains it as blockDictionaries and separately rereadsbody.count; summaryflag style0/delivery0. Per originalentry applyfallback first, per-single-messenger appearance override; link/list in summary use actual determinedappearance and maysethasArticleCard, otherwise mutablecopy type→paragraph. attachmentList dispatch uses baseappearance (not override). TrueBlockLoader receivesremainingdict+appearance, validblocks maysetattribution from originaldict string thenappend. Topattachments always appended. Oneblock/style0 getsdisplayMode1, final mutablearray storesdirect_blocks ivar. Attachmenthelper outerarray+nonempty only; each mutableCopy/type internal_attachment→actualBlockLoader with no extraelementguard. Equality concreteclass and selfshortcut then nullable other-receiver partId, seen/display, formpointer/profile/streaming, nullable ticket; only selfcreateTicket checksnullableblocks (non-symmetric originalgate), then pointerclientUUID/group/state/pointerreplyOptions/displayflags/pointersources/pointermetadata. Hash31*partId.hash+seenState.

Part convenience constructors original30f08..31bf0: sending uses receivernew, style1, firstbodydictionary.text nilfallbackempty, state1 BEFORE assigningupload, now/adminfalse/nativeintercomId, oneUUIDcopiedtoId/clientUUID,timestamptrue. Upload branch parsesbody onlynonempty then actualBlockUtils uploadblock/baseappearance, appendsnonnull, assignupload, originalstyle0 single/multiple displaymode1/0. Nilupload emptybody explicitlyemptyblocks. Bot and streaming instead consume real block.text, setstate0/date/admintrue/freshId/timestamp/blocks; bot suppliedparticipant+id. Streaming capturesrealshared.activeBot, repeatedparticipant getters, truestreamingmetadata, thenstyle7; it does not set isStreamingPart. ParticipantDetails makes realParticipatingAdmin genericdictionary {name:nexus.adminName,avatar:image_url from repeatedadminAvatar orNSNull}, no nilnamefallback, thenparticipant/admintrue. Temporaryarticle requires nonnilid/title, original pathappend and literal block {type:link,text:text,linkType:educate.article,articleId,url,title}; body/initialtrue/chat/id1 via receiverfactory style1, articlecreatedAt thennewUUID part/clientid. No inventedmissingvalues/URIencoding.

Frozen ConversationPart milestone:105 ObjC+55 Swift implementation units compile across device arm64 and simulator arm64/x86_64 targeting iOS15; sourceHashDrift=[] and no duplicate Swift ObjC runtime class .N symbols. ConversationPart135/135 ownselectors present, with43 original ivars/40 strictly-matched synth accessor pairs and actual logic in6implementation units. Strict link now42 genuine SDK class gaps (was28): actual Part implementations expose previously hidden BlockLoader/BlockUtils/appearance/Form/EventData/source-wrapper/reactions dependencies. An increased missing-class count is an expanded dependency graph, not a linked closure or behavior regression result. No fake model/UI/appearance/body returned. Initialization/login/nativeUI still unlinked, all new Part behavior runtime unverified. Evidence node_modules/.cache/ios-source-aggregate/receipt.json.

Form true dictionary slice e3070: required attributes[[String:Any]], typeString, disabledBool in order; compactMap real FormAttribute, reject emptyresult; typedconstructor allows emptyarray. Equality runtimeSelf type→disabled→array, hasharray→disabled→type; defaultinittrap. FormAttribute dictionary/coding/derived/store follows audit ios-conversation-part/form-attribute/CONTRACT.md, preserving fieldorder160B/Any? semantics, name-onlyrequired/optionsdictionaryprecedence/typedcasts/no size clamps, originaloptional-vs-empty arrays. Securecoder intentionally excludesisFormDisabled and eq/hashsubsets excludevalue/flags as originallyspecified. Storage uses actualstandardUserDefaults/formErrorKey/formLocalValueKey; validation must not execute against hostdefaults. Original typed-ticket-field initializer remains unrestored until real TicketFormResponseFormField/Options source is recovered. Private input/storage enum case source spellings not proved; retained semantic tags, no privateSwiftABI claim.
TicketFormResponseListOption original own6method metadata70332 has no ObjCprotocols/no NSSecureCoding/hash/equality; source only NSObject+Codable three REQUIRED labelString/archivedBool/idString. Do not add archive support because a parent allowed-class set mentions it. InputProperties original42B stores initialValueAny? then allowSubmission/valueIsEditable, directtypedinitializer and defaulttrap, no addedvalidation.

FinAnswerSource originalfb824 optionalStringtitle defaultempty, optionalStringtype; parsesoptionalurlString via URL(string:) before typebranch. Only exact typearticle requires idString (emptyaccepted), otherwise failinitializer; other/missingtypes sourceType.url, noidvalidation. OriginalsourceTypeenum cases article(String)/url provedreflection406c40, fields title/sourceType/url. Wrapper realparsedvalue, defaultinittrap, noeq/hash override; intentType raw1 forarticle else0, intentArticleId optionalpayload, intentUrl actualurl. ITBIntent requiredintentType and optionalintentUrl/intentArticleId/messengerAppId exactmetadata; Wrapper does NOT implement optionalmessengerAppId. No synthetic URL/title defaults beyond originalemptytitle, no added URL filtering.

ReactionsOption/Set original35da0..36808 both concretefactory + directbulkKVC (no dictionary guard), SafeNetworkModel/readonlystrongivars; no extra initdefaults. Option unicode_emoticon unfilteredcamelKVC; image_url NSStringonly→NSURL(string)→camelKVC (invalidURL clears prior), unknownignored. Option equality exactclass+self then typednil/self/indexpointer-or-number→unicodepointer-or-string→URLpointer-or-equal, repeatedgetters retained; hash31*(31*index.hash+unicode.hash)+URL.hash. Set reaction_index unfilteredselectedIndexKVC; reaction_set outerarray only, iterateddictionaryelements factoryappend(no nilfilter), directmutable_options store; equality exactclass+self then typednil/self/optionspointer-or-array→indexpointer-or-number, hash31*options.hash+selectedIndex.hash. No readonly-to-copy conversion or validation added.

EventData original129240..129f14/fielddescriptor408234 eight fields: fiveoptionalstrings(eventDescriptionstored fifth), optionalIntmergedConversationId, optionalEventParticipant, optional[EventContentItem]. Codingkeysorder eventAsPlainText/status/customStateLabel/customStatePrefix/mergedConversationId/participant/eventContent/description (description rawkey for eventDescription). All decodeIfPresent and encodeIfPresent, no eq/hash override. isValidEvent plaintext Swift.count>0 OR eventContent nonnil (emptyarrayvalid). EventParticipant stores avatar/initial/label/type/participantId/groupbool, all5optionalCodablekeys avatar/initial/label/type/id; group startsfalse in both constructors, readwrite but omittedfromcodec. Noeq/hash/copy extras. Both instanceFrom use actualDictionaryDecoder/convertSnakeCase, defaultinittraps. EventContentItem requiredtextString/optionalWeight; Weight fiveStringrawcases regular/bold/semibold/medium/light, customStringdecoder unknown→regular, wrongtype throws, rawValueinitunknownnil. Data-only field/key/stringtable receipts node_modules/.cache/ios-conversation-part-recovery/event-{reflection,raw-strings}.json from originalMachO. No SwiftUI fonthelper or entireUIABI completion claim.

Frozen Part parser dependency milestone:106 Objective-C+61 Swift source units compile for device arm64 and simulator arm64/x86_64 at iOS15. Strict aggregate has38 genuine missing SDK classes, reduced from42 by actual Form/EventData/FinAnswerSourceWrapper/ReactionsSet source. Source SHA recheck has no drift. No fixture implementations or original SDK executable inputs. Actual parsing/coding source is present; runtime behavior and full product initialization/UI remain unverified. FormAttribute typed TicketFormResponseFormField initializer remains missing; Avatar typed initializer and Participant admin title entry remain missing. Evidence node_modules/.cache/ios-source-aggregate/receipt.json.

Frozen Conversation milestone:109 ObjC+61 Swift implementation units compile for all three iOS15 targets, inputSHA unchanged, no Swift .N duplicate runtime classes. Conversation85/85 ownselectors (84instance+1class) present. Strict link41 genuine missing SDK classes (38 before Conversation): its real parser exposes Ticket/UIFlags/ConversationEndedButton/FooterNotice while removing Conversation class gap. No diagnostic/fake model bodies, vendor input or SDK initialization used. Conversation init sets isInboundConversation YES (GOT4a9c00/offset13), read stays zero; conversationParts getter mutates matching ticket parts, exact-class equality contains nullable comparisons then uiFlags isEqual without nil shortcut and final header/teamIntro/specialNotice pointer comparisons. Copy preserves original omissions and deep-copies parts via NSArray copyItems.27 simple accessor pairs have per-method instruction SHA/GOT/offset/ownership proof in IOS_CONVERSATION_ACCESSOR_PROVENANCE.json; state/style/parts getter manually recovered. New behavior runtime unverified.

Public Intercom facade original45 ownclass methods restored as real source, originalpublic header retained with local source model imports, wrapper variants0..3 and seven exported notification globals restored from original0x42cd60..90 data pointers/CFStrings. The facade delegates to actual ICMBridge source; caller-main synchronous/background-main-async behavior is retained only at original gated methods, with direct provider/auth/token/unread/login-state/wrapper/logging methods. No global initialized flag or optimistic callback. Auth filtered copy validates only and original input dictionary is forwarded; login attributes read before IDs, valid ID+emptyattrs fails without email fallback. Update has actual type/nonempty validation and distinct original logs/errors. Rich push nilcontent logs/returns; nonIntercom invokes original contentHandler unguarded with originalcontent. This does not close downstream UI/services/push or establish authentication/session/runtime. Original Swift extension surface remains outstanding. Static source evidence audit implementation-baseline/ios-public-facade/{RN_CONTRACT.md,original.asm.txt,inventory.json,Intercom.original.h}; notification data extraction cache ios-public-facade/notification-data.json. Original header declarations/comments are retained; no original executable/method bytes used.

Original public Swift contract restored from original35-line arm64 .swiftinterface plus method bodies6ffc..867c: nested Content sixcases including ticket; typed proactive content array; optional Result<Void,Error> completions; callback and @MainActor async JWT providers; auth/token Result wrappers; three HelpCenter Result wrappers. All invoke actual public ObjC facade; no optimistic success/error substitution outside original wrappers. HelpCenter nonnilvalue wins over simultaneouserror, then typed ICMHelpCenterDataError, otherwise originalcode3 somethingWentWrong. Async provider answer resumes MainActor before completion; no filtering/cancellation added. Compiler import name __present(_:) matches current Clang importer for original presentContent: header. Aggregate Swift validation now uses original module-name Intercom, language-mode5; privateSwiftABI/compiler-identical layout not claimed. Real CocoaPods/static/dynamic public module consumer remains unverified, as do full SDK runtime/auth/nativeUI. Astra limited static branch review confirmed key/value validation ordering, HelpCenter priority and async MainActor return only.

Frozen public facade milestone:110 Objective-C+62 Swift actual source units compile on all three iOS15 targets, Swift module-name Intercom. SourceSHA unchanged/no duplicate Swift .N runtime classes. ObjC public facade45/45 selectors and7 original exported notification globals are present on alltargets; Swift public extension6 Contentcases+12classfunctions compiled with original Result/MainActor signatures. Strict link41 real SDK class gaps remain, no vendor/fixture input. ObjC/Swift consumer packaging, all public Swift ABI and real initialization/auth/UI behavior remain unverified. Source/selector evidence node_modules/.cache/ios-source-aggregate/receipt.json; key branch static review only.

Original block pipeline source slice: ICMBlockLoader f220..f3b4 special5types and original ITB fallback, ITBBlockUtils75314..75e38 all5methods, ITBBlock74768..752e4 all28instance+4classmethods, ITBBlockView76694..7688c all14instance+2classmethods. Concrete block/view subclasses remain true missing product dependencies, not generic replacements. SDK special constructors returningnil fall back to ITB; ITB19type routes returningnil/unknown fall back to actualParagraph only when text pointer nonnil. Upload preserves ignored isVideo call and repeatedfilename/dimensions getters. Sizing actual viewClass estimate and two-pass ceil; prepare counts callbacks under receiverClass synchronized lock and completes on last callback thread, with no oncefilter or addedmain dispatch. Original base updateAppearance/undefinedKVC/nilvalue have literal ret bodies, base type=-1/plainText+uniqueId=nil/view estimatedSizeZero are original base behavior, not newly introduced restoration placeholders. Their original addresses are annotated in source. Concrete views are not thereby restored. ITBBlock requires nonnilappearance plus dynamic class dictionaryvalidation, uses original per-Class property-list cache/whitelist, textnil->empty/alignmentempty->left, exact subset equality and six-field securecoding/copy; no hash override added. ITBBlockView preserves weak block/delegates, copyid, reuse removes existingconstraints and dealloc sets delegate nil. Special Swift block classref data decoded to TicketBlock/CodeBlock/TableBlock (cache ios-block-recovery/special-classrefs.json); no original executable bytes used. New slice runtime/nativeUI unverified.

Frozen Blocks base/routing milestone:114 ObjC+62 Swift source units compile all three iOS15 targets (module Intercom), sourceSHA unchanged/no Swift .N runtime duplicates. Loader1/1, Utils5/5, Block32/32, baseView16/16 original ownselector symbols present. Strict link63 actualSDK classes: original routing now exposes24 genuine specialized block classes while two prior loader/util classgaps are removed. No concrete block/view replacement returned; abstract base original ret/nil/zero behaviors explicitly documented from original instructions. Complete SDK remains NOT_RESTORED and native rendering/consumer/session acceptance unverified.

Base/Admin appearance actualsource: ICMBlockAppearance45 originalivars/488B/94ownselectors,44simple accessor pairs verified against exact instruction patterns/offsets/ownership before standard @synthesize (IOS_APPEARANCE_ACCESSOR_PROVENANCE.json). appPrimaryColor getter intentionallyignores backingivar andreads currentTheme.primaryText; init snapshotscurrentTheme for7syntaxcolors then realInlineCodeChipLayoutManager.horizontalPadding; linkinitials retains originaltrim/UTF16firstunit/uppercase and trueICMAssets avatar helper call. Equality preserves43of45fields in originalorder:20colors comparedvia actualICMColor.hexStringFromColor/stringequality,5nullablefonts,5insets+size andscalars/nullablefollowstrings; excludesbothimageobjects, doesnotcompareallproperties. OriginalNSObjectisKindOfbaseguard/selfshortcut; Insetscomponentorderleft/top/right/bottom, NaNnot equal. Admin init a584..ab54 invokesactualTheme/Utilsfont/Assets/Translation, exactfonts/insets/padding/defaults/order with repeatedTheme.sharedreads. OriginalSwiftAI+Part constructors cb2c4/cb3c8 restored withtrueObjCsuperclasses/properties; Personal remainsunrestored until trueTheme/ColorHelperSwiftdependency is present. Theme/InlineLayout/Assets.spinnerImage+defaultAvatar/Utils.fontOfSize are honest class/selector dependencies, no defaultcolors/emptyimages/successstubs supplied. This is source/devicecompile evidence only; liveappearance/nativeUI/runtime/fullprivateSwiftABI unverified.

Frozen appearance base milestone:116 ObjC+63 Swift implementation units compile on all three iOS15targets, sourceSHA recheckmatches/no duplicateSwift.N classes. BlockAppearance94/94 andAdmin1/1 ownselectors present. Strictlink62 actualSDK classgaps (twoSwift appearance classgaps removed, actualInlineCodeChipLayoutManager dependency newlyrequired); Personal/Theme/concreteblocks/realUI staymissing. Assets.defaultAvatar/spinner andUtilsfont are additional genuine missingselectors on otherwisepresent classes, not hiddenby classcount. No runtime/nativeUI/consumeracceptance.

Appearance runtime dependencies expanded with actual original font helpers c80cc..c81f8 (UIFont respondsToSelector branch; weights1..5 map Bold/Medium/Light/Thin/Semibold, otherwiseRegular; fallback HelveticaNeue suffix literal table42ec18; no font substitution beyond original). ICMAssets all99 originalclassmethod source entries now present across primitives/images/avatars, not a class-success placeholder:84 simple constant getter/boolean-cap/sound routes strictly matched against instruction patterns and data CFStrings before transcription (IOS_ASSET_GETTER_PROVENANCE.json); copy/system/tint/url helpers manually recovered. Sixboolean images use actualcapInsets(4,4,0,4), sound getters explicitlycallconcreteICMAssets and genuineSDKbundle path sound/%@.caf; missing bundle/file originalUTF16 logs recovered from CFdata, no truncation. Avatar methods c34f0..c40dc use trueImageLoader generationcache/Theme/ColorHelper/UIFont/UIBezierPath/UIGraphics; defaultavatar cachekey deliberatelydoesnotvarybyactionflag and100ptcanvas ignores requesteddiameter wheninitials absent. Initials cachekey %.0f plusflag, fill/text/0.2blackoverlay retainsoriginalorder; defaultopaquecontext draws original16 path commands from decoded literalcontrolpoints (cache avatar-path-data.json includes binarySHA/source/datareads), then miter4/evenOddfill. No copied executable instructions/assets substitutes or new validation; missingTheme/ColorHelper remainactualclassdeps. No drawing/cache/audio/runtime validation has executed. Aggregate inputSHA inventory now includes ios-root publicheaders in addition to privateheaders.

Assets full source expansion supersedes prior subset:99originalclassmethod bodies are now present;84 literal getter/boolean-resizable/sound-route methods have exactpattern/data provenance (IOS_ASSET_GETTER_PROVENANCE.json),remaining15 manually recovered acrossprim/avatars/images sources. Allliteralnames and originalsoundmissinglogs decodedfrom MachO data, preserving nil/unknown/nativetemplate semantics. No dynamicreceiver or resourcefallback change beyond approvedexactAssetManagerreceiver bundleroute; soundconcrete ICMAssets dispatch preserved. All new files devicearm64 strictcompilepass; latestaggregate must include the full threeassetunits. CoreTheme/ColorHelper stillmissing, drawing/audio/resourcesconsumer behavior not tested. Aggregate remains truthful about classes and missingselectors, not a usablefullSDK.

Frozen Assets milestone:119 ObjC+63 Swift source units compile across all three iOS15 targets. All99 original ICMAssets own class selectors are present; source SHA recheck unchanged, no duplicate Swift .N runtime classes. Strict aggregate retains63 genuine missing SDK classes including ThemeProvider/ColorHelper and specialized blocks/native UI. No original executable, diagnostic fixture or substitute renderer in source input. Drawing/audio/normal Pod resource consumption and whole SDK remain unverified.

Frozen Theme milestone:119 ObjC+66 Swift implementation units compile on all three iOS15 targets. SHA recheck unchanged; no Swift .N duplicate runtime classes. Strict link60 genuine SDK class gaps (63 before genuine DefaultThemeProvider, ColorHelper and Personal appearance). Theme70 stored fields/58 separately initialized palette constants/11grey constants/10computed color entries preserve actual RGBA data, rawstyle2 dark branch, field aliases and distinct equal-color identities; no current-trait pre-resolution. Theme init/default trap and Provider initial two systemBlue reads→Config refresh→notification observer order preserved, no added observer deinit behavior. Provider holds actual ICMConfigProtocol (original24 required selectors/12properties); titleForSpace signature corrected to original signed q encoding. Clang warnings are locally scoped for four real protocol implementations located in the existing ImagesAndUI source category. Alias-to-real Swift ConfigModules conversion preserves actual nested model instances, without fixture model or substitute palette.
ColorHelper true two-candidate contrast selects original textAlwaysLight/textAlwaysDark, strict greater winner (ties first); getRed failure behavior differs intentionally between luminance getter and contrast helper. Dynamic helper eagerly resolves light/dark then computes each foreground before creating dynamic provider. Original threshold4.51, HSB factor clamp comparisons preserving NaN and failed getHue returns same object retained. UIColor isWhite/isBlack strict240/25 comparisons restored. Personal reads Theme/background twice for text/link and holds actual provider before super. Source/static checks only; private Swift ABI, color rendering/trait updates and normal consumer remain unverified. UIColor.init(hex:) extension remains unrestored and is not used by this restored path. This implementation stays within approved original contract; SPEC needs no behavior change.

Frozen InlineCode milestone:119 ObjC+67 Swift source units compile on all three iOS15 targets; strict link59 true SDK class gaps, SHA unchanged/no duplicate Swift.N runtime classes. Actual NSLayoutManager subclass restores drawBackground supercall then firstcontainer/ICMInlineCodeBackground color enumeration, glyph/character conversions, whole-storage deepest font descent, per-line intersection and trailing UTF16 whitespace/newline trimming; actual bounds/baseline/ascender/descender calculation with4pt horizontal padding,5pt vertical padding,2.5pt baseline offset and6pt native rounded drawing. Original signed/IEEE branch directions retained including NaN skips. Init/coder call actual superclass; no stored fields or new renderer. Astra finite geometry/trim/descent static review found no concrete mismatch. No runtime drawing/native text/normal consumer acceptance; completeSDK remains NOT_RESTORED.

Frozen TextBlock milestone:123 ObjC+67 Swift source units compile all3 iOS15 targets, source SHA unchanged/no Swift.N duplicate classes. Selected ownselector coverage TextBlock44/44, Paragraph2/2, HTMLParser3/3, InlineCitation12/12, CitationParser1/1. Strict59 true SDK class gaps: actual Paragraph replaced with new genuine TextBlockView dependency.11 accessor pairs+paragraph setter and4Citation pairs matched exact simple instructions/GOT/offset/ownership (31accessors, IOS_TEXT_ACCESSOR_PROVENANCE.json); originalParagraph heightMultiple1.1 from data3c2578. Text update directivar order/old attributedString required-marker read, original inline kern boundary/index sets, plainText four getters/options-1/linkkeypointer, nullableother-receiver equality and copy/coder tenfields omissions retained. No substitute stripHTML/importer: actual systemlibxml2 uses original HTML0x864 versus Citation0x61 parseflags, prefix-tag comparisons, unknown subtree dropping,codeattrs replacement,stringhref/three fontDescriptor reads; XPath substringclass andfirst-sourceIndex Foundationpredicate dedupe. Original XPath early-result cleanup omission retained. New parser/UI behavior remains runtimeunverified; normalPodlibxml2 header/link config is designpending, notconsumerpassed. SPEC unchanged originalcontract.

Frozen native text view milestone:125 ObjC+67 Swift source units compile all3 iOS15targets, SHA unchanged/no duplicateSwift.N runtime classes; strict58 real SDK class gaps. Hyperlink5/5, TextBlockView15/15, Intent4/4 ownselectors present. ITBIntent original ObjCprotocol (requiredintentType+3optionalmethods/NSObject) now has one shared Clang definition used by both original ObjCintent and SwiftFinAnswer wrapper, avoiding parallel same-named protocols. ITBIntentOpenerDelegate definition absent from surviving metadata; only forward declaration plus call-site-inferred NSObject method signature retained, no fabricated runtime protocol/requiredness.
ActualUITextView lifecycle preserves repeated gesture install, no gesture-state gate, caret end fallback, original unsigned range bounds, dead-work red substring beforelinkdispatch, NSURL/NSString URL conversion and repeateddelegate reads/default open. Original native measure uses one unsynchronized concrete global view, lazysetup/teardown,39pt emptyheight, textInset/layoutMargin/padding, sizeThatFits and float→ceilf→CGFloat, originalconstraintWidth branch. Actual layout adds four activeconstraints everylayout with no supercall, configure original property order, intent forwards real opener and returnsNO. No URL was opened and no rendering/click/normal consumer runtime was executed.
Eight real Textcomponent implementation units additionally strict-linked independently on devicearm64/simarm64/x86_64 without any diagnostic/fake/vendor code, only actualFoundation/UIKit/systemxml2. Receipt ios-text-block-recovery/strict-text-link-receipt.json has exact commands/inputSHA/linkedimages. This establishes only that original caller-supplied-appearance Textcomponent links; full source SDK remains unlinked/NOT_RESTORED and authentic native chat/UI acceptance unverified.

Frozen text variants milestone:128 ObjC+67 Swift units compile allthree iOS15 targets, sourceSHA unchanged/no Swift.N duplicates, strict53 remaining true SDK class gaps. Real Heading/SubHeading3+3methods use own appearance font/color even when supplied different font/color, type1/2; ITBCode3methods type9 setsactual codebackground/insets after superclass in init/update, not the stillmissing specializedSwift CodeBlock. List13methods+Ordered2+Unordered2 preserve ITBBlock inheritance, repeateditems.count, bullet-before-item formatting, nonfirstnewline, concreteITBTextBlock creation then realappearance/no type/nilfilter, two font-size reads/tabNatural1.5 and10ptleft, actual deepcopy/securecoding. AbstractList original8bc10 nilbullet literal is preserved, not a restoration fallback; realbulletvalues original%lu(index+1 wraps) anddecoded UTF16•(CF44ce10). All variant ownselectors present. The actual11unit textcomponent (now including variants) strict-links all3targets withonlyFoundation/UIKit/systemxml2, no vendor/fixture. Receipt strict-text-link-receipt.json supersedes8unit snapshot retained separately. Nativepixel/layout/linkinteraction/list-expansion/runtime/normalPodconsumer remainunverified, wholeSDK remainsNOT_RESTORED.

Frozen button/social milestone: 131 ObjC + 67 Swift source units compile for iOS15 device arm64 and simulator arm64/x86_64; all frozen input SHA values unchanged and no Swift .N duplicate runtime class symbols. Full aggregate strict link still fails on 50 genuine missing SDK classes. Selected own selectors: Button22/22, ButtonView13/13, Facebook4/4, Twitter4/4. Seven strict accessor pairs match original ivar/GOT/ownership/instructions (IOS_BUTTON_ACCESSOR_PROVENANCE.json). Original Button validation deliberately tests literal @"text" after dictionary linkUrl, not dictionary[text]; nullable equality/copy/coding preserve original scope. Actual native UIButtonConfiguration uses filled configuration, insets5/corner4, strong block capture in update handler, direct self subview ownership, four estimatedSize getter reads in layout, float-ceilf measurement and original 44+16 minimum; no replacement link control. Social models preserve original raw URL interpolation, repeated username/text reads and dictionary literal failure behavior. Fourteen genuine implementation units strictly link on all three targets using Foundation/UIKit/systemxml2, no vendor or diagnostic implementations: scripts/ios-link-blocks-component.py, node_modules/.cache/ios-blocks-component/receipt.json. No runtime initialization, rendering, taps, upload/network or normal Pod consumer was run. Original compatibility SPEC unchanged; full SDK remains NOT_RESTORED.

Frozen ImageBlock/ImageBlockView milestone:133 ObjC+67 Swift units compile allthree iOS15 targets with unchanged inputSHA/no Swift.N class duplicates. Image model selected42/42 ownselectors; ImageView device strict compilation and finite independent static key-branch review completed (its selector inventory added in next batch). Full aggregate remains SOURCE_AGGREGATE_UNLINKED with55 true missing SDK classes: replacing ImageBlock with realImageView exposes AnimatedImageView/LoadingView/UploadProgressButton/ImagePreviewMetadata/ImageUtils and existingUpload dependencies; declarations do not count as restored implementations. Image model retains original CGSizeZero reset after superclass KVC, zero dimensions400/600, scale/order, shallowcopy fields/floatencoding, fixed baseclass equality;24strict accessor instructions/GOT/offsets proven in IOS_IMAGE_ACCESSOR_PROVENANCE.json. Real view preserves original anchors/priorities/400fallback, beforestrong/completeweak load callbacks, animatedcover differences, preview/reload repeated getters, progressfade and cleanup/reuse ordering. No image loading, render, upload, playback, tap, initialization or normalconsumer ran. SPEC originalbaseline unchanged; fullSDK NOT_RESTORED.

Frozen image pipeline UI milestone:138 ObjC+67 Swift units compile allthree iOS15 targets; inputSHA unchanged/no Swift.N classduplicates. Selected ownselectors present in alltargets: ImageBlock42, ImageBlockView31, AnimatedImageView28, weakDisplayLinkProxy3, PreviewMetadata9, ImageUtils4, LoadingView20, UploadProgressButton8. Full aggregate strictlink remains UNLINKED with51 trueSDK class gaps; image-specific actual gaps are ITBUpload and Swift ShimmeringView (not replaced by declarations). Real AnimatedImageView routes cache-hit main-thread synchronous assignment, before-network callback before cancel, current-load token pointer identity, weak completion, original transition0.30000001192092896; actual CADisplayLink proxy/play-head/strict duration wrap/loop limit/frame-failure pause and visibility hooks retained. Loading constructs original shimmer dependency with0.1/0.1/1/0.2/0.99 settings, original dark0.4alpha0.15/light1alpha0.15, failure asset/layout, no fake shimmer implementation. Native progress button actual CAShapeLayer circle and fixed-coordinate cancel drawing/cache key, unclamped progress/didChangeValueForKey endValue retained. Preview model ownership/Utils four methods preserve original CoreGraphics grayscale/mask/context/scale/orientation and GIF prefix/CGRectIntegral scaling. IOS_IMAGE_ACCESSOR_PROVENANCE.json proves57simple accessors by exact instructions/ownership/offsets; imagePreviewerActionBlock only @? copy encoding proven, precise private block signature remains inferred. No rendering/playback/download/upload/previewtap/normalconsumer runtime ran, no vendor/fixture inputs. SPEC unchanged approvedoriginalbaseline; wholeSDK NOT_RESTORED.

Frozen Upload milestone:139 ObjC+67 Swift units compile allthree iOS15 targets/SHAunchanged/no Swift.N duplicate classes; Upload51/51 original ownselectors present. Strictlink remains51 genuine missing SDK classes, replacing ITBUpload gap with real ITBImageFileLoader dependency, plus stillmissing ShimmeringView. Native upload model now preserves UTType/file validation/image/video routing, rawfile/video AVAsset dimensions/duration, EXIF three date fields, cachekey preview/cache ownership, weak delegates/weak NSURLSessionDataTask cancellation, fmin/fmax progress clamp and original delayed mainqueue progress scheduling. Original setUploadState deliberately never stores ivar; raw0 setsprogress1 then calls failure, raw1 progress0, raw2 failure, others do nothing. Required system AVFoundation/UniformTypeIdentifiers added to aggregate link command; no consumer config changed. Existing declaration category signed state changed to actual original NSUInteger header. No file/media/upload runtime executed, decoder still no invented NSSecureCoding on Upload. SPEC unchanged; SDKNOT_RESTORED.

Frozen ImageFileLoader milestone:140 ObjC+67 Swift units compile allthree iOS15targets/SHAunchanged/no Swift.N classduplicates; FileLoader15/15 ownselectors present. Strictlink now50 real SDK missingclasses, image chain only ShimmeringView absent. Real loader preserves CGImageSource/cache options/CFNumberInt property read, screenWIDTH fallback for both dimensions, exact orientation3..8 degree table, CreateImageAtIndex despite thumbnail options, JPEG destination defaults/finalize log and original rotation/context lifecycle. Rotation retains original second space release0x910b8 and source-image replacement ownership; no hardening/cleanup redesign applied. File/image/runtime behavior not executed, no product initialization/network. SPEC unchanged, wholeSDKNOT_RESTORED.

Frozen complete image-dependency source milestone:140 ObjC+68 Swift units compile allthree iOS15targets/SHAunchanged/no Swift.N runtimeclassduplicates. Strict fullSDK link still fails on49 genuine remaining SDK classes. ShimmeringView/real CAGradientLayer mask/weak CAAnimationDelegate now implemented in actualSwift with original runtime aliases/defaults/property sideeffects/layout geometry/shared NSNumber/.copy beginTime and repeat semantics/stop-current-cycle fmod then fade lifecycle; Mask coder and delegate parameterlessinit retain original traps, View coder supported. Independent limited static review by Astra found no concrete deviation in selected shimmer lifecycle/geometry/layer-copy branches; no runtime/wholeprivateSwiftABI claim. IOS_IMAGE_ACCESSOR_PROVENANCE now94exact simpleaccessors across image/nativeview/upload/fileloader (all actual model/custom setters handled separately). Seven true ObjC plus six trueSwift implementation units strictly link on devicearm64/simarm64/x86_64 with only system/upstream Swift dependencies, no vendor/fixture. Receipt node_modules/.cache/ios-image-component/receipt.json and script ios-link-image-component.py record exact commands/inputSHA/objectSHA/artifactSHA/linkedimages. Scope includes cache/downloader/decoder/nativeAnimatedImageView+weakproxy/ImageLoader/ImageUtils/PreviewMetadata/Upload/FileLoader/ProgressButton/shimmer; excludes ImageBlockView/LoadingView because the genuine Blocks factory still references other unrestored renderer classes. No sourceSDK/UI runtime, real account/network/fileupload/rendering/playback/normalconsumer acceptance ran. SPEC original baseline unchanged; wholeSDK NOT_RESTORED.

Frozen attachment/native component milestone:143 ObjC+68 Swift units compile allthree iOS15 targets/SHAunchanged/no Swift.N duplicate runtimeclasses; fullSDK strictlink remains47 genuine missingclasses. Attachment20/List13/nativeTextAttachmentView8/Icon5/ListView7/ReuseManager8 ownselectors allpresent. IOS_ATTACHMENT_ACCESSOR_PROVENANCE proves17simpleaccessors/offsets/ownership. Actual Attachment init retains original concretebase-validation then conditionaldynamicclass literal-url validation, raw HTML interpolation/name fallback, directretained URL vs later copysetter, clipColor/leftmargin, fixed12leftinset/natural+truncatingtail style, nullableotherreceiver equality, original securedecode/deepListcopy. Real Icon suppliedrect maxXY nine-step stroke with exact original3arc doubledata, newcolor-receiver equality/nilnilinvalidate; original globalRTL/floorf frame and accessibility preserved. List exact dynamicviewClass estimate/threeestimatedSize reads/strictmax and remove-enqueue/dequeue-configure-add behavior retained. Real ReuseManager has concreteonce/shared dict/max5 perclass, no invented lock/pool policy; dequeue identifier-first thenlast/removeObject/prepare, enqueue existingbucket withidentifierdedupe, memorywarning prepares cachedviews withoutclearing, tearDown conditionalText teardown thenclear, original observer cleanup. Limited independent static review found no concrete deviation in AttachmentView/List/Icon/modelinit; ReuseManager has source-level originalinstructions review only, no runtime claim. Twenty-four genuineObjC plus six genuineSwift units strict-link allthree targets using only actual recovered source/system libraries; receipt node_modules/.cache/ios-native-blocks-component/receipt.json (old14unit receipts retained separately). The independent image13unit gate was rerun against current aggregate/inputSHA and passed. Fullfactory/ImageBlockView/LoadingView/fullmessenger still excluded from linkedcomponent; native render/reuse/click/file/network/upload/playback and normalconsumer remain NOT_RUN. Approvedoriginal SPEC unchanged; wholeSDKNOT_RESTORED.


Frozen Link/native-view milestone:151 Objective-C+68 Swift implementation units compile for iOS15 device arm64 and simulator arm64/x86_64 with unchanged frozen source SHA and no duplicate Swift .N ObjC runtime classes. Full aggregate strict link remains UNLINKED on45 real SDK classes (47 before this batch). Eleven actual Link classes provide239/239 original own selectors: LinkBlock45, Author23, Image12, LinkList39, plainLink14, CardView24, ListView16, AuthorView13, Item20, Footer17, Container16. IOS_LINK_ACCESSOR_PROVENANCE.json records139 trivial accessor instruction SHA/metadata ownership/offset/GOT checks; custom methods remain separately reconstructed, not generated by field-name assumptions.

Original Link data validation accumulates across all dictionary enumeration batches, checks each key before fetching its value, and continues after invalid entries. Author/Image inherit ITBBlock; value/copy semantics retain original exclusions, repeated getters, nullable other-receiver comparisons and raw double comparisons. PlainLink retains raw dictionary objects during init despite later copy setters, computed intent getter ignores its stored setter ivar, and LinkList allows empty NSString URL/title values as originally validated. Real Link Card/List/Author/Item/Footer/Container UIKit views preserve global layout direction, native attributed-text measurement, original noncontinuous default/max heights, original repeated constraint activation and selectedLink-before-delegate routing. Author loads the same avatar twice through actual AnimatedImageView (first weak-self/strong-author before callback and options0x500000; then nil before/completion), keeps original radius/addition/evaluation order, and has no substitute avatar UI. FooterContainer uses real list icon/text and original intrinsic height sum. Limited independent original static contracts/reviews cover stated branches, not runtime/layout or whole-class ABI acceptance.

Thirty-two actual Objective-C plus six actual Swift source units now strictly link for allthree targets with only system/upstream Swift libraries; node_modules/.cache/ios-native-link-blocks-component/receipt.json records commands/object/artifact SHA/linked images, and prior30unit artifacts remain in ios-native-blocks-component. The image13unit source gate was rerun against this frozen aggregate and passed. This is a source component with caller-supplied appearance, not complete factory/SDK/UI: ImageBlockView/LoadingView still depend on the incomplete original Blocks factory, and messenger initialization/UI destinations remain missing. No native rendering/taps/AutoLayout/avatar download or normal static/dynamic Pod consumer was run. No vendor SDK executable/object input, fixture implementation, security redesign or simplified UI was added; approved original SPEC remains unchanged and fullSDK stays NOT_RESTORED.


Frozen original appearance/typing milestone:154 Objective-C+68 Swift implementation units compile on device arm64 and simulator arm64/x86_64 with iOS15 deployment target, frozen SHA unchanged and no duplicate Swift .N runtime classes. Full SDK strict link still fails on38 real missing SDK classes, reduced from45 by six real appearance classes and Typing model/native dependency chain. Selected ownselectors allpresent: LinkCardAppearance4/LinkListAppearance1/ContainerCard1/InApp1/MessengerCard1/Post1, TypingBlock8/BlockView7/DotView8/TypingView9 (41 methods total). Six linear appearance constructors preserve all88 original setter calls in order, fresh theme/translation reads, exact promoted float literals/fonts/insets/default-vs-unset fields; IOS_LINK_APPEARANCE_PROVENANCE.json records original parents/no-own-property metadata, Card avatarRadius ivar and originalmethod SHA. Post code background intentionally uses textAlwaysLight, and it does not assign singleImagePadding. These are real source definitions, not success-only appearance placeholders; whole graph remains UNLINKED.

Original Typing uses actual native UIView circles and CAAnimationGroup scale/opacity animations, original float NSNumber0.7/1/0.4, duration0.4000000059604645, float index-times0.2 delay, CACurrentMediaTime beginTime, infinite repeat/autoreverse and original delegate performSelector without adding CAAnimationDelegate conformance absent from metadata. Dot init starts immediately; window changes start/stop dots, stop removes all animations, cleanup stops then clears group, stopped callback restarts only when window exists regardless of finished flag. Original fixed70x44 estimate, positioning/getter order, raw appearance dotColor and equality/copy semantics preserved. IOS_TYPING_ACCESSOR_PROVENANCE.json proves10 trivial accessors plus original data literal precision/addresses. No CA/UIView runtime or physical/native rendering acceptance was performed.

Thirty-three actual Objective-C plus six actual Swift units strictly link across allthree targets without original SDK or diagnostic fixtures; node_modules/.cache/ios-native-typing-blocks-component/receipt.json stores exact inputs/commands/object/artifact SHA/linked images. The actual image13unit gate was rerun against frozen154aggregate. Prior38/30/14unit native component artifacts and151aggregate snapshot are retained separately;154snapshot is cache ios-link-block-recovery/frozen-154-aggregate-receipt.json. Component callers supply appearance; newly restored appearance classes participate in the full source aggregate, not a falsely complete isolated theme/UI product. Full factory, remaining native destinations, UIKit behavior, real networking/accounts and normal Pod/static/dynamic consumers remain unverified. Approved original SPEC unchanged; no hardening/substitute UI/vendor-object input; fullSDK remains NOT_RESTORED.


Frozen NotificationCard milestone:156 Objective-C+68 Swift implementation units compile on device arm64 and simulator arm64/x86_64 at iOS15; frozen SHA unchanged, no duplicate Swift .N runtime classes. Full aggregate strict link remains UNLINKED on37 real SDK class gaps (38 before this batch), not a complete inventory of all unrestored SDK behavior. Three actual classes provide all67 original ownselectors (Card24/Channel17/View26); IOS_NOTIFICATION_ACCESSOR_PROVENANCE.json proves40 metadata-backed simpleaccessors including copy/strong/BOOL/insets ownership and original offset variables/instruction SHA.

Card original type13; KVC accepts only nonempty text/title, channels method takes entire dictionary, resets placeholder/defaultemail, parses concrete Channel with nil filtering, sets default using original array indexOfObject rather than enumeration ordinal, and takes first valid nonempty value while still validating later channel values. Validation checks only first original channel plus nonempty text/title. Both models update attributed content using real caller appearance without super update or nil-font/title substitutes. Card equality compares channels/userInput/userInputAttributedString.string/placeholder after superclass; copy omits title. Channel equality compares name/label/value/labelAttributedString.string and ignores defaultChannel; copy also omits defaultChannel. Neither adds original-absent securecoding overrides or deeper array copies.

Actual native NotificationCardView uses UIView/UILabel/CALayer, not new text-input or web UI. Original measured-title maxY, nonintegral layout versus integral estimate,44/19 actual input heights versus54/29 estimate increments and conditional35 channels height retained. Layout repeatedly appends channel labels/underline, offset accumulates raw-frame advance+25 across all enum batches, first underline is offset==0, attributesAtIndex0 remains unguarded, placeholder false does not reset prior border. Constrained estimate still measures hidden channels with ordered comparisons and original FMA/count-underflow behavior. No original-absent prepareForReuse cleanup, interaction or validation was added. Astra limited View static contract review found no concrete deviation; model/complete UIKit runtime/ABI remain unverified.

Thirty-five actual Objective-C+six actual Swift units strictly link on allthree targets with only system/upstream Swift libraries; node_modules/.cache/ios-native-notification-blocks-component/receipt.json includes exact commands/input/object/artifact SHA/linked images and no vendor/diagnostic implementation. Actual image13unit gate rerun against frozen156aggregate passed; previous39/38/30/14unit artifacts remain separately retained.156aggregate snapshot at cache ios-notification-card-recovery/frozen-156-aggregate-receipt.json. Component caller supplies appearance; fullfactory/native controllers/normal static or dynamic Pod consumer, SDK startup/auth/networking and UIKit rendering/interaction remain NOT_RUN or unlinked. Approved original SPEC unchanged, fullSDK NOT_RESTORED.


Frozen original messenger-fallback card milestone:163 Objective-C+68 Swift implementation units compile across iOS15 device arm64 and simulator arm64/x86_64 with unchanged frozen input SHA/no duplicate Swift .N runtime classes. Full aggregate strict link remains UNLINKED on36 real SDK class gaps (37 before batch), not the total remaining functionality inventory. All167 selected ownselectors present across7 actual classes: ICPCardFallbackModel24, ITBMessengerCardFallbackBlock29, ITB wrapperView46, ICPTextInputPayload16, JSBuilder4, ICPLoadingView8, ICP native WK component40. IOS_MESSENGER_CARD_ACCESSOR_PROVENANCE.json proves83 simple accessors including original weak delegate load/store/ownership and exact JS/numeric data SHA; custom cardId getter excluded from trivial proof. Four real private protocols restored from original metadata (three ITB intents inheriting ITBIntent plus NSObject-based ICPAppComponentDelegate five required methods); completion parameter UIAlertController is inferred from original callback textFields access, not proved by its opaque @? encoding alone.

ITB model preserves signed integer dimensions/direct appearance stores, URL parsing, nullable-other unique-id equality, original copy/coder ordering and real UIEdgeInsets archive methods. ICP model preserves direct fields/weak-independent data state, computed cardId ignoring stored setter, first card_id query item via NSURLComponents resolvingYES/NSPredicate, no URL hash fallback, exact-class equality and direct cardId comparison (two distinct nil-id models compareNO). Payload uses5 strong strings/pointer-or-string equality, completion ignored and hash title only. ITB wrapper preserves original366 width cap/150 default cachedheight, integer storage then NSNumber floatValue measurement, component-frame ownership, reuse removes/nils component before super, raw intent types0/3/4/5 and direct native delegate routing, typo sheetParamaters, and original getter forwarding. Original body rather than machine code/object payload is implemented.

Real card component uses UIKit/WKWebView/default WK configuration, four exact original JS strings and original documentEnd/mainFrame insertion order, same handler registration, parse-before-name check and five original actions. Current weak-self/current-payload completion reads UIAlertController text fields; UPDATE_TEXT retains original alphanumeric percent encoding/raw string formatting; host-only navigation comparison and original error/type behavior remain unchanged. Heights retain no top subtraction, original cache/defaults and reload order; spinner rotates by original double pi with promoted float duration. Original dealloc stops webView loading then clears delegates/removes/nils it; removeFromSuperview calls super before removing handler. Limited independent static review caught and corrected an initial wrong spinner-stop interpretation before final aggregate; selected JS/Loading/Payload/component-layout/action/teardown branches then showed no concrete discrepancy. No WK/UIKit runtime, web navigation/JS interaction/cookie behavior, native alert completion or full private ABI acceptance was run.

Forty-two actual Objective-C+six actual Swift units strictly link on allthree targets using only system/upstream Swift libraries, including original system WebKit now explicit in the iOS-specific link scripts. Receipt node_modules/.cache/ios-native-messenger-card-component/receipt.json stores commands/source/object/artifact SHA/linked images, no original framework/object or diagnostic implementations. Actual image13unit gate rerun against163aggregate passed; all prior41/39/38/30/14unit native component artifacts retained.163snapshot is cache ios-messenger-fallback-recovery/frozen-163-aggregate-receipt.json. The normal Pod system-framework declaration remains coordinated with root-config owner for later consumer build. This is the original card-local WKWebView, while original native messenger destinations/factory remain unfinished. SDK startup/auth/real network, normal static/dynamic consumer and full nativeUI acceptance remain NOT_RUN/unlinked. Original compatibility SPEC unchanged, hardeningApplied=false, fullSDK NOT_RESTORED.

Frozen original ImageText milestone:164 Objective-C+70 Swift units compile for iOS15 device arm64 and simulator arm64/x86_64. Original model provides25/25 own selectors (earlier progress called24 before counting the full extracted inventory);14 simple accessors have original instruction/metadata ownership/offset proof in IOS_IMAGE_TEXT_ACCESSOR_PROVENANCE.json. Full SDK strict link remains UNLINKED on35 real SDK classes; no source SHA drift or duplicate Swift .N ObjC runtime symbols. Snapshot node_modules/.cache/ios-image-text-recovery/frozen-164-aggregate-receipt.json records full commands/input SHA and a focused Swift-only recheck after unchanged Objective-C compilation.

Model preserves raw type20, constructor direct retention of title/imageUrl despite later copy setters, floatValue-promoted dimensions, no-super appearance update, superclass/text/url/numeric admission order, nullable self-first equality without superclass/loadingViewColorType comparison, copy including inherited text and original float securecoding. Actual Swift ImageTextBlockView is UIView-based; it uses genuine AnimatedImageView/ShimmeringView, aspectFit, fixed model size, optional title/native labels, original four stack-edge constraints/trailing-24, weak load callbacks, plain nil-to-empty Swift string bridges,0.3 success animation, and repeated original failure icon constraints without cleanup. No invented model.viewClass routing was added: original consumer is a BlockViewFactory inlined initializer, and the factory/full conversation route is still unrestored. ConversationCellContentView descriptor proves UIView constraint/ContentType associated type/read-only getter; content getter spelling is inferred from original conformers, and its original optimized-away witness is not claimed as recovered callable private ABI.

New Swift usage exposed the Objective-C AnimatedImage alias colliding with the local Swift implementation. The declaration now carries standard Swift-generated-header metadata (swift_name AnimatedImage; external_source_symbol Swift/Intercom/generated_declaration), allowing the compiler to resolve the true local implementation without introducing a duplicate class or fallback object. Original ObjC runtime class identity stays ICMAnimatedImage. Limited independent static review identified nil-to-empty text/URL bridge details before final freeze; these were corrected and all three Swift WMO compilations/strict link attempts rerun. No UIKit rendering/image load/App/defaults/network, normal static/dynamic Pod consumer or whole SDK runtime was run. FullSDK remains NOT_RESTORED; no vendor framework/object, fake successful dependency or hardened behavior was added.

Frozen original Rating/HyperLink/Translation milestone:165 Objective-C+74 Swift implementation units compile for iOS15 device arm64 and simulator arm64/x86_64, all source SHA unchanged and no duplicate Swift .N runtime classes. Full strict aggregate remains UNLINKED on35 genuine SDK class dependencies, now including ICMLinkOpener; replacing a missing model class with its actual native View source does not imply the remaining product path is linked. Four Rating classes provide40/40 original ownselectors (Block21/Option7/RatingIntent6/RemarkIntent6), with20 original metadata/instruction-backed simpleaccessors in IOS_RATING_ACCESSOR_PROVENANCE.json. Source snapshot node_modules/.cache/ios-rating-block-recovery/frozen-165-aggregate-receipt.json records commands and actual remaining class names.

Rating model retains rawtype15, direct constructor stores despite later copy setters, original camel-index precedence over snake index, selectedRating enumeration, no-super admission/equality and original options/remark equality exclusions. Options use real index/emoji/unicode and original sticker URL concatenation, no substitute image; both actual NSObject ITBIntent classes use raw6 and strong payload. Real Swift Rating View preserves four optional UI states, native stacks/31point AnimatedImageViews, long-term loader versus default grayscale cache, selected-nil accessibility wording, original scale/alpha and callback capture distinctions, strong alert/delegate snapshot behavior, no direct rating-model mutation, original state-dependent native text and UI constraints. ConversationCellBlockDelegate is explicitly PARTIAL: only3 of36 original requirements are declared; no absent requirement has a fake/default implementation. Full protocol ABI/consumer remains unrestored along with actual BlockViewFactory integration.

Actual Swift HyperLinkTextView is distinct from ITBHyperlinkTextView: original word tokenizer/tap routing, nil/empty attributed-text traps, custom accessibility actions and weak callback/URL-vs-string handling are source restored. Default handler references genuine ICMLinkOpener.shared/openLink: declarations whose original body matches the inlined route; missing implementation remains a strict-link failure. It is not replaced with UIApplication.openURL. Existing InlineCodeChipLayoutManager is used by the original withInlineCodeChips factory. UIView.icmDisplayScale preserves positive finite values including subnormal, otherwise1;11 bounded IEEE cases checked the original integer-condition expression, not UIKit runtime.

TranslationManager now contains actual injected localeProvider, two once-created messenger/helpCenter managers using fresh Config locales, translationLocale, exact lowercase table lookup in the original flat IntercomTranslations.bundle, missing-table English fallback, missing-key English retry and literal brace-parameter replacement in dictionary order. Bundle nil returns original key only on the original branch. Default init retains its original unavailable trap. Utils translationsBundle/loadTranslationsBundle preserve first-receiver dispatch_once and actual existing loadBundle path. Standard Swift-generated declaration metadata resolves the existing Objective-C translation declaration to the local true Swift class without duplicate runtime symbols. Limited independent original static review confirmed exact lowercased/replacingOccurrences default bindings, fallback order and displayScale IEEE behavior; no translation bundle loading/UI rendering/alert/image download, normal Pod or whole SDK runtime acceptance was run. FullSDK NOT_RESTORED, hardeningApplied=false.

Frozen original LinkOpener/transport milestone:167 Objective-C+75 Swift units compile for iOS15 device arm64 and simulator arm64/x86_64, with stable source SHA and no duplicate Swift .N runtime class symbols. Aggregate strict link still fails on34 real missing SDK classes; own-method gaps remain separately visible (HTTPClient23/60 and Utils33/40 original selectedselectors present). Class-gap reduction is not an all-selector or complete SDK tally. Actual generic Engine4/SessionDelegate15 original methods are all present;10 simple delegate accessors have original instruction/metadata proof in IOS_ENGINE_SESSION_ACCESSOR_PROVENANCE.json. Snapshot node_modules/.cache/ios-link-opener-recovery/frozen-167-aggregate-receipt.json retains the exact inputs/commands/gaps. The prior Rating completion negative option-index branch was corrected to original unselected behavior and recompiled on allthree targets; user tap negative tag still retains the original UInt conversion trap.

Real LinkOpener source now contains seven injected/default closure pairs, actual singleton/default provider behavior, complete3requirement original LinkOpenerProtocol, tracking→custom→universal→HelpCenter→regular routing and original AI-only article reaction distinction. Original parser retains query components split at every '=', joining remaining pieces with empty separator, case-insensitive parameter names and first-match behavior; tracking host contains tests remain case-sensitive and extraction preserves original decode/re-encode fallback. HelpCenter matching retains nullable host equality and original raw-string contains. Universal matching retains exact three original case-insensitive comparisons, domain normalization and original URL-description debug logs per candidate. Regexes use original case-insensitive options/UTF16 ranges and unguarded second-last path indexing. No additional URL validation/hardening/depth bound/host normalization was added. Ordinary regular URL http/https restriction is present in original code; custom/app/scene routes retain their earlier distinct handling.

Host routing preserves appdelegate optional-method behavior, original deep-link notification timing, first eligible ordered scene delivery and scene.open rather than an invented UIOpenURLContext. Utils.foregroundOrderedWindowScenes partitions original connected-scene enumeration into activation0 and allother states, filtering only actual UIWindowScene and concatenating without extra role/background filters. Actual HTTPClient.trackURL creates genuine ICMEngine/no-redirect NSURLSession path, retaining the original explicitly empty failure block. Engine upload invokes real MultipartTaskBuilder; download uses original config timeout; requestURL delivers response (not data) to unguarded original success/failure callbacks. SessionDelegate retains raw progress division including zero denominator, only HTTP200 download acceptance, nullable task.error failure, repeated block getters, original redirect suppression and finishTasksAndInvalidate after task callback. No requests, navigation, host/scene callbacks or media transfers were executed.

Existing image/native component link gates now include this actual generic Engine and actual multipart Swift implementation. Eight Objective-C+seven Swift units strictly link for allthree targets in node_modules/.cache/ios-image-engine-component/receipt.json;43Objective-C+sevenSwift native text/card/image/engine units strictly link in ios-native-engine-blocks-component/receipt.json. All prior13/48unit artifacts are retained in separate old cache paths. No vendor/object code or diagnostic replacement dependency is linked. Limited independent predicate/parser/log/binding review found the recorded log-format detail and it was corrected before freeze; default execution and transport bodies were directly reconstructed from original assembly, not runtime compared. FullSDK native UI/factory/complete delegate/normal Pod/static/dynamic App/auth/network/runtime acceptance remain unfinished/NOT_RUN.


Frozen original Video/VideoFile milestone:170 Objective-C+75 Swift implementation units compile for iOS15 device arm64 and simulator arm64/x86_64, with unchanged frozen input SHA and no duplicate Swift .N runtime class symbols. Full strict source aggregate remains UNLINKED on32 genuine SDK class dependencies; original HTTP/Utils/Push selector gaps remain separately listed. Video23/VideoFile27/providerView22/nativeFileView38/playButton7 ownselectors are117/117 present on allthree targets. IOS_VIDEO_ACCESSOR_PROVENANCE.json proves46 original metadata/instruction-backed simpleaccessors. Exact commands/input hashes/remaining gaps are frozen in node_modules/.cache/ios-video-block-recovery/frozen-170-aggregate-receipt.json.

The two real models retain original KVC id mapping, supported-provider order/case, asynchronous prepare/reuse behavior, nullable comparison receiver/order and copy/coder float precision/field exclusions. Provider View is original ITBVideoBlockView with default WKWebView, genuine LoadingView, repeated bounds reads through CGRectGetWidth/Height, original0.8second fade and independent isLoading storage. Six HTML literals match original CFString bytes exactly, including original malformed markup. Original signed64 FCVTZS followed by low32 format consumption is expressed explicitly for NaN, overflow and ordinary dimensions. No extra URL/HTML validation or escaping was introduced. Real VideoFile View constructs AVPlayer/AVPlayerViewController, original thumbnail animations/metrics/notification/KVO ordering and original PlayButton scale/reset/shadow behavior; AVKit is an original system dependency, now present in aggregate link commands. No HTML/player instance, provider network request, thumbnail transfer, host presentation, playback or UIKit runtime test was executed.

Approved resource-layout seam: ITBBlockUtils.loadBlocksAssetsBundle originally performs receiver class lookup then bundleForClass on every call, without once/cache. It now retains that single receiver evaluation; exact default ITBBlockUtils uses existing ICMUtils.intercomBundle, foreign subclasses retain bundleForClass:receiver. AssetManager retains the previously approved exact-default receiver seam. There is no added helper fallback/cache or caller rewrite. Critically, nil returned from a helper does not establish resource isolation: original UIImage imageNamed:inBundle:nil callers may use UIKit main-bundle fallback. Normal static/dynamic consumers must actually verify SDKbundle presence, all63 original resources and five real icon callsites, including videoFilePlayIcon and image-loading-fail. Those consumer checks remain NOT_RUN; no substitute assets or empty images were added. FullSDK remains NOT_RESTORED.


Frozen real Swift block-model milestone:170 Objective-C+80 Swift implementation units compile for allthree iOS15 targets (device arm64/simulator arm64/x86_64), with unchanged frozen input SHA and no duplicate Swift .N runtime class symbols. Full aggregate strict link remains UNLINKED on30 genuine SDK classes: original Code/Table/Ticket models are replaced with true sources, while actual TicketBlockView becomes an explicit new missing destination. Original method gaps in HTTP/Utils/Push and complete UI/factory/delegate/private protocol coverage remain separate from this class count. Six new classes have75/75 own Objective-C selector/type-encoding entries exactly matching original static metadata on allthree source objects: CodeBlock13, TableBlock14, TableRow10, TableCell10, TicketBlock15, TicketType13 (class selectors included). Evidence IOS_CODE_TABLE_TICKET_SELECTORS.json is a static object comparison, not runtime ABI/ivar-layout or private Swift ABI acceptance. Frozen receipt node_modules/.cache/ios-code-table-ticket-recovery/frozen-170-80-aggregate-receipt.json.

Swift CodeBlock genuinely subclasses ITBTextBlock, distinct from old ITBCodeBlock. It retains original nonempty highlighted_html admission, codeFont/codeTextColor superclass update, background/code-insets/10point text-insets, optional ICMBlockAppearance-only highlighting, concrete equality before superclass, shallow highlighted copy, and original coder/four-argument initializer traps. Its dedicated CodeSyntaxHighlighter is the original nominal struct with appearance storage; seven exact enum cases and45 token mappings are statically decoded from original metadata/data. Actual Character-based span stack parsing, first known space-separated token role, inherited nested color, malformed/unbalanced nil, five named entities/UInt32 numeric scalar decoding and10character semicolon lookahead are recovered. No generic HTML importer/stripper or replacement syntax palette was used. IOS_CODE_HIGHLIGHTER_PROVENANCE.json records descriptors/data/function hashes; pixel/parser runtime remains unverified.

TableBlock/Row/Cell preserve real nested ICMBlockLoader parsing, original malformed-element filtering versus empty-row acceptance, caption/title distinction, ignored rows KVC, secure allowed-class arrays, dictionary-init nonempty versus coder-empty semantics, concrete NSArray equality and original superclass ordering. Copy attempts each nested real copy with original wrong-type fallback; TableBlock returns boxed optional super-copy result. Original TableBlock.viewClass is ITBBlockView, not guessed modern TableBlockView routing. Real TicketType requires dictionary id/name/attributes-array/archived Bool, optional emoji, actual FormAttribute compactMap and exact typed initializer. Its secure coder requires NSString emoji despite the optional stored property, preserving original asymmetry. TicketBlock validates its real TicketType before original base admission; original required archive fields, concrete equality-before-super, shallow copy and actual missing TicketBlockView reference remain intact.

Two compiler/importer contracts were corrected from original evidence. Appearance arguments now use the original Objective-C ITBBlockAppearance protocol, allowing direct protocol messages rather than AnyObject dynamic member lookup. Original Code/Table/Ticket classes have no own init method, while existing Objective-C base copy/coder use inherited NSObject.init. A primary init declaration marked NS_SWIFT_UNAVAILABLE is therefore visible only under the empirically verified __swift__ importer macro; ordinary Objective-C sees no extra declaration/body. This prevents synthesized Swift init traps without adding a runtime initializer, category or diagnostic suppression. Original private-header annotation text is not recovered; this is an explicit source-build seam proven by the static own-selector gate. Normal private Clang-module consumer init/copy/coder remains NOT_RUN. No runtime UI/defaults/network/normalPod was executed, no fake successful dependencies added; wholeSDK remains NOT_RESTORED.


Frozen genuine TicketBlockView milestone:170ObjC+81Swift compile allthree iOS15 targets; frozen input SHA unchanged/no Swift.N duplicate runtimeclasses, strict aggregate still UNLINKED on29 real SDK class dependencies. IOS_CODE_TABLE_TICKET_SELECTORS.json now compares7classes/80own ObjC selector+type entries (TicketView5), all exact on three source objects; frozen receipt ios-code-table-ticket-recovery/frozen-170-81-aggregate-receipt.json. Actual Ticket View preserves typed content/provider/weak delegate/five lazy fields, native icon/title/type/vertical stack, construction configure ordering and archived-or-ticket disabling. Reconfigure adds gesture/subviews before original nil trap, uses stored content for accessibility template replacement, does not replace stored content or call superclass configure. It preserves injected theme base versus independently shared theme border, repeated constraints, +8 trailing inequality, icon-only RTL sign and original delegate-only tap. Finite independent static review found no concrete deviation in these selected branches; layout/RTL/accessibility/tap and normal consumer remain runtime NOT_RUN.

ConversationCellBlockDelegate currently declares4/36 original requirements, with no defaults. Original evidence proves only33 complete signatures; three slots are deletedMethodError with unrecovered names/signatures. ConversationContainerCardViewStylable is not a marker: descriptor proves one UIView-constrained instance method but its signature is absent and Ticket witness was optimized away. No fake marker/default/conformance was added. Those private protocol ABI gaps are explicit; genuine consumer/factory/model restoration continues independently, no whole SDK completion assertion.


Frozen original modern Swift TextBlockView milestone:170ObjC+84Swift compile on allthree iOS15 targets, source SHA stable/no Swift.N runtimeclass duplicates; full strict aggregate still UNLINKED on29 actual SDK classes. Static original own-selector/type comparison now covers8classes83entries, including TextView's3 own ObjC methods, in IOS_CODE_TABLE_TICKET_SELECTORS.json. Frozen receipt ios-modern-text-view-recovery/frozen-170-84-aggregate-receipt.json. This genuine modern UIView renderer is distinct from legacy ITBTextBlockView and uses the real HyperLinkTextView/InlineCodeChipLayoutManager, UITextViewDelegate, strong typed content/theme, two weak Swift delegates and cached text view.

Original text pipeline retains constructor-time background-attribute detection, actual dynamic UIFontMetrics scaling, list paragraph replacement, attributed-string repeated reads and nil traps, original background→chip-key conversion, last matching chip-font selection and minimum line-height IEEE comparison. Inline citations remain genuine UIKit circular label snapshots/NSTextAttachment images and original baseline/spacer geometry, using UIView.asTextAttachmentImage's UIGraphicsImageRenderer/layer.render and system textAttachmentWithImage: (Swift-imported NSTextAttachment(image:)). CitationTapDelegate descriptor0x3f202c proves exactly one required class-bound method; actual signature comes from original BlockPartView witness. Typed NSArray<ITBInlineCitation*> header bridge avoids an extra manual Swift array cast. Link callback retains original fallback to real LinkOpener when either weakself or weakdelegate is gone. Nil citation array returns original attributed object; nonnil empty array still produces mutable copy. No replacement text/web UI, added validation or simplified citation rendering was used.

Limited independent static review confirmed weak callback fallback and mutable paragraph-style/NaN line-height branch. No real font scaling/chip geometry/citation image rendering/accessibility/tap, normal Pod consumer or full factory/controller runtime acceptance ran. ConversationCellBlockDelegate now declares5/36 original requirements only; unknown3deleted slots/private Stylable requirement remain explicit. WholeSDK remains NOT_RESTORED, with method and class gaps kept separate.


Modern List/Button source milestone: 170 Objective-C + 88 Swift units compile for arm64 iOS15 and arm64/x86_64 iOS15 Simulator. Frozen receipt: `node_modules/.cache/ios-modern-list-view-recovery/frozen-170-88-aggregate-receipt.json`. Inputs SHA-stable; no undefined duplicate Swift `.N` ObjC aliases. Static source-object own selectors/type encodings match original for 10 classes / 90 entries (including List3/Button4); this is not runtime/private Swift ABI verification. Genuine List uses modern TextBlockView, nil textBlocks skips stack, nonnil empty still creates stack, spacing6, physical RTL edge order, and compactMap LF plain text. Genuine Button uses custom UIButton/plain configuration, original title font/colors/insets/corner4/height>=44 and real URL delegate/LinkOpener. Full aggregate remains strict-link blocked by 29 real SDK classes; future modern factory dependencies are not yet represented by this count. Full SDK, UIKit behavior, clipboard, haptics and normal Pod consumer NOT VERIFIED.


Modern Code milestone:171ObjC+93Swift all3 iOS15 targets compile,14classes116ownselectors/typeencodings and Feedback3 statically exact; input SHA stable/no duplicate Swift.N ObjC aliases. Frozen `node_modules/.cache/ios-code-view-recovery/frozen-171-93-aggregate-receipt.json`. Original native Code/Unwrapped/Expanded/Header image controls, syntax colors/gutter/trailing whitespace, default clipboard/success haptic/announcement and1.5s timer reset restored. Scheduler schedule/invalidate preserve injected Dispatcher ordering and overwrite semantics. Scheduler.isTimerValid deleted Bool body, Copier deleted requirement signature, Header symbol initializer and original private injection ABI remain explicitly unrecovered; no marker/default/fake methods. Full aggregate remains29real SDK-class gaps before additional factory dependencies. UI/clipboard/haptics/normal Pod consumer NOT RUN.


Modern Attachment/Header-symbol milestone:171ObjC+97Swift compile all3 iOS15 targets;16classes123ownselectors/typeencodings statically exact, inputSHAstable/no duplicate Swift.N aliases. Frozen `node_modules/.cache/ios-modern-attachment-recovery/frozen-171-97-aggregate-receipt.json`. Genuine nonpersonal Text subclass and personal SwiftUI/UTType/fileicon/hosting, nilwidth268/Any sender/delegate semantics, original group/RTL/newUIKit and legacy mask/border restored. Text parent coder/frame retain original trap bodies but Swift unavailable avoids synthesizing subclass extra initializers; own metadata parent3/subclass1 verified. Header symbolName ctor follows original descriptor+setupSymbol, independent original entry WMOdeleted. Fixed SDK FileType SwiftUI.Image now uses SDK resourcebundle; nil may fall back App resources and normal-consumer asset loading is NOT VERIFIED. Partialdelegate now6/36; three original private slots unknown, Scheduler.isTimerValid and copier injected ABI still unrecovered. Full aggregate remains29real class gaps, factory/remaining modernViews/controller/UI runtime/Pod consumers NOT RESTORED.


Modern Video/Link milestone:171ObjC+101Swift compile all3 iOS15 targets;18classes135ownselectors/typeencodings statically exact;inputSHAstable/no duplicate Swift.N aliases. Frozen `node_modules/.cache/ios-modern-video-recovery/frozen-171-101-aggregate-receipt.json`. True modern WKVideo/shimmer/failure/orientation-size reload and six exact original HTML format literals restored; no template escape/markup modernization. True Link UILabel preserves original nil-string Foundation bridge, grapheme String.count range and selective font scaling, articleId-only delegate route. Productdelegate8/36; Video visibility/general update and private nominalFactoryshape remain WMO-unrecovered, no pauseJS/no-op invented. Fullstrict aggregate still29realclass gaps before remaining factory dependencies. Native UI/media/network/normal consumers NOT RUN.


Modern LinkList/Default milestone:171ObjC+103Swift compile all3 iOS15 targets;21classes147ownselectors/typeencodings statically exact;SHAstable/no duplicate Swift.N aliases. Frozen `node_modules/.cache/ios-modern-link-list-recovery/frozen-171-103-aggregate-receipt.json`. True modern LinkList/item preserves typed unconditional array bridge/niltrap, per-item count reread, footer-separator ordering,999height/two-line calloutfont and articleId-vsHelpCenter routing; trait-scale separator updates retain original boundaries. Real Default text/nilbridge/subheadline/6x8edges restored; typing print remains original future Factory route16 responsibility. Partialdelegate9/36. Unused original lazy separator accessor implementation deleted; no fabricated getter/privateABI completeness claim. Full aggregate still29realclass gaps, remaining factory/images/VideoFile/Messenger/controller/normalconsumer/UI runtime NOT RESTORED.


Modern MessengerApp milestone:172ObjC+110Swift compile all3 iOS15 targets;24classes164ownselectors/typeencodings statically exact plus direct WKcategory1.5JS data literals byte/SHA exact;inputSHAstable/no duplicate Swift.N aliases. Frozen `node_modules/.cache/ios-modern-messenger-recovery/frozen-172-110-aggregate-receipt.json`. Actual native wrapper/nonpersistent WebKit Conversation specialization, script/payload/name/type order, original HTTPS+nonnullhost Sheet gate, height/text/Sheet callbacks, URL theme duplicate append and real pageWorld/inFrame:nil asyncJS callback recovered. Payload is distinct Swift-fields NSObject (only4own ObjCmethods); no added12property selectors. Privateheader NS_SWIFT_DISABLE_ASYNC is a documented importer seam (original annotation unknown), preserving original Task callback and runtime selector, no warning suppression/evaluateJavaScript substitute. Sheet protocol2required exact; actual controller41methods not restored despite no additional LLVMclass gap in this snapshot. Complete private MessengerApp protocol/Stylable conformance and ContentType sourcecase spellings/ABI remain unrecovered; genuine concrete behavior recovered without marker/noop. Partialdelegate17/36. Fullstrict still29class gaps plus explicit dynamic/selector dependencies; wholeSDK/consumer/nativeUI/JS/media/network NOT VERIFIED.


Modern VideoFile/value milestone:172ObjC+115Swift compile all3 iOS15 targets;28classes183ownselectors/typeencodings statically exact plus2HTTPBridgeSheetforward encodings. InputSHAstable/no duplicateSwift.N aliases. Frozen `node_modules/.cache/ios-modern-video-file-recovery/frozen-172-115-aggregate-receipt.json`. True UIKit VideoFile controls/thumbnail/shimmer and delegate play, real AVAssetImageGenerator sync-cache/background worker and MediumQuality MP4 export restored; original strongplay callback/no observer removal/error/filename quirks preserved. FooterNotice/AvatarDetails and UIFlags true parsing resolved2class gaps; fullstrict now27real classes, HTTPBridge22/50 ownselectors, still dynamic/selector/remainingfactory/images/Sheet/controller gaps. Two Sheet methods only direct genuine HTTPClient forwarding. Media/cachefiles/exports/playback/UI/normalconsumer NOT RUN. Original privateSwiftABI and custom SwiftError bridge domains/type namespace unverified; no guessed conformance/error descriptions.

Modern image milestone:172ObjC+117Swift compile all3 iOS15 targets;30classes200ownselectors/typeencodings statically exact; frozen input SHA stable/no duplicate Swift.N aliases. Receipt `node_modules/.cache/ios-modern-image-recovery/frozen-172-117-aggregate-receipt.json`. True OutboundImageBlockView/ConversationImageBlockView use actual animated-image loader/default nil fallback, original shimmer/progress/resize geometry and different link/image tap routes; conversation grouping/border uses true shared helpers. Resize uses loading width and does not update model dimensions. Private upload progress declaration CGFloat restores original importer type with unchanged64bit ObjC d encoding. Delegate20/36; Factory/Outbound delegate/Sheet/controller still missing. Full strict aggregate remains27realclass gaps plus dynamic/selector gaps. UIKit/image/network/upload/normalconsumer runtime NOT RUN; full SDK NOT_RESTORED.

Modern pure Swift Factory milestone:172ObjC+118Swift compile all3 iOS15 targets;frozen `node_modules/.cache/ios-block-factory-recovery/frozen-172-118-aggregate-receipt.json` inputSHAstable. Genuine modern renderer routes, typed compactMap/blocks niltrap, Video size/cache update, Messenger cache stale-content/original repeatedidentifier read and Code presenter re-read/expanded controller now present. Delegate33known/36;three WMO-deleted requirements not invented. Private ImageBlockDisplayContext uses provedraw0/1/2 and outbound1, source labels/namespace nominalshape notproven. ObjC Factory adapter awaits real OutboundContentBlockDelegate+Preview/FileManager; not a legacy viewClass substitute. Fullaggregate strictUNLINKED27realclasses plus dynamic/selector gaps; runtime/normalconsumer NOTRUN.

Real Outbound/Factory/Preview milestone:172ObjC+127Swift compile all3 iOS15 targets;frozen `node_modules/.cache/ios-outbound-preview-recovery/frozen-172-127-aggregate-receipt.json`, inputSHAstable/no Swift.Nduplicate aliases. Actual ObjC Factory uses real Outbound delegate/33knownrequirements,18originalret witnesses/2nil/false and original link/metric/Sheet/inputAlert/AVPlayer/KVO/MainActor previews. Genuine download/temp-copy, PreviewAction, UIKit imagezoom/QuickLook edit/dismiss and two Metric services restored;true Utilsarticle params method raises coverage34/40.36classes230ownselector/types exact;Help20/21 differs only original empty-.cxx_destruct ret elided by currentcompiler,no artificialfield/runtime patch.3deleted Delegate signatures/6Help methods/3FileManager protocol methods remain unknown; concrete observed behavior is real. Fullstrict26class gaps (two Metric classes resolved, actual Sheetclass now visible), plus dynamic/selector/UI/controller dependencies. No file/network/clipboard/media/QuickLook/UIKit/RN/normalconsumer runtime run; fullSDK NOT_RESTORED.

Native Sheet/navigation milestone:182ObjC+127Swift compile all3 iOS15 targets;frozen `node_modules/.cache/ios-sheet-recovery/frozen-182-127-aggregate-receipt.json` inputSHAstable/no Swift.Naliases.8newclasses127ownselectors/typeencodings plus10HSL/UIFontcategorytypes statically exact. Real Sheet41 preserves defaultWK, original direct handler/strongdelegate, dynamic JS parser/POST/header/decision sequence/progressNaN/submittimer/KVO-only dealloc;real Nav22/MessengerNav1/Error18/Buttons29/AutoLayout15/Pointer1/HSL9/UIFont1 close necessary realdependencies. HSL originalNE green!=max fixed beforefrozencompile. Aggregate strict23realclass gaps (Nav2/Sheet resolved), furtherdynamic/selector/controller/model/UI gaps explicit. No normalconsumer/UIKit/WK/JS/network/media runtime;wholeSDK NOT_RESTORED.


## Normal consumer and resource packaging closure

`.build/ios-normal-consumer/receipt.json` pins the unchanged SDK implementation inputs, true RN0.86.2 Codegen artifacts, local Pod resolution, normal static/dynamic build logs, Host images and selected public/private compiler probes. Each real Pod target compiles689 SDK/RCT source units (290 SDK ObjC,3 RCT ObjC,1 RCT ObjC++,395 Swift) plus CocoaPods own generated marker; no original executable SDK, fixture body or undefined-dynamic-lookup substitute is supplied.

The only consumer corrections are deletion of the private aggregate redundant public self-import (the normal parent umbrella already owns those declarations) and owned Intercom.bundle/Info.plist packaging metadata. The latter was first isolated on diagnostic App copies, then rebuilt through ordinary CocoaPods and actually run in both linkage modes. All63 original data hashes remain exact; both colliding Host image names resolve distinct SDK pixels, the real AssetManager default getter resolves the SDK image, and unknown assets stay nil. Static resources live in the App SDK bundle; dynamic resources live in the source framework SDK bundle. No image/getter/fallback behavior was changed.

XCTest bundles were built, not executed. Standard simctl install/launch ran the Host-only resource/class probe in this worktree’s independent external device set; no account/session/backend was started. Five full UI resource callsites, foreign receivers, native chat/cards/sheets interactions and complete behavior/privateABI remain unverified. Historical failures and snapshots are retained in the receipt.
