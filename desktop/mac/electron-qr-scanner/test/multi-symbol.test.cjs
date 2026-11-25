"use strict";

const assert = require("node:assert/strict");
const path = require("node:path");
const test = require("node:test");
const { URDecoder } = require("@ngraveio/bc-ur");
const { decodeImageSymbols, createCapture } = require("../index.cjs");

const FIXTURES = path.resolve(
  __dirname,
  "../../../../native-views/react-native-qr-scanner/test-fixtures",
);

function fnv1aHex(buffer) {
  let hash = 0x811c9dc5;
  for (const byte of buffer) {
    hash = Math.imul(hash ^ byte, 0x01000193) >>> 0;
  }
  return hash.toString(16).padStart(8, "0");
}

function assertClose(actual, expected, tolerance, label) {
  assert.ok(
    Math.abs(actual - expected) <= tolerance,
    `${label}: expected ${expected} ± ${tolerance}, got ${actual}`,
  );
}

test("case a: decodeImageSymbols reads single static QR centered at (0.5, 0.5)", async () => {
  const result = await decodeImageSymbols(path.join(FIXTURES, "a-single-static.png"));
  assert.equal(result.width, 720);
  assert.equal(result.height, 720);
  assert.equal(result.symbols.length, 1);
  assert.equal(
    result.symbols[0].text,
    "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
  );
  assertClose(result.symbols[0].centerX, 0.5, 0.03, "centerX");
  assertClose(result.symbols[0].centerY, 0.5, 0.03, "centerY");
});

test("case b: decodeImageSymbols reads all 3 QRs with flipped top-left Vision coordinates", async () => {
  const result = await decodeImageSymbols(path.join(FIXTURES, "b-multi-static.png"));
  assert.equal(result.width, 960);
  assert.equal(result.height, 960);
  assert.equal(result.symbols.length, 3);

  const expected = [
    {
      text: "wc:8a3f9c2d1b0e4a5f6c7d8e9f0a1b2c3d@2?relay-protocol=irn&symKey=abcdef123456",
      x: (540 + 180) / 960,
      y: (60 + 180) / 960,
    },
    {
      text: "onekey://market/detail?symbol=ETH",
      x: (90 + 130) / 960,
      y: (350 + 130) / 960,
    },
    {
      text: "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
      x: (580 + 140) / 960,
      y: (610 + 140) / 960,
    },
  ];
  for (const engines of [["vision"], ["zxing"]]) {
    const result = await decodeImageSymbols(path.join(FIXTURES, "b-multi-static.png"), { engines });
    assert.equal(result.width, 960);
    assert.equal(result.height, 960);
    assert.equal(result.symbols.length, 3, `${engines[0]} count`);

    for (const target of expected) {
      const match = result.symbols.find((s) => s.text === target.text);
      assert.ok(match, `${engines[0]} missing ${target.text}`);
      assertClose(match.centerX, target.x, 0.03, `${engines[0]} X for ${target.text}`);
      assertClose(match.centerY, target.y, 0.03, `${engines[0]} Y for ${target.text}`);
    }
  }
});

test("case c: animated UR video latches from multiSymbol=true to false and assembles crypto-psbt", async () => {
  const decoder = new URDecoder();
  let switchedToSingle = false;
  let textEventsAfterSwitch = 0;

  await new Promise((resolve, reject) => {
    const timer = setTimeout(() => reject(new Error("timed out assembling case c")), 15_000);
    const capture = createCapture({
      source: { kind: "file", path: path.join(FIXTURES, "c-animated-ur.mp4"), loop: true },
      multiSymbol: true,
      onSymbols(symbols) {
        if (!switchedToSingle && symbols.length === 1 && /^ur:/i.test(symbols[0].text)) {
          switchedToSingle = true;
          capture.setMultiSymbol(false);
          decoder.receivePart(symbols[0].text);
        }
      },
      onText(text) {
        textEventsAfterSwitch += 1;
        decoder.receivePart(text);
        if (decoder.isComplete()) {
          clearTimeout(timer);
          capture.dispose();
          resolve();
        }
      },
      onError(err) {
        clearTimeout(timer);
        capture.dispose();
        reject(err);
      },
    });
    capture.start();
  });

  assert.equal(switchedToSingle, true);
  assert.ok(textEventsAfterSwitch > 0);
  const ur = decoder.resultUR();
  assert.equal(ur.type, "crypto-psbt");
  assert.equal(ur.cbor.length, 2003);
  assert.equal(fnv1aHex(ur.cbor), "07a5e3d7");
});

test("case d: mixed animated UR + 2 static QRs detects all 3, supports setPaused, and assembles UR", async () => {
  const decoder = new URDecoder();
  let sawThreeCodes = false;
  let pauseVerified = false;
  let eventsWhilePaused = 0;
  let isPaused = false;

  await new Promise((resolve, reject) => {
    const timer = setTimeout(() => reject(new Error("timed out assembling case d")), 15_000);
    const capture = createCapture({
      source: {
        kind: "file",
        path: path.join(FIXTURES, "d-animated-ur-plus-multi.mp4"),
        loop: true,
      },
      multiSymbol: true,
      onSymbols(symbols, frameWidth, frameHeight) {
        if (isPaused) {
          eventsWhilePaused += 1;
          return;
        }
        if (!sawThreeCodes && symbols.length === 3) {
          sawThreeCodes = true;
          assert.equal(frameWidth, 960);
          assert.equal(frameHeight, 960);
          const urSym = symbols.find((s) => /^ur:crypto-psbt\//i.test(s.text));
          const ethSym = symbols.find(
            (s) => s.text === "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
          );
          const mktSym = symbols.find((s) => s.text === "onekey://market/detail?symbol=ETH");
          assert.ok(urSym && ethSym && mktSym);
          assertClose(urSym.centerX, (40 + 230) / 960, 0.03, "UR X");
          assertClose(urSym.centerY, (250 + 230) / 960, 0.03, "UR Y");
          assertClose(ethSym.centerX, (580 + 150) / 960, 0.03, "ETH X");
          assertClose(ethSym.centerY, (100 + 150) / 960, 0.03, "ETH Y");
          assertClose(mktSym.centerX, (580 + 150) / 960, 0.03, "Market X");
          assertClose(mktSym.centerY, (560 + 150) / 960, 0.03, "Market Y");

          // Verify pause/resume keeps capture alive without restarting
          isPaused = true;
          capture.setPaused(true);
          setTimeout(() => {
            pauseVerified = true;
            isPaused = false;
            capture.setPaused(false);
          }, 250);
        }
        const ur = symbols.find((s) => /^ur:crypto-psbt\//i.test(s.text));
        if (ur) {
          decoder.receivePart(ur.text);
          if (decoder.isComplete() && pauseVerified) {
            clearTimeout(timer);
            capture.dispose();
            resolve();
          }
        }
      },
      onError(err) {
        clearTimeout(timer);
        capture.dispose();
        reject(err);
      },
    });
    capture.start();
  });

  assert.equal(sawThreeCodes, true);
  assert.equal(pauseVerified, true);
  assert.equal(eventsWhilePaused, 0);
  const ur = decoder.resultUR();
  assert.equal(ur.type, "crypto-psbt");
  assert.equal(ur.cbor.length, 2003);
  assert.equal(fnv1aHex(ur.cbor), "07a5e3d7");
});
