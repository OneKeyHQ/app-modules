// Bundles demo-web/main.ts with esbuild (from local/workspace node_modules or ESBUILD_PATH)
// and serves the interactive closed-loop Web QR scanner demo on http://127.0.0.1:4173.
import { createReadStream, existsSync, mkdirSync, statSync } from 'node:fs';
import { createServer } from 'node:http';
import { createRequire } from 'node:module';
import { dirname, extname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const here = dirname(fileURLToPath(import.meta.url));
const pkgRoot = resolve(here, '..');
const repoRoot = resolve(pkgRoot, '../..');
const demoDir = join(pkgRoot, 'demo-web');
const distDir = join(demoDir, 'dist');
const fixturesDir = join(pkgRoot, 'test-fixtures');
const port = Number(process.env.PORT || 4173);

function loadEsbuild() {
  const req = createRequire(import.meta.url);
  const candidates = [
    process.env.ESBUILD_PATH,
    'esbuild',
    join(pkgRoot, 'node_modules/esbuild'),
    join(repoRoot, 'node_modules/esbuild'),
  ].filter(Boolean);
  for (const candidate of candidates) {
    try {
      return req(candidate);
    } catch {
      // try next
    }
  }
  throw new Error(
    'Could not resolve esbuild. Install esbuild in the workspace or set ESBUILD_PATH=/path/to/esbuild.'
  );
}

function findZxingWasm() {
  const candidates = [
    join(pkgRoot, 'node_modules/zxing-wasm/dist/reader/zxing_reader.wasm'),
    join(repoRoot, 'node_modules/zxing-wasm/dist/reader/zxing_reader.wasm'),
  ];
  for (const candidate of candidates) {
    if (existsSync(candidate)) return candidate;
  }
  throw new Error('Could not locate zxing_reader.wasm in workspace node_modules');
}

mkdirSync(distDir, { recursive: true });
const esbuild = loadEsbuild();
const zxingWasmPath = findZxingWasm();

await esbuild.build({
  entryPoints: [join(demoDir, 'main.ts')],
  outfile: join(distDir, 'main.js'),
  bundle: true,
  format: 'esm',
  platform: 'browser',
  target: ['es2022'],
  nodePaths: [
    join(pkgRoot, 'node_modules'),
    join(repoRoot, 'node_modules'),
  ],
  inject: [join(demoDir, 'shims.ts')],
});

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.png': 'image/png',
  '.mp4': 'video/mp4',
  '.json': 'application/json; charset=utf-8',
  '.wasm': 'application/wasm',
};

function parseByteRange(rangeHeader, fileSize) {
  if (fileSize <= 0) return null;
  const match = /^bytes=(\d*)-(\d*)$/.exec(rangeHeader.trim());
  if (!match) return null;
  const [, startStr, endStr] = match;
  if (!startStr && !endStr) return null;

  let start;
  let end;
  if (!startStr) {
    // Suffix range: bytes=-<suffix>
    const suffix = Number(endStr);
    if (!Number.isSafeInteger(suffix) || suffix <= 0) return null;
    start = Math.max(0, fileSize - suffix);
    end = fileSize - 1;
  } else {
    start = Number(startStr);
    end = endStr ? Number(endStr) : fileSize - 1;
    if (!Number.isSafeInteger(start) || !Number.isSafeInteger(end)) return null;
    if (start < 0 || start >= fileSize) return null;
    end = Math.min(end, fileSize - 1);
    if (start > end) return null;
  }
  return { start, end };
}

function pipeFileStream(filePath, res, options) {
  const stream = createReadStream(filePath, options);
  stream.on('error', () => {
    if (!res.headersSent) {
      res.writeHead(500);
    }
    res.end();
  });
  stream.pipe(res);
}

function serveFile(req, res, filePath) {
  if (!existsSync(filePath)) {
    res.writeHead(404);
    res.end('Not found');
    return;
  }
  const stat = statSync(filePath);
  const contentType = MIME[extname(filePath)] || 'application/octet-stream';
  const range = req.headers.range;
  if (range) {
    const parsed = parseByteRange(range, stat.size);
    if (!parsed) {
      res.writeHead(416, {
        'Content-Range': `bytes */${stat.size}`,
        'Accept-Ranges': 'bytes',
      });
      res.end('Range Not Satisfiable');
      return;
    }
    const { start, end } = parsed;
    res.writeHead(206, {
      'Content-Range': `bytes ${start}-${end}/${stat.size}`,
      'Accept-Ranges': 'bytes',
      'Content-Length': end - start + 1,
      'Content-Type': contentType,
    });
    pipeFileStream(filePath, res, { start, end });
    return;
  }
  res.writeHead(200, {
    'Content-Length': stat.size,
    'Content-Type': contentType,
    'Accept-Ranges': 'bytes',
  });
  pipeFileStream(filePath, res);
}

const server = createServer((req, res) => {
  const urlPath = (req.url || '/').split('?')[0];
  if (urlPath === '/' || urlPath === '/index.html') {
    serveFile(req, res, join(demoDir, 'index.html'));
    return;
  }
  if (urlPath === '/dist/main.js') {
    serveFile(req, res, join(distDir, 'main.js'));
    return;
  }
  if (urlPath === '/wasm/zxing_reader.wasm') {
    serveFile(req, res, zxingWasmPath);
    return;
  }
  if (urlPath.startsWith('/fixtures/')) {
    const name = urlPath.slice('/fixtures/'.length);
    if (name.includes('..') || name.includes('/')) {
      res.writeHead(400);
      res.end('Bad path');
      return;
    }
    serveFile(req, res, join(fixturesDir, name));
    return;
  }
  res.writeHead(404);
  res.end('Not found');
});

server.listen(port, '127.0.0.1', () => {
  console.log(`Web QR demo running at http://127.0.0.1:${port}`);
});
