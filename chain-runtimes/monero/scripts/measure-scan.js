#!/usr/bin/env node
/**
 * 量「拉块」与「扫描」各占多少。
 *
 * 这条脚本存在的理由：0.6 块/秒这个端到端数字**没有意义**，除非知道它是
 * 网络慢还是解密慢 —— 两者的结论完全相反。实测（Mac，公共节点，单观察密钥）：
 * 拉块 99.6%、扫描 0.4%，纯扫描 153 块/秒。
 *
 *   ./scripts/build-node-pkg.sh
 *   XMR_NODE=http://your-node:18081 node scripts/measure-scan.js
 */
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
    console.log(`  ${p.blocksScanned}块: 拉块 ${p.msDownload}ms / 扫描 ${p.msScan}ms`);
    if (p.done) break;
  }
  console.log(`\n合计 ${n} 块: 拉块 ${dl}ms (${(dl*100/(dl+sc)).toFixed(0)}%) / 扫描 ${sc}ms (${(sc*100/(dl+sc)).toFixed(0)}%)`);
  console.log(`纯扫描速率: ${(n/(sc/1000)).toFixed(0)} 块/秒`);
  console.log(`端到端速率: ${(n/((dl+sc)/1000)).toFixed(1)} 块/秒`);
})();
