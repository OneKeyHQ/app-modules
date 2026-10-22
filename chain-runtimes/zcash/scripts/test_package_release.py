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
    def test_prepare_writes_metadata_and_rejects_mixed_versions(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "Cargo.toml").write_text('[workspace.package]\nversion = "0.1.0-rc.1"\n')
            (root / "LICENSE").write_text("license")
            for package, name in release.PACKAGES.items():
                target = root / package
                target.mkdir()
                (target / "package.json").write_text(json.dumps({"name": name, "version": "0.1.0-rc.1"}))
            release.prepare(root)
            package = json.loads((root / "pkg/package.json").read_text())
            self.assertEqual(package["files"], [
                "onekey_zcash_runtime.js", "onekey_zcash_runtime.d.ts", "onekey_zcash_runtime_bg.wasm", "LICENSE",
            ])
            self.assertEqual((root / "pkg-keys/LICENSE").read_text(), "license")
            (root / "pkg-keys/package.json").write_text(
                json.dumps({"name": "onekey-zcash-keys", "version": "0.0.1"}))
            with self.assertRaisesRegex(ValueError, "version mismatch"):
                release.prepare(root)

    def test_prereleases_cannot_update_latest(self):
        release.check_version("0.1.0-alpha.1", "0.1.0-alpha.1", "next")
        release.check_version("0.1.0-rc.1", "0.1.0-rc.1", "next")
        release.check_version("0.1.0", "0.1.0", "latest")
        for value, tag in (("0.1.0-alpha.1", "latest"), ("0.1.0", "next")):
            with self.assertRaises(ValueError):
                release.check_version(value, value, tag)
        with self.assertRaises(ValueError):
            release.check_version("0.1.0-rc.1", "0.1.0-rc.2", "next")

    def test_missing_snippets_and_changed_bytes_are_rejected(self):
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
                "snippets/helper.js": b"snippet",
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
            pack({k: v for k, v in files.items() if not k.startswith("snippets/")})
            with self.assertRaisesRegex(ValueError, "incomplete"):
                release.verify_tarball(archive, root, name, "0.1.0-rc.1")
            pack({**files, f"{module}.js": b"changed"})
            with self.assertRaisesRegex(ValueError, "differs"):
                release.verify_tarball(archive, root, name, "0.1.0-rc.1")


if __name__ == "__main__":
    unittest.main()
