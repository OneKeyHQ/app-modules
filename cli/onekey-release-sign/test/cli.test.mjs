import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import test from 'node:test';
import { fileURLToPath } from 'node:url';

import { parseArgs, runCli } from '../src/cli.mjs';

const cliDir = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const binPath = path.join(cliDir, 'bin', 'onekey-release-sign.mjs');

function runGpgInHome(gpgHome, args, input) {
  return execFileSync('gpg', args, {
    env: { ...process.env, GNUPGHOME: gpgHome, LC_ALL: 'C' },
    input,
    encoding: 'utf8',
    stdio: ['pipe', 'pipe', 'pipe'],
  });
}

function genKey(gpgHome, name, email) {
  runGpgInHome(gpgHome, [
    '--batch',
    '--pinentry-mode',
    'loopback',
    '--passphrase',
    '',
    '--quick-generate-key',
    `${name} <${email}>`,
    'ed25519',
    'sign',
    'never',
  ]);
  const fpr = runGpgInHome(gpgHome, ['--batch', '--with-colons', '--fingerprint', email])
    .split('\n')
    .find((line) => line.startsWith('fpr:'))
    .split(':')[9]
    .toUpperCase();
  const pub = runGpgInHome(gpgHome, ['--batch', '--armor', '--export', fpr]);
  return { fpr, pub };
}

test('parseArgs handles repeatable flags, equals syntax, and booleans', () => {
  const parsed = parseArgs([
    'merge',
    '--in',
    'a.asc',
    '--in=b.asc',
    '--threshold',
    '2',
    '--json',
  ]);
  assert.equal(parsed.command, 'merge');
  assert.deepEqual(parsed.options.in, ['a.asc', 'b.asc']);
  assert.equal(parsed.options.threshold, '2');
  assert.equal(parsed.options.json, true);
});

test('runCli rejects unknown/misspelled options, argv passphrases, and unknown commands', () => {
  const helpRes = runCli(['--help']);
  assert.equal(helpRes.exitCode, 0);
  assert.match(helpRes.stdout, /onekey-release-sign/);

  const verRes = runCli(['--version']);
  assert.equal(verRes.exitCode, 0);
  assert.match(verRes.stdout, /^\d+\.\d+\.\d+\n$/);

  const badRes = runCli(['does-not-exist']);
  assert.equal(badRes.exitCode, 1);
  assert.match(badRes.stderr, /Unknown command/);

  // Misspelled check flags MUST fail instead of silently skipping verification!
  const typoArtifact = runCli([
    'verify',
    '--in',
    'a.asc',
    '--keyring',
    'k.asc',
    '--artfact',
    'pkg.zip',
  ]);
  assert.equal(typoArtifact.exitCode, 1);
  assert.match(typoArtifact.stderr, /Unknown option --artfact/);

  const typoAllow = runCli([
    'verify',
    '--in',
    'a.asc',
    '--keyring',
    'k.asc',
    '--allow-fingerprints',
    'AAAA',
  ]);
  assert.equal(typoAllow.exitCode, 1);
  assert.match(typoAllow.stderr, /Unknown option --allow-fingerprints/);

  // Passphrase on CLI argv MUST be rejected
  const passphraseRes = runCli([
    'sign',
    '--in',
    'payload.txt',
    '--passphrase',
    'super-secret-do-not-leak',
  ]);
  assert.equal(passphraseRes.exitCode, 1);
  assert.match(passphraseRes.stderr, /Unknown option --passphrase/);
  assert.equal(passphraseRes.stderr.includes('super-secret-do-not-leak'), false);
});

test('CLI binary runs full multi-artifact checksum -> sign (x2) -> merge (--payload) -> inspect -> verify workflow', () => {
  const baseTmp = fs.existsSync('/tmp') ? '/tmp' : os.tmpdir();
  const tmpRoot = fs.mkdtempSync(path.join(baseTmp, 'ok-cli-'));
  const gpgHome = path.join(tmpRoot, 'g');
  fs.mkdirSync(gpgHome, { mode: 0o700 });

  try {
    const keyA = genKey(gpgHome, 'CLI Signer A', 'cli-a@onekey.test');
    const keyB = genKey(gpgHome, 'CLI Signer B', 'cli-b@onekey.test');
    const keyringPath = path.join(tmpRoot, 'keyring.asc');
    fs.writeFileSync(keyringPath, `${keyA.pub}\n${keyB.pub}\n`, 'utf8');

    const artifact1 = path.join(tmpRoot, 'OneKey-Wallet-6.8.0-android.apk');
    const artifact2 = path.join(tmpRoot, 'OneKey-Wallet-6.8.0-mac-arm64.zip');
    fs.writeFileSync(artifact1, 'fake-apk-bytes-6.8.0', 'utf8');
    fs.writeFileSync(artifact2, 'fake-mac-bytes-6.8.0', 'utf8');
    const sumsPath = path.join(tmpRoot, 'SHA256SUMS');
    const sigAPath = path.join(tmpRoot, 'sig-a.asc');
    const sigBPath = path.join(tmpRoot, 'sig-b.asc');
    const mergedPath = path.join(tmpRoot, 'SHA256SUMS.asc');

    const execCli = (args) =>
      execFileSync(process.execPath, [binPath, ...args], {
        encoding: 'utf8',
        stdio: ['pipe', 'pipe', 'pipe'],
      });

    // 1. checksum for 2 artifacts
    execCli([
      'checksum',
      '--artifact',
      artifact1,
      '--artifact',
      artifact2,
      '--out',
      sumsPath,
    ]);

    // 2. sign by A and B (verifying BOTH artifacts before signing via stdin)
    execCli([
      'sign',
      '--in',
      sumsPath,
      '--artifact',
      artifact1,
      '--artifact',
      artifact2,
      '--key',
      keyA.fpr,
      '--gpg-home',
      gpgHome,
      '--batch',
      '--out',
      sigAPath,
    ]);
    execCli([
      'sign',
      '--in',
      sumsPath,
      '--artifact',
      artifact1,
      '--artifact',
      artifact2,
      '--key',
      keyB.fpr,
      '--gpg-home',
      gpgHome,
      '--batch',
      '--out',
      sigBPath,
    ]);

    // 3. merge + verify passing the original newline-terminated --payload AND both --artifact flags
    const mergeOut = execCli([
      'merge',
      '--in',
      sigAPath,
      '--in',
      sigBPath,
      '--payload',
      sumsPath,
      '--keyring',
      keyringPath,
      '--threshold',
      '2',
      '--artifact',
      artifact1,
      '--artifact',
      artifact2,
      '--out',
      mergedPath,
      '--json',
    ]);
    const mergeJson = JSON.parse(mergeOut);
    assert.equal(mergeJson.packetCount, 2);
    assert.equal(mergeJson.verified, true);
    assert.equal(mergeJson.primaryFingerprints.length, 2);

    // 4. inspect merged file with both artifacts
    const inspectJson = JSON.parse(
      execCli([
        'inspect',
        '--in',
        mergedPath,
        '--artifact',
        artifact1,
        '--artifact',
        artifact2,
        '--json',
      ]),
    );
    assert.equal(inspectJson.signed, true);
    assert.equal(inspectJson.signaturePackets, 2);
    assert.equal(inspectJson.payload.artifactChecks.length, 2);

    // 5. verify merged file with both artifacts
    const verifyJson = JSON.parse(
      execCli([
        'verify',
        '--in',
        mergedPath,
        '--keyring',
        keyringPath,
        '--threshold',
        '2',
        '--allow-fingerprint',
        keyA.fpr,
        '--allow-fingerprint',
        keyB.fpr,
        '--artifact',
        artifact1,
        '--artifact',
        artifact2,
        '--json',
      ]),
    );
    assert.equal(verifyJson.valid, true);
    assert.equal(verifyJson.uniqueSignerCount, 2);
    assert.equal(verifyJson.payload.artifactChecks.length, 2);

    // 6. Tamper the 2nd artifact while keeping the 1st valid -> verify MUST fail!
    fs.writeFileSync(artifact2, 'tampered-mac-bytes-6.8.0', 'utf8');
    const tamperedVerify = runCli([
      'verify',
      '--in',
      mergedPath,
      '--keyring',
      keyringPath,
      '--threshold',
      '2',
      '--artifact',
      artifact1,
      '--artifact',
      artifact2,
    ]);
    assert.equal(tamperedVerify.exitCode, 1);
    assert.match(tamperedVerify.stderr, /Artifact SHA-256 mismatch for OneKey-Wallet-6.8.0-mac-arm64.zip/);
  } finally {
    fs.rmSync(tmpRoot, { recursive: true, force: true });
  }
});
