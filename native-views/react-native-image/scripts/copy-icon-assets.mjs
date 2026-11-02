import fs from 'node:fs';

fs.cpSync(
  new URL('../src/icon/assets/', import.meta.url),
  new URL('../lib/module/icon/assets/', import.meta.url),
  { recursive: true }
);
