import assert from 'node:assert/strict';
import test from 'node:test';

import {
  canonicalizeCleartextBody,
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

test('crc24 matches RFC 4880 ASCII armor round-trip and detects tampering', () => {
  const sample = Buffer.from('OneKey multi-signature test payload', 'utf8');
  const armored = encodeArmor('PGP SIGNATURE', sample);
  const decoded = decodeArmor(armored, 'PGP SIGNATURE');
  assert.deepEqual(decoded.bytes, sample);
  assert.equal(encodeCrc24(crc24(sample)).length, 4);

  const tampered = armored.replace(/=[A-Za-z0-9+/]{4}\n-----END/, '=AAAA\n-----END');
  assert.throws(() => decodeArmor(tampered, 'PGP SIGNATURE'), /CRC-24 mismatch/);
});

test('canonicalizeCleartextBody strips trailing spaces and dash-unescapes per RFC 4880 §7.1', () => {
  const raw = '- --hello   \nworld\t \n- line';
  assert.equal(canonicalizeCleartextBody(raw), '--hello\r\nworld\r\nline');
  assert.equal(dashEscapeCleartextBody('--hello\nworld'), '- --hello\nworld');
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

test('parseCleartextSignedMessage rejects multiple signed message blocks and weak Hash headers', () => {
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
});

test('mergeCleartextSignatures rejects mismatched bodies and duplicate packets', () => {
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
  const merged = mergeCleartextSignatures([msgA, msgB]);
  assert.equal(merged.packetCount, 2);

  const reparsed = parseCleartextSignedMessage(merged.armored);
  assert.equal(reparsed.packets.length, 2);
  assert.equal(reparsed.plainBody, 'body-1');

  assert.throws(
    () => mergeCleartextSignatures([msgA, makeMsg('body-tampered', pktB)]),
    /different payload body/,
  );

  assert.throws(
    () => mergeCleartextSignatures([msgA, msgA]),
    /Duplicate signature packet/,
  );
});
