"""Build and run the production slider in a small Android test Activity.

Only Nitro/RN glue is stubbed; Android View, Canvas, text and animation are real.
Requires an already booted emulator, Android SDK build-tools 37 and kotlinc.
"""

import argparse
import json
import os
from pathlib import Path
import subprocess
import time
import zipfile


def run(*args, **kwargs):
    return subprocess.run([str(arg) for arg in args], check=True, **kwargs)


parser = argparse.ArgumentParser()
parser.add_argument("--serial", required=True)
parser.add_argument("--out", default=".tmp/segment-slider-android")
parser.add_argument("--source", type=Path)
args = parser.parse_args()
package = Path(__file__).resolve().parents[2]
output = Path(args.out).resolve()
output.mkdir(parents=True, exist_ok=True)
sdk = Path(os.environ.get("ANDROID_SDK_ROOT", Path.home() / "Library/Android/sdk"))
tools = sdk / "build-tools/37.0.0"
android = sdk / "platforms/android-34/android.jar"
adb = sdk / "platform-tools/adb"
native = package / "android/src/main/java/com/margelo/nitro/segmentslider"
env = dict(os.environ)
env["JAVA_HOME"] = subprocess.check_output(
    ["/usr/libexec/java_home", "-v", "17"], text=True
).strip()

base = "package com.margelo.nitro.segmentslider\nabstract class HybridSegmentSliderSpec {\n"
base += "abstract val view: android.view.View\n"
for name in ["defaultValue", "min", "max", "segments", "sliderHeight"]:
    base += f"abstract var {name}: Double\n"
for name in ["disabled", "showBubble", "centerOrigin", "snapTapToSegment"]:
    base += f"abstract var {name}: Boolean\n"
for name in ["fillColor", "trackColor", "thumbColor", "thumbBorderColor", "markActiveColor",
             "markInactiveColor", "markBorderColor", "bubbleColor", "bubbleTextColor"]:
    base += f"abstract var {name}: String\n"
base += """abstract var onChange: ((Double) -> Unit)?
abstract var onSlideStart: (() -> Unit)?
abstract var onSlideComplete: (() -> Unit)?
abstract fun setValue(value: Double)
open fun afterUpdate() {}
open fun dispose() {}
}
"""
(output / "HybridSegmentSliderSpec.kt").write_text(base)
(output / "ThemedReactContext.kt").write_text("""package com.facebook.react.uimanager
class ThemedReactContext(context: android.content.Context) : android.content.ContextWrapper(context)
""")
(output / "DoNotStrip.kt").write_text("""package com.facebook.proguard.annotations
annotation class DoNotStrip
""")
run("kotlinc", args.source or native / "HybridSegmentSlider.kt",
    native / "SegmentSliderColorParser.kt", package / "tests/android/BubbleLayoutActivity.kt",
    output / "HybridSegmentSliderSpec.kt", output / "ThemedReactContext.kt",
    output / "DoNotStrip.kt", "-classpath", android, "-jvm-target", "1.8",
    "-include-runtime", "-d", output / "classes.jar", env=env)
dex = output / "dex"
dex.mkdir(exist_ok=True)
run(tools / "d8", "--min-api", "26", "--lib", android, "--output", dex,
    output / "classes.jar", env=env)
(output / "AndroidManifest.xml").write_text("""<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.onekey.sliderbubbletest">
<uses-sdk android:minSdkVersion="26" android:targetSdkVersion="34"/>
<application android:debuggable="true" android:label="Slider Bubble Test" android:theme="@android:style/Theme.Material.Light.NoActionBar">
<activity android:name="com.margelo.nitro.segmentslider.BubbleLayoutActivity" android:exported="true"/>
</application></manifest>""")
unsigned = output / "unsigned.apk"
run(tools / "aapt2", "link", "-I", android, "--manifest", output / "AndroidManifest.xml",
    "-o", unsigned)
with zipfile.ZipFile(unsigned, "a") as apk:
    for path in dex.glob("*.dex"):
        apk.write(path, path.name)
run(tools / "zipalign", "-f", "4", unsigned, output / "aligned.apk")
run(tools / "apksigner", "sign", "--ks", Path.home() / ".android/debug.keystore",
    "--ks-pass", "pass:android", "--out", output / "test.apk", output / "aligned.apk", env=env)
run(adb, "-s", args.serial, "install", "-r", output / "test.apk")
run(adb, "-s", args.serial, "shell", "am", "force-stop", "com.onekey.sliderbubbletest")
run(adb, "-s", args.serial, "shell", "run-as", "com.onekey.sliderbubbletest",
    "rm", "-f", "files/result.json")
run(adb, "-s", args.serial, "shell", "am", "start", "-W", "-n",
    "com.onekey.sliderbubbletest/com.margelo.nitro.segmentslider.BubbleLayoutActivity")
for _ in range(30):
    result = subprocess.run([str(adb), "-s", args.serial, "exec-out", "run-as",
        "com.onekey.sliderbubbletest", "cat", "files/result.json"], capture_output=True)
    if result.returncode == 0:
        try:
            data = json.loads(result.stdout)
        except json.JSONDecodeError:
            time.sleep(0.5)
            continue
        (output / "result.json").write_text(json.dumps(data, indent=2))
        time.sleep(0.5)
        with (output / "screenshot.png").open("wb") as screenshot:
            run(adb, "-s", args.serial, "exec-out", "screencap", "-p", stdout=screenshot)
        print(json.dumps(data, indent=2))
        raise SystemExit(1 if data["failures"] else 0)
    time.sleep(0.5)
raise SystemExit("Native test Activity did not produce a result; inspect adb logcat")
