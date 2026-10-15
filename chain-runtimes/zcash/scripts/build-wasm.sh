#!/usr/bin/env bash
# Build the browser wasm packages.
#
# Prerequisites:
#   1. wasm32-unknown-unknown target: rustup target add wasm32-unknown-unknown
#   2. A clang that targets wasm32. Apple clang does not ("No available targets
#      are compatible with triple wasm32-unknown-unknown"); use brew install llvm.
#      (secp256k1-sys ships its own wasm sysroot; it only needs the compiler.)
#   3. wasm-pack: cargo install wasm-pack
#
# Usage:
#   bash scripts/build-wasm.sh            # release
#   PROFILE=dev bash scripts/build-wasm.sh

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROFILE="${PROFILE:-release}"
OUT="${OUT:-${ROOT}/pkg}"

if ! python3 -c 'import tomllib' 2>/dev/null; then
  echo "Python 3.11+ is required; select it in PATH before building." >&2
  exit 1
fi
RUST_TOOLCHAIN="$(python3 -c 'import sys, tomllib; print(tomllib.load(open(sys.argv[1], "rb"))["toolchain"]["channel"])' "${ROOT}/rust-toolchain.toml")"
rustup run "${RUST_TOOLCHAIN}" rustc --version

# Find a clang that can compile for wasm32.
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
  echo "Error: no clang with wasm32 support found." >&2
  echo "       On macOS run brew install llvm (Apple clang lacks the wasm32 target)." >&2
  exit 1
fi
echo "==> Using clang: ${CLANG}"

export CC_wasm32_unknown_unknown="${CLANG}"
export AR_wasm32_unknown_unknown="${AR_wasm32_unknown_unknown:-$(dirname "${CLANG}")/llvm-ar}"

bash "${ROOT}/scripts/vendor-deps.sh" "$@"

# With --mode no-install a missing wasm-opt is only an INFO line and the output
# silently doubles in size. Release requires it.
if [ "${PROFILE}" = "release" ] && ! command -v wasm-opt >/dev/null 2>&1; then
  echo "✗ wasm-opt (Binaryen) not found; release output would be unoptimized" >&2
  exit 1
fi

echo "==> wasm-pack build (${PROFILE})"
cd "${ROOT}/crates/zcash-runtime"
case "${PROFILE}" in
  release) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --release ;;
  # Optimized but keeps the name section, which wasm panic stacks need.
  # --dev would make Halo2 proving unusably slow.
  profiling) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --profiling ;;
  *) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --dev ;;
esac

echo "==> Artifacts"
ls -lh "${OUT}"/*.wasm | awk '{print "   ", $9, $5}'

echo "==> Building storage benchmark"
(cd "${ROOT}/crates/zcash-storage-benchmark" && \
  rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${ROOT}/pkg/storage-benchmark" --release)

# Always rebuild the keys package too, so the host never pairs a fresh runtime
# with a stale keys build.
echo "==> Building onekey-zcash-keys"
(cd "${ROOT}/crates/zcash-keys-runtime" && rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${ROOT}/pkg-keys" --release)
echo "Artifacts: $(du -h "${ROOT}"/pkg/*.wasm "${ROOT}"/pkg-keys/*.wasm "${ROOT}"/pkg/storage-benchmark/*.wasm | tr '\n' ' ')"

if [ "${OUT}" = "${ROOT}/pkg" ]; then
  python3 "${ROOT}/scripts/package-release.py" prepare
  python3 "${ROOT}/scripts/artifact-manifest.py" write
else
  echo "Custom runtime output: no complete artifact manifest produced."
fi
