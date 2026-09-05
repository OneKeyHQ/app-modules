import {
  NETWORK_SELECTOR_ASSET_COUNT,
  NETWORK_SELECTOR_BALANCES,
  NETWORK_SELECTOR_DEFAULT_ENABLED_IDS,
  NETWORK_SELECTOR_MAINNET_COUNT,
  NETWORK_SELECTOR_NETWORKS,
  NETWORK_SELECTOR_SOURCE_REF,
  NETWORK_SELECTOR_TESTNET_COUNT,
  buildNetworkSelectorRows,
  buildNetworkSelectorSections,
  searchNetworkSelectorNetworks,
} from '../pages/nativeListNetworkSelectorData';

describe('Native List network-selector data', () => {
  it('keeps the complete production preset catalog with stable source order', () => {
    expect(NETWORK_SELECTOR_SOURCE_REF).toBe(
      'app-monorepo@a1bacaf72172f39d7da1df7052065ce8d593f088',
    );
    expect(NETWORK_SELECTOR_NETWORKS).toHaveLength(96);
    expect(
      NETWORK_SELECTOR_NETWORKS.filter(network => !network.isTestnet),
    ).toHaveLength(NETWORK_SELECTOR_MAINNET_COUNT);
    expect(
      NETWORK_SELECTOR_NETWORKS.filter(network => network.isTestnet),
    ).toHaveLength(NETWORK_SELECTOR_TESTNET_COUNT);
    expect(
      new Set(NETWORK_SELECTOR_NETWORKS.map(network => network.id)).size,
    ).toBe(96);
    expect(
      NETWORK_SELECTOR_NETWORKS.every(
        network =>
          network.sourceName &&
          network.id &&
          network.name &&
          network.logoURI.startsWith('https://') &&
          network.impl &&
          network.symbol &&
          network.shortname,
      ),
    ).toBe(true);
    expect(NETWORK_SELECTOR_NETWORKS.slice(0, 4).map(({ id }) => id)).toEqual([
      'btc--0',
      'doge--0',
      'bch--0',
      'ltc--0',
    ]);
  });

  it('orders deterministic asset balances then preserves source order in A-Z groups', () => {
    const sections = buildNetworkSelectorSections('network');
    const assets = sections[0];
    expect(assets.networks).toHaveLength(NETWORK_SELECTOR_ASSET_COUNT);
    expect(assets.networks.map(({ id }) => id)).toEqual([
      'evm--56',
      'evm--1',
      'evm--42161',
      'evm--59144',
      'evm--10',
      'sol--101',
      'evm--8453',
      'btc--0',
      'evm--43114',
    ]);
    expect(Object.values(NETWORK_SELECTOR_BALANCES)).toEqual([
      '12.66',
      '8.12',
      '6.15',
      '3.25',
      '3.09',
      '2.88',
      '1.97',
      '1.87',
      '1.45',
    ]);

    const letterSections = sections.filter(section => section.indexTitle);
    expect(letterSections.map(section => section.indexTitle)).toEqual(
      letterSections
        .map(section => section.indexTitle)
        .slice()
        .sort((left, right) => (left ?? '').localeCompare(right ?? '')),
    );
    expect(
      letterSections
        .find(section => section.indexTitle === 'A')
        ?.networks.slice(0, 3)
        .map(({ name }) => name),
    ).toEqual(['Aurora', 'Aleph Zero EVM', 'Akash']);
    expect(sections.at(-1)).toMatchObject({
      key: 'testnet',
      title: '测试网',
    });
  });

  it('matches names broadly and implementation, symbol, or shortname exactly', () => {
    expect(
      searchNetworkSelectorNetworks(NETWORK_SELECTOR_NETWORKS, 'bit').map(
        ({ name }) => name,
      ),
    ).toEqual(expect.arrayContaining(['Bitcoin', 'Bitcoin Cash', 'Bitlayer']));
    expect(
      searchNetworkSelectorNetworks(NETWORK_SELECTOR_NETWORKS, 'AZERO').map(
        ({ id }) => id,
      ),
    ).toEqual(['evm--41455']);
    expect(
      searchNetworkSelectorNetworks(NETWORK_SELECTOR_NETWORKS, 'evm'),
    ).not.toHaveLength(0);
    expect(
      searchNetworkSelectorNetworks(NETWORK_SELECTOR_NETWORKS, 'no-such-chain'),
    ).toEqual([]);
  });

  it('builds serializable, unique native rows for both selector modes', () => {
    const selected = new Set<string>(NETWORK_SELECTOR_DEFAULT_ENABLED_IDS);
    const networkRows = buildNetworkSelectorRows('network', '', selected);
    const portfolioRows = buildNetworkSelectorRows('portfolio', '', selected);

    expect(new Set(networkRows.map(row => row.key)).size).toBe(
      networkRows.length,
    );
    expect(new Set(portfolioRows.map(row => row.key)).size).toBe(
      portfolioRows.length,
    );
    expect(networkRows.filter(row => row.type === 'identity')).toHaveLength(96);
    expect(portfolioRows.filter(row => row.type === 'identity')).toHaveLength(
      NETWORK_SELECTOR_MAINNET_COUNT,
    );
    expect(networkRows[0]).toMatchObject({
      type: 'sectionHeader',
      presentation: 'networkSelector',
      title: '有资产的网络',
    });
    expect(networkRows).toContainEqual(
      expect.objectContaining({
        type: 'system',
        variant: 'spacer',
        height: 18,
      }),
    );
    expect(
      networkRows
        .filter(row => row.type === 'identity')
        .every(row => row.presentation === 'networkSelector'),
    ).toBe(true);
    expect(portfolioRows[0]).toMatchObject({
      type: 'sectionHeader',
      variant: 'summary',
      title: '已选择 18 个网络',
    });
    expect(
      portfolioRows
        .filter(row => row.type === 'identity')
        .every(row => row.trailing?.some(item => item.kind === 'checkbox')),
    ).toBe(true);

    expect(() => JSON.stringify(networkRows)).not.toThrow();
  });
});
