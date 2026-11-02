import fs from 'node:fs';

// Bob creates a nested package scope; preserve the provider fallback there too.
const file = new URL('../lib/module/package.json', import.meta.url);
const metadata = JSON.parse(fs.readFileSync(file, 'utf8'));
metadata.imports = { '#onekey-icon-assets': './icon/assetUrls.js' };
fs.writeFileSync(file, JSON.stringify(metadata, null, 2) + '\n');
