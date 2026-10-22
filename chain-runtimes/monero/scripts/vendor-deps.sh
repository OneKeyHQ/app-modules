#!/usr/bin/env bash
# Reconstruct the patched OPFS VFS from upstream and the Zcash runtime's patch,
# so both runtimes run the same storage layer.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
exec python3 "${ROOT}/../zcash/scripts/prepare-vendor.py" --root "${ROOT}" "$@"
