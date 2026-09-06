import {
  ACCOUNT_SELECTOR_ACCOUNT_CACHE_LIMIT,
  ACCOUNT_SELECTOR_ACCOUNTS_PER_WALLET,
  ACCOUNT_SELECTOR_LOGICAL_ACCOUNT_COUNT,
  ACCOUNT_SELECTOR_TOTAL_WALLET_COUNT,
  ACCOUNT_SELECTOR_WATCH_WALLET_INDEX,
  ACCOUNT_SELECTOR_WALLET_COUNT,
  AccountRowsCache,
  accountKey,
  buildAccountRows,
  buildVisibleAccountRows,
  buildWalletRows,
  parseAccountKey,
  parseWalletKey,
  reorderWalletRows,
  resolveAccountSelectorInitialTarget,
  walletKey,
} from '../pages/nativeListAccountSelectorData';

describe('Native List account-selector stress data', () => {
  it('keeps the full 1,000 by 1,000 logical scale without materializing it', () => {
    expect(ACCOUNT_SELECTOR_WALLET_COUNT).toBe(1_000);
    expect(ACCOUNT_SELECTOR_ACCOUNTS_PER_WALLET).toBe(1_000);
    expect(ACCOUNT_SELECTOR_LOGICAL_ACCOUNT_COUNT).toBe(1_000_000);
    expect(buildWalletRows()).toHaveLength(1_001);
    expect(buildAccountRows(0)).toHaveLength(1_000);
    expect(buildWalletRows()[6]).toMatchObject({
      key: walletKey(ACCOUNT_SELECTOR_WATCH_WALLET_INDEX),
      title: '观察钱包',
    });
  });

  it('resolves one-based cold-open targets and rejects invalid values', () => {
    expect(
      resolveAccountSelectorInitialTarget({
        walletNumber: 500,
        accountNumber: 1_000,
      }),
    ).toEqual({
      walletNumber: 500,
      accountNumber: 1_000,
      walletIndex: 499,
      accountIndex: 999,
    });
    expect(
      resolveAccountSelectorInitialTarget({
        walletNumber: 0,
        accountNumber: 1_001,
      }),
    ).toEqual({
      walletNumber: 3,
      accountNumber: 1,
      walletIndex: 2,
      accountIndex: 0,
    });
    expect(
      resolveAccountSelectorInitialTarget({
        walletNumber: ACCOUNT_SELECTOR_TOTAL_WALLET_COUNT,
      }),
    ).toEqual({
      walletNumber: 1_001,
      accountNumber: 2,
      walletIndex: ACCOUNT_SELECTOR_WATCH_WALLET_INDEX,
      accountIndex: 1,
    });
  });

  it('reorders the full wallet list by stable keys without changing wallet identity', () => {
    const rows = buildWalletRows();
    const watchKey = walletKey(ACCOUNT_SELECTOR_WATCH_WALLET_INDEX);
    const reordered = reorderWalletRows(rows, {
      key: watchKey,
      fromIndex: 6,
      toIndex: 500,
      beforeKey: rows[500]?.key,
      afterKey: rows[501]?.key,
    });

    expect(reordered).toHaveLength(ACCOUNT_SELECTOR_TOTAL_WALLET_COUNT);
    expect(reordered[500]?.key).toBe(watchKey);
    expect(parseWalletKey(reordered[500]?.key ?? '')).toBe(
      ACCOUNT_SELECTOR_WATCH_WALLET_INDEX,
    );
    expect(new Set(reordered.map(row => row.key)).size).toBe(
      ACCOUNT_SELECTOR_TOTAL_WALLET_COUNT,
    );

    const restored = reorderWalletRows(reordered, {
      key: watchKey,
      fromIndex: 500,
      toIndex: 0,
      afterKey: rows[0]?.key,
    });
    expect(restored[0]?.key).toBe(watchKey);
  });

  it('reproduces the two watch-only accounts without reducing the stress fixture', () => {
    const rows = buildAccountRows(ACCOUNT_SELECTOR_WATCH_WALLET_INDEX);
    expect(rows).toHaveLength(2);
    expect(rows[0]).toMatchObject({
      title: 'Account #1',
      subtitle: '$0.00 · juno1x...c54j',
      leading: {
        kind: 'token',
        networkImage: {
          uri: 'https://uni.onekey-asset.com/static/chain/juno.png',
        },
      },
    });
    expect(rows[1]).toMatchObject({
      title: 'Account #2',
      subtitle: '$1,925,480... · 0x40ec...bbDf',
      leading: {
        kind: 'token',
        networkImage: {
          uri: 'https://uni.onekey-asset.com/static/chain/eth.png',
        },
      },
    });
    expect(buildVisibleAccountRows(rows, '').map(row => row.key)).toEqual([
      accountKey(ACCOUNT_SELECTOR_WATCH_WALLET_INDEX, 0),
      accountKey(ACCOUNT_SELECTOR_WATCH_WALLET_INDEX, 1),
      'account-add',
    ]);
    expect(
      parseAccountKey(accountKey(ACCOUNT_SELECTOR_WATCH_WALLET_INDEX, 2)),
    ).toBeUndefined();
  });

  it('makes every logical wallet addressable while keeping only a bounded row cache', () => {
    const cache = new AccountRowsCache();
    for (
      let walletIndex = 0;
      walletIndex < ACCOUNT_SELECTOR_WALLET_COUNT;
      walletIndex += 1
    ) {
      const rows = cache.get(walletIndex);
      expect(rows).toHaveLength(1_000);
      expect(rows[0].key).toBe(accountKey(walletIndex, 0));
      expect(rows[999].key).toBe(accountKey(walletIndex, 999));
      expect(parseAccountKey(rows[999].key)).toEqual({
        walletIndex,
        accountIndex: 999,
      });
      expect(new Set(rows.map(row => row.key)).size).toBe(1_000);
      expect(cache.cachedWalletCount).toBeLessThanOrEqual(
        ACCOUNT_SELECTOR_ACCOUNT_CACHE_LIMIT,
      );
      expect(cache.cachedRowCount).toBeLessThanOrEqual(
        ACCOUNT_SELECTOR_ACCOUNT_CACHE_LIMIT *
          ACCOUNT_SELECTOR_ACCOUNTS_PER_WALLET,
      );
    }
    expect(cache.cachedWalletIndexes).toEqual([997, 998, 999]);
  });

  it('places the add-account action after the three reference rows', () => {
    const rows = buildVisibleAccountRows(buildAccountRows(2), '');
    expect(rows).toHaveLength(1_001);
    expect(rows.slice(0, 3).map(row => row.key)).toEqual([
      accountKey(2, 0),
      accountKey(2, 1),
      accountKey(2, 2),
    ]);
    expect(rows[3]).toMatchObject({
      type: 'action',
      key: 'account-add',
      title: '添加账户',
    });
    expect(rows[1_000].key).toBe(accountKey(2, 999));
  });

  it('filters only the selected wallet rows and hides the add action while searching', () => {
    const rows = buildVisibleAccountRows(
      buildAccountRows(712),
      'Account #1000',
    );
    expect(rows).toHaveLength(1);
    expect(rows[0].key).toBe(accountKey(712, 999));
  });
});
