# SegmentSlider contract

## Scope and ownership

SegmentSlider renders a track, fill, marks, thumb and optional value bubble on
iOS and Android. The native view owns its current value and handles interaction
on the UI thread. The React wrapper forwards props and callbacks through Nitro;
it does not round-trip through JavaScript for each drag frame. Trading amounts,
account state and order submission belong to the caller.

## Public API and defaults

`src/SegmentSlider.nitro.ts` defines the required props and methods.

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
  (default false). It has no effect without marks.
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

| Behavior | iOS | Android |
| --- | --- | --- |
| Rendering | CALayer / CATextLayer | Canvas / Paint |
| Bubble text | Rounded value plus `%` | Rounded value plus `%` |
| Endpoint inset | 12 points; contains the 18.4-point active thumb and 18-point halo | 12 dp; contains the 18.4-dp active thumb and 18-dp halo |
| Bubble horizontal position | Centered on thumb, clamped inside view width | Background and text move together, clamped inside view width |
| Web | Not implemented by this package | App supplies its own DOM slider |

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
