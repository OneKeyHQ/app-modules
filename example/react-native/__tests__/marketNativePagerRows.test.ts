import {
  buildMarketQuotePatches,
  buildMarketSnapshot,
} from '../pages/marketNativePagerRows';
import { MARKET_REPLAY_SNAPSHOT } from '../pages/marketNativePagerSnapshot';

describe('Market built-in NativeList rows', () => {
  const source = MARKET_REPLAY_SNAPSHOT.spot.trending?.[0];

  it('uses the native market template with bounded source-derived styles', () => {
    expect(source).toBeDefined();
    const snapshot = buildMarketSnapshot({
      items: [source!],
      generation: 7,
      theme: 'dark',
      watchlist: true,
      loading: false,
      loadingMore: false,
      canLoadMore: false,
    });
    expect(snapshot.rows[0]).toMatchObject({
      type: 'market',
      height: 72,
      leading: {
        networkImage: {
          uri: 'https://uni.onekey-asset.com/static/chain/btc.png',
        },
      },
      badges: [
        {
          iconName: 'verified',
          actionKey: 'community-info',
        },
      ],
      longPressActionKey: 'watchlist-menu',
      diagnostics: { imageBindActionKey: 'image-bind' },
      style: {
        horizontalPadding: 20,
        leadingGap: 14,
        lineGap: 4,
        image: { width: 32, height: 32, shape: 'circle' },
        title: { fontSize: 16, lineHeight: 24, lines: 1 },
        price: { fontSize: 16, alignment: 'end' },
        change: { fontSize: 14, alignment: 'center' },
        changeWidth: 80,
        changeHeight: 32,
      },
    });
    expect(() => JSON.stringify(snapshot)).not.toThrow();
  });

  it('patches quote fields only and never rebinds the leading image', () => {
    expect(source).toBeDefined();
    const next = {
      ...source!,
      price: String(Number(source!.price ?? 0) + 1),
      priceChange24hPercent: '1.25',
      volume24h: String(Number(source!.volume24h ?? 0) + 2),
    };
    const patches = buildMarketQuotePatches([source!], [next]);
    expect(patches).toHaveLength(1);
    expect(patches?.[0]).toMatchObject({
      type: 'market',
      key: source!.key,
      changes: {
        price: expect.any(String),
        change: { text: '+1.25%', tone: 'positive' },
      },
    });
    expect(patches?.[0]?.changes).not.toHaveProperty('leading');
    expect(patches?.[0]?.changes).not.toHaveProperty('subtitle');
    expect(patches?.[0]?.changes).not.toHaveProperty('badges');
    expect(patches?.[0]?.changes).not.toHaveProperty('style');
    expect(
      buildMarketQuotePatches([source!], [{ ...next, key: 'different' }]),
    ).toBeUndefined();
  });

  it('keeps loading, empty, error, and pagination states inside NativeList', () => {
    const common = {
      items: [] as const,
      generation: 1,
      theme: 'dark' as const,
      watchlist: false,
      loadingMore: false,
      canLoadMore: false,
    };
    const initial = buildMarketSnapshot({ ...common, loading: true });
    expect(initial.rows).toHaveLength(10);
    expect(initial.rows[0]).toMatchObject({
      type: 'system',
      variant: 'loading',
      presentation: 'market',
      loadingStyle: 'skeleton',
      height: 56,
    });
    expect(initial.rows.every(row => !('message' in row))).toBe(true);
    const nextPage = buildMarketSnapshot({
      ...common,
      items: [source!],
      loading: false,
      loadingMore: true,
    });
    expect(nextPage.rows).toHaveLength(2);
    expect(nextPage.rows[0]).toMatchObject({
      type: 'market',
      key: source!.key,
    });
    expect(nextPage.rows[1]).toMatchObject({
      type: 'system',
      variant: 'loading',
      loadingStyle: 'spinner',
      height: 52,
    });
    expect(
      buildMarketSnapshot({ ...common, loading: false, error: 'business 404' })
        .rows[0],
    ).toMatchObject({ type: 'system', variant: 'retry', actionKey: 'retry' });
    expect(
      buildMarketSnapshot({ ...common, loading: false }).emptyState,
    ).toMatchObject({
      type: 'system',
      variant: 'noMatch',
      presentation: 'market',
    });
  });
});
