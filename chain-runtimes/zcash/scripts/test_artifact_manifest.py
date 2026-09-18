import importlib.util
from pathlib import Path
import tempfile
import unittest


spec = importlib.util.spec_from_file_location("artifact_manifest", Path(__file__).with_name("artifact-manifest.py"))
artifact = importlib.util.module_from_spec(spec)
spec.loader.exec_module(artifact)


class ArtifactManifestTests(unittest.TestCase):
    def test_all_three_packages_and_exact_inventory_are_required(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            for package, module in artifact.PACKAGES.items():
                target = root / package
                target.mkdir(parents=True, exist_ok=True)
                for name in ("package.json", f"{module}.js", f"{module}.d.ts", f"{module}_bg.wasm"):
                    (target / name).write_text("synthetic artifact")
            files = artifact.artifact_files(root)
            self.assertEqual(len(files), 12)
            path = root / "pkg/storage-benchmark/onekey_zcash_storage_benchmark_bg.wasm"
            path.unlink()
            with self.assertRaisesRegex(ValueError, "Incomplete artifact"):
                artifact.artifact_files(root)
            path.write_text("changed artifact")
            self.assertNotEqual(files, artifact.artifact_files(root))

    def test_symlinks_are_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "pkg").mkdir()
            (root / "pkg/escape").symlink_to(root)
            with self.assertRaisesRegex(ValueError, "Artifact symlink"):
                artifact.artifact_files(root)


if __name__ == "__main__":
    unittest.main()
