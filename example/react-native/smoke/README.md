# Android DeviceUtils install info smoke test

Generate the workspace's native bindings, then build the example's release APK
with the smoke test as its JavaScript entry point. This entry point calls the
exported DeviceUtils Nitro APIs in the example's React Native runtime.

```sh
yarn install --immutable
find native-modules native-views -mindepth 2 -maxdepth 2 -name nitro.json -print0 \
  | xargs -0 -n1 -P4 sh -c 'yarn --cwd "$(dirname "$1")" nitrogen' _
yarn workspace @onekeyfe/react-native-device-utils prepare
cd example/react-native/android
ENTRY_FILE=smoke/device-utils-install-info.js \
  ./gradlew :app:assembleRelease -PreactNativeArchitectures=arm64-v8a
```

The command builds for ARM64. Set `reactNativeArchitectures` to `x86_64`
for an x86-64 emulator. On each unlocked emulator, install the APK and run
the check. Set `ANDROID_SERIAL` to select the emulator when multiple devices
are attached. Use API 29 and API 33 or newer to cover the legacy and modern
PackageManager paths.

```sh
adb install -r app/build/outputs/apk/release/app-release.apk
python3 ../smoke/check-device-utils-install-info.py
```

The check compares the JavaScript installer enum and the epoch-millisecond
timestamp with PackageManager's package listing and install time. The
PackageManager dump exposes the install time to whole seconds.
