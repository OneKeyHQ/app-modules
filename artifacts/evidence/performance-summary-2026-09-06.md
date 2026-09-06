# Account Selector 1,000 x 1,000 performance run

## Revision and scope

- Revision: `465f60689598126bdc30677cd310e64f805cb2a8`
- Logical fixture: 1,000 wallets x 1,000 accounts = 1,000,000 logical accounts.
- Materialization bound observed on both platforms: 1,000 account rows for the selected wallet; LRU maximum 3 wallets / 3,000 account rows.
- This is simulator/emulator regression evidence. It is not physical-device Release/Profile acceptance.

## iOS Debug Simulator

- Environment: iPhone 16 Pro simulator, iOS 18.6, 402 x 874 logical / 1206 x 2622 pixels.
- Wallet switching: 101 / 101 callbacks; mean 136.0 ms, p50 135 ms, p95 142 ms, p99 143 ms, max 144 ms; row construction max 1 ms.
- Wallet-switch process sample: CPU mean 29.9%, p50 30.5%, p95 55.0%, max 56.4% (40 host samples).
- Account-list scroll signal, 60 drags: 4,581 active intervals over 44.162 s; effective 103.7 FPS; p50/p95 8.33/18.33 ms; 7.1% over 17.5 ms.
- Wallet-list scroll signal, 15 down + 15 up: 1,634 active intervals over 22.735 s; effective 71.9 FPS; p50/p95 15.00/21.67 ms; 20.1% over 17.5 ms.
- Repeated account-scroll process sample: CPU mean 17.1%, p50 17.7%, p95 21.5%, max 22.7% (53 active-window samples).
- Controlled cold-page memory: physical footprint 241 MB before scrolling, 254 MB after 60 drags, and still 254 MB after 15 more drags. The short-run retained trend plateaus after a 13 MB warm-up.
- Functional evidence: left-only scrolling left the right crop byte-identical (`229aa4c1312924d881e42f6417d20173`); the fixed footer crop below y=2280 remained byte-identical (`c4f4d46fdc0ec9c7f3a00cf441768065`).

Recorded 2026-09-05 baseline comparison: switching remains effectively unchanged (current p50/p95 135/142 ms versus 134/143 ms). Account-list cadence is about 8.5% lower (103.7 versus 113.3 effective FPS) with a similar p95 tail. Wallet-list cadence is about 8.6% lower (71.9 versus 78.7 effective FPS) and the p95 interval moved from 20.00 to 21.67 ms.

## Android Release emulator

- Environment: Android 15 / API 35 arm64-v8a emulator, 1080 x 2400, 420 dpi, forced 60 Hz, Skia OpenGL.
- Package: 125 MB Release APK, non-debuggable, ART `speed` compiled, main/background Hermes bundles packaged, arm64 `libnativelist.so` packaged.
- APK SHA-256: `204e9cfe094f9a69897838c4bd8906570c5b6c526d89d099d3da6c93670035bb`.
- Cold process launch: `am start -W` TotalTime 389 ms.
- Page mount to first native visible callback, five opens: 113, 116, 112, 99, 108 ms; p50 112 ms, max 116 ms.
- Wallet switching: 202 / 202 callbacks across two batches; mean 125.8 ms, p50 131 ms, p95 134 ms, p99 135 ms, max 149 ms; row construction max 1 ms.
- First switch-batch active-window CPU: mean 75.4%, p50 76%, p95 92%, max 104% on Android's per-core scale.
- Account list, 25 down + 20 up: 1,471 rendered frames; 17 deadline-missed/janky frames (1.16%); p50/p95/p99 17/23/24 ms.
- Account list, 15 down: 509 frames; 8 janky frames (1.57%); p50/p95/p99 17/23/24 ms.
- Account list, ten 100 ms fast flings: 456 frames; 7 janky frames (1.54%); p50/p95/p99 17/24/28 ms.
- Wallet list, 15 down + 15 up: 958 frames; 24 janky frames (2.51%); p50/p95/p99 17/22/34 ms.
- Retained PSS: 259,404 KiB with one wallet cached; 342,128 KiB after the first 101-switch cache warm-up; 346,905 KiB after the second batch; 336,828 KiB after full account scrolling; 329,601 KiB after wallet scrolling. The post-warm short run plateaus/decreases, but the absolute footprint is high and needs physical-device confirmation.
- Functional evidence: while the wallet list moved, the first visible account stayed at Account #149. The right crop remained byte-identical (`906fc54324cccfd4c42bb2eedfab8081`) and the fixed footer crop remained byte-identical (`f187f6a129b2abf11290b4b618cce065`).

Recorded 2026-09-05 baseline comparison: page-open p50 is essentially unchanged (112 versus 110 ms). Wallet switching has a slower median but a much tighter tail (current p50/p95 131/134 ms versus 118/155 ms), with a similar mean (125.8 versus 124.4 ms). Continuous scrolling is materially better on this emulator run: 1.16-2.51% janky frames and p50 17 ms versus the previously recorded 89.39-100% deadline-missed range and p50 400-450 ms. The current absolute retained PSS is substantially higher than the previous run, so memory is not accepted as a cross-run improvement.

## Drag/reorder acceptance and performance (current working tree)

- Scope: the account-selector example now contains 1,001 reorderable wallets and 1,000 logical accounts per wallet. This section measures the uncommitted drag/reorder implementation on top of `efd93ae39a54ef2bb441f1f319ad98165f331314`; the comparison app source is `app-monorepo` `origin/x@43b20d189228b3ef81e98a81f5dfab95543593e9`.
- Production parity contract: Native uses a 200 ms long press, 10 px movement allowance, 8 px horizontal placeholder inset, 12 px placeholder radius, and spring values `damping=25`, `stiffness=400`, `mass=0.4`. Web uses the production 5 px mouse threshold / stationary touch activation, `$bgHover` (`#FFFFFF12`) and `$bgActive` (`#FFFFFF1B`), a 12 px clone radius, `0 4px 24px rgba(0,0,0,0.12)` clone shadow, 200 ms sibling displacement, and 80 ms drop settle.

### Web production build in Chrome

- Runtime coverage: mouse, stationary touch long press, keyboard lift/move/drop/cancel, edge auto-scroll, drop/cancel, post-drop scrolling, and selection isolation all passed. The first wallet and `wallet 1001 of 1001` were each dragged and committed; the selected wallet's account list did not change.
- 20 consecutive real mouse reorder samples on a 120 Hz browser: 508 sampled frame intervals over 4.572 s; p50/p95/p99 8.3/10.1/10.3 ms; 3 intervals above 16.7 ms (0.59%); no Long Tasks.
- Drop settle observer: 20 / 20 samples completed; p50 81.8 ms, p95/max 82.5/82.5 ms, matching the production 80 ms transition contract.

### Android Debug emulator

- Environment: Android 15 / API 35 arm64-v8a emulator, 1080 x 2400, 420 dpi, 60 Hz. This drag run is Debug evidence and is separate from the Release scrolling numbers above.
- Runtime coverage: movement before the 200 ms threshold cancelled without reorder; long press followed by movement displaced siblings and committed. Reordering remained functional after deep scrolling; `wallet 1001 of 1001` was moved upward by two positions while TEST / Account #1 remained unchanged.
- One representative 1.8 s long drag: 103 rendered frames; 5 deadline-missed frames (4.9%); worst frame 35.6 ms.
- Post-haptic representative 1.8 s long drag: 97 rendered frames; 5 deadline-missed frames (5.2%); worst frame 51.2 ms. This single Debug-emulator sample is consistent with the pre-haptic miss rate but is not sufficient to claim a performance improvement or regression.
- Haptic dispatch evidence: Android's vibrator service recorded the app's drag-start `LONG_PRESS` feedback and three `CLOCK_TICK`/texture-tick position crossings during one drag. The emulator proves dispatch and system classification, not physical motor feel.
- Synthetic stress, 10 consecutive 700 ms drags with 300 ms gaps: 337 rendered frames; 49 deadline-missed frames (14.5%); worst frame 158.9 ms. This is an intentionally compressed Debug-emulator stress loop, not the normal-interaction result.

### iOS Debug simulator

- Environment: iPhone 16 Pro simulator, iOS 18.6, 402 x 874 logical pixels. Validation used XCTest's native `press(forDuration:thenDragTo:)` path (`synthesized=false`); ordinary synthesized swipes begin moving immediately and correctly cancel against the production 200 ms / 10 px long-press gate.
- Runtime coverage: the first wallet moved from position 1 to position 4; after deep scrolling, `wallet 1001 of 1001` moved upward by two positions. The accessibility order updated after both drops, while TEST / Account #1 remained unchanged. The captured active state showed the production-aligned 8 px inset, 12 px radius, and `$bgActive` placeholder treatment.
- Haptic dispatch evidence: source breakpoints on the built simulator binary recorded one `UIImpactFeedbackGenerator` drag-start call and three `UISelectionFeedbackGenerator` position-crossing calls in one native hold-then-drag. The simulator proves that the native paths execute, not physical Taptic Engine feel.
- 10 consecutive 234 px native hold-then-drag samples with 300 ms gaps: 10 / 10 completed; gesture interval p50 1,476 ms, p95/max 1,479/1,479 ms. The interval includes the fixed 600 ms test hold plus XCTest's drag motion, so it is a repeatability/latency signal rather than application-only render time.
- Reliable dropped-frame sampling is unavailable for iOS simulators in the current Apple tooling. No simulator FPS or jank percentage is claimed; physical-device Profile acceptance remains open.

## Full-frame investigation

- Target budgets are 16.7 ms at 60 Hz and 8.3 ms at 120 Hz. Haptic feedback is dispatched by the native view on index changes only and is not the dominant work in the current drag path.
- Android's primary hotspot is structural: every crossed wallet currently copies the 1,001-item list, enqueues a full-list `DiffUtil` submission, snapshots visible positions, invalidates item decorations, then forces an exact RecyclerView measure/layout when the diff commits before starting sibling animations. This explains why compressed dragging can amplify the tail to 158.9 ms.
- Android's smallest full-frame path is to maintain a drag-only mutable order, move the affected adapter position directly with `notifyItemMoved`, let `ItemTouchHelper`/RecyclerView animate the displaced sibling, and perform one immutable-list reconciliation plus one JS reorder event at drop. Forced measure/layout and full-list diffing should not run on each crossing.
- iOS already uses UICollectionView interactive movement, but `showInteractiveReorderPlaceholder` stops and recreates a spring animator for every gesture update, including updates that remain over the same wallet. Cache the target index/frame and restart that spring only when the destination changes; use the same index transition that gates selection haptics.
- Acceptance should be rerun on physical Release/Profile builds: zero application-caused long tasks, p95 within the device refresh budget, and no blank/incorrect wallet position after dragging the first, a middle, and wallet 1001. Simulator Debug results remain regression signals only.

## Verdict

- Dataset/caching bound: PASS.
- Wallet switching callback completion: PASS on both simulator environments.
- Independent lists and fixed footer: PASS on both simulator environments.
- Android continuous-scroll emulator regression: PASS and materially improved from the recorded baseline.
- Web drag/reorder: PASS, including the 1,001st wallet and touch/keyboard paths.
- Android drag/reorder: PASS for functional parity; the Debug stress-loop jank remains a profiling signal, not a Release acceptance result.
- iOS drag/reorder: PASS, including the 1,001st wallet, active long-press state, stable selection, and 10 / 10 repeated native gestures.
- Native drag haptic dispatch: PASS on both platforms; physical feel remains OPEN pending real-device acceptance.
- iOS account-list cadence: usable, but modestly below the recorded baseline.
- iOS wallet-list 120 Hz target: FAIL; current effective cadence is 71.9 FPS.
- Production performance: OPEN until physical iOS/Android Release/Profile runs are completed.
