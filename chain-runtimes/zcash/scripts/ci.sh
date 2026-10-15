#!/usr/bin/env bash
# CI hook: test, build and pack the npm tarballs into dist/.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."

VERSION=$(python3 -c 'import tomllib; print(tomllib.load(open("Cargo.toml", "rb"))["workspace"]["package"]["version"])')
TAG=latest
if [[ "$VERSION" =~ - ]]; then TAG=next; fi

bash scripts/vendor-deps.sh
python3 -m unittest discover -s scripts -p 'test_*.py'
cargo test --workspace --locked
bash scripts/build-wasm.sh --offline
python3 scripts/package-release.py pack --version "$VERSION" --tag "$TAG" --revision "$(git rev-parse HEAD)"

# Fixed file names for the publish workflow.
mv "dist/onekey-zcash-keys-$VERSION.tgz" dist/onekey-zcash-keys.tgz
mv "dist/onekey-zcash-runtime-$VERSION.tgz" dist/onekey-zcash-runtime.tgz
