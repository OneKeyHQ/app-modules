#!/bin/zsh
set -eu
if [[ $# != 2 ]]; then print -u2 'Usage: run.sh <external-device-set> <device-UDID>'; exit 2; fi
test_set="$1"
test_device="$2"
[[ "$test_set" == /Volumes/T7Shield/Project/* ]] || exit 2
package_dir="${0:A:h:h:h}"
output_dir="$package_dir/node_modules/.cache/ios-window-tests"
mkdir -p "$output_dir/OverlayWindowTests.app" "$output_dir/ModuleCache"
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer
xcrun --sdk iphonesimulator swiftc -parse-as-library \
  "$package_dir/tests/ios/WindowManagerTests.swift" \
  "$package_dir/ios/NativeOverlayWindowManager.swift" \
  "$package_dir/ios/NativeOverlaySheetController.swift" \
  "$package_dir/ios/NativeOverlayAnimation.swift" \
  -sdk "$(xcrun --sdk iphonesimulator --show-sdk-path)" \
  -target arm64-apple-ios18.0-simulator -module-cache-path "$output_dir/ModuleCache" \
  -framework UIKit -o "$output_dir/OverlayWindowTests.app/OverlayWindowTests"
cat > "$output_dir/OverlayWindowTests.app/Info.plist" <<'PLIST'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0"><dict>
<key>CFBundleIdentifier</key><string>so.onekey.nativeoverlay.tests</string>
<key>CFBundleExecutable</key><string>OverlayWindowTests</string>
<key>CFBundleName</key><string>OverlayWindowTests</string>
<key>CFBundlePackageType</key><string>APPL</string>
<key>CFBundleVersion</key><string>1</string><key>MinimumOSVersion</key><string>18.0</string>
<key>UIDeviceFamily</key><array><integer>1</integer><integer>2</integer></array>
<key>UILaunchScreen</key><dict/>
<key>UIApplicationSceneManifest</key><dict><key>UIApplicationSupportsMultipleScenes</key><false/></dict>
</dict></plist>
PLIST
codesign --force --sign - "$output_dir/OverlayWindowTests.app"
xcrun simctl --set "$test_set" install "$test_device" "$output_dir/OverlayWindowTests.app"
xcrun simctl --set "$test_set" terminate "$test_device" so.onekey.nativeoverlay.tests 2>/dev/null || true
container="$(xcrun simctl --set "$test_set" get_app_container "$test_device" so.onekey.nativeoverlay.tests data)"
rm -f "$container/Documents/results.json"
xcrun simctl --set "$test_set" launch "$test_device" so.onekey.nativeoverlay.tests
for iteration in {1..150}; do
  if [[ -f "$container/Documents/results.json" ]]; then
    cp "$container/Documents/results.json" "$output_dir/results.json"
    cat "$output_dir/results.json"
    python3 -c 'import json,sys; sys.exit(json.load(open(sys.argv[1]))["outcome"] != "passed")' "$output_dir/results.json"
    exit $?
  fi
  sleep 0.2
done
print -u2 'Native test watchdog expired; no native completion result was received.'
exit 1
