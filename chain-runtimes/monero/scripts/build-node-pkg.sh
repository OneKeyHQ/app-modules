#!/usr/bin/env bash
# Build a nodejs-target package for local smoke tests (scripts/smoke.js) only.
# Shipped builds use the web target from build-wasm.sh.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
WASM_PACK_TARGET=nodejs OUT="${ROOT}/pkg-node" exec bash "${ROOT}/scripts/build-wasm.sh" release
