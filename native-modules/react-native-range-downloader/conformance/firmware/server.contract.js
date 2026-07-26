'use strict';

const assert = require('node:assert/strict');
const crypto = require('node:crypto');
const test = require('node:test');

const {
  LARGE_BYTES,
  SMALL_BYTES,
  calculateDigest,
  parseRange,
  payloadChunk,
} = require('./server');

test('profiles preserve the exact acceptance sizes', () => {
  assert.equal(SMALL_BYTES, 4_194_304);
  assert.equal(LARGE_BYTES, 83_854_948);
});

test('payload generation is deterministic across chunk boundaries', () => {
  const whole = payloadChunk(65_530, 32);
  const split = Buffer.concat([
    payloadChunk(65_530, 6),
    payloadChunk(65_536, 26),
  ]);
  assert.deepEqual(split, whole);
});

test('streamed digest matches an independently chunked digest', () => {
  const hash = crypto.createHash('sha256');
  for (let offset = 0; offset < 100_003; offset += 997) {
    hash.update(payloadChunk(offset, Math.min(997, 100_003 - offset)));
  }
  assert.equal(calculateDigest(100_003), hash.digest('hex'));
});

test('range parser accepts only bounded explicit single ranges', () => {
  assert.deepEqual(parseRange('bytes=10-20', 32), { start: 10, end: 20 });
  assert.equal(parseRange('bytes=10-', 32), null);
  assert.equal(parseRange('bytes=-10', 32), null);
  assert.equal(parseRange('bytes=20-10', 32), null);
  assert.equal(parseRange('bytes=0-32', 32), null);
  assert.equal(parseRange('bytes=0-1,4-5', 32), null);
});
