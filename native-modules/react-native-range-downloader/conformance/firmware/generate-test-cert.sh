#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CERT_DIR="${FIRMWARE_CERT_DIR:-"${SCRIPT_DIR}/.certs"}"

mkdir -p "${CERT_DIR}"

openssl req \
  -x509 \
  -newkey rsa:2048 \
  -nodes \
  -keyout "${CERT_DIR}/ca.key" \
  -out "${CERT_DIR}/ca.crt" \
  -days 2 \
  -sha256 \
  -subj '/CN=OneKey Firmware Conformance CA' >/dev/null 2>&1

create_server_certificate() {
  local prefix="$1"
  local common_name="$2"
  local alt_names="$3"
  local extension_file="${CERT_DIR}/${prefix}.ext"

  openssl req \
    -newkey rsa:2048 \
    -nodes \
    -keyout "${CERT_DIR}/${prefix}.key" \
    -out "${CERT_DIR}/${prefix}.csr" \
    -subj "/CN=${common_name}" >/dev/null 2>&1

  {
    echo 'basicConstraints=CA:FALSE'
    echo 'keyUsage=digitalSignature,keyEncipherment'
    echo 'extendedKeyUsage=serverAuth'
    echo "subjectAltName=${alt_names}"
  } >"${extension_file}"

  openssl x509 \
    -req \
    -in "${CERT_DIR}/${prefix}.csr" \
    -CA "${CERT_DIR}/ca.crt" \
    -CAkey "${CERT_DIR}/ca.key" \
    -CAcreateserial \
    -out "${CERT_DIR}/${prefix}.crt" \
    -days 2 \
    -sha256 \
    -extfile "${extension_file}" >/dev/null 2>&1

  rm -f "${CERT_DIR}/${prefix}.csr" "${extension_file}"
}

create_server_certificate \
  'server' \
  'firmware.test' \
  'DNS:firmware.test,DNS:localhost,IP:127.0.0.1'
create_server_certificate \
  'wrong-server' \
  'wrong.test' \
  'DNS:wrong.test'

chmod 600 "${CERT_DIR}"/*.key
printf '%s\n' "${CERT_DIR}"
