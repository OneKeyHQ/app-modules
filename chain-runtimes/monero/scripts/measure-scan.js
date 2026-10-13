#!/usr/bin/env node
/**
 * Split sync time into block fetching and scanning, since an end-to-end
 * blocks/sec figure cannot tell network latency from decryption cost.
 * Measured (Mac, public node, one view key): fetch 99.6%, scan 0.4%,
 * scan-only 153 blocks/sec.
 *
 *   ./scripts/build-node-pkg.sh
 *   XMR_NODE=http://your-node:18081 node scripts/measure-scan.js
 */
const path = require('path');
const pkg = process.env.XMR_NODE_PKG || path.join(__dirname, '..', 'pkg-node');
const rt = require(path.join(pkg, 'onekey_monero_runtime.js'));

(async () => {
  if (!process.env.XMR_NODE) {
    console.error('Set XMR_NODE to a monerod RPC URL.');
    process.exit(1);
  }
  await rt.setNodeUrl(process.env.XMR_NODE);
  const tip = JSON.parse(await rt.chainTip());
  const key = '0'.repeat(63) + '7';
  const a = JSON.parse(rt.deriveAccount('mainnet', key));
  rt.openWallet(':memory:', 'mainnet');
  const added = JSON.parse(rt.addAccount('m', a.spendPublicKeyHex, a.viewPrivateKeyHex, BigInt(tip.height - 40)));
  const id = BigInt(added.accountId);
  rt.updateChainTip(id, BigInt(tip.height));

  let dl = 0, sc = 0, n = 0;
  for (let i = 0; i < 4; i += 1) {
    const p = JSON.parse(await rt.syncStep(id, 10n));
    if (!p.blocksScanned) break;
    dl += p.msDownload; sc += p.msScan; n += p.blocksScanned;
    console.log(`  ${p.blocksScanned} blocks: fetch ${p.msDownload}ms / scan ${p.msScan}ms`);
    if (p.done) break;
  }
  console.log(`\ntotal ${n} blocks: fetch ${dl}ms (${(dl*100/(dl+sc)).toFixed(0)}%) / scan ${sc}ms (${(sc*100/(dl+sc)).toFixed(0)}%)`);
  console.log(`scan-only rate: ${(n/(sc/1000)).toFixed(0)} blocks/sec`);
  console.log(`end-to-end rate: ${(n/((dl+sc)/1000)).toFixed(1)} blocks/sec`);
})();
