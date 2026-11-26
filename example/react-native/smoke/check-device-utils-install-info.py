import re
import subprocess
import time
import xml.etree.ElementTree as ET


PACKAGE = "com.example"
MARKER = "DeviceUtilsInstallInfo: "
DUMP_PATH = "/data/local/tmp/device-utils-install-info.xml"
INSTALL_TIME = re.compile(
    r"^[ 	]*firstInstallTime=(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})$",
    re.MULTILINE,
)


def shell(*args):
    return subprocess.check_output(["adb", "shell", *args], text=True, timeout=30)


def install_time_for_user(package_dump, user_id):
    package_fields, *user_fields = re.split(
        r"^[ \t]+User (\d+):[^\n]*\n", package_dump, flags=re.MULTILINE
    )
    user_times = {
        user: match.group(1)
        for user, fields in zip(user_fields[::2], user_fields[1::2])
        if (match := INSTALL_TIME.search(fields))
    }
    if user_times:
        return user_times.get(user_id)
    legacy_time = INSTALL_TIME.search(package_fields)
    return legacy_time.group(1) if legacy_time else None


def bridge_result(user_id):
    shell("am", "start", "--user", user_id, "-S", "-W", "-n", f"{PACKAGE}/.MainActivity")
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
    user_id = shell("am", "get-current-user").strip()
    if not re.fullmatch(r"[0-9]+", user_id):
        raise SystemExit(f"Could not identify the foreground Android user: {user_id!r}")

    packages = shell(
        "cmd", "package", "list", "packages", "--user", user_id, "-i", PACKAGE
    )
    installer = re.search(
        rf"^package:{re.escape(PACKAGE)}[ \t]+installer=([^\r\n]*)$",
        packages,
        re.MULTILINE,
    )
    package_dump = shell("dumpsys", "package", PACKAGE)
    installed = install_time_for_user(package_dump, user_id)
    if not installer or not installed:
        raise SystemExit(
            f"PackageManager did not report install info for {PACKAGE} in user {user_id}"
        )

    installer_package = installer.group(1).strip()
    expected_installer = {
        "com.android.vending": "playStore",
        "com.huawei.appmarket": "huaweiAppGallery",
        "null": "unknown",
        "": "unknown",
    }.get(installer_package, "other")
    # dumpsys renders firstInstallTime in the device's local time, to whole seconds.
    expected_seconds = int(shell("date", "-d", f"'{installed}'", "+%s"))
    result = bridge_result(user_id)
    if shell("am", "get-current-user").strip() != user_id:
        raise SystemExit("The foreground Android user changed during the smoke test")
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
    print(f"API {sdk}, user {user_id}: bridge returned {result}, matching PackageManager")


if __name__ == "__main__":
    main()
