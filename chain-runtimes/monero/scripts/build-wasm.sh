#!/usr/bin/env bash
#
# Build the wasm package into pkg/.
#
# Requires:
#   1. rustup (toolchain set by rust-toolchain.toml)
#   2. wasm-pack: cargo install wasm-pack
#   3. llvm-ar, configured in .cargo/config.toml. macOS /usr/bin/ar silently
#      writes empty archives for wasm objects (`undefined symbol: sqlite3_*`).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROFILE="${1:-release}"
OUT="${ROOT}/pkg"

# CI sets AR_wasm32_unknown_unknown itself; cargo's [env] does not override it.
AR="${AR_wasm32_unknown_unknown:-$(sed -n 's/^AR_wasm32_unknown_unknown = "\(.*\)"/\1/p' "${ROOT}/.cargo/config.toml")}"
if [[ -n "${AR}" && ! -x "${AR}" ]]; then
  echo "✗ ${AR} not found" >&2
  echo "  wasm targets need llvm-ar; the system ar silently writes empty archives." >&2
  echo "  install it: brew install llvm" >&2
  exit 1
fi

# With --mode no-install, a missing wasm-opt is only logged and skipped, leaving
# an unoptimized output. Release builds require it.
if [ "${PROFILE}" = "release" ] && ! command -v wasm-opt >/dev/null 2>&1; then
  echo "✗ wasm-opt (Binaryen) not found; the release build would be unoptimized" >&2
  exit 1
fi

echo "==> wasm-pack build (${PROFILE})"
cd "${ROOT}/crates/monero-runtime"
case "${PROFILE}" in
  release) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --release ;;
  profiling) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --profiling ;;
  *) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --dev ;;
esac

echo "==> output"
du -h "${OUT}"/*.wasm
