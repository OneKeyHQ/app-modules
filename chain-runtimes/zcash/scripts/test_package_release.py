import importlib.util
import io
import json
from pathlib import Path
import tarfile
import tempfile
import unittest


spec = importlib.util.spec_from_file_location("release", Path(__file__).with_name("package-release.py"))
release = importlib.util.module_from_spec(spec)
spec.loader.exec_module(release)


class PackageReleaseTests(unittest.TestCase):
    def test_prepare_preserves_benchmark_and_rejects_mixed_versions(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "Cargo.toml").write_text('[workspace.package]\nversion = "0.1.0-rc.1"\n')
            (root / "LICENSE").write_text("license")
            for package, name in release.PACKAGES.items():
                target = root / package
                target.mkdir()
                (target / "package.json").write_text(json.dumps({"name": name, "version": "0.1.0-rc.1"}))
            benchmark = root / "pkg/storage-benchmark"
            benchmark.mkdir()
            (benchmark / ".gitignore").write_text("*\n")
            (benchmark / "package.json").write_text(json.dumps({"version": "0.1.0-rc.1"}))
            release.prepare(root)
            self.assertFalse((benchmark / ".gitignore").exists())
            package = json.loads((root / "pkg/package.json").read_text())
            self.assertIn("storage-benchmark", package["files"])
            self.assertEqual((root / "pkg-keys/LICENSE").read_text(), "license")
            (benchmark / "package.json").write_text(json.dumps({"version": "0.0.1"}))
            with self.assertRaisesRegex(ValueError, "same version"):
                release.prepare(root)

    def test_prereleases_cannot_update_latest(self):
        release.check_version("0.1.0-alpha.1", "0.1.0-alpha.1", "alpha")
        release.check_version("0.1.0-rc.1", "0.1.0-rc.1", "rc")
        release.check_version("0.1.0", "0.1.0", "latest")
        for expected, tag in (("0.1.0-rc.1", "latest"), ("0.1.0-rc.2", "rc")):
            with self.assertRaises(ValueError):
                release.check_version("0.1.0-rc.1", expected, tag)
        for tag in ("rc", "latest"):
            with self.assertRaises(ValueError):
                release.check_version("0.1.0-alpha.1", "0.1.0-alpha.1", tag)
        with self.assertRaises(ValueError):
            release.check_version("0.1.0-beta.1", "0.1.0-beta.1", "rc")

    def test_missing_benchmark_and_changed_bytes_are_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            name = "onekey-zcash-runtime"
            module = name.replace("-", "_")
            files = {
                "package.json": json.dumps({"name": name, "version": "0.1.0-rc.1"}).encode(),
                "LICENSE": b"license",
                f"{module}.js": b"javascript",
                f"{module}.d.ts": b"types",
                f"{module}_bg.wasm": b"wasm",
                "storage-benchmark/benchmark.js": b"benchmark",
            }
            for path, data in files.items():
                target = root / path
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_bytes(data)
            archive = root / "package.tgz"

            def pack(contents):
                with tarfile.open(archive, "w:gz") as tar:
                    for path, data in contents.items():
                        member = tarfile.TarInfo(f"package/{path}")
                        member.size = len(data)
                        tar.addfile(member, io.BytesIO(data))

            pack(files)
            release.verify_tarball(archive, root, name, "0.1.0-rc.1")
            pack({k: v for k, v in files.items() if not k.startswith("storage-benchmark/")})
            with self.assertRaisesRegex(ValueError, "incomplete"):
                release.verify_tarball(archive, root, name, "0.1.0-rc.1")
            pack({**files, f"{module}.js": b"changed"})
            with self.assertRaisesRegex(ValueError, "differs"):
                release.verify_tarball(archive, root, name, "0.1.0-rc.1")


if __name__ == "__main__":
    unittest.main()
