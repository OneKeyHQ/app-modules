#!/usr/bin/env bash
#
# Build the wasm package into pkg/.
#
# Requires:
#   1. rustup with the toolchain pinned in rust-toolchain.toml
#   2. wasm-pack: cargo install wasm-pack
#   3. wasm-bindgen-cli matching Cargo.lock
#   4. A clang with wasm32 support and its llvm-ar (brew install llvm on macOS).
#      macOS /usr/bin/ar silently writes empty archives for wasm objects
#      (`undefined symbol: sqlite3_*`).
#   5. wasm-opt (Binaryen) for release builds
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROFILE="${1:-release}"
OUT="${OUT:-${ROOT}/pkg}"
# web for the App; build-node-pkg.sh passes nodejs for the smoke test.
TARGET="${WASM_PACK_TARGET:-web}"

# rustup run pins the toolchain even where a version manager overrides
# rust-toolchain.toml.
RUST_TOOLCHAIN="$(python3 -c 'import sys, tomllib; print(tomllib.load(open(sys.argv[1], "rb"))["toolchain"]["channel"])' "${ROOT}/rust-toolchain.toml")"
rustup run "${RUST_TOOLCHAIN}" rustc --version

pick_clang() {
  for c in "${CC_wasm32_unknown_unknown:-}" \
           /opt/homebrew/opt/llvm/bin/clang \
           /usr/local/opt/llvm/bin/clang "$(command -v clang || true)"; do
    [ -n "$c" ] && [ -x "$c" ] || continue
    if echo 'int main(void){return 0;}' > /tmp/_wasmprobe.c 2>/dev/null &&
       "$c" --target=wasm32-unknown-unknown -nostdlib -c /tmp/_wasmprobe.c -o /tmp/_wasmprobe.o 2>/dev/null; then
      echo "$c"; return 0
    fi
  done
  return 1
}
if ! CLANG="$(pick_clang)"; then
  echo "✗ no clang with wasm32 support found; on macOS run brew install llvm" >&2
  exit 1
fi
export CC_wasm32_unknown_unknown="${CLANG}"
# An exported value wins over .cargo/config.toml's [env] default.
export AR_wasm32_unknown_unknown="${AR_wasm32_unknown_unknown:-$(dirname "${CLANG}")/llvm-ar}"
if [[ ! -x "${AR_wasm32_unknown_unknown}" ]]; then
  echo "✗ ${AR_wasm32_unknown_unknown} not found" >&2
  echo "  wasm targets need llvm-ar; the system ar silently writes empty archives." >&2
  exit 1
fi
echo "==> Using clang: ${CLANG}"

# With --mode no-install, a missing wasm-opt is only logged and skipped, leaving
# an unoptimized output. Release builds require it.
if [ "${PROFILE}" = "release" ] && ! command -v wasm-opt >/dev/null 2>&1; then
  echo "✗ wasm-opt (Binaryen) not found; the release build would be unoptimized" >&2
  exit 1
fi

echo "==> wasm-pack build (${PROFILE})"
cd "${ROOT}/crates/monero-runtime"
case "${PROFILE}" in
  release) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target "${TARGET}" --out-dir "${OUT}" --release ;;
  profiling) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target "${TARGET}" --out-dir "${OUT}" --profiling ;;
  *) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target "${TARGET}" --out-dir "${OUT}" --dev ;;
esac

echo "==> output"
du -h "${OUT}"/*.wasm
