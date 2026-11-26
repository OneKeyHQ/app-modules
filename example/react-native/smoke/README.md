# Android DeviceUtils install info smoke test

Generate the workspace's native bindings, then build the example's release APK
with the smoke test as its JavaScript entry point. It calls the exported
DeviceUtils Nitro APIs in the example's React Native runtime. The example
also starts a background runtime, so its release APK needs `background.bundle`.

```sh
yarn install --immutable
find native-modules native-views -mindepth 2 -maxdepth 2 -name nitro.json -print0 \
  | xargs -0 -n1 -P4 sh -c 'yarn --cwd "$(dirname "$1")" nitrogen' _
yarn workspace @onekeyfe/react-native-device-utils prepare
cd example/react-native
mkdir -p android/app/src/main/assets
yarn exec react-native bundle --platform android --dev false --entry-file background.js \
  --bundle-output android/app/src/main/assets/background.bundle
cd android
ENTRY_FILE=smoke/device-utils-install-info.js \
  ./gradlew :app:assembleRelease -PreactNativeArchitectures=arm64-v8a
```

The command builds for ARM64. Set `reactNativeArchitectures` to `x86_64`
for an x86-64 emulator. On each unlocked emulator, install the APK and run
the check. Set `ANDROID_SERIAL` to select the emulator when multiple devices
are attached. Use API 29 and API 33 or newer to cover the legacy and modern
PackageManager paths.

```sh
adb install -r -i com.android.shell app/build/outputs/apk/release/app-release.apk
python3 ../smoke/check-device-utils-install-info.py
```

The explicit installer exercises the `other` enum. A fresh install with no
installer recorded exercises `unknown`. The check compares the JavaScript
installer enum and epoch-millisecond timestamp with PackageManager, whose
dump reports the install time to whole seconds. To check a secondary user,
make the installed APK available to that user with
`adb shell pm install-existing --user USER_ID com.example`. Switch to that
user and unlock the emulator before running the check. The script launches
and queries the app as the foreground user.
