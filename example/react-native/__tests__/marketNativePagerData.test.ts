import {
  applyMarketScenario,
  buildMarketPages,
  filterMarketSearch,
  formatPrice,
  formatCompactUsd,
  formatChange,
  replayItemsForPage,
  resolveUSMarketStatusVariant,
} from '../pages/marketNativePagerData';
import { MARKET_REPLAY_SNAPSHOT } from '../pages/marketNativePagerSnapshot';

describe('Market native pager production fixture', () => {
  it('builds pages from every server spot category instead of a fixed count', () => {
    const config = {
      ...MARKET_REPLAY_SNAPSHOT.config,
      spotCategories: [
        { id: 'alpha', name: 'Alpha' },
        { id: 'beta', name: 'Beta' },
        { id: 'gamma', name: 'Gamma' },
        { id: 'delta', name: 'Delta' },
      ],
    };

    expect(buildMarketPages(config).map(page => page.categoryId)).toEqual([
      'watchlist',
      'alpha',
      'beta',
      'gamma',
      'delta',
      'top_coins',
      'perps',
    ]);

    expect(
      buildMarketPages({ ...config, perpsCategories: [] }).map(
        page => page.categoryId,
      ),
    ).not.toContain('perps');
  });

  it('preserves a server-provided top-coins category without duplicating it', () => {
    const pages = buildMarketPages({
      ...MARKET_REPLAY_SNAPSHOT.config,
      spotCategories: [
        ...MARKET_REPLAY_SNAPSHOT.config.spotCategories,
        { id: 'top_coins', name: 'Top Assets' },
      ],
    });

    expect(pages.filter(page => page.categoryId === 'top_coins')).toHaveLength(
      1,
    );
    expect(pages.find(page => page.categoryId === 'top_coins')?.name).toBe(
      'Top Assets',
    );
  });

  it('keeps snapshot provenance and real token, stock, and perps metadata', () => {
    expect(MARKET_REPLAY_SNAPSHOT.source.baseUrl).toBe(
      'https://utility.onekeycn.com',
    );
    expect(MARKET_REPLAY_SNAPSHOT.fetchedAt).toMatch(
      /^2026-09-08T\d{2}:\d{2}:\d{2}/,
    );
    expect(MARKET_REPLAY_SNAPSHOT.source.requiredHeaders).toEqual(
      expect.arrayContaining([
        'Accept: application/json',
        'User-Agent: OneKeyWallet/6.15.0',
        'X-Onekey-Request-Version: 6.15.0',
        'X-Onekey-Request-Platform: ios-store',
      ]),
    );

    const allSpot = Object.values(MARKET_REPLAY_SNAPSHOT.spot).flat();
    const allPerps = Object.values(MARKET_REPLAY_SNAPSHOT.perps).flat();
    expect(allSpot.some(item => item.logoUrl && item.networkLogoUrl)).toBe(
      true,
    );
    expect(
      allSpot.some(
        item => item.stock?.source && item.stock.sourceLogoUri && item.price,
      ),
    ).toBe(true);
    expect(
      allPerps.some(
        item => item.maxLeverage && item.dexLabel && item.logoUrl && item.price,
      ),
    ).toBe(true);
  });

  it('replays local watchlist order and filters real metadata without mutation', () => {
    const firstTrending = MARKET_REPLAY_SNAPSHOT.spot.watchlist?.[0];
    const firstPerp = MARKET_REPLAY_SNAPSHOT.perps.hot?.[0];
    expect(firstTrending).toBeDefined();
    expect(firstPerp).toBeDefined();

    const watchlist = replayItemsForPage(
      MARKET_REPLAY_SNAPSHOT,
      {
        key: 'watchlist',
        kind: 'watchlist',
        name: 'Watchlist',
        categoryId: 'watchlist',
      },
      [firstPerp!.key, firstTrending!.key],
      'hot',
    );
    expect(watchlist.map(item => item.key)).toEqual([
      firstPerp!.key,
      firstTrending!.key,
    ]);
    expect(filterMarketSearch(watchlist, firstTrending!.symbol)).toEqual([
      watchlist[1],
    ]);
    expect(applyMarketScenario(watchlist, 'short')).toEqual(watchlist);
    expect(applyMarketScenario(watchlist, 'empty')).toEqual([]);
  });
  it('provides captured English names for every new stock category and search result', () => {
    const metadata = MARKET_REPLAY_SNAPSHOT.locales?.['en-US']?.stockMetadata;
    const stocks = Object.values(MARKET_REPLAY_SNAPSHOT.stocks ?? {}).flat();
    expect(new Set(stocks.map(item => item.key)).size).toBe(1009);
    expect(stocks.every(item => Boolean(metadata?.[item.key]?.subtitle))).toBe(
      true,
    );
    expect(metadata?.['stock:AAPL']?.subtitle).toBe('Apple');
  });
  it('replays exact network, time and stock-category responses instead of the default page', () => {
    const page = buildMarketPages(MARKET_REPLAY_SNAPSHOT.config).find(
      item => item.categoryId === 'trending',
    )!;
    const filters = {
      networkId: 'evm--1',
      timeRange: '5m',
      stockCategory: 'all',
    };
    const items = replayItemsForPage(
      MARKET_REPLAY_SNAPSHOT,
      page,
      [],
      'hot',
      filters,
    );
    expect(items).toBe(
      MARKET_REPLAY_SNAPSHOT.spotQueries?.['trending|evm--1|5m|all'],
    );
    expect(items.every(item => item.networkId === 'evm--1')).toBe(true);
    expect(items).not.toEqual(
      replayItemsForPage(MARKET_REPLAY_SNAPSHOT, page, [], 'hot', {
        ...filters,
        timeRange: '24h',
      }),
    );
    const stocks = { ...page, categoryId: 'stocks' };
    expect(
      replayItemsForPage(MARKET_REPLAY_SNAPSHOT, stocks, [], 'hot', {
        ...filters,
        stockCategory: 'market__tab__ai_tech',
      }),
    ).toBe(MARKET_REPLAY_SNAPSHOT.stocks?.market__tab__ai_tech);
  });

  it('includes complete approved test Stocks and Top Coins datasets', () => {
    expect(MARKET_REPLAY_SNAPSHOT.source.testBaseUrl).toBe(
      'https://utility.onekeytest.com',
    );
    expect(MARKET_REPLAY_SNAPSHOT.stocks?.all).toHaveLength(1009);
    expect(
      new Set(MARKET_REPLAY_SNAPSHOT.stocks?.all.map(item => item.key)).size,
    ).toBe(1009);
    expect(MARKET_REPLAY_SNAPSHOT.spot.top_coins).toHaveLength(32);
    expect(Object.keys(MARKET_REPLAY_SNAPSHOT.stocks ?? {})).toEqual(
      MARKET_REPLAY_SNAPSHOT.config.stockCategories.map(
        category => category.id,
      ),
    );
  });

  it('keeps every captured asset available for full watchlist replay', () => {
    const keys = [
      ...new Set(
        [
          ...Object.values(MARKET_REPLAY_SNAPSHOT.spotQueries ?? {}).flat(),
          ...Object.values(MARKET_REPLAY_SNAPSHOT.perps).flat(),
        ].map(item => item.key),
      ),
    ];
    expect(keys.length).toBe(1735);
    const page = buildMarketPages(MARKET_REPLAY_SNAPSHOT.config)[0]!;
    expect(
      replayItemsForPage(MARKET_REPLAY_SNAPSHOT, page, keys, 'hot'),
    ).toHaveLength(keys.length);
  });

  it('adds all source recommendations with captured prices and 24h changes', () => {
    const recommendations = MARKET_REPLAY_SNAPSHOT.config.recommendTokens!;
    const page = buildMarketPages(MARKET_REPLAY_SNAPSHOT.config)[0]!;
    const added = replayItemsForPage(
      MARKET_REPLAY_SNAPSHOT,
      page,
      recommendations.map(item => item.key),
      'all',
    );
    expect(added).toHaveLength(8);
    expect(added.map(item => item.symbol)).toEqual([
      'BTC',
      'ETH',
      'BNB',
      'SOL',
      'TRX',
      'AVAX',
      'ASTER',
      'AAVE',
    ]);
    for (const item of added) {
      expect(Number(item.price)).toBeGreaterThan(0);
      expect(Number.isFinite(Number(item.priceChange24hPercent))).toBe(true);
    }
  });

  it('matches production precision and capped percentage text', () => {
    expect(formatPrice('78331')).toBe('$78,331.00');
    expect(formatPrice('0.00321374')).toBe('$0.003214');
    expect(formatCompactUsd('185170')).toBe('$185.17K');
    expect(formatChange('123456')).toBe('>+99,999%');
    expect(formatChange('-123456')).toBe('>-99,999%');
    expect(formatChange('0')).toBe('+0.00%');
  });
});

describe('Market live watchlist', () => {
  it.each([
    ['5m', '-1.36', '36398.85612902936'],
    ['1h', '-1.17', '244097.7954039045'],
    ['4h', '-3.86', '746446.40762749999'],
    ['24h', '-12.27', '4558208.57475499481'],
  ] as const)(
    'uses %s metrics in replay and live while preserving 24h watchlist values',
    async (range, change, volume) => {
      const { fetchMarketPage } =
        require('../pages/marketNativePagerApi') as typeof import('../pages/marketNativePagerApi');
      const captured =
        MARKET_REPLAY_SNAPSHOT.spotQueries![
          `trending|sol--101|${range}|all`
        ]![0]!;
      expect(captured.priceChange24hPercent).toBe(change);
      expect(captured.volume24h).toBe(volume);
      const originalFetch = globalThis.fetch;
      globalThis.fetch = jest.fn(async () => ({
        ok: true,
        json: async () => ({
          code: 0,
          data: {
            list: [
              {
                ...captured,
                priceChange24hPercent: '-12.27',
                volume24h: '4558208.57475499481',
                [`priceChange${range}Percent`]: change,
                [`volume${range}`]: volume,
              },
            ],
            total: 1,
          },
        }),
      })) as unknown as typeof fetch;
      try {
        const result = await fetchMarketPage({
          page: {
            key: 'spot:trending',
            kind: 'spot',
            name: 'Trending',
            categoryId: 'trending',
          },
          pageNumber: 1,
          config: MARKET_REPLAY_SNAPSHOT.config,
          timeRange: range,
          stockCategory: 'all',
          perpsCategory: 'all',
          watchlistKeys: [],
          networkId: 'sol--101',
        });
        expect(result.items[0]?.priceChange24hPercent).toBe(change);
        expect(result.items[0]?.volume24h).toBe(volume);
        const canonical = replayItemsForPage(
          MARKET_REPLAY_SNAPSHOT,
          buildMarketPages(MARKET_REPLAY_SNAPSHOT.config)[0]!,
          [captured.key],
          'all',
        )[0]!;
        expect(canonical.priceChange24hPercent).toBe('-12.29');
        expect(canonical.volume24h).toBe('4543245.05224630659');
      } finally {
        globalThis.fetch = originalFetch;
      }
    },
  );

  it('requests every saved identity, keeps its order, and uses the selected locale and all perps', async () => {
    const { fetchMarketPage } =
      require('../pages/marketNativePagerApi') as typeof import('../pages/marketNativePagerApi');
    const unique = new Map(
      Object.values(MARKET_REPLAY_SNAPSHOT.spotQueries ?? {})
        .flat()
        .map(item => [item.key, item]),
    );
    const tokens = [...unique.values()];
    const keys = [...unique.keys(), 'perps:BTC'];
    const originalFetch = globalThis.fetch;
    const fetchMock = jest.fn(async (url: string) => ({
      ok: true,
      status: 200,
      json: async () => ({
        code: 0,
        data: url.includes('/batch')
          ? { list: tokens }
          : { tokens: [{ name: 'BTC', markPrice: '1' }] },
      }),
    }));
    globalThis.fetch = fetchMock as unknown as typeof fetch;
    try {
      const result = await fetchMarketPage({
        page: {
          key: 'watchlist',
          kind: 'watchlist',
          name: 'Favorites',
          categoryId: 'watchlist',
        },
        pageNumber: 1,
        config: MARKET_REPLAY_SNAPSHOT.config,
        timeRange: '1h',
        stockCategory: 'all',
        perpsCategory: 'stocks',
        watchlistKeys: keys,
        locale: 'en-US',
      });
      expect(result.items.map(item => item.key)).toEqual(keys);
      expect(fetchMock).toHaveBeenCalledTimes(2);
      const calls = fetchMock.mock.calls as unknown as [string, RequestInit][];
      const [url, options] = calls.find(([path]) => path.includes('/batch'))!;
      expect(url).toContain('/utility/v2/market/token/list/batch');
      expect(options.method).toBe('POST');
      expect(options.headers).toEqual(
        expect.objectContaining({ 'X-Onekey-Request-Locale': 'en-US' }),
      );
      const body = JSON.parse(options.body as string);
      expect(body.tokenAddressList).toHaveLength(tokens.length);
      expect(body.tokenAddressList.at(-1).contractAddress).toBe(
        tokens.at(-1)?.address,
      );
      expect(calls.find(([path]) => path.includes('/perps/'))![0]).toContain(
        'category=all',
      );
    } finally {
      globalThis.fetch = originalFetch;
    }
  });
});

describe('source stock status badge', () => {
  it('preserves issuer, halt, closure, session-gap and unavailable fallback precedence', () => {
    const now = new Date('2026-09-09T14:00:00Z');
    const source = 'ondo';
    const base = { source, isOpen: true, now };
    expect(
      resolveUSMarketStatusVariant({ ...base, source: 'xstock' }),
    ).toBeUndefined();
    expect(resolveUSMarketStatusVariant({ ...base, isPaused: true })).toBe(
      'halted',
    );
    expect(resolveUSMarketStatusVariant({ ...base, isOpen: false })).toBe(
      'closed',
    );
    expect(
      resolveUSMarketStatusVariant({
        ...base,
        status: { open: false, session: 'CLOSED', reason: null },
      }),
    ).toBe('open247');
    expect(
      resolveUSMarketStatusVariant({
        ...base,
        isPaused: true,
        now: new Date('2026-09-09T13:30:00Z'),
      }),
    ).toBe('awaitingOpen');
    expect(
      resolveUSMarketStatusVariant({
        ...base,
        status: {
          open: false,
          session: 'CLOSED',
          reason: 'market-status-unavailable',
          unavailable: true,
        },
      }),
    ).toBe('open');
    for (const [session, variant] of [
      ['PRE_MARKET', 'preMarket'],
      ['REGULAR', 'open'],
      ['POST_MARKET', 'postMarket'],
      ['OVERNIGHT', 'overnight'],
    ] as const) {
      expect(
        resolveUSMarketStatusVariant({
          ...base,
          status: { open: true, session, reason: null },
        }),
      ).toBe(variant);
    }
    expect(
      resolveUSMarketStatusVariant({
        ...base,
        now: new Date('2026-09-12T14:00:00Z'),
      }),
    ).toBe('open247');
  });
});
