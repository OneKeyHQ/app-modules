import type { IdentityRow } from '@onekeyfe/react-native-native-list';

export const TOKEN_SELECTOR_SOURCE_REF =
  'app-monorepo@8953599fc55dd2db0f38ad452bf4f9bc6573b80f /swap/v1/tokens?protocol=Swap&networkId=evm--1&limit=50 captured 2026-09-05' as const;

export const ETHEREUM_NETWORK_LOGO =
  'https://uni.onekey-asset.com/static/chain/eth.png';

export type TokenSelectorToken = Readonly<{
  networkId: string;
  contractAddress: string;
  name: string;
  symbol: string;
  logoURI: string;
  price: string;
  balance: string;
  fiatValue: string;
  isDeFi?: boolean;
}>;

const screenshotTokens: readonly TokenSelectorToken[] = [
  {
    networkId: 'evm--1',
    contractAddress: '',
    name: 'Ethereum',
    symbol: 'ETH',
    logoURI:
      'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address--1751363512633.png',
    price: '2455.27',
    balance: '0.0002076',
    fiatValue: '$0.51',
  },
  {
    networkId: 'evm--1',
    contractAddress: '0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2',
    name: 'Wrapped Ether',
    symbol: 'WETH',
    logoURI:
      'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2-1720667871986.png',
    price: '2455.27',
    balance: '0.000199',
    fiatValue: '$0.49',
    isDeFi: true,
  },
  {
    networkId: 'evm--1',
    contractAddress: '0x40d16fc0246ad3160ccc09b8d0d3a2cd28ae6c2f',
    name: 'Gho Token',
    symbol: 'GHO',
    logoURI:
      'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0x40d16fc0246ad3160ccc09b8d0d3a2cd28ae6c2f-1755759093264.png',
    price: '0.99917',
    balance: '0.3656',
    fiatValue: '$0.37',
    isDeFi: true,
  },
  {
    networkId: 'evm--1',
    contractAddress: '0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48',
    name: 'USD Coin',
    symbol: 'USDC',
    logoURI:
      'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48-1749190981666.png',
    price: '0.999999',
    balance: '0.28',
    fiatValue: '$0.28',
  },
  {
    networkId: 'evm--1',
    contractAddress: '0xcccc62962d17b8914c62d74ffb843d73b2a3cccc',
    name: 'cap USD',
    symbol: 'cUSD',
    logoURI:
      'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0xcccc62962d17b8914c62d74ffb843d73b2a3cccc-1757666467162.png',
    price: '0.99985',
    balance: '0.2515',
    fiatValue: '$0.25',
    isDeFi: true,
  },
  {
    networkId: 'evm--1',
    contractAddress: '0xdac17f958d2ee523a2206206994597c13d831ec7',
    name: 'Tether USD',
    symbol: 'USDT',
    logoURI:
      'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0xdac17f958d2ee523a2206206994597c13d831ec7-1722246302921.png',
    price: '1',
    balance: '0.2008',
    fiatValue: '$0.20',
  },
  {
    networkId: 'evm--1',
    contractAddress: '0x6982508145454ce325ddbe47a25d4ec3d2311933',
    name: 'Pepe',
    symbol: 'PEPE',
    logoURI:
      'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0x6982508145454ce325ddbe47a25d4ec3d2311933.png',
    price: '0.00000341',
    balance: '29,303.1209',
    fiatValue: '$0.10',
  },
  {
    networkId: 'evm--1',
    contractAddress: '0xdc035d45d973e3ec169d2276ddab16f1e407384f',
    name: 'USDS Stablecoin',
    symbol: 'USDS',
    logoURI:
      'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/address-0xdc035d45d973e3ec169d2276ddab16f1e407384f-1782885649480.png',
    price: '0.999736',
    balance: '0.09497',
    fiatValue: '$0.09',
    isDeFi: true,
  },
];

type BusinessTokenTuple = readonly [
  contractAddress: string,
  name: string,
  symbol: string,
  logoURI: string,
  price: string,
];

const businessTokenTuples: readonly BusinessTokenTuple[] = [
  ['', 'Ethereum', 'ETH', 'address--1751363512633.png', '2455.27'],
  [
    '0xdac17f958d2ee523a2206206994597c13d831ec7',
    'Tether USD',
    'USDT',
    'address-0xdac17f958d2ee523a2206206994597c13d831ec7-1722246302921.png',
    '1',
  ],
  [
    '0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48',
    'USD Coin',
    'USDC',
    'address-0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48-1749190981666.png',
    '0.999999',
  ],
  [
    '0xa35923162c49cf95e6bf26623385eb431ad920d3',
    'Turbo',
    'TURBO',
    'address-0xa35923162c49cf95e6bf26623385eb431ad920d3.png',
    '0.00101836',
  ],
  [
    '0x94a8b4ee5cd64c79d0ee816f467ea73009f51aa0',
    'Realio Network',
    'RIO',
    'address-0x94a8b4ee5cd64c79d0ee816f467ea73009f51aa0-1731123900127.png',
    '0.04847317',
  ],
  [
    '0x62d0a8458ed7719fdaf978fe5929c6d342b0bfce',
    'Beam',
    'BEAM',
    'address-0x62d0a8458ed7719fdaf978fe5929c6d342b0bfce.png',
    '0.00156675',
  ],
  [
    '0xd533a949740bb3306d119cc777fa900ba034cd52',
    'Curve DAO Token',
    'CRV',
    'address-0xd533a949740bb3306d119cc777fa900ba034cd52.png',
    '0.362952',
  ],
  [
    '0x514910771af9ca656af840dff83e8264ecf986ca',
    'ChainLink Token',
    'LINK',
    'address-0x514910771af9ca656af840dff83e8264ecf986ca.png',
    '11.73',
  ],
  [
    '0xb23d80f5fefcddaa212212f028021b41ded428cf',
    'Prime',
    'PRIME',
    'address-0xb23d80f5fefcddaa212212f028021b41ded428cf.png',
    '0.237797',
  ],
  [
    '0x4c1746a800d224393fe2470c70a35717ed4ea5f1',
    'Plume',
    'PLUME',
    'address-0x4c1746a800d224393fe2470c70a35717ed4ea5f1-1757750770655.png',
    '0.01364463',
  ],
  [
    '0x57e114b691db790c35207b2e685d4a43181e6061',
    'ENA',
    'ENA',
    'address-0x57e114b691db790c35207b2e685d4a43181e6061.png',
    '0.163444',
  ],
  [
    '0xda5e1988097297dcdc1f90d4dfe7909e847cbef6',
    'World Liberty Financial',
    'WLFI',
    'address-0xda5e1988097297dcdc1f90d4dfe7909e847cbef6-1756694380364.png',
    '0.056456',
  ],
  [
    '0xa27ec0006e59f245217ff08cd52a7e8b169e62d2',
    'AZTEC',
    'AZTEC',
    'address-0xa27ec0006e59f245217ff08cd52a7e8b169e62d2-1765087768761.png',
    '0.01493665',
  ],
  [
    '0x6985884c4392d348587b19cb9eaaf157f13271cd',
    'LayerZero',
    'ZRO',
    'address-0x6985884c4392d348587b19cb9eaaf157f13271cd-1721275576234.png',
    '1.047',
  ],
  [
    '0x4e3fbd56cd56c3e72c1403e103b45db9da5b9d2b',
    'Convex Token',
    'CVX',
    'address-0x4e3fbd56cd56c3e72c1403e103b45db9da5b9d2b.png',
    '2.24',
  ],
  [
    '0x232ce3bd40fcd6f80f3d55a522d03f25df784ee2',
    'Lighter',
    'LIT',
    'address-0x232ce3bd40fcd6f80f3d55a522d03f25df784ee2-1783322585018.png',
    '4.64',
  ],
  [
    '0xd31a59c85ae9d8edefec411d448f90841571b89c',
    'Wrapped SOL',
    'SOL',
    'address-0xd31a59c85ae9d8edefec411d448f90841571b89c.png',
    '102.43',
  ],
  [
    '0x1258d60b224c0c5cd888d37bbf31aa5fcfb7e870',
    'NodeAI',
    'GPU',
    'address-0x1258d60b224c0c5cd888d37bbf31aa5fcfb7e870.png',
    '0.01010916',
  ],
  [
    '0xa1fa7777974312f7d801a8880714a218f76233f8',
    'USDx',
    'USDx',
    'address-0xa1fa7777974312f7d801a8880714a218f76233f8-1788588393995.png',
    '0.999342',
  ],
  [
    '0xdc035d45d973e3ec169d2276ddab16f1e407384f',
    'USDS Stablecoin',
    'USDS',
    'address-0xdc035d45d973e3ec169d2276ddab16f1e407384f-1782885649480.png',
    '0.999736',
  ],
  [
    '0x163f8c2467924be0ae7b5347228cabf260318753',
    'Worldcoin',
    'WLD',
    'address-0x163f8c2467924be0ae7b5347228cabf260318753.png',
    '0.403466',
  ],
  [
    '0x1eef208926667594e5136e89d0e9dd6907959197',
    'PEAQ',
    'PEAQ',
    'address-0x1eef208926667594e5136e89d0e9dd6907959197-1757752320903.png',
    '0.02610272',
  ],
  [
    '0x04fa0d235c4abf4bcf4787af4cf447de572ef828',
    'UMA Voting Token v1',
    'UMA',
    'address-0x04fa0d235c4abf4bcf4787af4cf447de572ef828.png',
    '0.391061',
  ],
  [
    '0x195f5c217b96cd3dd75d39327161b8911a42e509',
    'NUTS',
    'NUTS',
    'address-0x195f5c217b96cd3dd75d39327161b8911a42e509-1757750555567.png',
    '0.00003394',
  ],
  [
    '0xfc209eeba3d744aa741cc5c2a73ebf9c977b5f82',
    'Brickken',
    'BKN',
    'address-0xfc209eeba3d744aa741cc5c2a73ebf9c977b5f82-1782022241973.png',
    '0.079401',
  ],
  [
    '0xb131f4a55907b10d1f0a50d8ab8fa09ec342cd74',
    'Memecoin',
    'MEME',
    'address-0xb131f4a55907b10d1f0a50d8ab8fa09ec342cd74.png',
    '0.00057092',
  ],
  [
    '0x2260fac5e5542a773aa44fbcfedf7c193bc2c599',
    'Wrapped BTC',
    'WBTC',
    'address-0x2260fac5e5542a773aa44fbcfedf7c193bc2c599.png',
    '79558',
  ],
  [
    '0x18084fba666a33d37592fa2633fd49a74dd93a88',
    'tBTC v2',
    'tBTC',
    'address-0x18084fba666a33d37592fa2633fd49a74dd93a88.png',
    '79673',
  ],
  [
    '0x0fedba9178b70e8b54e2af08ebffcf28a1e5a43b',
    'Reservoir',
    'DAM',
    'address-0x0fedba9178b70e8b54e2af08ebffcf28a1e5a43b-1757750673776.png',
    '0.00194885',
  ],
  [
    '0x1f9840a85d5af5bf1d1762f925bdaddc4201f984',
    'Uniswap',
    'UNI',
    'address-0x1f9840a85d5af5bf1d1762f925bdaddc4201f984.png',
    '6.28',
  ],
  [
    '0xec53bf9167f50cdeb3ae105f56099aaab9061f83',
    'Eigen',
    'EIGEN',
    'address-0xec53bf9167f50cdeb3ae105f56099aaab9061f83-1727837100115.png',
    '0.201594',
  ],
  [
    '0x7fc66500c84a76ad7e9c93437bfc5ac33e2ddae9',
    'Aave Token',
    'AAVE',
    'address-0x7fc66500c84a76ad7e9c93437bfc5ac33e2ddae9-1768204923952.png',
    '129.85',
  ],
  [
    '0x626e8036deb333b408be468f951bdb42433cbf18',
    'AIOZ Network',
    'AIOZ',
    'address-0x626e8036deb333b408be468f951bdb42433cbf18.png',
    '0.056381',
  ],
  [
    '0xcad2d4c4469ff09ab24d02a63bcedfcd44be0645',
    'Crypto Accept',
    'ACPT',
    'address-0xcad2d4c4469ff09ab24d02a63bcedfcd44be0645-1757751542314.png',
    '0.000005565223272436951',
  ],
  [
    '0xfaba6f8e4a5e8ab82f62fe7c39859fa577269be3',
    'Ondo',
    'ONDO',
    'address-0xfaba6f8e4a5e8ab82f62fe7c39859fa577269be3.png',
    '0.370714',
  ],
  [
    '0xfda09936dbd717368de0835ba441d9e62069d36f',
    'Intel (Ondo Tokenized)',
    'INTCon',
    'address-0xfda09936dbd717368de0835ba441d9e62069d36f-1757751326532.png',
    '95.75',
  ],
  [
    '0x68749665ff8d2d112fa859aa293f07a622782f38',
    'Tether Gold',
    'XAUt',
    'address-0x68749665ff8d2d112fa859aa293f07a622782f38.png',
    '4425.92',
  ],
  [
    '0xbe0ed4138121ecfc5c0e56b40517da27e6c5226b',
    'Aethir Token',
    'ATH',
    'address-0xbe0ed4138121ecfc5c0e56b40517da27e6c5226b.png',
    '0.0046323',
  ],
  [
    '0x32b86b99441480a7e5bd3a26c124ec2373e3f015',
    'BAD IDEA AI',
    'BAD',
    'address-0x32b86b99441480a7e5bd3a26c124ec2373e3f015.png',
    '5.76014e-10',
  ],
  [
    '0xfeac2eae96899709a43e252b6b92971d32f9c0f9',
    'ANyONe Protocol',
    'ANYONE',
    'address-0xfeac2eae96899709a43e252b6b92971d32f9c0f9-1721965702832.png',
    '0.152574',
  ],
  [
    '0xd2dda223b2617cb616c1580db421e4cfae6a8a85',
    'Bondly Token',
    'BONDLY',
    'address-0xd2dda223b2617cb616c1580db421e4cfae6a8a85-1757751851243.png',
    '0.000023776229800878986',
  ],
  [
    '0x91dfbee3965baaee32784c2d546b7a0c62f268c9',
    'Bondly',
    'BONDLY',
    'address-0x91dfbee3965baaee32784c2d546b7a0c62f268c9.png',
    '0.00012138',
  ],
  [
    '0x000006c2a22ff4a44ff1f5d0f2ed65f781f55555',
    'ZK Coin',
    'ZKC',
    'address-0x000006c2a22ff4a44ff1f5d0f2ed65f781f55555-1757751062636.png',
    '0.04903303',
  ],
  [
    '0xb551b43af192965f74e3dfaa476c890b403cad95',
    'Data bot',
    'DATA',
    'address-0xb551b43af192965f74e3dfaa476c890b403cad95.png',
    '0.00008467',
  ],
  [
    '0x720cd16b011b987da3518fbf38c3071d4f0d1495',
    'Flux',
    'FLUX',
    'address-0x720cd16b011b987da3518fbf38c3071d4f0d1495-1757752446049.png',
    '0.04580403447408582',
  ],
  [
    '0xaea46a60368a7bd060eec7df8cba43b7ef41ad85',
    'Fetch',
    'FET',
    'address-0xaea46a60368a7bd060eec7df8cba43b7ef41ad85.png',
    '0.167777',
  ],
  [
    '0x6b175474e89094c44da98b954eedeac495271d0f',
    'Dai Stablecoin',
    'DAI',
    'address-0x6b175474e89094c44da98b954eedeac495271d0f.png',
    '0.999731',
  ],
  [
    '0xf3e4872e6a4cf365888d93b6146a2baa7348f1a4',
    'iShares Silver Trust (Ondo Tokenized)',
    'SLVon',
    'address-0xf3e4872e6a4cf365888d93b6146a2baa7348f1a4-1757750638693.png',
    '59.46',
  ],
  [
    '0x95af4af910c28e8ece4512bfe46f1f33687424ce',
    'Manyu',
    'MANYU',
    'address-0x95af4af910c28e8ece4512bfe46f1f33687424ce-1757750689672.png',
    '4.48e-9',
  ],
  [
    '0x3fc29836e84e471a053d2d9e80494a867d670ead',
    'Ethereum Games',
    'ETHG',
    'address-0x3fc29836e84e471a053d2d9e80494a867d670ead-1757665401338.png',
    '0',
  ],
];

const TOKEN_LOGO_BASE =
  'https://uni.onekey-asset.com/server-service-indexer/evm--1/tokens/';

const defiSymbols = new Set(['AAVE', 'CRV', 'CVX', 'DAI', 'UNI']);

export const FULL_BUSINESS_TOKENS: readonly TokenSelectorToken[] =
  businessTokenTuples.map(
    ([contractAddress, name, symbol, logoFilename, price]) => ({
      networkId: 'evm--1',
      contractAddress,
      name,
      symbol,
      logoURI: `${TOKEN_LOGO_BASE}${logoFilename}`,
      price,
      balance: '0',
      fiatValue: '$0.00',
      isDeFi: defiSymbols.has(symbol),
    }),
  );

const screenshotTokenKeys = new Set(
  screenshotTokens.map(token => `${token.networkId}:${token.contractAddress}`),
);

export const TOKEN_SELECTOR_TOKENS: readonly TokenSelectorToken[] = [
  ...screenshotTokens,
  ...FULL_BUSINESS_TOKENS.filter(
    token =>
      !screenshotTokenKeys.has(`${token.networkId}:${token.contractAddress}`),
  ),
];

export const POPULAR_TOKEN_SYMBOLS = [
  'ETH',
  'USDT',
  'USDC',
  'WBTC',
  'WETH',
  'DAI',
] as const;

export const POPULAR_TOKENS = POPULAR_TOKEN_SYMBOLS.map(symbol =>
  TOKEN_SELECTOR_TOKENS.find(token => token.symbol === symbol),
).filter((token): token is TokenSelectorToken => Boolean(token));

export function buildTokenSelectorRows(
  searchText: string,
  showDeFiOnly: boolean,
): readonly IdentityRow[] {
  const query = searchText.trim().toLocaleLowerCase();
  return TOKEN_SELECTOR_TOKENS.filter(token => {
    if (showDeFiOnly && !token.isDeFi) return false;
    if (!query) return true;
    return [token.symbol, token.name, token.contractAddress].some(value =>
      value.toLocaleLowerCase().includes(query),
    );
  }).map(token => ({
    type: 'identity',
    key: `${token.networkId}:${token.contractAddress || 'native'}`,
    separator: false,
    leading: {
      kind: 'token',
      image: {
        uri: token.logoURI,
        width: 40,
        height: 40,
        loadingStrategy: 'none',
      },
      networkImage: {
        uri: ETHEREUM_NETWORK_LOGO,
        width: 16,
        height: 16,
        loadingStrategy: 'none',
      },
      fallbackText: token.symbol.slice(0, 2),
      shape: 'circle',
    },
    title: token.symbol,
    subtitle: token.name,
    trailing: [
      {
        kind: 'valuePair',
        primary: token.balance,
        secondary: token.fiatValue,
      },
      {
        kind: 'menu',
        actionKey: 'token.menu',
      },
    ],
    accessibilityLabel: `${token.symbol}, ${token.name}, ${token.balance}, ${token.fiatValue}`,
  }));
}
