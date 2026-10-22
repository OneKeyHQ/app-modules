# Android reorder cancellation regression

This source-checkout-only test uses Android's built-in instrumentation runner;
it adds no third-party test dependency and is excluded from the npm artifact.
It runs actual NativeListView/RecyclerView instances with callback-driven moves
and ItemTouchHelper's real release lifecycle. It does not simulate a complete
finger long-press/edge-scroll gesture or establish physical-device acceptance.

From `example/react-native/android`, with the repository dependencies, JDK 17
and Android SDK configured:

```sh
./gradlew --no-daemon --max-workers=2 \
  -I ../../../native-views/react-native-native-list/native-tests/android/reorder-test.init.gradle \
  -PreactNativeArchitectures=arm64-v8a \
  :onekeyfe_react-native-native-list:assembleDebugAndroidTest \
  :onekeyfe_react-native-native-list:testDebugUnitTest
```

Use only a task-owned arm64 emulator. Set its explicit serial, then run from
the repository root:

```sh
REORDER_TEST_SERIAL=emulator-5586
adb -s "$REORDER_TEST_SERIAL" install -r \
  native-views/react-native-native-list/android/build/outputs/apk/androidTest/debug/onekeyfe_react-native-native-list-debug-androidTest.apk
adb -s "$REORDER_TEST_SERIAL" shell am instrument -w -r \
  -e class com.margelo.nitro.nativelist.NativeListReorderCancellationTest \
  com.margelo.nitro.nativelist.test/android.test.InstrumentationTestRunner
```

Require `OK (3 tests)` in the instrumentation output; adb's shell exit status
alone does not establish test success. Coverage includes structural/font
cancellation, wallet expansion and declared opacity, translation/elevation,
detached helper auto-scroll and stale callbacks, and the next valid/unmoved drop.

## Section-index parent transitions

The same build command also includes `NativeListSectionIndexReparentingTest`.
Run it on a task-owned emulator by replacing the `-e class` value above with
`com.margelo.nitro.nativelist.NativeListSectionIndexReparentingTest` and require
`OK (3 tests)`. It exercises the production `NativeListView` with real Android
view transitions in both attachment directions, then checks delayed transition
completion, visibility/configuration changes, RTL, and detach/reattach ownership.
It does not establish physical-device interaction acceptance.
