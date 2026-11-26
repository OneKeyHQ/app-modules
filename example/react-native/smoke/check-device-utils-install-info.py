import re
import subprocess
import time
import xml.etree.ElementTree as ET


PACKAGE = "com.example"
MARKER = "DeviceUtilsInstallInfo: "
DUMP_PATH = "/data/local/tmp/device-utils-install-info.xml"


def shell(*args):
    return subprocess.check_output(["adb", "shell", *args], text=True, timeout=30)


def bridge_result():
    shell("am", "start", "-S", "-W", "-n", f"{PACKAGE}/.MainActivity")
    deadline = time.monotonic() + 45
    while time.monotonic() < deadline:
        shell("rm", "-f", DUMP_PATH)
        if DUMP_PATH in shell("uiautomator", "dump", DUMP_PATH):
            xml = shell("cat", DUMP_PATH)
            shell("rm", "-f", DUMP_PATH)
            for node in ET.fromstring(xml).iter("node"):
                if node.get("package") != PACKAGE:
                    continue
                label = node.get("content-desc") or node.get("text", "")
                if label.startswith(MARKER):
                    result = label.removeprefix(MARKER)
                    if result != "pending":
                        return result
        time.sleep(1)
    raise SystemExit("No DeviceUtils result appeared in the example app")


def main():
    packages = shell("cmd", "package", "list", "packages", "-i", PACKAGE)
    installer = re.search(
        rf"^package:{re.escape(PACKAGE)}[ \t]+installer=([^\r\n]*)$",
        packages,
        re.MULTILINE,
    )
    package_dump = shell("dumpsys", "package", PACKAGE)
    installed = re.search(
        r"^[ \t]*firstInstallTime=(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})$",
        package_dump,
        re.MULTILINE,
    )
    if not installer or not installed:
        raise SystemExit(f"PackageManager did not report install info for {PACKAGE}")

    installer_package = installer.group(1).strip()
    expected_installer = {
        "com.android.vending": "playStore",
        "com.huawei.appmarket": "huaweiAppGallery",
        "null": "unknown",
        "": "unknown",
    }.get(installer_package, "other")
    # dumpsys renders firstInstallTime in the device's local time, to whole seconds.
    expected_seconds = int(shell("date", "-d", f"'{installed.group(1)}'", "+%s"))
    result = bridge_result()
    value = re.fullmatch(r"(\w+) ([1-9][0-9]*)", result)
    if (
        not value
        or value.group(1) != expected_installer
        or int(value.group(2)) // 1000 != expected_seconds
    ):
        raise SystemExit(
            f"Bridge returned {result!r}, expected {expected_installer} "
            f"at {expected_seconds} epoch seconds"
        )
    sdk = shell("getprop", "ro.build.version.sdk").strip()
    print(f"API {sdk}: bridge returned {result}, matching PackageManager")


if __name__ == "__main__":
    main()
