import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

import {
  checkDoctor,
  signCleartext,
  verifyMultisigCleartext,
} from './gpg.mjs';
import {
  mergeCleartextSignatures,
  parseCleartextSignedMessage,
} from './openpgp.mjs';
import {
  createSha256SumsPayload,
  inspectReleasePayload,
} from './payload.mjs';

const pkgDir = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const pkgJson = JSON.parse(fs.readFileSync(path.join(pkgDir, 'package.json'), 'utf8'));

export const USAGE = `onekey-release-sign v${pkgJson.version}
Multi-signature OpenPGP/YubiKey CLI for OneKey OTA and full-release signing.

Usage:
  onekey-release-sign <command> [options]

Commands:
  doctor      Check local gpg, ykman, and YubiKey OpenPGP card readiness
  checksum    Generate canonical <sha256>  <filename> SHA256SUMS payload
  inspect     Inspect metadata.json.info, SHA256SUMS, or signed .asc file
  sign        Verify payload/artifact locally and clearsign with GPG/YubiKey
  merge       Merge 2+ signed .asc files into ONE RFC 4880 multi-sig .asc
  verify      Verify multi-sig .asc against explicit keyring & threshold

Examples:
  onekey-release-sign doctor
  onekey-release-sign checksum --artifact OneKey-Wallet-6.8.0-mac-arm64.zip --out OneKey-Wallet-6.8.0-mac-arm64.zip.SHA256SUMS
  onekey-release-sign inspect --in electron.metadata.json.info --artifact metadata.json
  onekey-release-sign sign --in electron.metadata.json.info --artifact metadata.json --key <FPR> --out sig-a.asc
  onekey-release-sign merge --in sig-a.asc --in sig-b.asc --keyring pubkeys.asc --threshold 2 --out combined.asc
  onekey-release-sign verify --in combined.asc --keyring pubkeys.asc --threshold 2 --artifact metadata.json --json
`;

const REPEATABLE_FLAGS = new Set([
  'in',
  'keyring',
  'allow-fingerprint',
  'artifact',
]);

const BOOLEAN_FLAGS = new Set([
  'json',
  'help',
  'version',
  'batch',
]);

/**
 * Parse CLI arguments without external dependencies.
 * @param {string[]} argv
 */
export function parseArgs(argv) {
  const positionals = [];
  const options = {};

  for (let i = 0; i < argv.length; i += 1) {
    const token = argv[i];
    if (token === '-h') {
      options.help = true;
      continue;
    }
    if (token === '-v') {
      options.version = true;
      continue;
    }
    if (!token.startsWith('--')) {
      positionals.push(token);
      continue;
    }
    const eqIdx = token.indexOf('=');
    const key = eqIdx >= 0 ? token.slice(2, eqIdx) : token.slice(2);
    if (BOOLEAN_FLAGS.has(key)) {
      options[key] = eqIdx >= 0 ? token.slice(eqIdx + 1) !== 'false' : true;
      continue;
    }
    let value;
    if (eqIdx >= 0) {
      value = token.slice(eqIdx + 1);
    } else {
      if (i + 1 >= argv.length) {
        throw new Error(`Missing value for option --${key}`);
      }
      value = argv[i + 1];
      i += 1;
    }
    if (REPEATABLE_FLAGS.has(key)) {
      if (!Array.isArray(options[key])) {
        options[key] = [];
      }
      options[key].push(value);
    } else {
      options[key] = value;
    }
  }

  return {
    command: positionals[0] ?? null,
    restPositionals: positionals.slice(1),
    options,
  };
}

function writeAtomic(filePath, content) {
  const dir = path.dirname(path.resolve(filePath));
  fs.mkdirSync(dir, { recursive: true });
  const tmpPath = `${filePath}.tmp-${process.pid}-${Date.now()}`;
  fs.writeFileSync(tmpPath, content, 'utf8');
  fs.renameSync(tmpPath, filePath);
}

function requireOption(value, name) {
  if (!value || (Array.isArray(value) && value.length === 0)) {
    throw new Error(`Missing required option --${name}`);
  }
  return value;
}

function parseThreshold(raw, fallback = 2) {
  if (raw === undefined) return fallback;
  const parsed = Number(raw);
  if (!Number.isInteger(parsed) || parsed < 1) {
    throw new Error(`Invalid --threshold value "${raw}" (must be integer >= 1)`);
  }
  return parsed;
}

/**
 * Execute a CLI invocation and return `{ exitCode, stdout, stderr }`.
 * @param {string[]} argv
 */
export function runCli(argv) {
  try {
    const { command, restPositionals, options } = parseArgs(argv);

    if (options.version) {
      return { exitCode: 0, stdout: `${pkgJson.version}\n`, stderr: '' };
    }
    if (options.help || !command || command === 'help') {
      return { exitCode: 0, stdout: USAGE, stderr: '' };
    }

    const gpgHome = options['gpg-home'];

    if (command === 'doctor') {
      const report = checkDoctor({ gpgHome });
      const stdout = options.json
        ? `${JSON.stringify(report, null, 2)}\n`
        : [
            `GPG:     ${report.gpgAvailable ? report.gpgVersion : 'NOT FOUND'}`,
            `YubiKey: ${report.cardDetected ? `Detected (serial=${report.cardSerial ?? 'unknown'}, sigKey=${report.signatureKey ?? 'none'})` : 'Not detected'}`,
            `ykman:   ${report.ykmanAvailable ? report.ykmanVersion : 'Not installed (optional)'}`,
          ].join('\n') + '\n';
      return { exitCode: report.gpgAvailable ? 0 : 1, stdout, stderr: '' };
    }

    if (command === 'checksum') {
      const artifacts = requireOption(options.artifact, 'artifact');
      const { content, entries } = createSha256SumsPayload(artifacts);
      if (options.out) {
        writeAtomic(options.out, content);
      }
      const stdout = options.json
        ? `${JSON.stringify({ out: options.out ?? null, entries }, null, 2)}\n`
        : options.out
          ? `Wrote ${entries.length} checksum entry(ies) to ${options.out}\n`
          : content;
      return { exitCode: 0, stdout, stderr: '' };
    }

    if (command === 'inspect') {
      const inputPath = options.in?.[0] ?? restPositionals[0];
      requireOption(inputPath, 'in');
      const raw = fs.readFileSync(inputPath, 'utf8');
      const artifactPath = options.artifact?.[0];

      let isSigned = false;
      let packetCount = 0;
      let hashes = [];
      let plainBody = raw;
      if (raw.includes('-----BEGIN PGP SIGNED MESSAGE-----')) {
        const parsed = parseCleartextSignedMessage(raw);
        isSigned = true;
        packetCount = parsed.packets.length;
        hashes = parsed.hashes;
        plainBody = parsed.plainBody;
      }

      const payload = inspectReleasePayload(plainBody, { artifactPath });
      const summary = {
        file: inputPath,
        signed: isSigned,
        signaturePackets: packetCount,
        hashes,
        payload,
      };
      const stdout = options.json
        ? `${JSON.stringify(summary, null, 2)}\n`
        : `${JSON.stringify(summary, null, 2)}\n`;
      return { exitCode: 0, stdout, stderr: '' };
    }

    if (command === 'sign') {
      const inputPath = options.in?.[0] ?? restPositionals[0];
      requireOption(inputPath, 'in');
      const rawBody = fs.readFileSync(inputPath, 'utf8');
      const artifactPath = options.artifact?.[0];
      // Verify payload schema & optional artifact SHA-256 BEFORE invoking YubiKey.
      const payload = inspectReleasePayload(rawBody, { artifactPath });

      const digestAlgo = (options['digest-algo'] ?? 'SHA256').toUpperCase();
      const signed = signCleartext({
        inputPath,
        keyId: options.key,
        digestAlgo,
        gpgHome,
        batch: Boolean(options.batch),
        passphrase: options.passphrase,
      });

      if (options.out) {
        writeAtomic(options.out, signed.armored);
      }

      const stdout = options.json
        ? `${JSON.stringify(
            {
              out: options.out ?? null,
              digestAlgo,
              packetCount: signed.parsed.packets.length,
              payload,
            },
            null,
            2,
          )}\n`
        : options.out
          ? `Signed ${inputPath} (${payload.kind}) -> ${options.out}\n`
          : signed.armored;
      return { exitCode: 0, stdout, stderr: '' };
    }

    if (command === 'merge') {
      const inputs = requireOption(options.in, 'in');
      const threshold = parseThreshold(options.threshold, 2);
      const signedTexts = inputs.map((filePath) => fs.readFileSync(filePath, 'utf8'));
      const expectedBody = options.payload
        ? fs.readFileSync(options.payload, 'utf8')
        : undefined;

      const merged = mergeCleartextSignatures(signedTexts, {
        expectedBody,
        minSignatures: threshold,
      });

      const artifactPath = options.artifact?.[0];
      let verification = null;
      if (options.keyring && options.keyring.length > 0) {
        verification = verifyMultisigCleartext({
          signedText: merged.armored,
          keyringFiles: options.keyring,
          threshold,
          allowedFingerprints: options['allow-fingerprint'] ?? [],
          artifactPath,
        });
      } else {
        // Still validate payload schema & artifact binding even without keyring.
        inspectReleasePayload(merged.plainBody, { artifactPath });
      }

      if (options.out) {
        writeAtomic(options.out, merged.armored);
      }

      const stdout = options.json
        ? `${JSON.stringify(
            {
              out: options.out ?? null,
              packetCount: merged.packetCount,
              hashes: merged.hashes,
              verified: Boolean(verification),
              primaryFingerprints: verification?.primaryFingerprints ?? null,
            },
            null,
            2,
          )}\n`
        : options.out
          ? `Merged ${merged.packetCount} signature(s) into ${options.out}${verification ? ` (verified ${verification.uniqueSignerCount}/${threshold} distinct signers)` : ''}\n`
          : merged.armored;
      return { exitCode: 0, stdout, stderr: '' };
    }

    if (command === 'verify') {
      const inputPath = options.in?.[0] ?? restPositionals[0];
      requireOption(inputPath, 'in');
      const keyrings = requireOption(options.keyring, 'keyring');
      const threshold = parseThreshold(options.threshold, 2);
      const signedText = fs.readFileSync(inputPath, 'utf8');
      const artifactPath = options.artifact?.[0];

      const result = verifyMultisigCleartext({
        signedText,
        keyringFiles: keyrings,
        threshold,
        allowedFingerprints: options['allow-fingerprint'] ?? [],
        artifactPath,
      });

      const stdout = options.json
        ? `${JSON.stringify(result, null, 2)}\n`
        : [
            `OK: Verified ${result.uniqueSignerCount} distinct trusted signer(s) (threshold=${result.threshold})`,
            ...result.primaryFingerprints.map((fpr, idx) => `  Signer ${idx + 1}: ${fpr}`),
            result.payload.artifactCheck
              ? `  Artifact: ${result.payload.artifactCheck.fileName} (sha256=${result.payload.artifactCheck.sha256})`
              : null,
          ]
            .filter(Boolean)
            .join('\n') + '\n';
      return { exitCode: 0, stdout, stderr: '' };
    }

    throw new Error(`Unknown command "${command}". Run "onekey-release-sign --help" for usage.`);
  } catch (error) {
    return {
      exitCode: 1,
      stdout: '',
      stderr: `Error: ${error instanceof Error ? error.message : String(error)}\n`,
    };
  }
}
