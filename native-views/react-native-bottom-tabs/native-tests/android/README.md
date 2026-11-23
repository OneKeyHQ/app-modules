# Android bottom blur regression

These tests run the shipping native tab view in a hardware-accelerated Activity
using Android's platform test runner, without adding a test dependency. The
React component's OS defaults are separately covered by
`example/react-native/__tests__/BottomTabs.androidBottomBlur.test.tsx`.

From `example/react-native/android`, after installing repository dependencies:

```sh
./gradlew --max-workers=2 \
  -I ../../../native-views/react-native-bottom-tabs/native-tests/android/bottom-blur-test.init.gradle \
  -PreactNativeArchitectures=arm64-v8a \
  :onekeyfe_react-native-bottom-tabs:assembleDebugAndroidTest
```

Install the resulting test APK on a task-owned emulator using its explicit
serial, then run:

```sh
adb -s "$TAB_BLUR_TEST_SERIAL" shell am instrument -w -r \
  -e class com.onekey.bottomtabs.BottomBlurTest,com.onekey.bottomtabs.FloatingBottomBlurTest \
  com.onekey.bottomtabs.test/android.test.InstrumentationTestRunner
```

Require `OK (10 tests)` (five each for docked and floating styles). Coverage includes live GPU output and contrast reduction,
scroll-driven background changes, bar/scene geometry, software-window fallback,
IME/system insets, resize, native tab clicks, configuration changes, tint/ripple
restoration, and resource release on hide, disable, detach, and disposal.
`blur-on.png` and `blur-hidden.png` are written to the test package's external
files directory for inspection.

The scroll workload emits four `bottomBlurFrames` records (off/on/on/off), each
with 240 frames after warm-up. Reports separate UI draw, synchronization,
RenderThread command submission, GPU, and total frame timings, plus gaps in
Choreographer callbacks over 25 ms. Emulator GPU/fence timings are not
physical-device performance or a guarantee of consumer-app frame rates. The
test toggles native configuration but does not establish dark-mode visual
acceptance. API <31 intentionally skips GPU-only cases.

The same opt-in build also includes `com.onekey.bottomtabs.BottomTabsDrawerTest`.
Run that class with the same instrumentation command for twelve focused native
drawer tests. They exercise full-size foreground translation, stable child slots,
one settled event and prop echoes, native gesture start/end cancellation, vertical
and bottom-bar exclusions, disabled opening swipes, cancelled/newer targets,
interrupted programmatic targets, Back, resize, detach/reattach and disabling,
plus integration with both shipping native bar styles. Boundary regressions
also exercise generated drawer props in RTL, physical-left placement, and closing
pans while opening swipes are disabled. They
cover a real ViewPager2 (including disabled input on a non-first page),
horizontal-scroll ownership through BOX_NONE/NONE overlays, and retaining
an eligible DOWN stream when a disabled native page declines the touch.
The test RootView is a real ReactViewGroup with its no-op requestLayout and
records React's native gesture cancellation notifications. Initial native fixture
bounds simulate Fabric mount transactions; opening does not trigger an ancestor
layout. Coordinate MotionEvent taps verify dim coverage/close and prevent native
foreground clicks, and late slot mounts assert immediate native bounds. A real
TouchTargetHelper regression checks closed/open/closed React target tags so the
retained invisible dim cannot block JS Pressable input;
consumer-app React Pressable/scroll interaction and physical haptics still need
device acceptance.
