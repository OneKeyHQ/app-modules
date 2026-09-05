import {
  ACCOUNT_SELECTOR_ACCOUNT_CACHE_LIMIT,
  ACCOUNT_SELECTOR_ACCOUNTS_PER_WALLET,
  ACCOUNT_SELECTOR_LOGICAL_ACCOUNT_COUNT,
  ACCOUNT_SELECTOR_WALLET_COUNT,
  AccountRowsCache,
  accountKey,
  buildAccountRows,
  buildVisibleAccountRows,
  buildWalletRows,
  parseAccountKey,
} from '../pages/nativeListAccountSelectorData';

describe('Native List account-selector stress data', () => {
  it('keeps the full 1,000 by 1,000 logical scale without materializing it', () => {
    expect(ACCOUNT_SELECTOR_WALLET_COUNT).toBe(1_000);
    expect(ACCOUNT_SELECTOR_ACCOUNTS_PER_WALLET).toBe(1_000);
    expect(ACCOUNT_SELECTOR_LOGICAL_ACCOUNT_COUNT).toBe(1_000_000);
    expect(buildWalletRows()).toHaveLength(1_000);
    expect(buildAccountRows(0)).toHaveLength(1_000);
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
