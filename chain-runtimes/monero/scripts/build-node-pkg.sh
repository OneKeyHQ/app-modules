#!/usr/bin/env bash
# 出一个 nodejs target 的包，只用于本机冒烟（scripts/smoke.js）。
# 出厂用的是 build-wasm.sh 的 web target。
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}/crates/monero-runtime"
wasm-pack build --mode no-install --locked --target nodejs \
  --out-dir "${ROOT}/pkg-node" --release
