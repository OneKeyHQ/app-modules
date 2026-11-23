import type {
  MarketAsset,
  MarketCategory,
  MarketConfig,
  MarketPageDescriptor,
  MarketPageKind,
  MarketReplaySnapshot,
  MarketScenario,
} from './marketNativePagerTypes';

export const MARKET_PAGE_SIZE = 20;

export const MARKET_TIME_FRAME_BY_RANGE = {
  '5m': '1',
  '1h': '2',
  '4h': '3',
  '24h': '4',
} as const;

export function buildMarketPages(
  config: MarketConfig,
  locale: 'zh-CN' | 'en-US' = 'zh-CN',
): MarketPageDescriptor[] {
  const pages: MarketPageDescriptor[] = [
    {
      key: 'watchlist',
      kind: 'watchlist',
      name: locale === 'en-US' ? 'Favorites' : '自选',
      categoryId: 'watchlist',
    },
    ...config.spotCategories.map(category => ({
      key: `spot:${category.id}`,
      kind: (category.id === 'top_coins'
        ? 'topCoins'
        : 'spot') as MarketPageKind,
      name: category.name,
      categoryId: category.id,
    })),
  ];

  if (!pages.some(page => page.categoryId === 'top_coins')) {
    pages.push({
      key: 'top-coins',
      kind: 'topCoins',
      name: locale === 'en-US' ? 'Top coins' : '主流币',
      categoryId: 'top_coins',
    });
  }

  if (config.perpsCategories.length > 0) {
    pages.push({
      key: 'perps',
      kind: 'perps',
      name: locale === 'en-US' ? 'Perps' : '合约',
      categoryId: 'perps',
    });
  }

  return pages;
}

export function replayItemsForPage(
  snapshot: MarketReplaySnapshot,
  page: MarketPageDescriptor,
  watchlistKeys: readonly string[],
  selectedPerpsCategory: string,
  filters?: { networkId: string; timeRange: string; stockCategory: string },
): readonly MarketAsset[] {
  if (page.kind === 'watchlist') {
    const byKey = new Map(
      [
        ...Object.values(snapshot.spotQueries ?? {}).flat(),
        ...Object.values(snapshot.spot).flat(),
        ...Object.values(snapshot.perps).flat(),
        ...(snapshot.config.recommendTokens ?? []),
      ].map(item => [item.key, item] as const),
    );
    return watchlistKeys.flatMap(key => {
      const item = byKey.get(key);
      return item ? [item] : [];
    });
  }
  if (page.kind === 'perps') {
    return snapshot.perps[selectedPerpsCategory] ?? [];
  }
  if (page.kind === 'topCoins') {
    return snapshot.spot.top_coins ?? [];
  }
  if (page.categoryId === 'stocks' && snapshot.stocks) {
    return snapshot.stocks[filters?.stockCategory ?? 'all'] ?? [];
  }
  if (filters && snapshot.spotQueries) {
    const key = [
      page.categoryId,
      page.categoryId === 'stocks' ? '' : filters.networkId,
      filters.timeRange,
      page.categoryId === 'stocks' ? filters.stockCategory : 'all',
    ].join('|');
    return snapshot.spotQueries[key] ?? [];
  }
  const items = snapshot.spot[page.categoryId] ?? [];
  return filters?.networkId
    ? items.filter(item => item.networkId === filters.networkId)
    : items;
}

export function applyMarketScenario(
  items: readonly MarketAsset[],
  scenario: MarketScenario,
): readonly MarketAsset[] {
  if (scenario === 'empty' || scenario === 'error') return [];
  if (scenario === 'short') return items.slice(0, 2);
  return items;
}

export function categoriesForPage(
  page: MarketPageDescriptor,
  config: MarketConfig,
  locale: 'zh-CN' | 'en-US' = 'zh-CN',
): readonly MarketCategory[] {
  if (page.kind === 'watchlist') {
    return [
      { id: 'all', name: locale === 'en-US' ? 'All' : '全部' },
      { id: 'spot', name: locale === 'en-US' ? 'Spot' : '现货' },
      { id: 'perps', name: locale === 'en-US' ? 'Perps' : '合约' },
    ];
  }
  if (page.kind === 'perps') return config.perpsCategories;
  if (page.categoryId === 'stocks') return config.stockCategories;
  return [];
}

export function filterWatchlist(
  items: readonly MarketAsset[],
  filter: string,
): readonly MarketAsset[] {
  if (filter === 'spot') return items.filter(item => item.kind !== 'perp');
  if (filter === 'perps') return items.filter(item => item.kind === 'perp');
  return items;
}

export function filterMarketSearch(
  items: readonly MarketAsset[],
  query: string,
): readonly MarketAsset[] {
  const normalized = query.trim().toLocaleLowerCase();
  if (!normalized) return items;
  return items.filter(item =>
    [item.symbol, item.name, item.stock?.subtitle, item.address]
      .filter(Boolean)
      .some(value => String(value).toLocaleLowerCase().includes(normalized)),
  );
}

export function kindLabel(kind: MarketPageKind) {
  switch (kind) {
    case 'watchlist':
      return 'local-watchlist';
    case 'perps':
      return 'perps';
    case 'topCoins':
      return 'top-coins';
    default:
      return 'spot';
  }
}

export function formatCompactUsd(value: string | undefined): string {
  const amount = Number(value);
  if (!Number.isFinite(amount)) return '--';
  const absolute = Math.abs(amount);
  const units = [
    { threshold: 1e12, suffix: 'T' },
    { threshold: 1e9, suffix: 'B' },
    { threshold: 1e6, suffix: 'M' },
    { threshold: 1e3, suffix: 'K' },
  ];
  const unit = units.find(item => absolute >= item.threshold);
  if (!unit) return `$${amount.toFixed(absolute < 1 ? 2 : 0)}`;
  const compact = amount / unit.threshold;
  const digits = 2;
  return `$${compact
    .toFixed(digits)
    .replace(/(\.[0-9]*?)0+$/, '$1')
    .replace(/\.$/, '')}${unit.suffix}`;
}

export function formatPrice(value: string | undefined): string {
  const amount = Number(value);
  if (!Number.isFinite(amount)) return '--';
  if (amount === 0) return '$0';
  const absolute = Math.abs(amount);
  const zeros =
    absolute < 1 ? Math.max(0, -Math.floor(Math.log10(absolute)) - 1) : 0;
  const maximumFractionDigits = absolute >= 1 ? 2 : Math.min(20, 4 + zeros);
  return `$${amount.toLocaleString('en-US', {
    minimumFractionDigits: absolute >= 1 ? 2 : 0,
    maximumFractionDigits,
  })}`;
}

export function formatChange(value: string | undefined): string {
  const amount = Number(value);
  if (!Number.isFinite(amount)) return '--';
  const isCapped = Math.abs(amount) > 99_999;
  const capped = Math.max(-99_999, Math.min(99_999, amount));
  const sign = capped >= 0 ? '+' : '-';
  const formatted = Math.abs(capped).toLocaleString('en-US', {
    minimumFractionDigits: isCapped ? 0 : 2,
    maximumFractionDigits: isCapped ? 0 : 2,
  });
  return `${isCapped ? '>' : ''}${sign}${formatted}%`;
}

// Copied from app-monorepo 551eb38fc1ec tradingHoursUtils.ts for the stock tag.
export type IFetchUSMarketStatusResult = {
  open: boolean;
  session: 'PRE_MARKET' | 'REGULAR' | 'POST_MARKET' | 'OVERNIGHT' | 'CLOSED';
  reason: string | null;
  unavailable?: boolean;
};

/**
 * US tokenized-stock trading sessions, defined in America/New_York wall-clock
 * time (the single source of truth per OK-58043). All local rendering must be
 * derived from these constants — never hardcode a user-local time.
 *
 * Ranges follow the venue's published windows (OK-58509, aligned with OKX):
 *
 *   Pre-market  04:01 – 09:29 ET
 *   Regular     09:31 – 15:59 ET
 *   Post-market 16:01 – 19:59 ET
 *   Overnight   20:05 – 03:55 ET (next day)
 *
 * Sessions are NOT contiguous: the minutes between them (e.g. 09:30, the
 * opening cross) belong to no session — the underlying venues pause trading
 * while switching session state. Those windows are short (1–5 min) and token
 * trading stays available, so the chip shows a dedicated "Awaiting open"
 * state while the trading-hours panel highlights the upcoming session row
 * (OK-58986) — never a halt/closed flash.
 *
 * A "cycle" is one full loop from the pre-market open to the overnight close
 * the next ET day. On DST transition days a cycle is ±1h long; ratios are
 * computed from real instants so segment widths stay proportional.
 */

const NY_TIME_ZONE = 'America/New_York';
const MINUTE_MS = 60 * 1000;
const MINUTES_PER_DAY = 24 * 60;

export enum EUSMarketSessionKey {
  PreMarket = 'preMarket',
  Regular = 'regular',
  PostMarket = 'postMarket',
  Overnight = 'overnight',
}

/**
 * Session boundaries as minutes since ET midnight, in cycle order. Ends are
 * exclusive (a `09:30` end renders as `09:29`); a value ≥ 24h means the
 * session closes on the next ET calendar day.
 */
const SESSIONS_ET_MINUTES: Array<{
  key: EUSMarketSessionKey;
  startMinutes: number;
  endMinutes: number;
}> = [
  {
    key: EUSMarketSessionKey.PreMarket,
    startMinutes: 4 * 60 + 1,
    endMinutes: 9 * 60 + 30,
  },
  {
    key: EUSMarketSessionKey.Regular,
    startMinutes: 9 * 60 + 31,
    endMinutes: 16 * 60,
  },
  {
    key: EUSMarketSessionKey.PostMarket,
    startMinutes: 16 * 60 + 1,
    endMinutes: 20 * 60,
  },
  {
    key: EUSMarketSessionKey.Overnight,
    startMinutes: 20 * 60 + 5,
    endMinutes: MINUTES_PER_DAY + 3 * 60 + 56,
  },
];
const CYCLE_START_ET_MINUTES = SESSIONS_ET_MINUTES[0].startMinutes;

interface INyWallClock {
  year: number;
  month: number; // 1-12
  day: number;
  hour: number;
  minute: number;
  /** 0 = Sunday ... 6 = Saturday */
  weekday: number;
}

const WEEKDAY_INDEX: Record<string, number> = {
  Sun: 0,
  Mon: 1,
  Tue: 2,
  Wed: 3,
  Thu: 4,
  Fri: 5,
  Sat: 6,
};

let cachedNyFormatter: Intl.DateTimeFormat | null | undefined;

function getNyFormatter(): Intl.DateTimeFormat | null {
  if (cachedNyFormatter !== undefined) {
    return cachedNyFormatter;
  }
  try {
    cachedNyFormatter = new Intl.DateTimeFormat('en-US', {
      timeZone: NY_TIME_ZONE,
      hourCycle: 'h23',
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      weekday: 'short',
    });
    // Probe once — some engines only throw on first format call.
    cachedNyFormatter.formatToParts(new Date());
  } catch {
    cachedNyFormatter = null;
  }
  return cachedNyFormatter;
}

/**
 * Approximate ET offset used ONLY when Intl lacks IANA time-zone data
 * (defense-in-depth for stripped-down JS engines). US DST rule since 2007:
 * EDT (UTC-4) from 2:00 local on the second Sunday of March until 2:00 local
 * on the first Sunday of November, otherwise EST (UTC-5). Evaluated in UTC —
 * the ±hours error window around the exact switch moment is acceptable for a
 * fallback path.
 */
export function approximateNyOffsetMinutes(instantMs: number): number {
  const d = new Date(instantMs);
  const year = d.getUTCFullYear();
  // 2:00 local = 07:00 UTC (EST) at the spring switch, 06:00 UTC (EDT) in fall
  const nthSundayUtc = (month: number, nth: number, utcHour: number) => {
    const firstDayWeekday = new Date(Date.UTC(year, month, 1)).getUTCDay();
    const firstSunday = 1 + ((7 - firstDayWeekday) % 7);
    return Date.UTC(year, month, firstSunday + (nth - 1) * 7, utcHour, 0);
  };
  const dstStart = nthSundayUtc(2, 2, 7); // second Sunday of March
  const dstEnd = nthSundayUtc(10, 1, 6); // first Sunday of November
  const isDst = instantMs >= dstStart && instantMs < dstEnd;
  return isDst ? -4 * 60 : -5 * 60;
}

function getNyWallClock(instantMs: number): INyWallClock {
  const formatter = getNyFormatter();
  if (formatter) {
    const parts = formatter.formatToParts(new Date(instantMs));
    const get = (type: Intl.DateTimeFormatPartTypes) =>
      parts.find(p => p.type === type)?.value ?? '';
    const weekday = WEEKDAY_INDEX[get('weekday')];
    const hourText = get('hour');
    return {
      year: Number(get('year')),
      month: Number(get('month')),
      day: Number(get('day')),
      // Some ICU builds render midnight as "24" with h23 — normalize it.
      hour: Number(hourText) % 24,
      minute: Number(get('minute')),
      weekday: weekday ?? new Date(instantMs).getUTCDay(),
    };
  }
  const shifted = new Date(
    instantMs + approximateNyOffsetMinutes(instantMs) * MINUTE_MS,
  );
  return {
    year: shifted.getUTCFullYear(),
    month: shifted.getUTCMonth() + 1,
    day: shifted.getUTCDate(),
    hour: shifted.getUTCHours(),
    minute: shifted.getUTCMinutes(),
    weekday: shifted.getUTCDay(),
  };
}

/**
 * Convert an ET wall-clock time to a UTC instant. Starts from a UTC guess and
 * converges by comparing the guess's actual ET wall clock — two rounds are
 * enough for any fixed-offset error, including across DST transitions.
 */
function nyWallClockToInstant({
  year,
  month,
  day,
  minutesOfDay,
}: {
  year: number;
  month: number;
  day: number;
  minutesOfDay: number;
}): number {
  const targetAsUtc = Date.UTC(
    year,
    month - 1,
    day,
    Math.floor(minutesOfDay / 60),
    minutesOfDay % 60,
  );
  let guess = targetAsUtc - approximateNyOffsetMinutes(targetAsUtc) * MINUTE_MS;
  for (let i = 0; i < 2; i += 1) {
    const wc = getNyWallClock(guess);
    const guessAsUtc = Date.UTC(
      wc.year,
      wc.month - 1,
      wc.day,
      wc.hour,
      wc.minute,
    );
    const diff = targetAsUtc - guessAsUtc;
    if (diff === 0) {
      break;
    }
    guess += diff;
  }
  return guess;
}

function shiftNyDate(
  base: { year: number; month: number; day: number },
  days: number,
): { year: number; month: number; day: number } {
  const d = new Date(Date.UTC(base.year, base.month - 1, base.day + days));
  return {
    year: d.getUTCFullYear(),
    month: d.getUTCMonth() + 1,
    day: d.getUTCDate(),
  };
}

export interface IUSMarketSessionSegment {
  key: EUSMarketSessionKey;
  /** UTC ms, inclusive */
  startInstant: number;
  /** UTC ms, exclusive */
  endInstant: number;
  /** Fraction of the whole cycle this segment spans (0..1) */
  ratio: number;
}

export interface IUSMarketTradingHours {
  /** Segments in cycle order: pre → regular → post → overnight (with gaps) */
  segments: IUSMarketSessionSegment[];
  cycleStartInstant: number;
  cycleEndInstant: number;
  /** Position of `now` within the cycle, 0..1 */
  nowRatio: number;
  /**
   * Session containing `now` by pure clock math (ignores holidays/halts).
   * A `now` inside an inter-session gap resolves to the upcoming session
   * (past the overnight close that is the NEXT cycle's first session) —
   * check `isNowInSessionGap` to tell the two cases apart.
   */
  currentSessionKey: EUSMarketSessionKey;
  /**
   * True when `now` falls between two sessions (e.g. after the 03:55
   * overnight close and before the 04:01 pre-market open): the underlying
   * venues pause trading while switching session state. Phantom clock gaps
   * inside the weekend closure do NOT count. `currentSessionKey` resolves to
   * the upcoming session for these short windows.
   */
  isNowInSessionGap: boolean;
  /** Current-or-upcoming weekend closure: Friday 20:00 ET */
  weekendStartInstant: number;
  /** Weekend closure end: Sunday 20:00 ET */
  weekendEndInstant: number;
}

/**
 * Compute the trading-hours cycle containing `now`, with every boundary as a
 * UTC instant. Callers render instants in the device time zone (plain Date
 * getters) — no further zone math needed anywhere else.
 */
export function getUSMarketTradingHours(
  now: Date = new Date(),
): IUSMarketTradingHours {
  const nowMs = now.getTime();
  const nowNy = getNyWallClock(nowMs);

  // The cycle day is the ET calendar date whose pre-market anchor (04:01)
  // precedes `now`.
  let cycleDate = {
    year: nowNy.year,
    month: nowNy.month,
    day: nowNy.day,
  };
  if (nowNy.hour * 60 + nowNy.minute < CYCLE_START_ET_MINUTES) {
    cycleDate = shiftNyDate(cycleDate, -1);
  }
  const nextDate = shiftNyDate(cycleDate, 1);

  const minutesToInstant = (minutes: number) =>
    minutes >= MINUTES_PER_DAY
      ? nyWallClockToInstant({
          ...nextDate,
          minutesOfDay: minutes - MINUTES_PER_DAY,
        })
      : nyWallClockToInstant({ ...cycleDate, minutesOfDay: minutes });

  const instants = SESSIONS_ET_MINUTES.map(
    ({ key, startMinutes, endMinutes }) => ({
      key,
      startInstant: minutesToInstant(startMinutes),
      endInstant: minutesToInstant(endMinutes),
    }),
  );
  const cycleStartInstant = instants[0].startInstant;
  const cycleEndInstant = instants[instants.length - 1].endInstant;
  const cycleDuration = cycleEndInstant - cycleStartInstant;

  const segments: IUSMarketSessionSegment[] = instants.map(
    ({ key, startInstant, endInstant }) => ({
      key,
      startInstant,
      endInstant,
      ratio: (endInstant - startInstant) / cycleDuration,
    }),
  );

  const clampedNow = Math.min(
    Math.max(nowMs, cycleStartInstant),
    cycleEndInstant - 1,
  );
  // Sessions are not contiguous — a `now` inside an inter-session gap
  // resolves to the upcoming session.
  const currentSegment =
    segments.find(s => clampedNow < s.endInstant) ??
    segments[segments.length - 1];

  // Weekend closure runs Friday 20:00 ET → Sunday 20:00 ET. Pick the ongoing
  // weekend when the cycle day sits inside one, otherwise the upcoming one.
  const { weekday } = getNyWallClock(cycleStartInstant);
  let fridayShift: number;
  if (weekday === 6) {
    fridayShift = -1;
  } else if (weekday === 0) {
    fridayShift = -2;
  } else {
    fridayShift = 5 - weekday;
  }
  const fridayDate = shiftNyDate(cycleDate, fridayShift);
  const sundayDate = shiftNyDate(fridayDate, 2);
  const weekendStartInstant = nyWallClockToInstant({
    ...fridayDate,
    minutesOfDay: 20 * 60,
  });
  const weekendEndInstant = nyWallClockToInstant({
    ...sundayDate,
    minutesOfDay: 20 * 60,
  });

  // In a gap, `now` sits before the upcoming session's start (mid-cycle gaps)
  // or past the overnight close (the cycle-edge gap). The weekend closure is
  // NOT a gap: the clock still produces phantom session boundaries on
  // Sat/Sun, but no session switch is happening, so treating those minutes
  // as "about to open" (or as a reason to suppress a halt) would be wrong.
  const isNowInSessionGap =
    (nowMs < currentSegment.startInstant ||
      nowMs >= currentSegment.endInstant) &&
    !(nowMs >= weekendStartInstant && nowMs < weekendEndInstant);
  // Past the overnight close the upcoming session belongs to the NEXT cycle —
  // remap the key so a gap `now` always resolves to the upcoming session, as
  // the field's contract promises.
  const currentSessionKey =
    nowMs >= cycleEndInstant ? segments[0].key : currentSegment.key;

  return {
    segments,
    cycleStartInstant,
    cycleEndInstant,
    nowRatio: (clampedNow - cycleStartInstant) / cycleDuration,
    currentSessionKey,
    isNowInSessionGap,
    weekendStartInstant,
    weekendEndInstant,
  };
}

export function usMarketSessionKeyFromBackendSession(
  session: IFetchUSMarketStatusResult['session'] | undefined,
): EUSMarketSessionKey | undefined {
  switch (session) {
    case 'PRE_MARKET':
      return EUSMarketSessionKey.PreMarket;
    case 'REGULAR':
      return EUSMarketSessionKey.Regular;
    case 'POST_MARKET':
      return EUSMarketSessionKey.PostMarket;
    case 'OVERNIGHT':
      return EUSMarketSessionKey.Overnight;
    default:
      return undefined;
  }
}

/**
 * Display status of a tokenized stock: the four US sessions plus
 * closed/halted, and "24/7" for tokens that keep trading through a
 * market-wide closure. The badge describes the UNDERLYING market state —
 * trading availability is decided by the quote path (providers may still
 * fill orders while the market is closed or halted, OK-58986); Open247 is
 * the one exception, surfacing that a 7×24 instrument stays tradable on
 * weekends/holidays instead of a discouraging "Closed".
 */
export enum EUSMarketStatusVariant {
  PreMarket = 'preMarket',
  Open = 'open',
  PostMarket = 'postMarket',
  Overnight = 'overnight',
  Closed = 'closed',
  Open247 = 'open247',
  /** Inter-session gap: the venue is switching session state (1–5 min). */
  AwaitingOpen = 'awaitingOpen',
  Halted = 'halted',
}

/**
 * A paused signal is a genuine per-stock halt EXCEPT for one pattern: an
 * explicitly tradable instrument (`isOpen === true`) flagged paused during a
 * trading-day session switch — venues routinely raise that transient flag,
 * so the gap handling wins over the halt display there (OK-58986); a real
 * halt spanning the gap surfaces once the gap ends. Instruments without
 * `isOpen === true` keep reading Halted straight through gaps — falling
 * through would flicker them to Closed/no-chip for the gap minutes. A live
 * market-wide closure (weekend is clock-detectable, holidays only via
 * `status`) also disables the exception: clock gaps then are phantom — no
 * session switch is happening. Takes a getter so callers with a lazily
 * computed cycle only pay for it when actually paused.
 */
function isEffectiveUSMarketHalt({
  isPaused,
  isOpen,
  status,
  getTradingHours,
}: {
  isPaused: boolean | undefined;
  isOpen: boolean | undefined;
  status: IFetchUSMarketStatusResult | undefined;
  getTradingHours: () => IUSMarketTradingHours;
}): boolean {
  if (isPaused !== true) {
    return false;
  }
  const marketWideClosed =
    !!status &&
    !status.unavailable &&
    (!status.open || status.session === 'CLOSED');
  return !(
    isOpen === true &&
    !marketWideClosed &&
    getTradingHours().isNowInSessionGap
  );
}

/**
 * Sources treated as Ondo-issued. Some Ondo stocks report a legacy
 * 'coingecko' source — keep this the single source of truth (kit's
 * `isOndoStockSource` delegates here).
 */
const ONDO_US_MARKET_STOCK_SOURCES = new Set(['ondo', 'coingecko']);

/**
 * Only Ondo-issued stock tokens follow the US-session trading model this
 * feature describes (OK-58043). xStocks and other providers run 7×24 with no
 * open/closed distinction, so they keep the legacy open/closed badge and get
 * no trading-hours entry.
 */
export function isOndoUSMarketStock(
  source: string | undefined | null,
): boolean {
  const normalized = source?.trim().toLowerCase();
  return !!normalized && ONDO_US_MARKET_STOCK_SOURCES.has(normalized);
}

/**
 * Single source of truth for a stock token's displayed market status.
 * Returns undefined for non-Ondo issuers (callers fall back to the legacy
 * two-state badge) and for tokens without stock signals.
 *
 * Signal priority:
 *   1. per-stock halt (`isPaused`) → Halted, regardless of `isOpen` — the
 *      badge reports the underlying stock's state; whether the token can
 *      still trade is the quote path's concern (OK-58986). One exception,
 *      see `isEffectiveUSMarketHalt`: an explicitly tradable instrument
 *      paused inside a trading-day session gap is treated as the gap itself;
 *   2. per-stock `isOpen === false` — the instrument's own window is closed;
 *   3. market-wide closure (backend CLOSED / weekend) with `isOpen === true`
 *      → Open247: only 7×24 instruments stay open through a closure, and a
 *      plain "Closed" would hide that they remain tradable;
 *   4. inter-session gap (clock math) → AwaitingOpen: the venue is switching
 *      session state; a halt/closed flash there reads as an error, and the
 *      panel highlights the upcoming session row alongside;
 *   5. market-wide session refines HOW it is open.
 *
 * When the market-status API is unavailable, falls back to pure clock math:
 * weekends are detectable locally, holidays are not.
 */
export function resolveUSMarketStatusVariant({
  source,
  isOpen,
  isPaused,
  status,
  now = new Date(),
}: {
  source?: string;
  isOpen?: boolean;
  isPaused?: boolean;
  status?: IFetchUSMarketStatusResult;
  now?: Date;
}): EUSMarketStatusVariant | undefined {
  if (!isOndoUSMarketStock(source)) {
    return undefined;
  }
  // The cycle computation is Intl-heavy — compute it lazily, at most once,
  // and only on the paths that need a session/gap decision.
  let cachedTradingHours: IUSMarketTradingHours | undefined;
  const resolveTradingHours = () => {
    if (!cachedTradingHours) {
      cachedTradingHours = getUSMarketTradingHours(now);
    }
    return cachedTradingHours;
  };
  if (
    isEffectiveUSMarketHalt({
      isPaused,
      isOpen,
      status,
      getTradingHours: resolveTradingHours,
    })
  ) {
    return EUSMarketStatusVariant.Halted;
  }
  if (isOpen === undefined) {
    return undefined;
  }
  if (isOpen === false) {
    return EUSMarketStatusVariant.Closed;
  }
  const sessionKeyToVariant = (key: EUSMarketSessionKey) => {
    switch (key) {
      case EUSMarketSessionKey.PreMarket:
        return EUSMarketStatusVariant.PreMarket;
      case EUSMarketSessionKey.PostMarket:
        return EUSMarketStatusVariant.PostMarket;
      case EUSMarketSessionKey.Overnight:
        return EUSMarketStatusVariant.Overnight;
      case EUSMarketSessionKey.Regular:
      default:
        return EUSMarketStatusVariant.Open;
    }
  };
  if (status && !status.unavailable) {
    if (!status.open || status.session === 'CLOSED') {
      // isOpen === true is guaranteed here (checked above): a token still
      // open through a market-wide closure is a 7×24 instrument.
      return EUSMarketStatusVariant.Open247;
    }
    const tradingHours = resolveTradingHours();
    // Inter-session gap: the dedicated chip wins over the backend session,
    // which may still report the one that just ended.
    if (tradingHours.isNowInSessionGap) {
      return EUSMarketStatusVariant.AwaitingOpen;
    }
    const sessionKey = usMarketSessionKeyFromBackendSession(status.session);
    // Unknown session value (future contract addition): fall back to the
    // clock session, matching resolveUSTradingHoursActiveRow.
    return sessionKeyToVariant(sessionKey ?? tradingHours.currentSessionKey);
  }
  const tradingHours = resolveTradingHours();
  const nowMs = now.getTime();
  if (
    nowMs >= tradingHours.weekendStartInstant &&
    nowMs < tradingHours.weekendEndInstant
  ) {
    // Same 7×24 rule as the status branch: isOpen === true through the
    // weekend closure marks a 7×24 instrument.
    return EUSMarketStatusVariant.Open247;
  }
  // Same gap rule as the status branch, on the pure clock fallback.
  if (tradingHours.isNowInSessionGap) {
    return EUSMarketStatusVariant.AwaitingOpen;
  }
  return sessionKeyToVariant(tradingHours.currentSessionKey);
}
