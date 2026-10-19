#!/usr/bin/env python3
"""Compile the actual RangeDownloader class with only Nitro-facing test stubs."""

from pathlib import Path
import hashlib
import subprocess
import tempfile


test_dir = Path(__file__).resolve().parent
package_dir = test_dir.parents[1]
source = package_dir / "ios/ReactNativeRangeDownloader.swift"
logic = package_dir / "ios/RangeDownloadLogic.swift"
swift = source.read_text()
marker = "public final class RangeDownloader:"
assert swift.count(marker) == 1
production_class = swift[swift.index(marker):]
callback = (
    "session.getAllTasks { [weak self] tasks in\n"
    "      guard let self = self, self.isActive(state) else { return }"
)
assert production_class.count(callback) == 1
production_class = production_class.replace(
    callback,
    "session.getAllTasks { [weak self] tasks in\n"
    "      defer { AuditEnumeration.done.signal() } // Test completion signal only.\n"
    "      guard let self = self, self.isActive(state) else { return }",
)

with tempfile.TemporaryDirectory(prefix="onekey-range-cancel-") as scratch:
    scratch_dir = Path(scratch)
    extracted = scratch_dir / "ProductionRangeDownloader.swift"
    executable = scratch_dir / "owner-cancellation"
    extracted.write_text(
        "import Foundation\n" + production_class
        + "\n" + (test_dir / "OwnerCancellationTests.swift").read_text()
    )
    subprocess.run(
        [
            "swiftc",
            "-parse-as-library",
            str(extracted),
            str(logic),
            str(test_dir / "Main.swift"),
            "-o",
            str(executable),
        ],
        check=True,
    )
    print(f"Production Swift SHA256: {hashlib.sha256(source.read_bytes()).hexdigest()}", flush=True)
    print("Instrumentation: one defer signal after the real permanent-fallback task enumeration", flush=True)
    subprocess.run([str(executable)], check=True)
