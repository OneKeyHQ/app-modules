#!/usr/bin/env node
/* eslint-disable */
// OCDS local fault server — drives the iOS concurrent bundle downloader for
// on-simulator OCDS conformance verification. HTTP on localhost:8788 (iOS ATS
// already exempts localhost cleartext; the native https guards are temporarily
// relaxed to allow http://localhost). NOT shipped; lives outside both repos.
//
// Endpoints:
//   GET  /ocds/manifest.json     -> {version, jsBundleVersion, downloadUrl, sha256, fileSize}
//   GET  /ocds/bundle.zip        -> Range-capable body with per-scenario fault injection
//   POST /ocds/scenario {name}   -> switch active fault scenario, reset counters
//   GET  /ocds/log               -> recent request log (server-side evidence)
//   GET  /ocds/health            -> {ok:true, scenario, sha256, fileSize}
//   POST /ocds/reset             -> clear the request log + counters

const http = require('http');
const crypto = require('crypto');

const PORT = 8788;
const SIZE = 16 * 1024 * 1024; // 16 MiB -> multiple concurrent segments

// Deterministic bundle bytes (so sha256 is stable for a server lifetime).
const BUNDLE = Buffer.allocUnsafe(SIZE);
for (let i = 0; i < SIZE; i += 4) BUNDLE.writeUInt32LE((i * 2654435761) >>> 0, i);
const SHA256 = crypto.createHash('sha256').update(BUNDLE).digest('hex');
const ETAG = '"ocds-bundle-v1"';

let scenario = 'clean';
const counters = {}; // per (scenario, rangeStart) fault counters
const reqLog = []; // {t, method, path, range, status, scenario, note}
function log(entry) {
  entry.t = Date.now();
  entry.scenario = scenario;
  reqLog.push(entry);
  if (reqLog.length > 5000) reqLog.shift();
  const r = entry.range != null ? ` range=${entry.range}` : '';
  console.log(`[${scenario}] ${entry.method} ${entry.path}${r} -> ${entry.status}${entry.note ? ' (' + entry.note + ')' : ''}`);
}

function parseRange(h) {
  const m = /bytes=(\d+)-(\d*)/.exec(h || '');
  if (!m) return null;
  return { start: Number(m[1]), end: m[2] === '' ? SIZE - 1 : Number(m[2]) };
}

function bump(key) {
  counters[key] = (counters[key] || 0) + 1;
  return counters[key];
}

function send206(res, range, body, note) {
  res.writeHead(206, {
    'Content-Range': `bytes ${range.start}-${range.start + body.length - 1}/${SIZE}`,
    'Content-Length': body.length,
    'Accept-Ranges': 'bytes',
    ETag: ETAG,
  });
  res.end(body);
  return note;
}

const server = http.createServer((req, res) => {
  const url = req.url.split('?')[0];

  // --- control plane ---
  if (req.method === 'POST' && url === '/ocds/scenario') {
    let b = '';
    req.on('data', (d) => (b += d));
    req.on('end', () => {
      try { scenario = JSON.parse(b).name || 'clean'; } catch { scenario = 'clean'; }
      for (const k of Object.keys(counters)) delete counters[k];
      log({ method: 'POST', path: url, status: 200, note: 'scenario=' + scenario });
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ ok: true, scenario }));
    });
    return;
  }
  if (req.method === 'POST' && url === '/ocds/reset') {
    reqLog.length = 0;
    for (const k of Object.keys(counters)) delete counters[k];
    res.writeHead(200); res.end('{"ok":true}');
    return;
  }
  if (url === '/ocds/log') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(reqLog));
    return;
  }
  if (url === '/ocds/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ ok: true, scenario, sha256: SHA256, fileSize: SIZE }));
    return;
  }
  if (url === '/ocds/manifest.json') {
    log({ method: 'GET', path: url, status: 200 });
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      version: '6.5.0', // == current app version -> NOT an app-shell update
      jsBundleVersion: '202699999', // newer bundle -> jsBundle hot-update path (localhost)
      jsBundleCount: 1,
      updateStrategy: 0, // silent -> auto-download on launch (no UI tap)
      downloadUrl: `http://localhost:${PORT}/ocds/bundle.zip`,
      sha256: SHA256,
      fileSize: SIZE,
      signature: '',
    }));
    return;
  }

  // --- data plane: the bundle, Range + fault injection ---
  if (url === '/ocds/bundle.zip') {
    const range = parseRange(req.headers.range);
    const rs = range ? range.start : null;

    // Non-range (single-stream / probe) request.
    if (!range) {
      // give-up: single-stream must ALSO fail, else the run succeeds and never
      // exhausts the retry budget.
      if (scenario === 'give-up') {
        res.writeHead(500); res.end('boom');
        log({ method: req.method, path: url, range: 'none', status: 500, note: 'give-up full' });
        return;
      }
      // permanent-4xx: concurrent ranges 404 -> fallback to single-stream full GET 200.
      log({ method: req.method, path: url, range: 'none', status: 200, note: 'full body' });
      res.writeHead(200, { 'Content-Length': SIZE, 'Accept-Ranges': 'bytes', ETag: ETAG });
      res.end(BUNDLE);
      return;
    }

    const full = BUNDLE.subarray(range.start, range.end + 1);

    switch (scenario) {
      case 'clean':
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206 });
        return void send206(res, range, full);

      case 'delay-tail': { // segs 5,6,7 (start>=10485760) hang ~25s; segs 0-4 respond now.
        if (range.start >= 10485760) {
          log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 0, note: 'HANG 25s (tail)' });
          setTimeout(() => { try { send206(res, range, full); } catch {} }, 25000);
          return;
        }
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'fast head' });
        return void send206(res, range, full);
      }

      case 'short-body': { // T5c: first hit on a range sends a SHORT body, then full on retry.
        if (range.start === 0 && range.end === 0) return void send206(res, range, full);
        const n = bump('sb:' + rs);
        if (n === 1) {
          const half = full.subarray(0, Math.max(1, Math.floor(full.length / 2)));
          res.writeHead(206, {
            'Content-Range': `bytes ${range.start}-${range.end}/${SIZE}`,
            'Content-Length': full.length, // promise full, deliver half -> incomplete
            'Accept-Ranges': 'bytes', ETag: ETAG,
          });
          res.end(half);
          log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'SHORT body (half)' });
          return;
        }
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'recovered full' });
        return void send206(res, range, full);
      }

      case 'range-ignored-probe': // 200 to EVERYTHING incl probe -> rangeUnsupported -> single-stream
        res.writeHead(200, { 'Content-Length': SIZE, 'Accept-Ranges': 'bytes', ETag: ETAG });
        res.end(BUNDLE);
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 200, note: '200 ignore-range (incl probe)' });
        return;

      case 'range-ignored-segment': // 206 to probe, 200 to segment GETs -> serverIgnoredRange -> permanent fallback
        if (range.start === 0 && range.end === 0) {
          log({ method: req.method, path: url, range: '0-0', status: 206, note: 'probe ok' });
          return void send206(res, range, full);
        }
        res.writeHead(200, { 'Content-Length': SIZE, 'Accept-Ranges': 'bytes', ETag: ETAG });
        res.end(BUNDLE);
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 200, note: '200 to segment (range ignored)' });
        return;

      case 'overlong-206': { // 206 correct CR but body +4096 bytes -> size mismatch -> retry -> single-stream
        if (range.start === 0 && range.end === 0) return void send206(res, range, full);
        const over = Buffer.concat([full, Buffer.alloc(4096, 0x5a)]);
        res.writeHead(206, { 'Content-Range': `bytes ${range.start}-${range.end}/${SIZE}`, 'Content-Length': over.length, 'Accept-Ranges': 'bytes', ETag: ETAG });
        res.end(over);
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'OVERLONG +4096' });
        return;
      }

      case 'misaligned-206': { // 206 Content-Range off by one -> bounds mismatch -> Permanent fallback
        if (range.start === 0 && range.end === 0) return void send206(res, range, full);
        res.writeHead(206, { 'Content-Range': `bytes ${range.start + 1}-${range.end}/${SIZE}`, 'Content-Length': full.length, 'Accept-Ranges': 'bytes', ETag: ETAG });
        res.end(full);
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'MISALIGNED CR +1' });
        return;
      }

      case 'bad-total-206': { // 206 with disagreeing total -> Permanent fallback
        if (range.start === 0 && range.end === 0) return void send206(res, range, full);
        res.writeHead(206, { 'Content-Range': `bytes ${range.start}-${range.end}/${SIZE + 1}`, 'Content-Length': full.length, 'Accept-Ranges': 'bytes', ETag: ETAG });
        res.end(full);
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'BAD TOTAL' });
        return;
      }

      case 'corrupt-bytes': { // correct length + CR, but one segment's bytes flipped -> assembled sha != manifest sha
        let body = full;
        if (range.start === 6291456) { body = Buffer.from(full); body[0] ^= 0xff; }
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: range.start === 6291456 ? 'CORRUPTED byte' : '' });
        return void send206(res, range, body);
      }

      case 'flap': { // first hit per segment -> TCP reset (transient); retry -> clean. Probe unaffected.
        if (range.start === 0 && range.end === 0) return void send206(res, range, full);
        const n = bump('flap:' + rs);
        if (n === 1) {
          log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 0, note: 'CONN RESET' });
          req.socket.destroy();
          return;
        }
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'recovered after reset' });
        return void send206(res, range, full);
      }

      case 'transient-5xx': { // first 2 hits per range -> 503, then recover.
        if (range.start === 0 && range.end === 0) return void send206(res, range, full);
        const n = bump('t5:' + rs);
        if (n <= 2) {
          res.writeHead(503, { 'Retry-After': '0' }); res.end('overloaded');
          log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 503, note: `attempt ${n}` });
          return;
        }
        return void send206(res, range, full, log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'recovered' }));
      }

      case 'permanent-4xx': { // range -> 404 permanent -> concurrent permanent fallback -> single-stream (handled above).
        res.writeHead(404); res.end('gone');
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 404, note: 'permanent' });
        return;
      }

      case 'range-416': { // first hit per range -> 416, then recover.
        if (range.start === 0 && range.end === 0) return void send206(res, range, full);
        const n = bump('416:' + rs);
        if (n === 1) {
          res.writeHead(416, { 'Content-Range': `bytes */${SIZE}` }); res.end();
          log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 416, note: 'first' });
          return;
        }
        return void send206(res, range, full, log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'recovered' }));
      }

      case 'stall': { // T10: first hit per range sends headers + a few bytes then HANGS (no end).
        if (range.start === 0 && range.end === 0) return void send206(res, range, full);
        const n = bump('st:' + rs);
        if (n === 1) {
          res.writeHead(206, {
            'Content-Range': `bytes ${range.start}-${range.end}/${SIZE}`,
            'Content-Length': full.length, 'Accept-Ranges': 'bytes', ETag: ETAG,
          });
          res.write(full.subarray(0, 16)); // dribble then stall
          log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'STALL (16B then hang)' });
          return; // never end -> stall watchdog must cancel
        }
        return void send206(res, range, full, log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 206, note: 'recovered after stall' }));
      }

      case 'give-up': { // everything fails -> exhaust retry budget -> DownloadGaveUpError.
        res.writeHead(500); res.end('boom');
        log({ method: req.method, path: url, range: `${range.start}-${range.end}`, status: 500, note: 'always fail' });
        return;
      }

      default:
        return void send206(res, range, full);
    }
  }

  res.writeHead(404); res.end('not found');
});

server.listen(PORT, '127.0.0.1', () => {
  console.log(`OCDS fault server on http://localhost:${PORT}  size=${SIZE}  sha256=${SHA256}`);
  console.log(`scenarios: clean | short-body | transient-5xx | permanent-4xx | range-416 | stall | give-up`);
});
