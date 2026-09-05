import type {
  CheckboxState,
  IdentityRow,
  RowModel,
  SectionHeaderRow,
  TrailingAccessory,
} from '@onekeyfe/react-native-native-list';

/**
 * Extracted from app-monorepo packages/shared/src/config/presetNetworks.ts at
 * origin/x a1bacaf72172f39d7da1df7052065ce8d593f088. The all-networks sentinel and
 * dev-only Cosmos testnet are intentionally excluded, matching production's
 * single-network selector (88 mainnets + 8 testnets).
 */
export const NETWORK_SELECTOR_SOURCE_REF =
  'app-monorepo@a1bacaf72172f39d7da1df7052065ce8d593f088' as const;

type NetworkTuple = readonly [
  sourceName: string,
  id: string,
  name: string,
  logoURI: string,
  impl: string,
  symbol: string,
  shortname: string,
  isTestnet: boolean,
];

const networkTuples = [
  [
    'btc',
    'btc--0',
    'Bitcoin',
    'https://uni.onekey-asset.com/static/chain/btc.png',
    'btc',
    'BTC',
    'BTC',
    false,
  ],
  [
    'doge',
    'doge--0',
    'Dogecoin',
    'https://uni.onekey-asset.com/static/chain/doge.png',
    'doge',
    'DOGE',
    'DOGE',
    false,
  ],
  [
    'bch',
    'bch--0',
    'Bitcoin Cash',
    'https://uni.onekey-asset.com/static/chain/bch.png',
    'bch',
    'BCH',
    'BCH',
    false,
  ],
  [
    'ltc',
    'ltc--0',
    'Litecoin',
    'https://uni.onekey-asset.com/static/chain/ltc.png',
    'ltc',
    'LTC',
    'LTC',
    false,
  ],
  [
    'neurai',
    'neurai--0',
    'Neurai',
    'https://uni.onekey-asset.com/static/chain/neurai.png',
    'neurai',
    'XNA',
    'XNA',
    false,
  ],
  [
    'tbtc',
    'tbtc--0',
    'Bitcoin Testnet',
    'https://uni.onekey-asset.com/static/chain/bitcoin-testnet.png',
    'tbtc',
    'TBTC',
    'TBTC',
    true,
  ],
  [
    'sbtc',
    'tbtc--1',
    'Bitcoin Signet',
    'https://uni.onekey-asset.com/static/chain/sbtc.png',
    'tbtc',
    'SBTC',
    'SBTC',
    true,
  ],
  [
    'eth',
    'evm--1',
    'Ethereum',
    'https://uni.onekey-asset.com/static/chain/eth.png',
    'evm',
    'ETH',
    'ETH',
    false,
  ],
  [
    'bsc',
    'evm--56',
    'BNB Chain',
    'https://uni.onekey-asset.com/static/chain/bsc.png',
    'evm',
    'BNB',
    'BSC',
    false,
  ],
  [
    'polygon',
    'evm--137',
    'Polygon',
    'https://uni.onekey-asset.com/static/chain/polygon.png',
    'evm',
    'POL',
    'Polygon',
    false,
  ],
  [
    'arbitrum',
    'evm--42161',
    'Arbitrum',
    'https://uni.onekey-asset.com/static/chain/arbitrum.png',
    'evm',
    'ETH',
    'Arbitrum',
    false,
  ],
  [
    'avalanche',
    'evm--43114',
    'Avalanche',
    'https://uni.onekey-asset.com/static/chain/avalanche.png',
    'evm',
    'AVAX',
    'Avalanche',
    false,
  ],
  [
    'optimism',
    'evm--10',
    'Optimism',
    'https://uni.onekey-asset.com/static/chain/optimism.png',
    'evm',
    'ETH',
    'Optimism',
    false,
  ],
  [
    'zksyncera',
    'evm--324',
    'zkSync Era',
    'https://uni.onekey-asset.com/static/chain/zksync-era-mainnet.png',
    'evm',
    'ETH',
    'ZKSYNCERA',
    false,
  ],
  [
    'opbnb',
    'evm--204',
    'opBNB',
    'https://uni.onekey-asset.com/static/chain/opbnb.png',
    'evm',
    'BNB',
    'obnb',
    false,
  ],
  [
    'okb',
    'evm--196',
    'X Layer',
    'https://uni.onekey-asset.com/static/chain/okb.png',
    'evm',
    'OKB',
    'okb',
    false,
  ],
  [
    'taiko',
    'evm--167000',
    'Taiko',
    'https://uni.onekey-asset.com/static/chain/taiko.png',
    'evm',
    'ETH',
    'tko-mainnet',
    false,
  ],
  [
    'scr',
    'evm--534352',
    'Scroll',
    'https://uni.onekey-asset.com/static/chain/scr.png',
    'evm',
    'ETH',
    'scr',
    false,
  ],
  [
    'ronin',
    'evm--2020',
    'Ronin',
    'https://uni.onekey-asset.com/static/chain/ronin.png',
    'evm',
    'RON',
    'ronin',
    false,
  ],
  [
    'pulse',
    'evm--369',
    'PulseChain',
    'https://uni.onekey-asset.com/static/chain/pulse.png',
    'evm',
    'PLS',
    'pls',
    false,
  ],
  [
    'octa',
    'evm--800001',
    'OctaSpace',
    'https://uni.onekey-asset.com/static/chain/octa.webp',
    'evm',
    'OCTA',
    'octa',
    false,
  ],
  [
    'merlinmainnet',
    'evm--4200',
    'Merlin',
    'https://uni.onekey-asset.com/static/chain/merlinmainnet.png',
    'evm',
    'BTC',
    'Merlin-Mainnet',
    false,
  ],
  [
    'mantle',
    'evm--5000',
    'Mantle',
    'https://uni.onekey-asset.com/static/chain/mantle.png',
    'evm',
    'MNT',
    'Mantle',
    false,
  ],
  [
    'mantapacific',
    'evm--169',
    'Manta Pacific',
    'https://uni.onekey-asset.com/static/chain/manta-pacific-mainnet.png',
    'evm',
    'MANTASPACIFIC',
    'mantapacific',
    false,
  ],
  [
    'linea',
    'evm--59144',
    'Linea',
    'https://uni.onekey-asset.com/static/chain/linea.png',
    'evm',
    'ETH',
    'Linea',
    false,
  ],
  [
    'kava',
    'evm--2222',
    'Kava',
    'https://uni.onekey-asset.com/static/chain/kava.png',
    'evm',
    'KAVA',
    'kava',
    false,
  ],
  [
    'iotex',
    'evm--4689',
    'IoTeX',
    'https://uni.onekey-asset.com/static/chain/iotex.png',
    'evm',
    'IOTX',
    'iotex',
    false,
  ],
  [
    'flare',
    'evm--14',
    'Flare',
    'https://uni.onekey-asset.com/static/chain/flare.png',
    'evm',
    'FLR',
    'flr',
    false,
  ],
  [
    'fevm',
    'evm--314',
    'Filecoin FEVM',
    'https://uni.onekey-asset.com/static/chain/fevm.png',
    'evm',
    'FIL',
    'FEVM',
    false,
  ],
  [
    'ethw',
    'evm--10001',
    'EthereumPoW',
    'https://uni.onekey-asset.com/static/chain/ethw.png',
    'evm',
    'ETHW',
    'ETHW',
    false,
  ],
  [
    'sepolia',
    'evm--11155111',
    'Ethereum Sepolia Testnet',
    'https://uni.onekey-asset.com/static/chain/sepolia.png',
    'evm',
    'TETH',
    'Sepolia',
    true,
  ],
  [
    'etc',
    'evm--61',
    'Ethereum Classic',
    'https://uni.onekey-asset.com/static/chain/etc.png',
    'evm',
    'ETC',
    'ETC',
    false,
  ],
  [
    'ace',
    'evm--648',
    'Endurance',
    'https://uni.onekey-asset.com/static/chain/ace.png',
    'evm',
    'ACE',
    'ace',
    false,
  ],
  [
    'cyeth',
    'evm--7560',
    'Cyber',
    'https://uni.onekey-asset.com/static/chain/cyeth.png',
    'evm',
    'ETH',
    'cyeth',
    false,
  ],
  [
    'cronos',
    'evm--25',
    'Cronos',
    'https://uni.onekey-asset.com/static/chain/cronos.png',
    'evm',
    'CRO',
    'CRO',
    false,
  ],
  [
    'cfxespace',
    'evm--1030',
    'Conflux eSpace',
    'https://uni.onekey-asset.com/static/chain/conflux-espace.png',
    'evm',
    'CFX',
    'CFXESPACE',
    false,
  ],
  [
    'boba',
    'evm--288',
    'Boba',
    'https://uni.onekey-asset.com/static/chain/boba.png',
    'evm',
    'ETH',
    'Boba',
    false,
  ],
  [
    'blast',
    'evm--81457',
    'Blast',
    'https://uni.onekey-asset.com/static/logo/blast.png',
    'evm',
    'ETH',
    'blast',
    false,
  ],
  [
    'btr',
    'evm--200901',
    'Bitlayer',
    'https://uni.onekey-asset.com/static/chain/btr.png',
    'evm',
    'BTC',
    'btr',
    false,
  ],
  [
    'base',
    'evm--8453',
    'Base',
    'https://uni.onekey-asset.com/static/chain/base.png',
    'evm',
    'ETH',
    'Base',
    false,
  ],
  [
    'bob',
    'evm--60808',
    'BOB',
    'https://uni.onekey-asset.com/static/chain/bob.png',
    'evm',
    'ETH',
    'bob',
    false,
  ],
  [
    'katana',
    'evm--747474',
    'Katana',
    'https://uni-test.onekey-asset.com/dashboard/logo/upload_1784281571805.0.8864057938722496.0.webp',
    'evm',
    'ETH',
    'Katana',
    false,
  ],
  [
    'aurora',
    'evm--1313161554',
    'Aurora',
    'https://uni.onekey-asset.com/static/chain/aurora.png',
    'evm',
    'ETH',
    'Aurora',
    false,
  ],
  [
    'neox',
    'evm--47763',
    'Neo X Mainnet',
    'https://uni.onekey-asset.com/dashboard/logo/upload_1729561119021.0.635920670130657.0.png',
    'evm',
    'GAS',
    'neox',
    false,
  ],
  [
    'azero',
    'evm--41455',
    'Aleph Zero EVM',
    'https://uni.onekey-asset.com/dashboard/logo/upload_1729837141734.0.5402483107018017.0.png',
    'evm',
    'AZERO',
    'azero',
    false,
  ],
  [
    'dtc',
    'evm--9798',
    'DataTradeChain',
    'https://uni.onekey-asset.com/static/chain/dtc.png',
    'evm',
    'DTT',
    'DTC',
    false,
  ],
  [
    'sonic',
    'evm--146',
    'Sonic',
    'https://uni.onekey-asset.com/static/chain/sonic.png',
    'evm',
    'S',
    'sonic',
    false,
  ],
  [
    'hsk',
    'evm--177',
    'HashKey Chain',
    'https://uni.onekey-asset.com/static/chain/hsk.png',
    'evm',
    'HSK',
    'hsk',
    false,
  ],
  [
    'sei',
    'evm--1329',
    'Sei',
    'https://uni.onekey-asset.com/static/chain/sei.png',
    'evm',
    'SEI',
    'sei',
    false,
  ],
  [
    'unichain',
    'evm--130',
    'Unichain',
    'https://uni.onekey-asset.com/static/logo/unichain.png',
    'evm',
    'ETH',
    'unichain',
    false,
  ],
  [
    'worldChain',
    'evm--480',
    'World Chain',
    'https://uni.onekey-asset.com/static/chain/world-chain.png',
    'evm',
    'ETH',
    'worldchain',
    false,
  ],
  [
    'hyperEvm',
    'evm--999',
    'HyperEVM',
    'https://uni.onekey-asset.com/static/chain/hyper-evm.png',
    'evm',
    'HYPE',
    'hyperevm',
    false,
  ],
  [
    'hoodi',
    'evm--560048',
    'Hoodi Testnet',
    'https://uni.onekey-asset.com/dashboard/logo/upload_1756881610802.0.9132280905497288.0.jpeg',
    'evm',
    'ETH',
    'Hoodi',
    true,
  ],
  [
    'celestia',
    'cosmos--celestia',
    'Celestia',
    'https://uni.onekey-asset.com/static/chain/celestia.png',
    'cosmos',
    'TIA',
    'Celestia',
    false,
  ],
  [
    'secret',
    'cosmos--secret-4',
    'Secret Network',
    'https://uni.onekey-asset.com/static/chain/secret.png',
    'cosmos',
    'SCRT',
    'Secret Network',
    false,
  ],
  [
    'juno',
    'cosmos--juno-1',
    'Juno',
    'https://uni.onekey-asset.com/static/chain/juno.png',
    'cosmos',
    'JUNO',
    'Juno',
    false,
  ],
  [
    'fetchai',
    'cosmos--fetchhub-4',
    'Fetch.ai',
    'https://uni.onekey-asset.com/static/chain/fetch.png',
    'cosmos',
    'FET',
    'Fetch.ai',
    false,
  ],
  [
    'cronosPosChain',
    'cosmos--crypto-org-chain-mainnet-1',
    'Cronos POS Chain',
    'https://uni.onekey-asset.com/static/chain/cryptoorg.png',
    'cosmos',
    'CRO',
    'Cronos POS Chain',
    false,
  ],
  [
    'akash',
    'cosmos--akashnet-2',
    'Akash',
    'https://uni.onekey-asset.com/static/chain/akash.png',
    'cosmos',
    'AKT',
    'Akash',
    false,
  ],
  [
    'osmosis',
    'cosmos--osmosis-1',
    'Osmosis',
    'https://uni.onekey-asset.com/static/chain/osmosis.png',
    'cosmos',
    'OSMO',
    'Osmosis',
    false,
  ],
  [
    'cosmoshub',
    'cosmos--cosmoshub-4',
    'Cosmos',
    'https://uni.onekey-asset.com/static/chain/cosmos.png',
    'cosmos',
    'ATOM',
    'Cosmos',
    false,
  ],
  [
    'bbn',
    'cosmos--bbn-1',
    'Babylon Genesis',
    'https://uni.onekey-asset.com/static/logo/babylon.png',
    'cosmos',
    'BABY',
    'BBN',
    false,
  ],
  [
    'bbnTestnet',
    'cosmos--bbn-test-5',
    'Babylon Testnet',
    'https://uni.onekey-asset.com/static/logo/babylon.png',
    'cosmos',
    'TBABY',
    'TBBN',
    true,
  ],
  [
    'noble',
    'cosmos--noble-1',
    'Noble',
    'https://uni.onekey-asset.com/static/chain/noble.png',
    'cosmos',
    'USDC',
    'Noble',
    false,
  ],
  [
    'astar',
    'dot--astar',
    'Astar',
    'https://uni.onekey-asset.com/static/chain/astar.png',
    'dot',
    'ASTR',
    'ASTR',
    false,
  ],
  [
    'manta',
    'dot--manta',
    'Manta Atlantic',
    'https://uni.onekey-asset.com/static/chain/manta-atlantic.png',
    'dot',
    'MANTA',
    'MANTA',
    false,
  ],
  [
    'hydradx',
    'dot--hydration',
    'Hydration',
    'https://uni.onekey-asset.com/static/chain/hdx.png',
    'dot',
    'HDX',
    'HDX',
    false,
  ],
  [
    'bifrost',
    'dot--bifrost-ksm',
    'Bifrost Kusama',
    'https://uni.onekey-asset.com/static/chain/bnc.png',
    'dot',
    'BNC',
    'BNC',
    false,
  ],
  [
    'bifrostDot',
    'dot--bifrost',
    'Bifrost Polkadot',
    'https://uni.onekey-asset.com/static/chain/bifrost.png',
    'dot',
    'BNC',
    'BNC',
    false,
  ],
  [
    'assethubPolkadot',
    'dot--asset-hub',
    'Polkadot AssetHub',
    'https://uni.onekey-asset.com/static/chain/dot-assethub.png',
    'dot',
    'DOT',
    'DOT',
    false,
  ],
  [
    'assethubKusama',
    'dot--kusama-assethub',
    'Kusama AssetHub',
    'https://uni.onekey-asset.com/static/chain/dot-ksm-assethub.png',
    'dot',
    'KSM',
    'KsmAssetHub',
    false,
  ],
  [
    'aptos',
    'aptos--1',
    'Aptos',
    'https://uni.onekey-asset.com/static/chain/apt.png',
    'aptos',
    'APT',
    'APT',
    false,
  ],
  [
    'lightning',
    'lightning--0',
    'Lightning Network',
    'https://uni.onekey-asset.com/static/chain/lnd.png',
    'lightning',
    'sats',
    'Lightning',
    false,
  ],
  [
    'tlightning',
    'tlightning--0',
    'Lightning Network Testnet',
    'https://uni.onekey-asset.com/static/chain/lightning-network-testnet.png',
    'tlightning',
    'sats',
    'LightningTestnet',
    true,
  ],
  [
    'cardano',
    'ada--0',
    'Cardano',
    'https://uni.onekey-asset.com/static/chain/ada.png',
    'ada',
    'ADA',
    'Cardano',
    false,
  ],
  [
    'ripple',
    'xrp--0',
    'XRP Ledger',
    'https://uni.onekey-asset.com/static/chain/xrp.png',
    'xrp',
    'XRP',
    'XRP Ledger',
    false,
  ],
  [
    'nostr',
    'nostr--0',
    'Nostr',
    'https://uni.onekey-asset.com/static/chain/nostr.png',
    'nostr',
    'Nostr',
    'Nostr',
    false,
  ],
  [
    'near',
    'near--0',
    'Near',
    'https://uni.onekey-asset.com/static/chain/near.png',
    'near',
    'NEAR',
    'Near',
    false,
  ],
  [
    'tron',
    'tron--0x2b6653dc',
    'Tron',
    'https://uni.onekey-asset.com/static/chain/tron.png',
    'tron',
    'TRX',
    'TRX',
    false,
  ],
  [
    'nile',
    'tron--0xcd8690dc',
    'Tron Nile Testnet',
    'https://uni.onekey-asset.com/static/chain/tron.png',
    'tron',
    'TTRX',
    'TTRX',
    true,
  ],
  [
    'cfx',
    'cfx--1029',
    'Conflux',
    'https://uni.onekey-asset.com/static/chain/cfx.png',
    'cfx',
    'CFX',
    'CFX',
    false,
  ],
  [
    'sol',
    'sol--101',
    'Solana',
    'https://uni.onekey-asset.com/static/chain/sol.png',
    'sol',
    'SOL',
    'SOL',
    false,
  ],
  [
    'nexa',
    'nexa--0',
    'Nexa',
    'https://uni.onekey-asset.com/static/chain/nexa.png',
    'nexa',
    'NEX',
    'Nexa',
    false,
  ],
  [
    'kaspa',
    'kaspa--kaspa',
    'Kaspa',
    'https://uni.onekey-asset.com/static/chain/kas.png',
    'kaspa',
    'KAS',
    'KAS',
    false,
  ],
  [
    'dnx',
    'dynex--0',
    'Dynex',
    'https://uni.onekey-asset.com/static/chain/dynex.png',
    'dynex',
    'DNX',
    'DNX',
    false,
  ],
  [
    'fil',
    'fil--314',
    'Filecoin',
    'https://uni.onekey-asset.com/static/chain/fil.png',
    'fil',
    'FIL',
    'FIL',
    false,
  ],
  [
    'algo',
    'algo--4160',
    'Algorand',
    'https://uni.onekey-asset.com/static/chain/algo.png',
    'algo',
    'ALGO',
    'ALGO',
    false,
  ],
  [
    'sui',
    'sui--mainnet',
    'SUI',
    'https://uni.onekey-asset.com/static/chain/sui.png',
    'sui',
    'SUI',
    'SUI',
    false,
  ],
  [
    'ckb',
    'nervos--mainnet',
    'Nervos',
    'https://uni.onekey-asset.com/static/chain/nervos.png',
    'nervos',
    'CKB',
    'CKB',
    false,
  ],
  [
    'alph',
    'alph--mainnet',
    'Alephium',
    'https://uni.onekey-asset.com/static/chain/alph.png',
    'alph',
    'ALPH',
    'alph',
    false,
  ],
  [
    'ton',
    'ton--mainnet',
    'TON',
    'https://uni.onekey-asset.com/static/chain/ton.png',
    'ton',
    'GRAM',
    'ton',
    false,
  ],
  [
    'scdo',
    'scdo--net1',
    'SCDO',
    'https://uni.onekey-asset.com/static/chain/scdo.png',
    'scdo',
    'SCDO',
    'SCDO',
    false,
  ],
  [
    'benfen',
    'bfc--mainnet',
    'BenFen',
    'https://uni.onekey-asset.com/static/chain/bfc.png',
    'bfc',
    'BFC',
    'BFC',
    false,
  ],
  [
    'neoN3',
    'neo--3',
    'Neo N3',
    'https://uni.onekey-asset.com/static/chain/neon3.png',
    'neo',
    'GAS',
    'neon3',
    false,
  ],
  [
    'stellar',
    'stellar--mainnet',
    'Stellar',
    'https://uni.onekey-asset.com/static/chain/stellar.png',
    'stellar',
    'XLM',
    'XLM',
    false,
  ],
  [
    'stellarTestnet',
    'stellar--testnet',
    'Stellar Testnet',
    'https://uni.onekey-asset.com/static/chain/stellar.png',
    'stellar',
    'XLM',
    'TXLM',
    true,
  ],
] as const satisfies readonly NetworkTuple[];

export type NetworkSelectorNetwork = Readonly<{
  sourceName: string;
  id: string;
  name: string;
  logoURI: string;
  impl: string;
  symbol: string;
  shortname: string;
  isTestnet: boolean;
}>;

export const NETWORK_SELECTOR_NETWORKS: readonly NetworkSelectorNetwork[] =
  networkTuples.map(
    ([sourceName, id, name, logoURI, impl, symbol, shortname, isTestnet]) => ({
      sourceName,
      id,
      name,
      logoURI,
      impl,
      symbol,
      shortname,
      isTestnet,
    }),
  );

export const NETWORK_SELECTOR_BALANCES = {
  'evm--56': '12.66',
  'evm--1': '8.12',
  'evm--42161': '6.15',
  'evm--59144': '3.25',
  'evm--10': '3.09',
  'sol--101': '2.88',
  'evm--8453': '1.97',
  'btc--0': '1.87',
  'evm--43114': '1.45',
} as const satisfies Readonly<Record<string, string>>;

export const NETWORK_SELECTOR_DEFAULT_ENABLED_IDS = [
  'btc--0',
  'tron--0x2b6653dc',
  'sol--101',
  'evm--1',
  'evm--56',
  'evm--8453',
  'evm--42161',
  'evm--43114',
  'evm--137',
  'evm--10',
  'evm--81457',
  'evm--5000',
  'evm--25',
  'evm--59144',
  'evm--200901',
  'evm--369',
  'evm--534352',
  'evm--60808',
] as const;

export const NETWORK_SELECTOR_MAINNET_COUNT = 88;
export const NETWORK_SELECTOR_TESTNET_COUNT = 8;
export const NETWORK_SELECTOR_ASSET_COUNT = 9;

export type NetworkSelectorMode = 'portfolio' | 'network';

export type NetworkSelectorSection = Readonly<{
  key: string;
  title?: string;
  indexTitle?: string;
  networks: readonly NetworkSelectorNetwork[];
  assetSection?: boolean;
  testnetSection?: boolean;
}>;

function matchesSearch(network: NetworkSelectorNetwork, searchText: string) {
  const search = searchText.trim().toLocaleLowerCase();
  if (!search) return true;
  if (network.name.toLocaleLowerCase().includes(search)) return true;
  return [network.impl, network.symbol, network.shortname].some(
    value => value.toLocaleLowerCase() === search,
  );
}

export function searchNetworkSelectorNetworks(
  networks: readonly NetworkSelectorNetwork[],
  searchText: string,
): readonly NetworkSelectorNetwork[] {
  return networks.filter(network => matchesSearch(network, searchText));
}

function assetValue(networkId: string): string | undefined {
  return NETWORK_SELECTOR_BALANCES[
    networkId as keyof typeof NETWORK_SELECTOR_BALANCES
  ];
}

export function buildNetworkSelectorSections(
  mode: NetworkSelectorMode,
  searchText = '',
): readonly NetworkSelectorSection[] {
  const source = NETWORK_SELECTOR_NETWORKS.filter(
    network => mode === 'network' || !network.isTestnet,
  );
  if (searchText.trim()) {
    const networks = searchNetworkSelectorNetworks(source, searchText);
    return networks.length ? [{ key: 'search', networks }] : [];
  }

  const mainnets = source.filter(network => !network.isTestnet);
  const assetNetworks = mainnets
    .filter(network => Number(assetValue(network.id) ?? 0) > 1)
    .slice()
    .sort(
      (left, right) =>
        Number(assetValue(right.id)) - Number(assetValue(left.id)),
    );
  const groups = new Map<string, NetworkSelectorNetwork[]>();
  for (const network of mainnets) {
    if (assetValue(network.id)) continue;
    const title = network.name[0]?.toLocaleUpperCase() || '#';
    const group = groups.get(title) ?? [];
    group.push(network);
    groups.set(title, group);
  }

  const sections: NetworkSelectorSection[] = [
    {
      key: 'assets',
      title: '有资产的网络',
      indexTitle: '#',
      networks: assetNetworks,
      assetSection: true,
    },
    ...Array.from(groups.entries())
      .sort(([left], [right]) => left.charCodeAt(0) - right.charCodeAt(0))
      .map(([title, networks]) => ({
        key: `letter-${title}`,
        title,
        indexTitle: title,
        networks,
      })),
  ];
  if (mode === 'network') {
    const testnets = source.filter(network => network.isTestnet);
    if (testnets.length) {
      sections.push({
        key: 'testnet',
        title: '测试网',
        networks: testnets,
        testnetSection: true,
      });
    }
  }
  return sections;
}

function sectionCheckboxState(
  networks: readonly NetworkSelectorNetwork[],
  selectedKeys: ReadonlySet<string>,
): CheckboxState {
  const selectedCount = networks.filter(network =>
    selectedKeys.has(network.id),
  ).length;
  if (!selectedCount) return 'unchecked';
  return selectedCount === networks.length ? 'checked' : 'indeterminate';
}

function buildNetworkRow(
  network: NetworkSelectorNetwork,
  sectionKey: string,
  mode: NetworkSelectorMode,
  selectedKeys: ReadonlySet<string>,
): IdentityRow {
  const trailing: TrailingAccessory[] = [];
  const value = assetValue(network.id);
  if (value) trailing.push({ kind: 'value', text: `$${value}` });
  if (mode === 'portfolio') {
    trailing.push({
      kind: 'checkbox',
      state: selectedKeys.has(network.id) ? 'checked' : 'unchecked',
      target: { scope: 'row' },
      actionKey: 'network.toggle',
    });
  }
  return {
    type: 'identity',
    key: network.id,
    presentation: 'networkSelector',
    sectionKey,
    groupId: network.id,
    groupPosition: 'single',
    size: 'small',
    leading: {
      kind: 'network',
      image: {
        uri: network.logoURI,
        width: 32,
        height: 32,
        cachePolicy: 'memory-disk',
        loadingStrategy: 'none',
      },
      fallbackText: network.name.slice(0, 1).toLocaleUpperCase(),
      shape: 'circle',
      backgroundColor: '#FFFFFFED',
    },
    title: network.name,
    trailing: trailing.length ? trailing : undefined,
    accessibilityLabel: `${network.name}${value ? `, $${value}` : ''}`,
  };
}

export function buildNetworkSelectorRows(
  mode: NetworkSelectorMode,
  searchText: string,
  selectedKeys: ReadonlySet<string>,
): readonly RowModel[] {
  const sections = buildNetworkSelectorSections(mode, searchText);
  const rows: RowModel[] = [];
  if (mode === 'portfolio' && !searchText.trim()) {
    rows.push({
      type: 'sectionHeader',
      key: 'portfolio-summary',
      sectionKey: 'portfolio-summary',
      variant: 'summary',
      title: `已选择 ${selectedKeys.size} 个网络`,
      value:
        selectedKeys.size === NETWORK_SELECTOR_MAINNET_COUNT
          ? '取消全选'
          : '全选',
      valueActionKey: 'network.toggleAll',
    });
  }
  for (const section of sections) {
    if (section.title) {
      let header: SectionHeaderRow;
      if (section.assetSection && mode === 'portfolio') {
        header = {
          type: 'sectionHeader',
          key: `header-${section.key}`,
          sectionKey: section.key,
          indexTitle: section.indexTitle,
          title: section.title,
          value: `$${Object.values(NETWORK_SELECTOR_BALANCES)
            .reduce((sum, value) => sum + Number(value), 0)
            .toFixed(2)}`,
          checkbox: {
            kind: 'checkbox',
            state: sectionCheckboxState(section.networks, selectedKeys),
            target: { scope: 'section', sectionKey: section.key },
            actionKey: 'network.toggleSection',
          },
        };
      } else {
        header = {
          type: 'sectionHeader',
          key: `header-${section.key}`,
          sectionKey: section.key,
          presentation: section.assetSection ? 'networkSelector' : undefined,
          title: section.title,
          indexTitle: section.indexTitle,
        };
      }
      rows.push(header);
    }
    rows.push(
      ...section.networks.map(network =>
        buildNetworkRow(network, section.key, mode, selectedKeys),
      ),
    );
    if (section.assetSection && sections.length > 1) {
      rows.push({
        type: 'system',
        key: 'asset-section-gap',
        sectionKey: section.key,
        variant: 'spacer',
        height: 18,
      });
    }
  }
  return rows;
}
