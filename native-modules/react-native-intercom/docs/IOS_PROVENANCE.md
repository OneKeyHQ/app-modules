# iOS source recovery provenance

Baseline: Intercom iOS 19.9.1, minimum iOS 15.0. Original artifact inputs are
retained outside this package at
`/Volumes/T7Shield/Project/intercom-native-audit-20261104/ios/`.

| Input | SHA-256 |
| --- | --- |
| Release ZIP | `a786554634283b13e303278a619962ba7c4bee9f4c9d79dc047953ae96ed8546` |
| Device arm64 framework executable | `3673aa7343bdb0d242c662d064b0d4c30ca5ce8b14fda82ccb36c0fe36949543` |
| Device dSYM | `a3c7261a086f16944a22fe422bab02369647fd0364b5eec5a9e4b540dc564311` |
| Simulator framework executable | `570a62daf245801b76291c74703c98bfebdd0eadfe24e210c0468999814a9e0b` |

Public headers are preserved from the release. Objective-C implementation files
are newly written source recovered from documented method semantics, selectors,
constants and ownership operations; they do not contain original object code.
No original SDK framework is a compile/link dependency or build output.

| Recovered unit | Device address ranges | Evidence |
| --- | --- | --- |
| IntercomContent and six content subclasses | `0x4000..0x4354` | Six factories/type/contentId; copied subclass identifiers |
| Help Center models | `0x39208..0x3a998` | Constructors/accessors/equality/hash/description |
| Company | `0x3b1e8..0x3bee8` | Validated setters/attributes/equality/hash |
| Custom-data validation | `0x3bf48..0x3c988` | Type checks/logging/null sentinels |
| User attributes | `0x3d0f8..0x3e5e0` | Validated setters/attributes/registration/equality/hash |
| User identity | `0x163cc..0x17f64` | KVC/state/registration/dictionary/equality/secure-coding/migration |
| Swift Keychain | `0xd7f0c..0xd8c78` | Real Security framework query/update/add/delete/read and default service |
| Logging-state dependency subset | `0xc7558..0xc75d0` | Two synchronized ICMUtils logging state methods; remaining class not recovered |
| Documents/cache dependency subset | `0xc7bfc..0xc80c8` | Six real path/directory/migration methods; remaining ICMUtils class not recovered |
| JWT expiry | `0xd8ff4..0xd96a0` | Three-component base64url/JSON numeric expiry parsing; strict comparison after 300-second grace |
| ErrorProcessor | `0xd73a4..0xd7e88` | Fixed NSError mapping and server JWT-expiry error classification |
| Device token data | `0x38074..0x38480` | Retained token/environment/submitted state and secure coding |
| Persistence proxy | `0x136ec..0x13bb0` | Real secure archive/file I/O and original error/exception handling |
| App identity | `0x13df8..0x144cc` | Source restored, unlinked: missing real OreoEngine dependency |
| Identity store | `0x144d0..0x163c8` | Source restored, unlinked: missing real Engine/Config/HTTPBridge dependencies |
| Oreo request helper subset | `0x51bc8..0x51ea4`, `0x52558..0x52684`, `0x54bc0..0x54c34`, `0x54d88..0x54e3c`, `0x544c8..0x547ac` | Ten real request/JWT/Basic auth/retry eligibility/random helper methods; remainder of Engine unrestored |
| Base URL configuration | `0x5653c..0x56a80` | Original region/custom-hostname/app-id construction |
| Installation fingerprint | `0x98ec..0xa1cc` | Retained installation fields, original identity-store capture, equality/hash/secure coding; product session still unlinked |
| Oreo shutdown state | `0x55b48..0x56538` | Fingerprint/time gate, secure coding, actual archive persistence and background save; product session still unlinked |
| Oreo error response | `0x54fdc..0x55b44` | Singular/array response parsing, shutdown construction and NSError/NSNull wrapping |
| HTTPClient failure/admission subset | `0x4b8dc..0x4c228` | Four original main/background failure, HTTP conversion and app/user identity guard methods; remainder of class missing |
| Nexus configuration | `0x9ac6c..0x9b4fc` | Four retained fields, KVC/parser, original literal URL transform and unchecked equality |
| User JWT refresher | `0xd9884..0xdc688` | Real provider/pending/timeout/cooldown/CAS/callback source; default iOS dependencies retained; exact erased private protocol member spelling not proven |
| Engine request entries/wrapper | `0x53520..0x544c4`, `0x54e40..0x54efc`; wrapper table `0x42dad0` | Actual preflight/placeholder/POST/DELETE/multipart/rebuild flow and original wrapper strings |
| Engine initialization/header source | `0x51680..0x51884`, `0x5195c..0x51bc4` | Original NSURLSession ownership, fingerprint/shared refresher/header/queue initialization; unlinked until shared refresher implementation exists |
| Supported locale getter | `0xe0e98..0xe0ed0`; static array `0x4c6f68` | Decoded ordered 47 small UTF-8 Swift string constants, newly written Swift array; remainder of TranslationManager absent |
| Oreo operation/replay subset | `0x52688..0x5351c`, `0x547b0..0x54bbc`, `0x54c38..0x54d84` | Actual task operations, ignore generation, retry/error classification and refresh replay; real refresher dependency still missing |
| Error status | `0x5ac0c..0x5ad6c` | Four original code-only classifier methods; no added HTTP retry policy |
| Oreo transport subset | `0x51888..0x51958`, `0x51ea8..0x524f4` | Real NSURLSession data task, original error precedence, JSON/code/shutdown handling; product Engine still partial |
| URLSession task operation | `0x510c8..0x51548` | Original asynchronous state/KVO, task resume/cancel and completion transitions |
| JWT replay context | `0x5154c..0x5167c` | Original copied JWT, shallow-copied fingerprint and copied builder block |
| SDK/header metadata Utils subset | `0xc71dc..0xc7324`, `0xc7424..0xc74fc` | Original SDK version, agent, app version and trimmed environment |
| Multipart task builder | `0x132a78..0x133438`, `0x133ef4..0x134e04` | Three original ObjC class methods, NSObject initialization and actual UTF-8/CRLF body bytes; source compiled independently |
| Native Logger | `0xc6f0c..0xc70b0` | Six original OSLog methods, including the baseline release's empty privateLog implementation |

See `IOS_CONFORMANCE.md` for remaining implementation and acceptance gaps. The
upstream Apache 2.0 license and original header copyright notices apply to the
preserved material; recovery modifications are explicitly documented here.

Headers under `ios/Models` retain the original release copyright text.
`ICMCustomDataValidation.h` is a recovered internal interface. `IntercomContent.m`,
`ICMHelpCenterModels.m`, `ICMCompany.m`, `ICMUserAttributes.m` and
`ICMCustomDataValidation.m` are reconstructed implementation source.

`ios/Identity/ICMUserIdentity.m`, `ICMKeychain.swift`, `ICMUtils.m` and their
internal declarations are reconstructed source from the device disassembly,
Objective-C metadata and named Swift dSYM symbols. The Keychain uses Foundation
and Security supplied by Apple, with no original framework input. The original
fixed Keychain namespace and AfterFirstUnlock behavior are preserved. Host tests
inject synthetic Security entry points and a test store; they never access that
namespace on the host. Linked IdentityStore behavior, real network/session code
and the remainder of ICMUtils are still missing.

`ios/Session` contains restored AppIdentity/IdentityStore source and explicitly
incomplete dependency declarations. These files are not counted as linked
product closures. They do not link or load the original SDK. Source object
compilation only establishes syntax/target compatibility until the remaining
real native implementation exists.


`ios/Identity/ICMDeviceData.m` restores only +deviceIdentifier from original
0x377b8..0x37858 (symbolicated lines 55824..55869). No original +load/+initialize
method exists for that class in the pinned symbol inventory. Remaining DeviceData
methods are absent. `ios/Session/ICMHTTPClient+HelpCenter.m` restores three request
mappings and their callback blocks at 0x4f6b8..0x4fdec (lines 81506..81987), using
actual recovered DeviceData and existing original class interfaces. Its product
identity/Engine closure remains unlinked. Fixture defaults, UUID and request sinks
exist only under scripts and are not implementations of SDK dependencies.


`ios/Identity/ICMSafeNetworkModel.m` reproduces the two original return-only KVC
handlers at 0x23d5c/0x23d60 (lines 34601..34605), with complete original class
inventory and no synthesized fallback methods. `ICMSheet.m` restores factory,
parser, equality/hash and retained body accessors from 0x3a9f8..0x3acf8 (lines
59298..59507); metadata confirms superclass ICMSafeNetworkModel, one NSDictionary
ivar at offset 8, 16-byte instance, eight own instance methods and one class method.
`ios/Session/ICMHTTPClient+Sheets.m` restores fetch/submit and their blocks from
0x4c26c..0x4c900 (lines 78049..78551). These request methods remain product-unlinked;
tests supply only diagnostic identity/engine/defaults and never original SDK code.


`ios/Session/ICMHTTPClient+IdentityRequests.m` restores setAuthTokens and callback
blocks at 0x46388..0x466ec (symbolicated lines 71792..72021), plus multipart identity
parameter construction at 0x4aeac..0x4b1cc (76747..76961). All original getter
conditions, description conversion, sentinel handling and callback/error behavior
are retained. This mapping does not implement server token verification or close
identity/Engine/Bridge integration. Tests use synthetic token names/values only.


`ICMAppEnvironmentChecker.m` restores all nine class methods at 0xc6980..0xc6dd8
(symbolicated 210450..210745), including decoded six-entry CFString table at
0x42ebb8. Simulator branch is independently confirmed true in the original
simulator, while device 0xc6abc returns false. Provision fixtures contain only
synthetic bytes, not signed profiles.

`ICMDeviceData.m` now restores the entire iOS method inventory at 0x373a4..0x38070
(symbolicated 55559..56435): UIKit capture, 21 readonly fields, serializer, static
and style helpers. Metadata gives 25 own instance methods including destructor,
four class methods, 21 ivars/136-byte instance. Original readonly copy properties
are populated by direct retaining ivar assignment. iOS source remains unlinked
on actual PushNotificationManager/identity implementation; no stub supplies them.
The macOS Foundation diagnostics compile only platform-independent helper/getter
methods, not an alternate production initializer/serializer. Utils gains original
applicationName (0xc7328), bundleIdentifier (0xc737c), automatic-push setting
(0xc7998..0xc7a94), notification extension detection (0xc7b58..0xc7bf4) and wrapper
classification (0xc8730..0xc8848), retaining original class/protocol discovery.


`ios/Session/PushNotificationStatus.swift` reconstructs the complete original
Swift status class from 0xe1f00..0xe29c0 (symbolicated 240188..240944), including
provider injection, default provider, notification observer, mutable lazy shared,
serial queue synchronized bit and mandatory completion behavior. Runtime class
name is `_TtC18IntercomSDKPrivate22PushNotificationStatus`; original ObjC metadata
has six instance/two class methods, queue at8/enabled16/provider24, 40-byte instance.
`ICMPushNotificationStatus.h` is its ObjC alias, not a separate implementation.
`ICMPushNotificationManager.m` restores only original isPushNotificationsEnabled
(0x1ede8..0x1ee28) and safeGetAuthorizationStatusWithCompletion plus block
(0x1ee2c..0x1ef1c). Real UserNotifications settings retrieval is used by default,
with original XCTest detection; no authorization request or synthetic product
status fills missing push integration.


`ios/Session/ICMBaseResponse.m` restores all original class methods/instance
methods, property ownership/layout, factory shared Config/identity effects and
Survey failure path from 0x23138..0x23d58 (symbolicated 33776..34600). Original
metadata specifies ICMSafeNetworkModel superclass, seven ivars/64-byte instance,
21 own instance methods and one class method. Config/UnreadConversations/
UnreadTickets/Carousel/Survey/HTTPBridge remain actual missing dependencies;
`ICMBaseResponseDependencies.h` supplies declarations only, not model bodies.
Diagnostics link explicitly named fixture classes for them and actual Nexus/Safe
source; fixture linkage never counts as a product response closure.


`ios/Session/ICMConfig.{h,m}` reconstructs the response/shared/persistence model
from 0x28274..0x2d7d0, with actual 37 original fields/224-byte layout and protocol
metadata, coding at 0x2827c..0x299dc, defaults/parser at 0x299e0..0x2a4f4, equality
0x2af34..0x2c900 and shared/persistence at 0x2c958..0x2cdde. Header/accessor layout
was transcribed from original metadata 10890..11611; source is independently
compiled, not copied method object code. All ten image/UI methods are now source restored in ICMConfig+ImagesAndUI.m;
no test image sink or nested-model fixture enters product source. Required
nested models, ImageLoaderCore and DataManager remain unlinked.

`ConversationStateSyncSettings.swift` recovers original runtime class
_TtC18IntercomSDKPrivate29ConversationStateSyncSettings from 0xf61d4..0xf6968,
including original dictionary required casts, direct/coder/default constructors,
raw intervals, encoding and dynamic-Self equality. `AttachmentSettings.swift`
recovers _TtC18IntercomSDKPrivate18AttachmentSettings at 0xf4718..0xf5aa8, including
actual original Info.plist/defaults/logging behavior and complete coding/model
semantics. Original stub 0x32b074 binds GOT0x456b78 to libswiftFoundation
NSCoder.decodeArrayOfObjects(ofClass:forKey:); camera/voice logs bind original
OSLogType info/error at GOT0x456430/0x456440. These required Config dependencies
are genuine Swift source. Tests replace bundle/defaults explicitly before any
constructor, leaving host preferences and real permission APIs untouched.

`ITBImageLoader.m` restores the original strong-core adapter at 0x7a0d4..0x7a5cc.
The Core interface is a declaration only until its genuine cache/downloader
implementation closes; this does not supply a product image pipeline.

`ConfigModules.swift` restores ConfigModules 0xf5af0..0xf61d0, HomeConfig
0xec2dc..0xec9b8, HomeHeader 0xecc04..0xed680, OpenConfig
0xf3b3c..0xf4510 and Space/SpaceBadge 0x10be0c..0x10cf5c, including
required original parsing/coding and original enum mappings.
`HomeHeaderBackground.swift` restores 0xed8f0..0xf0358;
`HomeHeaderContent.swift` restores content 0xf10e0..0xf2d38, close details
0xf059c..0xf0ef4 and label details 0xf2e88..0xf3738.
`CustomizationColors.swift` restores ActionColors 0xe7340..0xe871c,
ActionContrastColors 0xe8740..0xe8fc4, HeaderCustomization
0xeb27c..0xec27c; `CustomizationConfig.swift` restores 0xe8fc8..0xeb11c.
These ranges identify class methods in the symbolicated input, with generated
Swift metadata and shared forwarding helpers between them; no original object
code is copied. Newly restored private Swift models are source/object compiled,
not independently runtime verified. Original private Swift mangled ABI remains
unproven. UIKit color helpers in `ICMColor.m` restore only required
0xc4cec..0xc4e04 and original Swift hex conversion at0x30b310..0x30b464;
other ICMColor methods remain absent.

Genuine image dependency source now consists of ImageCaches.swift
(CacheKey 0x310808; Memory/Disk class material preceding downloader),
AnimatedImage.swift (0x30bce0..0x30d420), ImageDownloader.swift
(0x310ea8..0x3147e8 and class helpers), ImageLoaderCore.swift
(0x315f48..0x317744 and load/cache methods), and shared Dispatcher.swift.
Independent static downloader contract is audit/independent-review/ios-config-contract/IMAGE_DOWNLOADER.md.
No original object payload or framework enters these builds.
scripts/ios-link-config-source.py strictly links the genuine image pipeline for
all three iOS15 target architectures; runtime image/network behavior is not yet
verified. The full Config aggregate strictly fails solely on ICMDataManager,
whose transitive real Nexus/unread/conversation/presentation closure remains open.

UnreadManager.swift follows original 0x139054..0x139668, runtime ICMUnreadManager
(three ivars,32-byte object). Notification names were independently decoded from
CFStrings pointed to by 0x42cd80/88/c0/c8. Two real unread response classes follow
ICMUnreadConversationsResponse 0x3680c..0x37064 and
ICMUnreadTicketsResponse 0x3acfc..0x3b1e4. Actual response source compiles but
requires genuine DataManager/Conversation to link. UnreadManager itself strictly
links all three iOS15 targets and passes12 focused host state/notification checks;
no original differential or iOS runtime claim is made for these new sources.

DataManager main state/lifecycle source follows original0x56a84..0x56d98,
0x577c4..0x57cb8,0x5a528 and original21 ivar/accessor metadata.
The genuine UnreadManager is linked into aggregate Config; next strict unresolved
classes are ICMPresentationManager/IntercomNexusClient/IntercomNexusEvent.
DataManager remains partial: no missing request/merge/event implementation is
filled with a stub. Conversation category follows0x57cbc..0x58ad8 plus
0x59b14,0x59d40..0x59f40; related true HTTP/model closure remains unresolved.

Nexus transport source follows extracted implementation-baseline/ios-nexus
manifest (each original asm/metadata excerpt retains source ranges and SHA).
Actual Client/Connection/Socket/Event/Configuration strictly link for all three
iOS15 targets using only Foundation/UIKit/Network and recovered source. No task
is created during link checks.12 focused host Event checks cover the independently
corrected showAvatar dictionary default and event names/typo/unknown behavior.
Original22 event name/type mappings were decoded from const dictionary0x453098
(keys0x452f10,values0x452fc0), status names from0x42e690, timestamp scale1000
from0x3c2620. This is reconstructed literal data, not original method/object code.
Socket/Connection/Client critical branches have independent static Astra checks;
actual realtime/network/lifecycle runtime and full ABI remain unverified.

DataManager now accounts for all88 original own selector names across primary,
Conversations/Tickets/Open categories and synthesized accessors/ARC destructor.
Open integration0x56d9c..0x5716c, conversation construction/participants/suggestions
0x5720c..0x577c0, conversation update0x58adc..0x58f18, ticket paths
0x58f1c..0x59b10, caches/realtime0x59b14..0x5a9b8 follow actual original source
material. Source compiles, but missing models/services and original protocol
metadata prevent claiming complete product linkage or behavioral/ABI equality.

HTTPBridge seven response methods follow0x44118..0x445b4 and independent
implementation-baseline/ios-http-bridge/CONTRACT.md. This is actual store/unread/
Nexus application source, not a diagnostic provider. Other request methods and
Presentation/Carousel/Survey dependencies remain required.

UserUpdateRequest0x3f15c..0x3fdd8 and UserUpdateDeduper0x3e660..0x3f158
follow implementation-baseline/ios-http-bridge original extracts and independent
static merge/batching/archive review. Request overwrite reads old custom first
and only reads new custom when old exists; no extra preflight exception is added.
ProactiveContentVisibility follows0xe15b0..0xe183c, original Swift runtime type,
two BOOL fields and true internal-update side effect, not Config substitutes.
HTTPClient user update0x44d88..0x45758 and setDeviceTokenSubmitted0x4c22c..0x4c268
retain real DeviceData/visibility/Engine calls, original one-shot404 fallback and
callback ordering. HTTPBridge.updateUser0x4107c..0x41568 preserves real deduper,
response/UI/metrics paths. Metrics and other missing selectors are declarations
only, not bodies or accepted product functionality.

The expanded scripts/ios-link-source-aggregate.py now compiles all current
54 Objective-C units plus21 Swift files together for device arm64 and simulator
arm64/x86_64, deployment iOS15. Original vendor/scaffold/fixtures are excluded.
All three native aggregate compilations pass, proving current Swift static type
and ObjC runtime aliases can coexist in this build. Strict aggregate link still
fails with9 genuine missing SDK classes, recorded verbatim in receipt; absent
Objective-C selectors/protocols/resources are additional gaps not diagnosed by
the linker. This is explicitly not full SDK compile/link/runtime acceptance.

Latest presentation mainline source follows original extracts under
implementation-baseline/ios-presentation: Manager0xc10e8..0xc2ce4,
Root0x6c0ec..0x6ff70, Window0x6ff74..0x70648. AppearanceReset
0xc2ce8..0xc318c and HostAppState0xc4e08..0xc53dc are actual UIKit
implementations, not diagnostic providers. Original required/optional Root
protocols were decoded from metadata, with required bodies in the primary
implementation so strict compilation needs no protocol warning suppression.
OpenCallService0x162be8..0x1639dc and UIViewController first-conversation
getter0x326e24..0x327058 retain genuine DataManager/notification/routing effects.
The three NotificationCenterInterface requirements follow dSYM witness names
0x320d08/0x320dac/0x320df0. Concrete DataManager initializer typing remains an
explicit private Swift ABI gap until its original protocol is restored. Utils
sceneManifestPresent0xc793c..0xc7994 and fetchApplicationWindows0xc884c..0xc8a5c
retain original bundle/scene-delegate query semantics.

Current frozen aggregate receipt replaces earlier source-count snapshots:
65 Objective-C units +24 Swift files compile together on all three iOS15 targets.
All input SHA hashes were rechecked after compilation with no drift. Root120/120,
Manager55/55, Window9/9 and Appearance3/3 original own selectors are present
in compiled objects; this is selector presence only, not runtime ABI/behavior.
The script now compares checked-in IOS_EXPECTED_SELECTORS.json against compiled
object symbols for eight main-path classes, explicitly exposing HTTPBridge8/50,
HTTPClient13/60, Utils19/40 and Bridge0/69. Strict link still fails on28 genuine
SDK class dependencies. Original destination controllers, InApps, navigation,
theme, metrics/translation methods, native assets and initialized product/UI
runtime acceptance remain open. No vendor/scaffold/fixture bodies were compiled
or linked, and validation created no session, task, simulator or Keychain access.

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

Frozen Bridge milestone:89 ObjC+25 Swift compile on all three iOS15 targets;
Bridge69/69 original own selector symbols now present, PendingPush8/8, Payload25/25,
MaxSizeQueue9/9, RateLimiter11/11. Actual receipt counts are authoritative for
selector names; presence alone does not establish ABI/behavior/product closure.
Strict link30 actual SDK class gaps now includes genuine HelpCenterDataService
exposed by original wrappers. No replacement helpcenter/callback facade added.
Hash recheck found no drift. Rate/Queue original0x21a1c..0x221bc are retained in
implementation-baseline/ios-bridge extracts. No initialized SDK/session/network,
OS notification swizzle, real/default Keychain or source binary fallback was used.


Frozen inbox milestone:95 Objective-C +25 Swift implementation units compile for device arm64 and simulator arm64/x86_64 targeting iOS15. Strict aggregate link reports32 real missing SDK classes, including OpenResponse and MessagesEmptyState newly exposed by genuine inbox/open parsing. Source SHA recheck has no drift. ConversationPageResponse14/14 and ConversationsResponse12/12 original own selectors are present. Selected selector presence is not ABI/behavior or product closure. Evidence:node_modules/.cache/ios-source-aggregate/receipt.json; Session compile receipt includes78 units. Actual initializer/network/OS push/default Keychain were not executed; fullSDK NOT_RESTORED.


Diagnostic genuine source milestone supersedes earlier aggregate:95 ObjC+32 Swift compile on device arm64 and simulator arm64/x86_64 targeting iOS15, inputSHA unchanged. Strict aggregate reports31 actual missing SDK classes; DiagnosticSubscriber class gap is now removed by actual MetricKit lifecycle/extraction/filter/payload/store source, not a linkfixture. Remaining fullSDK NOT_RESTORED; runtime initialization/MetricKit delivery/whole diagnostic behavior not verified. Independent limited static contracts in audit implementation-baseline/ios-diagnostics; support host checks:14 config/store assertions,10 filter assertions, actual own-image BinaryIdentifier UUID matches dwarfdump. Receipts node_modules/.cache/ios-source-diagnostic-{store,filter,binary}/receipt.json and ios-source-aggregate/receipt.json. No defaultUserDefaults/hostKeychain/network/SDK initialization by these tests. NaN JSON fixture uncaught Foundation exception retained separately and product behavior unchanged.

Diagnostic implementation sources derive from original arm64 ce084..ce510 (actual-image identity), ceaf0..ceeb8 (configuration), cef0c..cfa74 (recursive filter), d01b8..d0f00 (payload/context), d15f0..d2dd4 (store), d3720..d67d0 (Subscriber/default/lifecycle/process/extraction). Original metadata/reflection/stub-binding extracts retained under audit implementation-baseline/ios-diagnostics. Source private Swift names/ABI not claimed exact; actual ObjC runtime name and original -init trap retained.

Frozen Open parser milestone:98 Objective-C+51 Swift implementation units compile on device arm64 and simulator arm64/x86_64 targeting iOS15. Strict aggregate link29 genuine SDK class gaps remain; OpenResponse/NewConversationResponse/MessagesEmptyState/PoweredBy are now supplied by real parser source. All input SHA rechecks match, no duplicate Swift ObjC runtime class .N symbols. Evidence node_modules/.cache/ios-source-aggregate/receipt.json. Product initialization/login/nativeUI still unlinked; no SDK runtime, network, OS push, MetricKit delivery, real Keychain or resource consumer acceptance claimed. Avatar typed seven-argument initializer remains absent despite parser source; private Swift enum spelling/ABI not fully proved.

Parser recovery evidence: original arm64 ComposerState26fb8..27520, ComposerSuggestions27524..28244; DictionaryInitializable/DictionaryDecoder100f80 and generic specializations101b7c/102304/102ab4; PoweredByfe138..ff1c4, ReplyOption original ffa10 initializer and 100580/100880 dictionary entries; CTA10f30c/HomeCard10f98c/NewConversationResponse110024/OpenResponse110cb0; Headerf6e98..fb778; Avatar12b330..12deb0; Participant12e530..130320. Detailed static contracts/raw extracts under audit implementation-baseline/ios-{messages-action,reply-option,avatar-details,avatar,participant}; no original method executable bytes transcribed or linked. Resource manifest IOS_RESOURCE_PROVENANCE.json covers63 byte-identical data resources only; Pod integration remains pending.

Frozen TeamPresence milestone:99 ObjC+55 Swift implementation units compile across all three iOS15 targets. Strict aggregate28 genuine SDK class gaps remain; actual TeamPresence, ActiveBot/ParticipatingAdmin/StreamingPart/PartMetadata supply that prior class gap. TeamPresence19/19 original own selector symbols present. All input SHA rechecks match; no duplicate Swift .N ObjC runtime classes. Evidence node_modules/.cache/ios-source-aggregate/receipt.json. No product initialization/runtime/OS notification/real HTTP acceptance; complete SDK NOT_RESTORED. Participant adminAppNameTitle:appName: and Avatar typed initializer remain missing private entries; copy subclass constructor dispatch not independently compared.

TeamPresence state/copy/parser from original3c98c..3d0f4 and metadata48B/fiveivars; ActiveBot12ad38..12b32c, ParticipatingAdmin130488..132710 contracts in audit implementation-baseline/ios-team-presence-models; actual StreamingPart original typed initializer, dictionary initializer132710 and equality13288c and PartMetadatafd79c..fe698 manually reconstructed. Limited static Open model review at independent-review/ios-open-response-models/receipt.json; six-file subset only, no runtime/fullclass claim.

Frozen ConversationPart milestone:105 ObjC+55 Swift implementation units compile across device arm64 and simulator arm64/x86_64 targeting iOS15; sourceHashDrift=[] and no duplicate Swift ObjC runtime class .N symbols. ConversationPart135/135 ownselectors present, with43 original ivars/40 strictly-matched synth accessor pairs and actual logic in6implementation units. Strict link now42 genuine SDK class gaps (was28): actual Part implementations expose previously hidden BlockLoader/BlockUtils/appearance/Form/EventData/source-wrapper/reactions dependencies. An increased missing-class count is an expanded dependency graph, not a linked closure or behavior regression result. No fake model/UI/appearance/body returned. Initialization/login/nativeUI still unlinked, all new Part behavior runtime unverified. Evidence node_modules/.cache/ios-source-aggregate/receipt.json.

ConversationPart originals 30dc4..35450 (source method bodies through destructor) and metadata class43ivars/280B,135ownselectors. Data-only declaration synthesis evidence includes metadata/asm SHA and each accessor instruction SHA plus matching GOT/offset/ownership in IOS_PART_ACCESSOR_PROVENANCE.json. Forty simple pairs generated as standard Objective-C declarations/@synthesize; state/displayStyle/articleId and all constructor/parser/classification/copy/equality logic are manually recovered source, never copied original method bytes. Detailed parser/equality/static dependency contracts audit implementation-baseline/ios-conversation-part.

Frozen Part parser dependency milestone:106 Objective-C+61 Swift source units compile for device arm64 and simulator arm64/x86_64 at iOS15. Strict aggregate has38 genuine missing SDK classes, reduced from42 by actual Form/EventData/FinAnswerSourceWrapper/ReactionsSet source. Source SHA recheck has no drift. No fixture implementations or original SDK executable inputs. Actual parsing/coding source is present; runtime behavior and full product initialization/UI remain unverified. FormAttribute typed TicketFormResponseFormField initializer remains missing; Avatar typed initializer and Participant admin title entry remain missing. Evidence node_modules/.cache/ios-source-aggregate/receipt.json.

Frozen Conversation milestone:109 ObjC+61 Swift implementation units compile for all three iOS15 targets, inputSHA unchanged, no Swift .N duplicate runtime classes. Conversation85/85 ownselectors (84instance+1class) present. Strict link41 genuine missing SDK classes (38 before Conversation): its real parser exposes Ticket/UIFlags/ConversationEndedButton/FooterNotice while removing Conversation class gap. No diagnostic/fake model bodies, vendor input or SDK initialization used. Conversation init sets isInboundConversation YES (GOT4a9c00/offset13), read stays zero; conversationParts getter mutates matching ticket parts, exact-class equality contains nullable comparisons then uiFlags isEqual without nil shortcut and final header/teamIntro/specialNotice pointer comparisons. Copy preserves original omissions and deep-copies parts via NSArray copyItems.27 simple accessor pairs have per-method instruction SHA/GOT/offset/ownership proof in IOS_CONVERSATION_ACCESSOR_PROVENANCE.json; state/style/parts getter manually recovered. New behavior runtime unverified.

Public Intercom facade original45 ownclass methods restored as real source, originalpublic header retained with local source model imports, wrapper variants0..3 and seven exported notification globals restored from original0x42cd60..90 data pointers/CFStrings. The facade delegates to actual ICMBridge source; caller-main synchronous/background-main-async behavior is retained only at original gated methods, with direct provider/auth/token/unread/login-state/wrapper/logging methods. No global initialized flag or optimistic callback. Auth filtered copy validates only and original input dictionary is forwarded; login attributes read before IDs, valid ID+emptyattrs fails without email fallback. Update has actual type/nonempty validation and distinct original logs/errors. Rich push nilcontent logs/returns; nonIntercom invokes original contentHandler unguarded with originalcontent. This does not close downstream UI/services/push or establish authentication/session/runtime. Original Swift extension surface remains outstanding. Static source evidence audit implementation-baseline/ios-public-facade/{RN_CONTRACT.md,original.asm.txt,inventory.json,Intercom.original.h}; notification data extraction cache ios-public-facade/notification-data.json. Original header declarations/comments are retained; no original executable/method bytes used.

Original public Swift contract restored from original35-line arm64 .swiftinterface plus method bodies6ffc..867c: nested Content sixcases including ticket; typed proactive content array; optional Result<Void,Error> completions; callback and @MainActor async JWT providers; auth/token Result wrappers; three HelpCenter Result wrappers. All invoke actual public ObjC facade; no optimistic success/error substitution outside original wrappers. HelpCenter nonnilvalue wins over simultaneouserror, then typed ICMHelpCenterDataError, otherwise originalcode3 somethingWentWrong. Async provider answer resumes MainActor before completion; no filtering/cancellation added. Compiler import name __present(_:) matches current Clang importer for original presentContent: header. Aggregate Swift validation now uses original module-name Intercom, language-mode5; privateSwiftABI/compiler-identical layout not claimed. Real CocoaPods/static/dynamic public module consumer remains unverified, as do full SDK runtime/auth/nativeUI. Astra limited static branch review confirmed key/value validation ordering, HelpCenter priority and async MainActor return only.
Original public header (retained API declarations and original documentation; local Models imports only) and data constants reside ios/Intercom.h/Intercom+Wrapper.h; real facade source ios/Session/Intercom.m and Intercom+Swift.swift. No original Swift object/method bytes or vendor module was compiled/linked.

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

ImageText original recovery inputs: Intercom19.9.1 device asm5ad70..5bd54 and original ICMImageTextBlock metadata, recorded in node_modules/.cache/ios-image-text-recovery/model.json/model.metadata.txt. Original25 own methods restored as Objective-C bodies/synthesized14 trivial accessors proven by IOS_IMAGE_TEXT_ACCESSOR_PROVENANCE.json. View/type evidence is audit/implementation-baseline/ios-image-text-view original.asm.txt/original.metadata.txt/factory-init.asm.txt/CONTRACT.md/protocol.json; original Swift view implementation1d1ccc..1d3498 and real BlockViewFactory creation1e4528..1e4690. Product contains source bodies only, not original object instructions. Native model/view initializer spelling is reconstructed from retained fields/call behavior; original private protocol getter name is explicitly inferred, not asserted from missing descriptor names. AnimatedImage bridge annotation follows actual Swift-generated declaration semantics for module Intercom and does not replace its real Swift implementation. Frozen164+70 compile receipt retains35 real undefined SDK classes and no duplicate runtime class symbols; source compile is not full UI/factory/normal Pod/runtime acceptance.

Rating recovery: raw original model a1f44..a2cc0, Option370a4..3735c, RatedIntent a2d14..a2dc0, RemarkIntent a2dc4 onward; exact method/metadata snapshots and20accessor proof at node_modules/.cache/ios-rating-block-recovery and IOS_RATING_ACCESSOR_PROVENANCE.json. Actual Swift view evidence is audit/implementation-baseline/ios-rating-view/CONTRACT.md, original.asm.txt/original.metadata.txt, factory-init.asm.txt and full delegate witness mapping; only3/36 delegate declarations currently restored, not a reconstructed full protocol ABI. HyperLinkTextView source uses original1fc64c..1fdb34 plus true UIView.icmDisplayScale326744..3267cc and existing InlineCodeChipLayoutManager, with original default handler ICMLinkOpener still unimplemented/unlinked. TranslationManager derives original e0c78..e1558 including actual config locale closures, while Utils adds original c7684..c7730 translation bundle methods; original lowercased and Foundation replacement stubs independently bound to system implementations. No original executable/object bytes used in product. Frozen165+74 aggregate compile passes three targets and records35 real missing SDK classes; these source bodies do not establish normal consumer/link/runtime or full native UI restoration.

LinkOpener original evidence: audit/implementation-baseline/ios-link-opener/{PREDICATES.md,original.asm.txt,original.metadata.txt,stub-bindings.json} and local node_modules/.cache/ios-link-opener-recovery/{methods.json,metadata.txt,foreground-scenes.json,http-track.json,ICMEngine.json,ICMEngineSessionDelegate.json,ICMEngineSessionDelegate.metadata.txt}. Core original execution dc6b8..dcd3c/default providers, deb24..df550/open handlers, dfbd0..e003c/scenes and Utils c8a60..c8c18 were read directly; original System UIKit class ref4a8f78 bound to UIWindowScene and logging stub32af3c to os_log_type_t.error. Actual no-redirect/upload/download/request Engine bodies40674..40c44 and delegate40c48..41034, HTTPtrack4b828..4b8d8 are source restored. Original empty HTTPtracking failure block is a proven ret body, not a restoration success stub. ICMMultipartTaskBuilder header now declares its two already-restored actual Swift upload entry points. Standard generated-Swift declaration metadata keeps ICMLinkOpener Clang reference and actual LinkOpener Swift class identical. No original compiled instructions/object bytes copied into product. Engine config arguments remain actual selector-bearing input objects; complete UploadConfig/DownloadConfig model and SDK caller integration are not established by this isolated source-link gate. Frozen167+75 aggregate/real15unit image-engine/real50unit native gates are source-link evidence only, no actual NSURLSession tasks/App/host handlers or UI acceptance.


Original Video/VideoFile source batch (170ObjC+75Swift aggregate) uses device19.9.1 assembly/metadata for ITBVideoBlock, ITBVideoFileBlock, ITBVideoBlockView, ITBVideoFileBlockView and ITBVideoFilePlayButton. IOS_VIDEO_ACCESSOR_PROVENANCE.json records46 strict trivial accessor proofs with original offsets/ownership/instruction SHA. Provider View original contracts/HTML byte hashes are audit implementation-baseline/ios-video-view/{CONTRACT.md,literal-manifest.json}; six literals were independently parsed from the recovered Objective-C source and compared6/6 exact. Native player/FileView/PlayButton methods were directly reconstructed from original instructions, not linked/copied from original objects. Finite independent static review confirmed provider bounds/loading/fade/literal branches; no runtime or private Swift ABI proof is claimed. Frozen source compilation receipt: node_modules/.cache/ios-video-block-recovery/frozen-170-aggregate-receipt.json. No original SDK compile/link input or diagnostic product replacement.

Resource placement adaptation is explicitly distinct from original receiver-bundle behavior: only exact default ITBBlockUtils/AssetManager receivers map to existing SDK Intercom.bundle; foreign receiver lookup remains original. BlockUtils retains one class read/no once/cache. Original resource data remain unchanged. Helper nil does not prove the whole chain cannot load an App asset because existing UIKit image lookup accepts nil bundle; normal consumer resource/real-icon isolation remains unverified. AVKit system linkage supports the original native AVPlayerViewController; normal Pod system configuration/consumer validation is still pending.


Swift Code/Table/Ticket model source provenance: original CodeBlock1ad1f0..1adbb4 plus CodeSyntaxHighlighter1adc04..1afd70, static roles arrays4cd480/528/5a0/5d8/6f0/788/820, struct descriptor3f0848/field40a4bc, seven-role enum descriptor3f086c/fields40a458. TableBlock/Row/Cell raw contracts/excerpts at audit implementation-baseline/ios-table-models; TicketType1277a8..128924 and TicketBlock258ae0..2598f0 reconstructed directly. Static source-object versus original selectors/type encodings are exact75entries/6classes/all3targets in IOS_CODE_TABLE_TICKET_SELECTORS.json. Code highlighter45token/7role data and bounded function SHA provenance are IOS_CODE_HIGHLIGHTER_PROVENANCE.json. No original machine-code objects or original SDK link/compile inputs are present in restored products. Runtime/private Swift ABI/complete UI routing is not asserted.

ITBBlock/TextBlock protocol-typed appearance and Swift-only unavailable inherited-init declaration are explicit source build metadata seams. Original own-method metadata lacks Code/Table/Ticket.init; compiler otherwise synthesized trap overrides that would intercept original Objective-C base copy/coder dispatch. Verified __swift__ conditional keeps the declaration in Swift importer only, leaves ordinary Objective-C inherited NSObject.init and original own-method set, uses no replacement init body/category/pragma. Exact original private-header annotation text remains unknown. Normal privateClang-module integration/actual copy and coder runtime still require consumer validation. TicketBlockView and ConversationContainerCardViewStylable's one optimized-away unknown method remain unrestored rather than receiving a fake class/empty protocol/default implementation.


TicketBlockView actual source inputs: audit implementation-baseline/ios-ticket-view/{CONTRACT.md,original.asm.txt,original.metadata.txt,protocol-descriptors.json}; original factory specialization1e5980, own code259910..25aebc, content offset4d0828 and original eight fields. Finite independent source review records exact SHA in limited-source-review.json. IOS_CODE_TABLE_TICKET_SELECTORS.json includes its5 original own selector/type entries, within80total/7classes/all3source objects; frozen aggregate170ObjC+81Swift remains UNLINKED, with no vendor inputs or diagnostic replacement implementation. True Ticket type is still only a required missing-model declaration; no stand-in Ticket object is constructed. Source ContentView conformance uses existing proven associatedtype/getter contract, while unknown original Stylable method and3unknown deleted Delegate slots remain explicitly unrecovered private protocol requirements.


Modern TextBlockView source provenance: original1de278..1e03bc with original ObjC metadata runtime13TextBlockView/UIView/6fields72B/3ownmethods; source uses actual existing HyperLinkTextView/ChipLayoutManager and original ITBTextBlock/InlineCitation objects. CitationTapDelegate originaldescriptor3f202c has1signature requirement/1instance method and dSYM witness1ec2f8 gives didTapCitations([ITBInlineCitation]); descriptor bytes are IOS_CITATION_PROTOCOL_PROVENANCE.json. UIView.asTextAttachmentImage original3265c0/closure326954 reconstructs true UIKit image renderer/layer rendering. NSTextAttachment(image:) was verified in the recovered Swift object to reference original system objc_msgSend$textAttachmentWithImage:, declared in SDK UIImage.h, not replaced with a fabricated attachment helper. Exact source/function/body hashes are retained in ios-modern-text-view-recovery and frozen170/84 aggregate receipt; no runtime/whole-privateSwiftABI/normal consumer acceptance claimed.


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


Current importer and Inbox foundation update: the active SDK build uses CocoaPods' default single Intercom parent module and automatic Swift compatibility-header phase. IntercomSourceImports.h is an otherwise empty public gate; only SDK pod-target INTERCOM_SOURCE_BUILD imports the real private aggregate. The actual differently named ReactNativeIntercom Pod sets header_dir Intercom. Private headers stay private; no active custom map, SourcePrivate Swift imports, PCH, experimental flag or host macro is required. Historical private-map/bridging-header probes are not the current packaging design. Actual complete RN/default-static/framework SDK consumer acceptance remains pending; minimal real CocoaPods importer evidence is pinned in the external ios-static-pod-importer packet.

Genuine ErrorViewController/ConversationSpaceViewController, LoadingViewController/LoadingSpinnerView, DotIndicatorView, AvatarView/AvatarViewModel/TeammatePresenceView/MultipleAvatarsView, singular MultipleAvatarView, InboxInfoView/GradientView, FeatureChecker, ContentStackView and SurveyQuestionBlockAppearance are implemented from original contracts. DataManager now declares the original 34-requirement/12-property protocol; the three previously split category method bodies are byte-identically included in its primary implementation, without forwarding or warning suppression. Original private-input field/selector semantics are retained. The bounded 132-Swift/12-ObjC core snapshot compiles for all three iOS15 architectures through the normal underlying-module gate and matches 16 selected classes' own selectors/types. This explicitly excludes ongoing newly added Survey sources and is not a passing whole-source aggregate or SDK link. The complete aggregate continues to include all actual sources and reports incomplete Survey/UI dependencies honestly.


Home/Help Center/Carousel current source milestone (earlier counts remain historical): genuine Home VM/Controller, all8renderer defaults and full Help Center Container/Search/List/Article/StartConversation/Cell/Header graph now have source. Frozen whole259ObjC/377Swift objects compiled without exclusions on all3targets; strictlink at that snapshot failed Carousel3+TicketCreateFormVC. The later Home viewModel Objective-C getter/setter correction is covered by16selected classes127own entries on3targets, `.build/ios-help-center-ui-source/selector-fixed/receipt.json`. This complements rather than rewrites the frozen whole snapshot. Home private generic/injection ABI and arbitrary StandardOutline frame/CGSize initialization remain outside observed defaults.

The Help Center31a03c dead-self specialization is restored at its actual Container callsite: create viewed/help/messenger/no_context event, set eventType0, use the same once global metric. Existing helpCenterSharedMetric storage is source-visible internally; no invented static service method or new metric instance. ArticleContainer observed four-argument construction and two-argument default entry preserve the default functional path; the erased private constructor ABI is not claimed. Genuine inherited coder traps are retained through minimal @nonobjc Swift declarations where original had no own Objective-C coder entry.

Carousel is restored as15real native classes including SafeNetworkModel model inheritance, real AdminBlockAppearance, permissions/filter group/cache service, native HTTP/statistics, metrics/reporting, native Page/Screen/Action/Permission UI and genuine ICMActionButton subclass. All343own selectors/plain method types match source objects on3targets (`.build/ios-carousel-source/native-complete/three-target-receipt.json`); the6native HTTP and2external UIKit category methods separately match8entries on3targets (`.build/ios-carousel-source/support/receipt.json`). HTTP source is organized in categories on the original owning classes, as other restored endpoints; original dispatch and functional bodies remain direct. These proofs do not establish current whole link, all private ABI, resource calls, permission/UI/WebKit/backend runtime or normal consumer acceptance. Original source quirks such as equality-based filter removal, nonpersistent-card versus default-sheet storage, callback ordering and native nil/index behavior are retained; no safety policy changes were introduced.


Normal consumer closure: `.build/ios-normal-consumer/receipt.json` records actual default static-library and dynamic-framework local CocoaPods builds with all689 SDK/RCT sources plus the normal CocoaPods marker, real IntercomReactNativeSpec generation, normal Host link, public Swift/ObjC compilation and selected private ICMConfig rejection. Consumer SDK deltas are exactly the redundant aggregate public import removal and new owned resource-bundle Info.plist. No public API, getter, fallback, image/data content, App production configuration or dependency changed. Public headers remain the original10 plus the otherwise-empty conditional gate.

Original63 resource data files remain byte-identical. The source packaging Info.plist is new owned non-executable bundle metadata, not original SDK data. Normal resource relocation without metadata compiled but UIKit image lookup failed; diagnostic App copies proved that this metadata alone restores lookup. Source Pod rebuilds then passed actual isolated static/dynamic Host resource runs:63hashes, image-loading-fail/videoFilePlayIcon Host collision isolation, actual AssetManager image getter, absent-image nil, RN module initializer selector and EventEmitter class loading. Both dynamic bundle URL and translation bundle URL are used directly by the diagnostic verifier; no App-root-only assumption or SDK fallback was introduced. XCTest was only built. Full UI/resource callsites/foreign receivers/account/backend and unknown private ABI remain outside acceptance.
