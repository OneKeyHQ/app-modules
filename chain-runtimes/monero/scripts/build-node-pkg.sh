#!/usr/bin/env bash
# Build a nodejs-target package for local smoke tests (scripts/smoke.js) only.
# Shipped builds use the web target from build-wasm.sh.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}/crates/monero-runtime"
wasm-pack build --mode no-install --locked --target nodejs \
  --out-dir "${ROOT}/pkg-node" --release
