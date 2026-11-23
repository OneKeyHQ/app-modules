import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import test from 'node:test';

import {
  signCleartext,
  verifyMultisigCleartext,
} from '../src/gpg.mjs';
import { mergeCleartextSignatures } from '../src/openpgp.mjs';
import { createSha256SumsPayload, sha256File } from '../src/payload.mjs';

function runGpgInHome(gpgHome, args, input) {
  return execFileSync('gpg', args, {
    env: { ...process.env, GNUPGHOME: gpgHome, LC_ALL: 'C' },
    input,
    encoding: 'utf8',
    stdio: ['pipe', 'pipe', 'pipe'],
  });
}

function generateTestKey(gpgHome, name, email) {
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

  const colonList = runGpgInHome(gpgHome, [
    '--batch',
    '--with-colons',
    '--fingerprint',
    email,
  ]);
  const fprLine = colonList
    .split('\n')
    .find((line) => line.startsWith('fpr:'));
  assert.ok(fprLine, `Failed to read fingerprint for ${email}`);
  const fingerprint = fprLine.split(':')[9].toUpperCase();
  const publicKey = runGpgInHome(gpgHome, ['--batch', '--armor', '--export', fingerprint]);
  return { fingerprint, publicKey };
}

function addSigningSubkey(gpgHome, primaryFingerprint) {
  runGpgInHome(gpgHome, [
    '--batch',
    '--pinentry-mode',
    'loopback',
    '--passphrase',
    '',
    '--quick-add-key',
    primaryFingerprint,
    'ed25519',
    'sign',
    'never',
  ]);
  const colonList = runGpgInHome(gpgHome, [
    '--batch',
    '--with-colons',
    '--fingerprint',
    primaryFingerprint,
  ]);
  const fprs = colonList
    .split('\n')
    .filter((line) => line.startsWith('fpr:'))
    .map((line) => line.split(':')[9].toUpperCase());
  assert.ok(fprs.length >= 2, 'Expected primary + subkey fingerprints');
  const subkeyFingerprint = fprs[fprs.length - 1];
  const publicKey = runGpgInHome(gpgHome, ['--batch', '--armor', '--export', primaryFingerprint]);
  return { subkeyFingerprint, publicKey };
}

test('End-to-end 2-of-3 GPG multisig merge and verify for OTA metadata.json.info and SHA256SUMS', () => {
  const baseTmp = fs.existsSync('/tmp') ? '/tmp' : os.tmpdir();
  const tmpRoot = fs.mkdtempSync(path.join(baseTmp, 'ok-e2e-'));
  const gpgHome = path.join(tmpRoot, 'g');
  fs.mkdirSync(gpgHome, { mode: 0o700 });

  try {
    // 1. Create 3 distinct test signers (A with a signing subkey, B, and untrusted C)
    const signerA = generateTestKey(gpgHome, 'OneKey Signer A', 'signer-a@onekey.test');
    const signerASub = addSigningSubkey(gpgHome, signerA.fingerprint);
    const signerB = generateTestKey(gpgHome, 'OneKey Signer B', 'signer-b@onekey.test');
    const signerC = generateTestKey(gpgHome, 'OneKey Signer C', 'signer-c@onekey.test');

    const trustedRingText = `${signerASub.publicKey}\n${signerB.publicKey}\n`;
    const trustedRingFile = path.join(tmpRoot, 'trusted-pubkeys.asc');
    fs.writeFileSync(trustedRingFile, trustedRingText, 'utf8');

    // 2. Prepare realistic OTA metadata.json + electron.metadata.json.info
    const metadataJsonPath = path.join(tmpRoot, 'metadata.json');
    fs.writeFileSync(
      metadataJsonPath,
      JSON.stringify({ 'build/index.html': 'abc123', 'build/main.js': 'def456' }, null, 2),
      'utf8',
    );
    const metadataSha256 = sha256File(metadataJsonPath);
    const metadataInfoPath = path.join(tmpRoot, 'electron.metadata.json.info');
    fs.writeFileSync(
      metadataInfoPath,
      JSON.stringify(
        {
          fileName: 'metadata.json',
          sha256: metadataSha256,
          size: fs.statSync(metadataJsonPath).size,
          generatedAt: '2026-11-23T12:00:00.000Z',
          appVersion: '6.8.0',
          buildNumber: '2026112301',
          bundleVersion: '7800001',
          appType: 'electron',
        },
        null,
        2,
      ) + '\n',
      'utf8',
    );

    // 3. Signer A and Signer B sign independently
    const sigA = signCleartext({
      inputPath: metadataInfoPath,
      keyId: `${signerA.fingerprint}!`,
      gpgHome,
      batch: true,
    });
    const sigB = signCleartext({
      inputPath: metadataInfoPath,
      keyId: `${signerB.fingerprint}!`,
      gpgHome,
      batch: true,
    });

    // 4. Merge into ONE standard RFC 4880 .asc file
    const merged = mergeCleartextSignatures([sigA.armored, sigB.armored]);
    assert.equal(merged.packetCount, 2);

    // 5. Verify with threshold=2 + artifact binding
    const verified = verifyMultisigCleartext({
      signedText: merged.armored,
      keyringFiles: [trustedRingFile],
      threshold: 2,
      allowedFingerprints: [signerA.fingerprint, signerB.fingerprint],
      artifactPath: metadataJsonPath,
    });
    assert.equal(verified.valid, true);
    assert.equal(verified.uniqueSignerCount, 2);
    assert.deepEqual(
      new Set(verified.primaryFingerprints),
      new Set([signerA.fingerprint, signerB.fingerprint]),
    );
    assert.equal(verified.payload.kind, 'metadata-info');
    assert.equal(verified.payload.artifactCheck.matched, true);

    // 6. Verify native GnuPG CLI also recognizes BOTH signatures in the single merged file
    const mergedPath = path.join(tmpRoot, 'electron.metadata.json.info.asc');
    fs.writeFileSync(mergedPath, merged.armored, 'utf8');
    const rawVerify = runGpgInHome(gpgHome, ['--batch', '--status-fd=1', '--verify', mergedPath]);
    const validSigCount = rawVerify
      .split('\n')
      .filter((line) => line.startsWith('[GNUPG:] VALIDSIG ')).length;
    assert.equal(validSigCount, 2);

    // 7. Security test: single signature alone must fail threshold=2
    assert.throws(
      () =>
        verifyMultisigCleartext({
          signedText: sigA.armored,
          keyringFiles: [trustedRingFile],
          threshold: 2,
        }),
      /Insufficient distinct trusted signatures: got 1, required >= 2/,
    );

    // 8. Security test: primary key + signing subkey of the SAME master key must count as only 1 signer!
    const sigASub = signCleartext({
      inputPath: metadataInfoPath,
      keyId: `${signerASub.subkeyFingerprint}!`,
      gpgHome,
      batch: true,
    });
    const sameMasterMerged = mergeCleartextSignatures([sigA.armored, sigASub.armored]);
    assert.equal(sameMasterMerged.packetCount, 2);
    assert.throws(
      () =>
        verifyMultisigCleartext({
          signedText: sameMasterMerged.armored,
          keyringFiles: [trustedRingFile],
          threshold: 2,
        }),
      /Insufficient distinct trusted signatures: got 1, required >= 2/,
    );

    // 9. Security test: untrusted Signer C cannot satisfy threshold=2 alongside Signer A
    const sigC = signCleartext({
      inputPath: metadataInfoPath,
      keyId: `${signerC.fingerprint}!`,
      gpgHome,
      batch: true,
    });
    const withUntrusted = mergeCleartextSignatures([sigA.armored, sigC.armored]);
    assert.throws(
      () =>
        verifyMultisigCleartext({
          signedText: withUntrusted.armored,
          keyringFiles: [trustedRingFile],
          threshold: 2,
        }),
      /Insufficient distinct trusted signatures: got 1, required >= 2/,
    );

    // 10. Security test: tampering 1 character in the cleartext body fails verification
    const tamperedAsc = merged.armored.replace('7800001', '7800002');
    assert.throws(
      () =>
        verifyMultisigCleartext({
          signedText: tamperedAsc,
          keyringFiles: [trustedRingFile],
          threshold: 2,
        }),
      /invalid\/expired\/revoked signature|BADSIG/i,
    );

    // 11. Security test: artifact SHA-256 mismatch is rejected
    const wrongArtifact = path.join(tmpRoot, 'wrong-metadata.json');
    fs.writeFileSync(wrongArtifact, '{"tampered": true}', 'utf8');
    assert.throws(
      () =>
        verifyMultisigCleartext({
          signedText: merged.armored,
          keyringFiles: [trustedRingFile],
          threshold: 2,
          artifactPath: wrongArtifact,
        }),
      /Artifact SHA-256 mismatch/,
    );

    // 12. Full-release SHA256SUMS.asc end-to-end flow
    const installerPath = path.join(tmpRoot, 'OneKey-Wallet-6.8.0-mac-arm64.zip');
    fs.writeFileSync(installerPath, 'fake-installer-binary-content-v6.8.0', 'utf8');
    const sums = createSha256SumsPayload([installerPath]);
    const sumsPath = path.join(tmpRoot, 'OneKey-Wallet-6.8.0-mac-arm64.zip.SHA256SUMS');
    fs.writeFileSync(sumsPath, sums.content, 'utf8');

    const sumSigA = signCleartext({
      inputPath: sumsPath,
      keyId: `${signerA.fingerprint}!`,
      gpgHome,
      batch: true,
    });
    const sumSigB = signCleartext({
      inputPath: sumsPath,
      keyId: `${signerB.fingerprint}!`,
      gpgHome,
      batch: true,
    });
    const sumMerged = mergeCleartextSignatures([sumSigA.armored, sumSigB.armored]);
    const sumVerified = verifyMultisigCleartext({
      signedText: sumMerged.armored,
      keyringFiles: [trustedRingFile],
      threshold: 2,
      artifactPath: installerPath,
    });
    assert.equal(sumVerified.valid, true);
    assert.equal(sumVerified.uniqueSignerCount, 2);
    assert.equal(sumVerified.payload.kind, 'sha256sums');
    assert.equal(sumVerified.payload.artifactCheck.matched, true);
  } finally {
    fs.rmSync(tmpRoot, { recursive: true, force: true });
  }
});
