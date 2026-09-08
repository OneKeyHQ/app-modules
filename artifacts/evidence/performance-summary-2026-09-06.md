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

## Drag/reorder acceptance and performance

- Scope: the account-selector example contains 1,001 reorderable wallets and 1,000 logical accounts per wallet. The interaction baseline is `eb532c93501ae13d2db7fd70e61cdb96b152f672`; the frame-path optimization is `03ebb0b8e58c677e131bf2e2d46b9f3da5bc60f2`; the full-height sidebar correction is `03cd67efd3557a4dc6e381d532ac090cc80a0ea3`; the comparison app source is `app-monorepo` `origin/x@43b20d189228b3ef81e98a81f5dfab95543593e9`.
- Production parity contract: Native uses a 200 ms long press, 10 px movement allowance, 8 px horizontal placeholder inset, 12 px placeholder radius, and spring values `damping=25`, `stiffness=400`, `mass=0.4`. Web uses the production 5 px mouse threshold / stationary touch activation, `$bgHover` (`#FFFFFF12`) and `$bgActive` (`#FFFFFF1B`), a 12 px clone radius, `0 4px 24px rgba(0,0,0,0.12)` clone shadow, 200 ms sibling displacement, and 80 ms drop settle.
- Full-height layout acceptance: Web wallet `clientHeight` is 1,098 px and its footer remains visible; Android's wallet list is `y=128, height=1982` with the footer at `y=2143`; iOS is `y=62, height=691.67` with the footer at `y=766`. The old fixed seven-row blank region is absent on all three targets.

### Web production build in Chrome

- Runtime coverage: mouse, stationary touch long press, keyboard lift/move/drop/cancel, edge auto-scroll, drop/cancel, post-drop scrolling, and selection isolation all passed. The first wallet and `wallet 1001 of 1001` were each dragged and committed; the selected wallet's account list did not change.
- Post-layout 20-drag alternating real-mouse run at a 590 x 1,280 viewport on a 120 Hz browser: 1,479 sampled intervals; p50/p95/p99 8.3/9.3/9.3 ms; 1 floating-point interval above 16.7 ms (0.068%); worst 16.7 ms; no Long Tasks.
- DOM/windowing bound: 29 of 1,001 wallet rows mounted, with `scrollHeight=78084` and `clientHeight=1098`. The deep cold-open target (`wallet-999`, displayed as wallet 1,001 because the watch wallet is inserted at display position 7) moved from index 1,000 to 998 without a blank gap or selection change.

### Android Release emulator

- Environment: Android 15 / API 35 arm64-v8a emulator, 1080 x 2400, 420 dpi, 60 Hz. The final arm64 Release APK contains both Hermes bundles and `libnativelist.so`; APK SHA-256 is `0d44f1e19a7668863088ecc91e0a832be19e0373a7e522ce582ed157d7b41abe`.
- Runtime coverage: movement before the 200 ms threshold cancelled without reorder; long press followed by movement displaced siblings and committed. Reordering remained functional after deep scrolling; `wallet 1001 of 1001` was moved upward by two positions while TEST / Account #1 remained unchanged.
- Post-layout five alternating 1.2 s native drags: 415 rendered frames; 7 deadline-missed/janky frames (1.69%). Per-sample jank was 1.23-2.44%; p50 was consistently 16 ms, p95 ranged from 17 to 18 ms, and p99 ranged from 18 to 19 ms.
- Compared with the pre-optimization representative Debug samples (4.9-5.2% jank) and compressed stress loop (14.5%), the Release result removes the structural tail. Build mode and run shape differ, so the percentages are regression evidence rather than a controlled A/B benchmark.
- Haptic dispatch evidence: Android's vibrator service recorded the app's drag-start `LONG_PRESS` feedback and three `CLOCK_TICK`/texture-tick position crossings during one drag. The emulator proves dispatch and system classification, not physical motor feel.

### iOS Debug simulator

- Environment: iPhone 16 Pro simulator, iOS 18.6, 402 x 874 logical pixels. Validation used XCTest's native `press(forDuration:thenDragTo:)` path (`synthesized=false`); ordinary synthesized swipes begin moving immediately and correctly cancel against the production 200 ms / 10 px long-press gate.
- Runtime coverage: the first wallet and, after deep scrolling, `wallet 1001 of 1001` both reordered successfully. On the optimized build, wallet 67 moved below wallets 68/69 and ten further ping-pong hold-then-drag gestures completed; the settled accessibility order had no missing or duplicate row, the selected account did not change, and no severe offset remained after drop.
- Haptic dispatch evidence: source breakpoints on the built simulator binary recorded one `UIImpactFeedbackGenerator` drag-start call and three `UISelectionFeedbackGenerator` position-crossing calls in one native hold-then-drag. The simulator proves that the native paths execute, not physical Taptic Engine feel.
- Apple's Animation Hitches instrument reports that this trace type is unsupported on the simulator. No simulator FPS or jank percentage is claimed; physical-device Profile acceptance remains open.

## Hardware-wallet grouped-row acceptance

- Fixture: the top-level list remains 1,001 reorderable wallet units. `OneKey Pro` is one stable top-level unit containing three child wallets, so the 1,000 wallets x 1,000 accounts stress model is unchanged.
- UI geometry: the settled group uses one 68 pt/dp parent row, three 68 pt/dp child rows, and three 12 pt/dp inter-row gaps for a deterministic 308 pt/dp logical height. Each child is independently selectable, while reorder treats the parent and children as one unit.
- Compact drag contract: both the active placeholder and lifted drag card are the 68 pt/dp parent-only row with a `+3` badge. Siblings reflow around that compact unit during movement; release at the target dynamically expands the settled group back to 308 pt/dp.
- Evidence: static, active-drag, and settled-drop captures are under `artifacts/evidence/hardware-wallet-group/`.

### Web hardware-wallet drag

- Runtime coverage: static grouped UI, child-wallet selection, 68 px parent-only active placeholder and drag card with `+3`, sibling reflow, edge movement, atomic drop, and dynamic expansion to 308 px all passed.
- Synthetic development-build mouse run: 180 frames over 1,487.8 ms; effective 121 FPS; p50/p95/worst 8.3/9.2/9.3 ms; no interval above 16.7 ms and no Long Task. This is an interaction-path diagnostic, not a production browser benchmark.

### Android hardware-wallet drag

- Environment: Android 16 / API 36 arm64-v8a Debug emulator, 1080 x 2400; final functional acceptance used a no-logging package.
- Runtime coverage: ordinary wallets exchanged in the actual settled order. The grouped row showed the 68 dp parent-only active placeholder and drag card with `+3`, surrounding wallets reflowed to avoid it, `OneKey Pro` moved from top-level index 2 to index 6, and release restored the parent plus all three children at the target. A subsequent ordinary-wallet reorder still exchanged correctly.
- Isolated `gfxinfo` evidence (`/tmp/android-final-gfxinfo-isolated.txt`): 255 frames; new deadline jank 84/255 (32.94%); legacy jank 2/255 (0.78%); p50/p90/p95/p99 17/18/18/19 ms; worst 34 ms; `Missed Vsync` 0. Functional acceptance passes, but this trace does not support a strict full-frame claim.

### iOS hardware-wallet drag

- Simulator evidence passes the grouped row's static 308 pt state, 68 pt parent-only active placeholder and drag card with `+3`, sibling reflow, target drop, and dynamic expansion back to the parent plus three children.
- Ordinary 68 pt wallets now move both upward and downward across the grouped row while the parent plus three children remain a complete 308 pt atomic unit. The group shifts as one unit, the final order commits correctly, and a consecutive second drag also passes. Evidence is under `artifacts/evidence/hardware-wallet-group/ios-atomic-reorder/`.
- Runtime cancellation could not be generated reliably by the current XCTest gesture injector; its cleanup branch was source-reviewed but remains runtime-open. Simulator FPS evidence and physical haptic feel also remain open.

## Full-frame optimization result

- Target budgets are 16.7 ms at 60 Hz and 8.3 ms at 120 Hz. Haptic feedback is dispatched by the native view on index changes only and is not the dominant work in the current drag path.
- Web now coalesces pointer/touch movement to animation frames, caches the viewport, carries the current index, and remaps only mounted rows. A same-size reorder no longer rebuilds the full snapshot, theme, sections, layout, and render window on every crossed wallet.
- Android now holds a drag-only mutable order, calls `notifyItemMoved` for each crossing, animates only the displaced view, and performs one immutable `AsyncListDiffer` reconciliation at drop. Full 1,001-row copies/diffs, visible-position snapshots, and forced RecyclerView measure/layout were removed from the per-crossing and activation paths.
- iOS keeps UICollectionView's lifted drag view and auto-scroll, but freezes UIKit's provisional destination when an ordinary wallet crosses the unequal-height grouped row. Only crossed cells translate by one 68 pt row step; the final diffable snapshot is applied once at release. The placeholder spring still restarts only when the destination index changes.
- Web meets the measured browser budget. Android is close to the 60 Hz budget but is not literally zero-jank on this emulator. iOS cannot be called full-frame without a physical Release/Profile trace.

## Verdict

- Dataset/caching bound: PASS.
- Wallet switching callback completion: PASS on both simulator environments.
- Independent lists and fixed footer: PASS on both simulator environments.
- Android continuous-scroll emulator regression: PASS and materially improved from the recorded baseline.
- Web drag/reorder: PASS, including the 1,001st wallet and touch/keyboard paths.
- Android drag/reorder: functional PASS for ordinary and grouped wallets, including a regular reorder after moving the group; isolated deadline jank is 32.94%, so strict full-frame remains OPEN.
- iOS drag/reorder: grouped-row static, active, target-drop, bidirectional ordinary-wallet crossing, and consecutive second-drag behavior PASS; cancellation runtime coverage and FPS remain OPEN.
- Native drag haptic dispatch: PASS on both platforms; physical feel remains OPEN pending real-device acceptance.
- Hardware-wallet grouped UI and compact drag: PASS on Web, Android, and iOS, including iOS bidirectional ordinary-wallet crossing without collapsing the 308 pt grouped row.
- iOS account-list cadence: usable, but modestly below the recorded baseline.
- iOS wallet-list 120 Hz target: FAIL; current effective cadence is 71.9 FPS.
- Production performance: OPEN until physical iOS/Android Release/Profile runs are completed.
