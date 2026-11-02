import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import test from 'node:test';
import { fileURLToPath } from 'node:url';
import {
  compileSource,
  generateOutputs,
  loadTools,
  writeOutputs,
} from './generate-icons.mjs';

const index = process.argv.indexOf('--app-root');
assert.ok(
  index >= 0 && process.argv[index + 1],
  'Pass --app-root <App checkout>'
);
const appRoot = path.resolve(process.argv[index + 1]);
const tools = loadTools(appRoot);
const svg = (body, attributes = '') =>
  `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" ${attributes}>${body}</svg>`;

test('all canonical names and sprite symbols retain complete source semantics', () => {
  const { outputs, count, diagnostics } = generateOutputs(appRoot);
  assert.equal(count, 1909);
  assert.equal(outputs.size, 3820);
  assert.deepEqual(diagnostics, [
    {
      name: 'GooglePayIllus',
      source: 'illus/google-pay.svg',
      missingReferences: ['clip0_22357_29550'],
    },
  ]);
  const get = (suffix) =>
    [...outputs].find(([file]) => file.endsWith(suffix))[1];
  const manifest = JSON.parse(get('/manifest.json'));
  const existingNames = [
    ...fs
      .readFileSync(
        path.join(appRoot, 'packages/components/src/primitives/Icon/Icons.tsx'),
        'utf8'
      )
      .matchAll(/^\s*([A-Z][A-Za-z0-9]*)\s*:\s*'\1',/gm),
  ]
    .map((match) => match[1])
    .sort();
  assert.deepEqual(Object.keys(manifest).sort(), existingNames);
  for (const name of Object.keys(manifest)) {
    const resource = tools.parse(get(`/src/icon/assets/${name}.svg`));
    const root = resource.children.find((node) => node.type === 'element');
    assert.equal(root.name, 'svg');
    assert.equal(root.children.length, 1);
    const symbol = root.children[0];
    assert.equal(symbol.name, 'symbol');
    assert.equal(symbol.attributes.id, `onekey-${name}`);
    assert.ok(
      ['fixed', 'single', 'multiple', 'raster'].includes(
        manifest[name].recolorMode
      )
    );
    assert.ok(manifest[name]);
    assert.deepEqual(
      symbol.attributes.viewBox.trim().split(/[ ,]+/).map(Number),
      manifest[name].viewBox
    );
    assert.ok(symbol.children.length);
    assert.equal(
      get(`/resources/icons/${name}.svg`),
      fs
        .readFileSync(
          path.join(appRoot, 'packages/components/svg', manifest[name].source),
          'utf8'
        )
        .replace(/\n?$/, '\n')
    );
  }
});

test('namespace references and reject unsafe/unsupported input without inventing geometry', () => {
  const body =
    '<defs><linearGradient id="paint"><stop stop-color="#0f0"/></linearGradient></defs><path fill="url(#paint)" d="M0 0h24v24z"/><g clip-path="url(#missing)"><circle fill="currentColor" r="2"/></g>';
  const icon = compileSource(svg(body), 'Probe', tools);
  assert.match(icon.symbol, /id="onekey-Probe-paint"/);
  assert.match(icon.symbol, /url\(#onekey-Probe-paint\)/);
  assert.match(icon.symbol, /url\(#onekey-Probe-missing\)/);
  assert.deepEqual(icon.missingReferences, ['missing']);
  assert.match(icon.raw, /url\(#missing\)/);
  for (const input of [
    svg('<image href="https://example.com/a.svg"/>'),
    svg('<script/>'),
    '<?xml-stylesheet href="https://example.com/a.css"?>' + svg('<path/>'),
    '<!DOCTYPE svg SYSTEM "https://example.com/a.dtd">' + svg('<path/>'),
    svg('<unknown/>'),
    svg('<path fill="URL(#paint)"/>'),
    svg('<path fill="url( #paint )"/>'),
    svg('<path style="fill:currentColor"/>'),
    svg('<path opacity="currentColor"/>'),
    svg('<path fill="url(https://example.com/a)"/>'),
    svg('<path/>', 'onload="alert(1)"'),
    svg('<path/>').replace('http://www.w3.org/2000/svg', 'wrong'),
  ]) {
    assert.throws(() => compileSource(input, 'Unsafe', tools));
  }
  assert.doesNotThrow(() =>
    compileSource(
      svg('<path/>').replace(' xmlns="http://www.w3.org/2000/svg"', ''),
      'MissingNamespace',
      tools
    )
  );
});

test('idempotent read-only check detects stale and extra resources with no partial output', () => {
  const tempRoot = fileURLToPath(new URL('../../../.tmp/', import.meta.url));
  fs.mkdirSync(tempRoot, { recursive: true });
  const root = fs.mkdtempSync(path.join(tempRoot, 'onekey-icons-'));
  try {
    const first = generateOutputs(appRoot, root);
    assert.equal(writeOutputs(first.outputs), 3820);
    assert.equal(writeOutputs(generateOutputs(appRoot, root).outputs), 0);
    const snapshots = new Map(
      [...first.outputs.keys()].map((file) => [
        file,
        { data: fs.readFileSync(file), mtime: fs.statSync(file).mtimeMs },
      ])
    );
    assert.equal(writeOutputs(first.outputs, true), 0);
    for (const [file, before] of snapshots) {
      assert.deepEqual(fs.readFileSync(file), before.data);
      assert.equal(fs.statSync(file).mtimeMs, before.mtime);
    }
    const fixtureApp = path.join(root, 'fixture-app');
    const fixtureComponents = path.join(fixtureApp, 'packages/components');
    fs.mkdirSync(fixtureComponents, { recursive: true });
    fs.copyFileSync(
      path.join(appRoot, 'packages/components/package.json'),
      path.join(fixtureComponents, 'package.json')
    );
    fs.symlinkSync(
      path.join(appRoot, 'node_modules'),
      path.join(fixtureApp, 'node_modules'),
      'dir'
    );
    fs.cpSync(
      path.join(appRoot, 'packages/components/svg'),
      path.join(fixtureComponents, 'svg'),
      { recursive: true }
    );
    const manifest = JSON.parse(
      [...first.outputs].find(([file]) => file.endsWith('/manifest.json'))[1]
    );
    const names = Object.keys(manifest);
    const early = path.join(
      fixtureComponents,
      'svg',
      manifest[names[0]].source
    );
    fs.writeFileSync(
      early,
      fs.readFileSync(early, 'utf8').replaceAll('currentColor', '#123456')
    );
    const late = path.join(
      fixtureComponents,
      'svg',
      manifest[names.at(-1)].source
    );
    fs.writeFileSync(late, '<svg><broken');
    assert.throws(() =>
      writeOutputs(generateOutputs(fixtureApp, root).outputs)
    );
    for (const [file, before] of snapshots) {
      assert.deepEqual(fs.readFileSync(file), before.data);
      assert.equal(fs.statSync(file).mtimeMs, before.mtime);
    }
    const stale = path.join(root, 'resources/icons/Unexpected.svg');
    fs.writeFileSync(stale, '<svg/>');
    assert.throws(() => writeOutputs(first.outputs, true), /Unexpected/);
    fs.unlinkSync(stale);
    const file = [...first.outputs.keys()][0];
    fs.writeFileSync(file, 'stale');
    assert.throws(() => writeOutputs(first.outputs, true), /stale/);
    assert.equal(fs.readFileSync(file, 'utf8'), 'stale');
  } finally {
    fs.rmSync(root, { recursive: true, force: true });
  }
});

test('recolor classification proves paint semantics and otherwise stays raster', () => {
  const mode = (body, attributes = '') =>
    compileSource(svg(body, attributes), 'Paint', tools).recolorMode;
  assert.equal(mode('<path fill="currentColor" d="M0 0h24v24z"/>'), 'single');
  assert.equal(
    mode('<g fill="currentColor"><path d="M0 0h24v24z"/></g>'),
    'single'
  );
  assert.equal(
    mode('<path fill="currentColor" stroke="currentColor" d="M0 0h24v24z"/>'),
    'multiple'
  );
  assert.equal(
    mode(
      '<path fill="currentColor" d="M0 0h16v24z"/><path fill="currentColor" d="M8 0h16v24z"/>'
    ),
    'multiple'
  );
  assert.equal(mode('<path stroke="currentColor" d="M0 0h24v24z"/>'), 'raster'); // Default fill is visible black.
  assert.equal(
    mode('<path fill="none" stroke="currentColor" d="M0 0h24v24z"/>'),
    'single'
  );
  assert.equal(
    mode('<path fill="currentColor" fill-opacity="0.447" d="M0 0h24v24z"/>'),
    'single'
  );
  assert.equal(
    mode('<path fill="currentColor" opacity="0"/><path fill="#123456"/>'),
    'fixed'
  );
  assert.equal(
    mode('<path fill="currentColor"/><path fill="#fff"/>'),
    'raster'
  );
  for (const keyword of [
    'INHERIT',
    'unset',
    'initial',
    'revert',
    'revert-layer',
    'context-fill',
    'var(--paint)',
  ]) {
    assert.equal(
      mode(`<g fill="currentColor"><path fill="${keyword}"/></g>`),
      'raster'
    );
  }
  assert.equal(mode('<defs/><path fill="currentColor"/>'), 'raster');
  assert.equal(
    mode('<g clip-path="url(#missing)"><path fill="currentColor"/></g>'),
    'raster'
  );
  assert.equal(
    mode('<path fill="currentColor" vector-effect="non-scaling-stroke"/>'),
    'raster'
  );
  assert.equal(
    mode('<defs><linearGradient id="paint"/></defs><path fill="url(#paint)"/>'),
    'fixed'
  );
});
