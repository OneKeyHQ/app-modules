import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import {
  FlatList,
  Image,
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  View,
  useWindowDimensions,
  type LayoutChangeEvent,
} from 'react-native';
import { useNavigation, type NavigationProp } from '@react-navigation/native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import {
  NativeList,
  type ActionAnchorInvalidatedEvent,
  type NativeListActionAnchor,
  type NativeListRef,
  type RowActionEvent,
  type VisibleRangeChangedEvent,
} from '@onekeyfe/react-native-native-list';
import {
  CollapsiblePagerView,
  type CollapsiblePagerDiagnostics,
  type CollapsiblePagerMountState,
  type CollapsiblePagerViewOnPageSelectedEvent,
} from '@onekeyfe/react-native-pager-view';

import {
  MarketApiError,
  defaultPerpsCategory,
  fetchMarketBanners,
  fetchMarketConfig,
  fetchMarketPage,
  fetchUSMarketStatus,
  getMarketRequestDiagnostics,
} from './marketNativePagerApi';
import {
  applyMarketScenario,
  buildMarketPages,
  categoriesForPage,
  filterMarketSearch,
  filterWatchlist,
  replayItemsForPage,
  isOndoUSMarketStock,
  resolveUSMarketStatusVariant,
  type IFetchUSMarketStatusResult,
} from './marketNativePagerData';
import {
  buildMarketQuotePatches,
  buildMarketSnapshot,
  type MarketThemeName,
} from './marketNativePagerRows';
import { MARKET_REPLAY_SNAPSHOT } from './marketNativePagerSnapshot';
import {
  MARKET_SOURCE_ICONS,
  MARKET_SOURCE_COPY,
  MARKET_SOURCE_NETWORK_LOGOS,
  MARKET_SOURCE_STATUS_CHIPS,
} from './marketNativePagerAssets';
import { MarketImage } from './marketNativePagerImage';

import type {
  MarketAsset,
  MarketBanner,
  MarketConfig,
  MarketDataMode,
  MarketPageDescriptor,
  MarketScenario,
  MarketTimeRange,
} from './marketNativePagerTypes';

type MarketLocale = 'zh-CN' | 'en-US';

type PageDiagnostics = Readonly<{
  requestCount: number;
  quotePatchCount: number;
  structuralUpdateCount: number;
  imageBindCount: number;
  visibleRange: string;
  sourceUrl: string;
  fetchedAt: string;
  note: string;
}>;

const EMPTY_PAGE_DIAGNOSTICS: PageDiagnostics = {
  requestCount: 0,
  quotePatchCount: 0,
  structuralUpdateCount: 0,
  imageBindCount: 0,
  visibleRange: '--',
  sourceUrl: '',
  fetchedAt: '',
  note: '',
};

const DEFAULT_WATCHLIST_KEYS = [
  'btc--0:',
  'evm--56:0x390a684ef9cade28a7ad0dfa61ab1eb3842618c4',
  'perps:BTC',
  'evm--4663:0x39dbed3a2bd333467115de45665cc57f813c4571',
] as const;
const FULL_WATCHLIST_KEYS = [
  ...new Set([
    ...DEFAULT_WATCHLIST_KEYS,
    ...Object.values(
      MARKET_REPLAY_SNAPSHOT.spotQueries ?? MARKET_REPLAY_SNAPSHOT.spot,
    )
      .flat()
      .map(item => item.key),
    ...Object.values(MARKET_REPLAY_SNAPSHOT.perps)
      .flat()
      .map(item => item.key),
  ]),
];

const COPY = {
  'zh-CN': {
    search: '搜索任何内容',
    market: '市场',
    defi: 'DeFi',
    browser: '浏览器',
    allNetworks: '全部网络',
    nameVolume: '名称 / 交易额',
    price: '价格',
    change: '涨跌',
    edit: '编辑自选',
    done: '完成',
    pageBoundary: '示例保留详情入口参数，但不进入钱包、交易或签名流程。',
    noMatch: '没有匹配行情',
    retry: '重试',
    pin: '置顶',
    remove: '移出自选',
    cancel: '取消',
    unavailable: '该接口暂不可用（404）',
  },
  'en-US': {
    search: 'Search anything',
    market: 'Market',
    defi: 'DeFi',
    browser: 'Browser',
    allNetworks: 'All networks',
    nameVolume: 'Name / Turnover',
    price: 'Price',
    change: 'Change',
    edit: 'Edit watchlist',
    done: 'Done',
    pageBoundary:
      'The example preserves detail parameters but does not enter wallet, trade, or signing flows.',
    noMatch: 'No matching quotes',
    retry: 'Retry',
    pin: 'Pin to top',
    remove: 'Remove from favorites',
    cancel: 'Cancel',
    unavailable: 'This endpoint is unavailable (404)',
  },
} as const;

function sameKeys(left: readonly MarketAsset[], right: readonly MarketAsset[]) {
  return (
    left.length === right.length &&
    left.every((item, index) => item.key === right[index]?.key)
  );
}

function mergeByKey(
  existing: readonly MarketAsset[],
  incoming: readonly MarketAsset[],
) {
  const seen = new Set(existing.map(item => item.key));
  return [...existing, ...incoming.filter(item => !seen.has(item.key))];
}

function MarketIcon({
  name,
  theme,
  size = 24,
  subdued = true,
}: {
  name: keyof typeof MARKET_SOURCE_ICONS;
  theme: MarketThemeName;
  size?: number;
  subdued?: boolean;
}) {
  const source = MARKET_SOURCE_ICONS[name][theme];
  return (
    <MarketImage
      source={typeof source === 'string' ? { uri: source } : source}
      style={{
        width: size,
        height: size,
        opacity:
          name.startsWith('status') ||
          ['noResults', 'alignTop', 'star', 'recognized'].includes(name)
            ? 1
            : !subdued
            ? (theme === 'dark' ? 237 : 223) / 255
            : name === 'search' || name === 'chevronDown'
            ? (theme === 'dark' ? 100 : 114) / 255
            : (theme === 'dark' ? 175 : 155) / 255,
      }}
    />
  );
}

function BannerCard({
  banner,
  theme,
  onPress,
}: {
  banner: MarketBanner;
  theme: MarketThemeName;
  onPress: () => void;
}) {
  // Theme tokens from app-monorepo's tamagui.config.ts and colors/primitive.
  const colors =
    theme === 'dark'
      ? {
          'bg/info-subdued': '#1166FB18',
          'bg/success-subdued': '#29F99D0B',
          'bg/caution-subdued': '#F9B4000B',
          'bg/critical-subdued': '#F22F3E11',
          'text/success': '#46FEA5D4',
          'text/critical': '#FF9592',
          'text/info': '#70B8FF',
          'text/caution': '#FEE949F5',
        }
      : {
          'bg/info-subdued': '#008CFF0B',
          'bg/success-subdued': '#00A32F0B',
          'bg/caution-subdued': '#F4DD0016',
          'bg/critical-subdued': '#FF000008',
          'text/success': '#00713FDE',
          'text/critical': '#C40006D3',
          'text/info': '#006DCBF2',
          'text/caution': '#9E6C00',
        };
  const background =
    colors[banner.backgroundColor as keyof typeof colors] ??
    (theme === 'dark' ? '#191919' : '#F9F9F9');

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`${banner.title} ${banner.description?.text ?? ''}`}
      testID={`market-banner-${banner.id}`}
      onPress={onPress}
      style={({ pressed }) => [
        styles.bannerCard,
        {
          backgroundColor: background,
          borderColor: theme === 'dark' ? '#FFFFFF12' : '#0000000F',
        },
        pressed && styles.pressed,
      ]}
    >
      <View style={{ flex: 1 }}>
        <View style={styles.bannerTitleRow}>
          <Text
            numberOfLines={2}
            style={[
              styles.bannerTitle,
              theme === 'dark' ? styles.textDark : styles.textLight,
            ]}
          >
            {banner.title}
          </Text>
          {banner.type === 'perps' ? (
            <Text
              style={[
                styles.leverageBadge,
                {
                  backgroundColor: theme === 'dark' ? '#0077FF3A' : '#008FF519',
                  color: colors['text/info'],
                },
              ]}
            >
              10x
            </Text>
          ) : null}
        </View>
        {banner.description ? (
          <Text
            style={[
              styles.bannerChange,
              {
                color:
                  colors[banner.description.fontColor as keyof typeof colors] ??
                  (theme === 'dark' ? '#FFFFFFAF' : '#0000009B'),
              },
            ]}
          >
            {banner.description.text}
          </Text>
        ) : null}
      </View>
      <View style={styles.bannerLogos}>
        {banner.tokenLogos.slice(0, 3).map((uri, index) => (
          <View
            key={`${uri}:${index}`}
            style={[
              styles.bannerLogo,
              {
                borderColor: theme === 'dark' ? '#FFFFFF12' : '#0000000F',
                backgroundColor: theme === 'dark' ? '#FFFFFF12' : '#0000000F',
              },
              index > 0 && styles.bannerLogoOverlap,
            ]}
          >
            <Image source={{ uri }} style={styles.bannerLogoImage} />
          </View>
        ))}
      </View>
    </Pressable>
  );
}

function MarketHeader({
  banners,
  theme,
  tall,
  onBannerPress,
  onLayout,
}: {
  banners: readonly MarketBanner[];
  theme: MarketThemeName;
  tall: boolean;
  onBannerPress: (banner: MarketBanner) => void;
  onLayout: (event: LayoutChangeEvent) => void;
}) {
  const dark = theme === 'dark';
  return (
    <View
      onLayout={onLayout}
      style={[
        styles.header,
        dark ? styles.backgroundDark : styles.backgroundLight,
      ]}
      testID="market-collapsible-header"
    >
      <ScrollView
        horizontal
        nestedScrollEnabled
        bounces={false}
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={[
          styles.bannerList,
          Platform.OS === 'web' && { paddingTop: 16 },
        ]}
        testID="market-banner-list"
      >
        {banners.map(banner => (
          <BannerCard
            key={banner.id}
            banner={banner}
            theme={theme}
            onPress={() => onBannerPress(banner)}
          />
        ))}
      </ScrollView>
      {tall ? (
        <View
          style={[
            styles.dynamicHeaderProbe,
            dark ? styles.darkPill : styles.lightPill,
          ]}
          testID="market-dynamic-header-probe"
        >
          <Text
            style={[
              styles.dynamicHeaderProbeText,
              dark ? styles.textDark : styles.textLight,
            ]}
          >
            Dynamic header height probe · 64
          </Text>
        </View>
      ) : null}
    </View>
  );
}

function FilterChip({
  label,
  selected,
  theme,
  testID,
  onPress,
}: {
  label: string;
  selected: boolean;
  theme: MarketThemeName;
  testID: string;
  onPress: () => void;
}) {
  const dark = theme === 'dark';
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityState={{ selected }}
      testID={testID}
      onPress={onPress}
      style={({ pressed }) => [
        styles.filterChip,
        selected && (dark ? styles.darkChipSelected : styles.lightChipSelected),
        pressed && styles.pressed,
      ]}
    >
      <Text
        style={[
          styles.filterChipText,
          dark ? styles.subduedTextDark : styles.subduedTextLight,
          selected && (dark ? styles.textDark : styles.textLight),
        ]}
      >
        {label}
      </Text>
    </Pressable>
  );
}

function MarketStickyHeader({
  pages,
  activeIndex,
  activePage,
  config,
  theme,
  locale,
  networkId,
  timeRange,
  watchlistFilter,
  stockCategory,
  perpsCategory,
  watchlistEmpty,
  onSelectPage,
  onNetworkChange,
  onTimeRangeChange,
  onWatchlistFilterChange,
  onStockCategoryChange,
  onPerpsCategoryChange,
  onToggleWatchlistEdit,
  onToggleDiagnostics,
  onLayout,
}: {
  pages: readonly MarketPageDescriptor[];
  activeIndex: number;
  activePage: MarketPageDescriptor;
  config: MarketConfig;
  theme: MarketThemeName;
  locale: MarketLocale;
  networkId: string;
  timeRange: MarketTimeRange;
  watchlistFilter: string;
  stockCategory: string;
  perpsCategory: string;
  watchlistEmpty: boolean;
  onSelectPage: (index: number) => void;
  onNetworkChange: (value: string) => void;
  onTimeRangeChange: (value: MarketTimeRange) => void;
  onWatchlistFilterChange: (value: string) => void;
  onStockCategoryChange: (value: string) => void;
  onPerpsCategoryChange: (value: string) => void;
  onToggleWatchlistEdit: () => void;
  onToggleDiagnostics: () => void;
  onLayout: (event: LayoutChangeEvent) => void;
}) {
  const dark = theme === 'dark';
  const copy = COPY[locale];
  const categoryFilters = categoriesForPage(activePage, config, locale);
  const [filterMenu, setFilterMenu] = useState<'network' | 'time'>();
  const [networkSearch, setNetworkSearch] = useState('');
  const [networkSearchFocused, setNetworkSearchFocused] = useState(false);
  const tabsRef = useRef<ScrollView>(null);
  const tabsWidthRef = useRef(0);
  const tabLayoutsRef = useRef(new Map<number, { x: number; width: number }>());
  const keepActiveTabVisible = useCallback(() => {
    const layout = tabLayoutsRef.current.get(activeIndex);
    if (!layout || !tabsWidthRef.current) return;
    const position =
      activeIndex === 0 ? 0 : activeIndex === pages.length - 1 ? 1 : 0.5;
    tabsRef.current?.scrollTo({
      x:
        Platform.OS === 'web'
          ? Math.max(0, layout.x + layout.width - tabsWidthRef.current)
          : activeIndex === 0
          ? 0
          : Math.max(
              0,
              // Source FlashList cells include a 20-point leading margin;
              // its scroll viewport excludes the 16-point trailing padding.
              layout.x -
                20 -
                (tabsWidthRef.current - 16 - layout.width - 20) * position,
            ),
      animated: true,
    });
  }, [activeIndex, pages.length]);
  useEffect(keepActiveTabVisible, [keepActiveTabVisible]);
  const { bottom } = useSafeAreaInsets();
  useEffect(() => {
    setNetworkSearch('');
    setNetworkSearchFocused(false);
  }, [filterMenu]);
  const networkOptions = [
    { id: '', name: copy.allNetworks, logoUrl: '' },
    ...config.networks.map(network => ({
      id: network.networkId,
      name: network.name,
      logoUrl: network.logoUrl,
    })),
  ].filter(network =>
    `${network.name} ${network.id}`
      .toLowerCase()
      .includes(networkSearch.trim().toLowerCase()),
  );
  const selectedCategory =
    activePage.kind === 'watchlist'
      ? watchlistFilter
      : activePage.kind === 'perps'
      ? perpsCategory
      : stockCategory;
  const selectCategory =
    activePage.kind === 'watchlist'
      ? onWatchlistFilterChange
      : activePage.kind === 'perps'
      ? onPerpsCategoryChange
      : onStockCategoryChange;

  return (
    <View
      onLayout={onLayout}
      style={[dark ? styles.backgroundDark : styles.backgroundLight]}
      testID="market-sticky-header"
    >
      <Modal
        transparent
        visible={Boolean(filterMenu)}
        animationType="fade"
        onRequestClose={() => setFilterMenu(undefined)}
      >
        <KeyboardAvoidingView
          style={[
            styles.filterBackdrop,
            !dark && { backgroundColor: '#00000044' },
          ]}
          behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        >
          <Pressable
            testID="market-filter-dismiss"
            style={StyleSheet.absoluteFill}
            onPress={() => setFilterMenu(undefined)}
          />
          <View
            style={[
              styles.filterMenu,
              {
                backgroundColor: dark ? '#1B1B1B' : '#FFFFFF',
                marginBottom: bottom || 20,
              },
            ]}
          >
            {filterMenu === 'network' ? (
              <>
                <View style={styles.filterMenuHeader}>
                  <Text
                    style={[
                      styles.filterMenuTitle,
                      { flex: 1, marginRight: 12 },
                      dark ? styles.textDark : styles.textLight,
                    ]}
                  >
                    {locale === 'en-US' ? 'Select network' : '选择网络'}
                  </Text>
                  <Pressable
                    accessibilityRole="button"
                    accessibilityLabel={copy.cancel}
                    testID="market-filter-close"
                    onPress={() => setFilterMenu(undefined)}
                    style={[
                      styles.filterClose,
                      { backgroundColor: dark ? '#FFFFFF12' : '#0000000F' },
                    ]}
                  >
                    <MarketIcon name="close" theme={theme} size={20} />
                  </Pressable>
                </View>
                <View
                  style={[
                    styles.networkSearch,
                    { backgroundColor: dark ? '#FFFFFF12' : '#0000000F' },
                  ]}
                >
                  <MarketIcon name="search" theme={theme} size={20} />
                  <TextInput
                    testID="market-network-search"
                    value={networkSearch}
                    onChangeText={setNetworkSearch}
                    placeholder={locale === 'en-US' ? 'Search' : '搜索'}
                    placeholderTextColor={dark ? '#FFFFFF64' : '#00000072'}
                    onFocus={() => setNetworkSearchFocused(true)}
                    onBlur={() => setNetworkSearchFocused(false)}
                    autoCorrect={false}
                    autoCapitalize="none"
                    style={[
                      styles.searchInput,
                      { height: 36 },
                      dark ? styles.textDark : styles.textLight,
                    ]}
                  />
                  {networkSearch ? (
                    <Pressable
                      accessibilityRole="button"
                      accessibilityLabel={
                        locale === 'en-US' ? 'Clear search' : '清空搜索'
                      }
                      testID="market-network-search-clear"
                      onPress={() => setNetworkSearch('')}
                    >
                      <MarketIcon name="close" theme={theme} size={20} />
                    </Pressable>
                  ) : null}
                </View>
              </>
            ) : null}
            <ScrollView
              keyboardShouldPersistTaps="handled"
              style={
                filterMenu === 'network'
                  ? {
                      height:
                        networkSearchFocused && Platform.OS !== 'web'
                          ? 262
                          : 362,
                      flexGrow: 0,
                    }
                  : undefined
              }
              contentContainerStyle={
                filterMenu === 'network'
                  ? { paddingBottom: bottom || 8 }
                  : styles.timeOptions
              }
            >
              {(filterMenu === 'network'
                ? networkOptions
                : ['5m', '1h', '4h', '24h'].map(value => ({
                    id: value,
                    name: value,
                    logoUrl: '',
                  }))
              ).map(option => {
                const selected =
                  option.id ===
                  (filterMenu === 'network' ? networkId : timeRange);
                return (
                  <Pressable
                    key={option.id}
                    testID={`market-filter-option-${option.id || 'all'}`}
                    accessibilityRole="button"
                    accessibilityState={{ selected }}
                    onPress={() => {
                      if (filterMenu === 'network') onNetworkChange(option.id);
                      else onTimeRangeChange(option.id as MarketTimeRange);
                      setFilterMenu(undefined);
                    }}
                    style={[
                      filterMenu === 'network'
                        ? styles.filterOption
                        : styles.timeOption,
                      filterMenu === 'time' &&
                        selected &&
                        (dark
                          ? styles.darkChipSelected
                          : styles.lightChipSelected),
                    ]}
                  >
                    {filterMenu === 'network' ? (
                      option.logoUrl ? (
                        <MarketImage
                          source={{ uri: option.logoUrl }}
                          style={{
                            width: 32,
                            height: 32,
                            borderRadius: 16,
                          }}
                        />
                      ) : (
                        <MarketIcon
                          name="allNetworks"
                          theme={theme}
                          size={32}
                          subdued={false}
                        />
                      )
                    ) : null}
                    <Text
                      style={[
                        filterMenu === 'network'
                          ? styles.networkOptionText
                          : styles.filterChipText,
                        dark ? styles.textDark : styles.textLight,
                        filterMenu === 'time' &&
                          !selected &&
                          (dark
                            ? styles.subduedTextDark
                            : styles.subduedTextLight),
                      ]}
                    >
                      {option.name}
                    </Text>
                    {filterMenu === 'network' && selected ? (
                      <MarketIcon
                        name="checkRadio"
                        theme={theme}
                        size={24}
                        subdued={false}
                      />
                    ) : null}
                  </Pressable>
                );
              })}
              {filterMenu === 'network' && networkOptions.length === 0 ? (
                <View style={styles.networkEmpty}>
                  <MarketIcon
                    name="noResults"
                    theme={theme}
                    size={144}
                    subdued={false}
                  />
                  <Text
                    style={[
                      styles.filterMenuTitle,
                      { marginTop: 8, marginBottom: 8 },
                      dark ? styles.textDark : styles.textLight,
                    ]}
                  >
                    {locale === 'en-US' ? 'No results' : '无结果'}
                  </Text>
                </View>
              ) : null}
            </ScrollView>
          </View>
        </KeyboardAvoidingView>
      </Modal>
      <ScrollView
        ref={tabsRef}
        horizontal
        nestedScrollEnabled
        bounces={false}
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={[
          styles.marketTabs,
          Platform.OS === 'web' && { paddingRight: 0 },
        ]}
        onLayout={event => {
          tabsWidthRef.current = event.nativeEvent.layout.width;
          keepActiveTabVisible();
        }}
        onContentSizeChange={keepActiveTabVisible}
        testID="market-category-tabs"
      >
        {pages.map((page, index) => (
          <Pressable
            key={page.key}
            accessibilityRole="tab"
            accessibilityState={{ selected: index === activeIndex }}
            testID={`market-tab-${page.key}`}
            onPress={() => onSelectPage(index)}
            onLongPress={onToggleDiagnostics}
            style={styles.marketTabButton}
            onLayout={event => {
              tabLayoutsRef.current.set(index, event.nativeEvent.layout);
              keepActiveTabVisible();
            }}
          >
            <Text
              style={[
                styles.marketTabText,
                dark ? styles.subduedTextDark : styles.subduedTextLight,
                index === activeIndex &&
                  (dark ? styles.textDark : styles.textLight),
              ]}
            >
              {page.name}
            </Text>
            {index === activeIndex ? (
              <View
                style={[
                  styles.activeTabLine,
                  dark ? styles.lineDark : styles.lineLight,
                ]}
              />
            ) : null}
          </Pressable>
        ))}
      </ScrollView>
      {Platform.OS !== 'web' ? (
        <View
          pointerEvents="none"
          style={{
            position: 'absolute',
            right: 0,
            top: 0,
            width: 16,
            height: 44,
            backgroundColor: dark ? '#0F0F0F' : '#FFFFFF',
          }}
        />
      ) : null}
      <View
        style={{
          ...(Platform.OS === 'ios'
            ? { position: 'absolute' as const, top: 44, left: 0, right: 0 }
            : {}),
          height: StyleSheet.hairlineWidth,
          backgroundColor: dark ? '#FFFFFF22' : '#0000001F',
          transform: [{ translateY: -StyleSheet.hairlineWidth / 2 }],
        }}
      />

      <View
        style={[
          styles.secondaryFilters,
          activePage.kind === 'spot' &&
            activePage.categoryId !== 'stocks' && {
              paddingTop: 14,
              paddingBottom: 8,
            },
          (watchlistEmpty || activePage.kind === 'topCoins') && {
            display: 'none',
          },
        ]}
        testID="market-secondary-filters"
      >
        {activePage.kind === 'spot' && activePage.categoryId !== 'stocks' ? (
          <>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel={
                networkId
                  ? config.networks.find(item => item.networkId === networkId)
                      ?.name
                  : copy.allNetworks
              }
              style={styles.compactDropdown}
              testID="market-network-filter"
              onPress={() => setFilterMenu('network')}
            >
              {networkId &&
              config.networks.find(item => item.networkId === networkId)
                ?.logoUrl ? (
                <MarketImage
                  source={{
                    uri: config.networks.find(
                      item => item.networkId === networkId,
                    )!.logoUrl,
                  }}
                  style={{ width: 18, height: 18, borderRadius: 9 }}
                />
              ) : (
                <MarketIcon
                  name="allNetworks"
                  theme={theme}
                  size={18}
                  subdued={false}
                />
              )}
              <Text
                style={[
                  styles.filterChipText,
                  dark ? styles.textDark : styles.textLight,
                ]}
              >
                {networkId
                  ? config.networks.find(item => item.networkId === networkId)
                      ?.name || networkId
                  : locale === 'en-US'
                  ? 'All'
                  : '全部'}
              </Text>
              <MarketIcon name="chevronDown" theme={theme} size={18} />
            </Pressable>
            <View style={{ flex: 1 }} />
            <Pressable
              accessibilityRole="button"
              style={styles.compactDropdown}
              testID="market-time-filter"
              onPress={() => setFilterMenu('time')}
            >
              <Text
                style={[
                  styles.filterChipText,
                  dark ? styles.textDark : styles.textLight,
                ]}
              >
                {timeRange}
              </Text>
              <MarketIcon name="chevronDown" theme={theme} size={18} />
            </Pressable>
          </>
        ) : (
          <ScrollView
            horizontal
            nestedScrollEnabled
            showsHorizontalScrollIndicator={false}
            style={styles.categoryFilterScroll}
            contentContainerStyle={styles.categoryFilterList}
          >
            {categoryFilters.map(category => (
              <FilterChip
                key={category.id}
                label={category.name}
                selected={selectedCategory === category.id}
                theme={theme}
                testID={`market-sub-filter-${category.id}`}
                onPress={() => selectCategory(category.id)}
              />
            ))}
          </ScrollView>
        )}
        {activePage.kind === 'watchlist' ? (
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={copy.edit}
            onPress={onToggleWatchlistEdit}
            testID="market-watchlist-edit"
            style={styles.editButton}
          >
            <MarketIcon name="pencil" theme={theme} size={20} />
          </Pressable>
        ) : null}
      </View>

      <View
        style={[styles.columnHeader, watchlistEmpty && { display: 'none' }]}
        testID="market-column-header"
      >
        <Text
          style={[
            styles.columnTitle,
            dark ? styles.subduedTextDark : styles.subduedTextLight,
          ]}
        >
          {copy.nameVolume}
        </Text>
        <Text
          style={[
            styles.columnPrice,
            dark ? styles.subduedTextDark : styles.subduedTextLight,
          ]}
        >
          {copy.price}
        </Text>
        <Text
          style={[
            styles.columnChange,
            dark ? styles.subduedTextDark : styles.subduedTextLight,
          ]}
        >
          {copy.change}
        </Text>
      </View>
    </View>
  );
}

function AnchoredActionCard({
  open,
  anchor,
  locale,
  isFirstItem,
  onPin,
  onRemove,
  onClose,
}: {
  open: boolean;
  anchor?: NativeListActionAnchor;
  locale: MarketLocale;
  isFirstItem: boolean;
  onPin: () => void;
  onRemove: () => void;
  onClose: () => void;
}) {
  const { width, height } = useWindowDimensions();
  const openedAt = useRef(Date.now());
  const [visible, setVisible] = useState(false);
  useEffect(() => {
    if (!open) {
      setVisible(false);
      return;
    }
    openedAt.current = Date.now();
    const timer = setTimeout(() => setVisible(true), 500);
    return () => clearTimeout(timer);
  }, [open]);
  const top = Math.max(
    16,
    Math.min((anchor?.windowRect.y ?? height * 0.45) - 52, height - 60),
  );
  const left = Math.max(16, Math.min(width * 0.48 - 47, width - 110));
  return (
    <Modal
      transparent
      visible={open}
      onRequestClose={onClose}
      statusBarTranslucent
    >
      <View style={{ flex: 1 }}>
        <Pressable
          testID="market-watchlist-menu-close"
          accessibilityLabel={COPY[locale].cancel}
          style={StyleSheet.absoluteFill}
          onPress={() => {
            if (Date.now() - openedAt.current >= 350) onClose();
          }}
        />
        <View
          testID="market-watchlist-menu"
          style={{
            position: 'absolute',
            top,
            left,
            width: 94,
            height: 44,
            borderRadius: 12,
            backgroundColor: '#6B6C6F',
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'center',
            opacity: visible ? 1 : 0,
          }}
        >
          <Pressable
            testID="market-watchlist-pin"
            accessibilityRole="button"
            accessibilityLabel={COPY[locale].pin}
            disabled={isFirstItem}
            onPress={onPin}
            style={{
              width: 46,
              height: 44,
              alignItems: 'center',
              justifyContent: 'center',
              opacity: isFirstItem ? 0.3 : 1,
            }}
          >
            <MarketIcon name="alignTop" theme="dark" size={20} />
          </Pressable>
          <View
            style={{
              height: 24,
              width: StyleSheet.hairlineWidth,
              backgroundColor: 'rgba(255,255,255,0.18)',
            }}
          />
          <Pressable
            testID="market-watchlist-remove"
            accessibilityRole="button"
            accessibilityLabel={COPY[locale].remove}
            onPress={onRemove}
            style={{
              width: 46,
              height: 44,
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <MarketIcon name="star" theme="dark" size={20} />
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}

function MarketBadgeInfo({
  asset,
  action,
  theme,
  locale,
  onClose,
}: {
  asset?: MarketAsset;
  action?: string;
  theme: MarketThemeName;
  locale: MarketLocale;
  onClose: () => void;
}) {
  const dark = theme === 'dark';
  const { bottom } = useSafeAreaInsets();
  const copy = MARKET_SOURCE_COPY[locale];
  const dex = asset?.dexLabel?.toLowerCase();
  const dexDescription =
    dex === 'xyz' || dex === 'para' || dex === 'io' ? copy[dex] : undefined;
  const stock =
    (asset &&
      MARKET_REPLAY_SNAPSHOT.locales?.[locale]?.stockMetadata?.[asset.key]) ??
    asset?.stock;
  const [marketStatus, setMarketStatus] =
    useState<IFetchUSMarketStatusResult>();
  useEffect(() => {
    setMarketStatus(undefined);
    if (
      !asset ||
      action !== 'stock-info' ||
      !isOndoUSMarketStock(stock?.source) ||
      stock?.isOpen !== true
    )
      return;
    const controller = new AbortController();
    const update = async () => {
      const status = await fetchUSMarketStatus(controller.signal);
      if (!controller.signal.aborted) setMarketStatus(status);
    };
    void update();
    const timer = setInterval(update, 60_000);
    return () => {
      controller.abort();
      clearInterval(timer);
    };
  }, [asset?.key, action, stock?.source, stock?.isOpen]);
  const statusVariant = useMemo(
    () => resolveUSMarketStatusVariant({ ...stock, status: marketStatus }),
    [stock, marketStatus],
  );
  const statusChip = statusVariant
    ? MARKET_SOURCE_STATUS_CHIPS[statusVariant]
    : undefined;
  const body = {
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500' as const,
    fontSize: 16,
    lineHeight: 24,
    letterSpacing: 0,
    fontVariant: (Platform.OS === 'web'
      ? []
      : ['tabular-nums']) as Array<'tabular-nums'>,
  };
  const regularBody = {
    ...body,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Regular',
    fontWeight: '400' as const,
  };
  return (
    <Modal
      transparent
      visible={Boolean(asset)}
      animationType="fade"
      onRequestClose={onClose}
    >
      <View
        style={[
          styles.filterBackdrop,
          !dark && { backgroundColor: '#00000044' },
        ]}
      >
        <Pressable
          testID="market-badge-dismiss"
          style={StyleSheet.absoluteFill}
          onPress={onClose}
        />
        <View
          testID="market-badge-info"
          style={[
            styles.filterMenu,
            {
              backgroundColor: dark ? '#1B1B1B' : '#FFFFFF',
              marginBottom: bottom || 20,
              borderCurve: 'continuous',
            },
          ]}
        >
          <View style={[styles.filterMenuHeader, { alignItems: 'flex-start' }]}>
            {action === 'community-info' ? (
              <MarketIcon name="recognized" theme={theme} size={32} />
            ) : (
              <Text
                style={[
                  styles.filterMenuTitle,
                  dark ? styles.textDark : styles.textLight,
                ]}
              >
                {action === 'dex-info' ? dex : copy.tag}
              </Text>
            )}
            <Pressable
              testID="market-badge-close"
              accessibilityLabel={COPY[locale].cancel}
              onPress={onClose}
              style={[
                styles.filterClose,
                { backgroundColor: dark ? '#FFFFFF12' : '#0000000F' },
              ]}
            >
              <MarketIcon name="close" theme={theme} size={20} />
            </Pressable>
          </View>
          <View
            style={{
              paddingHorizontal: 20,
              paddingTop:
                action === 'community-info'
                  ? 16
                  : action === 'stock-info'
                  ? 4
                  : 0,
              paddingBottom: action === 'stock-info' ? 20 : 16,
              marginTop: -2,
              gap: 16,
            }}
          >
            {action === 'community-info' ? (
              <Text style={[body, dark ? styles.textDark : styles.textLight]}>
                {copy.community}
              </Text>
            ) : null}
            {action === 'dex-info' ? (
              <Text
                style={[regularBody, dark ? styles.textDark : styles.textLight]}
              >
                {dexDescription}
              </Text>
            ) : null}
            {action === 'stock-info' ? (
              <>
                {asset?.communityRecognized ? (
                  <View
                    style={{
                      flexDirection: 'row',
                      gap: 12,
                      alignItems: 'center',
                    }}
                  >
                    <MarketIcon name="recognized" theme={theme} size={24} />
                    <Text
                      style={[
                        { ...body, flex: 1 },
                        dark ? styles.textDark : styles.textLight,
                      ]}
                    >
                      {copy.community}
                    </Text>
                  </View>
                ) : null}
                {stock?.sourceLogoUri ? (
                  <View
                    style={{
                      flexDirection: 'row',
                      gap: 12,
                      alignItems: 'center',
                    }}
                  >
                    <View
                      style={{
                        width: 24,
                        height: 24,
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <Image
                        source={{ uri: stock.sourceLogoUri }}
                        style={{ width: 20, height: 20, borderRadius: 10 }}
                      />
                    </View>
                    <Text
                      style={[
                        { ...body, flex: 1 },
                        dark ? styles.textDark : styles.textLight,
                      ]}
                    >
                      {stock?.source === 'ondo'
                        ? copy.ondo
                        : stock?.source === 'xstock'
                        ? copy.xstock
                        : stock?.title}
                    </Text>
                  </View>
                ) : null}
                {stock ? (
                  <View style={{ gap: 8 }}>
                    {statusChip && statusVariant ? (
                      <View
                        style={{
                          alignSelf: 'flex-start',
                          flexDirection: 'row',
                          alignItems: 'center',
                          justifyContent: 'center',
                          gap: 3,
                          paddingHorizontal: 4,
                          borderRadius: 4,
                          backgroundColor: statusChip[theme].backgroundColor,
                        }}
                        testID="market-stock-status"
                      >
                        <MarketIcon
                          name={`status${statusVariant}`}
                          theme={theme}
                          size={12}
                        />
                        <Text
                          style={{
                            ...regularBody,
                            fontSize: 10,
                            lineHeight: 16,
                            color: statusChip[theme].color,
                          }}
                        >
                          {statusChip.text[locale]}
                        </Text>
                      </View>
                    ) : null}
                    {stock?.description ? (
                      <Text
                        style={[
                          { ...regularBody, fontSize: 14, lineHeight: 20 },
                          dark
                            ? styles.subduedTextDark
                            : styles.subduedTextLight,
                        ]}
                      >
                        {stock.description}
                      </Text>
                    ) : null}
                  </View>
                ) : null}
              </>
            ) : null}
          </View>
        </View>
      </View>
    </Modal>
  );
}

function MarketFavoritesEmpty({
  config,
  theme,
  locale,
  onAdd,
}: {
  config: MarketConfig;
  theme: MarketThemeName;
  locale: MarketLocale;
  onAdd: (keys: readonly string[]) => void;
}) {
  const tokens = useMemo(
    () => (config.recommendTokens ?? []).slice(0, 8),
    [config.recommendTokens],
  );
  const [selected, setSelected] = useState(() => tokens.map(item => item.key));
  const [badge, setBadge] = useState<MarketAsset>();
  const { height } = useWindowDimensions();
  const dark = theme === 'dark';
  useEffect(() => setSelected(tokens.map(item => item.key)), [tokens]);
  return (
    <View
      testID="market-favorites-empty"
      style={{
        paddingHorizontal: 20,
        paddingTop:
          Platform.OS === 'web' ? Math.max(16, (height - 800) * 0.5) : 16,
        // Match the narrow Desktop reference's empty-watchlist offset.
        transform: Platform.OS === 'web' ? [{ translateY: -58 }] : undefined,
        paddingBottom: 8,
        gap: 8,
      }}
    >
      {Array.from({ length: Math.ceil(tokens.length / 2) }, (_, index) => (
        <View key={index} style={{ flexDirection: 'row', gap: 8 }}>
          {tokens.slice(index * 2, index * 2 + 2).map(item => (
            <Pressable
              key={item.key}
              testID={`market-recommend-${item.symbol}`}
              accessibilityRole="checkbox"
              accessibilityState={{ checked: selected.includes(item.key) }}
              accessibilityLabel={item.symbol}
              onPress={() =>
                setSelected(keys =>
                  keys.includes(item.key)
                    ? keys.filter(key => key !== item.key)
                    : [...keys, item.key],
                )
              }
              style={{
                flex: 1,
                flexDirection: 'row',
                gap: 12,
                paddingHorizontal: 10,
                paddingVertical: 6,
                borderRadius: 12,
                borderWidth: 1,
                borderColor: dark ? '#FFFFFF12' : '#0000000F',
                backgroundColor: dark ? '#FFFFFF12' : '#0000000F',
                alignItems: 'center',
                minHeight: 50,
              }}
            >
              <View>
                <View
                  style={{
                    width: 32,
                    height: 32,
                    borderRadius: 16,
                    backgroundColor: dark ? '#FFFFFF2C' : '#FFFFFF',
                    overflow: 'hidden',
                  }}
                >
                  <Image
                    source={{ uri: item.logoUrl }}
                    style={[
                      StyleSheet.absoluteFill,
                      {
                        borderRadius: 16,
                        borderWidth: dark ? 1 : 0,
                        borderColor: '#FFFFFF09',
                      },
                    ]}
                  />

                </View>
                {item.networkLogoUrl ||
                MARKET_SOURCE_NETWORK_LOGOS[item.networkId ?? ''] ? (
                  <View
                    style={{
                      padding: 2,
                      borderRadius: 10,
                      backgroundColor: dark ? '#0F0F0F' : '#FFFFFF',
                      position: 'absolute',
                      right: -4,
                      bottom: -4,
                    }}
                  >
                    <Image
                      source={{
                        uri:
                          item.networkLogoUrl ||
                          MARKET_SOURCE_NETWORK_LOGOS[item.networkId ?? ''],
                      }}
                      style={{ width: 16, height: 16, borderRadius: 8 }}
                    />
                  </View>
                ) : null}
              </View>
              <View style={{ flex: 1, minWidth: 0 }}>
                <View
                  style={{ flexDirection: 'row', alignItems: 'center', gap: 4 }}
                >
                  <Text
                    numberOfLines={1}
                    style={[
                      {
                        fontSize: 14,
                        lineHeight: 20,
                        fontFamily:
                          Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
                        fontWeight: '500',
                        letterSpacing: 0,
                        fontVariant: ['tabular-nums'],
                        flexShrink: 1,
                      },
                      dark ? styles.textDark : styles.textLight,
                    ]}
                  >
                    {item.symbol}
                  </Text>
                  {item.communityRecognized ? (
                    <Pressable
                      onPress={event => {
                        event.stopPropagation();
                        setBadge(item);
                      }}
                    >
                      <MarketIcon name="recognized" theme={theme} size={16} />
                    </Pressable>
                  ) : null}
                </View>
                <Text
                  numberOfLines={1}
                  style={[
                    {
                      fontSize: 12,
                      lineHeight: 16,
                      maxWidth: 70,
                      fontFamily:
                        Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Regular',
                      fontWeight: '400',
                      letterSpacing: 0,
                      fontVariant: ['tabular-nums'],
                    },
                    dark ? styles.subduedTextDark : styles.subduedTextLight,
                  ]}
                >
                  {item.name}
                </Text>
              </View>
              {selected.includes(item.key) ? (
                <MarketIcon
                  name="checkRadio"
                  theme={theme}
                  size={20}
                  subdued={false}
                />
              ) : (
                <View style={{ width: 20 }} />
              )}
            </Pressable>
          ))}
        </View>
      ))}
      <Pressable
        testID="market-confirm-button-btn"
        accessibilityRole="button"
        disabled={!selected.length}
        onPress={() => onAdd(selected)}
        style={{
          marginTop: 24,
          height: 50,
          borderRadius: 25,
          borderCurve: 'continuous',
          alignItems: 'center',
          justifyContent: 'center',
          opacity: selected.length ? 1 : 0.4,
          backgroundColor: dark ? '#FFFFFFED' : '#000000DF',
        }}
      >
        <Text
          style={{
            color: dark ? '#000000DF' : '#FFFFFFED',
            fontSize: 16,
            lineHeight: 24,
            fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
            fontWeight: '500',
            letterSpacing: 0,
            fontVariant: Platform.OS === 'web' ? [] : ['tabular-nums'],
          }}
        >
          {MARKET_SOURCE_COPY[locale].addTokens.replace(
            '{number}',
            String(selected.length),
          )}
        </Text>
      </Pressable>
      <MarketBadgeInfo
        asset={badge}
        action="community-info"
        theme={theme}
        locale={locale}
        onClose={() => setBadge(undefined)}
      />
    </View>
  );
}

function MarketFavoritesDialog({
  visible,
  items,
  theme,
  locale,
  onClose,
}: {
  visible: boolean;
  items: readonly MarketAsset[];
  theme: MarketThemeName;
  locale: MarketLocale;
  onClose: () => void;
}) {
  const dark = theme === 'dark';
  const { bottom } = useSafeAreaInsets();
  return (
    <Modal
      transparent
      visible={visible}
      animationType="slide"
      onRequestClose={onClose}
    >
      <View
        style={[
          styles.filterBackdrop,
          !dark && { backgroundColor: '#00000044' },
        ]}
      >
        <Pressable style={StyleSheet.absoluteFill} onPress={onClose} />
        <View
          testID="market-watchlist-edit-dialog"
          style={[
            styles.filterMenu,
            {
              height: '75%',
              backgroundColor: dark ? '#1B1B1B' : '#FFFFFF',
              marginBottom: bottom || 20,
            },
          ]}
        >
          <View style={styles.filterMenuHeader}>
            <Text
              style={[
                styles.filterMenuTitle,
                dark ? styles.textDark : styles.textLight,
              ]}
            >
              {COPY[locale].edit}
            </Text>
            <Pressable
              testID="market-watchlist-edit-close"
              accessibilityLabel={COPY[locale].cancel}
              onPress={onClose}
              style={styles.filterClose}
            >
              <MarketIcon name="close" theme={theme} size={20} />
            </Pressable>
          </View>
          <FlatList
            data={items}
            keyExtractor={item => item.key}
            initialNumToRender={12}
            renderItem={({ item }) => (
              <View
                style={{
                  height: 64,
                  paddingHorizontal: 20,
                  flexDirection: 'row',
                  gap: 12,
                  alignItems: 'center',
                }}
              >
                <Image
                  source={{ uri: item.logoUrl }}
                  style={{ width: 32, height: 32, borderRadius: 16 }}
                />
                <View style={{ flex: 1 }}>
                  <Text
                    style={[
                      {
                        fontSize: 16,
                        fontFamily:
                          Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
                        fontWeight: '500',
                        letterSpacing: 0,
                        fontVariant: ['tabular-nums'],
                      },
                      dark ? styles.textDark : styles.textLight,
                    ]}
                  >
                    {item.kind === 'perp' ? item.name : item.symbol}
                  </Text>
                  <Text
                    numberOfLines={1}
                    style={[
                      { fontSize: 12 },
                      dark ? styles.subduedTextDark : styles.subduedTextLight,
                    ]}
                  >
                    {MARKET_REPLAY_SNAPSHOT.locales?.[locale]?.stockMetadata?.[
                      item.key
                    ]?.subtitle ??
                      item.stock?.subtitle ??
                      item.name}
                  </Text>
                </View>
              </View>
            )}
          />
        </View>
      </View>
    </Modal>
  );
}

export type MarketSearchParams = {
  theme: MarketThemeName;
  locale: MarketLocale;
};
export function MarketNativeSearchPage({
  route,
}: {
  route: { params: MarketSearchParams };
}) {
  const { theme, locale } = route.params;
  const navigation = useNavigation();
  const insets = useSafeAreaInsets();
  const [query, setQuery] = useState('');
  const dark = theme === 'dark';
  const items = useMemo(
    () => [
      ...new Map(
        [
          ...Object.values(MARKET_REPLAY_SNAPSHOT.spot).flat(),
          ...Object.values(MARKET_REPLAY_SNAPSHOT.stocks ?? {}).flat(),
          ...Object.values(MARKET_REPLAY_SNAPSHOT.perps).flat(),
        ].map(item => {
          const stock =
            MARKET_REPLAY_SNAPSHOT.locales?.[locale]?.stockMetadata?.[item.key];
          return [
            item.key,
            stock?.subtitle ? { ...item, stock, name: stock.subtitle } : item,
          ];
        }),
      ).values(),
    ],
    [locale],
  );
  const results = useMemo(
    () => (query.trim() ? filterMarketSearch(items, query) : []),
    [items, query],
  );
  return (
    <View
      testID="market-search-page"
      style={[
        styles.screen,
        {
          paddingTop: insets.top,
          paddingBottom: insets.bottom,
          backgroundColor: dark ? '#0F0F0F' : '#FFFFFF',
        },
      ]}
    >
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          gap: 12,
          padding: 16,
        }}
      >
        <View
          style={[
            styles.searchBox,
            { flex: 1 },
            dark ? styles.darkPill : styles.lightPill,
          ]}
        >
          <MarketIcon name="search" theme={theme} size={20} />
          <TextInput
            testID="market-search-page-input"
            autoFocus
            value={query}
            onChangeText={setQuery}
            placeholder={COPY[locale].search}
            placeholderTextColor={dark ? '#FFFFFF64' : '#00000072'}
            autoCapitalize="none"
            autoCorrect={false}
            style={[
              styles.searchInput,
              dark ? styles.textDark : styles.textLight,
            ]}
          />
        </View>
        <Pressable
          testID="market-search-back"
          onPress={() => navigation.goBack()}
        >
          <Text style={dark ? styles.textDark : styles.textLight}>
            {COPY[locale].cancel}
          </Text>
        </Pressable>
      </View>
      <FlatList
        keyboardShouldPersistTaps="handled"
        data={results}
        keyExtractor={item => item.key}
        renderItem={({ item }) => (
          <View
            style={{
              height: 64,
              paddingHorizontal: 20,
              flexDirection: 'row',
              alignItems: 'center',
              gap: 12,
            }}
          >
            <Image
              source={{ uri: item.logoUrl }}
              style={{ width: 32, height: 32, borderRadius: 16 }}
            />
            <View style={{ flex: 1 }}>
              <Text
                style={[
                  {
                    fontSize: 16,
                    fontFamily:
                      Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
                    fontWeight: '500',
                    letterSpacing: 0,
                    fontVariant: ['tabular-nums'],
                  },
                  dark ? styles.textDark : styles.textLight,
                ]}
              >
                {item.symbol}
              </Text>
              <Text
                numberOfLines={1}
                style={[
                  { fontSize: 12 },
                  dark ? styles.subduedTextDark : styles.subduedTextLight,
                ]}
              >
                {item.name}
              </Text>
            </View>
          </View>
        )}
      />
    </View>
  );
}

function MarketListPage({
  page,
  active,
  mode,
  scenario,
  theme,
  locale,
  config,
  networkId,
  timeRange,
  stockCategory,
  perpsCategory,
  watchlistFilter,
  watchlistKeys,
  emptyInset,
  onRecoverScenario,
  onWatchlistKeysChange,
  onMessage,
  onDiagnostics,
}: {
  page: MarketPageDescriptor;
  active: boolean;
  mode: MarketDataMode;
  scenario: MarketScenario;
  theme: MarketThemeName;
  locale: MarketLocale;
  config: MarketConfig;
  networkId: string;
  timeRange: MarketTimeRange;
  stockCategory: string;
  perpsCategory: string;
  watchlistFilter: string;
  watchlistKeys: readonly string[];
  emptyInset: number;
  onRecoverScenario: () => void;
  onWatchlistKeysChange: (keys: readonly string[]) => void;
  onMessage: (message: string) => void;
  onDiagnostics: (
    pageKey: string,
    diagnostics: Partial<PageDiagnostics>,
  ) => void;
}) {
  const listRef = useRef<NativeListRef>(null);
  const requestAbortRef = useRef<AbortController | null>(null);
  const latestItemsRef = useRef<readonly MarketAsset[]>([]);
  const rawLiveItemsRef = useRef<readonly MarketAsset[]>([]);
  const { height: windowHeight } = useWindowDimensions();
  const { bottom: bottomInset } = useSafeAreaInsets();
  const [items, setItems] = useState<readonly MarketAsset[]>([]);
  const [generation, setGeneration] = useState(1);
  const [loading, setLoading] = useState(mode === 'live');
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState<string>();
  const pageNumberRef = useRef(1);
  const cursorRef = useRef<string | undefined>(undefined);
  const [canLoadMore, setCanLoadMore] = useState(false);
  const [badgeInfo, setBadgeInfo] = useState<{
    asset: MarketAsset;
    action: string;
  }>();
  const [menuAsset, setMenuAsset] = useState<MarketAsset>();
  const [menuAnchor, setMenuAnchor] = useState<NativeListActionAnchor>();
  const requestSerialRef = useRef(0);
  const copy = COPY[locale];

  const replayItems = useMemo(() => {
    let next = replayItemsForPage(
      MARKET_REPLAY_SNAPSHOT,
      page,
      watchlistKeys,
      perpsCategory,
      { networkId, timeRange, stockCategory },
    );
    if (page.kind === 'watchlist')
      next = filterWatchlist(next, watchlistFilter);
    return applyMarketScenario(next, scenario);
  }, [
    page,
    perpsCategory,
    scenario,
    watchlistFilter,
    watchlistKeys,
    networkId,
    timeRange,
    stockCategory,
  ]);

  const replaceStructure = useCallback(
    (next: readonly MarketAsset[]) => {
      latestItemsRef.current = next;
      setItems(next);
      setGeneration(value => value + 1);
      onDiagnostics(page.key, { structuralUpdateCount: 1 });
    },
    [onDiagnostics, page.key],
  );

  const acceptQuotes = useCallback(
    (incoming: readonly MarketAsset[], allowPatches: boolean) => {
      let next = incoming;
      if (page.kind === 'watchlist')
        next = filterWatchlist(next, watchlistFilter);
      next = applyMarketScenario(next, scenario);
      const previous = latestItemsRef.current;
      const patches = allowPatches
        ? buildMarketQuotePatches(previous, next, theme)
        : undefined;
      if (patches && sameKeys(previous, next)) {
        latestItemsRef.current = next;
        listRef.current?.applyPatches(patches);
        onDiagnostics(page.key, { quotePatchCount: patches.length });
      } else {
        replaceStructure(next);
      }
    },
    [
      page.kind,
      page.key,
      theme,
      replaceStructure,
      scenario,
      watchlistFilter,
      onDiagnostics,
    ],
  );

  const runLiveRequest = useCallback(
    async ({
      append = false,
      polling = false,
    }: { append?: boolean; polling?: boolean } = {}) => {
      if (polling && requestAbortRef.current) return;
      const serial = requestSerialRef.current + 1;
      requestSerialRef.current = serial;
      requestAbortRef.current?.abort();
      const controller = new AbortController();
      requestAbortRef.current = controller;
      const nextPage = append ? pageNumberRef.current + 1 : 1;
      if (append) setLoadingMore(true);
      else if (!polling) setLoading(true);
      setError(undefined);
      onDiagnostics(page.key, { requestCount: 1 });
      try {
        let result = await fetchMarketPage({
          page,
          pageNumber: nextPage,
          cursor: append ? cursorRef.current : undefined,
          config,
          networkId,
          timeRange,
          stockCategory,
          perpsCategory,
          watchlistKeys,
          forceFailure: scenario === 'error',
          signal: controller.signal,
          locale,
        });
        const refreshedItems: MarketAsset[] = [...result.items];
        // Refresh every loaded page, keeping the full list and its pagination cursor.
        if (polling) {
          const loadedPageCount = pageNumberRef.current;
          for (
            let current = 2;
            current <= loadedPageCount && result.canLoadMore;
            current += 1
          ) {
            result = await fetchMarketPage({
              page,
              pageNumber: current,
              cursor: result.nextCursor,
              config,
              networkId,
              timeRange,
              stockCategory,
              perpsCategory,
              watchlistKeys,
              signal: controller.signal,
              locale,
            });
            refreshedItems.push(...result.items);
          }
        }
        if (requestSerialRef.current !== serial || controller.signal.aborted)
          return;
        const incoming = append
          ? mergeByKey(rawLiveItemsRef.current, result.items)
          : mergeByKey([], refreshedItems);
        const addedRows = incoming.length - rawLiveItemsRef.current.length;
        rawLiveItemsRef.current = incoming;
        acceptQuotes(incoming, polling && !append);
        pageNumberRef.current = result.page;
        cursorRef.current = result.nextCursor;
        setCanLoadMore(result.canLoadMore && (!append || addedRows > 0));
        onDiagnostics(page.key, {
          sourceUrl: result.sourceUrl,
          fetchedAt: result.fetchedAt,
          note: result.note ?? '',
        });
      } catch (requestError) {
        if (requestSerialRef.current !== serial || controller.signal.aborted)
          return;
        const nextError =
          requestError instanceof MarketApiError
            ? `${requestError.message}${
                requestError.responseCode === undefined
                  ? ''
                  : ` (code ${requestError.responseCode})`
              }`
            : requestError instanceof Error
            ? requestError.message
            : String(requestError);
        setError(nextError);
        if (!polling) replaceStructure([]);
        onDiagnostics(page.key, {
          sourceUrl:
            requestError instanceof MarketApiError
              ? requestError.sourceUrl
              : '',
          fetchedAt: new Date().toISOString(),
          note: nextError,
        });
      } finally {
        if (requestSerialRef.current === serial) {
          requestAbortRef.current = null;
          setLoading(false);
          setLoadingMore(false);
        }
      }
    },
    [
      acceptQuotes,
      config,
      locale,
      networkId,
      onDiagnostics,
      page,
      perpsCategory,
      replaceStructure,
      scenario,
      stockCategory,
      timeRange,
      watchlistKeys,
    ],
  );

  useEffect(() => {
    setMenuAsset(undefined);
    setMenuAnchor(undefined);
    setBadgeInfo(undefined);
    if (mode === 'replay') {
      requestAbortRef.current?.abort();
      setError(
        scenario === 'error'
          ? locale === 'en-US'
            ? 'Replay request failed. Tap to retry.'
            : '回放请求失败，点击重试。'
          : undefined,
      );
      setLoading(false);
      setLoadingMore(false);
      pageNumberRef.current = 1;
      setCanLoadMore(false);
      replaceStructure(replayItems);
      onDiagnostics(page.key, {
        sourceUrl:
          page.categoryId === 'stocks' || page.kind === 'topCoins'
            ? MARKET_REPLAY_SNAPSHOT.source.testBaseUrl ?? ''
            : MARKET_REPLAY_SNAPSHOT.source.baseUrl,
        fetchedAt:
          page.categoryId === 'stocks' || page.kind === 'topCoins'
            ? MARKET_REPLAY_SNAPSHOT.source.testFetchedAt ??
              MARKET_REPLAY_SNAPSHOT.fetchedAt
            : MARKET_REPLAY_SNAPSHOT.fetchedAt,
        note: 'deterministic replay',
      });
      return;
    }
    if (!active) {
      requestSerialRef.current += 1;
      requestAbortRef.current?.abort();
      setLoading(false);
      setLoadingMore(false);
      return;
    }
    void runLiveRequest();
  }, [
    active,
    mode,
    page.key,
    replayItems,
    replaceStructure,
    runLiveRequest,
    scenario,
    onDiagnostics,
  ]);

  useEffect(
    () => () => {
      requestSerialRef.current += 1;
      requestAbortRef.current?.abort();
    },
    [],
  );

  useEffect(() => {
    // A theme change intentionally sends a full style snapshot. Seed it from
    // the latest patched values so polling patches cannot be reverted.
    setItems([...latestItemsRef.current]);
    setGeneration(value => value + 1);
  }, [theme]);

  useEffect(() => {
    if (mode !== 'live' || !active || scenario === 'error') return undefined;
    const interval = setInterval(
      () => {
        void runLiveRequest({ polling: true });
      },
      page.kind === 'spot' ? 60_000 : 30_000,
    );
    return () => clearInterval(interval);
  }, [active, mode, page.kind, runLiveRequest, scenario]);

  const snapshot = useMemo(() => {
    const next = buildMarketSnapshot({
      items: items.map(item => {
        const stock =
          mode === 'replay'
            ? MARKET_REPLAY_SNAPSHOT.locales?.[locale]?.stockMetadata?.[
                item.key
              ]
            : undefined;
        return stock ? { ...item, stock } : item;
      }),
      generation,
      theme,
      locale,
      watchlist: page.kind === 'watchlist',
      loading,
      loadingMore,
      error,
      canLoadMore,
    });
    return {
      ...next,
      layout: {
        ...next.layout,
        contentPaddingTop:
          Platform.OS === 'web' ? (page.kind === 'perps' ? 8 : 4) : 0,
        contentPaddingBottom: Platform.OS === 'web' ? 0 : bottomInset,
      },
    };
  }, [
    bottomInset,
    canLoadMore,
    locale,
    error,
    generation,
    items,
    loading,
    loadingMore,
    page.kind,
    theme,
  ]);

  const findAsset = useCallback(
    (key: string | undefined) =>
      latestItemsRef.current.find(item => item.key === key),
    [],
  );

  const closeMenu = useCallback(() => {
    if (menuAnchor) {
      listRef.current?.setActionAnchorState({
        token: menuAnchor.token,
        open: false,
        restoreFocus: true,
      });
    }
    setMenuAsset(undefined);
    setMenuAnchor(undefined);
  }, [menuAnchor]);

  const handleRowAction = useCallback(
    (event: RowActionEvent) => {
      if (event.actionKey === 'retry') {
        if (scenario === 'error') onRecoverScenario();
        else void runLiveRequest();
        return;
      }
      const asset = findAsset(event.rowKey);
      if (!asset) return;
      if (event.actionKey === 'image-bind') {
        onDiagnostics(page.key, { imageBindCount: 1 });
        return;
      }
      if (event.actionKey === 'prewarm-detail-boundary') {
        onDiagnostics(page.key, { note: `prewarm ${asset.symbol}` });
        return;
      }
      if (event.actionKey === 'watchlist-menu') {
        setMenuAsset(asset);
        setMenuAnchor(event.anchor);
        if (event.anchor) {
          listRef.current?.setActionAnchorState({
            token: event.anchor.token,
            open: true,
          });
        }
        return;
      }
      if (event.actionKey.endsWith('-info')) {
        setBadgeInfo({ asset, action: event.actionKey });
        return;
      }
      if (event.actionKey === 'open-detail-boundary') {
        onMessage(
          `${copy.pageBoundary} ${asset.networkId ?? 'perps'} · ${
            asset.symbol
          }`,
        );
      }
    },
    [
      copy.pageBoundary,
      findAsset,
      onDiagnostics,
      onMessage,
      onRecoverScenario,
      onWatchlistKeysChange,
      page.key,
      page.kind,
      runLiveRequest,
      scenario,
      watchlistKeys,
    ],
  );

  const handleVisibleRange = useCallback(
    (range: VisibleRangeChangedEvent) => {
      onDiagnostics(page.key, {
        visibleRange: `${range.firstIndex}-${range.lastIndex} · ${
          range.firstKey ?? '--'
        } → ${range.lastKey ?? '--'}`,
      });
    },
    [onDiagnostics, page.key],
  );

  return (
    <View style={styles.listPage} testID={`market-page-${page.key}`}>
      {page.kind === 'watchlist' &&
      !loading &&
      !error &&
      (watchlistKeys.length === 0 || scenario === 'empty') ? (
        <ScrollView
          style={styles.nativeList}
          testID="market-favorites-empty-scroll"
          nestedScrollEnabled
          contentInsetAdjustmentBehavior="never"
          {...(Platform.OS === 'web'
            ? { dataSet: { collapsiblePagerScroll: '' } }
            : {})}
          contentContainerStyle={
            Platform.OS === 'android'
              ? { paddingTop: emptyInset, minHeight: windowHeight + emptyInset }
              : Platform.OS === 'ios'
              ? { minHeight: windowHeight - emptyInset }
              : undefined
          }
        >
          <MarketFavoritesEmpty
            config={config}
            theme={theme}
            locale={locale}
            onAdd={keys => {
              onWatchlistKeysChange(keys);
              onRecoverScenario();
            }}
          />
        </ScrollView>
      ) : (
        <NativeList
          ref={listRef}
          style={styles.nativeList}
          snapshot={snapshot}
          webVirtualizationEnabled
          testID={`market-native-list-${page.key}`}
          onRowAction={handleRowAction}
          onActionAnchorInvalidated={(event: ActionAnchorInvalidatedEvent) => {
            if (event.token === menuAnchor?.token) closeMenu();
          }}
          onVisibleRangeChanged={handleVisibleRange}
          onEndReached={() => {
            if (
              active &&
              mode === 'live' &&
              canLoadMore &&
              !loading &&
              !loadingMore
            ) {
              void runLiveRequest({ append: true });
            }
          }}
          onRefresh={() => {
            if (mode === 'live') void runLiveRequest();
            else replaceStructure(replayItems);
          }}
        />
      )}
      <MarketBadgeInfo
        asset={badgeInfo?.asset}
        action={
          badgeInfo?.action === 'community-info'
            ? 'stock-info'
            : badgeInfo?.action
        }
        theme={theme}
        locale={locale}
        onClose={() => setBadgeInfo(undefined)}
      />
      <AnchoredActionCard
        open={Boolean(menuAsset)}
        isFirstItem={items[0]?.key === menuAsset?.key}
        anchor={menuAnchor}
        locale={locale}
        onPin={() => {
          if (!menuAsset) return;
          onWatchlistKeysChange([
            menuAsset.key,
            ...watchlistKeys.filter(key => key !== menuAsset.key),
          ]);
          closeMenu();
        }}
        onRemove={() => {
          if (!menuAsset) return;
          onWatchlistKeysChange(
            watchlistKeys.filter(key => key !== menuAsset.key),
          );
          closeMenu();
        }}
        onClose={closeMenu}
      />
    </View>
  );
}

function DiagnosticsPanel({
  theme,
  mode,
  scenario,
  locale,
  tallHeader,
  activePage,
  pageDiagnostics,
  mountedPages,
  pagerDiagnostics,
  lastMessage,
  onModeChange,
  onScenarioChange,
  onThemeChange,
  onLocaleChange,
  onToggleTallHeader,
  onBusyJs,
  onClose,
}: {
  theme: MarketThemeName;
  mode: MarketDataMode;
  scenario: MarketScenario;
  locale: MarketLocale;
  tallHeader: boolean;
  activePage: MarketPageDescriptor;
  pageDiagnostics: PageDiagnostics;
  mountedPages: CollapsiblePagerMountState;
  pagerDiagnostics?: CollapsiblePagerDiagnostics['nativeEvent'];
  lastMessage: string;
  onModeChange: (mode: MarketDataMode) => void;
  onScenarioChange: (scenario: MarketScenario) => void;
  onThemeChange: (theme: MarketThemeName) => void;
  onLocaleChange: (locale: MarketLocale) => void;
  onToggleTallHeader: () => void;
  onBusyJs: () => void;
  onClose: () => void;
}) {
  const dark = theme === 'dark';
  const requestDiagnostics = getMarketRequestDiagnostics();
  const insets = useSafeAreaInsets();
  return (
    <View
      style={[
        styles.diagnosticsPanel,
        { top: insets.top + 8 },
        dark ? styles.diagnosticsPanelDark : styles.diagnosticsPanelLight,
      ]}
      testID="market-diagnostics-panel"
    >
      <View style={styles.diagnosticsHeader}>
        <Text
          style={[
            styles.diagnosticsTitle,
            dark ? styles.textDark : styles.textLight,
          ]}
        >
          Market acceptance
        </Text>
        <Pressable onPress={onClose} testID="market-diagnostics-close">
          <Text style={dark ? styles.textDark : styles.textLight}>×</Text>
        </Pressable>
      </View>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.diagnosticsControls}
      >
        {(['replay', 'live'] as const).map(value => (
          <FilterChip
            key={value}
            label={value}
            selected={mode === value}
            theme={theme}
            testID={`market-mode-${value}`}
            onPress={() => onModeChange(value)}
          />
        ))}
        {(['long', 'short', 'empty', 'error'] as const).map(value => (
          <FilterChip
            key={value}
            label={value}
            selected={scenario === value}
            theme={theme}
            testID={`market-scenario-${value}`}
            onPress={() => onScenarioChange(value)}
          />
        ))}
        <FilterChip
          label={theme}
          selected
          theme={theme}
          testID="market-theme-toggle"
          onPress={() => onThemeChange(dark ? 'light' : 'dark')}
        />
        <FilterChip
          label={locale}
          selected
          theme={theme}
          testID="market-locale-toggle"
          onPress={() => onLocaleChange(locale === 'zh-CN' ? 'en-US' : 'zh-CN')}
        />
        <FilterChip
          label={tallHeader ? 'header 64' : 'header 0'}
          selected={tallHeader}
          theme={theme}
          testID="market-dynamic-header-toggle"
          onPress={onToggleTallHeader}
        />
        <FilterChip
          label="JS busy 1200ms"
          selected={false}
          theme={theme}
          testID="market-js-busy"
          onPress={onBusyJs}
        />
      </ScrollView>
      <Text
        numberOfLines={1}
        style={[
          styles.diagnosticsText,
          dark ? styles.subduedTextDark : styles.subduedTextLight,
        ]}
      >
        {activePage.key} · mounted [{mountedPages.mountedPages.join(', ')}] ·
        native pages {pagerDiagnostics?.nativePageCount ?? '--'} / attached{' '}
        {pagerDiagnostics?.attachedPageCount ?? '--'}
      </Text>
      <Text
        numberOfLines={1}
        style={[
          styles.diagnosticsText,
          dark ? styles.subduedTextDark : styles.subduedTextLight,
        ]}
      >
        request {pageDiagnostics.requestCount} · patch{' '}
        {pageDiagnostics.quotePatchCount} · structural{' '}
        {pageDiagnostics.structuralUpdateCount} · image bind{' '}
        {pageDiagnostics.imageBindCount}
      </Text>
      <Text
        numberOfLines={1}
        style={[
          styles.diagnosticsText,
          dark ? styles.subduedTextDark : styles.subduedTextLight,
        ]}
      >
        visible {pageDiagnostics.visibleRange} ·{' '}
        {pageDiagnostics.fetchedAt || MARKET_REPLAY_SNAPSHOT.fetchedAt}
      </Text>
      <Text
        numberOfLines={1}
        style={[
          styles.diagnosticsText,
          dark ? styles.subduedTextDark : styles.subduedTextLight,
        ]}
      >
        {requestDiagnostics.headerMode} ·{' '}
        {pageDiagnostics.note || lastMessage || 'ready'}
      </Text>
      <Text
        numberOfLines={1}
        style={[
          styles.diagnosticsSource,
          dark ? styles.subduedTextDark : styles.subduedTextLight,
        ]}
      >
        {pageDiagnostics.sourceUrl || requestDiagnostics.baseUrl}
      </Text>
    </View>
  );
}

export function MarketNativePagerExamplePage() {
  const navigation =
    useNavigation<NavigationProp<{ MarketSearch: MarketSearchParams }>>();
  const insets = useSafeAreaInsets();
  const pagerRef = useRef<CollapsiblePagerView>(null);
  const [mode, setMode] = useState<MarketDataMode>('replay');
  const [scenario, setScenario] = useState<MarketScenario>('long');
  const [theme, setTheme] = useState<MarketThemeName>('dark');
  const [locale, setLocale] = useState<MarketLocale>('zh-CN');
  const [config, setConfig] = useState<MarketConfig>(
    MARKET_REPLAY_SNAPSHOT.config,
  );
  const [banners, setBanners] = useState<readonly MarketBanner[]>(
    MARKET_REPLAY_SNAPSHOT.banners,
  );
  const [activeIndex, setActiveIndex] = useState(0);
  const [headerHeight, setHeaderHeight] = useState(
    Platform.OS === 'web' ? 142 : 134,
  );
  const [stickyHeaderHeight, setStickyHeaderHeight] = useState(120);
  const [tallHeader, setTallHeader] = useState(false);
  const [showDiagnostics, setShowDiagnostics] = useState(false);
  const [networkId, setNetworkId] = useState('');
  const [timeRange, setTimeRange] = useState<MarketTimeRange>('1h');
  const [watchlistFilter, setWatchlistFilter] = useState('all');
  const [stockCategory, setStockCategory] = useState('all');
  const [perpsCategory, setPerpsCategory] = useState(
    defaultPerpsCategory(MARKET_REPLAY_SNAPSHOT.config.perpsCategories),
  );
  const [watchlistKeys, setWatchlistKeys] =
    useState<readonly string[]>(FULL_WATCHLIST_KEYS);
  const [watchlistEditMode, setWatchlistEditMode] = useState(false);
  const [lastMessage, setLastMessage] = useState('');
  const [diagnosticsByPage, setDiagnosticsByPage] = useState<
    Record<string, PageDiagnostics>
  >({});
  const [mountedPages, setMountedPages] = useState<CollapsiblePagerMountState>({
    position: 0,
    mountedPages: [0, 1],
    pageKeys: [],
  });
  const [pagerDiagnostics, setPagerDiagnostics] =
    useState<CollapsiblePagerDiagnostics['nativeEvent']>();
  const dark = theme === 'dark';
  const pages = useMemo(
    () => buildMarketPages(config, locale),
    [config, locale],
  );
  const activePage =
    pages[Math.min(activeIndex, pages.length - 1)] ?? pages[0]!;

  useEffect(() => {
    if (mode !== 'live') {
      const localized =
        MARKET_REPLAY_SNAPSHOT.locales?.[locale] ?? MARKET_REPLAY_SNAPSHOT;
      setConfig(localized.config);
      setBanners(localized.banners);
      return undefined;
    }
    const controller = new AbortController();
    Promise.all([
      fetchMarketConfig(controller.signal, locale),
      fetchMarketBanners(controller.signal, locale),
    ])
      .then(([configResult, bannerResult]) => {
        setConfig(configResult.config);
        setBanners(bannerResult.banners);
        setPerpsCategory(value =>
          configResult.config.perpsCategories.some(item => item.id === value)
            ? value
            : defaultPerpsCategory(configResult.config.perpsCategories),
        );
      })
      .catch(error => {
        if (!controller.signal.aborted) {
          setLastMessage(
            error instanceof Error ? error.message : String(error),
          );
        }
      });
    return () => controller.abort();
  }, [mode, locale]);

  useEffect(() => {
    if (activeIndex >= pages.length) {
      setActiveIndex(Math.max(0, pages.length - 1));
    }
  }, [activeIndex, pages.length]);

  const updatePageDiagnostics = useCallback(
    (pageKey: string, next: Partial<PageDiagnostics>) => {
      setDiagnosticsByPage(previous => {
        const current = previous[pageKey] ?? EMPTY_PAGE_DIAGNOSTICS;
        return {
          ...previous,
          [pageKey]: {
            ...current,
            ...next,
            requestCount:
              current.requestCount +
              (next.requestCount === undefined ? 0 : next.requestCount),
            quotePatchCount:
              current.quotePatchCount +
              (next.quotePatchCount === undefined ? 0 : next.quotePatchCount),
            structuralUpdateCount:
              current.structuralUpdateCount +
              (next.structuralUpdateCount === undefined
                ? 0
                : next.structuralUpdateCount),
            imageBindCount:
              current.imageBindCount +
              (next.imageBindCount === undefined ? 0 : next.imageBindCount),
          },
        };
      });
    },
    [],
  );

  const selectPage = useCallback((index: number) => {
    setActiveIndex(index);
    pagerRef.current?.setPage(index);
  }, []);

  const handleMountedPagesChanged = useCallback(
    (next: CollapsiblePagerMountState) => {
      setMountedPages(previous =>
        previous.position === next.position &&
        previous.mountedPages.length === next.mountedPages.length &&
        previous.mountedPages.every(
          (pageIndex, index) => pageIndex === next.mountedPages[index],
        ) &&
        previous.pageKeys.length === next.pageKeys.length &&
        previous.pageKeys.every(
          (pageKey, index) => pageKey === next.pageKeys[index],
        )
          ? previous
          : next,
      );
    },
    [],
  );

  const handlePageSelected = useCallback(
    (event: CollapsiblePagerViewOnPageSelectedEvent) => {
      setActiveIndex(event.nativeEvent.position);
      setWatchlistEditMode(false);
    },
    [],
  );

  const handleHeaderLayout = useCallback((event: LayoutChangeEvent) => {
    setHeaderHeight(Math.round(event.nativeEvent.layout.height));
  }, []);

  const handleStickyHeaderLayout = useCallback((event: LayoutChangeEvent) => {
    setStickyHeaderHeight(Math.round(event.nativeEvent.layout.height));
  }, []);

  const handleBusyJs = useCallback(() => {
    setLastMessage(
      'JS busy scheduled; start a native collapse or horizontal swipe now.',
    );
    setTimeout(() => {
      const startedAt = Date.now();
      while (Date.now() - startedAt < 1200) {
        // Deliberately keep the example main JS runtime busy for acceptance.
      }
      setLastMessage(`JS busy completed at ${new Date().toISOString()}`);
    }, 150);
  }, []);

  const background = dark ? '#0F0F0F' : '#FFFFFF';
  return (
    <View
      style={[
        styles.screen,
        { backgroundColor: background, paddingTop: insets.top },
      ]}
      testID="market-native-pager-example"
    >
      <StatusBar
        translucent
        backgroundColor="transparent"
        barStyle={dark ? 'light-content' : 'dark-content'}
      />
      {Platform.OS !== 'web' ? (
        <>
          <Pressable
            testID="market-search"
            accessibilityRole="button"
            accessibilityLabel={COPY[locale].search}
            onPress={() =>
              navigation.navigate('MarketSearch', { theme, locale })
            }
            style={[
              styles.searchBox,
              dark ? styles.darkPill : styles.lightPill,
              {
                paddingHorizontal: 9,
                borderWidth: 1,
                borderColor: 'transparent',
              },
            ]}
          >
            <View style={styles.searchGlyph}>
              <MarketIcon name="search" theme={theme} size={20} />
            </View>
            <TextInput
              accessible={false}
              pointerEvents="none"
              editable={false}
              placeholder={COPY[locale].search}
              placeholderTextColor={dark ? '#FFFFFF64' : '#00000072'}
              allowFontScaling={false}
              style={[
                styles.searchInput,
                {
                  height: 36,
                  lineHeight: undefined,
                  paddingVertical: 8,
                  fontFamily: Platform.OS === 'ios' ? 'System' : 'sans-serif',
                },
              ]}
            />
          </Pressable>

          <View style={styles.primaryNavigation}>
            <Pressable
              accessibilityRole="tab"
              accessibilityState={{ selected: true }}
              accessibilityHint="Long press to open Market acceptance diagnostics"
              onLongPress={() => setShowDiagnostics(value => !value)}
              testID="market-primary-tab"
            >
              <Text
                numberOfLines={1}
                style={[
                  styles.primaryNavigationActive,
                  dark ? styles.textDark : styles.textLight,
                ]}
              >
                {COPY[locale].market}
              </Text>
            </Pressable>
            {[COPY[locale].defi, COPY[locale].browser].map(name => (
              <Pressable
                key={name}
                accessibilityRole="tab"
                testID={`market-boundary-${name}`}
                onPress={() =>
                  setLastMessage(`${name} · ${COPY[locale].pageBoundary}`)
                }
              >
                <Text
                  numberOfLines={1}
                  style={[
                    styles.primaryNavigationText,
                    dark ? styles.subduedTextDark : styles.subduedTextLight,
                  ]}
                >
                  {name}
                </Text>
              </Pressable>
            ))}
          </View>
        </>
      ) : null}
      <CollapsiblePagerView
        ref={pagerRef}
        style={styles.pager}
        initialPage={0}
        headerHeight={headerHeight}
        stickyHeaderHeight={stickyHeaderHeight}
        pageRetentionDistance={1}
        scrollEnabled
        testID="market-collapsible-pager"
        onPageSelected={handlePageSelected}
        onMountedPagesChanged={handleMountedPagesChanged}
        onCollapsibleStateChanged={event =>
          setPagerDiagnostics(event.nativeEvent)
        }
        header={
          <MarketHeader
            banners={banners}
            theme={theme}
            tall={tallHeader}
            onBannerPress={banner =>
              setLastMessage(
                `banner ${banner.tokenListId} · ${COPY[locale].pageBoundary}`,
              )
            }
            onLayout={handleHeaderLayout}
          />
        }
        stickyHeader={
          <MarketStickyHeader
            pages={pages}
            activeIndex={activeIndex}
            activePage={activePage}
            config={config}
            theme={theme}
            locale={locale}
            networkId={networkId}
            timeRange={timeRange}
            watchlistFilter={watchlistFilter}
            stockCategory={stockCategory}
            perpsCategory={perpsCategory}
            watchlistEmpty={
              activePage.kind === 'watchlist' &&
              (watchlistKeys.length === 0 || scenario === 'empty')
            }
            onSelectPage={selectPage}
            onNetworkChange={setNetworkId}
            onTimeRangeChange={setTimeRange}
            onWatchlistFilterChange={setWatchlistFilter}
            onStockCategoryChange={setStockCategory}
            onPerpsCategoryChange={setPerpsCategory}
            onToggleWatchlistEdit={() => setWatchlistEditMode(true)}
            onToggleDiagnostics={() => setShowDiagnostics(value => !value)}
            onLayout={handleStickyHeaderLayout}
          />
        }
      >
        {pages.map((page, index) => (
          <View
            key={page.key}
            style={[styles.listPage, { backgroundColor: background }]}
          >
            <MarketListPage
              page={page}
              active={index === activeIndex}
              mode={mode}
              scenario={scenario}
              theme={theme}
              locale={locale}
              config={config}
              networkId={networkId}
              timeRange={timeRange}
              stockCategory={stockCategory}
              perpsCategory={perpsCategory}
              watchlistFilter={watchlistFilter}
              watchlistKeys={watchlistKeys}
              emptyInset={headerHeight + stickyHeaderHeight}
              onRecoverScenario={() => setScenario('long')}
              onWatchlistKeysChange={setWatchlistKeys}
              onMessage={setLastMessage}
              onDiagnostics={updatePageDiagnostics}
            />
          </View>
        ))}
      </CollapsiblePagerView>

      <MarketFavoritesDialog
        visible={watchlistEditMode}
        items={replayItemsForPage(
          MARKET_REPLAY_SNAPSHOT,
          pages[0]!,
          watchlistKeys,
          perpsCategory,
        )}
        theme={theme}
        locale={locale}
        onClose={() => setWatchlistEditMode(false)}
      />
      {lastMessage ? (
        <Pressable
          onPress={() => setLastMessage('')}
          style={[
            styles.messageToast,
            dark ? styles.darkPill : styles.lightPill,
          ]}
          testID="market-boundary-message"
        >
          <Text
            numberOfLines={2}
            style={[
              styles.messageToastText,
              dark ? styles.textDark : styles.textLight,
            ]}
          >
            {lastMessage}
          </Text>
        </Pressable>
      ) : null}

      {showDiagnostics ? (
        <DiagnosticsPanel
          theme={theme}
          mode={mode}
          scenario={scenario}
          locale={locale}
          tallHeader={tallHeader}
          activePage={activePage}
          pageDiagnostics={
            diagnosticsByPage[activePage.key] ?? EMPTY_PAGE_DIAGNOSTICS
          }
          mountedPages={mountedPages}
          pagerDiagnostics={pagerDiagnostics}
          lastMessage={lastMessage}
          onModeChange={setMode}
          onScenarioChange={setScenario}
          onThemeChange={setTheme}
          onLocaleChange={setLocale}
          onToggleTallHeader={() => setTallHeader(value => !value)}
          onBusyJs={handleBusyJs}
          onClose={() => setShowDiagnostics(false)}
        />
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1 },
  pager: { flex: 1 },
  header: { paddingTop: 0 },
  backgroundDark: { backgroundColor: '#0F0F0F' },
  backgroundLight: { backgroundColor: '#FFFFFF' },
  textDark: { color: '#FFFFFFED' },
  textLight: { color: '#000000DF' },
  subduedTextDark: { color: '#FFFFFFAF' },
  subduedTextLight: { color: '#0000009B' },
  iconDark: { borderColor: '#FFFFFF64', backgroundColor: '#FFFFFF64' },
  iconLight: { borderColor: '#11111164', backgroundColor: '#11111164' },
  lineDark: { backgroundColor: '#FFFFFFED' },
  lineLight: { backgroundColor: '#111111' },
  darkPill: { backgroundColor: '#202020', borderColor: '#FFFFFF1F' },
  lightPill: { backgroundColor: '#F5F5F5', borderColor: '#1111111A' },
  pressed: { opacity: 0.72 },
  filterBackdrop: {
    flex: 1,
    backgroundColor: '#0000009B',
    justifyContent: 'flex-end',
    paddingHorizontal: 20,
  },
  filterMenu: {
    width: '100%',
    maxHeight: '85%',
    borderRadius: 24,
    overflow: 'hidden',
  },
  filterMenuHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 20,
  },
  filterClose: {
    width: 30,
    height: 30,
    borderRadius: 15,
    alignItems: 'center',
    justifyContent: 'center',
  },
  networkEmpty: { padding: 20, alignItems: 'center' },
  filterMenuTitle: {
    fontSize: 20,
    lineHeight: 28,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-SemiBold',
    fontWeight: '600',
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  networkSearch: {
    marginHorizontal: 20,
    marginTop: 10,
    marginBottom: 8,
    height: 38,
    paddingHorizontal: 10,
    borderRadius: 20,
    gap: 6,
    flexDirection: 'row',
    alignItems: 'center',
  },
  filterOption: {
    height: 48,
    paddingHorizontal: 12,
    marginHorizontal: 8,
    borderRadius: 12,
    gap: 12,
    flexDirection: 'row',
    alignItems: 'center',
  },
  networkOptionText: {
    flex: 1,
    fontSize: 16,
    lineHeight: 24,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500',
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  timeOptions: { padding: 8, gap: 4 },
  timeOption: { paddingHorizontal: 12, paddingVertical: 8, borderRadius: 8 },
  searchBox: {
    height: 40,
    marginHorizontal: 20,
    borderRadius: 22,
    borderWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 14,
  },
  searchGlyph: { width: 20, height: 20, marginRight: 7 },
  searchCircle: {
    position: 'absolute',
    left: 3,
    top: 3,
    width: 16,
    height: 16,
    borderWidth: 2,
    borderRadius: 8,
    backgroundColor: 'transparent',
  },
  searchHandle: {
    position: 'absolute',
    width: 8,
    height: 2,
    left: 15,
    top: 16,
    transform: [{ rotate: '45deg' }],
  },
  searchInput: {
    flex: 1,
    height: 40,
    fontSize: 16,
    lineHeight: 22,
    fontFamily: Platform.OS === 'ios' ? 'System' : 'Roobert-Regular',
    fontWeight: '400',
    paddingVertical: 0,
    ...(Platform.OS === 'web'
      ? { outlineStyle: 'solid' as const, outlineWidth: 0 }
      : {}),
  },
  primaryNavigation: {
    height: 52,
    paddingTop: 8,
    paddingHorizontal: 20,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
  },
  primaryNavigationActive: {
    fontSize: 20,
    lineHeight: 28,
    fontFamily: 'Roobert-SemiBold',
    fontWeight: '600',
  },
  primaryNavigationText: {
    fontSize: 20,
    lineHeight: 28,
    fontFamily: 'Roobert-SemiBold',
    fontWeight: '600',
  },
  bannerList: { paddingHorizontal: 16, paddingVertical: 8, gap: 12 },
  bannerCard: {
    width: 128,
    height: 118,
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#80808026',
    paddingHorizontal: 12,
    paddingVertical: 14,
    justifyContent: 'space-between',
  },
  bannerTitleRow: { flexDirection: 'row', alignItems: 'flex-start', gap: 4 },
  bannerTitle: {
    flexShrink: 1,
    fontSize: 14,
    lineHeight: 20,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-SemiBold',
    fontWeight: '600',
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  bannerChange: {
    fontSize: 14,
    lineHeight: 20,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500',
    marginTop: 2,
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  leverageBadge: {
    borderRadius: 4,
    overflow: 'hidden',
    backgroundColor: '#164766',
    color: '#70B8FF',
    fontSize: 10,
    lineHeight: 16,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Regular',
    fontWeight: '400',
    paddingHorizontal: 6,
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  bannerLogos: { flexDirection: 'row', alignItems: 'center' },
  bannerLogo: {
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    overflow: 'hidden',
  },
  bannerLogoImage: { width: 20, height: 20, borderRadius: 10 },
  bannerLogoOverlap: { marginLeft: -6 },
  dynamicHeaderProbe: {
    height: 64,
    marginHorizontal: 16,
    marginBottom: 8,
    borderWidth: StyleSheet.hairlineWidth,
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
  },
  dynamicHeaderProbeText: {
    fontSize: 13,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500',
  },
  marketTabs: { height: 44, paddingHorizontal: 20, gap: 20 },
  marketTabButton: { height: 44, justifyContent: 'center', flexShrink: 0 },
  marketTabText: {
    fontSize: 16,
    lineHeight: 24,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500',
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  activeTabLine: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    height: 2,
    borderRadius: 1,
  },
  compactDropdown: { flexDirection: 'row', alignItems: 'center', gap: 4 },
  secondaryFilters: {
    height: 42,
    paddingTop: 6,
    paddingBottom: 4,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    gap: 8,
  },
  categoryFilterScroll: { flex: 1 },
  categoryFilterList: { alignItems: 'center', gap: 8, paddingRight: 4 },
  filterChip: {
    height: 32,
    paddingHorizontal: 10,
    borderRadius: 16,
    alignItems: 'center',
    justifyContent: 'center',
  },
  darkChipSelected: { backgroundColor: '#FFFFFF1B' },
  lightChipSelected: { backgroundColor: '#00000017' },
  filterChipText: {
    fontSize: 14,
    lineHeight: 20,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500',
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  editButton: {
    width: 32,
    height: 32,
    alignItems: 'center',
    justifyContent: 'center',
    marginLeft: 'auto',
  },
  editGlyph: {
    fontSize: 20,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Regular',
  },
  columnHeader: {
    height: 32,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  columnTitle: {
    flex: 1,
    fontSize: 12,
    lineHeight: 16,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500',
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  columnPrice: {
    width: 92,
    textAlign: 'right',
    fontSize: 12,
    lineHeight: 16,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500',
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  columnChange: {
    width: 88,
    textAlign: 'right',
    fontSize: 12,
    lineHeight: 16,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Medium',
    fontWeight: '500',
    letterSpacing: 0,
    fontVariant: ['tabular-nums'],
  },
  listPage: { flex: 1 },
  nativeList: { flex: 1 },
  actionCard: {
    position: 'absolute',
    zIndex: 20,
    width: 164,
    padding: 8,
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
  },
  actionCardDark: { backgroundColor: '#262626', borderColor: '#FFFFFF24' },
  actionCardLight: { backgroundColor: '#FFFFFF', borderColor: '#1111111A' },
  actionCardFallback: { left: 20, bottom: 100 },
  actionCardTitle: {
    fontSize: 14,
    lineHeight: 20,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-SemiBold',
    fontWeight: '600',
    paddingHorizontal: 8,
    paddingVertical: 4,
  },
  actionCardButton: {
    minHeight: 36,
    justifyContent: 'center',
    paddingHorizontal: 8,
  },
  negativeText: { color: '#EE4B5A' },
  messageToast: {
    position: 'absolute',
    left: 20,
    right: 20,
    bottom: 24,
    minHeight: 44,
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    paddingHorizontal: 14,
    paddingVertical: 10,
    zIndex: 30,
  },
  messageToastText: {
    fontSize: 13,
    lineHeight: 18,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Regular',
    fontWeight: '400',
  },
  diagnosticsPanel: {
    position: 'absolute',
    left: 8,
    right: 8,
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    padding: 10,
    zIndex: 40,
  },
  diagnosticsPanelDark: {
    backgroundColor: '#171717F7',
    borderColor: '#FFFFFF26',
  },
  diagnosticsPanelLight: {
    backgroundColor: '#FFFFFFF7',
    borderColor: '#11111126',
  },
  diagnosticsHeader: {
    height: 24,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  diagnosticsTitle: {
    fontSize: 14,
    lineHeight: 20,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-SemiBold',
    fontWeight: '600',
  },
  diagnosticsControls: { gap: 6, paddingVertical: 6 },
  diagnosticsText: {
    fontSize: 11,
    lineHeight: 15,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Regular',
    fontWeight: '400',
  },
  diagnosticsSource: {
    fontSize: 10,
    lineHeight: 14,
    fontFamily: Platform.OS === 'ios' ? 'Roobert' : 'Roobert-Regular',
    fontWeight: '400',
  },
});
