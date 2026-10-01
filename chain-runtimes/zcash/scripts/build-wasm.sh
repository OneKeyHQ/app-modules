#!/usr/bin/env bash
# 构建浏览器可用的 wasm 包。
#
# 前置条件：
#   1. wasm32-unknown-unknown target：rustup target add wasm32-unknown-unknown
#   2. 支持 wasm32 的 clang。macOS 自带的 Apple clang **不支持**，会报
#      "No available targets are compatible with triple wasm32-unknown-unknown"。
#      用 Homebrew 的：brew install llvm
#      （secp256k1-sys 自带 wasm sysroot，只是需要一个能产 wasm 的 clang。）
#   3. wasm-pack：cargo install wasm-pack
#
# 用法：
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

# 找一个能编 wasm32 的 clang
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
  echo "错误：找不到支持 wasm32 的 clang。" >&2
  echo "      macOS 请先 brew install llvm（Apple clang 不支持 wasm32 目标）。" >&2
  exit 1
fi
echo "==> 使用 clang: ${CLANG}"

export CC_wasm32_unknown_unknown="${CLANG}"
export AR_wasm32_unknown_unknown="${AR_wasm32_unknown_unknown:-$(dirname "${CLANG}")/llvm-ar}"

bash "${ROOT}/scripts/vendor-deps.sh" "$@"

# --mode no-install 找不到 wasm-opt 只会打一行 INFO 然后跳过：产物不报错地胖一倍
#（函数名段不剥、不做 -O）。release 必须有它。
if [ "${PROFILE}" = "release" ] && ! command -v wasm-opt >/dev/null 2>&1; then
  echo "✗ 找不到 wasm-opt（Binaryen），release 产物会不经优化直接输出" >&2
  exit 1
fi

echo "==> wasm-pack build (${PROFILE})"
cd "${ROOT}/crates/zcash-runtime"
case "${PROFILE}" in
  release) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --release ;;
  # 优化照旧，但保留名字段。wasm 里的 panic 只给函数序号，没有它就只能靠
  # 通读依赖去猜是谁调的 —— 而 --dev 会让 Halo2 证明慢到不可用。
  profiling) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --profiling ;;
  *) rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --dev ;;
esac

echo "==> 产物"
ls -lh "${OUT}"/*.wasm | awk '{print "   ", $9, $5}'

echo "==> 构建 storage benchmark"
(cd "${ROOT}/crates/zcash-storage-benchmark" && \
  rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${ROOT}/pkg/storage-benchmark" --release)

# keys 包必须一起构建。只建 runtime 会让宿主用到过期的 keys 产物，
# 而那种不一致极难从现象反推（接口存在、行为却是旧的）。
echo "==> 构建 onekey-zcash-keys"
(cd "${ROOT}/crates/zcash-keys-runtime" && rustup run "${RUST_TOOLCHAIN}" wasm-pack build --mode no-install --locked --target web --out-dir "${ROOT}/pkg-keys" --release)
echo "产物：$(du -h "${ROOT}"/pkg/*.wasm "${ROOT}"/pkg-keys/*.wasm "${ROOT}"/pkg/storage-benchmark/*.wasm | tr '\n' ' ')"

if [ "${OUT}" = "${ROOT}/pkg" ]; then
  python3 "${ROOT}/scripts/package-release.py" prepare
  python3 "${ROOT}/scripts/artifact-manifest.py" write
else
  echo "Custom runtime output: no complete artifact manifest produced."
fi
