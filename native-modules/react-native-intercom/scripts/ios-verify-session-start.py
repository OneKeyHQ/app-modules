#!/usr/bin/env python3
"""Focused session-token state checks; diagnostics never enter product sources."""
import hashlib
import json
import pathlib
import re
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-session-start"
output.mkdir(parents=True, exist_ok=True)
sources = [package / "ios/Session" / name for name in ["ICMSessionStartAttempt.m", "ICMSessionManager.m"]]
fixture = package / "scripts/ios-session-start-tests.m"
commands = []

def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr)
        result.check_returncode()
    return result

binary = output / "session-start-tests"
run(["xcrun", "clang", "-fobjc-arc", "-fmodules", "-Wall", "-Wextra", "-Werror", *map(str, sources), str(fixture), "-framework", "Foundation", "-o", str(binary)])
result = run([str(binary)])
(output / "host-tests.stdout").write_text(result.stdout)
receipt = {"status": "SOURCE_HOST_BRANCH_TESTED_PRODUCT_UNLINKED", "assertions": int(re.search(r"PASS (\d+)", result.stdout)[1]), "scope": "SessionManager/StartAttempt actual source with test-only Config threshold and record sink; not full product session or original dynamic comparison", "fixtureImplementationsUsedOnlyInHostTest": ["FixtureSessionConfig", "FixtureSessionRecorder"], "originalSDKCompileOrLinkInput": False, "networkTasksCreated": 0, "defaultsOrKeychainAccessed": False, "inputs": {str(path.relative_to(package)): hashlib.sha256(path.read_bytes()).hexdigest() for path in [*sources, fixture, package / "ios/Session/ICMSessionManager.h", package / "ios/Session/ICMSessionStartAttempt.h"]}, "commands": commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(result.stdout.strip())
print(output / "receipt.json")
