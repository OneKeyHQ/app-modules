#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
TEST_IOS = ROOT / "scripts" / "test_ios_sdk.py"
TEST_ANDROID = ROOT / "scripts" / "test_android_sdk.py"


def run_step(script: Path) -> None:
    proc = subprocess.run([sys.executable, str(script)], text=True)
    if proc.returncode != 0:
        raise SystemExit(proc.returncode)


def main() -> int:
    print("=== Running iOS Clean SDK Verification & E2E Tests ===")
    run_step(TEST_IOS)
    print("\n=== Running Android Clean SDK Verification & E2E Tests ===")
    run_step(TEST_ANDROID)
    print("\n=== ALL CLEAN SDK VERIFICATION & E2E TESTS PASSED ===")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
