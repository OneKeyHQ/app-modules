import crypto from 'node:crypto';

const CRC24_INIT = 0xb704ce;
const CRC24_POLY = 0x1864cfb;

const SIGNATURE_PACKET_TAG = 2;
const SIG_TYPE_CANONICAL_TEXT = 0x01;

export const ALLOWED_HASH_ALGORITHMS = new Map([
  [8, 'SHA256'],
  [9, 'SHA384'],
  [10, 'SHA512'],
]);

export const ALLOWED_HASH_NAMES = new Set(['SHA256', 'SHA384', 'SHA512']);

const SIGNED_MSG_BEGIN = '-----BEGIN PGP SIGNED MESSAGE-----';
const SIG_BEGIN = '-----BEGIN PGP SIGNATURE-----';
const SIG_END = '-----END PGP SIGNATURE-----';

/**
 * Compute RFC 4880 Section 6.1 CRC-24 checksum over raw bytes.
 * @param {Uint8Array} bytes
 * @returns {number}
 */
export function crc24(bytes) {
  let crc = CRC24_INIT;
  for (let i = 0; i < bytes.length; i += 1) {
    crc ^= bytes[i] << 16;
    for (let bit = 0; bit < 8; bit += 1) {
      crc <<= 1;
      if ((crc & 0x1000000) !== 0) {
        crc ^= CRC24_POLY;
      }
    }
  }
  return crc & 0xffffff;
}

/**
 * Encode a 24-bit CRC value as a 4-character Base64 string (without leading '=').
 * @param {number} value
 * @returns {string}
 */
export function encodeCrc24(value) {
  const buf = Buffer.from([
    (value >> 16) & 0xff,
    (value >> 8) & 0xff,
    value & 0xff,
  ]);
  return buf.toString('base64');
}

/**
 * Encode raw binary data into RFC 4880 ASCII Armor.
 * @param {string} label e.g. 'PGP SIGNATURE'
 * @param {Uint8Array} bytes
 * @param {Record<string, string>} [headers]
 * @returns {string}
 */
export function encodeArmor(label, bytes, headers = {}) {
  const data = Buffer.from(bytes);
  const b64 = data.toString('base64');
  const wrapped = [];
  for (let i = 0; i < b64.length; i += 64) {
    wrapped.push(b64.slice(i, i + 64));
  }
  const headerLines = Object.entries(headers).map(([k, v]) => `${k}: ${v}`);
  const checksum = encodeCrc24(crc24(data));
  return [
    `-----BEGIN ${label}-----`,
    ...headerLines,
    '',
    ...wrapped,
    `=${checksum}`,
    `-----END ${label}-----`,
  ].join('\n');
}

/**
 * Decode an RFC 4880 ASCII Armor block and verify its CRC-24 checksum.
 * @param {string} armoredText
 * @param {string} [expectedLabel]
 * @returns {{ label: string, headers: Record<string, string>, bytes: Buffer }}
 */
export function decodeArmor(armoredText, expectedLabel) {
  const lines = armoredText.replace(/\r\n/g, '\n').replace(/\r/g, '\n').split('\n');
  const beginIndices = [];
  const endIndices = [];
  for (let i = 0; i < lines.length; i += 1) {
    if (/^-----BEGIN [A-Z0-9 ]+-----$/u.test(lines[i])) {
      beginIndices.push(i);
    } else if (/^-----END [A-Z0-9 ]+-----$/u.test(lines[i])) {
      endIndices.push(i);
    }
  }
  if (beginIndices.length !== 1 || endIndices.length !== 1) {
    throw new Error('ASCII armor must contain exactly one BEGIN and one END marker');
  }
  const beginIdx = beginIndices[0];
  const endIdx = endIndices[0];
  if (endIdx <= beginIdx) {
    throw new Error('Malformed ASCII armor marker order');
  }
  const beginLabel = lines[beginIdx].slice('-----BEGIN '.length, -'-----'.length);
  const endLabel = lines[endIdx].slice('-----END '.length, -'-----'.length);
  if (beginLabel !== endLabel) {
    throw new Error(`Mismatched armor labels: BEGIN ${beginLabel} vs END ${endLabel}`);
  }
  if (expectedLabel && beginLabel !== expectedLabel) {
    throw new Error(`Expected armor label "${expectedLabel}", got "${beginLabel}"`);
  }

  let blankIdx = -1;
  const headers = {};
  for (let i = beginIdx + 1; i < endIdx; i += 1) {
    const line = lines[i];
    if (line.trim() === '') {
      blankIdx = i;
      break;
    }
    const colon = line.indexOf(':');
    if (colon <= 0) {
      throw new Error(`Malformed armor header line: ${line}`);
    }
    headers[line.slice(0, colon).trim()] = line.slice(colon + 1).trim();
  }
  if (blankIdx < 0) {
    throw new Error('Missing blank line separating armor headers and payload');
  }

  const bodyLines = lines
    .slice(blankIdx + 1, endIdx)
    .map((line) => line.trim())
    .filter(Boolean);
  if (bodyLines.length === 0) {
    throw new Error('Empty ASCII armor body');
  }

  let expectedCrc = null;
  if (bodyLines[bodyLines.length - 1].startsWith('=')) {
    const crcLine = bodyLines.pop();
    const crcB64 = crcLine.slice(1);
    if (!/^[A-Za-z0-9+/]{4}$/u.test(crcB64)) {
      throw new Error('Malformed CRC-24 checksum line in ASCII armor');
    }
    const crcBytes = Buffer.from(crcB64, 'base64');
    if (crcBytes.length !== 3) {
      throw new Error('Invalid CRC-24 length in ASCII armor');
    }
    expectedCrc = (crcBytes[0] << 16) | (crcBytes[1] << 8) | crcBytes[2];
  }

  const b64Payload = bodyLines.join('');
  if (!/^[A-Za-z0-9+/]+={0,2}$/u.test(b64Payload) || b64Payload.length % 4 !== 0) {
    throw new Error('Invalid Base64 characters in ASCII armor body');
  }
  const bytes = Buffer.from(b64Payload, 'base64');
  if (bytes.length === 0) {
    throw new Error('Decoded ASCII armor body is empty');
  }

  if (expectedCrc !== null) {
    const actualCrc = crc24(bytes);
    if (actualCrc !== expectedCrc) {
      throw new Error(
        `ASCII armor CRC-24 mismatch: expected ${encodeCrc24(expectedCrc)}, got ${encodeCrc24(actualCrc)}`,
      );
    }
  }

  return { label: beginLabel, headers, bytes };
}

/**
 * Canonicalize cleartext body per RFC 4880 Section 7.1 (dash-unescape, strip
 * trailing spaces/tabs per line, join with CRLF).
 * @param {string} rawBody
 * @returns {string}
 */
export function canonicalizeCleartextBody(rawBody) {
  const lines = rawBody.replace(/\r\n/g, '\n').replace(/\r/g, '\n').split('\n');
  return lines
    .map((line) => {
      const unescaped = line.startsWith('- ') ? line.slice(2) : line;
      return unescaped.replace(/[ \t]+$/u, '');
    })
    .join('\r\n');
}

/**
 * Dash-escape a cleartext payload per RFC 4880 Section 7.1 for output formatting.
 * @param {string} plainBody
 * @returns {string}
 */
export function dashEscapeCleartextBody(plainBody) {
  const normalized = plainBody.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
  return normalized
    .split('\n')
    .map((line) => (line.startsWith('-') ? `- ${line}` : line))
    .join('\n');
}

/**
 * Parse binary OpenPGP signature packet list and validate every packet is a
 * strict canonical-text signature using SHA-256/384/512.
 * @param {Uint8Array} binary
 * @returns {Array<{
 *   raw: Buffer,
 *   version: number,
 *   signatureType: number,
 *   pubKeyAlgo: number,
 *   hashAlgo: number,
 *   hashName: string,
 *   packetDigest: string,
 * }>}
 */
export function parseSignaturePackets(binary) {
  const buf = Buffer.from(binary);
  if (buf.length === 0) {
    throw new Error('Empty signature packet buffer');
  }

  const packets = [];
  let offset = 0;

  while (offset < buf.length) {
    const startOffset = offset;
    const headerByte = buf[offset];
    if ((headerByte & 0x80) === 0) {
      throw new Error(`Invalid OpenPGP packet header at offset ${startOffset}`);
    }

    let tag;
    let bodyLen;
    let headerLen;

    if ((headerByte & 0x40) !== 0) {
      // New packet format (RFC 4880 §4.2.2)
      tag = headerByte & 0x3f;
      if (offset + 1 >= buf.length) {
        throw new Error('Truncated new-format OpenPGP packet length');
      }
      const l0 = buf[offset + 1];
      if (l0 < 192) {
        bodyLen = l0;
        headerLen = 2;
      } else if (l0 <= 223) {
        if (offset + 2 >= buf.length) {
          throw new Error('Truncated 2-octet OpenPGP packet length');
        }
        bodyLen = ((l0 - 192) << 8) + buf[offset + 2] + 192;
        headerLen = 3;
      } else if (l0 === 255) {
        if (offset + 5 >= buf.length) {
          throw new Error('Truncated 5-octet OpenPGP packet length');
        }
        bodyLen = buf.readUInt32BE(offset + 2);
        headerLen = 6;
      } else {
        throw new Error('Partial body length packets are not supported for signatures');
      }
    } else {
      // Old packet format (RFC 4880 §4.2.1)
      tag = (headerByte >> 2) & 0x0f;
      const lenType = headerByte & 0x03;
      if (lenType === 0) {
        if (offset + 1 >= buf.length) {
          throw new Error('Truncated 1-octet old-format OpenPGP packet length');
        }
        bodyLen = buf[offset + 1];
        headerLen = 2;
      } else if (lenType === 1) {
        if (offset + 2 >= buf.length) {
          throw new Error('Truncated 2-octet old-format OpenPGP packet length');
        }
        bodyLen = buf.readUInt16BE(offset + 1);
        headerLen = 3;
      } else if (lenType === 2) {
        if (offset + 4 >= buf.length) {
          throw new Error('Truncated 4-octet old-format OpenPGP packet length');
        }
        bodyLen = buf.readUInt32BE(offset + 1);
        headerLen = 5;
      } else {
        throw new Error('Indeterminate-length OpenPGP packets are not supported');
      }
    }

    const bodyStart = offset + headerLen;
    const packetEnd = bodyStart + bodyLen;
    if (packetEnd > buf.length) {
      throw new Error(`Truncated OpenPGP packet body at offset ${startOffset}`);
    }

    if (tag !== SIGNATURE_PACKET_TAG) {
      throw new Error(
        `Unexpected OpenPGP packet tag ${tag} at offset ${startOffset}; only Signature packets (tag 2) are allowed`,
      );
    }

    if (bodyLen < 4) {
      throw new Error('Truncated OpenPGP Signature packet body');
    }

    const version = buf[bodyStart];
    if (version !== 4 && version !== 6) {
      throw new Error(`Unsupported OpenPGP Signature packet version: ${version}`);
    }
    const signatureType = buf[bodyStart + 1];
    if (signatureType !== SIG_TYPE_CANONICAL_TEXT) {
      throw new Error(
        `Expected canonical text signature (0x01), got 0x${signatureType.toString(16).padStart(2, '0')}. Sign with "gpg --clearsign" or "gpg --detach-sign --textmode".`,
      );
    }
    const pubKeyAlgo = buf[bodyStart + 2];
    const hashAlgo = buf[bodyStart + 3];
    const hashName = ALLOWED_HASH_ALGORITHMS.get(hashAlgo);
    if (!hashName) {
      throw new Error(
        `Disallowed OpenPGP hash algorithm id ${hashAlgo}; only SHA-256, SHA-384, and SHA-512 are accepted`,
      );
    }

    const raw = Buffer.from(buf.subarray(startOffset, packetEnd));
    const packetDigest = crypto.createHash('sha256').update(raw).digest('hex');

    packets.push({
      raw,
      version,
      signatureType,
      pubKeyAlgo,
      hashAlgo,
      hashName,
      packetDigest,
    });

    offset = packetEnd;
  }

  if (packets.length === 0) {
    throw new Error('No OpenPGP signature packets found');
  }

  return packets;
}

/**
 * Strictly parse an RFC 4880 Cleartext Signed Message.
 * Rejects multiple signed blocks, weak hash headers, and non-signature packets.
 * @param {string} signedText
 */
export function parseCleartextSignedMessage(signedText) {
  if (typeof signedText !== 'string' || !signedText.trim()) {
    throw new Error('Signed message input is empty');
  }
  const normalized = signedText.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
  const lines = normalized.split('\n');

  const msgStarts = [];
  const sigStarts = [];
  const sigEnds = [];
  for (let i = 0; i < lines.length; i += 1) {
    if (lines[i] === SIGNED_MSG_BEGIN) msgStarts.push(i);
    if (lines[i] === SIG_BEGIN) sigStarts.push(i);
    if (lines[i] === SIG_END) sigEnds.push(i);
  }

  if (msgStarts.length !== 1 || sigStarts.length !== 1 || sigEnds.length !== 1) {
    throw new Error(
      'Cleartext signed message must contain exactly one PGP SIGNED MESSAGE block and one PGP SIGNATURE block',
    );
  }

  const msgStartIdx = msgStarts[0];
  const sigStartIdx = sigStarts[0];
  const sigEndIdx = sigEnds[0];
  if (!(msgStartIdx < sigStartIdx && sigStartIdx < sigEndIdx)) {
    throw new Error('Malformed PGP cleartext message framing order');
  }

  // Reject any non-whitespace content before BEGIN or after END to prevent smuggling.
  const leading = lines.slice(0, msgStartIdx).join('').trim();
  const trailing = lines.slice(sigEndIdx + 1).join('').trim();
  if (leading.length > 0 || trailing.length > 0) {
    throw new Error('Unexpected trailing or leading content outside PGP signed message');
  }

  let blankIdx = -1;
  const hashes = new Set();
  for (let i = msgStartIdx + 1; i < sigStartIdx; i += 1) {
    const line = lines[i];
    if (line === '') {
      blankIdx = i;
      break;
    }
    const colon = line.indexOf(':');
    if (colon <= 0) {
      throw new Error(`Malformed cleartext armor header: ${line}`);
    }
    const key = line.slice(0, colon).trim();
    const value = line.slice(colon + 1).trim();
    if (key.toLowerCase() === 'hash') {
      for (const token of value.split(',')) {
        const hashName = token.trim().toUpperCase();
        if (!ALLOWED_HASH_NAMES.has(hashName)) {
          throw new Error(`Disallowed cleartext Hash header: ${hashName}`);
        }
        hashes.add(hashName);
      }
    }
  }
  if (blankIdx < 0) {
    throw new Error('Missing blank line after PGP SIGNED MESSAGE headers');
  }

  const rawBody = lines.slice(blankIdx + 1, sigStartIdx).join('\n');
  const canonicalBody = canonicalizeCleartextBody(rawBody);
  const signatureBlock = lines.slice(sigStartIdx, sigEndIdx + 1).join('\n');
  const decodedSig = decodeArmor(signatureBlock, 'PGP SIGNATURE');
  const packets = parseSignaturePackets(decodedSig.bytes);

  return {
    hashes: [...hashes],
    rawBody,
    canonicalBody,
    plainBody: canonicalBody.replace(/\r\n/g, '\n'),
    signatureBlock,
    packets,
  };
}

/**
 * Merge 2+ individually signed PGP cleartext messages (or detached text-mode
 * signatures with `expectedBody`) into ONE standard RFC 4880 Cleartext Signed
 * Message containing all signature packets in a single armor block.
 *
 * @param {string[]} signedInputs
 * @param {{ expectedBody?: string, minSignatures?: number }} [options]
 * @returns {{
 *   armored: string,
 *   canonicalBody: string,
 *   plainBody: string,
 *   hashes: string[],
 *   packetCount: number,
 * }}
 */
export function mergeCleartextSignatures(
  signedInputs,
  { expectedBody, minSignatures = 2 } = {},
) {
  if (!Array.isArray(signedInputs) || signedInputs.length === 0) {
    throw new Error('At least one signed input is required');
  }

  let referenceCanonical =
    expectedBody !== undefined ? canonicalizeCleartextBody(expectedBody) : null;
  let referencePlain =
    expectedBody !== undefined
      ? expectedBody.replace(/\r\n/g, '\n').replace(/\r/g, '\n')
      : null;

  const collectedPackets = [];
  const seenDigests = new Set();
  const hashSet = new Set();

  for (let idx = 0; idx < signedInputs.length; idx += 1) {
    const input = signedInputs[idx];
    if (typeof input !== 'string' || !input.trim()) {
      throw new Error(`Signed input #${idx + 1} is empty`);
    }

    let packets;
    if (input.includes(SIGNED_MSG_BEGIN)) {
      const parsed = parseCleartextSignedMessage(input);
      if (referenceCanonical === null) {
        referenceCanonical = parsed.canonicalBody;
        referencePlain = parsed.plainBody;
      } else if (parsed.canonicalBody !== referenceCanonical) {
        throw new Error(
          `Signed input #${idx + 1} has a different payload body; all signers must sign the exact same canonical payload`,
        );
      }
      packets = parsed.packets;
    } else if (input.includes(SIG_BEGIN)) {
      if (referenceCanonical === null) {
        throw new Error(
          `Signed input #${idx + 1} is a detached signature; provide expectedBody or at least one cleartext-signed input`,
        );
      }
      const decoded = decodeArmor(input, 'PGP SIGNATURE');
      packets = parseSignaturePackets(decoded.bytes);
    } else {
      throw new Error(
        `Signed input #${idx + 1} is neither a PGP SIGNED MESSAGE nor a PGP SIGNATURE`,
      );
    }

    for (const pkt of packets) {
      if (seenDigests.has(pkt.packetDigest)) {
        throw new Error(
          `Duplicate signature packet detected in input #${idx + 1}; each signer must provide a distinct signature`,
        );
      }
      seenDigests.add(pkt.packetDigest);
      hashSet.add(pkt.hashName);
      collectedPackets.push(pkt);
    }
  }

  if (collectedPackets.length < minSignatures) {
    throw new Error(
      `Expected at least ${minSignatures} signature packet(s), got ${collectedPackets.length}`,
    );
  }

  const hashOrder = ['SHA256', 'SHA384', 'SHA512'];
  const sortedHashes = [...hashSet].sort(
    (a, b) => hashOrder.indexOf(a) - hashOrder.indexOf(b),
  );
  const mergedBytes = Buffer.concat(collectedPackets.map((p) => p.raw));
  const mergedSigArmor = encodeArmor('PGP SIGNATURE', mergedBytes);
  const escapedBody = dashEscapeCleartextBody(referencePlain ?? '');

  const armored = [
    SIGNED_MSG_BEGIN,
    `Hash: ${sortedHashes.join(', ')}`,
    '',
    escapedBody,
    mergedSigArmor,
    '',
  ].join('\n');

  return {
    armored,
    canonicalBody: referenceCanonical ?? '',
    plainBody: referencePlain ?? '',
    hashes: sortedHashes,
    packetCount: collectedPackets.length,
  };
}
