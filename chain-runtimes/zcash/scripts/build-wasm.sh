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

echo "==> wasm-pack build (${PROFILE})"
cd "${ROOT}/crates/zcash-runtime"
case "${PROFILE}" in
  release) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --release ;;
  # 优化照旧，但保留名字段。wasm 里的 panic 只给函数序号，没有它就只能靠
  # 通读依赖去猜是谁调的 —— 而 --dev 会让 Halo2 证明慢到不可用。
  profiling) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --profiling ;;
  *) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --dev ;;
esac

echo "==> 产物"
ls -lh "${OUT}"/*.wasm | awk '{print "   ", $9, $5}'

echo "==> 构建 storage benchmark"
(cd "${ROOT}/crates/zcash-storage-benchmark" && \
  wasm-pack build --mode no-install --locked --target web --out-dir "${ROOT}/pkg/storage-benchmark" --release)

# keys 包必须一起构建。只建 runtime 会让宿主用到过期的 keys 产物，
# 而那种不一致极难从现象反推（接口存在、行为却是旧的）。
echo "==> 构建 onekey-zcash-keys"
(cd "${ROOT}/crates/zcash-keys-runtime" && wasm-pack build --mode no-install --locked --target web --out-dir "${ROOT}/pkg-keys" --release)
echo "产物：$(du -h "${ROOT}"/pkg/*.wasm "${ROOT}"/pkg-keys/*.wasm "${ROOT}"/pkg/storage-benchmark/*.wasm | tr '\n' ' ')"
