export type MarketDataMode = 'replay' | 'live';

export type MarketScenario = 'long' | 'short' | 'empty' | 'error';

export type MarketTimeRange = '5m' | '1h' | '4h' | '24h';

export type MarketPageKind = 'watchlist' | 'spot' | 'topCoins' | 'perps';

export type MarketCategory = Readonly<{
  id: string;
  name: string;
}>;

export type MarketNetwork = Readonly<{
  networkId: string;
  name: string;
  logoUrl: string;
}>;

export type MarketStockMetadata = Readonly<{
  title?: string;
  subtitle?: string;
  source?: string;
  sourceLogoUri?: string;
  isOpen?: boolean;
  isPaused?: boolean;
  description?: string;
}>;

export type MarketAsset = Readonly<{
  key: string;
  kind: 'token' | 'stock' | 'perp';
  sourceCategory: string;
  networkId?: string;
  networkName?: string;
  networkLogoUrl?: string;
  address?: string;
  isNative?: boolean;
  name: string;
  symbol: string;
  logoUrl?: string;
  logoUrls?: readonly string[];
  price?: string;
  priceChange24hPercent?: string;
  volume24h?: string;
  marketCap?: string;
  communityRecognized?: boolean;
  stock?: MarketStockMetadata;
  maxLeverage?: number;
  dexLabel?: string;
  subtitle?: string;
}>;

export type MarketBanner = Readonly<{
  id: string;
  type?: 'ticker' | 'perps';
  title: string;
  backgroundColor: string;
  description?: Readonly<{
    text: string;
    fontColor: string;
  }>;
  tokenLogos: readonly string[];
  tokenListId: string;
}>;

export type MarketConfig = Readonly<{
  recommendTokens?: readonly MarketAsset[];
  spotCategories: readonly MarketCategory[];
  stockCategories: readonly MarketCategory[];
  perpsCategories: readonly MarketCategory[];
  networks: readonly MarketNetwork[];
  minLiquidity: number;
}>;

export type MarketPageDescriptor = Readonly<{
  key: string;
  kind: MarketPageKind;
  name: string;
  categoryId: string;
}>;

export type MarketReplaySnapshot = Readonly<{
  schemaVersion: 1;
  fetchedAt: string;
  source: Readonly<{
    baseUrl: string;
    testBaseUrl?: string;
    testFetchedAt?: string;
    requestVersion: string;
    requestPlatform: string;
    requiredHeaders: readonly string[];
    notes: readonly string[];
  }>;
  config: MarketConfig;
  banners: readonly MarketBanner[];
  locales?: Readonly<
    Record<
      string,
      Readonly<{
        config: MarketConfig;
        banners: readonly MarketBanner[];
        stockMetadata?: Readonly<Record<string, MarketStockMetadata>>;
      }>
    >
  >;
  /** Exact public response for category|network|time range|stock category. */
  spotQueries?: Readonly<Record<string, readonly MarketAsset[]>>;
  spot: Readonly<Record<string, readonly MarketAsset[]>>;
  stocks?: Readonly<Record<string, readonly MarketAsset[]>>;
  perps: Readonly<Record<string, readonly MarketAsset[]>>;
}>;

export type MarketPageResult = Readonly<{
  items: readonly MarketAsset[];
  total: number;
  page: number;
  canLoadMore: boolean;
  fetchedAt: string;
  sourceUrl: string;
  note?: string;
  nextCursor?: string;
}>;
