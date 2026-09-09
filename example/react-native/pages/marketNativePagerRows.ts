import type {
  MarketBadgeModel,
  MarketRow,
  MarketRowStyle,
  NativeListSnapshot,
  NativeListTheme,
  RowModel,
  RowPatch,
  ValueTextSegment,
} from '@onekeyfe/react-native-native-list';

import { MARKET_SOURCE_NETWORK_LOGOS } from './marketNativePagerAssets';

import {
  formatChange,
  formatCompactUsd,
  formatPrice,
} from './marketNativePagerData';

import type { MarketAsset } from './marketNativePagerTypes';

export type MarketThemeName = 'dark' | 'light';

export const MARKET_NATIVE_THEMES: Record<MarketThemeName, NativeListTheme> = {
  dark: {
    background: '#0F0F0F',
    rowBackground: '#0F0F0F',
    rowSelectedBackground: '#262626',
    rowPressedBackground: '#202020',
    subduedBackground: '#202020',
    strongBackground: '#2A2A2A',
    primaryText: '#FFFFFFED',
    secondaryText: '#FFFFFFAF',
    disabledText: '#FFFFFF64',
    icon: '#FFFFFFA3',
    iconSubdued: '#FFFFFF64',
    separator: '#FFFFFF22',
    accent: '#44D7A8',
    positive: '#46FEA5D4',
    negative: '#F1495B',
    criticalBackground: '#F1495B',
    inverseBackground: '#FFFFFFED',
    inverseText: '#0F0F0F',
    info: '#70B8FF',
    caution: '#F5A623',
  },
  light: {
    background: '#FFFFFF',
    rowBackground: '#FFFFFF',
    rowSelectedBackground: '#F0F0F0',
    rowPressedBackground: '#F5F5F5',
    subduedBackground: '#F5F5F5',
    strongBackground: '#ECECEC',
    primaryText: '#000000DF',
    secondaryText: '#0000009B',
    disabledText: '#A8A8A8',
    icon: '#6B6B6B',
    iconSubdued: '#A8A8A8',
    separator: '#0000001F',
    accent: '#168566',
    positive: '#00713FDE',
    negative: '#D92D42',
    criticalBackground: '#E44857',
    inverseBackground: '#111111',
    inverseText: '#FFFFFF',
    info: '#1677C8',
    caution: '#A96300',
  },
};

const TOKEN_ROW_STYLE: MarketRowStyle = {
  horizontalPadding: 20,
  verticalPadding: 12,
  leadingGap: 14,
  lineGap: 4,
  titleBadgeGap: 4,
  trailingGap: 8,
  image: {
    width: 32,
    height: 32,
    shape: 'circle',
    cornerRadius: 16,
    contentFit: 'cover',
  },
  title: {
    fontSize: 16,
    fontWeight: 'medium',
    lineHeight: 24,
    lines: 1,
    alignment: 'start',
  },
  subtitle: {
    fontSize: 14,
    fontWeight: 'regular',
    lineHeight: 20,
    lines: 1,
    alignment: 'start',
  },
  price: {
    fontSize: 16,
    fontWeight: 'medium',
    lineHeight: 24,
    lines: 1,
    alignment: 'end',
  },
  change: {
    fontSize: 14,
    fontWeight: 'medium',
    lineHeight: 20,
    lines: 1,
    alignment: 'center',
  },
  changeWidth: 80,
  changeHeight: 32,
  changeCornerRadius: 8,
};

const STOCK_ROW_STYLE: MarketRowStyle = {
  ...TOKEN_ROW_STYLE,
  lineGap: 0,
  leadingGap: 14,
  image: {
    width: 40,
    height: 40,
    shape: 'circle',
    cornerRadius: 20,
    contentFit: 'cover',
  },
};

const PERP_ROW_STYLE: MarketRowStyle = {
  ...TOKEN_ROW_STYLE,
  horizontalPadding: 16,
  leadingGap: 8,
};

function smallPriceSegments(
  value: string | undefined,
): readonly ValueTextSegment[] | undefined {
  const amount = Number(value);
  if (!Number.isFinite(amount) || amount === 0 || Math.abs(amount) >= 0.0001) {
    return undefined;
  }
  const fraction = Math.abs(amount).toFixed(16).slice(2);
  const zeroCount = fraction.match(/^0+/)?.[0].length ?? 0;
  if (zeroCount < 4) return undefined;
  const significant =
    fraction.slice(zeroCount, zeroCount + 4).replace(/0+$/, '') || '0';
  return [
    { text: amount < 0 ? '-$0.0' : '$0.0' },
    { text: String(zeroCount), style: 'subscript' },
    { text: significant },
  ];
}

function changeTone(value: string | undefined) {
  const number = Number(value);
  if (!Number.isFinite(number) || number === 0) return 'neutral' as const;
  return number > 0 ? ('positive' as const) : ('negative' as const);
}

function badgesForAsset(
  asset: MarketAsset,
  locale: 'zh-CN' | 'en-US',
  theme: MarketThemeName,
): readonly MarketBadgeModel[] {
  const badges: MarketBadgeModel[] = [];
  if (asset.communityRecognized) {
    badges.push({
      key: `${asset.key}:community-verified`,
      iconName: 'verified',
      tone: 'success',
      actionKey: 'community-info',
      accessibilityLabel:
        locale === 'en-US'
          ? `${asset.symbol} community recognized`
          : `${asset.symbol} 社区认可`,
    });
  }
  if (asset.stock?.sourceLogoUri) {
    badges.push({
      key: `${asset.key}:stock-source`,
      icon: asset.stock.sourceLogoUri
        ? {
            uri: asset.stock.sourceLogoUri,
            width: 14,
            height: 14,
            contentFit: 'contain',
            cachePolicy: 'memory-disk',
            retryTimes: 2,
          }
        : undefined,
      tone: 'neutral',
      actionKey: 'stock-info',
      accessibilityLabel:
        asset.stock.title ||
        (locale === 'en-US' ? 'Stock provider information' : '股票来源信息'),
    });
  }
  if (asset.kind === 'perp' && asset.maxLeverage) {
    badges.push({
      key: `${asset.key}:leverage`,
      text: `${asset.maxLeverage}x`,
      tone: 'info',
      textColor: theme === 'dark' ? '#70B8FF' : '#006DCBF2',
      backgroundColor: theme === 'dark' ? '#0077FF3A' : '#008FF519',
      accessibilityLabel:
        locale === 'en-US'
          ? `Up to ${asset.maxLeverage}x leverage`
          : `最高 ${asset.maxLeverage} 倍杠杆`,
    });
  }
  if (
    asset.kind === 'perp' &&
    ['xyz', 'para', 'io'].includes(asset.dexLabel?.toLowerCase() ?? '')
  ) {
    badges.push({
      key: `${asset.key}:dex`,
      text: asset.dexLabel?.toLowerCase(),
      tone: 'info',
      textColor: theme === 'dark' ? '#70B8FF' : '#006DCBF2',
      backgroundColor: theme === 'dark' ? '#0077FF3A' : '#008FF519',
      actionKey: 'dex-info',
      accessibilityLabel:
        locale === 'en-US'
          ? `${asset.dexLabel} perps provider`
          : `${asset.dexLabel} 合约来源`,
    });
  }
  return badges;
}

function marketRow(
  asset: MarketAsset,
  watchlist: boolean,
  locale: 'zh-CN' | 'en-US',
  theme: MarketThemeName,
): MarketRow {
  const networkLogoUrl =
    asset.networkLogoUrl || MARKET_SOURCE_NETWORK_LOGOS[asset.networkId ?? ''];
  const subtitle =
    asset.kind === 'stock'
      ? asset.stock?.subtitle || asset.name
      : asset.subtitle || formatCompactUsd(asset.volume24h ?? asset.marketCap);
  const priceSegments = smallPriceSegments(asset.price);
  const tone = changeTone(asset.priceChange24hPercent);
  const rowStyle =
    asset.kind === 'stock'
      ? STOCK_ROW_STYLE
      : asset.kind === 'perp'
      ? PERP_ROW_STYLE
      : TOKEN_ROW_STYLE;
  return {
    key: asset.key,
    type: 'market',
    variant:
      asset.kind === 'stock'
        ? 'stock'
        : asset.kind === 'perp'
        ? 'perp'
        : 'token',
    height: 72,
    testID: `market-row-${asset.key}`,
    accessibilityLabel: `${asset.symbol}, ${formatPrice(
      asset.price,
    )}, ${formatChange(asset.priceChange24hPercent)}`,
    leading: {
      kind: 'token',
      image: asset.logoUrl
        ? {
            uri: asset.logoUrl,
            width: asset.kind === 'stock' ? 40 : 32,
            height: asset.kind === 'stock' ? 40 : 32,
            contentFit: 'cover',
            cachePolicy: 'memory-disk',
            retryTimes: 2,
          }
        : undefined,
      networkImage:
        asset.kind !== 'perp' && networkLogoUrl
          ? {
              uri: networkLogoUrl,
              width: 16,
              height: 16,
              contentFit: 'cover',
              cachePolicy: 'memory-disk',
              retryTimes: 2,
            }
          : undefined,
      fallbackIcon: { name: 'CryptoCoinOutline' },
      shape: 'circle',
      backgroundColor: theme === 'dark' ? '#FFFFFF2C' : '#FFFFFF',
      borderColor: theme === 'dark' ? '#FFFFFF09' : undefined,
    },
    title: asset.kind === 'perp' ? asset.name : asset.symbol,
    subtitle,
    price: priceSegments
      ? priceSegments.map(segment => segment.text).join('')
      : formatPrice(asset.price),
    priceSegments,
    change: {
      text: formatChange(asset.priceChange24hPercent),
      tone,
      textColor: '#FFFFFF',
      backgroundColor:
        tone === 'positive'
          ? theme === 'dark'
            ? '#44FFA49E'
            : '#008F4ACF'
          : tone === 'negative'
          ? theme === 'dark'
            ? '#FE4E54E4'
            : '#DB0007B7'
          : '#666666',
    },
    badges: badgesForAsset(asset, locale, theme),
    pressActionKey: 'open-detail-boundary',
    pressInActionKey: 'prewarm-detail-boundary',
    longPressActionKey: watchlist ? 'watchlist-menu' : undefined,
    diagnostics: { imageBindActionKey: 'image-bind' },
    style: {
      ...rowStyle,
      change: {
        ...rowStyle.change,
        fontSize:
          Math.abs(Number(asset.priceChange24hPercent)) >= 10_000 ? 13 : 14,
      },
    },
  };
}

export function buildMarketQuotePatches(
  previous: readonly MarketAsset[],
  next: readonly MarketAsset[],
  theme: MarketThemeName = 'dark',
): readonly RowPatch[] | undefined {
  if (
    previous.length !== next.length ||
    previous.some(
      (item, index) =>
        item.key !== next[index]?.key ||
        Math.abs(Number(item.priceChange24hPercent)) >= 10_000 !==
          Math.abs(Number(next[index]?.priceChange24hPercent)) >= 10_000,
    )
  ) {
    return undefined;
  }
  return next.flatMap((item, index): readonly RowPatch[] => {
    const before = previous[index];
    if (
      !before ||
      (before.price === item.price &&
        before.priceChange24hPercent === item.priceChange24hPercent)
    ) {
      return [];
    }
    const tone = changeTone(item.priceChange24hPercent);
    const priceSegments = smallPriceSegments(item.price);
    return [
      {
        type: 'market',
        key: item.key,
        changes: {
          price: priceSegments
            ? priceSegments.map(segment => segment.text).join('')
            : formatPrice(item.price),
          priceSegments,
          change: {
            text: formatChange(item.priceChange24hPercent),
            tone,
            textColor: '#FFFFFF',
            backgroundColor:
              tone === 'positive'
                ? theme === 'dark'
                  ? '#44FFA49E'
                  : '#008F4ACF'
                : tone === 'negative'
                ? theme === 'dark'
                  ? '#FE4E54E4'
                  : '#DB0007B7'
                : '#666666',
          },
        },
      },
    ];
  });
}

export function buildMarketSnapshot({
  items,
  generation,
  theme,
  watchlist,
  loading,
  loadingMore,
  error,
  canLoadMore,
  locale = 'zh-CN',
}: {
  items: readonly MarketAsset[];
  generation: number;
  theme: MarketThemeName;
  watchlist: boolean;
  loading: boolean;
  loadingMore: boolean;
  error?: string;
  canLoadMore: boolean;
  locale?: 'zh-CN' | 'en-US';
}): NativeListSnapshot {
  let rows: RowModel[] = items.map(item =>
    marketRow(item, watchlist, locale, theme),
  );
  if (loading && rows.length === 0) {
    rows = Array.from({ length: 10 }, (_, index) => ({
      key: `market-loading-${index}`,
      height: 56,
      type: 'system' as const,
      variant: 'loading' as const,
      presentation: 'market' as const,
      loadingStyle: 'skeleton' as const,
      accessibilityLabel:
        index === 0
          ? locale === 'en-US'
            ? 'Loading market data'
            : '正在加载线上行情'
          : undefined,
    }));
  } else if (error && rows.length === 0) {
    rows = [
      {
        key: 'market-retry',
        type: 'system',
        variant: 'retry',
        presentation: 'market',
        message: error,
        actionKey: 'retry',
      },
    ];
  } else if (loadingMore) {
    rows = [
      ...rows,
      {
        key: 'market-loading-more',
        height: 52,
        type: 'system',
        variant: 'loading',
        presentation: 'market',
        loadingStyle: 'spinner',
        accessibilityLabel: locale === 'en-US' ? 'Loading more…' : '加载更多…',
      },
    ];
  } else if (!canLoadMore && rows.length > 0) {
    rows = [
      ...rows,
      {
        key: 'market-end',
        height: 36,
        type: 'system',
        variant: 'end',
        presentation: 'market',
        message: locale === 'en-US' ? 'No more results' : '没有更多了',
      },
    ];
  }

  return {
    schemaVersion: 1,
    generation,
    theme: MARKET_NATIVE_THEMES[theme],
    layout: {
      kind: 'linear',
      contentPaddingTop: 0,
      contentPaddingBottom: 0,
      contentPaddingHorizontal: 0,
      itemSpacing: 0,
    },
    rows,
    selection: { mode: 'none', selectedKeys: [] },
    capabilities: {
      loadMore: canLoadMore,
      endReachedThreshold: 0.2,
      pullToRefresh: true,
      refreshing: loading && items.length > 0,
    },
    emptyState: {
      key: 'market-empty',
      height: 88,
      type: 'system',
      variant: 'noMatch',
      presentation: 'market',
      message: locale === 'en-US' ? 'No data' : '暂无数据',
    },
  };
}
