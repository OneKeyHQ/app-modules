// Bundles demo-web/main.ts with esbuild (from app-monorepo or local node_modules)
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
  const candidates = [
    join(repoRoot, 'node_modules/esbuild'),
    '/Volumes/T7Shield/Project/app-monorepo/node_modules/esbuild',
  ];
  for (const candidate of candidates) {
    try {
      return createRequire(import.meta.url)(candidate);
    } catch {
      // try next
    }
  }
  throw new Error('Could not resolve esbuild');
}

function findZxingWasm() {
  const candidates = [
    join(repoRoot, 'node_modules/zxing-wasm/dist/reader/zxing_reader.wasm'),
    join(pkgRoot, 'node_modules/zxing-wasm/dist/reader/zxing_reader.wasm'),
    '/Volumes/T7Shield/Project/app-monorepo/node_modules/zxing-wasm/dist/reader/zxing_reader.wasm',
  ];
  for (const candidate of candidates) {
    if (existsSync(candidate)) return candidate;
  }
  throw new Error('Could not locate zxing_reader.wasm');
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
    join(repoRoot, 'node_modules'),
    '/Volumes/T7Shield/Project/app-monorepo/node_modules',
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
    const [startStr, endStr] = range.replace(/bytes=/, '').split('-');
    const start = parseInt(startStr, 10);
    const end = endStr ? parseInt(endStr, 10) : stat.size - 1;
    res.writeHead(206, {
      'Content-Range': `bytes ${start}-${end}/${stat.size}`,
      'Accept-Ranges': 'bytes',
      'Content-Length': end - start + 1,
      'Content-Type': contentType,
    });
    createReadStream(filePath, { start, end }).pipe(res);
    return;
  }
  res.writeHead(200, {
    'Content-Length': stat.size,
    'Content-Type': contentType,
    'Accept-Ranges': 'bytes',
  });
  createReadStream(filePath).pipe(res);
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
