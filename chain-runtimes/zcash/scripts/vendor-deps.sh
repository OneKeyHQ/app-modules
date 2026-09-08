#!/usr/bin/env bash
# Reconstruct the exact upstream crates and checked-in patches.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
exec python3 "${ROOT}/scripts/prepare-vendor.py" "$@"
