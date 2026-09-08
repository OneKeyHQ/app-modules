import hashlib
import importlib.util
import io
import json
from pathlib import Path
import tarfile
import tempfile
import unittest
from unittest.mock import patch


SCRIPT = Path(__file__).with_name("prepare-vendor.py")
SPEC = importlib.util.spec_from_file_location("prepare_vendor", SCRIPT)
vendor = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(vendor)


class VendorTests(unittest.TestCase):
    def setUp(self):
        parent = SCRIPT.parent.parent / "target" / "vendor-tests"
        parent.mkdir(parents=True, exist_ok=True)
        self.temporary = tempfile.TemporaryDirectory(dir=parent)
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.identity = "fixture-1.0.0"
        self.archive = self.root / "vendor/.archives/fixture-1.0.0.crate"
        self.archive.parent.mkdir(parents=True)
        self.write_archive("fixture-1.0.0/src/lib.rs", b"before\n")
        (self.root / "patches").mkdir()
        (self.root / "patches/change.patch").write_text(
            "--- a/src/lib.rs\n+++ b/src/lib.rs\n@@ -1 +1 @@\n-before\n+after\n"
        )
        (self.root / "patches/manifest.json").write_text(json.dumps([{
            "name": "fixture", "version": "1.0.0", "patch": "change.patch",
            "sha256": hashlib.sha256(self.archive.read_bytes()).hexdigest(),
        }]))
        self.addCleanup(patch.stopall)
        patch.object(vendor, "ROOT", self.root).start()
        patch.dict("os.environ", {"CARGO_HOME": str(self.root / "cargo")}).start()

    def write_archive(self, name, data):
        with tarfile.open(self.archive, "w:gz") as contents:
            member = tarfile.TarInfo(name)
            member.size = len(data)
            contents.addfile(member, io.BytesIO(data))

    def test_reconstructs_the_patch_and_verifies_an_existing_tree(self):
        vendor.prepare(offline=True)
        vendor.prepare(offline=True)
        self.assertEqual((self.root / "vendor/fixture-1.0.0/src/lib.rs").read_text(), "after\n")

    def test_refuses_drift_without_overwriting_existing_sources(self):
        vendor.prepare(offline=True)
        source = self.root / "vendor/fixture-1.0.0/src/lib.rs"
        source.write_text("local change\n")
        with self.assertRaisesRegex(RuntimeError, "Existing vendor files differ"):
            vendor.prepare(offline=True)
        self.assertEqual(source.read_text(), "local change\n")

    def test_refuses_an_archive_with_the_wrong_checksum(self):
        self.archive.write_bytes(b"not the pinned archive")
        with self.assertRaisesRegex(RuntimeError, "checksum mismatch"):
            vendor.prepare(offline=True)
        self.assertFalse((self.root / "vendor/fixture-1.0.0").exists())

    def test_offline_never_downloads_a_missing_archive(self):
        self.archive.unlink()
        with patch.object(vendor.urllib.request, "urlopen") as download:
            with self.assertRaisesRegex(RuntimeError, "Missing cached archive"):
                vendor.prepare(offline=True)
            download.assert_not_called()

    def test_refuses_archive_path_traversal(self):
        self.write_archive("fixture-1.0.0/../../escape", b"fixture")
        with self.assertRaisesRegex(RuntimeError, "Invalid crate archive path"):
            vendor.extract_crate(self.archive, self.root / "extracted", self.identity)
        self.assertFalse((self.root / "escape").exists())

    def test_refuses_archive_links(self):
        with tarfile.open(self.archive, "w:gz") as contents:
            member = tarfile.TarInfo("fixture-1.0.0/link")
            member.type = tarfile.SYMTYPE
            member.linkname = "../../escape"
            contents.addfile(member)
        with self.assertRaisesRegex(RuntimeError, "Unsupported crate archive entry"):
            vendor.extract_crate(self.archive, self.root / "extracted", self.identity)


if __name__ == "__main__":
    unittest.main()
