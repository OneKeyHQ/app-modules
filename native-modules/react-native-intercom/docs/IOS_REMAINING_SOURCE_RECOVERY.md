# Remaining original iOS source restoration

Status: source recovery and finite compile/metadata validation; no runtime acceptance.

The target is iOS 15+, with native process-owned resources shared beneath independently initialized mobile `main` and `bg` JavaScript runtimes. This batch does not add JavaScript state, storage deserialization, runtime readiness assumptions, or App integration.

## Baseline and scope

The reference is Intercom iOS 19.9.1 arm64, SHA-256 `3673aa7343bdb0d242c662d064b0d4c30ca5ce8b14fda82ccb36c0fe36949543`. Original binary instructions and metadata are offline evidence only; neither the binary nor reference framework is a compiler, linker, or runtime input. No fixture implementation, scaffold replacement, or vendor method payload fills missing bodies.

The source follows the existing approved compatibility contract in SPEC sections 4–7 and 9. No new public behavior or security policy is proposed. Original remote content, cards/sheets, and Web JavaScript remain in the baseline. App migration, publishing, and later independent hardening are outside this batch.

## Recovered bodies

| Source | Original evidence packet | Preserved behavior |
| --- | --- | --- |
| `ICMVersionManager`, HTTPClient version category | `implementation-baseline/ios-version-rich-push/VERSION_CONTRACT.md` | Numeric NSString comparison; actual Engine latest-release request; original diagnostics and callback behavior |
| `ICMRichPushMediaDownloadConfig`, HTTPClient/Bridge rich push, Utils rich push | `ios-version-rich-push/RICH_PUSH_CONTRACT.md` | Getter always 30 despite stored setter; MIME-derived extension; original move success/error condition; synchronous supplied callbacks |
| `ICMColor` | `ios-version-rich-push/COLOR_CONTRACT.md` | Seventeen restored methods, fresh theme lookups and original ordered/NaN branch behavior; four existing methods retained |
| `ICMNexusBackdoor` | `ios-version-rich-push/NEXUS_BACKDOOR_CONTRACT.md` | Actual NexusEventFactory raw type 6 into actual DataManager.eventOccurred |
| `ICMUserBlockAppearance`, `ITBBlockCollectionViewCell` | `implementation-baseline/ios-block-list/LEGACY_CELL_CONTRACT.md` | Original theme values, weak delegates, same-class reuse, cleanup/enqueue ordering and estimated frame |
| `ITBBlockListViewController`, `ITBBlockViewDelegate` | `ios-block-list/LEGACY_LIST_CONTRACT.md` | Original sizing, reload paths and delegate freshness; genuine required/optional ViewDelegate methods |
| Push rich/foreground category and Swift communication extension | `implementation-baseline/ios-integration-push/FOREGROUND_RICH_COMMUNICATION_CONTRACT.md` | Actual foreground routing/metrics, media attachments, synchronous avatar data read, Intents donation followed immediately by content update, original debug logs |
| `TouchTransparentCollectionView` | `independent-review/private-runtime-source-presence/THREE_MISSING_BODY_CONTRACT.md` | Three surviving own methods; array-order child hit checks and coordinate-space conversion |

All packet paths are relative to `/Volumes/T7Shield/Project/intercom-native-audit-20261104`. Adjacent original instruction/metadata/literal files define the finite body evidence, including exact diagnostic strings.

## Exactness and unresolved boundaries

The frozen own-method inventory identified nine plain signed/unsigned signature differences. The source restores unsigned PendingPush verdict/init, PushManager delivery/verdict methods and font weight arguments, and signed Utils.fetchSDKType. These changes preserve bodies and align original Objective-C encodings; they do not add enum cases or coercion policies.

DictionaryDecoder loses only the extra NSObject superclass. Its generic decoding, date strategy and current debug catch remain intact. It is a pure Swift root, with no invented Objective-C name or extra Objective-C initializer.

The List controller delegate protocol's runtime descriptor was erased in the original. The source documents only the three observed optional messages, preserving guarded calls; it does not claim an exact original emitted private protocol ABI. ITBBlockViewDelegate is separately recovered from genuine required/optional runtime metadata.

EmojiButton and DictionaryEncoder normal constructors are unavailable: original descriptors have zero implementation offsets and vtable slots bind swift_deletedMethodError. No normal callers were found by the bounded original scan. No guessed normal constructor, encoder wrapper, empty class, explicit fake destructor, or successful fallback was added. Private naming, optimized-away constructors and compiler-generated lifecycle entries remain separate inventory boundaries.

## Verification

`.build/ios-remaining-source/compile.py` compiled fourteen changed Objective-C units for arm64 device, arm64 simulator and x86_64 simulator at iOS 15 with ARC, modules, `-Wall -Wextra -Werror`: 42/42 passed. `receipt.json` records actual commands, logs, source/header SHA-256 and object SHA-256. The config getter's narrow unused-property-ivar diagnostic suppression preserves its original constant getter and synthesized `Td,VtimeoutInterval` metadata without adding an ivar read.

`compare.py` and `own-metadata.json` compare current device object metadata against the frozen original own-method table: all selected entries match. Complete named classes and selected category/signedness entries are distinguished. Selector/type presence proves only that finite metadata surface; body semantics derive from the cited original packets, and neither establishes complete private ABI or runtime behavior.

Swift communication/transparent-collection/decoder compilation must use the current complete genuine source importer after the owners freeze the dependency graph. A reduced Swift set that omits unresolved dependencies cannot establish whole-SDK success. Native link, consumer frameworks/entitlements, notification service extension delivery, remote push, actual hit testing, original-vs-recovered UI and network behavior remain unverified.

Post-change spec audit: these restored original behaviors stay within SPEC sections 4–7 and 9; no semantic contract change is required. Platform divergence remains the approved iOS/Android baseline. No Android, Web, desktop, or extension behavior changes in this batch.

## HTTP bridge completion batch

`ICMHTTPBridge+Completion` restores the five fixed-client same-selector forwards and original dismissal/message metrics from `ios-http-bridge-completion/FORWARD_AND_DISMISS_CONTRACT.md` and `ios-inapps/CONVERSATION_LIFECYCLE_HTTP_CONTRACT.md`. Dismiss success retains task/payload for the callback scope, performs no response parse, and dispatches the original supplied block only when off-main. The message literal keeps original nil traps, number boxing, repeated last-part reads and metric acquisition order.

`ICMHTTPBridge+UploadBlocks` follows `ios-http-bridge-completion/UPLOAD_BLOCK_HELPERS_CONTRACT.md`: synchronous nil-upload success(nil); original cancellation NSError; exact fused `fma(progress, 0.8, 0.1)` progress; dictionary/array helpers with original image/video getter order, repeated dimensions, unsigned byte length, and unmodified nil behavior. It does not use replacement ITBBlock models or the separate Swift bridge's progress policy.

Each new Objective-C unit passed all three strict targets in `bridge-receipt.json` and `upload-block-receipt.json` (3/3 each). The refreshed own table currently covers 13 selected HTTPBridge methods and 4 selected HTTPClient methods, all exact. The final reply method follows `ios-http-bridge-completion/REPLY_CONTRACT.md`; it preserves synchronous invalid-ID errors, strong upload/conversation captures, actual Nexus connection metadata and raw6 event, user callback before fresh ID/status reads, and event creation before metric singleton lookup. This finite batch does not imply whole-source, link, or runtime acceptance.

Final Objective-C batch result: 17 units, 51/51 strict target compilations passed across the four receipts. `reply-receipt.json` records the final reply unit. `own-metadata.json` selects 110 methods: 77 across eight complete classes and Client 4 / Bridge 14 / Push 9 / Utils 6 selected category or signedness entries. All selected names/types match; no selected missing/extra/type difference remains. The Swift communication selector and TouchTransparent three methods remain outside this ObjC-only count until the complete current Swift importer is compiled.
