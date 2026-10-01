#!/usr/bin/env node
/**
 * 端到端冒烟：派生 → 开库 → 建账户 → 子地址 → 余额 → 扫描队列 → 选币。
 *
 * 不需要节点、不需要网络。跑的是真实 wasm，不是 mock —— 这条脚本存在的意义
 * 是「编译通过」和「真的能用」之间那道坎。
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
  console.log(`${ok ? '✓' : '✗'} ${label}${ok ? '' : `  期望 ${expected}，实际 ${actual}`}`);
}

// 固定私钥：派生必须是确定性的，换一次运行结果不能变
const key = '0'.repeat(63) + '7';
const a = JSON.parse(rt.deriveAccount('mainnet', key));
const b = JSON.parse(rt.deriveAccount('mainnet', key));

check('派生是确定性的', a.primaryAddress, b.primaryAddress);
check('主网主地址长度 95', a.primaryAddress.length, 95);
check('主网主地址以 4 开头', a.primaryAddress[0], '4');
check('自己的地址自己认', rt.validateAddress('mainnet', a.primaryAddress), true);
check('主网地址在测试网被拒', rt.validateAddress('testnet', a.primaryAddress), false);

const sub = JSON.parse(
  rt.deriveSubaddress('mainnet', a.spendPublicKeyHex, a.viewPrivateKeyHex, 0, 1),
);
check('子地址以 8 开头', sub.address[0], '8');
check('子地址不等于主地址', sub.address !== a.primaryAddress, true);

// SQLite 路径。`:memory:` 走特例分支，不需要 OPFS。
rt.openWallet(':memory:', 'mainnet');
const added = JSON.parse(
  rt.addAccount('smoke', a.spendPublicKeyHex, a.viewPrivateKeyHex, 3700000n),
);
check('落库地址与纯派生一致', added.primaryAddress, a.primaryAddress);

const id = BigInt(added.accountId);
rt.registerSubaddress(id, 0, 1);

const balance = JSON.parse(rt.balance(id, undefined));
check('空钱包总额为 0', balance.totalAtomic, '0');
// 不知道链尖时可用余额必须是 0，不能乐观放行
check('不知链尖则可用为 0', balance.unlockedAtomic, '0');

rt.updateChainTip(id, 3700100n);
const peek = JSON.parse(rt.syncPeek(id));
check('待扫区间从 birthday 起', peek.ranges[0].start, 3700000);
check('待扫区间到链尖+1', peek.ranges[0].end, 3700101);
check('尚未扫过任何块', peek.scannedToHeight, null);

const diag = JSON.parse(rt.diagDatabase());
check('身份入库', diag.counts.identities, 1);
check('子地址入库', diag.counts.subaddresses, 1);

// 签名入口是纯计算，不碰网络也不碰库。坏输入必须报错 —— 静默接受会让一笔
// 垃圾交易一路走到广播。
for (const [label, hex, reason] of [
  ['非十六进制', 'zz', 'notHex'],
  ['不是可签名交易', '00010203', 'notASignableTransaction'],
]) {
  try {
    rt.signTransaction(hex, '00'.repeat(32));
    check(`签名拒绝${label}`, 'succeeded', 'failed');
  } catch (e) {
    const err = JSON.parse(e.message ?? e);
    check(`签名拒绝${label}`, err.params.reason, reason);
  }
}

// 需要节点的部分。没给 XMR_NODE_URL 就跳过，这样这条脚本在离线环境里依然
// 是绿的 —— 把「没测」和「测失败」混成一个结果是最糟的。
async function online() {
  const url = process.env.XMR_NODE_URL;
  if (!url) {
    console.log('\n(跳过联网检查：未设置 XMR_NODE_URL)');
    return;
  }
  console.log(`\n联网检查 ${url}`);
  await rt.setNodeUrl(url);

  const tip = JSON.parse(await rt.chainTip());
  check('链尖是正整数', Number.isInteger(tip.height) && tip.height > 0, true);
  console.log(`  链尖 ${tip.height}`);

  // 真正扫一段。走的是二进制路由（get_blocks_by_height.bin），它和 JSON 路由
  // 的 Content-Type 不同 —— 这一步同时验证了那个分支。
  rt.updateChainTip(id, BigInt(tip.height));
  rt.rewindTo(id, BigInt(tip.height - 20), BigInt(tip.height));
  const step = JSON.parse(await rt.syncStep(id, 20n));
  check('扫到了块', step.blocksScanned > 0, true);
  console.log(
    `  扫了 ${step.blocksScanned} 块，下载 ${step.msDownload}ms / 扫描 ${step.msScan}ms`,
  );

  // 空钱包构造交易必须报「余额不足」。这条同时证明了取手续费率的往返是通的
  // —— 手续费率在选币之前就要问节点。
  try {
    await rt.prepareTransaction(
      id,
      JSON.stringify([{ address: a.primaryAddress, amountAtomic: '1000' }]),
      false,
      'smoke-r1',
      BigInt(tip.height),
      'normal',
    );
    check('空钱包构造应当失败', 'succeeded', 'failed');
  } catch (e) {
    const err = JSON.parse(e.message ?? e);
    check('空钱包构造报余额不足', err.params.reason, 'insufficientUnlockedFunds');
  }

  const released = JSON.parse(rt.releaseReservation('smoke-r1'));
  check('失败的构造没有留下预留', released.released, 0);
}

online()
  .catch((e) => {
    failures += 1;
    console.log(`✗ 联网检查抛错: ${e.message ?? e}`);
  })
  .then(() => {
    rt.closeWallet();
    console.log(failures === 0 ? '\n全部通过' : `\n${failures} 项失败`);
    process.exit(failures === 0 ? 0 : 1);
  });
