import { Platform } from 'react-native';

import {
  MARKET_PAGE_SIZE,
  type IFetchUSMarketStatusResult,
  MARKET_TIME_FRAME_BY_RANGE,
} from './marketNativePagerData';

import type {
  MarketAsset,
  MarketBanner,
  MarketCategory,
  MarketConfig,
  MarketNetwork,
  MarketPageDescriptor,
  MarketPageResult,
  MarketTimeRange,
} from './marketNativePagerTypes';

export const MARKET_API_BASE_URL = 'https://utility.onekeycn.com';
export const MARKET_TEST_API_BASE_URL = 'https://utility.onekeytest.com';
export const MARKET_REQUEST_VERSION = '6.15.0';
const MARKET_WEB_PROXY_PREFIX = '/onekey-market-api';

const marketPlatform =
  Platform.OS === 'ios'
    ? 'ios-store'
    : Platform.OS === 'android'
    ? 'android-googleplay'
    : 'web';

export class MarketApiError extends Error {
  constructor(
    message: string,
    readonly sourceUrl: string,
    readonly httpStatus?: number,
    readonly responseCode?: number,
  ) {
    super(message);
    this.name = 'MarketApiError';
  }
}

export function getMarketRequestDiagnostics() {
  return {
    baseUrl: MARKET_API_BASE_URL,
    stocksAndTopCoinsBaseUrl: MARKET_TEST_API_BASE_URL,
    requestVersion: MARKET_REQUEST_VERSION,
    requestPlatform: marketPlatform,
    headerMode:
      Platform.OS === 'web'
        ? 'same-origin proxy: OneKeyWallet User-Agent + request version/platform'
        : 'OneKeyWallet User-Agent + request version/platform',
  } as const;
}

function requestHeaders(locale = 'zh-CN'): Record<string, string> {
  const headers: Record<string, string> = {
    Accept: 'application/json',
  };
  if (Platform.OS !== 'web') {
    headers['User-Agent'] = `OneKeyWallet/${MARKET_REQUEST_VERSION}`;
    headers['X-Onekey-Request-Version'] = MARKET_REQUEST_VERSION;
    headers['X-Onekey-Request-Platform'] = marketPlatform;
    headers['X-Onekey-Request-Locale'] = locale;
    headers['X-Onekey-Request-Currency'] = 'usd';
  }
  return headers;
}

function queryString(params: Record<string, string | number | undefined>) {
  return Object.entries(params)
    .filter(
      (entry): entry is [string, string | number] => entry[1] !== undefined,
    )
    .map(
      ([key, value]) =>
        `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`,
    )
    .join('&');
}

async function requestOneKey<T>(
  path: string,
  params: Record<string, string | number | undefined>,
  signal?: AbortSignal,
  locale = 'zh-CN',
  payload?: object,
): Promise<{ data: T; sourceUrl: string }> {
  const query = queryString(params);
  const requestPath = `${path}${query ? `?${query}` : ''}`;
  const baseUrl = path.startsWith('/swap/')
    ? 'https://swap.onekeycn.com'
    : path === '/utility/v1/stocks' || path === '/utility/v1/market/asset/list'
    ? MARKET_TEST_API_BASE_URL
    : MARKET_API_BASE_URL;
  const sourceUrl = `${baseUrl}${requestPath}`;
  // Production rejects or silently empties some browser-shaped requests. Keep
  // the canonical URL in diagnostics while the example dev server injects the
  // same public OneKey request headers used by native clients.
  const fetchUrl =
    Platform.OS === 'web'
      ? `${MARKET_WEB_PROXY_PREFIX}${requestPath}${
          query ? '&' : '?'
        }locale=${encodeURIComponent(locale)}`
      : sourceUrl;
  const response = await fetch(fetchUrl, {
    method: payload ? 'POST' : 'GET',
    headers: {
      ...requestHeaders(locale),
      ...(payload ? { 'Content-Type': 'application/json' } : {}),
    },
    body: payload ? JSON.stringify(payload) : undefined,
    signal,
  });
  if (!response.ok) {
    throw new MarketApiError(
      `Market HTTP ${response.status}`,
      sourceUrl,
      response.status,
    );
  }
  const body = (await response.json()) as {
    code?: number;
    message?: string;
    data?: T;
  };
  if (body.code !== 0 || body.data === undefined || body.data === null) {
    throw new MarketApiError(
      body.message || `Market response code ${String(body.code)}`,
      sourceUrl,
      response.status,
      body.code,
    );
  }
  return { data: body.data, sourceUrl };
}

type RawNetwork = {
  networkId: string;
  name: string;
  logoUrl: string;
};

type RawStock = {
  title?: string;
  subtitle?: string;
  source?: string;
  sourceLogoUri?: string;
  isOpen?: boolean;
  isPaused?: boolean;
  description?: string;
};

type RawToken = {
  networkId?: string;
  address?: string;
  isNative?: boolean;
  name: string;
  symbol: string;
  logoUrl?: string;
  logoUrls?: string[];
  price?: string;
  priceChange24hPercent?: string;
  priceChange5mPercent?: string;
  priceChange1hPercent?: string;
  priceChange4hPercent?: string;
  volume24h?: string;
  volume5m?: string;
  volume1h?: string;
  volume4h?: string;
  marketCap?: string;
  communityRecognized?: boolean;
  stock?: RawStock;
};

type RawPerp = {
  name: string;
  displayName?: string;
  tokenImageUrl?: string;
  markPrice?: string;
  change24hPercent?: string | number;
  volume24h?: string;
  maxLeverage?: number;
};

function normalizeToken(
  item: RawToken,
  sourceCategory: string,
  networks: ReadonlyMap<string, MarketNetwork>,
  timeRange: MarketTimeRange = '24h',
): MarketAsset {
  const network = item.networkId ? networks.get(item.networkId) : undefined;
  return {
    key: `${item.networkId ?? 'unknown'}:${item.address ?? ''}`,
    kind: item.stock ? 'stock' : 'token',
    sourceCategory,
    networkId: item.networkId,
    networkName: network?.name,
    networkLogoUrl: network?.logoUrl,
    address: item.address,
    isNative: item.isNative,
    name: item.name,
    symbol: item.symbol,
    logoUrl: item.logoUrl,
    logoUrls: item.logoUrls,
    price: item.price,
    // Like the source Market row's change24h/turnover fields, these display
    // values follow the selected interval; watchlist calls keep the 24h default.
    priceChange24hPercent: item.stock
      ? item.priceChange24hPercent
      : item[`priceChange${timeRange}Percent`] ?? item.priceChange24hPercent,
    volume24h: item[`volume${timeRange}`] ?? item.volume24h,
    marketCap: item.marketCap,
    communityRecognized: item.communityRecognized,
    stock: item.stock,
  };
}

function normalizePerp(item: RawPerp, sourceCategory: string): MarketAsset {
  const parts = item.name.split(':');
  return {
    key: `perps:${item.name}`,
    kind: 'perp',
    sourceCategory,
    name: item.displayName || parts.at(-1) || item.name,
    symbol: item.name,
    logoUrl: item.tokenImageUrl,
    price: item.markPrice,
    priceChange24hPercent:
      item.change24hPercent === undefined
        ? undefined
        : String(item.change24hPercent),
    volume24h: item.volume24h,
    maxLeverage: item.maxLeverage,
    dexLabel: parts.length > 1 ? parts[0] : 'Hyperliquid',
  };
}

export async function fetchMarketConfig(
  signal?: AbortSignal,
  locale = 'zh-CN',
): Promise<{
  config: MarketConfig;
  sourceUrl: string;
}> {
  const response = await requestOneKey<{
    spotCategories?: { type: string; name: string }[];
    stockCategories?: { category: string; name: string }[];
    perpsCategories?: { categoryId: string; name: string }[];
    networkList?: RawNetwork[];
    recommendTokens?: Array<{
      chainId: string;
      contractAddress: string;
      name: string;
      symbol: string;
      logo: string;
      communityRecognized?: boolean;
    }>;
    minLiquidity?: number;
  }>('/utility/v2/market/basic-config', { configVersion: 2 }, signal, locale);
  return {
    sourceUrl: response.sourceUrl,
    config: {
      recommendTokens: (response.data.recommendTokens ?? []).map(item => ({
        key: `${item.chainId}:${item.contractAddress}`,
        kind: 'token',
        sourceCategory: 'recommended',
        networkId: item.chainId,
        address: item.contractAddress,
        isNative: item.contractAddress === '',
        name: item.name,
        symbol: item.symbol,
        logoUrl: item.logo,
        communityRecognized: item.communityRecognized,
        networkLogoUrl: response.data.networkList?.find(
          network => network.networkId === item.chainId,
        )?.logoUrl,
      })),
      spotCategories: (response.data.spotCategories ?? []).map(item => ({
        id: item.type,
        name: item.name,
      })),
      stockCategories: (response.data.stockCategories ?? []).map(item => ({
        id: item.category,
        name: item.name,
      })),
      perpsCategories: (response.data.perpsCategories ?? []).map(item => ({
        id: item.categoryId,
        name: item.name,
      })),
      networks: response.data.networkList ?? [],
      minLiquidity: response.data.minLiquidity ?? 5000,
    },
  };
}

export async function fetchMarketBanners(
  signal?: AbortSignal,
  locale = 'zh-CN',
): Promise<{
  banners: MarketBanner[];
  sourceUrl: string;
}> {
  const response = await requestOneKey<{
    data?: Array<{
      _id: string;
      type?: 'ticker' | 'perps';
      title: string;
      backgroundColor: string;
      description?: { text: string; fontColor: string };
      tokenLogos?: string[];
      tokenListId: string;
    }>;
  }>('/utility/v2/market/banner/list', {}, signal, locale);
  return {
    sourceUrl: response.sourceUrl,
    banners: (response.data.data ?? []).map(item => ({
      id: item._id,
      type: item.type,
      title: item.title,
      backgroundColor: item.backgroundColor,
      description: item.description,
      tokenLogos: item.tokenLogos ?? [],
      tokenListId: item.tokenListId,
    })),
  };
}

export async function fetchMarketPage({
  page,
  pageNumber,
  cursor,
  config,
  networkId,
  timeRange,
  stockCategory,
  perpsCategory,
  watchlistKeys,
  forceFailure,
  signal,
  locale = 'zh-CN',
}: {
  page: MarketPageDescriptor;
  pageNumber: number;
  cursor?: string;
  config: MarketConfig;
  networkId?: string;
  timeRange: MarketTimeRange;
  stockCategory: string;
  perpsCategory: string;
  watchlistKeys?: readonly string[];
  forceFailure?: boolean;
  signal?: AbortSignal;
  locale?: string;
}): Promise<MarketPageResult> {
  if (forceFailure) {
    await requestOneKey(
      '/utility/v2/market/acceptance-intentional-404',
      {},
      signal,
      locale,
    );
  }

  const fetchedAt = new Date().toISOString();
  if (page.kind === 'watchlist') {
    const keys = [...new Set(watchlistKeys ?? [])];
    const spotKeys = keys.filter(key => !key.startsWith('perps:'));
    const networks = new Map(
      config.networks.map(item => [item.networkId, item]),
    );
    const [spot, perps] = await Promise.all([
      spotKeys.length
        ? requestOneKey<{ list: (RawToken | null)[] }>(
            '/utility/v2/market/token/list/batch',
            {},
            signal,
            locale,
            {
              currency: 'usd',
              tokenAddressList: spotKeys.map(key => {
                const separator = key.indexOf(':');
                const address = key.slice(separator + 1);
                return {
                  chainId: key.slice(0, separator),
                  contractAddress: address,
                  isNative: address === '',
                };
              }),
            },
          )
        : undefined,
      keys.some(key => key.startsWith('perps:'))
        ? fetchMarketPage({
            page: {
              key: 'watchlist-live-perps',
              kind: 'perps',
              name: 'Perps',
              categoryId: 'perps',
            },
            pageNumber: 1,
            config,
            timeRange,
            stockCategory: 'all',
            perpsCategory: 'all',
            signal,
            locale,
          })
        : undefined,
    ]);
    // The original batch API preserves request order, including unavailable entries.
    const spotItems = (spot?.data.list ?? []).flatMap((item, index) =>
      item
        ? [
            {
              ...normalizeToken(item, 'watchlist', networks),
              key: spotKeys[index],
            },
          ]
        : [],
    );
    const byKey = new Map(
      [...spotItems, ...(perps?.items ?? [])].map(item => [item.key, item]),
    );
    const items = keys.flatMap(key => {
      const item = byKey.get(key);
      return item ? [item] : [];
    });
    return {
      items,
      total: items.length,
      page: 1,
      canLoadMore: false,
      fetchedAt,
      sourceUrl: [spot?.sourceUrl, perps?.sourceUrl]
        .filter(Boolean)
        .join(' | '),
      note: `Requested all ${keys.length} watchlist identities; ${
        keys.length - items.length
      } unavailable in the public response.`,
    };
  }

  if (page.kind === 'perps') {
    const response = await requestOneKey<{
      tokens?: RawPerp[];
      updatedAt?: number;
    }>(
      '/utility/v2/market/perps/token-list',
      { category: perpsCategory, assetTypeVersion: 2 },
      signal,
      locale,
    );
    const items = (response.data.tokens ?? []).map(item =>
      normalizePerp(item, perpsCategory),
    );
    return {
      items,
      total: items.length,
      page: 1,
      canLoadMore: false,
      fetchedAt,
      sourceUrl: response.sourceUrl,
    };
  }

  if (page.kind === 'topCoins') {
    const response = await requestOneKey<{
      list?: Array<{
        assetId: string;
        name?: string;
        symbol: string;
        logoUrl?: string;
        price?: string;
        priceChange24hPercent?: string;
        marketCap?: string;
        volume24h?: string;
      }>;
      total?: number;
    }>(
      '/utility/v1/market/asset/list',
      { currency: 'usd', type: 'top_coins', page: pageNumber, limit: 100 },
      signal,
      locale,
    );
    const items: MarketAsset[] = (response.data.list ?? []).map(item => ({
      key: `asset:${item.assetId}`,
      kind: 'token',
      sourceCategory: 'top_coins',
      name: item.name || item.symbol,
      symbol: item.symbol,
      logoUrl: item.logoUrl,
      price: item.price,
      priceChange24hPercent: item.priceChange24hPercent,
      marketCap: item.marketCap,
      volume24h: item.volume24h,
    }));
    return {
      items,
      total: response.data.total ?? items.length,
      page: pageNumber,
      canLoadMore:
        items.length > 0 &&
        (response.data.total === undefined
          ? items.length >= 100
          : pageNumber * 100 < response.data.total),
      fetchedAt,
      sourceUrl: response.sourceUrl,
    };
  }

  if (page.categoryId === 'stocks' && stockCategory !== '__token-list__') {
    const response = await requestOneKey<{
      items?: Array<{
        stockId: string;
        symbol: string;
        name: string;
        logoUrl?: string;
        price?: string;
        priceChange24hPercent?: string;
        marketCap?: string;
        volume24h?: string;
      }>;
      nextCursor?: string;
      total?: number;
    }>(
      '/utility/v1/stocks',
      {
        limit: MARKET_PAGE_SIZE,
        cursor,
        category: stockCategory === 'all' ? undefined : stockCategory,
        sortBy: 'default',
        sortType: 'asc',
      },
      signal,
      locale,
    );
    const items: MarketAsset[] = (response.data.items ?? []).map(item => ({
      key: `stock:${item.stockId}`,
      kind: 'stock',
      sourceCategory: page.categoryId,
      name: item.name,
      symbol: item.symbol,
      logoUrl: item.logoUrl,
      price: item.price,
      priceChange24hPercent: item.priceChange24hPercent,
      marketCap: item.marketCap,
      volume24h: item.volume24h,
      stock: { subtitle: item.name },
    }));
    return {
      items,
      total: response.data.total ?? items.length,
      page: pageNumber,
      canLoadMore: Boolean(
        response.data.nextCursor && response.data.nextCursor !== cursor,
      ),
      nextCursor: response.data.nextCursor,
      fetchedAt,
      sourceUrl: response.sourceUrl,
    };
  }

  const networks = new Map(config.networks.map(item => [item.networkId, item]));
  const response = await requestOneKey<{
    list?: RawToken[];
    total?: number;
  }>(
    '/utility/v2/market/token/list',
    {
      networkId: networkId || undefined,
      sortBy: 'v24hUSD',
      sortType: 'desc',
      page: pageNumber,
      limit: MARKET_PAGE_SIZE,
      minLiquidity: config.minLiquidity,
      type: page.categoryId,
      category:
        page.categoryId === 'stocks' &&
        stockCategory !== 'all' &&
        stockCategory !== '__token-list__'
          ? stockCategory
          : undefined,
      timeFrame: MARKET_TIME_FRAME_BY_RANGE[timeRange],
      currency: 'usd',
    },
    signal,
    locale,
  );
  const items = (response.data.list ?? []).map(item =>
    normalizeToken(item, page.categoryId, networks, timeRange),
  );
  const total = response.data.total ?? items.length;
  return {
    items,
    total,
    page: pageNumber,
    // Production currently ignores limit=20 and returns all 101 rows on page
    // 1, then an empty page 2. Keep page 2 reachable so the example verifies
    // the real response-driven pagination/end-state contract.
    canLoadMore:
      items.length > 0 &&
      (items.length >= MARKET_PAGE_SIZE || items.length < total),
    fetchedAt,
    sourceUrl: response.sourceUrl,
    note:
      pageNumber === 1 && items.length > MARKET_PAGE_SIZE
        ? `Server returned ${items.length} rows despite limit=${MARKET_PAGE_SIZE}.`
        : undefined,
  };
}

export function defaultPerpsCategory(categories: readonly MarketCategory[]) {
  return (
    categories.find(item => item.id === 'hot')?.id ?? categories[0]?.id ?? 'hot'
  );
}

// Same unavailable fallback as ServiceSwap.fetchCheckUSMarketStatus.
export async function fetchUSMarketStatus(
  signal?: AbortSignal,
): Promise<IFetchUSMarketStatusResult> {
  try {
    return (
      await requestOneKey<IFetchUSMarketStatusResult>(
        '/swap/v1/check/us-market-status',
        {},
        signal,
      )
    ).data;
  } catch {
    return {
      open: false,
      session: 'CLOSED',
      reason: 'market-status-unavailable',
      unavailable: true,
    };
  }
}
