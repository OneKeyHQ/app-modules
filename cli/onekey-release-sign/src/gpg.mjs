import { execFileSync } from 'node:child_process';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';

import {
  ALLOWED_HASH_ALGORITHMS,
  parseCleartextSignedMessage,
} from './openpgp.mjs';
import { inspectReleasePayload } from './payload.mjs';

const FINGERPRINT_RE = /^[A-F0-9]{40}$/u;

/**
 * Normalize a 40-hex OpenPGP fingerprint (strips spaces/0x prefix, uppercases).
 * @param {string} value
 * @returns {string}
 */
export function normalizeFingerprint(value) {
  const cleaned = String(value ?? '')
    .replace(/^0x/iu, '')
    .replace(/\s+/gu, '')
    .toUpperCase();
  if (!FINGERPRINT_RE.test(cleaned)) {
    throw new Error(`Invalid 40-character hex GPG fingerprint: "${value}"`);
  }
  return cleaned;
}

function buildGpgEnv(gpgHome) {
  const env = { ...process.env, LC_ALL: 'C' };
  if (gpgHome) {
    env.GNUPGHOME = gpgHome;
  }
  return env;
}

/**
 * Run a GnuPG command via execFileSync (never via a shell).
 * @param {string[]} args
 * @param {{ gpgHome?: string, input?: string | Buffer, gpgBin?: string, allowNonZero?: boolean }} [options]
 */
function runGpg(args, { gpgHome, input, gpgBin = 'gpg', allowNonZero = false } = {}) {
  try {
    const stdout = execFileSync(gpgBin, args, {
      env: buildGpgEnv(gpgHome),
      input,
      encoding: 'utf8',
      stdio: ['pipe', 'pipe', 'pipe'],
    });
    return { exitCode: 0, stdout, stderr: '' };
  } catch (error) {
    if (error?.code === 'ENOENT') {
      throw new Error(`Required binary "${gpgBin}" not found in PATH. Install GnuPG first.`);
    }
    if (allowNonZero && typeof error?.status === 'number') {
      return {
        exitCode: error.status,
        stdout: String(error.stdout ?? ''),
        stderr: String(error.stderr ?? ''),
      };
    }
    const stderr = String(error?.stderr ?? '').trim();
    throw new Error(
      `gpg ${args.join(' ')} failed (exit ${error?.status ?? 'unknown'}): ${stderr || error.message}`,
    );
  }
}

/**
 * Inspect local GPG & YubiKey environment readiness.
 * @param {{ gpgHome?: string, gpgBin?: string, ykmanBin?: string }} [options]
 */
export function checkDoctor({ gpgHome, gpgBin = 'gpg', ykmanBin = 'ykman' } = {}) {
  const versionRes = runGpg(['--version'], { gpgHome, gpgBin, allowNonZero: true });
  const gpgVersion = versionRes.stdout.split('\n')[0]?.trim() || null;

  const cardRes = runGpg(['--card-status'], { gpgHome, gpgBin, allowNonZero: true });
  const cardDetected = cardRes.exitCode === 0 && cardRes.stdout.includes('Application ID');
  let signatureKey = null;
  let cardSerial = null;
  if (cardDetected) {
    const sigMatch = /Signature key\s*\.+:\s*([A-F0-9 ]+)/iu.exec(cardRes.stdout);
    if (sigMatch) {
      signatureKey = sigMatch[1].replace(/\s+/gu, '').toUpperCase();
    }
    const serialMatch = /Serial number\s*\.+:\s*(\S+)/iu.exec(cardRes.stdout);
    if (serialMatch) {
      cardSerial = serialMatch[1];
    }
  }

  let ykmanVersion = null;
  let openpgpInfo = null;
  try {
    ykmanVersion = execFileSync(ykmanBin, ['--version'], {
      encoding: 'utf8',
      stdio: ['pipe', 'pipe', 'pipe'],
    }).trim();
    try {
      openpgpInfo = execFileSync(ykmanBin, ['openpgp', 'info'], {
        encoding: 'utf8',
        stdio: ['pipe', 'pipe', 'pipe'],
      }).trim();
    } catch {
      openpgpInfo = null;
    }
  } catch {
    ykmanVersion = null;
  }

  return {
    gpgAvailable: versionRes.exitCode === 0,
    gpgVersion,
    cardDetected,
    cardSerial,
    signatureKey,
    ykmanAvailable: Boolean(ykmanVersion),
    ykmanVersion,
    openpgpInfo,
  };
}

/**
 * Sign a payload file using `gpg --clearsign --digest-algo SHA256` (producing
 * an RFC 4880 Type 0x01 canonical text signature compatible with YubiKey PIN +
 * physical touch).
 *
 * @param {{
 *   inputPath: string,
 *   keyId?: string,
 *   digestAlgo?: 'SHA256' | 'SHA384' | 'SHA512',
 *   gpgHome?: string,
 *   gpgBin?: string,
 *   batch?: boolean,
 *   passphrase?: string,
 * }} options
 */
export function signCleartext({
  inputPath,
  keyId,
  digestAlgo = 'SHA256',
  gpgHome,
  gpgBin = 'gpg',
  batch = false,
  passphrase,
}) {
  if (!['SHA256', 'SHA384', 'SHA512'].includes(digestAlgo)) {
    throw new Error(`Unsupported digest algorithm: ${digestAlgo}`);
  }
  const args = [
    '--clearsign',
    '--armor',
    '--digest-algo',
    digestAlgo,
    '--no-emit-version',
    '--output',
    '-',
  ];
  if (keyId) {
    args.push('--local-user', keyId);
  }
  if (batch) {
    args.push('--batch', '--yes', '--pinentry-mode', 'loopback');
    if (passphrase !== undefined) {
      args.push('--passphrase', passphrase);
    }
  }
  args.push(inputPath);

  const res = runGpg(args, { gpgHome, gpgBin });
  // Validate the generated signature strictly conforms to RFC 4880 Type 0x01.
  const parsed = parseCleartextSignedMessage(res.stdout);
  return {
    armored: res.stdout,
    parsed,
  };
}

/**
 * Parse GnuPG `--status-fd=1` output into verified and rejected signature records.
 * @param {string} statusOutput
 */
export function parseGpgVerifyStatus(statusOutput) {
  const validSignatures = [];
  const invalidSignatures = [];
  const lines = statusOutput.replace(/\r\n/g, '\n').split('\n');

  for (const line of lines) {
    if (!line.startsWith('[GNUPG:] ')) continue;
    const rest = line.slice('[GNUPG:] '.length).trim();
    const parts = rest.split(/\s+/u);
    const code = parts[0];

    if (code === 'VALIDSIG') {
      // VALIDSIG <fpr> <date> <ts> <exp> <ver> <rsv> <pkalgo> <hashalgo> <sigclass> [<primary-fpr>]
      const signingFingerprint = (parts[1] ?? '').toUpperCase();
      const timestamp = Number(parts[3] ?? 0);
      const hashAlgo = Number(parts[8] ?? 0);
      const sigClass = (parts[9] ?? '').toUpperCase();
      const primaryFingerprint = (parts[10] || parts[1] || '').toUpperCase();
      validSignatures.push({
        signingFingerprint,
        primaryFingerprint,
        timestamp,
        hashAlgo,
        hashName: ALLOWED_HASH_ALGORITHMS.get(hashAlgo) ?? `UNKNOWN_${hashAlgo}`,
        sigClass,
      });
    } else if (
      code === 'BADSIG' ||
      code === 'ERRSIG' ||
      code === 'EXPSIG' ||
      code === 'EXPKEYSIG' ||
      code === 'REVKEYSIG'
    ) {
      invalidSignatures.push({ code, detail: rest });
    }
  }

  return { validSignatures, invalidSignatures };
}

function createIsolatedGpgHome() {
  // GnuPG creates Unix domain sockets inside GNUPGHOME; macOS limits socket
  // paths to 104 bytes, and os.tmpdir() (/var/folders/...) can exceed it.
  const baseTmp =
    process.platform !== 'win32' && fs.existsSync('/tmp') ? '/tmp' : os.tmpdir();
  const dir = fs.mkdtempSync(path.join(baseTmp, 'okgpg-'));
  fs.chmodSync(dir, 0o700);
  return dir;
}

/**
 * Cryptographically verify a single or multi-signature RFC 4880 `.asc` message
 * inside a fresh isolated temporary `GNUPGHOME` containing ONLY the explicitly
 * supplied trusted public keys, deduplicating signers strictly by Primary Key
 * Fingerprint.
 *
 * @param {{
 *   signedText: string,
 *   keyringFiles?: string[],
 *   keyringTexts?: string[],
 *   threshold?: number,
 *   allowedFingerprints?: string[],
 *   artifactPath?: string,
 *   gpgBin?: string,
 * }} options
 */
export function verifyMultisigCleartext({
  signedText,
  keyringFiles = [],
  keyringTexts = [],
  threshold = 2,
  allowedFingerprints = [],
  artifactPath,
  gpgBin = 'gpg',
}) {
  if (!Number.isInteger(threshold) || threshold < 1) {
    throw new Error(`Invalid signature threshold: ${threshold} (must be integer >= 1)`);
  }
  if (keyringFiles.length === 0 && keyringTexts.length === 0) {
    throw new Error('At least one trusted public key (--keyring) is required for verification');
  }

  const allowlist =
    allowedFingerprints.length > 0
      ? new Set(allowedFingerprints.map(normalizeFingerprint))
      : null;

  // 1. Strict RFC 4880 structural & packet validation before invoking gpg.
  const parsed = parseCleartextSignedMessage(signedText);

  // 2. Inspect & optionally bind payload against local artifact SHA-256.
  const payloadInfo = inspectReleasePayload(parsed.plainBody, { artifactPath });

  // 3. Verify cryptographically in an isolated temporary GNUPGHOME (0700).
  const tmpHome = createIsolatedGpgHome();
  try {
    for (const file of keyringFiles) {
      runGpg(['--batch', '--import', file], { gpgHome: tmpHome, gpgBin });
    }
    for (let i = 0; i < keyringTexts.length; i += 1) {
      const keyFile = path.join(tmpHome, `key-${i}.asc`);
      fs.writeFileSync(keyFile, keyringTexts[i], { mode: 0o600 });
      runGpg(['--batch', '--import', keyFile], { gpgHome: tmpHome, gpgBin });
    }

    const msgFile = path.join(tmpHome, 'message.asc');
    fs.writeFileSync(msgFile, signedText, { mode: 0o600 });

    const verifyRes = runGpg(
      ['--batch', '--status-fd=1', '--verify', msgFile],
      { gpgHome: tmpHome, gpgBin, allowNonZero: true },
    );
    const { validSignatures, invalidSignatures } = parseGpgVerifyStatus(verifyRes.stdout);

    // Reject if any cryptographic signature in the message was tampered/revoked/expired.
    const hardErrors = invalidSignatures.filter(
      (item) =>
        item.code === 'BADSIG' ||
        item.code === 'EXPSIG' ||
        item.code === 'EXPKEYSIG' ||
        item.code === 'REVKEYSIG',
    );
    if (hardErrors.length > 0) {
      throw new Error(
        `Signature verification rejected due to invalid/expired/revoked signature: ${hardErrors.map((e) => e.detail).join('; ')}`,
      );
    }

    // Deduplicate valid signatures strictly by PRIMARY key fingerprint and
    // enforce SHA-256/384/512 + canonical text class 0x01 + optional allowlist.
    const uniqueSigners = new Map();
    const skippedSigners = [];

    for (const sig of validSignatures) {
      if (!ALLOWED_HASH_ALGORITHMS.has(sig.hashAlgo)) {
        skippedSigners.push({ ...sig, reason: 'DISALLOWED_HASH_ALGO' });
        continue;
      }
      if (sig.sigClass !== '01') {
        skippedSigners.push({ ...sig, reason: 'NOT_CANONICAL_TEXT_SIGNATURE' });
        continue;
      }
      if (allowlist && !allowlist.has(sig.primaryFingerprint)) {
        skippedSigners.push({ ...sig, reason: 'NOT_IN_ALLOWLIST' });
        continue;
      }
      if (!uniqueSigners.has(sig.primaryFingerprint)) {
        uniqueSigners.set(sig.primaryFingerprint, sig);
      }
    }

    const uniquePrimaryFingerprints = [...uniqueSigners.keys()];
    if (uniquePrimaryFingerprints.length < threshold) {
      throw new Error(
        `Insufficient distinct trusted signatures: got ${uniquePrimaryFingerprints.length}, required >= ${threshold}`,
      );
    }

    return {
      valid: true,
      threshold,
      uniqueSignerCount: uniquePrimaryFingerprints.length,
      primaryFingerprints: uniquePrimaryFingerprints,
      signatures: [...uniqueSigners.values()],
      skippedSigners,
      packetCount: parsed.packets.length,
      payload: payloadInfo,
    };
  } finally {
    fs.rmSync(tmpHome, { recursive: true, force: true });
  }
}
