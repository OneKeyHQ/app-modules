#!/usr/bin/env bash
# CI hook: test, build and pack the npm tarball into dist/.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."

VERSION=$(python3 -c 'import tomllib; print(tomllib.load(open("Cargo.toml", "rb"))["workspace"]["package"]["version"])')
TAG=latest
if [[ "$VERSION" == *-* ]]; then TAG=next; fi

cargo test --workspace --locked
bash scripts/build-wasm.sh release
python3 scripts/package-release.py --version "$VERSION" --tag "$TAG"
