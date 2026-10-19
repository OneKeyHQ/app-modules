#!/usr/bin/env node
/**
 * End-to-end smoke test against the real wasm build: derive -> open DB ->
 * add account -> subaddress -> balance -> scan queue -> coin selection.
 *
 * Offline by default; set XMR_NODE to also run the node checks.
 *
 *   ./scripts/build-node-pkg.sh && node scripts/smoke.js
 */
const path = require('path');
const pkg = process.env.XMR_NODE_PKG || path.join(__dirname, '..', 'pkg-node');
const rt = require(path.join(pkg, 'onekey_monero_runtime.js'));

let failures = 0;
function check(label, actual, expected) {
  const ok = actual === expected;
  if (!ok) failures += 1;
  console.log(`${ok ? '✓' : '✗'} ${label}${ok ? '' : `  expected ${expected}, got ${actual}`}`);
}

// Fixed key: derivation must be deterministic across runs.
const key = '0'.repeat(63) + '7';
const a = JSON.parse(rt.deriveAccount('mainnet', key));
const b = JSON.parse(rt.deriveAccount('mainnet', key));

check('derivation is deterministic', a.primaryAddress, b.primaryAddress);
check('mainnet primary address length is 95', a.primaryAddress.length, 95);
check('mainnet primary address starts with 4', a.primaryAddress[0], '4');
check('own address validates', rt.validateAddress('mainnet', a.primaryAddress), true);
check('mainnet address rejected on testnet', rt.validateAddress('testnet', a.primaryAddress), false);

const sub = JSON.parse(
  rt.deriveSubaddress('mainnet', a.spendPublicKeyHex, a.viewPrivateKeyHex, 0, 1),
);
check('subaddress starts with 8', sub.address[0], '8');
check('subaddress differs from primary address', sub.address !== a.primaryAddress, true);

// SQLite path. `:memory:` is special-cased and needs no OPFS.
rt.openWallet(':memory:', 'mainnet');
const added = JSON.parse(
  rt.addAccount('smoke', a.spendPublicKeyHex, a.viewPrivateKeyHex, 3700000n),
);
check('stored address matches derivation', added.primaryAddress, a.primaryAddress);

const id = BigInt(added.accountId);
rt.registerSubaddress(id, 0, 1);

const balance = JSON.parse(rt.balance(id, undefined));
check('empty wallet total is 0', balance.totalAtomic, '0');
// Unlocked balance must be 0 while the chain tip is unknown.
check('unlocked is 0 without a chain tip', balance.unlockedAtomic, '0');

rt.updateChainTip(id, 3700100n);
const peek = JSON.parse(rt.syncPeek(id));
check('pending range starts at birthday', peek.ranges[0].start, 3700000);
check('pending range ends at tip + 1', peek.ranges[0].end, 3700101);
check('no blocks scanned yet', peek.scannedToHeight, null);

const diag = JSON.parse(rt.diagDatabase());
check('identity stored', diag.counts.identities, 1);
check('subaddress stored', diag.counts.subaddresses, 1);

// Signing is pure computation. Bad input must be rejected, never passed on to broadcast.
const review = JSON.stringify({
  network: 'mainnet',
  fromAddress: a.primaryAddress,
  toAddress: a.primaryAddress,
  amountAtomic: '1000',
  feeAtomic: '0',
  changeAtomic: '0',
  isMaxSend: false,
});
for (const [label, hex, req, field] of [
  ['non-hex input', 'zz', review, 'signableHex'],
  ['an unsignable transaction', '00010203', review, 'signableHex'],
  ['a malformed review', '00010203', '{}', 'reviewRequest'],
]) {
  try {
    rt.signTransaction(hex, '00'.repeat(32), req);
    check(`signing rejects ${label}`, 'succeeded', 'failed');
  } catch (e) {
    const err = JSON.parse(e.message ?? e);
    check(`signing rejects ${label}`, err.params.field, field);
  }
}

check('signed transaction verifier is exported', typeof rt.verifySignedTransaction, 'function');
try {
  rt.verifySignedTransaction('zz', review, '00');
  check('signed verification rejects malformed input', 'succeeded', 'failed');
} catch (e) {
  const err = JSON.parse(e.message ?? e);
  check('signed verification rejects malformed input', err.params.field, 'signableHex');
}

async function broadcastReviewBoundaries() {
  for (const [label, signable, req, field] of [
    ['missing review', undefined, undefined, 'reviewJson'],
    ['missing signable', undefined, review, 'signableHex'],
    ['malformed signable', 'zz', review, 'signableHex'],
  ]) {
    try {
      await rt.broadcastTransaction(id, '00', 3700100n, '1', '0', signable, req);
      check(`broadcast rejects ${label}`, 'succeeded', 'failed');
    } catch (e) {
      const err = JSON.parse(e.message ?? e);
      check(`broadcast rejects ${label}`, err.params.field, field);
    }
  }
  const after = JSON.parse(rt.diagDatabase());
  check('rejected broadcasts do not record transactions', after.counts.transactions, 0);
}

// Node checks; skipped (and reported as skipped) without XMR_NODE.
async function online() {
  const url = process.env.XMR_NODE;
  if (!url) {
    console.log('\n(skipping node checks: XMR_NODE not set)');
    return;
  }
  console.log(`\nnode checks against ${url}`);
  await rt.setNodeUrl(url);

  const tip = JSON.parse(await rt.chainTip());
  check('chain tip is a positive integer', Number.isInteger(tip.height) && tip.height > 0, true);
  console.log(`  chain tip ${tip.height}`);

  // Scan a real range over the binary route (get_blocks_by_height.bin), which
  // uses a different Content-Type from the JSON routes.
  rt.updateChainTip(id, BigInt(tip.height));
  rt.rewindTo(id, BigInt(tip.height - 20), BigInt(tip.height));
  const step = JSON.parse(await rt.syncStep(id, 20n));
  check('blocks scanned', step.blocksScanned > 0, true);
  console.log(
    `  scanned ${step.blocksScanned} blocks, download ${step.msDownload}ms / scan ${step.msScan}ms`,
  );

  // An empty wallet must fail with insufficient funds. The fee rate is fetched
  // before coin selection, so this also covers that round trip.
  try {
    await rt.prepareTransaction(
      id,
      JSON.stringify([{ address: a.primaryAddress, amountAtomic: '1000' }]),
      false,
      BigInt(tip.height),
      'normal',
    );
    check('empty wallet build fails', 'succeeded', 'failed');
  } catch (e) {
    const err = JSON.parse(e.message ?? e);
    check('empty wallet build reports insufficient funds', err.params.reason, 'insufficientUnlockedFunds');
  }

  // The review covers one recipient, so preparation refuses more.
  try {
    await rt.prepareTransaction(
      id,
      JSON.stringify([
        { address: a.primaryAddress, amountAtomic: '1000' },
        { address: sub.address, amountAtomic: '1000' },
      ]),
      false,
      BigInt(tip.height),
      'normal',
    );
    check('two-recipient build fails', 'succeeded', 'failed');
  } catch (e) {
    const err = JSON.parse(e.message ?? e);
    check('two-recipient build is refused', err.params.reason, 'exactlyOneRecipient');
  }
}

broadcastReviewBoundaries()
  .then(online)
  .catch((e) => {
    failures += 1;
    console.log(`✗ node checks threw: ${e.message ?? e}`);
  })
  .then(() => {
    rt.closeWallet();
    console.log(failures === 0 ? '\nall passed' : `\n${failures} failed`);
    process.exit(failures === 0 ? 0 : 1);
  });
