import assert from 'node:assert/strict';
import test from 'node:test';

import {
  canonicalizeCleartextBody,
  canonicalizePlaintextForClearsign,
  crc24,
  dashEscapeCleartextBody,
  decodeArmor,
  encodeArmor,
  encodeCrc24,
  mergeCleartextSignatures,
  parseCleartextSignedMessage,
  parseSignaturePackets,
} from '../src/openpgp.mjs';

function makeFakeSigPacket({
  version = 4,
  sigType = 0x01,
  pubKeyAlgo = 1,
  hashAlgo = 8,
  payload = Buffer.from([0xaa, 0xbb, 0xcc, 0xdd]),
  newFormat = true,
} = {}) {
  const body = Buffer.concat([
    Buffer.from([version, sigType, pubKeyAlgo, hashAlgo]),
    payload,
  ]);
  if (newFormat) {
    return Buffer.concat([Buffer.from([0xc0 | 2, body.length]), body]);
  }
  return Buffer.concat([Buffer.from([0x80 | (2 << 2), body.length]), body]);
}

test('crc24 matches RFC 4880 ASCII armor round-trip, rejects tampering, and requires checksum line', () => {
  const sample = Buffer.from('OneKey multi-signature test payload', 'utf8');
  const armored = encodeArmor('PGP SIGNATURE', sample);
  const decoded = decodeArmor(armored, 'PGP SIGNATURE');
  assert.deepEqual(decoded.bytes, sample);
  assert.equal(encodeCrc24(crc24(sample)).length, 4);

  const tampered = armored.replace(/=[A-Za-z0-9+/]{4}\n-----END/, '=AAAA\n-----END');
  assert.throws(() => decodeArmor(tampered, 'PGP SIGNATURE'), /CRC-24 mismatch/);

  // Omitted CRC-24 line MUST be rejected
  const missingCrc = armored.replace(/\n=[A-Za-z0-9+/]{4}\n-----END/, '\n-----END');
  assert.throws(
    () => decodeArmor(missingCrc, 'PGP SIGNATURE'),
    /Missing mandatory CRC-24 checksum/,
  );
});

test('canonicalizeCleartextBody and canonicalizePlaintextForClearsign align with GnuPG framing', () => {
  const raw = '- --hello   \nworld\t \n- line';
  assert.equal(canonicalizeCleartextBody(raw), '--hello\r\nworld\r\nline');
  assert.equal(dashEscapeCleartextBody('--hello\nworld'), '- --hello\nworld');

  // Trailing single LF or CRLF is framing for clearsign; double LF preserves one blank line.
  assert.equal(canonicalizePlaintextForClearsign('line1\nline2\n'), 'line1\r\nline2');
  assert.equal(canonicalizePlaintextForClearsign('line1\r\nline2\r\n'), 'line1\r\nline2');
  assert.equal(canonicalizePlaintextForClearsign('line1\nline2\n\n'), 'line1\r\nline2\r\n');
  // Plaintext starting with "- " is NOT dash-unescaped (only armored cleartext is).
  assert.equal(canonicalizePlaintextForClearsign('- literal\n'), '- literal');
});

test('parseSignaturePackets parses old and new packet formats and rejects unsafe packets', () => {
  const pkt1 = makeFakeSigPacket({ newFormat: true, payload: Buffer.from([1, 2, 3, 4]) });
  const pkt2 = makeFakeSigPacket({ newFormat: false, payload: Buffer.from([5, 6, 7, 8]) });

  const parsed = parseSignaturePackets(Buffer.concat([pkt1, pkt2]));
  assert.equal(parsed.length, 2);
  assert.equal(parsed[0].hashName, 'SHA256');
  assert.equal(parsed[1].hashName, 'SHA256');

  // Reject binary signature type (0x00)
  const binarySig = makeFakeSigPacket({ sigType: 0x00 });
  assert.throws(() => parseSignaturePackets(binarySig), /canonical text signature/i);

  // Reject weak SHA-1 hash algorithm (2)
  const sha1Sig = makeFakeSigPacket({ hashAlgo: 2 });
  assert.throws(() => parseSignaturePackets(sha1Sig), /Disallowed OpenPGP hash/i);

  // Reject non-signature packet tag (e.g. Literal Data tag 11)
  const literalPacket = Buffer.from([0xc0 | 11, 4, 1, 2, 3, 4]);
  assert.throws(() => parseSignaturePackets(literalPacket), /Unexpected OpenPGP packet tag 11/);
});

test('parseCleartextSignedMessage rejects multiple blocks, weak headers, and omitted CRC-24', () => {
  const pkt = makeFakeSigPacket();
  const sigArmor = encodeArmor('PGP SIGNATURE', pkt);
  const valid = [
    '-----BEGIN PGP SIGNED MESSAGE-----',
    'Hash: SHA256',
    '',
    '{"hello":"world"}',
    sigArmor,
  ].join('\n');

  const parsed = parseCleartextSignedMessage(valid);
  assert.equal(parsed.plainBody, '{"hello":"world"}');
  assert.equal(parsed.packets.length, 1);

  const smuggled = `${valid}\n${valid}`;
  assert.throws(() => parseCleartextSignedMessage(smuggled), /exactly one PGP SIGNED MESSAGE/);

  const weakHash = valid.replace('Hash: SHA256', 'Hash: SHA1');
  assert.throws(() => parseCleartextSignedMessage(weakHash), /Disallowed cleartext Hash header/);

  const missingCrcMsg = valid.replace(/\n=[A-Za-z0-9+/]{4}\n-----END/, '\n-----END');
  assert.throws(
    () => parseCleartextSignedMessage(missingCrcMsg),
    /Missing mandatory CRC-24 checksum/,
  );
});

test('mergeCleartextSignatures enforces clear-signed inputs, framing match, and uniqueness', () => {
  const pktA = makeFakeSigPacket({ payload: Buffer.from([1, 1, 1, 1]) });
  const pktB = makeFakeSigPacket({ payload: Buffer.from([2, 2, 2, 2]) });
  const makeMsg = (body, pkt) =>
    [
      '-----BEGIN PGP SIGNED MESSAGE-----',
      'Hash: SHA256',
      '',
      body,
      encodeArmor('PGP SIGNATURE', pkt),
    ].join('\n');

  const msgA = makeMsg('body-1', pktA);
  const msgB = makeMsg('body-1', pktB);
  // Matches expectedBody terminated with trailing LF or CRLF
  const merged = mergeCleartextSignatures([msgA, msgB], { expectedBody: 'body-1\n' });
  assert.equal(merged.packetCount, 2);
  const mergedCrlf = mergeCleartextSignatures([msgA, msgB], { expectedBody: 'body-1\r\n' });
  assert.equal(mergedCrlf.packetCount, 2);

  const reparsed = parseCleartextSignedMessage(merged.armored);
  assert.equal(reparsed.packets.length, 2);
  assert.equal(reparsed.plainBody, 'body-1');

  // Detached signatures without embedded cleartext body MUST be rejected
  const detachedB = encodeArmor('PGP SIGNATURE', pktB);
  assert.throws(
    () => mergeCleartextSignatures([msgA, detachedB]),
    /must be a clear-signed PGP SIGNED MESSAGE/,
  );

  assert.throws(
    () => mergeCleartextSignatures([msgA, makeMsg('body-tampered', pktB)]),
    /different payload body/,
  );

  assert.throws(
    () => mergeCleartextSignatures([msgA, msgA]),
    /Duplicate signature packet/,
  );
});
