# Independent iOS overlay acceptance

Design/review owner: Astra. Implementation, package SPEC, native tests and
consumer Gallery owner: Sol. This document is the review contract and evidence
ledger; it does not replace the package SPEC.

## Scope and baseline

- SDK baseline: `f72a3bdb1`, isolated `app-modules-overlay-independent-windows-20261105`.
- Consumer baseline: `fe8ce9d4b8`, isolated `app-monorepo-overlay-independent-windows-20261105`.
- Platform: iOS; the React overlay store/content belong to `main`. `bg` has a
  separate JS heap and does not own these UI entries. The native manager is a
  process-shared instance confined to the main thread, with scene-local windows.
- User selected independent UIWindow plus real UISheetPresentationController.
  Public props/events, Android/Web, and page-scope presentation stay unchanged.
- No publishing, commits or PR creation in this task. Existing user worktrees,
  wallet data and active simulators are outside the test environment.

## Minimal implementation contract

1. Every active global entry has its own passthrough UIWindow/root presenter,
   including sheet, center, fullscreen, toast and anchored presentations.
   A sheet is presented only by its own root; no traversal of other entries'
   presentedViewController chain. Keep the existing system sheet, custom detent,
   transition animator, dimming and content-width behavior.
   UIKit now transforms only that entry's root presenter, so lower independent
   sheets do not receive the same push-back transform as a single presentation
   chain. Inspect the resulting backdrop/corners/stack visually; retaining a
   system sheet does not assert identical multi-sheet stack appearance.
2. One scene manager orders all those windows by level then store stackOrder.
   Ties use a stable native entry order. Keep existing level bands; allocate a
   bounded fractional subrank inside a band rather than multiplying unbounded
   stackOrder into windowLevel (for example, base + rank / (count + 1), inside
   base + [0, 1)). Live stackOrder changes reorder the same windows without
   remounting content. Preserve the store's existing level semantics: an active
   global entry keeps its initial effective level, including a nonterminal own
   sheet re-presentation; only the next completed close/open cycle adopts a new
   level prop. Do not introduce native-only live cross-band moves while the JS
   store still resolves Back/status using the old level. Reordering must not
   call makeKey.
3. Resolve nested overlay hosts back to the same app window/scene. Window
   ownership belongs to the native container/presentation lifetime, not to level
   or React content mount order. An entry leaving one scene cannot remove an
   entry in another scene. A scene-disconnect notification actively releases its
   resources; cleanup must not wait for the next presentation.
4. Closing a non-key window must not change the key window or survivor first
   responder. Closing the key entry restores an eligible surviving window in
   the same scene, or the original app window. Eligible means still attached,
   visible, not closing/released, and either effectively blocking or containing an actual first
   responder; a system sheet retains its existing blocking semantics. Choose
   the highest eligible window by current ordering. Passive toast/debug windows
   without a first responder are ineligible, regardless of level. Preserve existing
   input activation behavior; do not indiscriminately make every new window key.
   Reopening restores eligibility; closing a non-key entry never runs key recovery.
5. Keep the local hidden/entering/shown/exiting state machine. Defer
   mid-presentation close to the native completion. For system sheets, once
   host dismissal starts, record desired visibility and finish that dismissal;
   if visibility is true at completion, stage the same React child and present
   this entry again without a terminal JS event. Its own controller/window may
   change; other entries must remain intact. Do not use the view animator's
   reversal as a substitute for UIKit presentation cancellation. Preserve normal
   UIKit user-pan cancellation and existing non-sheet animation retargeting.
   Every asynchronous completion must match
   controller identity, current presentation generation and applicable phase;
   obsolete callbacks cannot detach new content, release its window or emit a
   terminal event. No shared scene lock may span arbitrary JS guard work.
   For this entry's own reopen, retain a weak original first responder only if
   its window was key when dismissal began. Restore it after the new native
   presentation completion only if it still belongs to that entry, the entry
   remains visible/highest eligible in the scene, and no foreign/system window
   has taken key ownership. A non-key lower entry must never regain focus merely
   because it reopened. Record draft and focus preservation separately.
6. Invalidate/reparent/disconnect first invalidate obsolete operations, then
   detach the owned touch handler, stage/release the owned child and remove the
   owned window. Finish each actual close at most once. Timer expiry is not
   native transition completion. Preserve current callback/API semantics;
   Dialog's 1500 ms fallback is not upgraded to a strict native barrier here.
7. Preserve Fabric touch handler ownership, content staging, uncullable shadow
   node measurements, sheet-width reporting, host-origin correction, contentSize,
   rotation dimensions and status/orientation delegation. Do not introduce a
   second React owner or detach a surviving child to simulate independence.
8. React ownership remains a separate limit: if B is rendered inside A's React
   children, unmounting A can still unmount B. Independent-lifetime acceptance
   uses entries mounted under an independent Gallery parent or Dialog host.
   Existing global-owner window detachment also closes its entry even before a
   React unmount; staging A's nested content may detach a B host. This migration
   does not remove that cleanup or promise lifetime beyond the source owner.

Existing NativeOverlayDiagnostic temporary controller/presenter IDs may support
runtime evidence. Follow its current SPEC: no addresses, content, business IDs
or arbitrary errors in persistent logs. A React mount nonce alone does not
prove native controller identity. No public debug API is required.

## Required checks and runtime evidence

Failure means an observed behavior contradicts the tested requirement. NOT RUN
means no valid runtime exercise was obtained; source/test success cannot promote
that status. Earlier standalone UIKit probe results establish feasibility only.
They do not validate the production custom animator, Fabric or Dialog wrapper.

| Scenario | Actual action and passing condition | Status |
| --- | --- | --- |
| PIN-like to NFC-like Dialog | Real Dialog form accepts synthetic input; confirm creates B before A's normal Footer close. B stays visible and accepts a real tap. Repeat confirm path without ghost/duplicate entries. Do not repair ordering in the test business callback. | PASS: Astra drove two complete v3 form/confirm/input cycles |
| Real Dialog lower-close with upper input | Open independent Dialog A/B, focus/type in B, then invoke A.close from B. Wait through A's native exit and Dialog finalization; B must retain its keyboard/input and accept text without retapping. This exercises Dialog's own close/finalize callbacks, not just OverlayView. | PASS: initial failure corrected; Astra independently drove v3 before to beforex without retap, then closed B and tapped base |
| A/B/C independent lower removal | From C close A, then in a fresh run close B. C and other survivors retain native controller/presenter IDs and React mount nonce, text, counter, scroll and lifecycle; no disappearance/re-presentation. | PASS: Astra v3 bottom A and fresh middle B cases; native C 19/20 and 25/26 respectively retain lifetime |
| Surviving keyboard and scrolling | Focus C, type and scroll, close lower entry, append input without retapping its field, then perform another real scroll/tap. Visible software keyboard and native geometry remain valid. | PASS: independent v3 visible software keyboard, no-retap input, count 1 to 2 and scroll 50 to 100 in both lower-removal cases |
| Mixed presentation and levels | Exercise sheet/center/sheet in one level, anchored content, toast above secure and lock above toast; late lower-level creation cannot cover higher levels. Dismiss each owner independently and prove the next visible layer receives actual input. | PASS: independent v3 sheet/center/sheet and secure/toast/lock interactions. Explicit anchored supplement PASS; late lower-level creation runtime NOT RUN |
| Top close and keyboard restore | Close key C while a passive toast remains; B or the app becomes key, not the toast. Interact with B; a new transient entry can enter/close while another exits. Assert a new keyboard did-show after native close when required, before no-retap input. Sheet completion alone is not a keyboard geometry barrier. | PASS: independent v3 top lock close leaves passive toast and interactive secure A; separate history case closes key C and B accepts bravox without retap, with visible software keyboard. No keyboard notification instrumentation was added to Fabric |
| Duplicate close, close during enter, reopen | Duplicate requests finish once; entering close does not leave a window; false/true/false at dismissal boundaries cannot let an old completion destroy the current entry. System-sheet reopen finishes its own exit before presenting again with the same React content and no terminal JS event. Own draft/focus and other survivors are checked separately. | PASS: independent v3 Rapid C preserves mount/draft/scroll and restores no-retap input; Rapid A with previous input history does not steal C. Entering close completes once and base receives tap. Dedicated duplicate-call/stale-callback injection NOT RUN |
| System pan cancel/complete | Short cancel keeps the same instance; completed drag closes only its entry. Reopen afterward works. Distinguish UIKit cancel appearance callbacks from reconstruction. | PASS: independent v3 short drags re-appear with same controllers 51 and 53, without new present; full drag closes only 51, then a new C opens and accepts input after another cancel |
| Clear/unmount and no blocking residue | Clear all or leave Gallery during transition. No owned overlay remains visible; base UI accepts a real tap and a new overlay opens normally. | PASS for repeated Clear, close-during-enter and new opens; final base taps 7 with no overlay/keyboard. Route-unmount-during-transition NOT RUN |
| Fabric geometry and update regression | Existing Gallery measureInWindow/anchor demo aligns with actual visible targets before/after keyboard and rotation; input/taps work after content updates and close/reopen. | PASS for independent v3 center/sheet measurement before/after keyboard (pixel check within 0.34 pt) and input. Explicit anchor positioning/reopen/click PASS; rotation NOT RUN |
| Scene teardown/reuse | Focused native test releases registered owners on an injected disconnect and permits reuse. Cross-scene isolation, stale callbacks and real disconnect are separate requirements. | Focused injected-notification test PASS; real multi-scene/disconnect and stale-callback runtime NOT RUN |

Lowest-layer focused tests should cover rank bounds/mixed ordering/live stack updates,
non-key close, key restoration eligibility, owning-entry removal, stale
generation callbacks and native sheet retention. Tests that merely inspect
source text cannot prove these behaviors. Native build must compile the actual
consumer copy and its real Fabric codegen, not only the standalone probe.

## Evidence and handoff requirements

- Test simulator/device data and build outputs stay under the isolated external
  worktree. Never select a default device or replace a global XCTest device set.
- Sol owns device operation through implementation/self-test, then explicitly
  hands over device UDID, custom set, transport endpoint, app bundle, native
  build/source hashes and active Metro session. Astra operates only after this
  handoff and returns ownership explicitly.
- Astra independently drives at least the actual Dialog chain, lower/middle
  removal and a keyboard or reopen case. Inspect screenshots, native diagnostic
  events and command receipts; do not merely reuse implementation screenshots.
- Evidence records SDK source revision/diff hash, copied consumer package hash,
  app binary/build receipt, runtime/device, Gallery testIDs and per-case outcome.
  Native source edits require a matching rebuild; Metro-only reload is not proof.
- Explicitly retain limitations: iOS 26 unavailable, Duo/iPad runtime gaps,
  VoiceOver, third-party/system sheets and real multi-scene behavior if untested.

## Review ledger

### Static review and focused native checks

Implementation is present; the independently operated v3 OneKey/Fabric results
are recorded below. Astra read the SDK/Gallery diff and the focused native runner. The native result at
`native-views/react-native-native-overlay/node_modules/.cache/ios-window-tests/results.json`
reports seven passing checks, driven by Sol on an isolated iOS 27.0 simulator and
independently read by Astra after the v3 source correction:

- Independent roots, stable ties and bounded rank with extreme stackOrder.
- Same-level stack reordering preserves root/key identity; re-requesting an
  existing owner with a different level preserves its active level.
- Passive toast is not selected for key restoration.
- Desired-hidden and closing entries are excluded; released owner registration
  receives a new window.
- Scoped input restoration succeeds for the eligible owner, but refuses a higher
  entry, foreign key window, or replacement first responder in the same window.
- Injected scene-disconnect notification releases registered roots and allows reuse.
- Production SheetController survives another window's sheet dismissal with
  the original view/presenter/window identity and no unexpected-disappear callback.

These tests compile the actual WindowManager, SheetController and Animation
sources into a UIKit test app. The sheet checks use `animated: false`; they do
not compile the React/Fabric Container, do not exercise its generation/reopen
paths, and do not prove real multi-scene/disconnect behavior. The scene check
posts a notification against one live scene. These focused checks do not independently upgrade any Gallery result.

The first source review requested exclusion of dead weak owners from key
recovery and ineligible state during reentrant finish cleanup. Both changes
were observed in the subsequent diff: the key candidate checks `owner != nil`,
and `finishing` guards cleanup while preserving closing state through staging.
Existing permanent-invalidated handling is compatible with Fabric's existing
`shouldBeRecycled == false` behavior. Runtime confirmation remains separate.

The later API audit found that the JS store applies level/strategy changes only
on the next open cycle. Native-only live level changes would desynchronize
window order from JS Back/status ownership. The parent required freezing each
active global lifetime's level, including own transient re-presentations, in
the existing native files. Astra verified the final manager's immutable level,
Container's active-cycle level retention, SPEC and updated focused test. The
earlier live-level check is not the final public contract.

The independent consumer evidence below was collected after explicit device
handoff. Earlier standalone probe evidence is not used as a substitute for this
migration's real OneKey acceptance.

### Dialog keyboard boundary under investigation

The baseline consumer Dialog calls `Keyboard.dismissWithDelay(50)` from both
`handleClose` and imperative `finalize`, as well as `dialogShow`. The native
helper immediately calls global `KeyboardController.dismiss()` before waiting
for the requested delay. Consequently, independent native windows alone cannot
prevent those JS callbacks from dismissing another Dialog's input. This
source-identified risk was then reproduced in the real Gallery. In
`dialog-lower.json`, A/controller 25 exits normally; B/controller 27 has no
native disappearance/teardown event and retains text `before`, but its software
keyboard disappears and direct no-retap `x` is not accepted. Astra inspected
the before/after/no-retap screenshots and the result/events. This is a Dialog
keyboard side-effect failure, not cascading native sheet destruction.

The user subsequently approved the minimal iOS-only correction: skip the two
global keyboard dismissals in close/finalize and let the native entry own its
input teardown. Keep dialogShow's existing dismissal, Android/Web behavior and
public APIs. Popover and shared keyboard helpers are outside this change.
Corrected runtime acceptance must cover both lower-close preserving B input and
ordinary top Dialog close/clear releasing its own keyboard and touch shield.
The corrected Sol-driven `v2-dialog-lower.json` reports `beforex` after direct
no-retap input, with only lower controller 9 exiting and no lifecycle event for
survivor 11. Astra inspected that result and `v2-dialog-lower-interactive.png`:
the caret, appended text and software QWERTY keyboard are visibly present.
The v3 independent operation below also passed this scenario.

### Rapid system-sheet reopen failure

Sol observed the original host-dismiss reversal path fail in the actual Fabric
Gallery: Rapid C left C's native controller alive but its surface disappeared,
with dimming remaining above A. Astra inspected the failure screenshot and
existing transition source. Reversing only UIViewPropertyAnimator followed by
completeTransition(false) does not establish that UIKit's presentation hierarchy
was restored. Adding generation guards alone cannot repair the missing surface.
Apple separately documents [interactive cancellation and transition completion](https://developer.apple.com/documentation/uikit/uiviewcontrollercontexttransitioning/cancelinteractivetransition%28%29).
This is not a claim that completeTransition(false) is universally prohibited;
the correction responds to the observed failure of this host-reversal path.
The parent authorized the per-entry completion/re-presentation design above;
Astra reviewed its source correction, which retains UIKit pan callbacks and
removes only the host-driven reversal path. Sol's v2 runtime rerun restores the
surface and draft, and accepts interaction after retapping, but does not restore
the entry's own focus automatically. That is an unfinished acceptance item,
not an accepted loss of input ownership. The parent authorized the guarded weak
responder restoration described above. Astra reviewed the v3 implementation and
seven focused native checks; the matching v3 independent Fabric run below
restores focus and accepts direct no-retap input.
Failure artifacts are in
the consumer `.tmp/overlay-validation/evidence/rapid-*.png`. Preserve them when
recording the corrected build, and bind each outcome to its actual revision.

### Native build provenance

The first v3 automatic DevSession attempt selected a local shell cache without
compiling the changed Swift sources. That run is not v3 runtime acceptance and
must not be reported as a successful rebuild. Sol retained the cache/source and installed-binary diagnosis, then forced the
standard local-shell rebuild. The installed v3 binary and all three Swift source
hashes were independently recalculated by Astra and match the provenance below.


### Independently operated final v3 Gallery run

Astra exclusively operated the real OneKey/Fabric app with isolated idb HID from
17:53 to 18:03 local time on 2026-11-05, then explicitly returned the device to
Sol. All input was synthetic; no wallet/account data was imported or read.

- Consumer evidence directory: `.tmp/overlay-validation/evidence/astra/`.
- Device: `3CE3AFA7-89A8-48CE-9B7F-65524793D575`, isolated external
  `.tmp/overlay-validation/SimulatorDevices`, ordinary iOS 27.0.
- DevSession: `wk-a66c5b7a3a1e-dev-9ddfd24bc69c-6923fc19d80cdbe5`.
- Installed `OneKeyWallet.debug.dylib` SHA-256:
  `7d9f9186e7fa418b339de727c35bccfc85d845a3819b709a44a7b5fd7e87b450`.
- `native-v3-provenance.json` contains the three Swift source hashes and installed
  app path; Astra recalculated all four hashes, rather than relying only on the
  build-success message. `results.json` records checked AX/native assertions.
- `commands.jsonl` records exact timestamped HID and screenshot commands;
  `.tmp/overlay-validation/astra_driver.py` contains independent primitives.
  The only reused helper flushed the native logger; it did not invoke app UI
  callbacks. Named PNG and AX snapshots preserve each observed state.
- `independent-native.log` is scoped by `run-boundary.json` to the first
  independent command. `all-native-raw.log` preserves older process logs too;
  their reused diagnostic IDs must not be combined into one native lifetime.

| Evidence prefix | Independently observed result |
| --- | --- |
| `history-*` | A/B/C accept alpha/bravo/charlie in sequence. Rapid A changes only A native 11/12 to 17/18, C 15/16 accepts x without retap. Rapid C changes its own controller to 19/20 while React mount 6, draft and scroll survive; direct y succeeds. Closing lower A17 leaves B13 and C19 untouched; direct z, count 2 and scroll 100 succeed. |
| `middle-*` | Fresh A21/B23/C25; closing middle B23 emits one completion/teardown and no A21/C25 lifecycle events. C mount 9 accepts middlex without retap, count 2 and scroll 100. |
| `dialog-lower-*` | Real Dialog A27 closes while B29 remains focused; beforex is entered without retap and QWERTY stays visible. Closing B subsequently removes its own keyboard; base receives a tap. |
| `pin-*`, `nfc-*` | Two genuine form confirmation cycles: 31 to 33 and 35 to 37. The callback opens NFC before normal Footer close; no await-close workaround. Both surviving NFC forms accept input and close normally. |
| `mixed-*`, `secure-*` | Removing center B leaves C interactive. Lock C is above secure A/toast; closing C leaves the passive toast and A accepts secure/count input. |
| `pan-*`, `top-restore-*` | Short system drags repeat didAppear on the same 51/52 and 53/54, without a new present. Completed drag releases only C51; B49 survives. Reopened C accepts input after cancellation. Programmatic C53 close restores B's visible keyboard and direct bravox succeeds without retap. |
| `close-during-enter-*`, `final-base` | C39 waits for presentation completion, then emits one completed dismissal and teardown. Repeated Clear leaves a tappable base; final visible base count is 7 with no overlay or software keyboard. |
| `measure-*` | Center and sheet accept input and return correct screen measurement before/after keyboard lift. `measured-pixels.json` compares actual red-box screenshot pixels with displayed measureInWindow values; maximum discrepancy is 0.34 pt. |

This is tested coverage, not proof for every interleaving or device. Remaining
runtime gaps include real multi-scene/disconnect, VoiceOver, iOS 26, Duo/iPad,
route unmount during transition, dedicated duplicate-call/stale-callback
injection and late lower-level creation ordering. Current Phone Gallery is
configured portrait (`app.json` and `EXDefaultScreenOrientationMask`); the idb
transport has no rotation command. Rotation/wide-window geometry was not run,
and no orientation override or extra device was added for this acceptance.

The ordinary compact-phone Popover exercised here uses a system sheet, not an
anchored panel. The explicit `nativePresentation="anchored"` Gallery-only supplement below
passed on the same v3 native binary. Legacy Measure center still
inherits the Gallery ScrollView's first-tap keyboard dismissal: its first Close
tap hid the keyboard, and a second Close completed dismissal. This pre-existing
fixture behavior was recorded, not changed or claimed as a native cascade.


### Explicit anchored supplement

After a second exclusive handoff, Astra independently operated the added
Gallery-only trigger using the existing public `nativePresentation="anchored"`
prop. Production Popover and the native binary were unchanged. The 260 pt panel
appeared below and left-aligned with its trigger, and actual taps advanced its
counter from 0 to 2. Closing it restored a clickable base (7 to 8). Scrolling the
page moved the trigger label from y=479 to y=379; reopening moved the panel and
its content by the same 100 pt, and its action still worked. Closing again left
base count 9, no overlay and no keyboard. Astra inspected the screenshots and
AX counters, recalculated the unchanged native binary hash and final fixture
hash, then explicitly returned device ownership to Sol for cleanup.

Evidence: consumer `.tmp/overlay-validation/evidence/astra-anchor/`, including
`results.json`, `commands.jsonl`, `opened.png`, `action-two.png`,
`reopened-at-new-anchor.png`, `reopened-interactive.png`, `final-base.png`, and
an exact `fixture.tsx` snapshot. Fixture SHA-256:
`3b6a825ce6e0fc4f6a0f80216581f0668ffc3e2bfe3702e8c30c8093fb485294`.
This supplements compact-phone anchored behavior; it does not verify rotation,
other scenes or arbitrary stacking interleavings. The pre-anchor core Gallery
source was not separately hashed at the time; any later reverse-reconstructed
snapshot must be labelled reconstructed, not a contemporaneous capture.
