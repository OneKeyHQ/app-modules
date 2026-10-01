#!/usr/bin/env bash
#
# 构建 wasm 产物到 pkg/。
#
# 前置：
#   1. rustup（toolchain 由 rust-toolchain.toml 锁定）
#   2. wasm-pack：cargo install wasm-pack
#   3. **llvm-ar** —— macOS 自带的 /usr/bin/ar 读不懂 wasm 目标文件，会把编译好
#      的 .o 静默打成空归档，链接期报一串 `undefined symbol: sqlite3_*`。
#      归档器由 .cargo/config.toml 指定，这里只做存在性检查给出可读提示。
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROFILE="${1:-release}"
OUT="${ROOT}/pkg"

AR="$(sed -n 's/^AR_wasm32_unknown_unknown = "\(.*\)"/\1/p' "${ROOT}/.cargo/config.toml")"
if [[ -n "${AR}" && ! -x "${AR}" ]]; then
  echo "✗ 找不到 ${AR}" >&2
  echo "  wasm 目标需要 llvm-ar；系统自带的 ar 会静默产出空归档。" >&2
  echo "  装一个：brew install llvm" >&2
  exit 1
fi

# --mode no-install 找不到 wasm-opt 只会打一行 INFO 然后跳过：产物不报错地胖
#（函数名段不剥、不做 -O）。release 必须有它。
if [ "${PROFILE}" = "release" ] && ! command -v wasm-opt >/dev/null 2>&1; then
  echo "✗ 找不到 wasm-opt（Binaryen），release 产物会不经优化直接输出" >&2
  exit 1
fi

echo "==> wasm-pack build (${PROFILE})"
cd "${ROOT}/crates/monero-runtime"
case "${PROFILE}" in
  release) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --release ;;
  profiling) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --profiling ;;
  *) wasm-pack build --mode no-install --locked --target web --out-dir "${OUT}" --dev ;;
esac

echo "==> 产物"
du -h "${OUT}"/*.wasm
