#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODULE_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"
APP_MODULES_DIR="$(cd "${MODULE_DIR}/../.." && pwd)"
ANDROID_EXAMPLE_DIR="${APP_MODULES_DIR}/example/react-native/android"
REPORT_DIR="${FIRMWARE_REPORT_DIR:-"${SCRIPT_DIR}/reports/android"}"
SERVER_LOG="${REPORT_DIR}/server.log"
MODE="${1:-full}"

mkdir -p "${REPORT_DIR}"
"${SCRIPT_DIR}/generate-test-cert.sh" >/dev/null

node "${SCRIPT_DIR}/server.js" >"${SERVER_LOG}" 2>&1 &
SERVER_PID=$!
cleanup() {
  kill "${SERVER_PID}" >/dev/null 2>&1 || true
  wait "${SERVER_PID}" >/dev/null 2>&1 || true
}
trap cleanup EXIT

for _ in $(seq 1 50); do
  if curl \
    --silent \
    --fail \
    --noproxy '*' \
    --cacert "${SCRIPT_DIR}/.certs/ca.crt" \
    --resolve 'firmware.test:9443:127.0.0.1' \
    'https://firmware.test:9443/health' >/dev/null; then
    break
  fi
  sleep 0.1
done

node --test "${SCRIPT_DIR}/server.contract.js"
(
  cd "${ANDROID_EXAMPLE_DIR}"
  ./gradlew \
    :onekeyfe_react-native-sni-connect:testDebugUnitTest \
    :onekeyfe_react-native-range-downloader:testDebugUnitTest \
    :app:assembleDebug \
    --no-daemon
)

if [[ "${MODE}" == 'build-only' ]]; then
  exit 0
fi

if [[ -z "${FIRMWARE_ANDROID_DRIVER:-}" ]]; then
  echo 'FIRMWARE_ANDROID_DRIVER is required for device/runtime conformance' >&2
  exit 2
fi

for profile in small large; do
  for trial in 1 2 3; do
    trial_directory="${REPORT_DIR}/${profile}-${trial}"
    mkdir -p "${trial_directory}"
    FIRMWARE_TEST_CA_CERT="${SCRIPT_DIR}/.certs/ca.crt" \
      FIRMWARE_TEST_MANIFEST_URL='https://10.0.2.2:9443/manifest' \
      FIRMWARE_TEST_PROFILE="${profile}" \
      FIRMWARE_TEST_REPORT_DIR="${trial_directory}" \
      "${FIRMWARE_ANDROID_DRIVER}"
  done
done
