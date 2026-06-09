import { Platform } from 'react-native';

// Standalone replica of the OneKey market K-line fetch used by the app
// (packages/kit-bg ServiceMarketV2.fetchMarketTokenKline + the request-header
// interceptor), so the example can feed real candle data to a market-mode chart
// over the bridge — no app background service needed.

export interface IKlinePoint {
  o: number; // open
  h: number; // high
  l: number; // low
  c: number; // close
  v: number; // volume
  t: number; // unix seconds
}

export interface IKlineResponse {
  points: IKlinePoint[];
  total: number;
}

// TradingView resolution -> API interval (mirrors normalizeTradingViewKLineInterval).
function intervalFromResolution(res: string): string {
  switch (res) {
    case '1':
    case '1m':
      return '1m';
    case '5':
    case '5m':
      return '5m';
    case '15':
    case '15m':
      return '15m';
    case '30':
    case '30m':
      return '30m';
    case '60':
    case '1h':
    case '1H':
      return '1H';
    case '240':
    case '4h':
    case '4H':
      return '4H';
    case '1d':
    case 'D':
    case '1D':
      return '1D';
    case '1w':
    case 'W':
    case '1W':
      return '1W';
    default:
      return res;
  }
}

function uuid(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (ch) => {
    const r = Math.floor(Math.random() * 16);
    const v = ch === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

// The x-onekey-* headers + UA are what get the request past Cloudflare; there is
// no request signing. Values mirror the app's getRequestHeaders() interceptor.
function oneKeyHeaders(): Record<string, string> {
  const rid = uuid();
  return {
    'x-onekey-request-id': rid,
    'x-amzn-trace-id': rid,
    'x-onekey-instance-id': rid,
    'x-onekey-request-platform': `${Platform.OS === 'ios' ? 'ios' : 'android'}-stable`,
    'x-onekey-request-version': '6.3.0',
    'x-onekey-request-currency': 'usd',
    'x-onekey-request-locale': 'zh-cn',
    'User-Agent': 'OneKeyWallet/6.3.0',
  };
}

export async function fetchMarketKline(opts: {
  tokenAddress: string;
  networkId: string;
  resolution: string;
  from: number;
  to: number;
}): Promise<IKlineResponse> {
  const url =
    'https://utility.onekeycn.com/utility/v2/market/token/kline?' +
    new URLSearchParams({
      tokenAddress: opts.tokenAddress,
      networkId: opts.networkId,
      interval: intervalFromResolution(opts.resolution),
      timeFrom: String(opts.from),
      timeTo: String(opts.to),
      currency: 'usd',
    }).toString();
  const res = await fetch(url, { headers: oneKeyHeaders() });
  const json = (await res.json()) as { code: number; data?: IKlineResponse };
  return json.data ?? { points: [], total: 0 };
}
