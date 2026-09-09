// SVG paths copied from app-monorepo 551eb38fc1ec, packages/components/src/primitives/Icon/react.
// Bundled SVG files use the existing native file decoder on both platforms.
export const MARKET_SOURCE_ICONS = {
  search: {
    dark: require('./marketNativePagerAssets/search-dark.svg'),
    light: require('./marketNativePagerAssets/search-light.svg'),
  },
  pencil: {
    dark: require('./marketNativePagerAssets/pencil-dark.svg'),
    light: require('./marketNativePagerAssets/pencil-light.svg'),
  },
  chevronDown: {
    dark: require('./marketNativePagerAssets/chevronDown-dark.svg'),
    light: require('./marketNativePagerAssets/chevronDown-light.svg'),
  },
  check: {
    dark: require('./marketNativePagerAssets/check-dark.svg'),
    light: require('./marketNativePagerAssets/check-light.svg'),
  },
  allNetworks: {
    dark: require('./marketNativePagerAssets/allNetworks-dark.svg'),
    light: require('./marketNativePagerAssets/allNetworks-light.svg'),
  },
  close: {
    dark: require('./marketNativePagerAssets/close-dark.svg'),
    light: require('./marketNativePagerAssets/close-light.svg'),
  },
  checkRadio: {
    dark: require('./marketNativePagerAssets/checkRadio-dark.svg'),
    light: require('./marketNativePagerAssets/checkRadio-light.svg'),
  },
  noResults: {
    dark: require('./marketNativePagerAssets/noResults-dark.svg'),
    light: require('./marketNativePagerAssets/noResults-light.svg'),
  },
  alignTop: {
    dark: require('./marketNativePagerAssets/alignTop-dark.svg'),
    light: require('./marketNativePagerAssets/alignTop-light.svg'),
  },
  star: {
    dark: require('./marketNativePagerAssets/star-dark.svg'),
    light: require('./marketNativePagerAssets/star-light.svg'),
  },
  recognized: {
    dark: require('./marketNativePagerAssets/recognized-dark.svg'),
    light: require('./marketNativePagerAssets/recognized-light.svg'),
  },
  statuspreMarket: {
    dark: require('./marketNativePagerAssets/status-preMarket-dark.svg'),
    light: require('./marketNativePagerAssets/status-preMarket-light.svg'),
  },
  statusopen: {
    dark: require('./marketNativePagerAssets/status-open-dark.svg'),
    light: require('./marketNativePagerAssets/status-open-light.svg'),
  },
  statuspostMarket: {
    dark: require('./marketNativePagerAssets/status-postMarket-dark.svg'),
    light: require('./marketNativePagerAssets/status-postMarket-light.svg'),
  },
  statusovernight: {
    dark: require('./marketNativePagerAssets/status-overnight-dark.svg'),
    light: require('./marketNativePagerAssets/status-overnight-light.svg'),
  },
  statusclosed: {
    dark: require('./marketNativePagerAssets/status-closed-dark.svg'),
    light: require('./marketNativePagerAssets/status-closed-light.svg'),
  },
  statusopen247: {
    dark: require('./marketNativePagerAssets/status-open247-dark.svg'),
    light: require('./marketNativePagerAssets/status-open247-light.svg'),
  },
  statusawaitingOpen: {
    dark: require('./marketNativePagerAssets/status-awaitingOpen-dark.svg'),
    light: require('./marketNativePagerAssets/status-awaitingOpen-light.svg'),
  },
  statushalted: {
    dark: require('./marketNativePagerAssets/status-halted-dark.svg'),
    light: require('./marketNativePagerAssets/status-halted-light.svg'),
  },
} as const;

// Original locale strings from app-monorepo, kept verbatim.
export const MARKET_SOURCE_COPY = {
  'zh-CN': {
    community: '较高社区认可度',
    ondo: '由 Ondo Finance 代币化发行',
    xstock: '由 xStock 代币化发行',
    tag: '标签',
    addTokens: '添加 {number} 个代币',
    xyz: 'xyz 表示来自 Hyperliquid 上 xyz HIP-3 DEX 的市场，涵盖股票、大宗商品等资产类别。',
    para: 'para 表示来自 Hyperliquid 上 Paragon HIP-3 DEX 的市场，涵盖股票、加密指数和新兴主题。',
    io: 'io 表示来自 Hyperliquid 上 Entropy HIP-3 DEX 的市场，涵盖 Pre-IPO AI 公司和上市科技股。',
  },
  'en-US': {
    community: 'Community-recognized',
    ondo: 'Tokenized by Ondo Finance',
    xstock: 'Tokenized by xStock',
    tag: 'Tag',
    addTokens: 'Add {number} tokens',
    xyz: 'xyz identifies markets from the xyz HIP-3 DEX on Hyperliquid, including equities, commodities, and other asset classes.',
    para: 'para identifies markets from the Paragon HIP-3 DEX on Hyperliquid, including equities, crypto indices, and emerging themes.',
    io: 'io identifies markets from the Entropy HIP-3 DEX on Hyperliquid, including pre-IPO AI companies and listed technology equities.',
  },
} as const;

// app-monorepo presetNetworks entries absent from the captured Market filter config.
export const MARKET_SOURCE_NETWORK_LOGOS: Readonly<Record<string, string>> = {
  'btc--0': 'https://uni.onekey-asset.com/static/chain/btc.png',
  'tron--0x2b6653dc': 'https://uni.onekey-asset.com/static/chain/tron.png',
};

// StockIsOpenBadge tokens, strings and icons from the same source revision.
export const MARKET_SOURCE_STATUS_CHIPS = {
  preMarket: {
    text: {
      'en-US': 'Pre-market',
      'zh-CN': '盘前',
    },
    dark: {
      backgroundColor: '#ffaa001e',
      color: '#fee949f5',
    },
    light: {
      backgroundColor: '#ffee0047',
      color: '#9e6c00',
    },
  },
  open: {
    text: {
      'en-US': 'Regular market',
      'zh-CN': '盘中',
    },
    dark: {
      backgroundColor: '#22ff991e',
      color: '#46fea5d4',
    },
    light: {
      backgroundColor: '#00a43319',
      color: '#00713fde',
    },
  },
  postMarket: {
    text: {
      'en-US': 'Post-market',
      'zh-CN': '盘后',
    },
    dark: {
      backgroundColor: '#ffaa001e',
      color: '#fee949f5',
    },
    light: {
      backgroundColor: '#ffee0047',
      color: '#9e6c00',
    },
  },
  overnight: {
    text: {
      'en-US': 'Overnight',
      'zh-CN': '隔夜',
    },
    dark: {
      backgroundColor: '#0077ff3a',
      color: '#70b8ff',
    },
    light: {
      backgroundColor: '#008ff519',
      color: '#006dcbf2',
    },
  },
  closed: {
    text: {
      'en-US': 'Closed',
      'zh-CN': '休市',
    },
    dark: {
      backgroundColor: '#ffffff12',
      color: '#ffffffaf',
    },
    light: {
      backgroundColor: '#0000000f',
      color: '#0000009b',
    },
  },
  open247: {
    text: {
      'en-US': '24/7',
      'zh-CN': '24/7',
    },
    dark: {
      backgroundColor: '#ffffff12',
      color: '#ffffffaf',
    },
    light: {
      backgroundColor: '#0000000f',
      color: '#0000009b',
    },
  },
  awaitingOpen: {
    text: {
      'en-US': 'Awaiting Open',
      'zh-CN': '待开盘',
    },
    dark: {
      backgroundColor: '#ffaa001e',
      color: '#fee949f5',
    },
    light: {
      backgroundColor: '#ffee0047',
      color: '#9e6c00',
    },
  },
  halted: {
    text: {
      'en-US': 'Halted',
      'zh-CN': '暂停',
    },
    dark: {
      backgroundColor: '#ff173f2d',
      color: '#ff9592',
    },
    light: {
      backgroundColor: '#f3000d14',
      color: '#c40006d3',
    },
  },
} as const;
