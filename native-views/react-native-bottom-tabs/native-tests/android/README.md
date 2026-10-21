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
