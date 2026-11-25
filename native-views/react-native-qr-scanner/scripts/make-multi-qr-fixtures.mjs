// Generates the 4 deterministic multi-QR test fixtures in test-fixtures/:
//   a-single-static.png
//   b-multi-static.png
//   c-animated-ur.mp4 (+ c-animated-ur.json)
//   d-animated-ur-plus-multi.mp4 (+ d-animated-ur-plus-multi-frame0.png)
//
// Usage:
//   node scripts/make-multi-qr-fixtures.mjs /path/to/node_modules_with_qrcode
import { spawnSync } from 'node:child_process';
import {
  copyFileSync,
  mkdirSync,
  mkdtempSync,
  rmSync,
  writeFileSync,
} from 'node:fs';
import { createRequire } from 'node:module';
import { tmpdir } from 'node:os';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const nodeModules = process.argv[2];
if (!nodeModules) {
  throw new Error('usage: make-multi-qr-fixtures.mjs <node_modules with qrcode>');
}

const QR = createRequire(join(nodeModules, 'noop.js'))('qrcode');
const here = dirname(fileURLToPath(import.meta.url));
const pkgRoot = resolve(here, '..');
const outDir = join(pkgRoot, 'test-fixtures');
const macFixturesDir = resolve(
  pkgRoot,
  '../../desktop/mac/electron-qr-scanner/test/fixtures'
);

mkdirSync(outDir, { recursive: true });

const PAYLOADS = {
  ethAddress: 'ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F',
  walletConnect:
    'wc:8a3f9c2d1b0e4a5f6c7d8e9f0a1b2c3d@2?relay-protocol=irn&symKey=abcdef123456',
  marketDetail: 'onekey://market/detail?symbol=ETH',
};

function runFfmpeg(args) {
  const result = spawnSync('ffmpeg', ['-y', '-loglevel', 'error', ...args], {
    stdio: 'inherit',
  });
  if (result.status !== 0) {
    throw new Error(`ffmpeg failed: ${args.join(' ')}`);
  }
}

const workDir = mkdtempSync(join(tmpdir(), 'onekey-multi-qr-'));
try {
  // a. Single static QR image (720x720)
  await QR.toFile(join(outDir, 'a-single-static.png'), PAYLOADS.ethAddress, {
    errorCorrectionLevel: 'M',
    margin: 4,
    width: 720,
  });

  // Prepare individual QR PNGs for composites
  const qrWc = join(workDir, 'qr-wc.png');
  const qrMarket = join(workDir, 'qr-market.png');
  const qrEth = join(workDir, 'qr-eth.png');

  await QR.toFile(qrWc, PAYLOADS.walletConnect, {
    errorCorrectionLevel: 'M',
    margin: 3,
    width: 360,
  });
  await QR.toFile(qrMarket, PAYLOADS.marketDetail, {
    errorCorrectionLevel: 'M',
    margin: 3,
    width: 260,
  });
  await QR.toFile(qrEth, PAYLOADS.ethAddress, {
    errorCorrectionLevel: 'M',
    margin: 3,
    width: 280,
  });

  // b. Multi static QR image (960x960, light neutral background with 3 QRs)
  runFfmpeg([
    '-f',
    'lavfi',
    '-i',
    'color=c=#e9edf2:s=960x960:d=1',
    '-i',
    qrWc,
    '-i',
    qrMarket,
    '-i',
    qrEth,
    '-filter_complex',
    '[0:v][1:v]overlay=540:60[v1];[v1][2:v]overlay=90:350[v2];[v2][3:v]overlay=580:610',
    '-frames:v',
    '1',
    join(outDir, 'b-multi-static.png'),
  ]);

  // c. Animated BC-UR video (copy deterministic crypto-psbt fixture)
  const animatedUrMp4 = join(outDir, 'c-animated-ur.mp4');
  copyFileSync(join(macFixturesDir, 'animated-ur.mp4'), animatedUrMp4);
  copyFileSync(
    join(macFixturesDir, 'animated-ur.json'),
    join(outDir, 'c-animated-ur.json')
  );

  // d. Animated BC-UR + 2 static QR codes in one 960x960 video (8 fps)
  const qrEth300 = join(workDir, 'qr-eth-300.png');
  const qrMarket300 = join(workDir, 'qr-market-300.png');
  await QR.toFile(qrEth300, PAYLOADS.ethAddress, {
    errorCorrectionLevel: 'M',
    margin: 3,
    width: 300,
  });
  await QR.toFile(qrMarket300, PAYLOADS.marketDetail, {
    errorCorrectionLevel: 'M',
    margin: 3,
    width: 300,
  });

  const mixedMp4 = join(outDir, 'd-animated-ur-plus-multi.mp4');
  runFfmpeg([
    '-f',
    'lavfi',
    '-i',
    'color=c=#e9edf2:s=960x960:r=8',
    '-i',
    animatedUrMp4,
    '-i',
    qrEth300,
    '-i',
    qrMarket300,
    '-filter_complex',
    '[1:v]scale=460:460:flags=neighbor[ur];[0:v][ur]overlay=40:250:shortest=1[v1];[v1][2:v]overlay=580:100[v2];[v2][3:v]overlay=580:560',
    '-c:v',
    'libx264',
    '-pix_fmt',
    'yuv420p',
    '-crf',
    '18',
    mixedMp4,
  ]);

  // Also extract frame 0 of d as a still image (animated UR frame + 2 static QRs)
  runFfmpeg([
    '-i',
    mixedMp4,
    '-frames:v',
    '1',
    join(outDir, 'd-animated-ur-plus-multi-frame0.png'),
  ]);

  writeFileSync(
    join(outDir, 'manifest.json'),
    `${JSON.stringify(
      {
        a: {
          file: 'a-single-static.png',
          kind: 'image',
          expected: [
            { text: PAYLOADS.ethAddress, centerX: 0.5, centerY: 0.5 },
          ],
        },
        b: {
          file: 'b-multi-static.png',
          kind: 'image',
          expected: [
            {
              text: PAYLOADS.walletConnect,
              centerX: (540 + 180) / 960,
              centerY: (60 + 180) / 960,
            },
            {
              text: PAYLOADS.marketDetail,
              centerX: (90 + 130) / 960,
              centerY: (350 + 130) / 960,
            },
            {
              text: PAYLOADS.ethAddress,
              centerX: (580 + 140) / 960,
              centerY: (610 + 140) / 960,
            },
          ],
        },
        c: {
          file: 'c-animated-ur.mp4',
          kind: 'video',
          ur: { type: 'crypto-psbt', bytes: 2003, fnv1a: '07a5e3d7' },
        },
        d: {
          file: 'd-animated-ur-plus-multi.mp4',
          frame0: 'd-animated-ur-plus-multi-frame0.png',
          kind: 'video',
          ur: {
            type: 'crypto-psbt',
            bytes: 2003,
            fnv1a: '07a5e3d7',
            centerX: (40 + 230) / 960,
            centerY: (250 + 230) / 960,
          },
          staticCodes: [
            {
              text: PAYLOADS.ethAddress,
              centerX: (580 + 150) / 960,
              centerY: (100 + 150) / 960,
            },
            {
              text: PAYLOADS.marketDetail,
              centerX: (580 + 150) / 960,
              centerY: (560 + 150) / 960,
            },
          ],
        },
      },
      null,
      2
    )}\n`
  );
  console.log('Generated 4 fixtures in', outDir);
} finally {
  rmSync(workDir, { recursive: true, force: true });
}
