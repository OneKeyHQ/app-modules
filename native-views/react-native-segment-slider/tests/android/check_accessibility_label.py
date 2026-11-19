"""Check the SeekBar label on the Android example's Segment Slider Test page."""

import argparse
import subprocess
from xml.etree import ElementTree


parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("--serial", required=True, help="Android emulator serial")
args = parser.parse_args()

result = subprocess.run(
    ["adb", "-s", args.serial, "exec-out", "uiautomator", "dump", "/dev/tty"],
    check=True,
    capture_output=True,
    text=True,
    timeout=30,
)
start = result.stdout.find("<hierarchy")
end = result.stdout.find("</hierarchy>", start)
if start < 0 or end < 0:
    raise SystemExit(f"Android accessibility hierarchy unavailable: {result.stdout.strip()}")

tree = ElementTree.fromstring(result.stdout[start:end + len("</hierarchy>")])
labels = [
    node.get("content-desc")
    for node in tree.iter("node")
    if node.get("package") == "com.example" and node.get("class") == "android.widget.SeekBar"
]
if "Continuous (no marks)" not in labels:
    raise SystemExit(f"Expected the example slider's label on its SeekBar node, got {labels!r}")

print("Android SeekBar exposes the caller's aria-label")
