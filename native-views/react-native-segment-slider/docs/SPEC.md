# SegmentSlider contract

## Scope and ownership

SegmentSlider is the only slider implementation for OneKey apps. It renders a
track, fill, marks, thumb and optional value bubble on iOS, Android and Web
(desktop, web and browser extension). On iOS and Android the native view owns
its current value and handles interaction on the UI thread; the React wrapper
forwards props and callbacks through Nitro and does not round-trip through
JavaScript for each drag frame. On Web a DOM implementation mutates styles
imperatively during a drag instead of re-rendering React per frame. Trading
amounts, account state, order submission and theme resolution belong to the
caller.

## Public API and defaults

The package exports one component, `SegmentSlider`, with the same props on every
platform (`src/types.ts`). It is controlled: `value` is the current value and
`onChange` reports integer-rounded interaction values. While the user is
dragging, an incoming `value` is ignored so the thumb follows the finger; after
the interaction ends, a `value` different from the last reported value moves
the thumb without emitting `onChange`.

Shared props (defaults in parentheses): `min` (0), `max` (100), `segments`,
`sliderHeight` (4), `disabled` (false), `showBubble` (true), `centerOrigin`
(false), `snapTapToSegment` (false), `hapticStep` (0), the nine color props
below, `onSlideStart` and `onSlideComplete`. `alignSegmentMarksToIntegerValues`
(false) is accepted everywhere and only changes mark placement on Web.

Colors are resolved CSS color strings (`#rgb`, `#rrggbb`, `#rrggbbaa` or
`rgba()`); the package never reads an application theme. Each color prop is
optional and falls back to the package's neutral light palette:
`fillColor` (filled track), `trackColor` (unfilled track), `thumbColor` and
`thumbBorderColor`, `markActiveColor` (fill and border of marks the value has
reached), `markInactiveColor` and `markBorderColor` (other marks),
`bubbleColor` and `bubbleTextColor`.

`hapticStep` requests a light haptic tick each time a drag moves the value into
a new `hapticStep`-sized bucket from `min` (for a 0..100 range, `1` ticks on
every 1%). Entering a segment mark keeps its own, stronger tick; at most one
tick fires per value change, and the mark tick wins. `0` keeps the mark-only
tick. Haptics only fire during a drag, never from a `value` update. Web has no
haptics.

`src/SegmentSlider.nitro.ts` defines the native view's required props and
methods. The component above is the only public entry point; the Nitro view and
its `setValue` method are internal.

- `defaultValue` seeds the value once. Later prop commits do not reset it.
- `min` / `max` define the range (native defaults 0 / 100); callers provide a
  finite, ordered range. Both platforms reserve 12 points/dp at each end for
  the enlarged drag thumb (and its shadow on iOS). On both platforms track,
  marks, fill and touch-to-value mapping use the same 12-point/dp inset.
- `segments` specifies the number of intervals; values below 1 render no marks.
- `sliderHeight` sets track thickness (native default 4 points).
- `disabled` dims the view and prevents interaction (default false).
- `showBubble` displays the rounded value followed by `%` during interaction
  (native default true). It does not change the value or callback behavior.
- `centerOrigin` fills from zero instead of the left edge (default false).
- `snapTapToSegment` snaps quick taps to marks while drags stay continuous
  (default false). It has no effect without marks. On Web a press on a mark
  always lands exactly on that mark.
- `hapticStep` adds the per-step tick described above (native default 0).
- Color props supply resolved theme colors. Invalid colors fall back according
  to the platform color parser; the view does not resolve application themes.
- `onChange` reports changed, integer-rounded interaction values.
  `onSlideStart` / `onSlideComplete` delimit an interaction.
- `setValue(value)` updates the native view without emitting `onChange` and
  cancels an in-flight drag. Callers supply a finite value within their range.

## Lifecycle, resources and safety

Native layout and drawing own the layers/paints, touch state and transient halo.
There is no persistence, network request, deserialized application data or cache.
Each mounted view owns its resources; separate JavaScript runtimes do not share
the view's interaction state. Initialization does not depend on another runtime.

The iOS imperative method dispatches to the main queue. User interaction runs
on the UI thread. The iOS control temporarily disables ancestor pan gestures
during a touch and restores them on completion, cancellation or detachment.
Android prevents parent touch interception during interaction. Layer/paint work
stays native; no new bridge calls or resources are required for bubble layout.
The caller controls the number of marks and must leave vertical room above the
track for a 20-point bubble and its 8-point gap.

## Platform behavior

| Behavior | iOS | Android | Web |
| --- | --- | --- | --- |
| Rendering | CALayer / CATextLayer | Canvas / Paint | DOM elements, styles mutated during drag |
| Bubble text | Rounded value plus `%` | Rounded value plus `%` | Rounded value plus `%` |
| Endpoint inset | 12 points; contains the 18.4-point active thumb and 18-point halo | 12 dp; contains the 18.4-dp active thumb and 18-dp halo | 8 px (half the 16-px thumb) |
| Bubble horizontal position | Centered on thumb, clamped inside view width | Background and text move together, clamped inside view width | Centered on thumb, not clamped |
| Stacking (bottom to top) | Track, fill, halo, marks, thumb, bubble | Track, fill, halo, marks, thumb, bubble | Track, fill, marks, thumb, bubble; marks stay clickable under the thumb |
| Mark tick | `UIImpactFeedbackGenerator(.light)`, intensity 0.6 | `CLOCK_TICK` | None |
| Step tick (`hapticStep`) | `UISelectionFeedbackGenerator` | `TEXT_HANDLE_MOVE` (API 27+), else `CLOCK_TICK` | None |
| Keyboard | None | None | Arrow keys move one segment (1 unit without marks), Home / End jump to the ends |
| Pressed thumb | Scales to 18.4 points, plain thumb colors; the node halo stays under it | Scales to 18.4 dp, plain thumb colors; the node halo stays under it | Stays 16 px; plain thumb colors |
| Mark hover | None (no pointer) | None (no pointer) | Ring while the pointer hovers a mark |

Accepted Web divergences: the endpoint inset, pressed thumb size and bubble
clamping differ because desktop pointers do not need the enlarged touch thumb;
there is no haptic hardware to drive. Bubble text uses each platform's system
font.

On both platforms, when the view is at least as wide as the measured bubble, the entire
bubble must stay within the view's horizontal bounds at both endpoints and
throughout the range. Its width, text and vertical position do not change.
The scaled thumb and visible halo must also stay within the horizontal view
bounds during dragging, including at 0% and 100%. Touches at the outer view
edges still select the range endpoints. If the view is narrower than the bubble,
its left edge stays at the view's left edge; full visibility is not guaranteed.
The bubble extends above the 24-point/dp touch view. Callers must allow child
overflow (as React Native's default `ReactViewGroup` does) and reserve vertical
space above it. A clipping ancestor must include that space; setting its clipping
rectangle to only the touch view cannot display the bubble.

## Conformance and acceptance

Web: `src/SegmentSlider.web.tsx` implements rendering, pointer and keyboard
interaction. `src/__tests__/SegmentSlider.web.test.tsx` (jsdom) covers the
controlled value contract, keyboard stepping, integer-aligned marks, the color
props and thumb stacking. jsdom does not lay out, so pointer-to-value mapping
and rendered appearance need a browser check: on desktop Perps, drag through
0%, 50% and 100%, click each mark and confirm the thumb covers the mark it sits
on, in light and dark themes.

Style parity: `tests/style-gallery/` renders the same rows (`scenarios.json`:
4 and 0 segments at 0 / 48 / 100, pressed rows, light and dark OneKey theme
colors) with the production code on each platform and `compose.py` combines
them into `docs/screenshots/style-gallery.png`. iOS uses the Mac Catalyst
build below with `tests/style-gallery/StyleGallery.swift`; Android uses
`tests/android/run.py --gallery tests/style-gallery/scenarios.json` (run with
`adb shell wm density 320` for 2x output); Web uses
`tests/style-gallery/render-web.cjs` (headless Chromium, real mouse press).
Like the other harnesses, this does not exercise the React Native / Nitro
integration.

Haptics (`hapticStep`): acceptance needs a physical iPhone and Android phone
(simulators have no haptics). Drag slowly across 0..100 with `hapticStep = 1`
and confirm one light tick per percent and the stronger tick on each mark.

Status: iOS endpoint containment implemented for OK-65961. The Mac Catalyst
UIKit harness passes 440/440 checks, including transformed thumb and halo
bounds, bubble containment and outer-edge tap mapping. Before the thumb fix,
the added containment checks reproduced eight thumb/halo clipping failures.
iPhone / React Native acceptance is pending.
Android endpoint containment is implemented. An Android 14 / API 34 emulator
running the production Kotlin View/Canvas code passes 460/460 checks: enlarged
thumb and halo bounds, background/text containment, vertical space, light/dark
bubble colors, 160/320-dp widths, unsigned/signed ranges, dragging and tap mapping.
The original implementation failed eight of the initial 44 checks, and its GPU
screenshot showed both endpoint clipping and truncated `100%` text.
Injected Android pointer DOWN/MOVE/UP events also reached both endpoints;
GPU screenshots confirmed complete circles and `0%` / `100%` text while held.
This harness stubs the Nitro base and React context; it does not validate the
complete OneKey Perps screen or the production Nitro/Fabric integration.

- `src/SegmentSlider.tsx`: native controlled wrapper (value sync via `setValue`).
- `src/types.ts`: shared props and default colors.
- `ios/HybridSegmentSlider.swift`: value mapping, interaction, CALayer layout.
- `ios/SegmentSliderColorParser.swift`: colors and native touch forwarding.
- `android/src/main/java/com/margelo/nitro/segmentslider/HybridSegmentSlider.kt`:
  Android implementation.
- `tests/BubbleLayoutTests.swift`: compiles the production Swift implementation
  with only the generated Nitro base replaced by an NSObject test stub. Checks
  actual UIKit layer frames (including the scaled thumb and visible halo), text
  and callback values at endpoints, intermediate values, resized widths and
  signed ranges. Mac Catalyst execution does not
  establish React Native/Fabric or iPhone acceptance.
- `tests/android/BubbleLayoutActivity.kt`: Android View/Canvas rendering,
  text raster bounds and touch-event regression coverage. An RN-like wrapper
  allows child overflow within a horizontally clipping sheet with headroom.
- `tests/android/run.py`: builds a local test APK from the production Kotlin
  sources and runs it on an explicitly selected, already booted emulator.
  Requires JDK 17, kotlinc, Android platform 34 and build-tools 37. It installs
  only `com.onekey.sliderbubbletest`; it does not build or replace OneKey App.

```sh
python3 native-views/react-native-segment-slider/tests/android/run.py \
  --serial <emulator-serial> --out .tmp/segment-slider-android
```

The Android runner saves `result.json` and `screenshot.png` in the output folder.

Run the focused harness on an Apple Silicon Mac with Xcode, from the repository
root (no React Native dependency installation required):

```sh
slider_sdk=$(xcrun --sdk macosx --show-sdk-path)
slider_pkg=native-views/react-native-segment-slider
mkdir -p .tmp/segment-slider
xcrun swiftc -sdk "$slider_sdk" -target arm64-apple-ios15.0-macabi \
  -F "$slider_sdk/System/iOSSupport/System/Library/Frameworks" \
  -I "$slider_sdk/System/iOSSupport/usr/include" \
  "$slider_pkg/ios/HybridSegmentSlider.swift" \
  "$slider_pkg/ios/SegmentSliderColorParser.swift" \
  "$slider_pkg/tests/BubbleLayoutTests.swift" \
  -o .tmp/segment-slider/bubble-tests
.tmp/segment-slider/bubble-tests .tmp/segment-slider/bubble-preview.png
```

Required iPhone acceptance: in the Perps Add Position sheet, drag through 0%,
50% and 100%, confirm full bubble text and complete thumb/halo circles at each
point, correct amount updates,
and normal tap/drag completion. Capture light/dark screenshots. Do not submit
an order as part of this display check.
