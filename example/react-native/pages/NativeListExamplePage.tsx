import { useMemo, useRef, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import {
  NativeList,
  type NativeListRef,
  type NativeListSnapshot,
  type NativeListTheme,
  type ReorderEvent,
  type RowModel,
  type SelectionDeltaEvent,
} from '@onekeyfe/react-native-native-list';

import type { RootStackParamList } from '../route';

export type NativeListExampleKey =
  | 'container-linear'
  | 'container-sectioned'
  | 'container-grid'
  | 'container-table'
  | 'row-identity'
  | 'row-rail'
  | 'row-activity'
  | 'row-message'
  | 'row-data'
  | 'row-media'
  | 'row-metric'
  | 'row-section-header'
  | 'row-action'
  | 'row-system'
  | 'checkbox';

type ExampleGroup = 'container' | 'row' | 'selection';

export type NativeListExample = Readonly<{
  key: NativeListExampleKey;
  group: ExampleGroup;
  title: string;
  description: string;
  source: string;
}>;

export const nativeListExamples: readonly NativeListExample[] = [
  {
    key: 'container-linear',
    group: 'container',
    title: 'Linear container',
    description: 'Token manager list with refresh, assets and list states',
    source:
      'packages/kit/src/views/AssetList/components/TokenManager/TokenManagerList.tsx',
  },
  {
    key: 'container-sectioned',
    group: 'container',
    title: 'Sectioned container',
    description: 'All Networks manager with sticky sections and selection',
    source:
      'packages/kit/src/views/ChainSelector/components/AllNetworksManager/NetworksSectionList.tsx',
  },
  {
    key: 'container-grid',
    group: 'container',
    title: 'Grid container',
    description: 'Two-column NFT collection used by the Home asset view',
    source: 'packages/kit/src/views/Home/components/NFTListView/index.tsx',
  },
  {
    key: 'container-table',
    group: 'container',
    title: 'Table container',
    description: 'Perpetual market columns with stable weighted alignment',
    source:
      'packages/kit/src/views/Perp/components/TokenSelector/PerpTokenSelectorRow.tsx',
  },
  {
    key: 'row-identity',
    group: 'row',
    title: 'Identity rows',
    description: 'Gallery token and NFT rows with status and network corners',
    source:
      'packages/kit/src/views/Developer/pages/Gallery/Components/stories/ListItem.tsx',
  },
  {
    key: 'row-rail',
    group: 'row',
    title: 'Rail rows',
    description: 'Horizontal perpetual favorites with selection and reorder',
    source:
      'packages/kit/src/views/Perp/components/FavoritesBar/FavoriteTokenItem.tsx',
  },
  {
    key: 'row-activity',
    group: 'row',
    title: 'Activity rows',
    description: 'Transaction history statuses, amounts and pending actions',
    source:
      'packages/kit/src/components/TxHistoryListView/TxHistoryListItem.tsx',
  },
  {
    key: 'row-message',
    group: 'row',
    title: 'Message rows',
    description: 'Notification topics, unread state, content and time',
    source:
      'packages/kit/src/views/Notifications/components/NotificationListView.tsx',
  },
  {
    key: 'row-data',
    group: 'row',
    title: 'Data rows',
    description: 'Asset, price, 24h change and volume market cells',
    source:
      'packages/kit/src/views/Perp/components/TokenSelector/PerpTokenSelectorRow.tsx',
  },
  {
    key: 'row-media',
    group: 'row',
    title: 'Media tile rows',
    description: 'NFT gallery cards with collection, asset name and network',
    source:
      'packages/kit/src/views/Home/components/NFTListView/NFTListItem.tsx',
  },
  {
    key: 'row-metric',
    group: 'row',
    title: 'Metric card rows',
    description: 'Portfolio value, P&L, activity and performance metrics',
    source:
      'packages/kit/src/views/Perp/components/Portfolio/PerpPortfolioContent.tsx',
  },
  {
    key: 'row-section-header',
    group: 'row',
    title: 'Section header rows',
    description: 'Network value headers, alphabet groups and history dates',
    source:
      'packages/kit/src/views/ChainSelector/components/AllNetworksManager/NetworksSectionList.tsx; packages/kit/src/components/TxHistoryListView/TxHistorySectionHeader.tsx',
  },
  {
    key: 'row-action',
    group: 'row',
    title: 'Action rows',
    description: 'Token management and bookmark editing actions',
    source:
      'packages/kit/src/views/AssetList/components/TokenManager/TokenManagerList.tsx; packages/kit/src/views/Discovery/pages/BookmarkListModal/index.tsx',
  },
  {
    key: 'row-system',
    group: 'row',
    title: 'System rows',
    description: 'Token-list loading, no-match and inline retry states',
    source:
      'packages/kit/src/components/Loading/ListLoading.tsx; packages/kit/src/components/TokenListView/CrossNetworkSearchRows.tsx',
  },
  {
    key: 'checkbox',
    group: 'selection',
    title: 'Checkbox selection',
    description: 'List, section and row selection with all supported states',
    source:
      'packages/components/src/forms/Checkbox/index.tsx; packages/kit/src/views/ChainSelector/components/AllNetworksManager/NetworksSectionList.tsx',
  },
];

const demoTheme: NativeListTheme & { rowPressedBackground: string } = {
  background: '#FFFFFF',
  rowBackground: '#FFFFFF',
  rowSelectedBackground: '#0000000F',
  rowPressedBackground: '#00000017',
  subduedBackground: '#F9F9F9',
  strongBackground: '#0000000F',
  primaryText: '#000000DF',
  secondaryText: '#0000009B',
  disabledText: '#00000072',
  icon: '#0000009B',
  iconSubdued: '#00000072',
  separator: '#0000001F',
  accent: '#0D8200FC',
  positive: '#00713FDE',
  negative: '#C40006D3',
  info: '#006DCBF2',
  criticalBackground: '#F3000D14',
  inverseBackground: '#000000DF',
  inverseText: '#FCFCFC',
};

const image = (uri: string, width = 40, height = 40) => ({
  uri,
  width,
  height,
  contentFit: 'cover' as const,
});

// Exact 80x80 transparent PNG returned by the app's Hyperliquid asset source.
const hyperliquidHypeImageUri =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFAAAABQCAMAAAC5zwKfAAAAJFBMVEVMaXGX++SW/OSX/OSX/OSX/OSX/uSW/OSX/+aX++SW/OSX/OROiWbtAAAAC3RSTlMA8+DGaoslqRA+UyHu5zYAAAAJcEhZcwAAMTYAADE2AZrnQiAAAAJqSURBVHja7VjboqMgDBzCReT/vxUQEPahux5tuXjUt22eLNAxCZkhCHzta18bG7vxXyUCZPHHQXEVTa+rxASAM+Ef8HBe5u3Z8nQb0Diz/+l5+PdI1/w74kHH+ZaHKpr3IavdDUCuP8esDJcBxVQbdcpfzKFe63l9DfMLXNCNRIh0yUOVWjPhmEPFsYYTgFI1p4rdqKdYkAAlzkaYSrbnls1DsZoajWo29dif3cvDXRQGLPYVpjebXiGbQzEqWi7L3QpwQPtjWgj5UgYBSJF4pa54aSMO9DNmAj53YZ1bcHpQAxkEYz7VblG/Id0BkIMqZJFxqlUPn4ZVzzhyLc8ilMpqOeYRsbnB5sT8KdV6Jwtv7RsFecyYnE5pBzUrVcf9q2auTuEl0dYvg/xXK1QupE+qm0idUp2ByIAicdqKKIMD+Lf6S+XZXqnQs3gwZJ4FtGQfdvHpkBmxpz1kT3songUUIvcXOJkJcT4NmLtMcUR5AYA15ZOYpamHAALfSeJPLzBgStNDW+JeYpN0p+SLgerBbD3uTiDHEmYLQbb68vcRf8JHAjVazlChpBf2DGCurfKxvkvDMgRHmqjSgNebukx97rsIqurD5FvM6gfNAQ6k/E6/sLQPtS5VWYIA8H5A+k4Lk0qndvz6itcd+2rHB1F1u1ECgD3FYKdu3x7as267p3j5g2jnQfny5rze98wyvbjvpiEdDGtn8CclWUeEhCj9kF6xXoxWrFdvozrU9GRJ1+/LNQ0NEVdvo0AqH+WdAm4AIr8jhpsfMZDKntQux/tflhQT2/buiXHjnFeZJ4Dk093R1772P9gfZjfEeisViUkAAAAASUVORK5CYII=';

// ServiceDiscovery.buildWebsiteIconUrl('https://dydx.trade', 128).
const dydxBookmarkLogo = {
  uri: 'https://utility.onekeycn.com/utility/v1/discover/icon?hostname=dydx.trade&size=128',
  width: 40,
  height: 40,
  headers: { 'User-Agent': 'OneKeyWallet/1' },
  contentFit: 'cover' as const,
};

/**
 * Public, deterministic data mirrored from app-monorepo fixtures:
 * - kit/.../Gallery/Components/stories/ListItem.tsx: TOKENDATA/NFTDATA
 * - shared/src/config/presetNetworks.ts: public preset network records
 * - kit/.../Perp/components/FooterTicker/footerTickerUtils.test.ts: createItem
 * - kit/.../Perp/utils/tokenSelector{Favorites,Tabs}.test.ts: favorite/sort fixtures
 * - kit/.../Perp/components/Portfolio/usePerpPortfolioData.test.ts: fills/chart fixtures
 * - kit/src/components/TxHistoryListView/TxHistoryListItem.tsx and the
 *   verified public simulator history: transaction rows
 * - shared/src/locale/json/en_US.json notifications.* copy and
 *   kit/src/views/Notifications/components/NotificationListView.tsx: topic rows
 * - kit/src/components/{TokenListView/CrossNetworkSearchRows,Loading/ListLoading}.tsx
 */
const tokenImages = {
  bitcoin: image(
    'https://cdn.jsdelivr.net/gh/atomiclabs/cryptocurrency-icons@1a63530be6e374711a8554f31b17e4cb92c25fa5/128/color/btc.png',
  ),
  ethereum: image(
    'https://cdn.jsdelivr.net/gh/atomiclabs/cryptocurrency-icons@1a63530be6e374711a8554f31b17e4cb92c25fa5/128/color/eth.png',
  ),
  polygon: image(
    'https://cdn.jsdelivr.net/gh/atomiclabs/cryptocurrency-icons@1a63530be6e374711a8554f31b17e4cb92c25fa5/128/color/matic.png',
  ),
  usdc: image(
    'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48.png',
  ),
  usdt: image(
    'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0xdac17f958d2ee523a2206206994597c13d831ec7-1722246302921.png',
  ),
  solana: image('https://uni.onekey-asset.com/static/chain/sol.png'),
  arbitrum: image('https://uni.onekey-asset.com/static/chain/arbitrum.png'),
  sui: image(
    'https://uni.onekey-asset.com/server-service-onchain/sui--mainnet/tokens/0x2::sui::SUI.png',
  ),
  atom: image('https://uni.onekey-asset.com/static/chain/cosmos.png'),
} as const;

const ethereumCorner = image(
  'https://uni.onekey-asset.com/static/chain/eth.png',
  16,
  16,
);

const networkImages = {
  Ethereum: image('https://uni.onekey-asset.com/static/chain/eth.png', 32, 32),
  'BNB Chain': image(
    'https://uni.onekey-asset.com/static/chain/bsc.png',
    32,
    32,
  ),
  Polygon: image(
    'https://uni.onekey-asset.com/static/chain/polygon.png',
    32,
    32,
  ),
  Arbitrum: image(
    'https://uni.onekey-asset.com/static/chain/arbitrum.png',
    32,
    32,
  ),
  Linea: image('https://uni.onekey-asset.com/static/chain/linea.png', 32, 32),
  Solana: image('https://uni.onekey-asset.com/static/chain/sol.png', 32, 32),
  Bitcoin: image('https://uni.onekey-asset.com/static/chain/btc.png', 32, 32),
  Base: image('https://uni.onekey-asset.com/static/chain/base.png', 32, 32),
  Avalanche: image(
    'https://uni.onekey-asset.com/static/chain/avalanche.png',
    32,
    32,
  ),
  Optimism: image(
    'https://uni.onekey-asset.com/static/chain/optimism.png',
    32,
    32,
  ),
  Scroll: image('https://uni.onekey-asset.com/static/chain/scr.png', 32, 32),
  PulseChain: image(
    'https://uni.onekey-asset.com/static/chain/pulse.png',
    32,
    32,
  ),
  Mantle: image('https://uni.onekey-asset.com/static/chain/mantle.png', 32, 32),
  Cronos: image('https://uni.onekey-asset.com/static/chain/cronos.png', 32, 32),
  Blast: image('https://uni.onekey-asset.com/static/logo/blast.png', 32, 32),
  Bitlayer: image('https://uni.onekey-asset.com/static/chain/btr.png', 32, 32),
  BOB: image('https://uni.onekey-asset.com/static/chain/bob.png', 32, 32),
  Tron: image('https://uni.onekey-asset.com/static/chain/tron.png', 32, 32),
} as const;

const tokenVisual = (
  name: keyof typeof tokenImages,
  fallbackText: string,
  withNetwork = false,
) => ({
  kind: 'token' as const,
  image: tokenImages[name],
  fallbackText,
  networkImage: withNetwork ? ethereumCorner : undefined,
});

const tokenManagerRows = [
  {
    type: 'identity',
    key: 'token-btc',
    leading: tokenVisual('bitcoin', 'BTC'),
    title: 'BTC',
    subtitle: 'Bitcoin',
    trailing: [
      {
        kind: 'icon',
        name: 'MinusCircleOutline',
        tintColor: '#C40006D3',
        disabled: true,
        actionKey: 'token.remove',
      },
    ],
  },
  {
    type: 'identity',
    key: 'token-eth',
    leading: tokenVisual('ethereum', 'ETH'),
    title: 'ETH',
    subtitle: 'Ethereum',
    trailing: [
      {
        kind: 'icon',
        name: 'MinusCircleOutline',
        tintColor: '#C40006D3',
        actionKey: 'token.remove',
      },
    ],
  },
  {
    type: 'identity',
    key: 'token-usdc',
    leading: tokenVisual('usdc', 'USDC', true),
    title: 'USDC',
    subtitle: 'USD Coin',
    badges: [{ key: 'network', text: 'Multichain' }],
    trailing: [
      {
        kind: 'icon',
        name: 'MinusCircleOutline',
        tintColor: '#C40006D3',
        actionKey: 'token.remove',
      },
    ],
  },
  {
    type: 'identity',
    key: 'token-usdt',
    leading: tokenVisual('usdt', 'USDT', true),
    title: 'USDT',
    subtitle: 'Tether',
    badges: [{ key: 'network', text: 'Ethereum' }],
    trailing: [
      {
        kind: 'icon',
        name: 'MinusCircleOutline',
        tintColor: '#C40006D3',
        actionKey: 'token.remove',
      },
    ],
  },
  {
    type: 'identity',
    key: 'token-sol',
    leading: tokenVisual('solana', 'SOL'),
    title: 'SOL',
    subtitle: 'Solana',
    trailing: [
      {
        kind: 'icon',
        name: 'PlusCircleOutline',
        actionKey: 'token.add',
      },
    ],
  },
  {
    type: 'identity',
    key: 'token-arb',
    leading: tokenVisual('arbitrum', 'ARB'),
    title: 'ARB',
    subtitle: 'Arbitrum',
    trailing: [
      {
        kind: 'icon',
        name: 'PlusCircleOutline',
        actionKey: 'token.add',
      },
    ],
  },
] as const satisfies readonly RowModel[];

const galleryTokenRows: readonly RowModel[] = [
  {
    type: 'identity',
    key: 'gallery-token-btc',
    leading: {
      ...tokenVisual('bitcoin', 'BTC'),
      shape: 'rounded',
      cornerIcon: {
        name: 'ErrorSolid',
        tintColor: '#C40006D3',
        backgroundColor: '#FFFFFF',
      },
    },
    title: 'BTC',
    subtitle: '30.00 BTC',
    trailing: [
      {
        kind: 'valuePair',
        primary: '$902,617.17',
        secondary: '+4.32%',
        secondaryTone: 'positive',
      },
    ],
  },
  {
    type: 'identity',
    key: 'gallery-token-eth',
    leading: {
      ...tokenVisual('ethereum', 'ETH'),
      shape: 'rounded',
      cornerIcon: {
        name: 'QuestionmarkSolid',
        tintColor: '#9E6C00',
        backgroundColor: '#FFFFFF',
      },
    },
    title: 'Ethereum',
    subtitle: '2.35 ETH',
    trailing: [
      {
        kind: 'valuePair',
        primary: '$3,836.97',
        secondary: '+4.32%',
        secondaryTone: 'positive',
      },
    ],
  },
  {
    type: 'identity',
    key: 'gallery-token-polygon',
    leading: { ...tokenVisual('polygon', 'MATIC'), shape: 'rounded' },
    title: 'Polygon',
    subtitle: '2.35 Matic',
    trailing: [
      {
        kind: 'valuePair',
        primary: '$10421.23',
        secondary: '-4.32%',
        secondaryTone: 'negative',
      },
    ],
  },
];

const networkIdentity = ({
  key,
  name,
  sectionKey,
  groupPosition,
  disabled,
  loading,
  value,
}: {
  key: string;
  name: string;
  sectionKey: string;
  groupPosition: 'first' | 'middle' | 'last' | 'single';
  disabled?: boolean;
  loading?: boolean;
  value?: string;
}): RowModel => ({
  type: 'identity',
  key,
  sectionKey,
  groupId: `network-${sectionKey}`,
  groupPosition,
  disabled,
  leading: {
    kind: 'network',
    image: networkImages[name as keyof typeof networkImages],
    fallbackText: name.slice(0, 2).toUpperCase(),
    backgroundColor: '#0000000F',
  },
  size: 'small',
  title: name,
  trailing: [
    ...(value ? ([{ kind: 'value' as const, text: value }] as const) : []),
    {
      kind: 'checkbox',
      state: 'unchecked',
      disabled,
      loading,
      target: { scope: 'row' },
    },
  ],
});

const networkSummaryHeader = (
  selectedCount: number,
  totalCount: number,
): RowModel => ({
  type: 'sectionHeader',
  key: 'network-list-header',
  sectionKey: 'network-list',
  variant: 'summary',
  title: `${selectedCount} networks selected`,
  value: selectedCount === totalCount ? 'Deselect all' : 'Select all',
  valueActionKey: 'selection.all',
});

function sectionedNetworkRows(
  selectedCount: number,
  includeListHeader: boolean,
): readonly RowModel[] {
  return [
    ...(includeListHeader ? [networkSummaryHeader(selectedCount, 18)] : []),
    {
      type: 'sectionHeader',
      key: 'network-assets-header',
      sectionKey: 'assets',
      title: 'Networks with assets',
      value: '$20.71',
      checkbox: {
        kind: 'checkbox',
        state: 'unchecked',
        target: { scope: 'section', sectionKey: 'assets' },
      },
    },
    networkIdentity({
      key: 'network-arbitrum',
      name: 'Arbitrum',
      sectionKey: 'assets',
      groupPosition: 'first',
      value: '$5.72',
    }),
    networkIdentity({
      key: 'network-linea',
      name: 'Linea',
      sectionKey: 'assets',
      groupPosition: 'middle',
      value: '$3.32',
    }),
    networkIdentity({
      key: 'network-optimism',
      name: 'Optimism',
      sectionKey: 'assets',
      groupPosition: 'middle',
      value: '$3.16',
    }),
    networkIdentity({
      key: 'network-solana',
      name: 'Solana',
      sectionKey: 'assets',
      groupPosition: 'middle',
      value: '$2.93',
    }),
    networkIdentity({
      key: 'network-ethereum',
      name: 'Ethereum',
      sectionKey: 'assets',
      groupPosition: 'middle',
      value: '$2.52',
    }),
    networkIdentity({
      key: 'network-bitcoin',
      name: 'Bitcoin',
      sectionKey: 'assets',
      groupPosition: 'middle',
      value: '$1.91',
    }),
    networkIdentity({
      key: 'network-base',
      name: 'Base',
      sectionKey: 'assets',
      groupPosition: 'last',
      value: '$1.14',
    }),
    {
      type: 'sectionHeader',
      key: 'network-a-header',
      sectionKey: 'a',
      indexTitle: 'A',
      title: 'A',
    },
    networkIdentity({
      key: 'network-avalanche',
      name: 'Avalanche',
      sectionKey: 'a',
      groupPosition: 'single',
    }),
    {
      type: 'sectionHeader',
      key: 'network-b-header',
      sectionKey: 'b',
      indexTitle: 'B',
      title: 'B',
    },
    networkIdentity({
      key: 'network-bnb-chain',
      name: 'BNB Chain',
      sectionKey: 'b',
      groupPosition: 'first',
    }),
    networkIdentity({
      key: 'network-blast',
      name: 'Blast',
      sectionKey: 'b',
      groupPosition: 'middle',
    }),
    networkIdentity({
      key: 'network-bitlayer',
      name: 'Bitlayer',
      sectionKey: 'b',
      groupPosition: 'middle',
    }),
    networkIdentity({
      key: 'network-bob',
      name: 'BOB',
      sectionKey: 'b',
      groupPosition: 'last',
    }),
    {
      type: 'sectionHeader',
      key: 'network-c-header',
      sectionKey: 'c',
      indexTitle: 'C',
      title: 'C',
    },
    networkIdentity({
      key: 'network-cronos',
      name: 'Cronos',
      sectionKey: 'c',
      groupPosition: 'single',
    }),
    {
      type: 'sectionHeader',
      key: 'network-m-header',
      sectionKey: 'm',
      indexTitle: 'M',
      title: 'M',
    },
    networkIdentity({
      key: 'network-mantle',
      name: 'Mantle',
      sectionKey: 'm',
      groupPosition: 'single',
    }),
    {
      type: 'sectionHeader',
      key: 'network-p-header',
      sectionKey: 'p',
      indexTitle: 'P',
      title: 'P',
    },
    networkIdentity({
      key: 'network-polygon',
      name: 'Polygon',
      sectionKey: 'p',
      groupPosition: 'first',
    }),
    networkIdentity({
      key: 'network-pulse-chain',
      name: 'PulseChain',
      sectionKey: 'p',
      groupPosition: 'last',
    }),
    {
      type: 'sectionHeader',
      key: 'network-s-header',
      sectionKey: 's',
      indexTitle: 'S',
      title: 'S',
    },
    networkIdentity({
      key: 'network-scroll',
      name: 'Scroll',
      sectionKey: 's',
      groupPosition: 'single',
    }),
    {
      type: 'sectionHeader',
      key: 'network-t-header',
      sectionKey: 't',
      indexTitle: 'T',
      title: 'T',
    },
    networkIdentity({
      key: 'network-tron',
      name: 'Tron',
      sectionKey: 't',
      groupPosition: 'single',
    }),
  ];
}

function checkboxNetworkRows(selectedCount: number): readonly RowModel[] {
  return sectionedNetworkRows(selectedCount, true);
}

const nftGalleryData = [
  {
    image:
      'https://images.glow.app/https%3A%2F%2Farweave.net%2F0WFtaZrc_DUzL2Tt_zztq-9cfJoSDhDacSfrPT50HOo%3Fext%3Dpng?ixlib=js-3.8.0&w=80&h=80&dpr=2&fit=crop&s=7af3b8e6a74c4abc0ab9de93ca67d1c4',
    title: 'Critter Ywin',
    subtitle: 'Hyperspace · 6/27/23, 7:19 AM',
    amount: '3.186 SOL',
    value: '$52.82',
    networkImage: tokenImages.bitcoin,
  },
  {
    image:
      'https://images.glow.app/https%3A%2F%2Farweave.net%2FhRZG2ePVGpBSogaNSdp4Jm3vUILhvB-h3gB7-nRrPsE%3Fext%3Dpng?ixlib=js-3.8.0&w=80&h=80&dpr=2&fit=crop&s=cbd0b1bc0ab5d8b867930546c5e87358',
    title: 'Critter Yore',
    subtitle: 'Magic Eden · 5/23/23, 6:40 PM',
    amount: '3.186 SOL',
    value: '$52.82',
    networkImage: tokenImages.ethereum,
  },
  {
    image:
      'https://images.glow.app/https%3A%2F%2Farweave.net%2F99eb109nC2JgMA5GHpW0GK8TdidO8lm5eDj0FgzfWdA%3Fext%3Dpng?ixlib=js-3.8.0&w=80&h=80&dpr=2&fit=crop&s=2ff9b1faad864bf338d0b881051f6c16',
    title: 'Critter Osar',
    subtitle: 'Magic Eden · 5/22/23, 1:33 PM',
    amount: '3.186 SOL',
    value: '$52.82',
    networkImage: tokenImages.polygon,
  },
  {
    image: '',
    title: 'Critter Osa',
    subtitle: 'Magic Eden · 5/22/23, 1:33 PM',
    amount: '3.186 SOL',
    value: '$52.82',
    networkImage: undefined,
  },
] as const;

const galleryNftIdentityRows: readonly RowModel[] = nftGalleryData.map(
  item => ({
    type: 'identity' as const,
    key: `gallery-${item.title}`,
    leading: {
      kind: 'token' as const,
      image: item.image ? image(item.image) : undefined,
      networkImage: item.networkImage,
      fallbackText: 'NFT',
      shape: 'rounded' as const,
    },
    title: item.title,
    subtitle: item.subtitle,
    subtitleLines: 1 as const,
    trailing: [
      {
        kind: 'valuePair' as const,
        primary: item.amount,
        secondary: item.value,
      },
    ],
  }),
);

const nftRows: readonly RowModel[] = [
  {
    type: 'mediaTile',
    key: 'nft-lido-withdrawal-1',
    variant: 'gallery',
    imageState: 'empty',
    networkImage: ethereumCorner,
    title: 'Lido Withdrawal NFT...',
    subtitle: 'Lido: stETH Withdrawal NFT',
  },
  {
    type: 'mediaTile',
    key: 'nft-uniswap-v3',
    variant: 'gallery',
    imageState: 'empty',
    networkImage: ethereumCorner,
    title: 'Uniswap - 0.01% - US...',
    subtitle: 'Uniswap V3 Positions NFT...',
  },
  {
    type: 'mediaTile',
    key: 'nft-uniswap-v4',
    variant: 'gallery',
    imageState: 'empty',
    networkImage: ethereumCorner,
    title: 'Uniswap - 0.3% - USD...',
    subtitle: 'Uniswap v4 Positions NFT',
  },
  {
    type: 'mediaTile',
    key: 'nft-lido-withdrawal-2',
    variant: 'gallery',
    imageState: 'error',
    networkImage: ethereumCorner,
    title: '-',
    subtitle: 'Lido: stETH Withdrawal NFT',
  },
];

const hyperliquidVisual = (symbol: string, size = 32) => ({
  kind: 'token' as const,
  image: image(
    symbol === 'HYPE'
      ? hyperliquidHypeImageUri
      : `https://uni.onekey-asset.com/static/hyperliquid/${symbol}.png`,
    size,
    size,
  ),
  fallbackText: symbol,
  backgroundColor: '#FFFFFF',
});

const marketRows: readonly RowModel[] = [
  ['BTC', 'Bitcoin', '$4.35B', '80,922.00', '+4.45%', '40x', ''],
  ['ETH', 'Ethereum', '$1.27B', '2,510.80', '+4.80%', '25x', ''],
  ['HYPE', 'Hyperliquid', '$711.75M', '86.06', '+5.03%', '10x', ''],
  ['ZEC', 'Zcash', '$399M', '955.43', '+16.04%', '10x', ''],
  ['CL', 'Crude Oil', '$350.1M', '91.38', '+1.51%', '20x', 'xyz'],
  ['SOL', 'Solana', '$307.71M', '103.70', '+3.29%', '20x', ''],
  ['SKHX', 'SK Hynix', '$260.46M', '1,231.60', '+5.23%', '10x', 'xyz'],
  ['SPCX', 'SpaceX', '$218.6M', '150.64', '+7.19%', '20x', 'xyz'],
  ['SP500', '', '$213.88M', '7,753.10', '+1.19%', '50x', 'xyz'],
].map(values => ({
  type: 'dataRow' as const,
  key: `market-${values[0]}`,
  leading: hyperliquidVisual(`${values[6] ?? ''}${values[0] ?? ''}`),
  favorite: true,
  badges: [
    { key: 'leverage', text: values[5] ?? '', tone: 'info' as const },
    ...(values[6]
      ? [{ key: 'venue', text: values[6], tone: 'info' as const }]
      : []),
  ],
  columns: [
    {
      key: 'asset',
      text: values[0] ?? '',
      secondaryLeadingText: values[1] ?? '',
      secondaryText: values[2] ?? '',
      weight: 3 as const,
    },
    {
      key: 'price',
      text: values[3] ?? '',
      secondaryText: values[4] ?? '',
      secondaryTone: 'positive' as const,
      weight: 2 as const,
      alignment: 'end' as const,
    },
  ],
}));

const historyReceiveRows: readonly RowModel[] = [0, 1].map(index => ({
  type: 'activity' as const,
  key: `activity-receive-usdc-${index}`,
  sectionKey: 'history-2026-09-03',
  leading: {
    ...tokenVisual('usdc', 'USDC'),
    networkImage: networkImages.Base,
    shape: 'rounded' as const,
  },
  title: 'Receive',
  description: '0x21e2ea...f90622',
  primaryAmount: '+0.1 USDC',
  secondaryAmount: '$0.10',
}));

const notificationRows: readonly RowModel[] = [
  {
    type: 'message',
    key: 'notification-sent-usdg',
    unread: true,
    leading: { kind: 'icon', name: 'ArrowTopOutline' },
    title: 'Sent',
    body: 'Wallet 2 / EVM #1 0x5618...b4b7 sent 0.012 USDG',
    bodyLines: 3,
    time: '1 day ago',
  },
];

const railRows: readonly RowModel[] = [
  ['BTC', '80,922.00', 'success', 'BTC'],
  ['ETH', '2,510.80', 'success', 'ETH'],
  ['HYPE', '86.06', 'success', 'HYPE'],
  ['ZEC', '955.43', 'success', 'ZEC'],
  ['SOL', '103.70', 'success', 'SOL'],
  ['NVDA', '230.27', 'success', 'xyzNVDA'],
].map(item => ({
  type: 'rail' as const,
  key: `favorite-${item[0]}`,
  visual: hyperliquidVisual(item[3] ?? ''),
  title: item[0] ?? '',
  badge: {
    key: 'price',
    text: item[1] ?? '-',
    tone: item[2] as 'success' | 'danger',
  },
  draggable: true,
}));

const metricRows: readonly RowModel[] = [
  {
    type: 'metricCard',
    key: 'metric-activity',
    variant: 'activity',
    title: 'ACTIVITY',
    value: '',
    metrics: [
      { key: 'volume', label: 'Volume', value: '$5.13K' },
      {
        key: 'most-traded',
        label: 'Most Traded',
        value: 'BTC',
        visual: hyperliquidVisual('BTC', 16),
      },
      { key: 'fees-paid', label: 'Fees Paid', value: '$1.50' },
      { key: 'net-deposits', label: 'Net Deposits', value: '$13.66' },
      { key: 'total-trades', label: 'Total Trades', value: '3' },
    ],
  },
  {
    type: 'metricCard',
    key: 'metric-performance',
    variant: 'performance',
    title: 'PERFORMANCE',
    value: '',
    progress: 0.5,
    metrics: [
      {
        key: 'win-rate',
        label: 'Win Rate',
        value: '50.0%',
        tone: 'positive',
      },
      { key: 'profit-factor', label: 'Profit Factor', value: '4.00' },
      { key: 'avg-win', label: 'Avg Win', value: '$20.00' },
      { key: 'avg-loss', label: 'Avg Loss', value: '-$5.00' },
    ],
  },
];

const actionRows: readonly RowModel[] = [
  {
    type: 'sectionHeader',
    key: 'action-token-manager-header',
    sectionKey: 'action-token-manager',
    variant: 'gallery',
    title: 'Token Manager',
  },
  {
    type: 'action',
    key: 'action-custom-token',
    title: 'Custom token',
    actionKey: 'token.custom',
    tone: 'neutral',
    trailing: [{ kind: 'chevron', actionKey: 'token.custom' }],
  },
  {
    type: 'sectionHeader',
    key: 'action-token-section-header',
    sectionKey: 'action-tokens',
    title: 'Tokens',
  },
  ...tokenManagerRows,
  { type: 'system', key: 'action-section-gap', variant: 'spacer', height: 64 },
  {
    type: 'sectionHeader',
    key: 'action-bookmark-header',
    sectionKey: 'action-bookmarks',
    variant: 'gallery',
    title: 'Bookmark editing',
  },
  {
    type: 'identity',
    key: 'bookmark-onekey',
    leadingAction: {
      kind: 'icon',
      name: 'MinusCircleSolid',
      tintColor: '#C40006D3',
      actionKey: 'bookmark.remove',
    },
    leading: {
      kind: 'image',
      image: image('https://onekey.so/favicon.ico'),
      shape: 'rounded',
    },
    title: 'OneKey',
    subtitle: 'https://onekey.so',
    trailing: [
      { kind: 'icon', name: 'PencilOutline', actionKey: 'bookmark.rename' },
      { kind: 'icon', name: 'DragOutline', actionKey: 'bookmark.drag' },
    ],
  },
  {
    type: 'identity',
    key: 'bookmark-dydx',
    leadingAction: {
      kind: 'icon',
      name: 'MinusCircleSolid',
      tintColor: '#C40006D3',
      actionKey: 'bookmark.remove',
    },
    leading: {
      kind: 'image',
      image: dydxBookmarkLogo,
      shape: 'rounded',
    },
    title: 'dYdX',
    subtitle: 'https://dydx.trade',
    trailing: [
      { kind: 'icon', name: 'PencilOutline', actionKey: 'bookmark.rename' },
      { kind: 'icon', name: 'DragOutline', actionKey: 'bookmark.drag' },
    ],
  },
];

const systemRows: readonly RowModel[] = [
  { type: 'system', key: 'system-loading-1', variant: 'loading' },
  { type: 'system', key: 'system-loading-2', variant: 'loading' },
  { type: 'system', key: 'system-loading-3', variant: 'loading' },
  { type: 'system', key: 'system-loading-4', variant: 'loading' },
  { type: 'system', key: 'system-loading-5', variant: 'loading' },
  {
    type: 'system',
    key: 'system-no-match',
    variant: 'noMatch',
    message: 'No matching tokens found on Ethereum',
  },
  { type: 'system', key: 'system-end', variant: 'end' },
  {
    type: 'system',
    key: 'system-retry',
    variant: 'retry',
    message: 'Search failed for other networks',
    actionKey: 'system.retry',
  },
];

const initialSelectedKeys: Partial<Record<NativeListExampleKey, string[]>> = {
  'container-sectioned': [
    'network-arbitrum',
    'network-linea',
    'network-optimism',
    'network-solana',
    'network-ethereum',
    'network-bitcoin',
    'network-base',
    'network-avalanche',
    'network-bnb-chain',
    'network-blast',
    'network-bitlayer',
    'network-bob',
    'network-cronos',
    'network-mantle',
    'network-polygon',
    'network-pulse-chain',
    'network-scroll',
    'network-tron',
  ],
  'row-section-header': ['network-ethereum'],
  checkbox: [
    'network-arbitrum',
    'network-linea',
    'network-optimism',
    'network-solana',
    'network-ethereum',
    'network-bitcoin',
    'network-base',
    'network-avalanche',
    'network-bnb-chain',
    'network-blast',
    'network-bitlayer',
    'network-bob',
    'network-cronos',
    'network-mantle',
    'network-polygon',
    'network-pulse-chain',
    'network-scroll',
    'network-tron',
  ],
};

const sectionedSelectableNetworkKeys = [
  'network-arbitrum',
  'network-linea',
  'network-optimism',
  'network-solana',
  'network-ethereum',
  'network-bitcoin',
  'network-base',
  'network-avalanche',
  'network-bnb-chain',
  'network-blast',
  'network-bitlayer',
  'network-bob',
  'network-cronos',
  'network-mantle',
  'network-polygon',
  'network-pulse-chain',
  'network-scroll',
  'network-tron',
] as const;

function snapshotFor({
  example,
  selectedKeys,
  currentRailRows,
  currentMediaRows,
}: {
  example: NativeListExampleKey;
  selectedKeys: readonly string[];
  currentRailRows: readonly RowModel[];
  currentMediaRows: readonly RowModel[];
}): NativeListSnapshot {
  const generation =
    nativeListExamples.findIndex(item => item.key === example) + 1;
  const base = {
    schemaVersion: 1 as const,
    generation,
    theme: demoTheme,
  };

  switch (example) {
    case 'container-linear':
      return {
        ...base,
        layout: {
          kind: 'linear',
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: [
          {
            type: 'action',
            key: 'linear-custom-token',
            title: 'Custom token',
            actionKey: 'token.custom',
            tone: 'neutral',
            trailing: [{ kind: 'chevron', actionKey: 'token.custom' }],
          },
          {
            type: 'sectionHeader',
            key: 'linear-token-section-header',
            sectionKey: 'linear-tokens',
            title: 'Tokens',
          },
          ...tokenManagerRows,
        ],
        capabilities: {
          pullToRefresh: true,
          loadMore: true,
          endReachedThreshold: 0.15,
        },
      };
    case 'container-sectioned':
      return {
        ...base,
        theme: { ...demoTheme, rowSelectedBackground: '#FFFFFF' },
        layout: {
          kind: 'sectioned',
          stickyHeaders: true,
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: sectionedNetworkRows(selectedKeys.length, true),
        selection: {
          mode: 'multiple',
          selectedKeys,
          rowPressToggles: false,
        },
        capabilities: {
          sectionIndex: { enabled: true },
        },
      };
    case 'checkbox':
      return {
        ...base,
        layout: {
          kind: 'sectioned',
          stickyHeaders: true,
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: checkboxNetworkRows(selectedKeys.length),
        selection: {
          mode: 'multiple',
          selectedKeys,
          rowPressToggles: false,
        },
      };
    case 'container-grid':
      return {
        ...base,
        layout: {
          kind: 'grid',
          gridColumns: 2,
          contentPadding: 10,
          contentPaddingHorizontal: 10,
          contentPaddingTop: 12,
          contentPaddingBottom: 24,
          itemSpacing: 0,
        },
        rows: nftRows,
      };
    case 'container-table':
    case 'row-data':
      return {
        ...base,
        theme: {
          ...demoTheme,
          subduedBackground: '#FFFFFF',
          rowPressedBackground: '#00000017',
        },
        layout: { kind: 'table', contentPadding: 0, itemSpacing: 0 },
        rows: [
          {
            type: 'sectionHeader',
            key: `${example}-market-header`,
            sectionKey: 'markets',
            title: 'Asset / Volume',
            value: 'Last price / 24h Change',
            titleIcon: {
              kind: 'icon',
              name: 'ChevronBottomOutline',
              tintColor: '#000000DF',
            },
          },
          ...marketRows,
        ],
      };
    case 'row-identity':
      return {
        ...base,
        layout: {
          kind: 'linear',
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: [
          {
            type: 'sectionHeader',
            key: 'gallery-token-header',
            sectionKey: 'gallery-token',
            variant: 'gallery',
            title: 'Token',
          },
          ...galleryTokenRows,
          {
            type: 'system',
            key: 'gallery-section-gap',
            variant: 'spacer',
            height: 64,
          },
          {
            type: 'sectionHeader',
            key: 'gallery-nft-header',
            sectionKey: 'gallery-nft',
            variant: 'gallery',
            title: 'NFT',
          },
          ...galleryNftIdentityRows,
        ],
      };
    case 'row-rail':
      return {
        ...base,
        layout: {
          kind: 'linear',
          orientation: 'horizontal',
          contentPadding: 0,
          itemSpacing: 4,
        },
        rows: currentRailRows,
        selection: {
          mode: 'single',
          selectedKeys,
          rowPressToggles: true,
        },
        capabilities: { reorderable: true },
      };
    case 'row-activity':
      return {
        ...base,
        layout: {
          kind: 'linear',
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: [
          {
            type: 'sectionHeader',
            key: 'history-2026-09-03-header',
            sectionKey: 'history-2026-09-03',
            variant: 'history',
            title: '09/03/2026',
          },
          ...historyReceiveRows,
        ],
      };
    case 'row-message':
      return {
        ...base,
        layout: {
          kind: 'linear',
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: notificationRows,
      };
    case 'row-media':
      return {
        ...base,
        layout: {
          kind: 'grid',
          gridColumns: 2,
          contentPadding: 10,
          contentPaddingHorizontal: 10,
          contentPaddingTop: 12,
          contentPaddingBottom: 24,
          itemSpacing: 0,
        },
        rows: currentMediaRows,
      };
    case 'row-metric':
      return {
        ...base,
        layout: {
          kind: 'linear',
          contentPadding: 20,
          contentPaddingHorizontal: 20,
          contentPaddingTop: 0,
          contentPaddingBottom: 20,
          itemSpacing: 16,
        },
        rows: metricRows,
      };
    case 'row-section-header':
      return {
        ...base,
        layout: {
          kind: 'sectioned',
          stickyHeaders: true,
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: [
          ...sectionedNetworkRows(selectedKeys.length, false),
          {
            type: 'sectionHeader',
            key: 'history-2026-09-03-header',
            sectionKey: 'history-2026-09-03',
            variant: 'history',
            title: '09/03/2026',
          },
          ...historyReceiveRows,
        ],
        selection: {
          mode: 'multiple',
          selectedKeys,
          rowPressToggles: false,
        },
      };
    case 'row-action':
      return {
        ...base,
        layout: {
          kind: 'linear',
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: actionRows,
      };
    case 'row-system':
      return {
        ...base,
        layout: {
          kind: 'linear',
          contentPaddingHorizontal: 8,
          contentPaddingTop: 0,
          contentPaddingBottom: 0,
          itemSpacing: 0,
        },
        rows: systemRows,
      };
  }
}

type Props = NativeStackScreenProps<RootStackParamList, 'NativeListExample'>;

export function NativeListExamplePage({ route }: Props) {
  const example = route.params?.example ?? nativeListExamples[0].key;
  const metadata =
    nativeListExamples.find(item => item.key === example) ??
    nativeListExamples[0];
  const listRef = useRef<NativeListRef>(null);
  const [selectedKeys, setSelectedKeys] = useState<readonly string[]>(
    initialSelectedKeys[example] ?? [],
  );
  const [currentRailRows, setCurrentRailRows] =
    useState<readonly RowModel[]>(railRows);
  const currentMediaRows = nftRows;
  const [status, setStatus] = useState('Ready');
  const snapshot = useMemo(
    () =>
      snapshotFor({
        example,
        selectedKeys,
        currentRailRows,
        currentMediaRows,
      }),
    [currentMediaRows, currentRailRows, example, selectedKeys],
  );

  const handleSelection = (event: SelectionDeltaEvent) => {
    setSelectedKeys(current => {
      const next = new Set(current);
      event.removedKeys.forEach(key => next.delete(key));
      event.addedKeys.forEach(key => next.add(key));
      return [...next];
    });
    setStatus(
      `${event.source}: +${event.addedKeys.length} / -${event.removedKeys.length}`,
    );
  };

  const handleReorder = (event: ReorderEvent) => {
    setCurrentRailRows(current => {
      const next = [...current];
      const [moved] = next.splice(event.fromIndex, 1);
      if (moved) next.splice(event.toIndex, 0, moved);
      return next;
    });
    setStatus(`${event.key}: ${event.fromIndex} → ${event.toIndex}`);
  };

  return (
    <View style={styles.container}>
      <View style={styles.sourcePanel}>
        <Text style={styles.sourceLabel}>Source</Text>
        <Text style={styles.sourceText} selectable>
          {metadata?.source}
        </Text>
        <Text style={styles.statusText}>{status}</Text>
      </View>
      <NativeList
        ref={listRef}
        testID={`native-list-${example}`}
        style={styles.list}
        snapshot={snapshot}
        onRefresh={() => {
          setStatus('Refreshed');
          listRef.current?.setRefreshing(false);
        }}
        onSelectionDelta={handleSelection}
        onReorder={handleReorder}
        onRowAction={event => {
          if (
            (example === 'container-sectioned' || example === 'checkbox') &&
            event.actionKey === 'selection.all'
          ) {
            setSelectedKeys(current => {
              const selected = new Set(current);
              const allSelected = sectionedSelectableNetworkKeys.every(key =>
                selected.has(key),
              );
              return allSelected ? [] : sectionedSelectableNetworkKeys;
            });
          }
          setStatus(`${event.rowKey ?? 'list'} / ${event.actionKey}`);
        }}
        onEndReached={event =>
          setStatus(`End reached · generation ${event.generation}`)
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#FFFFFF' },
  sourcePanel: {
    backgroundColor: '#FFFFFF',
    borderBottomColor: '#E0E0E0',
    borderBottomWidth: StyleSheet.hairlineWidth,
    paddingHorizontal: 14,
    paddingVertical: 9,
  },
  sourceLabel: {
    color: '#8D8D8D',
    fontSize: 11,
    fontWeight: '500',
    textTransform: 'uppercase',
  },
  sourceText: { color: '#646464', fontSize: 12, lineHeight: 16, marginTop: 2 },
  statusText: { color: '#8D8D8D', fontSize: 11, marginTop: 4 },
  list: { flex: 1 },
});
