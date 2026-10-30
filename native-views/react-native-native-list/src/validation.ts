import { ROW_BOX_STYLE_KEYS_BY_TYPE } from './models';
import type {
  ActivityRow,
  ButtonGroupRow,
  IdentityRow,
  ImageSource,
  LeadingVisual,
  MarketRow,
  MarketRowStyle,
  NativeListListStyle,
  NativeListSnapshot,
  NativeListTextStyle,
  NativeListTypographyToken,
  RowBoxStyle,
  RowModel,
  RowPatch,
  RowTextHelp,
  WalletGroupRow,
  WebMarketTableCell,
  WebMarketTablePresentation,
  WebMarketTableText,
} from './models';

const MAX_KEY_LENGTH = 256;
const MAX_TEXT_LENGTH = 4096;
const MAX_BADGES = 2;
const MAX_TRAILING_ACCESSORIES = 2;
const MAX_FOOTER_ACTIONS = 3;
const MAX_IMAGE_EDGE = 4096;
const MAX_IMAGE_HEADERS = 32;
const MAX_HEADER_LENGTH = 4096;
const MAX_SPACER_HEIGHT = 512;
const MAX_SECTION_INDEX_TITLE_LENGTH = 8;
const MAX_MARKET_BADGES = 3;

function isDevelopment(): boolean {
  return typeof __DEV__ !== 'undefined' && __DEV__;
}

class NativeListInputError extends Error {
  constructor(readonly path: string, readonly reason: string) {
    super(`NativeList ${path}: ${reason}`);
  }
}

function fail(path: string, message: string): never {
  throw new NativeListInputError(path, message);
}

/** Component boundaries report locations, never caller-controlled payload values. */
export function containNativeListInput<T>(operation: () => T): T | undefined {
  try {
    return operation();
  } catch (error) {
    const path = error instanceof NativeListInputError ? error.path : 'input';
    const reason =
      isDevelopment() && error instanceof NativeListInputError
        ? `: ${error.reason.split('"')[0].trim()}`
        : '';
    console.warn(`NativeList rejected invalid input at ${path}${reason}`);
    return undefined;
  }
}

export function emptyNativeListSnapshot(): NativeListSnapshot {
  return {
    schemaVersion: 1,
    generation: 0,
    layout: { kind: 'linear' },
    rows: [],
  };
}

function assertPlainSerializable(
  value: unknown,
  path: string,
  seen: Set<object>,
  requireFinite = true
): void {
  if (value === null || value === undefined) return;
  const valueType = typeof value;
  if (valueType === 'string' || valueType === 'boolean') return;
  if (valueType === 'number') {
    if (requireFinite && !Number.isFinite(value))
      fail(path, 'numbers must be finite');
    return;
  }
  if (valueType !== 'object')
    fail(path, 'must contain only plain serializable data');

  const objectValue = value as object;
  if (seen.has(objectValue)) fail(path, 'must not contain circular references');
  seen.add(objectValue);
  if (Array.isArray(value)) {
    value.forEach((item, index) =>
      assertPlainSerializable(item, `${path}[${index}]`, seen, requireFinite)
    );
  } else {
    const prototype = Object.getPrototypeOf(value);
    if (prototype !== Object.prototype && prototype !== null) {
      fail(path, 'must contain plain objects only');
    }
    Object.entries(value as Record<string, unknown>).forEach(([key, item]) => {
      if (
        key === '__proto__' ||
        (key === 'constructor' && typeof item === 'object')
      )
        fail(path, 'unsafe object key');
      assertPlainSerializable(item, `${path}.${key}`, seen, requireFinite);
    });
  }
  seen.delete(objectValue);
}

function assertKey(key: string, path: string): void {
  if (
    typeof key !== 'string' ||
    key.trim().length === 0 ||
    key.length > MAX_KEY_LENGTH
  ) {
    fail(
      path,
      `must be a non-blank string no longer than ${MAX_KEY_LENGTH} characters`
    );
  }
}

function assertText(text: string | undefined, path: string): void {
  if (text === undefined) return;
  if (typeof text !== 'string' || text.length > MAX_TEXT_LENGTH) {
    fail(path, `must be a string no longer than ${MAX_TEXT_LENGTH} characters`);
  }
}

function assertSectionIndexTitle(
  title: string | undefined,
  path: string
): void {
  if (title === undefined) return;
  if (
    typeof title !== 'string' ||
    title.length === 0 ||
    Array.from(title).length > MAX_SECTION_INDEX_TITLE_LENGTH
  ) {
    fail(
      path,
      `must be a non-empty string no longer than ${MAX_SECTION_INDEX_TITLE_LENGTH} Unicode code points`
    );
  }
  if (title.trim() !== title) {
    fail(path, 'must not have leading or trailing whitespace');
  }
}

function assertTextTone(tone: string | undefined, path: string): void {
  if (
    tone !== undefined &&
    !['primary', 'secondary', 'positive', 'negative'].includes(tone)
  ) {
    fail(path, 'must be primary, secondary, positive, or negative');
  }
}

function assertBoundedStyleNumber(
  value: number | undefined,
  path: string,
  min: number,
  max: number
): void {
  if (
    value !== undefined &&
    (!Number.isFinite(value) || value < min || value > max)
  ) {
    fail(path, `must be within ${min}...${max}`);
  }
}

/**
 * The named typography scale from docs/STYLE_SPEC.md §3.2. Tokens are resolved
 * to numbers here, before serialization, so the iOS, Android, and Web renderers
 * never learn the vocabulary and cannot drift from it.
 */
const TYPOGRAPHY_TOKENS: Readonly<
  Record<
    NativeListTypographyToken,
    Readonly<{
      fontSize: number;
      lineHeight: number;
      fontWeight: NonNullable<NativeListTextStyle['fontWeight']>;
    }>
  >
> = {
  $headingXl: { fontSize: 24, lineHeight: 32, fontWeight: 'semibold' },
  $headingLg: { fontSize: 20, lineHeight: 28, fontWeight: 'semibold' },
  $headingMd: { fontSize: 18, lineHeight: 24, fontWeight: 'semibold' },
  $headingSm: { fontSize: 16, lineHeight: 24, fontWeight: 'medium' },
  $headingXs: { fontSize: 14, lineHeight: 20, fontWeight: 'semibold' },
  $bodyLg: { fontSize: 16, lineHeight: 24, fontWeight: 'regular' },
  $bodyMd: { fontSize: 14, lineHeight: 20, fontWeight: 'regular' },
  $bodySm: { fontSize: 12, lineHeight: 16, fontWeight: 'regular' },
  $bodyXs: { fontSize: 11, lineHeight: 16, fontWeight: 'regular' },
};

/** Style keys name model fields, never views. See docs/STYLE_SPEC.md §4. */
const TEXT_STYLE_KEYS_BY_ROW_TYPE: Readonly<
  Record<RowModel['type'], readonly string[]>
> = {
  identity: [
    'title',
    'subtitle',
    'tertiary',
    'badge',
    'value',
    'valueSecondary',
  ],
  walletGroup: [],
  rail: ['title', 'badge', 'status'],
  activity: [
    'title',
    'description',
    'status',
    'primaryAmount',
    'secondaryAmount',
  ],
  message: ['title', 'body', 'time'],
  dataRow: ['columns', 'columnSecondary', 'index'],
  market: ['title', 'subtitle', 'price', 'change'],
  mediaTile: ['title', 'subtitle', 'badge'],
  metricCard: ['title', 'value', 'subtitle', 'trend'],
  sectionHeader: ['title', 'subtitle', 'value'],
  action: ['title', 'value'],
  buttonGroup: [],
  system: ['title', 'message', 'actionText'],
};

const EXTRA_STYLE_KEYS_BY_ROW_TYPE: Readonly<
  Partial<Record<RowModel['type'], readonly string[]>>
> = {
  identity: ['leadingContentMaxWidthRatio'],
  market: [
    'titleBadgeLayout',
    'contentTrailingGap',
    'subtitleTrailingPadding',
    'changeWidth',
    'changeHeight',
    'changeCornerRadius',
  ],
  sectionHeader: ['valuePlacement', 'titleDecoration'],
};

function assertTextStyle(
  style: NativeListTextStyle | undefined,
  path: string,
  additionalKeys: readonly string[] = []
): void {
  if (style === undefined) return;
  if (typeof style !== 'object' || style === null || Array.isArray(style)) {
    fail(path, 'must be an object');
  }
  assertUnknownKeys(
    style as Record<string, unknown>,
    [
      'token',
      'fontSize',
      'fontWeight',
      'color',
      'lineHeight',
      'lines',
      'truncate',
      'alignment',
      'verticalAlignment',
      'offsetY',
      ...additionalKeys,
    ],
    path,
    'text style key'
  );
  if (
    style.color !== undefined &&
    (typeof style.color !== 'string' ||
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(style.color))
  ) {
    fail(`${path}.color`, 'must be #RRGGBB or #RRGGBBAA');
  }
  if (
    style.token !== undefined &&
    !Object.prototype.hasOwnProperty.call(TYPOGRAPHY_TOKENS, style.token)
  ) {
    fail(
      `${path}.token`,
      `must be one of ${Object.keys(TYPOGRAPHY_TOKENS).join(', ')}`
    );
  }
  assertBoundedStyleNumber(style.fontSize, `${path}.fontSize`, 8, 48);
  assertBoundedStyleNumber(style.lineHeight, `${path}.lineHeight`, 8, 64);
  if (
    style.fontWeight !== undefined &&
    !['regular', 'medium', 'semibold', 'bold'].includes(style.fontWeight)
  ) {
    fail(`${path}.fontWeight`, 'must be regular, medium, semibold, or bold');
  }
  if (
    style.alignment !== undefined &&
    !['start', 'center', 'end'].includes(style.alignment)
  ) {
    fail(`${path}.alignment`, 'must be start, center, or end');
  }
  if (style.lines !== undefined && ![1, 2, 3].includes(style.lines)) {
    fail(`${path}.lines`, 'must be 1, 2, or 3');
  }
  if (
    style.truncate !== undefined &&
    !['tail', 'clip'].includes(style.truncate)
  ) {
    fail(`${path}.truncate`, 'must be tail or clip');
  }
  if (
    style.verticalAlignment !== undefined &&
    !['top', 'center', 'bottom'].includes(style.verticalAlignment)
  ) {
    fail(`${path}.verticalAlignment`, 'must be top, center, or bottom');
  }
  assertBoundedStyleNumber(style.offsetY, `${path}.offsetY`, -8, 8);
}

function assertBoxStyle(style: RowBoxStyle, path: string): void {
  const container = style.container;
  if (container !== undefined) {
    const containerPath = `${path}.container`;
    if (
      typeof container !== 'object' ||
      container === null ||
      Array.isArray(container)
    ) {
      fail(containerPath, 'must be an object');
    }
    assertUnknownKeys(
      container as Record<string, unknown>,
      [
        'height',
        'backgroundColor',
        'opacity',
        'cornerRadius',
        'borderWidth',
        'borderColor',
        'contentVerticalAlignment',
      ],
      containerPath,
      'row container style key'
    );
    for (const key of ['backgroundColor', 'borderColor'] as const) {
      const value = container[key];
      if (
        value !== undefined &&
        (typeof value !== 'string' ||
          !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(value))
      ) {
        fail(`${containerPath}.${key}`, 'must be #RRGGBB or #RRGGBBAA');
      }
    }
    assertBoundedStyleNumber(
      container.height,
      `${containerPath}.height`,
      0,
      4096
    );
    assertBoundedStyleNumber(
      container.opacity,
      `${containerPath}.opacity`,
      0,
      1
    );
    assertBoundedStyleNumber(
      container.cornerRadius,
      `${containerPath}.cornerRadius`,
      0,
      80
    );
    assertBoundedStyleNumber(
      container.borderWidth,
      `${containerPath}.borderWidth`,
      0,
      8
    );
    if (
      container.contentVerticalAlignment !== undefined &&
      !['top', 'center', 'bottom'].includes(container.contentVerticalAlignment)
    ) {
      fail(
        `${containerPath}.contentVerticalAlignment`,
        'must be top, center, or bottom'
      );
    }
  }
  for (const field of [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'titleBadgeGap',
    'trailingGap',
  ] as const) {
    assertBoundedStyleNumber(style[field], `${path}.${field}`, 0, 64);
  }
  assertBoundedStyleNumber(style.lineGap, `${path}.lineGap`, 0, 16);
  if (style.image !== undefined) {
    if (
      typeof style.image !== 'object' ||
      style.image === null ||
      Array.isArray(style.image)
    ) {
      fail(`${path}.image`, 'must be an object');
    }
    assertUnknownKeys(
      style.image as Record<string, unknown>,
      ['width', 'height', 'shape', 'cornerRadius', 'contentFit'],
      `${path}.image`,
      'image style key'
    );
    assertBoundedStyleNumber(style.image.width, `${path}.image.width`, 1, 160);
    assertBoundedStyleNumber(
      style.image.height,
      `${path}.image.height`,
      1,
      160
    );
    assertBoundedStyleNumber(
      style.image.cornerRadius,
      `${path}.image.cornerRadius`,
      0,
      80
    );
    assertVisualShape(style.image.shape, `${path}.image.shape`);
    if (
      style.image.contentFit !== undefined &&
      !['cover', 'contain', 'fill', 'center'].includes(style.image.contentFit)
    ) {
      fail(
        `${path}.image.contentFit`,
        'must be cover, contain, fill, or center'
      );
    }
  }
}

function assertMarketStyle(
  style: MarketRowStyle | undefined,
  path: string
): void {
  if (!style) return;
  assertBoxStyle(style, path);
  for (const field of [
    'contentTrailingGap',
    'subtitleTrailingPadding',
  ] as const) {
    assertBoundedStyleNumber(style[field], `${path}.${field}`, 0, 64);
  }
  // OneKey patch: validate the opt-in Market badge layout.
  if (
    style.titleBadgeLayout !== undefined &&
    style.titleBadgeLayout !== 'inline'
  ) {
    fail(`${path}.titleBadgeLayout`, 'must be inline when provided');
  }
  assertBoundedStyleNumber(style.changeWidth, `${path}.changeWidth`, 1, 160);
  assertBoundedStyleNumber(style.changeHeight, `${path}.changeHeight`, 1, 160);
  assertBoundedStyleNumber(
    style.changeCornerRadius,
    `${path}.changeCornerRadius`,
    0,
    80
  );
  assertTextStyle(style.title, `${path}.title`);
  assertTextStyle(style.subtitle, `${path}.subtitle`);
  assertTextStyle(style.price, `${path}.price`);
  assertTextStyle(style.change, `${path}.change`);
}

/**
 * Rejects keys the template does not declare, so a style written for one
 * template cannot reach a shared view through another. See docs/STYLE_SPEC.md
 * §7 rule 1.
 */
function assertRowStyle(row: RowModel, path: string): void {
  const style = (row as { style?: unknown }).style;
  if (style === undefined) return;
  if (typeof style !== 'object' || style === null || Array.isArray(style)) {
    fail(`${path}.style`, 'must be an object');
  }
  const textKeys = TEXT_STYLE_KEYS_BY_ROW_TYPE[row.type] ?? [];
  const allowed = new Set([
    'container',
    ...ROW_BOX_STYLE_KEYS_BY_TYPE[row.type],
    ...textKeys,
    ...(EXTRA_STYLE_KEYS_BY_ROW_TYPE[row.type] ?? []),
  ]);
  Object.keys(style as Record<string, unknown>).forEach((key) => {
    if (!allowed.has(key)) {
      fail(
        `${path}.style.${key}`,
        `is not a style key of the "${row.type}" template`
      );
    }
  });
  // Market keeps its own richer assertions, driven from assertMarketRow.
  if (row.type === 'market') return;
  assertBoxStyle(style as RowBoxStyle, `${path}.style`);
  if (row.type === 'identity') {
    const ratio = (style as IdentityRow['style'])?.leadingContentMaxWidthRatio;
    if (
      ratio !== undefined &&
      (!Number.isFinite(ratio) || ratio <= 0 || ratio > 1)
    )
      fail(
        `${path}.style.leadingContentMaxWidthRatio`,
        'must be within (0, 1]'
      );
  }
  if (row.type === 'sectionHeader') {
    const sectionStyle = style as Extract<
      RowModel,
      { type: 'sectionHeader' }
    >['style'];
    if (
      sectionStyle?.valuePlacement !== undefined &&
      sectionStyle.valuePlacement !== 'afterTitle'
    )
      fail(`${path}.style.valuePlacement`, 'must be afterTitle');
    if (
      sectionStyle?.titleDecoration !== undefined &&
      !['none', 'dashed'].includes(sectionStyle.titleDecoration)
    )
      fail(`${path}.style.titleDecoration`, 'must be none or dashed');
  }
  const slots = style as Record<string, NativeListTextStyle | undefined>;
  textKeys.forEach((key) =>
    assertTextStyle(slots[key], `${path}.style.${key}`)
  );
}

const LIST_STYLE_KEYS: readonly string[] = ['separator', 'groupCornerRadius'];
const SEPARATOR_STYLE_KEYS: readonly string[] = ['inset', 'color'];

function assertUnknownKeys(
  value: Record<string, unknown>,
  allowed: readonly string[],
  path: string,
  subject: string
): void {
  Object.keys(value).forEach((key) => {
    if (!allowed.includes(key)) fail(`${path}.${key}`, `is not a ${subject}`);
  });
}

function assertListStyle(
  listStyle: NativeListListStyle | undefined,
  path: string
): void {
  if (listStyle === undefined) return;
  if (
    typeof listStyle !== 'object' ||
    listStyle === null ||
    Array.isArray(listStyle)
  ) {
    fail(path, 'must be an object');
  }
  assertUnknownKeys(
    listStyle as Record<string, unknown>,
    LIST_STYLE_KEYS,
    path,
    'list style key'
  );
  assertBoundedStyleNumber(
    listStyle.groupCornerRadius,
    `${path}.groupCornerRadius`,
    0,
    40
  );
  const separator = listStyle.separator;
  if (separator === undefined) return;
  if (
    typeof separator !== 'object' ||
    separator === null ||
    Array.isArray(separator)
  ) {
    fail(`${path}.separator`, 'must be an object');
  }
  assertUnknownKeys(
    separator as Record<string, unknown>,
    SEPARATOR_STYLE_KEYS,
    `${path}.separator`,
    'separator style key'
  );
  assertBoundedStyleNumber(separator.inset, `${path}.separator.inset`, 0, 64);
  // Same format as every row style color; renderers parse it directly.
  if (
    separator.color !== undefined &&
    (typeof separator.color !== 'string' ||
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(separator.color))
  ) {
    fail(`${path}.separator.color`, 'must be #RRGGBB or #RRGGBBAA');
  }
}

/** Token resolution is idempotent: a resolved style carries no token. */
function resolveTextStyle(
  style: NativeListTextStyle | undefined
): NativeListTextStyle | undefined {
  if (!style?.token) return style;
  const { token, ...overrides } = style;
  return { ...TYPOGRAPHY_TOKENS[token], ...overrides };
}

function resolveStyleTokens(
  style: Record<string, unknown>,
  rowType: RowModel['type']
): Record<string, unknown> {
  const textKeys = TEXT_STYLE_KEYS_BY_ROW_TYPE[rowType] ?? [];
  let changed = false;
  const next: Record<string, unknown> = { ...style };
  textKeys.forEach((key) => {
    const slot = style[key] as NativeListTextStyle | undefined;
    const resolved = resolveTextStyle(slot);
    if (resolved !== slot) {
      next[key] = resolved;
      changed = true;
    }
  });
  return changed ? next : style;
}

function normalizeRowStyles<T extends RowModel>(row: T): T {
  // Narrowing a generic does not reach the walletGroup members; go through the
  // union type instead.
  const model: RowModel = row;
  const style = (row as { style?: Record<string, unknown> }).style;
  const nextStyle = style ? resolveStyleTokens(style, row.type) : style;
  const prefix = model.type === 'market' ? model.subtitlePrefix : undefined;
  const nextPrefixStyle = resolveTextStyle(prefix?.style);
  let nextParent: IdentityRow | undefined;
  let nextChildren: readonly IdentityRow[] | undefined;
  if (model.type === 'walletGroup') {
    const parent = normalizeRowStyles(model.parent);
    const children = model.children.map(normalizeRowStyles);
    if (
      parent !== model.parent ||
      children.some((child, index) => child !== model.children[index])
    ) {
      nextParent = parent;
      nextChildren = children;
    }
  }
  if (
    nextStyle === style &&
    nextPrefixStyle === prefix?.style &&
    nextParent === undefined
  ) {
    return row;
  }
  const next: Record<string, unknown> = { ...row };
  if (nextStyle !== style) next.style = nextStyle;
  if (prefix && nextPrefixStyle !== prefix.style) {
    next.subtitlePrefix = { ...prefix, style: nextPrefixStyle };
  }
  if (nextParent !== undefined) {
    next.parent = nextParent;
    next.children = nextChildren;
  }
  return next as T;
}

function assertMarketBadges(badges: MarketRow['badges'], path: string): void {
  if ((badges?.length ?? 0) > MAX_MARKET_BADGES) {
    fail(`${path}.badges`, `supports at most ${MAX_MARKET_BADGES} badges`);
  }
  const badgeKeys = new Set<string>();
  badges?.forEach((badge, index) => {
    const badgePath = `${path}.badges[${index}]`;
    assertKey(badge.key, `${badgePath}.key`);
    if (badgeKeys.has(badge.key)) {
      fail(`${path}.badges`, `duplicate badge key "${badge.key}"`);
    }
    badgeKeys.add(badge.key);
    assertText(badge.text, `${badgePath}.text`);
    // OneKey patch: share typography bounds with Market text styles.
    assertTextStyle(badge.style, `${badgePath}.style`, [
      'height',
      'horizontalPadding',
    ]);
    if (badge.style)
      assertUnknownKeys(
        badge.style,
        ['fontSize', 'fontWeight', 'lineHeight', 'height', 'horizontalPadding'],
        `${badgePath}.style`,
        'Market badge style key'
      );
    assertBoundedStyleNumber(
      badge.style?.height,
      `${badgePath}.style.height`,
      1,
      64
    );
    assertBoundedStyleNumber(
      badge.style?.horizontalPadding,
      `${badgePath}.style.horizontalPadding`,
      0,
      32
    );
    if (badge.iconName !== undefined && badge.iconName !== 'verified') {
      fail(`${badgePath}.iconName`, 'must be verified when provided');
    }
    if (badge.iconName !== undefined && badge.icon !== undefined) {
      fail(`${badgePath}.icon`, 'cannot be combined with iconName');
    }
    assertImage(badge.icon, `${badgePath}.icon`);
    if (
      badge.tone !== undefined &&
      !['neutral', 'info', 'success', 'warning', 'danger'].includes(badge.tone)
    ) {
      fail(
        `${badgePath}.tone`,
        'must be neutral, info, success, warning, or danger'
      );
    }
    if (!badge.text && !badge.icon && !badge.iconName) {
      fail(badgePath, 'requires text, icon, or iconName');
    }
    if (badge.actionKey !== undefined) {
      assertKey(badge.actionKey, `${badgePath}.actionKey`);
    }
  });
}

function assertWebMarketTable(
  table: WebMarketTablePresentation | undefined,
  path: string
): void {
  if (!table) return;
  assertUnknownKeys(
    table,
    ['columns', 'cells', 'testID', 'draggable', 'contextMenuActionKey'],
    path,
    'Web table key'
  );
  if (table.draggable !== undefined && typeof table.draggable !== 'boolean')
    fail(`${path}.draggable`, 'must be boolean');
  if (table.contextMenuActionKey !== undefined)
    assertKey(table.contextMenuActionKey, `${path}.contextMenuActionKey`);
  assertText(table.testID, `${path}.testID`);
  if (
    !Array.isArray(table.columns) ||
    table.columns.length < 1 ||
    table.columns.length > 12
  )
    fail(`${path}.columns`, 'must contain 1...12 columns');
  if (
    !Array.isArray(table.cells) ||
    table.cells.length !== table.columns.length
  )
    fail(`${path}.cells`, 'must match the column count');
  const keys = new Set<string>();
  table.columns.forEach((column, index) => {
    const field = `${path}.columns[${index}]`;
    assertUnknownKeys(
      column,
      [
        'key',
        'width',
        'minWidth',
        'flex',
        'alignment',
        'paddingStart',
        'paddingEnd',
      ],
      field,
      'Web column key'
    );
    assertKey(column.key, `${field}.key`);
    if (keys.has(column.key)) fail(field, 'column keys must be unique');
    keys.add(column.key);
    assertBoundedStyleNumber(column.width, `${field}.width`, 0, 4096);
    assertBoundedStyleNumber(column.minWidth, `${field}.minWidth`, 0, 4096);
    assertBoundedStyleNumber(column.flex, `${field}.flex`, 0, 16);
    assertBoundedStyleNumber(
      column.paddingStart,
      `${field}.paddingStart`,
      0,
      64
    );
    assertBoundedStyleNumber(column.paddingEnd, `${field}.paddingEnd`, 0, 64);
    if (
      column.alignment !== undefined &&
      !['start', 'center', 'end'].includes(column.alignment)
    )
      fail(`${field}.alignment`, 'must be start, center, or end');
  });
  const text = (value: WebMarketTableText, field: string) => {
    assertUnknownKeys(
      value,
      ['text', 'segments', 'tone', 'style'],
      field,
      'Web text key'
    );
    assertText(value.text, `${field}.text`);
    assertTextTone(value.tone, `${field}.tone`);
    assertTextStyle(value.style, `${field}.style`);
    if (value.style)
      assertUnknownKeys(
        value.style,
        ['fontSize', 'fontWeight', 'lineHeight', 'color'],
        `${field}.style`,
        'Web table text style'
      );
    if ((value.segments?.length ?? 0) > 64)
      fail(`${field}.segments`, 'supports at most 64 segments');
    value.segments?.forEach((segment, index) => {
      assertUnknownKeys(
        segment,
        ['text', 'style'],
        `${field}.segments[${index}]`,
        'Web text segment'
      );
      assertText(segment.text, `${field}.segments[${index}].text`);
      if (segment.style !== undefined && segment.style !== 'subscript')
        fail(field, 'segment style must be subscript');
    });
  };
  const line = (
    parts: readonly WebMarketTableText[] | undefined,
    field: string
  ) => {
    if (!parts) return;
    if (!Array.isArray(parts) || parts.length > 8)
      fail(field, 'supports at most 8 text parts');
    parts.forEach((value, index) => text(value, `${field}[${index}]`));
  };
  table.cells.forEach((cell: WebMarketTableCell, index) => {
    const field = `${path}.cells[${index}]`;
    assertKey(cell.key, `${field}.key`);
    if (cell.key !== table.columns[index].key)
      fail(field, 'cell key/order must match its column');
    switch (cell.kind) {
      case 'favorite':
        assertUnknownKeys(
          cell,
          [
            'key',
            'kind',
            'active',
            'disabled',
            'testID',
            'hoverActionKey',
            'actionKey',
            'accessibilityLabel',
          ],
          field,
          'Web favorite key'
        );
        if (typeof cell.active !== 'boolean')
          fail(field, 'active must be boolean');
        if (cell.disabled !== undefined && typeof cell.disabled !== 'boolean')
          fail(field, 'disabled must be boolean');
        if (cell.hoverActionKey !== undefined)
          assertKey(cell.hoverActionKey, `${field}.hoverActionKey`);
        assertText(cell.testID, `${field}.testID`);
        assertKey(cell.actionKey, `${field}.actionKey`);
        assertText(cell.accessibilityLabel, `${field}.accessibilityLabel`);
        break;
      case 'asset':
        assertUnknownKeys(
          cell,
          [
            'key',
            'kind',
            'leading',
            'title',
            'secondary',
            'hoverSecondary',
            'hoverActionKey',
            'hoverActionLabel',
            'badges',
            'lineGap',
            'secondaryBadges',
            'hoverImages',
          ],
          field,
          'Web asset key'
        );
        assertBoundedStyleNumber(cell.lineGap, `${field}.lineGap`, 0, 8);
        assertMarketBadges(cell.secondaryBadges, `${field}.secondary`);
        cell.secondaryBadges?.forEach((badge, i) => {
          assertUnknownKeys(
            badge,
            ['key', 'text', 'style'],
            `${field}.secondaryBadges[${i}]`,
            'Web secondary badge key'
          );
          if (!badge.text) fail(field, 'secondary badges require text');
        });
        if (
          cell.hoverImages !== undefined &&
          (!Array.isArray(cell.hoverImages) || cell.hoverImages.length > 4)
        )
          fail(field, 'supports at most 4 hover images');
        cell.hoverImages?.forEach((image, i) => {
          if (image !== null) assertImage(image, `${field}.hoverImages[${i}]`);
        });
        assertLeadingVisual(cell.leading, `${field}.leading`);
        text(cell.title, `${field}.title`);
        line(cell.secondary, `${field}.secondary`);
        line(cell.hoverSecondary, `${field}.hoverSecondary`);
        assertText(cell.hoverActionLabel, `${field}.hoverActionLabel`);
        if (cell.hoverActionKey !== undefined)
          assertKey(cell.hoverActionKey, `${field}.hoverActionKey`);
        assertMarketBadges(cell.badges, field);
        cell.badges?.forEach((badge, i) =>
          assertUnknownKeys(
            badge,
            [
              'key',
              'text',
              'iconName',
              'icon',
              'textColor',
              'backgroundColor',
              'actionKey',
              'accessibilityLabel',
              'style',
            ],
            `${field}.badges[${i}]`,
            'Web title badge key'
          )
        );
        break;
      case 'text':
        assertUnknownKeys(
          cell,
          ['key', 'kind', 'primary', 'secondary', 'partGap'],
          field,
          'Web text cell key'
        );
        assertBoundedStyleNumber(cell.partGap, `${field}.partGap`, 0, 8);
        if (!Array.isArray(cell.primary) || !cell.primary.length)
          fail(field, 'requires primary text');
        line(cell.primary, `${field}.primary`);
        line(cell.secondary, `${field}.secondary`);
        break;
      case 'sparkline':
        assertUnknownKeys(
          cell,
          [
            'key',
            'kind',
            'points',
            'width',
            'height',
            'strokeColor',
            'fillColor',
          ],
          field,
          'Web sparkline key'
        );
        if (
          !Array.isArray(cell.points) ||
          cell.points.length > 256 ||
          cell.points.some((value: number) => !Number.isFinite(value))
        )
          fail(field, 'supports at most 256 finite samples');
        if (typeof cell.width !== 'number' || typeof cell.height !== 'number')
          fail(field, 'sparkline dimensions are required');
        assertBoundedStyleNumber(cell.width, `${field}.width`, 1, 512);
        assertBoundedStyleNumber(cell.height, `${field}.height`, 1, 512);
        for (const color of [cell.strokeColor, cell.fillColor])
          if (
            typeof color !== 'string' ||
            !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(color)
          )
            fail(field, 'colors must be #RRGGBB or #RRGGBBAA');
        break;
      default:
        fail(field, 'unsupported Web table cell kind');
    }
  });
}

function assertMarketRow(row: MarketRow, path: string): void {
  assertWebMarketTable(row.webTable, `${path}.webTable`);
  if (!['token', 'stock', 'perp'].includes(row.variant)) {
    fail(`${path}.variant`, 'must be token, stock, or perp');
  }
  assertText(row.title, `${path}.title`);
  assertText(row.subtitle, `${path}.subtitle`);
  // OneKey patch: validate the independently laid out Market name.
  if (row.subtitlePrefix) {
    assertText(row.subtitlePrefix.text, `${path}.subtitlePrefix.text`);
    assertText(
      row.subtitlePrefix.hoverActionKey,
      `${path}.subtitlePrefix.hoverActionKey`
    );
    assertBoundedStyleNumber(
      row.subtitlePrefix.gap,
      `${path}.subtitlePrefix.gap`,
      0,
      64
    );
    assertBoundedStyleNumber(
      row.subtitlePrefix.maxWidth,
      `${path}.subtitlePrefix.maxWidth`,
      1,
      320
    );
    assertTextStyle(row.subtitlePrefix.style, `${path}.subtitlePrefix.style`);
  }
  assertText(row.price, `${path}.price`);
  assertText(row.change.text, `${path}.change.text`);
  assertTrailingAccessories(
    row.leadingAction ? [row.leadingAction] : undefined,
    `${path}.leadingAction`
  );
  if (row.leadingAction) {
    assertText(row.leadingAction.name, `${path}.leadingAction.name`);
    if (row.leadingAction.actionKey !== undefined) {
      assertKey(row.leadingAction.actionKey, `${path}.leadingAction.actionKey`);
    }
  }
  assertLeadingVisual(row.leading, `${path}.leading`);
  const assertSegments = (
    segments: MarketRow['priceSegments'],
    segmentPath: string
  ) => {
    segments?.forEach((segment, index) => {
      assertText(segment.text, `${segmentPath}[${index}].text`);
      if (segment.style !== undefined && segment.style !== 'subscript') {
        fail(
          `${segmentPath}[${index}].style`,
          'must be subscript when provided'
        );
      }
    });
  };
  assertSegments(row.subtitleSegments, `${path}.subtitleSegments`);
  assertSegments(row.priceSegments, `${path}.priceSegments`);
  assertSegments(row.change.textSegments, `${path}.change.textSegments`);
  if (!['positive', 'negative', 'neutral'].includes(row.change.tone)) {
    fail(`${path}.change.tone`, 'must be positive, negative, or neutral');
  }
  assertMarketBadges(row.badges, path);
  for (const [key, value] of [
    ['pressActionKey', row.pressActionKey],
    ['pressInActionKey', row.pressInActionKey],
    ['longPressActionKey', row.longPressActionKey],
    ['diagnostics.imageBindActionKey', row.diagnostics?.imageBindActionKey],
  ] as const) {
    if (value !== undefined) assertKey(value, `${path}.${key}`);
  }
  assertMarketStyle(row.style, `${path}.style`);
}

function assertSectionHeaderVariant(
  variant: string | undefined,
  path: string
): void {
  if (
    variant !== undefined &&
    !['summary', 'gallery', 'history'].includes(variant)
  ) {
    fail(path, 'must be summary, gallery, or history when provided');
  }
}

function assertNonNegativeNumber(
  value: number | undefined,
  path: string
): void {
  if (value !== undefined && value < 0) {
    fail(path, 'must be non-negative');
  }
}

function assertVisualShape(shape: string | undefined, path: string): void {
  if (shape !== undefined && !['circle', 'rounded', 'square'].includes(shape)) {
    fail(path, 'must be circle, rounded, or square');
  }
}

function assertTrailingAccessories(
  accessories: IdentityRow['trailing'] | undefined,
  path: string
): void {
  accessories?.forEach((accessory, index) => {
    const accessoryPath = `${path}[${index}]`;
    if (accessory.kind === 'button') {
      assertUnknownKeys(
        accessory as Record<string, unknown>,
        [
          'kind',
          'label',
          'actionKey',
          'disabled',
          'testID',
          'accessibilityLabel',
          'textColor',
          'backgroundColor',
          'pressedBackgroundColor',
          'hoverBackgroundColor',
          'focusRingColor',
        ],
        accessoryPath,
        'button key'
      );
      if (typeof accessory.label !== 'string' || !accessory.label.trim())
        fail(`${accessoryPath}.label`, 'must be non-blank');
      assertKey(accessory.actionKey, `${accessoryPath}.actionKey`);
      if (
        accessory.disabled !== undefined &&
        typeof accessory.disabled !== 'boolean'
      )
        fail(`${accessoryPath}.disabled`, 'must be boolean');
      for (const field of [
        'textColor',
        'backgroundColor',
        'pressedBackgroundColor',
        'hoverBackgroundColor',
        'focusRingColor',
      ] as const) {
        const value = accessory[field];
        if (
          value !== undefined &&
          (typeof value !== 'string' ||
            !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(value))
        )
          fail(`${accessoryPath}.${field}`, 'must be #RRGGBB or #RRGGBBAA');
      }
    }
    if (
      accessory.kind === 'icon' &&
      accessory.buttonStyle !== undefined &&
      accessory.buttonStyle !== 'tertiary'
    )
      fail(`${accessoryPath}.buttonStyle`, 'must be tertiary');
    if (
      accessory.kind === 'progress' &&
      (accessory.value < 0 || accessory.value > 1)
    ) {
      fail(`${accessoryPath}.value`, 'must be within 0...1');
    }
    if (accessory.kind === 'valuePair') {
      if (
        accessory.primaryFontWeight !== undefined &&
        accessory.primaryFontWeight !== 'medium'
      )
        fail(
          `${accessoryPath}.primaryFontWeight`,
          'must be medium when provided'
        );
      assertTextHelp(accessory.primaryHelp, `${accessoryPath}.primaryHelp`);
      assertTextTone(accessory.primaryTone, `${accessoryPath}.primaryTone`);
      assertTextTone(accessory.secondaryTone, `${accessoryPath}.secondaryTone`);
      for (const field of [
        'primaryTextSegments',
        'secondaryTextSegments',
      ] as const) {
        const segments = accessory[field];
        if (segments === undefined) continue;
        const segmentPath = `${accessoryPath}.${field}`;
        if (!Array.isArray(segments) || segments.length > 64)
          fail(segmentPath, 'must be an array of at most 64 segments');
        let textLength = 0;
        segments.forEach((segment, segmentIndex) => {
          const path = `${segmentPath}[${segmentIndex}]`;
          const entry = protocolObject(segment, path);
          assertUnknownKeys(entry, ['text', 'style'], path, 'segment key');
          if (
            typeof entry.text !== 'string' ||
            entry.text.length > MAX_TEXT_LENGTH
          )
            fail(
              `${path}.text`,
              `must be a string no longer than ${MAX_TEXT_LENGTH} characters`
            );
          if (entry.style !== undefined && entry.style !== 'subscript')
            fail(`${path}.style`, 'must be subscript when provided');
          textLength += entry.text.length;
        });
        if (textLength > MAX_TEXT_LENGTH)
          fail(
            segmentPath,
            `total text must be at most ${MAX_TEXT_LENGTH} characters`
          );
      }
    }
  });
}

function assertTextHelp(help: RowTextHelp | undefined, path: string): void {
  if (help === undefined) return;
  const object = protocolObject(help, path);
  assertUnknownKeys(
    object,
    ['actionKey', 'presentation', 'underline'],
    path,
    'help key'
  );
  assertKey(help.actionKey, `${path}.actionKey`);
  if (!['tooltip', 'popover'].includes(help.presentation))
    fail(`${path}.presentation`, 'must be tooltip or popover');
  if (help.underline !== undefined && help.underline !== 'dashed')
    fail(`${path}.underline`, 'must be dashed');
}

function assertButtonGroup(
  row: Pick<ButtonGroupRow, 'buttons'>,
  path: string
): void {
  if (!Array.isArray(row.buttons) || row.buttons.length === 0)
    fail(`${path}.buttons`, 'must contain at least one button');
  const keys = new Set<string>();
  row.buttons.forEach((button, index) => {
    const itemPath = `${path}.buttons[${index}]`;
    const object = protocolObject(button, itemPath);
    assertUnknownKeys(
      object,
      [
        'key',
        'label',
        'actionKey',
        'disabled',
        'loading',
        'testID',
        'accessibilityLabel',
      ],
      itemPath,
      'button key'
    );
    assertKey(button.key, `${itemPath}.key`);
    if (keys.has(button.key)) fail(`${itemPath}.key`, 'duplicate button key');
    keys.add(button.key);
    if (
      typeof button.label !== 'string' ||
      !button.label.trim() ||
      button.label.length > MAX_TEXT_LENGTH
    )
      fail(`${itemPath}.label`, 'must be non-blank bounded text');
    assertKey(button.actionKey, `${itemPath}.actionKey`);
    for (const field of ['disabled', 'loading'] as const) {
      if (button[field] !== undefined && typeof button[field] !== 'boolean')
        fail(`${itemPath}.${field}`, 'must be boolean');
    }
    for (const field of ['testID', 'accessibilityLabel'] as const) {
      if (button[field] !== undefined && typeof button[field] !== 'string')
        fail(`${itemPath}.${field}`, 'must be a string');
    }
  });
}

function assertIdentityExtras(
  row: Pick<
    IdentityRow,
    'titleIcon' | 'secondaryTitleIcon' | 'titleImage' | 'trailing' | 'style'
  >,
  path: string
): void {
  for (const field of ['titleIcon', 'secondaryTitleIcon'] as const) {
    const titleIcon = row[field];
    if (titleIcon === undefined) continue;
    const iconPath = `${path}.${field}`;
    const icon = protocolObject(titleIcon, iconPath);
    assertUnknownKeys(
      icon,
      ['name', 'tintColor', 'accessibilityLabel', 'hoverActionKey'],
      iconPath,
      'title icon key'
    );
    if (typeof titleIcon.name !== 'string' || !titleIcon.name.trim())
      fail(`${iconPath}.name`, 'must be non-blank');
    if (
      titleIcon.tintColor !== undefined &&
      (typeof titleIcon.tintColor !== 'string' ||
        !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(titleIcon.tintColor))
    )
      fail(`${iconPath}.tintColor`, 'must be #RRGGBB or #RRGGBBAA');
    if (
      titleIcon.accessibilityLabel !== undefined &&
      typeof titleIcon.accessibilityLabel !== 'string'
    )
      fail(`${iconPath}.accessibilityLabel`, 'must be a string');
    if (titleIcon.hoverActionKey !== undefined)
      assertKey(titleIcon.hoverActionKey, `${iconPath}.hoverActionKey`);
  }
  if (row.titleImage !== undefined) {
    const imagePath = `${path}.titleImage`;
    const descriptor = protocolObject(row.titleImage, imagePath);
    assertUnknownKeys(
      descriptor,
      ['image', 'accessibilityLabel', 'hoverActionKey'],
      imagePath,
      'title image key'
    );
    protocolObject(row.titleImage.image, `${imagePath}.image`);
    if (typeof row.titleImage.image.uri !== 'string')
      fail(`${imagePath}.image.uri`, 'must be a string');
    assertImage(row.titleImage.image, `${imagePath}.image`);
    const { width, height } = row.titleImage.image;
    if (width !== height || width < 1 || width > 40)
      fail(`${imagePath}.image`, 'must be square within 1...40');
    if (
      row.titleImage.accessibilityLabel !== undefined &&
      typeof row.titleImage.accessibilityLabel !== 'string'
    )
      fail(`${imagePath}.accessibilityLabel`, 'must be a string');
    if (row.titleImage.hoverActionKey !== undefined)
      assertKey(row.titleImage.hoverActionKey, `${imagePath}.hoverActionKey`);
  }
  const ratio = row.style?.leadingContentMaxWidthRatio;
  if (
    ratio !== undefined &&
    (typeof ratio !== 'number' ||
      !Number.isFinite(ratio) ||
      ratio <= 0 ||
      ratio > 1)
  )
    fail(`${path}.style.leadingContentMaxWidthRatio`, 'must be within (0, 1]');
  assertTrailingAccessories(row.trailing, `${path}.trailing`);
}

function assertImage(image: ImageSource | undefined, path: string): void {
  if (!image) return;
  if (!isAllowedImageUri(image.uri))
    fail(`${path}.uri`, 'unsupported image URI');
  if (!image.uri.trim()) fail(`${path}.uri`, 'must not be empty');
  if (image.fallbackUris !== undefined) {
    if (!Array.isArray(image.fallbackUris))
      fail(`${path}.fallbackUris`, 'must be an array');
    image.fallbackUris.forEach((uri, index) => {
      if (typeof uri !== 'string')
        fail(`${path}.fallbackUris[${index}]`, 'must be a string');
      assertText(uri, `${path}.fallbackUris[${index}]`);
    });
  }
  if (
    !Number.isFinite(image.width) ||
    !Number.isFinite(image.height) ||
    image.width <= 0 ||
    image.height <= 0 ||
    image.width > MAX_IMAGE_EDGE ||
    image.height > MAX_IMAGE_EDGE
  ) {
    fail(path, `width and height must be within 1...${MAX_IMAGE_EDGE}`);
  }
  if (
    image.overscan !== undefined &&
    (image.overscan < 1 || image.overscan > 4)
  ) {
    fail(`${path}.overscan`, 'must be within 1...4');
  }
  if (image.headers) {
    const entries = Object.entries(image.headers);
    if (entries.length > MAX_IMAGE_HEADERS) {
      fail(`${path}.headers`, `supports at most ${MAX_IMAGE_HEADERS} headers`);
    }
    entries.forEach(([key, value]) => {
      if (
        !key.trim() ||
        key.length > MAX_HEADER_LENGTH ||
        typeof value !== 'string' ||
        value.length > MAX_HEADER_LENGTH
      ) {
        fail(
          `${path}.headers`,
          `keys and values must be within 1...${MAX_HEADER_LENGTH} characters`
        );
      }
    });
  }
}

function assertLeadingVisual(
  visual: LeadingVisual | undefined,
  path: string
): void {
  if (!visual) return;
  if (visual.kind === 'stackedImages') {
    if (visual.images.length < 2 || visual.images.length > 3) {
      fail(`${path}.images`, 'must contain 2 or 3 images');
    }
    visual.images.forEach((image, index) =>
      assertImage(image, `${path}.images[${index}]`)
    );
  } else if (visual.kind === 'image') {
    assertImage(visual.image, `${path}.image`);
    assertVisualShape(visual.shape, `${path}.shape`);
  } else if (visual.kind !== 'icon') {
    assertImage(visual.image, `${path}.image`);
    assertVisualShape(visual.shape, `${path}.shape`);
    assertText(visual.cornerIcon?.name, `${path}.cornerIcon.name`);
    // OneKey patch: validate optional selector overlays at the JSON boundary.
    assertText(visual.fallbackIcon?.name, `${path}.fallbackIcon.name`);
    if (
      visual.fallbackTextColor !== undefined &&
      (typeof visual.fallbackTextColor !== 'string' ||
        !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(visual.fallbackTextColor))
    )
      fail(`${path}.fallbackTextColor`, 'must be a hex color');
    if (
      visual.fallbackIcon?.size !== undefined &&
      (!Number.isFinite(visual.fallbackIcon.size) ||
        visual.fallbackIcon.size <= 0 ||
        visual.fallbackIcon.size > 40)
    )
      fail(`${path}.fallbackIcon.size`, 'must be within (0, 40]');
    if (
      visual.fallbackIcon?.backgroundColor !== undefined &&
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(visual.fallbackIcon.backgroundColor)
    )
      fail(`${path}.fallbackIcon.backgroundColor`, 'must be a hex color');
    if ((visual.overlays?.length ?? 0) > 2)
      fail(`${path}.overlays`, 'supports at most two overlays');
    visual.overlays?.forEach((overlay, index) => {
      if (!['topLeft', 'bottomRight'].includes(overlay.position))
        fail(
          `${path}.overlays[${index}].position`,
          'must be topLeft or bottomRight'
        );
      if (
        overlay.size !== undefined &&
        (overlay.size <= 0 || overlay.size > 40)
      )
        fail(`${path}.overlays[${index}].size`, 'must be within 1...40');
      if (
        overlay.padding !== undefined &&
        (!Number.isFinite(overlay.padding) ||
          overlay.padding < 0 ||
          overlay.padding * 2 >= (overlay.size ?? 20))
      )
        fail(
          `${path}.overlays[${index}].padding`,
          'must fit inside the overlay'
        );
      if (
        overlay.offset !== undefined &&
        (!Number.isFinite(overlay.offset) ||
          overlay.offset < 0 ||
          overlay.offset > 20)
      )
        fail(`${path}.overlays[${index}].offset`, 'must be within 0...20');
      for (const field of ['width', 'height'] as const) {
        const value = overlay[field];
        if (
          value !== undefined &&
          (!Number.isFinite(value) || value <= 0 || value > 40)
        )
          fail(`${path}.overlays[${index}].${field}`, 'must be within 1...40');
      }
      for (const field of ['offsetX', 'offsetY'] as const) {
        const value = overlay[field];
        if (
          value !== undefined &&
          (!Number.isFinite(value) || value < 0 || value > 20)
        )
          fail(`${path}.overlays[${index}].${field}`, 'must be within 0...20');
      }
      if (
        overlay.padding !== undefined &&
        overlay.padding * 2 >=
          Math.min(
            overlay.width ?? overlay.size ?? 20,
            overlay.height ?? overlay.size ?? 20
          )
      )
        fail(
          `${path}.overlays[${index}].padding`,
          'must fit inside the overlay'
        );
      assertImage(overlay.image, `${path}.overlays[${index}].image`);
      if (overlay.fallbackIcon !== undefined && !overlay.image)
        fail(`${path}.overlays[${index}].fallbackIcon`, 'requires image');
      if (
        overlay.fallbackIcon !== undefined &&
        (typeof overlay.fallbackIcon.name !== 'string' ||
          !overlay.fallbackIcon.name.trim())
      )
        fail(
          `${path}.overlays[${index}].fallbackIcon.name`,
          'must be non-blank'
        );
      if (
        overlay.fallbackIcon?.tintColor !== undefined &&
        !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(overlay.fallbackIcon.tintColor)
      )
        fail(
          `${path}.overlays[${index}].fallbackIcon.tintColor`,
          'must be a hex color'
        );
      assertText(overlay.text, `${path}.overlays[${index}].text`);
      if (
        overlay.fontWeight !== undefined &&
        !['regular', 'medium', 'semibold', 'bold'].includes(overlay.fontWeight)
      )
        fail(
          `${path}.overlays[${index}].fontWeight`,
          'must be regular, medium, semibold, or bold'
        );
    });
    if (visual.kind === 'token') {
      assertImage(visual.networkImage, `${path}.networkImage`);
    }
  }
}

function assertVisual(row: RowModel, path: string): void {
  const visuals =
    row.type === 'identity'
      ? [row.leading]
      : row.type === 'rail'
      ? [row.visual]
      : row.type === 'activity'
      ? [row.leading, row.secondaryLeading]
      : row.type === 'message'
      ? [row.leading]
      : row.type === 'dataRow'
      ? [row.leading]
      : row.type === 'market'
      ? [row.leading]
      : row.type === 'metricCard'
      ? [row.visual]
      : [];

  visuals.forEach((visual, index) => {
    assertLeadingVisual(visual, `${path}.visual[${index}]`);
  });
}

function assertActivity(row: Partial<ActivityRow>, path: string): void {
  if (row.hideLeading !== undefined && typeof row.hideLeading !== 'boolean')
    fail(`${path}.hideLeading`, 'must be boolean');
  assertText(row.title, `${path}.title`);
  assertText(row.description, `${path}.description`);
  if (
    row.presentation !== undefined &&
    !['stacked', 'table'].includes(row.presentation)
  )
    fail(`${path}.presentation`, 'must be stacked or table');
  if (row.descriptionActionKey !== undefined)
    assertKey(row.descriptionActionKey, `${path}.descriptionActionKey`);
  if ((row.amounts?.length ?? 0) > 32)
    fail(`${path}.amounts`, 'supports at most 32 amount lines');
  const amountKeys = new Set<string>();
  row.amounts?.forEach((amount, index) => {
    const amountPath = `${path}.amounts[${index}]`;
    assertKey(amount.key, `${amountPath}.key`);
    if (amountKeys.has(amount.key)) fail(amountPath, 'duplicate amount key');
    amountKeys.add(amount.key);
    assertText(amount.text, `${amountPath}.text`);
    assertLeadingVisual(amount.leading, `${amountPath}.leading`);
    assertText(amount.secondaryText, `${amountPath}.secondaryText`);
    if (
      amount.tone !== undefined &&
      !['primary', 'secondary', 'positive', 'negative'].includes(amount.tone)
    )
      fail(`${amountPath}.tone`, 'invalid text tone');
    amount.textSegments?.forEach((segment, segmentIndex) => {
      assertText(
        segment.text,
        `${amountPath}.textSegments[${segmentIndex}].text`
      );
      if (segment.style !== undefined && segment.style !== 'subscript')
        fail(amountPath, 'invalid text segment style');
    });
  });
  const assertActivitySegments = (
    segments: readonly { text: string; style?: string }[] | undefined,
    segmentPath: string
  ) =>
    segments?.forEach((segment, index) => {
      assertText(segment.text, `${segmentPath}[${index}].text`);
      if (segment.style !== undefined && segment.style !== 'subscript')
        fail(segmentPath, 'invalid text segment style');
    });
  row.amounts?.forEach((amount, index) =>
    assertActivitySegments(
      amount.secondaryTextSegments,
      `${path}.amounts[${index}].secondaryTextSegments`
    )
  );
  assertActivitySegments(
    row.fee?.primaryTextSegments,
    `${path}.fee.primaryTextSegments`
  );
  assertActivitySegments(
    row.fee?.secondaryTextSegments,
    `${path}.fee.secondaryTextSegments`
  );
  assertText(row.fee?.label, `${path}.fee.label`);
  assertText(row.fee?.primary, `${path}.fee.primary`);
  assertText(row.fee?.secondary, `${path}.fee.secondary`);
  if ((row.badges?.length ?? 0) > 4)
    fail(`${path}.badges`, 'supports at most 4 activity badges');
  const badgeKeys = new Set<string>();
  row.badges?.forEach((badge, index) => {
    assertKey(badge.key, `${path}.badges[${index}].key`);
    if (badgeKeys.has(badge.key)) fail(`${path}.badges`, 'duplicate badge key');
    badgeKeys.add(badge.key);
    assertText(badge.text, `${path}.badges[${index}].text`);
    if (
      badge.tone !== undefined &&
      !['neutral', 'info', 'success', 'warning', 'danger'].includes(badge.tone)
    )
      fail(`${path}.badges`, 'invalid badge tone');
  });
  if (row.fee?.hidden !== undefined && typeof row.fee.hidden !== 'boolean')
    fail(`${path}.fee.hidden`, 'must be boolean');

  if ((row.footerActions?.length ?? 0) > MAX_FOOTER_ACTIONS) {
    fail(
      `${path}.footerActions`,
      `supports at most ${MAX_FOOTER_ACTIONS} actions`
    );
  }
  const keys = new Set<string>();
  row.footerActions?.forEach((action, index) => {
    assertKey(action.key, `${path}.footerActions[${index}].key`);
    if (keys.has(action.key))
      fail(`${path}.footerActions`, `duplicate action key "${action.key}"`);
    keys.add(action.key);
  });
}

function assertWalletGroup(row: WalletGroupRow, path: string): void {
  if (row.parent.key !== row.key) {
    fail(`${path}.parent.key`, 'must match the wallet group key');
  }
  if (row.children.length === 0) {
    fail(`${path}.children`, 'must contain at least one child wallet');
  }
  const members = [row.parent, ...row.children];
  const keys = new Set<string>();
  members.forEach((member, memberIndex) => {
    const memberPath =
      memberIndex === 0
        ? `${path}.parent`
        : `${path}.children[${memberIndex - 1}]`;
    if (member.type !== 'identity' || member.presentation !== 'walletSidebar') {
      fail(memberPath, 'must be a walletSidebar identity row');
    }
    assertRow(member, memberIndex, memberPath);
    if (keys.has(member.key)) {
      fail(memberPath, `duplicate wallet member key "${member.key}"`);
    }
    keys.add(member.key);
  });
}

function assertRowType(
  type: unknown,
  path: string
): asserts type is RowModel['type'] {
  if (
    typeof type !== 'string' ||
    !Object.prototype.hasOwnProperty.call(ROW_BOX_STYLE_KEYS_BY_TYPE, type)
  ) {
    fail(`${path}.type`, 'is not supported');
  }
}

function assertRow(
  row: RowModel,
  index: number,
  path = `rows[${index}]`
): void {
  assertRowType(row.type, path);
  assertUnknownKeys(row, ROW_FIELDS[row.type], path, 'row key');
  assertKey(row.key, `${path}.key`);
  // OneKey patch: explicit dimensions and opacity cannot corrupt list layout.
  if (row.height !== undefined && (row.height < 0 || row.height > 4096))
    fail(`${path}.height`, 'must be within 0...4096');
  if (
    row.heightRounding !== undefined &&
    ((row.height === undefined && row.style?.container?.height === undefined) ||
      !['floor', 'nearest'].includes(row.heightRounding))
  )
    fail(
      `${path}.heightRounding`,
      'requires an explicit height and must be floor or nearest'
    );
  if (row.opacity !== undefined && (row.opacity < 0 || row.opacity > 1))
    fail(`${path}.opacity`, 'must be within 0...1');
  if (row.groupId && !row.groupPosition) {
    fail(`${path}.groupPosition`, 'is required when groupId is present');
  }
  if (!row.groupId && row.groupPosition) {
    fail(`${path}.groupId`, 'is required when groupPosition is present');
  }
  assertRowStyle(row, path);
  assertVisual(row, path);

  switch (row.type) {
    case 'walletGroup':
      assertWalletGroup(row, path);
      break;
    case 'identity':
      if (row.contentHeight !== undefined && row.contentHeight !== 'auto') {
        fail(`${path}.contentHeight`, 'must be auto');
      }
      if (
        row.subtitleLines !== undefined &&
        ![0, 1, 2].includes(row.subtitleLines)
      ) {
        fail(`${path}.subtitleLines`, 'must be 0, 1, or 2');
      }
      if (row.longPressActionKey !== undefined) {
        assertKey(row.longPressActionKey, `${path}.longPressActionKey`);
      }
      if (
        row.presentation !== undefined &&
        row.presentation !== 'walletSidebar' &&
        row.presentation !== 'accountSelector' &&
        row.presentation !== 'networkSelector'
      ) {
        fail(
          `${path}.presentation`,
          'must be walletSidebar, accountSelector, or networkSelector when provided'
        );
      }
      assertText(row.title, `${path}.title`);
      if ((row.titleSuffixes?.length ?? 0) > 3)
        fail(`${path}.titleSuffixes`, 'supports at most 3 suffixes');
      row.titleSuffixes?.forEach((suffix, index) => {
        assertText(suffix.text, `${path}.titleSuffixes[${index}].text`);
        if (!['secondary', 'disabled'].includes(suffix.tone))
          fail(
            `${path}.titleSuffixes[${index}].tone`,
            'must be secondary or disabled'
          );
        if (
          suffix.maxWidth !== undefined &&
          (!Number.isFinite(suffix.maxWidth) ||
            suffix.maxWidth < 0 ||
            suffix.maxWidth > 4096)
        )
          fail(
            `${path}.titleSuffixes[${index}].maxWidth`,
            'must be finite within 0...4096'
          );
      });
      assertIdentityExtras(row, path);
      assertText(row.subtitle, `${path}.subtitle`);
      // OneKey patch: selector text segments truncate independently.
      row.subtitleSegments?.forEach((segment, segmentIndex) => {
        assertText(
          segment.text,
          `${path}.subtitleSegments[${segmentIndex}].text`
        );
        if (
          segment.tone !== undefined &&
          ![
            'primary',
            'secondary',
            'disabled',
            'caution',
            'positive',
            'negative',
          ].includes(segment.tone)
        )
          fail(
            `${path}.subtitleSegments[${segmentIndex}].tone`,
            'invalid selector text tone'
          );
      });
      let previousMatchEnd = 0;
      row.titleMatch?.forEach((match) => {
        if (
          !Number.isInteger(match.start) ||
          !Number.isInteger(match.end) ||
          match.start < previousMatchEnd ||
          match.end <= match.start ||
          match.end > row.title.length
        )
          fail(
            `${path}.titleMatch`,
            'must contain ordered, non-overlapping UTF-16 ranges inside title'
          );
        previousMatchEnd = match.end;
      });
      assertText(row.tertiary, `${path}.tertiary`);
      if (
        row.tertiaryTone !== undefined &&
        !['secondary', 'info'].includes(row.tertiaryTone)
      ) {
        fail(`${path}.tertiaryTone`, 'must be secondary or info');
      }
      if ((row.badges?.length ?? 0) > MAX_BADGES) {
        fail(`${path}.badges`, `supports at most ${MAX_BADGES} badges`);
      }
      if ((row.trailing?.length ?? 0) > MAX_TRAILING_ACCESSORIES) {
        fail(
          `${path}.trailing`,
          `supports at most ${MAX_TRAILING_ACCESSORIES} accessories`
        );
      }
      assertTrailingAccessories(row.trailing, `${path}.trailing`);
      assertTrailingAccessories(
        row.leadingAction ? [row.leadingAction] : undefined,
        `${path}.leadingAction`
      );
      break;
    case 'rail':
      assertText(row.title, `${path}.title`);
      break;
    case 'activity':
      assertActivity(row, path);
      break;
    case 'message':
      if (
        row.presentation !== undefined &&
        row.presentation !== 'inlineWarning'
      ) {
        fail(`${path}.presentation`, 'must be inlineWarning');
      }
      assertText(row.title, `${path}.title`);
      assertText(row.body, `${path}.body`);
      if (![0, 1, 2, 3].includes(row.bodyLines ?? 3)) {
        fail(`${path}.bodyLines`, 'must be 0, 1, 2, or 3');
      }
      assertImage(row.thumbnail, `${path}.thumbnail`);
      break;
    case 'dataRow':
      if (row.columns.length < 2 || row.columns.length > 4) {
        fail(`${path}.columns`, 'must contain 2...4 columns');
      }
      row.columns.forEach((column, columnIndex) => {
        assertKey(column.key, `${path}.columns[${columnIndex}].key`);
        assertText(column.text, `${path}.columns[${columnIndex}].text`);
        assertIdentityExtras(column, `${path}.columns[${columnIndex}]`);
        for (const field of [
          'textSegments',
          'secondaryTextSegments',
        ] as const) {
          column[field]?.forEach((segment, segmentIndex) => {
            assertText(
              segment.text,
              `${path}.columns[${columnIndex}].${field}[${segmentIndex}].text`
            );
            if (segment.style !== undefined && segment.style !== 'subscript')
              fail(
                `${path}.columns[${columnIndex}].${field}[${segmentIndex}].style`,
                'must be subscript'
              );
          });
        }
        assertText(
          column.secondaryLeadingText,
          `${path}.columns[${columnIndex}].secondaryLeadingText`
        );
        assertText(
          column.secondaryText,
          `${path}.columns[${columnIndex}].secondaryText`
        );
        assertTextTone(
          column.secondaryTone,
          `${path}.columns[${columnIndex}].secondaryTone`
        );
      });
      if ((row.badges?.length ?? 0) > MAX_BADGES) {
        fail(`${path}.badges`, `supports at most ${MAX_BADGES} badges`);
      }
      if ((row.trailing?.length ?? 0) > MAX_TRAILING_ACCESSORIES) {
        fail(
          `${path}.trailing`,
          `supports at most ${MAX_TRAILING_ACCESSORIES} accessories`
        );
      }
      assertTrailingAccessories(row.trailing, `${path}.trailing`);
      break;
    case 'market':
      assertMarketRow(row, path);
      break;
    case 'mediaTile':
      assertMediaPreview(row.media, `${path}.media`);
      assertImage(row.image, `${path}.image`);
      if (
        row.imageState !== undefined &&
        !['empty', 'error'].includes(row.imageState)
      ) {
        fail(`${path}.imageState`, 'must be empty or error when provided');
      }
      if (
        row.imageState === undefined &&
        row.image === undefined &&
        row.media === undefined
      ) {
        fail(
          `${path}.image`,
          'is required unless media is provided or imageState is empty or error'
        );
      }
      assertImage(row.networkImage, `${path}.networkImage`);
      assertText(row.title, `${path}.title`);
      assertText(row.subtitle, `${path}.subtitle`);
      break;
    case 'metricCard':
      assertText(row.title, `${path}.title`);
      assertText(row.value, `${path}.value`);
      assertText(row.subtitle, `${path}.subtitle`);
      assertText(row.trend, `${path}.trend`);
      if (
        row.variant !== undefined &&
        !['standard', 'activity', 'performance'].includes(row.variant)
      ) {
        fail(`${path}.variant`, 'must be standard, activity, or performance');
      }
      if (row.metrics && (row.metrics.length < 2 || row.metrics.length > 5)) {
        fail(`${path}.metrics`, 'must contain 2...5 metrics');
      }
      row.metrics?.forEach((metric, metricIndex) => {
        assertKey(metric.key, `${path}.metrics[${metricIndex}].key`);
        assertText(metric.label, `${path}.metrics[${metricIndex}].label`);
        assertText(metric.value, `${path}.metrics[${metricIndex}].value`);
        assertTextTone(metric.tone, `${path}.metrics[${metricIndex}].tone`);
        assertLeadingVisual(
          metric.visual,
          `${path}.metrics[${metricIndex}].visual`
        );
      });
      if (
        row.progress !== undefined &&
        (row.progress < 0 || row.progress > 1)
      ) {
        fail(`${path}.progress`, 'must be within 0...1');
      }
      break;
    case 'sectionHeader':
      if (
        row.titleLoading !== undefined &&
        typeof row.titleLoading !== 'boolean'
      )
        fail(`${path}.titleLoading`, 'must be boolean');
      assertKey(row.sectionKey, `${path}.sectionKey`);
      if (
        row.presentation !== undefined &&
        row.presentation !== 'networkSelector'
      ) {
        fail(`${path}.presentation`, 'must be networkSelector when provided');
      }
      assertSectionIndexTitle(row.indexTitle, `${path}.indexTitle`);
      assertSectionHeaderVariant(row.variant, `${path}.variant`);
      assertText(row.title, `${path}.title`);
      assertText(row.subtitle, `${path}.subtitle`);
      assertText(row.value, `${path}.value`);
      if (row.leadingBadge) {
        assertKey(row.leadingBadge.key, `${path}.leadingBadge.key`);
        assertText(row.leadingBadge.text, `${path}.leadingBadge.text`);
      }
      assertTextHelp(row.valueHelp, `${path}.valueHelp`);
      if (row.valueActionKey !== undefined) {
        assertKey(row.valueActionKey, `${path}.valueActionKey`);
      }
      assertTrailingAccessories(
        row.titleIcon ? [row.titleIcon] : undefined,
        `${path}.titleIcon`
      );
      assertTrailingAccessories(
        row.valueIcon ? [row.valueIcon] : undefined,
        `${path}.valueIcon`
      );
      if (row.variant === 'summary' && row.checkbox !== undefined) {
        fail(`${path}.checkbox`, 'is not supported by summary headers');
      }
      break;
    case 'action':
      if (
        row.presentation !== undefined &&
        row.presentation !== 'accountSelector'
      ) {
        fail(`${path}.presentation`, 'must be accountSelector when provided');
      }
      assertKey(row.actionKey, `${path}.actionKey`);
      assertText(row.title, `${path}.title`);
      if ((row.trailing?.length ?? 0) > MAX_TRAILING_ACCESSORIES) {
        fail(
          `${path}.trailing`,
          `supports at most ${MAX_TRAILING_ACCESSORIES} accessories`
        );
      }
      assertTrailingAccessories(row.trailing, `${path}.trailing`);
      break;
    case 'buttonGroup':
      assertButtonGroup(row, path);
      break;
    case 'system':
      const presentation = 'presentation' in row ? row.presentation : undefined;
      if (presentation !== undefined && presentation !== 'market') {
        fail(`${path}.presentation`, 'must be market when provided');
      }
      if (
        // OneKey patch: deprecated-wallet warnings retain the original scrolling semantics.
        !['loading', 'retry', 'noMatch', 'end', 'spacer', 'warning'].includes(
          row.variant
        )
      ) {
        fail(
          `${path}.variant`,
          'must be loading, retry, noMatch, end, spacer, or warning'
        );
      }
      if (row.variant === 'warning') assertText(row.title, `${path}.title`);
      if (
        row.variant === 'loading' &&
        row.loadingStyle !== undefined &&
        row.loadingStyle !== 'skeleton' &&
        row.loadingStyle !== 'spinner'
      ) {
        fail(
          `${path}.loadingStyle`,
          'must be skeleton or spinner when provided'
        );
      }
      if (row.variant !== 'spacer') {
        assertText(row.message, `${path}.message`);
      }
      if (
        row.variant === 'spacer' &&
        (row.height < 0 || row.height > MAX_SPACER_HEIGHT)
      ) {
        fail(`${path}.height`, `must be within 0...${MAX_SPACER_HEIGHT}`);
      }
      if (row.variant === 'retry') {
        assertKey(row.actionKey, `${path}.actionKey`);
        assertText(row.actionText, `${path}.actionText`);
      }
      break;
  }
}

function assertGroups(rows: readonly RowModel[]): void {
  let index = 0;
  while (index < rows.length) {
    const groupId = rows[index]?.groupId;
    if (!groupId) {
      index += 1;
      continue;
    }
    const start = index;
    while (index < rows.length && rows[index]?.groupId === groupId) index += 1;
    const group = rows.slice(start, index);
    const expected =
      group.length === 1
        ? ['single']
        : group.map((_, groupIndex) =>
            groupIndex === 0
              ? 'first'
              : groupIndex === group.length - 1
              ? 'last'
              : 'middle'
          );
    group.forEach((row, groupIndex) => {
      if (row.groupPosition !== expected[groupIndex]) {
        fail(
          `rows[${start + groupIndex}].groupPosition`,
          `must be "${expected[groupIndex]}"`
        );
      }
    });
    if (rows.slice(index).some((row) => row.groupId === groupId)) {
      fail(`rows[${start}].groupId`, `group "${groupId}" must be contiguous`);
    }
  }
}

function assertMediaPreview(value: unknown, path: string) {
  if (value === undefined) return;
  if (!value || typeof value !== 'object' || Array.isArray(value))
    fail(path, 'must be a media descriptor');
  const media = value as {
    source?: import('./models').ImageSource;
    probeOrder?: unknown;
  };
  assertImage(media.source, `${path}.source`);
  if (!media.source) fail(`${path}.source`, 'is required');
  const order = media.probeOrder;
  if (
    !Array.isArray(order) ||
    order.length < 1 ||
    order.length > 2 ||
    order.some((kind) => kind !== 'image' && kind !== 'video') ||
    new Set(order).size !== order.length
  ) {
    fail(
      `${path}.probeOrder`,
      'must contain one or two distinct image/video candidates'
    );
  }
}

function assertSnapshotContract(
  snapshot: NativeListSnapshot
): NativeListSnapshot {
  if (snapshot.disclosure !== undefined) {
    if (typeof snapshot.disclosure.expanded !== 'boolean')
      fail('snapshot.disclosure.expanded', 'must be a boolean');
    const inset = snapshot.disclosure.expandedTrailingInset;
    if (inset !== undefined && (!Number.isFinite(inset) || inset < 0))
      fail(
        'snapshot.disclosure.expandedTrailingInset',
        'must be finite and nonnegative'
      );
  }
  if (snapshot.structuralTransition) {
    assertKey(
      snapshot.structuralTransition.id,
      'snapshot.structuralTransition.id'
    );
    if (snapshot.structuralTransition.anchorKey !== undefined)
      assertKey(
        snapshot.structuralTransition.anchorKey,
        'snapshot.structuralTransition.anchorKey'
      );
    const duration = snapshot.structuralTransition.durationMs;
    if (
      duration !== undefined &&
      (!Number.isFinite(duration) || duration < 0 || duration > 2000)
    )
      fail(
        'snapshot.structuralTransition.durationMs',
        'must be finite within 0...2000'
      );
  }
  if (
    snapshot.capabilities?.refreshTriggerDistance !== undefined &&
    (!Number.isFinite(snapshot.capabilities.refreshTriggerDistance) ||
      snapshot.capabilities.refreshTriggerDistance <= 0)
  )
    fail(
      'capabilities.refreshTriggerDistance',
      'must be a positive finite number'
    );

  assertPlainSerializable(snapshot, 'snapshot', new Set<object>());
  assertText(
    snapshot.theme?.imageFallbackBackground,
    'theme.imageFallbackBackground'
  );
  if (
    snapshot.theme?.successBackground !== undefined &&
    (typeof snapshot.theme.successBackground !== 'string' ||
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(snapshot.theme.successBackground))
  )
    fail('theme.successBackground', 'must be a hex color');
  if (
    snapshot.theme?.rowHoverBackground !== undefined &&
    (typeof snapshot.theme.rowHoverBackground !== 'string' ||
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(snapshot.theme.rowHoverBackground))
  )
    fail('theme.rowHoverBackground', 'must be a hex color');
  if (snapshot.schemaVersion !== 1) fail('snapshot.schemaVersion', 'must be 1');
  if (!Number.isSafeInteger(snapshot.generation) || snapshot.generation < 0) {
    fail('snapshot.generation', 'must be a non-negative safe integer');
  }
  if (
    snapshot.layout.gridColumns !== undefined &&
    (!Number.isInteger(snapshot.layout.gridColumns) ||
      snapshot.layout.gridColumns < 2 ||
      snapshot.layout.gridColumns > 7)
  ) {
    fail('snapshot.layout.gridColumns', 'must be an integer from 2 to 7');
  }
  if (snapshot.layout.kind === 'grid' && !snapshot.layout.gridColumns) {
    fail('snapshot.layout.gridColumns', 'is required for grid layout');
  }
  assertNonNegativeNumber(
    snapshot.layout.contentPadding,
    'snapshot.layout.contentPadding'
  );
  assertNonNegativeNumber(
    snapshot.layout.contentPaddingHorizontal,
    'snapshot.layout.contentPaddingHorizontal'
  );
  assertNonNegativeNumber(
    snapshot.layout.contentPaddingTop,
    'snapshot.layout.contentPaddingTop'
  );
  assertNonNegativeNumber(
    snapshot.layout.contentPaddingBottom,
    'snapshot.layout.contentPaddingBottom'
  );
  assertNonNegativeNumber(
    snapshot.layout.itemSpacing,
    'snapshot.layout.itemSpacing'
  );
  if (
    snapshot.capabilities?.endReachedThreshold !== undefined &&
    (snapshot.capabilities.endReachedThreshold < 0 ||
      snapshot.capabilities.endReachedThreshold > 1)
  ) {
    fail('snapshot.capabilities.endReachedThreshold', 'must be within 0...1');
  }
  const sectionIndex = snapshot.capabilities?.sectionIndex;
  if (sectionIndex !== undefined) {
    if (typeof sectionIndex.enabled !== 'boolean') {
      fail('snapshot.capabilities.sectionIndex.enabled', 'must be a boolean');
    }
    if (
      sectionIndex.hapticsEnabled !== undefined &&
      typeof sectionIndex.hapticsEnabled !== 'boolean'
    ) {
      fail(
        'snapshot.capabilities.sectionIndex.hapticsEnabled',
        'must be a boolean'
      );
    }
    if (
      sectionIndex.centeredInWindow !== undefined &&
      typeof sectionIndex.centeredInWindow !== 'boolean'
    ) {
      fail(
        'snapshot.capabilities.sectionIndex.centeredInWindow',
        'must be a boolean'
      );
    }
    if (
      sectionIndex.enabled &&
      (snapshot.layout.kind !== 'sectioned' ||
        snapshot.layout.orientation === 'horizontal')
    ) {
      fail(
        'snapshot.capabilities.sectionIndex',
        'requires a vertical sectioned layout'
      );
    }
  }

  assertListStyle(snapshot.listStyle, 'snapshot.listStyle');

  const rowKeys = new Set<string>();
  const sectionIndexTitles = new Set<string>();
  snapshot.rows.forEach((row, index) => {
    assertRow(row, index);
    if (rowKeys.has(row.key))
      fail(`rows[${index}].key`, `duplicate key "${row.key}"`);
    rowKeys.add(row.key);
    if (row.type === 'sectionHeader' && row.indexTitle !== undefined) {
      const normalizedTitle = row.indexTitle.normalize('NFC');
      if (sectionIndexTitles.has(normalizedTitle)) {
        fail(
          `rows[${index}].indexTitle`,
          `duplicate index title "${row.indexTitle}"`
        );
      }
      sectionIndexTitles.add(normalizedTitle);
    }
  });
  if (snapshot.emptyState) assertRow(snapshot.emptyState, 0, 'emptyState');
  if (snapshot.fixedFooter) assertRow(snapshot.fixedFooter, 0, 'fixedFooter');
  assertGroups(snapshot.rows);

  const selectedKeys = new Set<string>();
  snapshot.selection?.selectedKeys.forEach((key, index) => {
    assertKey(key, `snapshot.selection.selectedKeys[${index}]`);
    if (!rowKeys.has(key))
      fail('snapshot.selection.selectedKeys', `unknown row key "${key}"`);
    const selectedRow = snapshot.rows.find((row) => row.key === key);
    if (
      selectedRow?.disabled ||
      selectedRow?.type === 'sectionHeader' ||
      selectedRow?.type === 'action' ||
      selectedRow?.type === 'system'
    ) {
      fail('snapshot.selection.selectedKeys', `row "${key}" is not selectable`);
    }
    if (selectedKeys.has(key))
      fail('snapshot.selection.selectedKeys', `duplicate key "${key}"`);
    selectedKeys.add(key);
  });
  if (snapshot.selection?.mode === 'none' && selectedKeys.size > 0) {
    fail(
      'snapshot.selection.selectedKeys',
      'must be empty when selection mode is none'
    );
  }
  if (snapshot.selection?.mode === 'single' && selectedKeys.size > 1) {
    fail(
      'snapshot.selection.selectedKeys',
      'supports at most one key in single mode'
    );
  }
  return snapshot;
}

const COMMON_ROW_FIELDS = [
  'height',
  'heightRounding',
  'testID',
  'opacity',
  'backgroundColor',
  'backgroundFullWidth',
  'pressDisabled',
  'key',
  'revision',
  'sectionKey',
  'groupId',
  'groupPosition',
  'size',
  'density',
  'disabled',
  'selected',
  'accessibilityLabel',
  'separator',
  'type',
  'style',
];

const ROW_FIELDS: Readonly<Record<string, readonly string[]>> = {
  identity: [
    ...COMMON_ROW_FIELDS,
    'contentHeight',
    'longPressActionKey',
    'titleMatch',
    'titleActionKey',
    'titleActionOnHover',
    'subtitleSegments',
    'presentation',
    'leading',
    'leadingAction',
    'title',
    'titleSuffixes',
    'titleIcon',
    'secondaryTitleIcon',
    'titleImage',
    'subtitle',
    'tertiary',
    'tertiaryTone',
    'titleLines',
    'subtitleLines',
    'badges',
    'trailing',
    'draggable',
  ],
  walletGroup: [...COMMON_ROW_FIELDS, 'parent', 'children', 'draggable'],
  rail: [
    ...COMMON_ROW_FIELDS,
    'visual',
    'title',
    'status',
    'badge',
    'draggable',
  ],
  activity: [
    ...COMMON_ROW_FIELDS,
    'leading',
    'hideLeading',
    'secondaryLeading',
    'title',
    'description',
    'status',
    'primaryAmount',
    'secondaryAmount',
    'amounts',
    'badges',
    'presentation',
    'fee',
    'descriptionActionKey',
    'footerActions',
  ],
  message: [
    ...COMMON_ROW_FIELDS,
    'presentation',
    'unread',
    'leading',
    'title',
    'body',
    'bodyLines',
    'time',
    'thumbnail',
  ],
  dataRow: [
    ...COMMON_ROW_FIELDS,
    'leading',
    'columns',
    'trailing',
    'checkbox',
    'index',
    'badge',
    'badges',
    'favorite',
    'favoriteActive',
  ],
  market: [
    ...COMMON_ROW_FIELDS,
    'variant',
    'webTable',
    'leadingAction',
    'leading',
    'title',
    'subtitle',
    'subtitlePrefix',
    'subtitleSegments',
    'price',
    'priceSegments',
    'change',
    'badges',
    'pressActionKey',
    'pressInActionKey',
    'longPressActionKey',
    'diagnostics',
  ],
  mediaTile: [
    ...COMMON_ROW_FIELDS,
    'variant',
    'media',
    'image',
    'imageState',
    'networkImage',
    'title',
    'subtitle',
    'badge',
    'closeActionKey',
  ],
  metricCard: [
    ...COMMON_ROW_FIELDS,
    'variant',
    'title',
    'value',
    'subtitle',
    'trend',
    'trendTone',
    'visual',
    'badge',
    'metrics',
    'progress',
  ],
  sectionHeader: [
    ...COMMON_ROW_FIELDS,
    'sticky',
    'valueActionTestID',
    'valueSegments',
    'titleActionKey',
    'titleActionOnHover',
    'presentation',
    'indexTitle',
    'variant',
    'title',
    'leadingBadge',
    'titleLoading',
    'subtitle',
    'value',
    'valueHelp',
    'valueActionKey',
    'titleIcon',
    'valueIcon',
    'checkbox',
  ],
  action: [
    ...COMMON_ROW_FIELDS,
    'presentation',
    'title',
    'actionKey',
    'tone',
    'icon',
    'checkbox',
    'trailing',
  ],
  buttonGroup: [...COMMON_ROW_FIELDS, 'buttons'],
  system: [
    ...COMMON_ROW_FIELDS,
    'variant',
    'presentation',
    'loadingStyle',
    'message',
    'actionKey',
    'actionText',
    'title',
    'borderColor',
  ],
};

const COMMON_PATCH_FIELDS = [
  'height',
  'heightRounding',
  'opacity',
  'pressDisabled',
  'revision',
  'disabled',
  'selected',
  'accessibilityLabel',
  'separator',
  'style',
];

const PATCH_FIELDS: Readonly<Record<string, readonly string[]>> = {
  identity: [
    ...COMMON_PATCH_FIELDS,
    'contentHeight',
    'longPressActionKey',
    'titleMatch',
    'titleActionKey',
    'titleActionOnHover',
    'subtitleSegments',
    'leading',
    'leadingAction',
    'title',
    'titleSuffixes',
    'titleIcon',
    'secondaryTitleIcon',
    'titleImage',
    'subtitle',
    'tertiary',
    'tertiaryTone',
    'subtitleLines',
    'badges',
    'trailing',
  ],
  rail: [...COMMON_PATCH_FIELDS, 'title', 'visual', 'status', 'badge'],
  activity: [
    ...COMMON_PATCH_FIELDS,
    'presentation',
    'leading',
    'title',
    'badges',
    'status',
    'hideLeading',
    'secondaryLeading',
    'description',
    'primaryAmount',
    'secondaryAmount',
    'amounts',
    'fee',
    'descriptionActionKey',
    'footerActions',
  ],
  message: [
    ...COMMON_PATCH_FIELDS,
    'presentation',
    'title',
    'unread',
    'body',
    'bodyLines',
    'time',
    'thumbnail',
  ],
  dataRow: [
    ...COMMON_PATCH_FIELDS,
    'leading',
    'badges',
    'trailing',
    'badge',
    'columns',
    'checkbox',
    'index',
    'favorite',
    'favoriteActive',
  ],
  market: [
    ...COMMON_PATCH_FIELDS,
    'longPressActionKey',
    'subtitleSegments',
    'leading',
    'leadingAction',
    'title',
    'subtitle',
    'badges',
    'webTable',
    'price',
    'priceSegments',
    'change',
    'pressActionKey',
    'pressInActionKey',
    'diagnostics',
  ],
  mediaTile: [
    ...COMMON_PATCH_FIELDS,
    'title',
    'subtitle',
    'badge',
    'media',
    'image',
    'imageState',
    'networkImage',
    'closeActionKey',
  ],
  metricCard: [
    ...COMMON_PATCH_FIELDS,
    'title',
    'subtitle',
    'visual',
    'badge',
    'variant',
    'value',
    'trend',
    'trendTone',
    'metrics',
    'progress',
  ],
  sectionHeader: [
    ...COMMON_PATCH_FIELDS,
    'titleActionKey',
    'titleActionOnHover',
    'title',
    'titleIcon',
    'subtitle',
    'checkbox',
    'variant',
    'value',
    'leadingBadge',
    'titleLoading',
    'valueHelp',
    'valueActionKey',
    'valueIcon',
  ],
  action: [
    ...COMMON_PATCH_FIELDS,
    'title',
    'trailing',
    'checkbox',
    'tone',
    'icon',
  ],
  buttonGroup: [...COMMON_PATCH_FIELDS, 'buttons'],
  system: ['revision', 'disabled', 'message'],
};

function protocolObject(value: unknown, path: string): Record<string, unknown> {
  if (!value || typeof value !== 'object' || Array.isArray(value))
    fail(path, 'must be an object');
  return value as Record<string, unknown>;
}

function protocolKey(value: unknown, path: string): void {
  if (typeof value !== 'string' || value.trim().length === 0)
    fail(path, 'must be a non-blank string');
}

const PROTOCOL_ARRAY_FIELDS = new Set([
  'columns',
  'titleSuffixes',
  'cells',
  'points',
  'images',
  'overlays',
  'badges',
  'trailing',
  'footerActions',
  'buttons',
  'amounts',
  'metrics',
  'children',
  'subtitleSegments',
  'titleMatch',
  'selectedKeys',
  'segments',
  'hoverSecondary',
  'hoverImages',
  'secondaryBadges',
  'fallbackUris',
  'probeOrder',
  'priceSegments',
  'textSegments',
  'secondaryTextSegments',
  'primaryTextSegments',
]);
const PROTOCOL_NUMBER_FIELDS = new Set([
  'leadingContentMaxWidthRatio',
  'height',
  'width',
  'fontSize',
  'lineHeight',
  'opacity',
  'offsetY',
  'offsetX',
  'cornerRadius',
  'borderWidth',
  'horizontalPadding',
  'verticalPadding',
  'leadingGap',
  'lineGap',
  'titleBadgeGap',
  'trailingGap',
  'start',
  'end',
  'padding',
  'offset',
  'overscan',
  'minWidth',
  'flex',
  'paddingStart',
  'paddingEnd',
  'changeWidth',
  'changeHeight',
  'changeCornerRadius',
  'contentTrailingGap',
  'subtitleTrailingPadding',
  'partGap',
  'contentPadding',
  'contentPaddingHorizontal',
  'contentPaddingTop',
  'contentPaddingBottom',
  'itemSpacing',
  'gridColumns',
]);

/** Validate only shapes that renderers dereference, not display budgets. */
function assertProtocolFields(
  value: unknown,
  path: string,
  parentField = ''
): void {
  if (value === undefined || value === null || typeof value !== 'object')
    return;
  if (Array.isArray(value)) {
    for (const [index, item] of value.entries()) {
      const itemPath = `${path}[${index}]`;
      if (
        ['selectedKeys', 'fallbackUris', 'probeOrder'].includes(parentField)
      ) {
        if (typeof item !== 'string') fail(itemPath, 'must be a string');
      } else if (parentField === 'points') {
        if (typeof item !== 'number') fail(itemPath, 'must be a finite number');
      } else if (parentField === 'hoverImages' && item === null) continue;
      else protocolObject(item, itemPath);
      assertProtocolFields(item, itemPath, parentField);
    }
    return;
  }
  const object = value as Record<string, unknown>;
  if (parentField === 'fallbackIcon') {
    protocolKey(object.name, `${path}.name`);
    if (
      object.size !== undefined &&
      (typeof object.size !== 'number' ||
        !Number.isFinite(object.size) ||
        object.size <= 0 ||
        object.size > 40)
    )
      fail(`${path}.size`, 'must be within (0, 40]');
    for (const field of ['tintColor', 'backgroundColor'] as const) {
      const color = object[field];
      if (
        color !== undefined &&
        (typeof color !== 'string' ||
          !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(color))
      )
        fail(`${path}.${field}`, 'must be a hex color');
    }
  }
  if (
    parentField === 'overlays' &&
    object.fallbackIcon !== undefined &&
    !object.image
  )
    fail(`${path}.fallbackIcon`, 'requires image');
  if (
    !path.includes('.style.') &&
    ['image', 'networkImage', 'thumbnail', 'images', 'hoverImages'].includes(
      parentField
    ) &&
    typeof object.uri !== 'string'
  )
    fail(`${path}.uri`, 'must be a string');
  Object.entries(value).forEach(([field, item]) => {
    if (item === undefined) return;
    if (field === 'headers') {
      const headers = protocolObject(item, `${path}.headers`);
      for (const [name, headerValue] of Object.entries(headers)) {
        if (!name.trim() || typeof headerValue !== 'string')
          fail(`${path}.headers`, 'must contain string headers');
      }
      return;
    }
    const fieldPath = `${path}.${field}`;
    if (
      !path.endsWith('.style') &&
      PROTOCOL_ARRAY_FIELDS.has(field) &&
      !Array.isArray(item)
    )
      fail(fieldPath, 'must be an array');
    if (PROTOCOL_NUMBER_FIELDS.has(field) && typeof item !== 'number')
      fail(fieldPath, 'must be a finite number');
    if (
      PROTOCOL_NUMBER_FIELDS.has(field) &&
      typeof item === 'number' &&
      item < 0 &&
      !['offsetX', 'offsetY'].includes(field)
    )
      fail(fieldPath, 'must be non-negative');
    if (
      parentField === 'overlays' &&
      field === 'fontWeight' &&
      !['regular', 'medium', 'semibold', 'bold'].includes(item as string)
    )
      fail(fieldPath, 'must be regular, medium, semibold, or bold');
    if (field === 'buttonStyle' && item !== 'tertiary')
      fail(fieldPath, 'must be tertiary');
    if (field === 'hideLeading' && typeof item !== 'boolean')
      fail(fieldPath, 'must be boolean');
    if (
      field === 'key' ||
      field === 'sectionKey' ||
      field === 'groupId' ||
      field === 'actionKey' ||
      field.endsWith('ActionKey')
    )
      protocolKey(item, fieldPath);
    if (
      (field === 'uri' || field === 'fallbackUri') &&
      typeof item !== 'string'
    )
      fail(fieldPath, 'must be a string');
    if (
      (field === 'style' &&
        !(item === 'subscript' && /[Ss]egments\[/.test(path))) ||
      field === 'container'
    )
      protocolObject(item, fieldPath);
    if (
      path.endsWith('.style') &&
      [
        'title',
        'subtitle',
        'tertiary',
        'body',
        'time',
        'price',
        'change',
        'columns',
        'columnSecondary',
        'value',
        'valueSecondary',
        'badge',
        'description',
        'status',
        'primaryAmount',
        'secondaryAmount',
        'trend',
        'message',
        'actionText',
      ].includes(field)
    )
      protocolObject(item, fieldPath);
    assertProtocolFields(item, fieldPath, field);
  });
}

function assertRowProtocol(row: RowModel, path: string): void {
  protocolObject(row, path);
  assertRowType(row.type, path);
  protocolKey(row.key, `${path}.key`);
  assertCoreRow(row, path);
  assertProtocolFields(row, path);
  for (const field of ['leading', 'visual', 'secondaryLeading'] as const) {
    const visual = (row as unknown as Record<string, unknown>)[field];
    if (
      visual !== undefined ||
      (field === 'leading' &&
        (row.type === 'identity' || row.type === 'market'))
    ) {
      const object = protocolObject(visual, `${path}.${field}`);
      if (object.kind === 'stackedImages' && !Array.isArray(object.images))
        fail(`${path}.${field}.images`, 'must be an array');
    }
  }
  for (const field of [
    'title',
    'subtitle',
    'tertiary',
    'body',
    'time',
    'price',
    'indexTitle',
  ] as const) {
    const value = (row as unknown as Record<string, unknown>)[field];
    if (value !== undefined && typeof value !== 'string')
      fail(`${path}.${field}`, 'must be a string');
  }
  if (row.height !== undefined && row.height < 0)
    fail(`${path}.height`, 'must be non-negative');
  if (row.type === 'identity') {
    if (isDevelopment()) assertIdentityExtras(row, path);
    if (row.titleSuffixes !== undefined && !Array.isArray(row.titleSuffixes))
      fail(`${path}.titleSuffixes`, 'must be an array');
    row.titleSuffixes?.forEach((suffix, index) => {
      protocolObject(suffix, `${path}.titleSuffixes[${index}]`);
      if (
        typeof suffix.text !== 'string' ||
        !['secondary', 'disabled'].includes(suffix.tone)
      )
        fail(`${path}.titleSuffixes[${index}]`, 'invalid title suffix');
      if (
        suffix.maxWidth !== undefined &&
        (!Number.isFinite(suffix.maxWidth) ||
          suffix.maxWidth < 0 ||
          suffix.maxWidth > 4096)
      )
        fail(
          `${path}.titleSuffixes[${index}].maxWidth`,
          'must be finite within 0...4096'
        );
    });
    let end = 0;
    row.titleMatch?.forEach((match) => {
      if (
        !Number.isInteger(match.start) ||
        !Number.isInteger(match.end) ||
        match.start < end ||
        match.end <= match.start ||
        match.end > row.title.length
      )
        fail(`${path}.titleMatch`, 'invalid text range');
      end = match.end;
    });
  }
  if (row.type === 'buttonGroup' && isDevelopment())
    assertButtonGroup(row, path);
  if (row.type === 'sectionHeader' && isDevelopment())
    assertTextHelp(row.valueHelp, `${path}.valueHelp`);
  if (row.type === 'mediaTile' && row.media) {
    protocolObject(row.media.source, `${path}.media.source`);
    if (!Array.isArray(row.media.probeOrder))
      fail(`${path}.media.probeOrder`, 'must be an array');
  }
  if (row.type === 'walletGroup') {
    protocolObject(row.parent, `${path}.parent`);
    if (!Array.isArray(row.children))
      fail(`${path}.children`, 'must be an array');
    const members = [row.parent, ...row.children];
    const keys = new Set<string>();
    if (row.parent.key !== row.key)
      fail(`${path}.parent.key`, 'must match its group');
    members.forEach((member, index) => {
      assertRowProtocol(member, `${path}.members[${index}]`);
      if (member.type !== 'identity' || keys.has(member.key))
        fail(path, 'invalid wallet member identity');
      keys.add(member.key);
    });
  }
  if (row.type === 'dataRow') {
    if (!Array.isArray(row.columns))
      fail(`${path}.columns`, 'must be an array');
    row.columns.forEach((column, index) => {
      const columnPath = `${path}.columns[${index}]`;
      protocolObject(column, columnPath);
      if (isDevelopment()) assertIdentityExtras(column, columnPath);
    });
    if (isDevelopment())
      assertTrailingAccessories(row.trailing, `${path}.trailing`);
  }
  if (row.type === 'market') {
    protocolObject(row.change, `${path}.change`);
    if (row.webTable) {
      const { columns, cells } = row.webTable;
      if (
        !Array.isArray(columns) ||
        !Array.isArray(cells) ||
        columns.length !== cells.length
      )
        fail(`${path}.webTable`, 'columns and cells must match');
      const keys = new Set<string>();
      columns.forEach((column, index) => {
        protocolKey(column.key, `${path}.webTable.columns[${index}].key`);
        if (keys.has(column.key) || cells[index]?.key !== column.key)
          fail(`${path}.webTable`, 'invalid column identity');
        keys.add(column.key);
        const cell = cells[index];
        if (!['favorite', 'asset', 'text', 'sparkline'].includes(cell.kind))
          fail(`${path}.webTable.cells[${index}]`, 'unsupported cell kind');
        if (cell.kind === 'asset')
          protocolObject(cell.title, `${path}.webTable.cells[${index}].title`);
        if (
          (cell.kind === 'asset' || cell.kind === 'text') &&
          cell.secondary !== undefined &&
          !Array.isArray(cell.secondary)
        )
          fail(
            `${path}.webTable.cells[${index}].secondary`,
            'must be an array'
          );
        if (cell.kind === 'text' && !Array.isArray(cell.primary))
          fail(`${path}.webTable.cells[${index}].primary`, 'must be an array');
        // The Web renderer spreads samples into Math.min/Math.max.
        if (
          cell.kind === 'sparkline' &&
          (!Array.isArray(cell.points) || cell.points.length > 256)
        )
          fail(
            `${path}.webTable.cells[${index}].points`,
            'must be an array of at most 256 samples'
          );
      });
    }
  }
}

function assertSnapshotProtocol(snapshot: NativeListSnapshot): void {
  protocolObject(snapshot, 'snapshot');
  if (snapshot.disclosure !== undefined) {
    protocolObject(snapshot.disclosure, 'snapshot.disclosure');
    if (typeof snapshot.disclosure.expanded !== 'boolean')
      fail('snapshot.disclosure.expanded', 'must be a boolean');
    const inset = snapshot.disclosure.expandedTrailingInset;
    if (inset !== undefined && (!Number.isFinite(inset) || inset < 0))
      fail(
        'snapshot.disclosure.expandedTrailingInset',
        'must be finite and nonnegative'
      );
  }
  if (snapshot.structuralTransition) {
    protocolObject(
      snapshot.structuralTransition,
      'snapshot.structuralTransition'
    );
    protocolKey(
      snapshot.structuralTransition.id,
      'snapshot.structuralTransition.id'
    );
    if (snapshot.structuralTransition.anchorKey !== undefined)
      protocolKey(
        snapshot.structuralTransition.anchorKey,
        'snapshot.structuralTransition.anchorKey'
      );
    const duration = snapshot.structuralTransition.durationMs;
    if (
      duration !== undefined &&
      (!Number.isFinite(duration) || duration < 0 || duration > 2000)
    )
      fail(
        'snapshot.structuralTransition.durationMs',
        'must be finite within 0...2000'
      );
  }
  if (
    snapshot.theme?.successBackground !== undefined &&
    (typeof snapshot.theme.successBackground !== 'string' ||
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(snapshot.theme.successBackground))
  )
    fail('theme.successBackground', 'must be a hex color');
  if (
    snapshot.theme?.rowHoverBackground !== undefined &&
    (typeof snapshot.theme.rowHoverBackground !== 'string' ||
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(snapshot.theme.rowHoverBackground))
  )
    fail('theme.rowHoverBackground', 'must be a hex color');
  if (snapshot.schemaVersion !== 1) fail('snapshot.schemaVersion', 'must be 1');
  if (!Number.isSafeInteger(snapshot.generation) || snapshot.generation < 0)
    fail('snapshot.generation', 'must be a non-negative safe integer');
  protocolObject(snapshot.layout, 'snapshot.layout');
  assertProtocolFields(snapshot.layout, 'snapshot.layout');
  if (
    snapshot.layout.orientation !== undefined &&
    !['vertical', 'horizontal'].includes(snapshot.layout.orientation)
  )
    fail('snapshot.layout.orientation', 'is not supported');
  if (!['linear', 'sectioned', 'grid', 'table'].includes(snapshot.layout.kind))
    fail('snapshot.layout.kind', 'is not supported');
  if (!Array.isArray(snapshot.rows)) fail('snapshot.rows', 'must be an array');
  if (
    snapshot.layout.kind === 'grid' &&
    (!Number.isInteger(snapshot.layout.gridColumns) ||
      (snapshot.layout.gridColumns ?? 0) < 1 ||
      (snapshot.layout.gridColumns ?? 0) > 7)
  )
    fail('snapshot.layout.gridColumns', 'must be an integer from 1 to 7');
  const keys = new Set<string>();
  for (const [index, row] of snapshot.rows.entries()) {
    assertRowProtocol(row, `rows[${index}]`);
    if (keys.has(row.key)) fail(`rows[${index}].key`, 'duplicate row identity');
    keys.add(row.key);
  }
  if (snapshot.emptyState) assertRowProtocol(snapshot.emptyState, 'emptyState');
  if (snapshot.fixedFooter)
    assertRowProtocol(snapshot.fixedFooter, 'fixedFooter');
  if (snapshot.selection) {
    if (!Array.isArray(snapshot.selection.selectedKeys))
      fail('snapshot.selection.selectedKeys', 'must be an array');
    snapshot.selection.selectedKeys.forEach((key) => {
      if (!keys.has(key))
        fail('snapshot.selection.selectedKeys', 'unknown row identity');
    });
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}

function isAllowedImageUri(value: unknown): value is string {
  return (
    typeof value === 'string' &&
    value.trim().length > 0 &&
    (!/^[a-z][a-z\d+.-]*:/i.test(value) ||
      /^(https?|file|content|ph|assets-library|asset|res|blob):/i.test(value) ||
      /^data:image\//i.test(value))
  );
}

const IMAGE_FIELDS = [
  'uri',
  'width',
  'height',
  'headers',
  'contentFit',
  'cachePolicy',
  'autoplay',
  'optimizeTos',
  'overscan',
  'loadingStrategy',
  'fallbackUri',
  'fallbackUris',
  'retryTimes',
];
const ICON_FIELDS = [
  'name',
  'tintColor',
  'backgroundColor',
  'size',
  'accessibilityLabel',
  'hoverActionKey',
];
const VISUAL_FIELDS = [
  'kind',
  'name',
  'image',
  'images',
  'networkImage',
  'shape',
  'tintColor',
  'backgroundColor',
  'fallbackIcon',
  'fallbackText',
  'fallbackTextColor',
  'overlays',
  'borderStyle',
  'borderColor',
  'cornerIcon',
];
const TEXT_STYLE_FIELDS = [
  'token',
  'fontSize',
  'fontWeight',
  'color',
  'lineHeight',
  'lines',
  'truncate',
  'alignment',
  'verticalAlignment',
  'offsetY',
];
const ACCESSORY_FIELDS: Readonly<Record<string, readonly string[]>> = {
  value: ['kind', 'text', 'secondary', 'textSegments'],
  valuePair: [
    'kind',
    'primary',
    'secondary',
    'primaryFontWeight',
    'primaryHelp',
    'primaryTextSegments',
    'secondaryTextSegments',
    'primaryTone',
    'secondaryTone',
  ],
  button: [
    'kind',
    'label',
    'actionKey',
    'disabled',
    'testID',
    'accessibilityLabel',
    'textColor',
    'backgroundColor',
    'pressedBackgroundColor',
    'hoverBackgroundColor',
    'focusRingColor',
  ],
  icon: [
    'kind',
    'name',
    'tintColor',
    'buttonStyle',
    'disabled',
    'actionKey',
    'testID',
    'hoverActionKey',
    'accessibilityLabel',
  ],
  checkbox: ['kind', 'state', 'disabled', 'loading', 'target', 'actionKey'],
  radio: ['kind', 'checked', 'disabled', 'actionKey'],
  switch: ['kind', 'value', 'disabled', 'actionKey'],
  chevron: ['kind', 'actionKey'],
  menu: ['kind', 'actionKey'],
  drag: ['kind', 'actionKey'],
  spinner: ['kind'],
  progress: ['kind', 'value'],
};

const REQUIRED_ROW_FIELDS: Readonly<Record<string, readonly string[]>> = {
  identity: ['title', 'leading'],
  walletGroup: ['parent', 'children'],
  rail: ['title', 'visual'],
  activity: ['title', 'leading'],
  message: ['title', 'body', 'time'],
  dataRow: ['columns'],
  market: ['title', 'leading', 'variant', 'price', 'change'],
  mediaTile: ['title', 'variant'],
  metricCard: ['title', 'value'],
  sectionHeader: ['title', 'sectionKey'],
  action: ['title', 'actionKey'],
  buttonGroup: ['buttons'],
  system: ['variant'],
};

function assertCoreRow(row: RowModel, path: string): void {
  const object = row as unknown as Record<string, unknown>;
  for (const key of REQUIRED_ROW_FIELDS[row.type]) {
    const value = object[key];
    if (value === undefined || value === null)
      fail(`${path}.${key}`, 'required field');
    if (
      ['title', 'body', 'time', 'price', 'value', 'variant'].includes(key) &&
      typeof value !== 'string'
    )
      fail(`${path}.${key}`, 'must be a string');
  }
  if (row.type === 'market') {
    if (!row.price.trim()) fail(`${path}.price`, 'must be nonblank');
    if (typeof row.change?.text !== 'string')
      fail(`${path}.change.text`, 'must be a string');
  }
  if (row.type === 'activity') {
    row.amounts?.forEach((amount, index) => {
      protocolKey(amount.key, `${path}.amounts[${index}].key`);
      if (typeof amount.text !== 'string')
        fail(`${path}.amounts[${index}].text`, 'required financial text');
    });
    if (row.fee && typeof row.fee.primary !== 'string')
      fail(`${path}.fee.primary`, 'required financial text');
    row.footerActions?.forEach((action, index) => {
      protocolKey(action.key, `${path}.footerActions[${index}].key`);
      protocolKey(action.label, `${path}.footerActions[${index}].label`);
    });
  }
  if (row.type === 'action') protocolKey(row.actionKey, `${path}.actionKey`);
  if (row.type === 'sectionHeader')
    protocolKey(row.sectionKey, `${path}.sectionKey`);
  if (
    row.type === 'system' &&
    ['retry', 'warning', 'noMatch'].includes(row.variant)
  ) {
    if (typeof object.message !== 'string')
      fail(`${path}.message`, 'must be a string');
    if (row.variant === 'retry')
      protocolKey(row.actionKey, `${path}.actionKey`);
  }
  if (row.type === 'dataRow') {
    if (!Array.isArray(row.columns) || row.columns.length === 0)
      fail(`${path}.columns`, 'required columns');
    row.columns.forEach((column, index) => {
      protocolObject(column, `${path}.columns[${index}]`);
      protocolKey(column.key, `${path}.columns[${index}].key`);
      if (typeof column.text !== 'string')
        fail(`${path}.columns[${index}].text`, 'must be a string');
    });
  }
  if (row.type === 'buttonGroup') {
    if (!Array.isArray(row.buttons) || row.buttons.length === 0)
      fail(`${path}.buttons`, 'required buttons');
    const keys = new Set<string>();
    row.buttons.forEach((button, index) => {
      const field = `${path}.buttons[${index}]`;
      protocolObject(button, field);
      protocolKey(button.key, `${field}.key`);
      protocolKey(button.actionKey, `${field}.actionKey`);
      protocolKey(button.label, `${field}.label`);
      if (keys.has(button.key)) fail(field, 'duplicate button identity');
      keys.add(button.key);
    });
  }
  for (const field of [
    'trailing',
    'leadingAction',
    'titleIcon',
    'valueIcon',
  ] as const) {
    const value = object[field];
    if (value === undefined) continue;
    // Identity title icons are decorative descriptors, not trailing controls.
    if (field === 'titleIcon' && row.type !== 'sectionHeader') continue;
    const accessories = field === 'trailing' ? value : [value];
    if (!Array.isArray(accessories))
      fail(`${path}.${field}`, 'must be an array');
    accessories.forEach((accessory, index) => {
      const entry = protocolObject(accessory, `${path}.${field}[${index}]`);
      for (const key of entry.kind === 'value'
        ? ['text']
        : entry.kind === 'valuePair'
        ? ['primary', 'secondary']
        : []) {
        if (typeof entry[key] !== 'string')
          fail(`${path}.${field}[${index}].${key}`, 'required financial text');
      }
      if (
        ['button', 'menu', 'switch'].includes(entry.kind as string) ||
        'actionKey' in entry
      ) {
        protocolKey(entry.actionKey, `${path}.${field}[${index}].actionKey`);
        if (entry.kind === 'button')
          protocolKey(entry.label, `${path}.${field}[${index}].label`);
      }
      if (entry.kind === 'icon')
        protocolKey(entry.name, `${path}.${field}[${index}].name`);
    });
  }
}

function pickFields(
  object: Record<string, unknown>,
  fields: readonly string[]
): Record<string, unknown> {
  return Object.fromEntries(
    Object.entries(object).filter(([key]) => fields.includes(key))
  );
}

function sanitizeImage(value: unknown): Record<string, unknown> | undefined {
  if (
    !isRecord(value) ||
    !isAllowedImageUri(value.uri) ||
    typeof value.width !== 'number' ||
    !Number.isFinite(value.width) ||
    value.width <= 0 ||
    typeof value.height !== 'number' ||
    !Number.isFinite(value.height) ||
    value.height <= 0
  )
    return undefined;
  const next = pickFields(value, IMAGE_FIELDS);
  next.width = Math.min(MAX_IMAGE_EDGE, value.width);
  next.height = Math.min(MAX_IMAGE_EDGE, value.height);
  if (next.headers !== undefined) {
    if (!isRecord(next.headers)) delete next.headers;
    else
      next.headers = Object.fromEntries(
        Object.entries(next.headers)
          .filter(
            ([key, item]) =>
              key.trim() &&
              key.length <= MAX_HEADER_LENGTH &&
              typeof item === 'string' &&
              item.length <= MAX_HEADER_LENGTH
          )
          .slice(0, MAX_IMAGE_HEADERS)
      );
  }
  if (next.fallbackUris !== undefined) {
    if (Array.isArray(next.fallbackUris))
      next.fallbackUris = next.fallbackUris.filter(isAllowedImageUri);
    else delete next.fallbackUris;
  }
  if (next.fallbackUri !== undefined && !isAllowedImageUri(next.fallbackUri))
    delete next.fallbackUri;
  if (
    next.overscan !== undefined &&
    (typeof next.overscan !== 'number' ||
      !Number.isFinite(next.overscan) ||
      next.overscan < 1 ||
      next.overscan > 4)
  )
    delete next.overscan;
  if (
    next.retryTimes !== undefined &&
    (typeof next.retryTimes !== 'number' ||
      !Number.isFinite(next.retryTimes) ||
      next.retryTimes < 0)
  )
    delete next.retryTimes;
  const options: Readonly<Record<string, readonly string[]>> = {
    contentFit: ['cover', 'contain', 'fill', 'center'],
    cachePolicy: ['memory-disk', 'memory', 'disk', 'none'],
    loadingStrategy: ['static', 'skeleton', 'none'],
  };
  for (const [key, allowed] of Object.entries(options))
    if (next[key] !== undefined && !allowed.includes(next[key] as string))
      delete next[key];
  for (const key of ['autoplay', 'optimizeTos'])
    if (next[key] !== undefined && typeof next[key] !== 'boolean')
      delete next[key];
  return next;
}

const NESTED_DECORATION_FIELDS: Readonly<Record<string, readonly string[]>> = {
  webTable: ['columns', 'cells', 'testID', 'draggable', 'contextMenuActionKey'],
  columns: [
    'key',
    'text',
    'textSegments',
    'titleIcon',
    'secondaryTitleIcon',
    'secondaryLeadingText',
    'secondaryText',
    'secondaryTextSegments',
    'weight',
    'alignment',
    'tone',
    'secondaryTone',
    'width',
    'minWidth',
    'flex',
    'paddingStart',
    'paddingEnd',
  ],
  cells: [
    'key',
    'kind',
    'active',
    'disabled',
    'testID',
    'hoverActionKey',
    'actionKey',
    'accessibilityLabel',
    'leading',
    'title',
    'secondary',
    'lineGap',
    'secondaryBadges',
    'hoverImages',
    'hoverSecondary',
    'hoverActionLabel',
    'badges',
    'primary',
    'partGap',
    'points',
    'width',
    'height',
    'strokeColor',
    'fillColor',
  ],
  badges: [
    'key',
    'text',
    'tone',
    'iconName',
    'icon',
    'textColor',
    'backgroundColor',
    'actionKey',
    'accessibilityLabel',
    'style',
  ],
  overlays: [
    'position',
    'size',
    'width',
    'height',
    'padding',
    'offset',
    'offsetX',
    'offsetY',
    'image',
    'fallbackIcon',
    'name',
    'text',
    'fontWeight',
    'tintColor',
    'backgroundColor',
  ],
  primaryHelp: ['actionKey', 'presentation', 'underline'],
  valueHelp: ['actionKey', 'presentation', 'underline'],
};

function sanitizeDecoration(value: unknown, field: string): unknown {
  if (value === undefined || value === null) return undefined;
  if (
    !Array.isArray(value) &&
    [
      'image',
      'networkImage',
      'thumbnail',
      'source',
      'images',
      'hoverImages',
    ].includes(field)
  )
    return sanitizeImage(value);
  if (
    [
      'titleImage',
      'titleIcon',
      'secondaryTitleIcon',
      'fallbackIcon',
      'cornerIcon',
      'media',
    ].includes(field) &&
    !isRecord(value)
  )
    return undefined;
  if (field === 'style' && typeof value !== 'string' && !isRecord(value))
    return undefined;
  if (
    ['badges', 'overlays'].includes(field) &&
    !Array.isArray(value) &&
    !isRecord(value)
  )
    return undefined;
  if (field === 'style' && typeof value === 'string')
    return value === 'subscript' ? value : undefined;
  if (Array.isArray(value)) {
    const items = value.map((item) => sanitizeDecoration(item, field));
    return field === 'hoverImages'
      ? items.map((item) => item ?? null)
      : items.filter((item) => item !== undefined);
  }
  if (!isRecord(value)) return value;
  let object = value;
  if (field === 'titleImage') {
    const image = sanitizeImage(value.image);
    if (!image) return undefined;
    const edge = Math.min(
      40,
      Math.max(
        1,
        Math.round(Math.min(image.width as number, image.height as number))
      )
    );
    object = {
      ...pickFields(value, ['image', 'accessibilityLabel', 'hoverActionKey']),
      image: { ...image, width: edge, height: edge },
    };
  } else if (
    ['titleIcon', 'secondaryTitleIcon', 'fallbackIcon', 'cornerIcon'].includes(
      field
    ) &&
    value.kind === undefined
  ) {
    if (typeof value.name !== 'string' || !value.name.trim()) return undefined;
    object = pickFields(value, ICON_FIELDS);
  } else if (['leading', 'visual', 'secondaryLeading'].includes(field)) {
    object = pickFields(value, VISUAL_FIELDS);
  } else if (field === 'media') {
    object = pickFields(value, ['source', 'probeOrder']);
  }
  if (
    field === 'trailing' ||
    (['leadingAction', 'titleIcon', 'valueIcon'].includes(field) &&
      object.kind !== undefined)
  ) {
    const fields = ACCESSORY_FIELDS[object.kind as string];
    if (fields) object = pickFields(object, fields);
  } else if (field === 'buttons')
    object = pickFields(object, [
      'key',
      'label',
      'actionKey',
      'disabled',
      'loading',
      'testID',
      'accessibilityLabel',
    ]);
  else if (field === 'container')
    object = pickFields(object, [
      'height',
      'backgroundColor',
      'opacity',
      'cornerRadius',
      'borderWidth',
      'borderColor',
      'contentVerticalAlignment',
    ]);
  else if (field === 'imageStyle')
    object = pickFields(object, [
      'width',
      'height',
      'shape',
      'cornerRadius',
      'contentFit',
    ]);
  else if (field === 'style')
    object = pickFields(object, [
      ...TEXT_STYLE_FIELDS,
      'container',
      ...new Set(Object.values(ROW_BOX_STYLE_KEYS_BY_TYPE).flat()),
      ...new Set(Object.values(TEXT_STYLE_KEYS_BY_ROW_TYPE).flat()),
      ...new Set(Object.values(EXTRA_STYLE_KEYS_BY_ROW_TYPE).flat()),
      'height',
    ]);
  else if (
    [
      'title',
      'subtitle',
      'tertiary',
      'body',
      'time',
      'price',
      'change',
      'value',
      'valueSecondary',
      'columnSecondary',
      'primaryAmount',
      'secondaryAmount',
      'description',
      'status',
      'trend',
      'message',
      'actionText',
    ].includes(field) &&
    !('text' in object)
  ) {
    object = pickFields(object, TEXT_STYLE_FIELDS);
  }
  if (NESTED_DECORATION_FIELDS[field])
    object = pickFields(object, NESTED_DECORATION_FIELDS[field]);
  if (
    [
      'textSegments',
      'primaryTextSegments',
      'secondaryTextSegments',
      'priceSegments',
      'segments',
    ].includes(field)
  )
    object = pickFields(object, ['text', 'style']);
  if (field === 'subtitleSegments')
    object = pickFields(object, [
      'text',
      'style',
      'textSegments',
      'tone',
      'separatorBefore',
    ]);
  const styleObject =
    ['style', 'imageStyle', 'container'].includes(field) ||
    TEXT_STYLE_FIELDS.some((key) => key in object);
  const next: Record<string, unknown> = {};
  for (const [key, item] of Object.entries(object)) {
    if (item === undefined || item === null) continue;
    if (
      (/color$/i.test(key) || /background$/i.test(key)) &&
      (typeof item !== 'string' || !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(item))
    )
      continue;
    if (
      /TextSegments$/.test(key) ||
      [
        'textSegments',
        'priceSegments',
        'subtitleSegments',
        'segments',
      ].includes(key)
    ) {
      if (
        !Array.isArray(item) ||
        item.some(
          (segment) => !isRecord(segment) || typeof segment.text !== 'string'
        )
      )
        continue;
    }
    if (
      styleObject &&
      PROTOCOL_NUMBER_FIELDS.has(key) &&
      typeof item !== 'number'
    )
      continue;
    if (
      key === 'token' &&
      (typeof item !== 'string' || !Object.hasOwn(TYPOGRAPHY_TOKENS, item))
    )
      continue;
    if (
      key === 'fontWeight' &&
      !['regular', 'medium', 'semibold', 'bold'].includes(item as string)
    )
      continue;
    if (
      [
        'disabled',
        'loading',
        'checked',
        'selected',
        'separator',
        'hideLeading',
        'pressDisabled',
        'draggable',
        'active',
        'autoplay',
      ].includes(key) &&
      typeof item !== 'boolean'
    )
      continue;
    if (key === 'primaryFontWeight' && item !== 'medium') continue;
    if (key === 'buttonStyle' && item !== 'tertiary') continue;
    if (key === 'hoverActionKey' && (typeof item !== 'string' || !item.trim()))
      continue;
    if (key === 'accessibilityLabel' && typeof item !== 'string') continue;
    if (
      (field === 'style' || PROTOCOL_NUMBER_FIELDS.has(key)) &&
      typeof item !== 'object' &&
      typeof item === 'number' &&
      (!Number.isFinite(item) ||
        (item < 0 && !['offsetX', 'offsetY'].includes(key)))
    )
      continue;
    if (
      field === 'style' &&
      PROTOCOL_NUMBER_FIELDS.has(key) &&
      typeof item !== 'number'
    )
      continue;
    if (
      key === 'leadingContentMaxWidthRatio' &&
      (typeof item !== 'number' || item <= 0 || item > 1)
    )
      continue;
    const normalized = sanitizeDecoration(
      item,
      field === 'style' && key === 'image' ? 'imageStyle' : key
    );
    if (normalized !== undefined) next[key] = normalized;
  }
  if (field === 'titleImage') {
    // The nested image was already normalized with title-specific geometry.
    if (!next.image) return undefined;
  }
  if (field === 'media' && (!next.source || !Array.isArray(next.probeOrder)))
    return undefined;
  if (['leading', 'visual', 'secondaryLeading'].includes(field)) {
    if (next.kind === 'image' && !next.image)
      return {
        kind: 'token',
        ...pickFields(next, [
          'fallbackIcon',
          'fallbackText',
          'fallbackTextColor',
        ]),
      };
    if (
      next.kind === 'stackedImages' &&
      (!Array.isArray(next.images) || next.images.length < 2)
    )
      return { kind: 'token' };
    if (
      next.kind === 'icon' &&
      (typeof next.name !== 'string' || !next.name.trim())
    )
      return { kind: 'token' };
    if (
      ![
        'token',
        'account',
        'wallet',
        'network',
        'image',
        'icon',
        'stackedImages',
      ].includes(next.kind as string)
    )
      return { kind: 'token' };
  }
  if (
    field === 'overlays' &&
    (!['topLeft', 'bottomRight'].includes(next.position as string) ||
      (!next.image && !next.name && !next.text))
  )
    return undefined;
  if (
    field === 'trailing' &&
    next.kind === 'icon' &&
    !('actionKey' in value) &&
    (typeof next.name !== 'string' || !next.name.trim())
  )
    return undefined;
  if (
    field === 'trailing' &&
    ![
      'value',
      'valuePair',
      'button',
      'checkbox',
      'radio',
      'switch',
      'chevron',
      'menu',
      'drag',
      'icon',
      'spinner',
      'progress',
    ].includes(next.kind as string) &&
    !('actionKey' in value)
  )
    return undefined;
  if (
    field === 'badges' &&
    (typeof next.key !== 'string' ||
      (!next.text && !next.iconName && !next.icon))
  )
    return undefined;
  if (next.kind === 'sparkline') {
    const points = Array.isArray(value.points)
      ? value.points
          .filter(
            (point) => typeof point === 'number' && Number.isFinite(point)
          )
          .slice(0, 256)
      : [];
    // Preserve the table's column identity even when its optional drawing fails.
    return points.length < 2
      ? { key: next.key, kind: 'text', primary: [] }
      : { ...next, points };
  }
  return next;
}

function sanitizeRowForProduction(row: RowModel): RowModel {
  const object = protocolObject(row, 'row');
  const fields = ROW_FIELDS[row.type];
  if (!fields) return row;
  const next = pickFields(object, fields);
  for (const [field, value] of Object.entries(next)) {
    if (
      (/color$/i.test(field) || /background$/i.test(field)) &&
      (typeof value !== 'string' ||
        !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(value))
    ) {
      delete next[field];
      continue;
    }
    if (field === 'parent' && isRecord(value))
      next[field] = sanitizeRowForProduction(value as unknown as RowModel);
    else if (field === 'children' && Array.isArray(value))
      next[field] = value.map((child) =>
        sanitizeRowForProduction(child as RowModel)
      );
    else {
      const normalized = sanitizeDecoration(value, field);
      if (normalized === undefined) delete next[field];
      else next[field] = normalized;
    }
  }
  if (isRecord(next.style))
    next.style = pickFields(next.style, [
      'container',
      ...ROW_BOX_STYLE_KEYS_BY_TYPE[row.type],
      ...(TEXT_STYLE_KEYS_BY_ROW_TYPE[row.type] ?? []),
      ...(EXTRA_STYLE_KEYS_BY_ROW_TYPE[row.type] ?? []),
    ]);
  return sameJsonValue(row, next) ? row : (next as unknown as RowModel);
}

/** Empty images and optional nulls share one meaning before strict assertions. */
function normalizeEmptyImages(value: unknown, field = ''): unknown {
  if (value === null || value === undefined || typeof value !== 'object')
    return value;
  if (Array.isArray(value)) {
    const next = value.map((item) => normalizeEmptyImages(item, field));
    return next.every((item, index) => item === value[index])
      ? value
      : next.filter((item) => item !== undefined);
  }
  const object = value as Record<string, unknown>;
  if (
    typeof object.uri === 'string' &&
    !object.uri.trim() &&
    'width' in object &&
    'height' in object
  )
    return undefined;
  let changed = false;
  const next: Record<string, unknown> = {};
  for (const [key, item] of Object.entries(object)) {
    const optional =
      object.type &&
      ![
        'key',
        'type',
        ...(REQUIRED_ROW_FIELDS[object.type as string] ?? []),
      ].includes(key);
    if (
      item === undefined ||
      (item === null && optional) ||
      (['priceSegments', 'subtitleSegments'].includes(key) &&
        Array.isArray(item) &&
        item.length === 0)
    ) {
      changed = true;
      continue;
    }
    const normalized =
      field === 'headers' ? item : normalizeEmptyImages(item, key);
    if (normalized !== undefined) next[key] = normalized;
    if (normalized !== item) changed = true;
  }
  if (
    (field === 'media' &&
      object.source !== undefined &&
      next.source === undefined) ||
    (field === 'titleImage' &&
      object.image !== undefined &&
      next.image === undefined)
  )
    return undefined;
  if (
    ['leading', 'visual', 'secondaryLeading'].includes(field) &&
    next.kind === 'image' &&
    !next.image
  )
    return { kind: 'token' };
  if (field === 'overlays' && object.image !== undefined && !next.image)
    return undefined;
  return changed ? next : value;
}

function normalizeSnapshotForProduction(
  snapshot: NativeListSnapshot
): NativeListSnapshot {
  if (!Array.isArray(snapshot.rows)) return snapshot;
  let rows = snapshot.rows.map(sanitizeRowForProduction);
  rows = rows.map((row, index) => {
    if (!row.groupId) return row;
    const previous = rows[index - 1]?.groupId === row.groupId;
    const following = rows[index + 1]?.groupId === row.groupId;
    return {
      ...row,
      groupPosition: previous
        ? following
          ? 'middle'
          : 'last'
        : following
        ? 'first'
        : 'single',
    };
  });
  const next = {
    ...snapshot,
    rows,
    ...(snapshot.emptyState
      ? {
          emptyState: sanitizeRowForProduction(
            snapshot.emptyState
          ) as NativeListSnapshot['emptyState'],
        }
      : {}),
    ...(snapshot.fixedFooter
      ? {
          fixedFooter: sanitizeRowForProduction(
            snapshot.fixedFooter
          ) as NativeListSnapshot['fixedFooter'],
        }
      : {}),
  };
  if (snapshot.theme)
    next.theme = sanitizeDecoration(
      snapshot.theme,
      'theme'
    ) as NativeListSnapshot['theme'];
  if (snapshot.listStyle)
    next.listStyle = sanitizeDecoration(
      snapshot.listStyle,
      'style'
    ) as NativeListSnapshot['listStyle'];
  if (snapshot.selection && Array.isArray(snapshot.selection.selectedKeys)) {
    const selectable = new Set(
      rows
        .filter(
          (row) =>
            !row.disabled &&
            ![
              'sectionHeader',
              'action',
              'system',
              'buttonGroup',
              'market',
              'walletGroup',
            ].includes(row.type)
        )
        .map((row) => row.key)
    );
    const selectedKeys = [
      ...new Set(
        snapshot.selection.selectedKeys.filter((key) => selectable.has(key))
      ),
    ];
    next.selection = {
      ...snapshot.selection,
      selectedKeys:
        snapshot.selection.mode === 'none'
          ? []
          : snapshot.selection.mode === 'single'
          ? selectedKeys.slice(0, 1)
          : selectedKeys,
    };
  }
  return sameJsonValue(snapshot, next) ? snapshot : next;
}

export function validateSnapshot(
  snapshot: NativeListSnapshot
): NativeListSnapshot {
  assertPlainSerializable(snapshot, 'snapshot', new Set<object>(), false);
  protocolObject(snapshot, 'snapshot');
  const normalized = normalizeEmptyImages(snapshot) as NativeListSnapshot;
  const canonical = isDevelopment()
    ? normalized
    : normalizeSnapshotForProduction(normalized);
  assertPlainSerializable(canonical, 'snapshot', new Set<object>());
  if (isDevelopment()) assertSnapshotContract(canonical);
  assertSnapshotProtocol(canonical);
  return normalizeSnapshotStyles(canonical);
}

const ownedNativeSnapshots = new WeakMap<
  NativeListSnapshot,
  ReadonlyMap<string, number>
>();

function capturePlainData<T>(value: T, seen = new Map<object, object>()): T {
  if (value === null || typeof value !== 'object') return value;
  const previous = seen.get(value);
  if (previous) return previous as T;
  const copy: Record<string, unknown> | unknown[] = Array.isArray(value)
    ? new Array(value.length)
    : Object.create(Object.getPrototypeOf(value));
  seen.set(value, copy);
  const captureKey = (key: string) => {
    Object.defineProperty(copy, key, {
      value: capturePlainData((value as Record<string, unknown>)[key], seen),
      enumerable: true,
      writable: true,
      configurable: true,
    });
  };
  if (Array.isArray(value)) {
    for (let index = 0; index < value.length; index += 1) {
      if (index in value) captureKey(String(index));
    }
    Object.keys(value).forEach((key) => {
      const index = Number(key);
      if (
        Number.isInteger(index) &&
        index >= 0 &&
        index < value.length &&
        String(index) === key
      )
        return;
      captureKey(key);
    });
  } else {
    Object.keys(value).forEach(captureKey);
  }
  return copy as T;
}

function deepFreeze<T>(value: T, seen = new WeakSet<object>()): T {
  if (value === null || typeof value !== 'object' || seen.has(value))
    return value;
  seen.add(value);
  Object.values(value).forEach((item) => deepFreeze(item, seen));
  return Object.freeze(value);
}

/** Capture caller data once before validation, including accessor values. */
export function captureNativeListInput<T>(value: T): T {
  return capturePlainData(value);
}

/** Private native-wrapper ownership; exported validators keep their public identity. */
export function ownValidatedNativeSnapshot(
  snapshot: NativeListSnapshot
): NativeListSnapshot {
  const owned = deepFreeze(snapshot);
  ownedNativeSnapshots.set(
    owned,
    new Map(owned.rows.map((row, index) => [row.key, index]))
  );
  return owned;
}

/**
 * Resolves typography tokens to numbers. Returns the original snapshot when
 * nothing needs resolving, so the common no-style path keeps object identity.
 */
function normalizeSnapshotStyles(
  snapshot: NativeListSnapshot
): NativeListSnapshot {
  let changed = false;
  const rows = snapshot.rows.map((row) => {
    const next = normalizeRowStyles(row);
    if (next !== row) changed = true;
    return next;
  });
  const emptyState = snapshot.emptyState
    ? normalizeRowStyles(snapshot.emptyState)
    : snapshot.emptyState;
  const fixedFooter = snapshot.fixedFooter
    ? normalizeRowStyles(snapshot.fixedFooter)
    : snapshot.fixedFooter;
  if (
    !changed &&
    emptyState === snapshot.emptyState &&
    fixedFooter === snapshot.fixedFooter
  ) {
    return snapshot;
  }
  return {
    ...snapshot,
    rows,
    ...(emptyState !== undefined ? { emptyState } : {}),
    ...(fixedFooter !== undefined ? { fixedFooter } : {}),
  };
}

type PatchValues<Patch = RowPatch> = Patch extends RowPatch
  ? Omit<Patch, 'changes'> & {
      changes: {
        [Key in keyof Patch['changes']]: Exclude<Patch['changes'][Key], null>;
      };
    }
  : never;

function assertPatchChanges(patch: PatchValues, index: number): void {
  const path = `patches[${index}].changes`;
  const patchedStyle = (patch.changes as { style?: unknown }).style;
  if (patchedStyle !== undefined) {
    assertRowStyle({ type: patch.type, style: patchedStyle } as RowModel, path);
  }
  // OneKey patch: partial balance updates retain a valid, current accessibility label.
  if ('accessibilityLabel' in patch.changes) {
    assertText(patch.changes.accessibilityLabel, `${path}.accessibilityLabel`);
  }
  // OneKey patch: partial updates may refer to an existing height but still require a valid policy.
  if (
    'heightRounding' in patch.changes &&
    patch.changes.heightRounding !== undefined &&
    !['floor', 'nearest'].includes(patch.changes.heightRounding)
  )
    fail(`${path}.heightRounding`, 'must be floor or nearest');
  if (
    patch.changes.revision !== undefined &&
    (!Number.isSafeInteger(patch.changes.revision) ||
      patch.changes.revision < 0)
  ) {
    fail(`${path}.revision`, 'must be a non-negative safe integer');
  }

  switch (patch.type) {
    case 'identity':
      if (
        patch.changes.contentHeight !== undefined &&
        patch.changes.contentHeight !== 'auto'
      ) {
        fail(`${path}.contentHeight`, 'must be auto');
      }
      if (
        patch.changes.subtitleLines !== undefined &&
        ![0, 1, 2].includes(patch.changes.subtitleLines)
      ) {
        fail(`${path}.subtitleLines`, 'must be 0, 1, or 2');
      }
      if (patch.changes.longPressActionKey !== undefined) {
        assertKey(
          patch.changes.longPressActionKey,
          `${path}.longPressActionKey`
        );
      }
      assertText(patch.changes.title, `${path}.title`);
      if ((patch.changes.titleSuffixes?.length ?? 0) > 3)
        fail(`${path}.titleSuffixes`, 'supports at most 3 suffixes');
      patch.changes.titleSuffixes?.forEach((suffix, index) => {
        assertText(suffix.text, `${path}.titleSuffixes[${index}].text`);
        if (!['secondary', 'disabled'].includes(suffix.tone))
          fail(
            `${path}.titleSuffixes[${index}].tone`,
            'must be secondary or disabled'
          );
        if (
          suffix.maxWidth !== undefined &&
          (!Number.isFinite(suffix.maxWidth) ||
            suffix.maxWidth < 0 ||
            suffix.maxWidth > 4096)
        )
          fail(
            `${path}.titleSuffixes[${index}].maxWidth`,
            'must be finite within 0...4096'
          );
      });
      assertIdentityExtras(patch.changes, path);
      assertText(patch.changes.subtitle, `${path}.subtitle`);
      assertText(patch.changes.tertiary, `${path}.tertiary`);
      if (
        patch.changes.tertiaryTone !== undefined &&
        !['secondary', 'info'].includes(patch.changes.tertiaryTone)
      ) {
        fail(`${path}.tertiaryTone`, 'must be secondary or info');
      }
      assertLeadingVisual(patch.changes.leading, `${path}.leading`);
      assertTrailingAccessories(
        patch.changes.leadingAction ? [patch.changes.leadingAction] : undefined,
        `${path}.leadingAction`
      );
      if ((patch.changes.badges?.length ?? 0) > MAX_BADGES) {
        fail(`${path}.badges`, `supports at most ${MAX_BADGES} badges`);
      }
      if ((patch.changes.trailing?.length ?? 0) > MAX_TRAILING_ACCESSORIES) {
        fail(
          `${path}.trailing`,
          `supports at most ${MAX_TRAILING_ACCESSORIES} accessories`
        );
      }
      assertTrailingAccessories(patch.changes.trailing, `${path}.trailing`);
      break;
    case 'rail':
      assertText(patch.changes.title, `${path}.title`);
      assertLeadingVisual(patch.changes.visual, `${path}.visual`);
      break;
    case 'activity':
      assertActivity(patch.changes, path);
      assertText(patch.changes.title, `${path}.title`);
      assertText(patch.changes.description, `${path}.description`);
      assertText(patch.changes.status, `${path}.status`);
      assertText(patch.changes.primaryAmount, `${path}.primaryAmount`);
      assertText(patch.changes.secondaryAmount, `${path}.secondaryAmount`);
      assertLeadingVisual(patch.changes.leading, `${path}.leading`);
      assertLeadingVisual(
        patch.changes.secondaryLeading,
        `${path}.secondaryLeading`
      );
      if ((patch.changes.footerActions?.length ?? 0) > MAX_FOOTER_ACTIONS) {
        fail(
          `${path}.footerActions`,
          `supports at most ${MAX_FOOTER_ACTIONS} actions`
        );
      }
      break;
    case 'message':
      if (
        patch.changes.presentation !== undefined &&
        patch.changes.presentation !== 'inlineWarning'
      ) {
        fail(`${path}.presentation`, 'must be inlineWarning');
      }
      assertText(patch.changes.title, `${path}.title`);
      assertText(patch.changes.body, `${path}.body`);
      assertText(patch.changes.time, `${path}.time`);
      if (
        patch.changes.bodyLines !== undefined &&
        ![0, 1, 2, 3].includes(patch.changes.bodyLines)
      ) {
        fail(`${path}.bodyLines`, 'must be 0, 1, 2, or 3');
      }
      assertImage(patch.changes.thumbnail, `${path}.thumbnail`);
      break;
    case 'dataRow':
      if (
        patch.changes.columns &&
        (patch.changes.columns.length < 2 || patch.changes.columns.length > 4)
      ) {
        fail(`${path}.columns`, 'must contain 2...4 columns');
      }
      patch.changes.columns?.forEach((column, columnIndex) => {
        assertKey(column.key, `${path}.columns[${columnIndex}].key`);
        assertText(column.text, `${path}.columns[${columnIndex}].text`);
        assertIdentityExtras(column, `${path}.columns[${columnIndex}]`);
        for (const field of [
          'textSegments',
          'secondaryTextSegments',
        ] as const) {
          column[field]?.forEach((segment, segmentIndex) => {
            assertText(
              segment.text,
              `${path}.columns[${columnIndex}].${field}[${segmentIndex}].text`
            );
            if (segment.style !== undefined && segment.style !== 'subscript')
              fail(
                `${path}.columns[${columnIndex}].${field}[${segmentIndex}].style`,
                'must be subscript'
              );
          });
        }
        assertText(
          column.secondaryText,
          `${path}.columns[${columnIndex}].secondaryText`
        );
        assertTextTone(
          column.secondaryTone,
          `${path}.columns[${columnIndex}].secondaryTone`
        );
      });
      if ((patch.changes.badges?.length ?? 0) > MAX_BADGES) {
        fail(`${path}.badges`, `supports at most ${MAX_BADGES} badges`);
      }
      if ((patch.changes.trailing?.length ?? 0) > MAX_TRAILING_ACCESSORIES) {
        fail(
          `${path}.trailing`,
          `supports at most ${MAX_TRAILING_ACCESSORIES} accessories`
        );
      }
      assertTrailingAccessories(patch.changes.trailing, `${path}.trailing`);
      break;
    case 'market':
      assertMarketRow(
        {
          type: 'market',
          key: patch.key,
          variant: 'token',
          leading: { kind: 'icon', name: 'placeholder' },
          title: '',
          price: '',
          change: { text: '', tone: 'neutral' },
          ...patch.changes,
        } as MarketRow,
        path
      );
      break;
    case 'mediaTile':
      assertMediaPreview(patch.changes.media, `${path}.media`);
      assertImage(patch.changes.image, `${path}.image`);
      if (
        patch.changes.imageState !== undefined &&
        !['empty', 'error'].includes(patch.changes.imageState)
      ) {
        fail(`${path}.imageState`, 'must be empty or error when provided');
      }
      assertImage(patch.changes.networkImage, `${path}.networkImage`);
      assertText(patch.changes.title, `${path}.title`);
      assertText(patch.changes.subtitle, `${path}.subtitle`);
      break;
    case 'metricCard':
      assertText(patch.changes.title, `${path}.title`);
      assertText(patch.changes.value, `${path}.value`);
      assertText(patch.changes.subtitle, `${path}.subtitle`);
      assertText(patch.changes.trend, `${path}.trend`);
      assertLeadingVisual(patch.changes.visual, `${path}.visual`);
      if (
        patch.changes.variant !== undefined &&
        !['standard', 'activity', 'performance'].includes(patch.changes.variant)
      ) {
        fail(`${path}.variant`, 'must be standard, activity, or performance');
      }
      if (
        patch.changes.metrics &&
        (patch.changes.metrics.length < 2 || patch.changes.metrics.length > 5)
      ) {
        fail(`${path}.metrics`, 'must contain 2...5 metrics');
      }
      patch.changes.metrics?.forEach((metric, metricIndex) => {
        assertKey(metric.key, `${path}.metrics[${metricIndex}].key`);
        assertText(metric.label, `${path}.metrics[${metricIndex}].label`);
        assertText(metric.value, `${path}.metrics[${metricIndex}].value`);
        assertTextTone(metric.tone, `${path}.metrics[${metricIndex}].tone`);
        assertLeadingVisual(
          metric.visual,
          `${path}.metrics[${metricIndex}].visual`
        );
      });
      if (
        patch.changes.progress !== undefined &&
        (patch.changes.progress < 0 || patch.changes.progress > 1)
      ) {
        fail(`${path}.progress`, 'must be within 0...1');
      }
      break;
    case 'sectionHeader':
      if (
        patch.changes.titleLoading !== undefined &&
        typeof patch.changes.titleLoading !== 'boolean'
      )
        fail(`${path}.titleLoading`, 'must be boolean');
      assertSectionHeaderVariant(patch.changes.variant, `${path}.variant`);
      assertText(patch.changes.title, `${path}.title`);
      assertText(patch.changes.subtitle, `${path}.subtitle`);
      assertText(patch.changes.value, `${path}.value`);
      if (patch.changes.leadingBadge) {
        assertKey(patch.changes.leadingBadge.key, `${path}.leadingBadge.key`);
        assertText(
          patch.changes.leadingBadge.text,
          `${path}.leadingBadge.text`
        );
      }
      assertTextHelp(patch.changes.valueHelp, `${path}.valueHelp`);
      if (patch.changes.valueActionKey !== undefined) {
        assertKey(patch.changes.valueActionKey, `${path}.valueActionKey`);
      }
      assertTrailingAccessories(
        patch.changes.titleIcon ? [patch.changes.titleIcon] : undefined,
        `${path}.titleIcon`
      );
      assertTrailingAccessories(
        patch.changes.valueIcon ? [patch.changes.valueIcon] : undefined,
        `${path}.valueIcon`
      );
      break;
    case 'action':
      assertText(patch.changes.title, `${path}.title`);
      if ((patch.changes.trailing?.length ?? 0) > MAX_TRAILING_ACCESSORIES) {
        fail(
          `${path}.trailing`,
          `supports at most ${MAX_TRAILING_ACCESSORIES} accessories`
        );
      }
      assertTrailingAccessories(patch.changes.trailing, `${path}.trailing`);
      break;
    case 'buttonGroup':
      if (patch.changes.buttons !== undefined)
        assertButtonGroup({ buttons: patch.changes.buttons }, path);
      break;
    case 'system':
      assertText(patch.changes.message, `${path}.message`);
      break;
    default:
      fail(`patches[${index}].type`, 'is not supported');
  }
}

function normalizePatch(patch: RowPatch, index: number): RowPatch | undefined {
  const path = `patches[${index}]`;
  protocolObject(patch, path);
  protocolKey(patch.key, `${path}.key`);
  if (isDevelopment()) assertKey(patch.key, `${path}.key`);
  assertRowType(patch.type, path);
  const raw = protocolObject(patch.changes, `${path}.changes`);
  assertPlainSerializable(raw, `${path}.changes`, new Set<object>(), false);
  const fields = PATCH_FIELDS[patch.type];
  const changes: Record<string, unknown> = {};
  for (const [field, value] of Object.entries(raw)) {
    if (value === undefined) continue;
    if (!fields?.includes(field)) {
      if (isDevelopment())
        fail(`${path}.changes.${field}`, 'unsupported patch field');
      continue;
    }
    if (value === null) {
      if (
        REQUIRED_ROW_FIELDS[patch.type].includes(field) ||
        (patch.type === 'system' && field === 'message')
      ) {
        // The concrete system variant is checked after merging with its baseline.
        if (patch.type !== 'system')
          fail(`${path}.changes.${field}`, 'required field cannot be cleared');
      }
      changes[field] = null;
      continue;
    }
    if (
      ['priceSegments', 'subtitleSegments'].includes(field) &&
      Array.isArray(value) &&
      value.length === 0
    ) {
      changes[field] = null;
      continue;
    }
    const normalized = normalizeEmptyImages(value, field);
    if (normalized === undefined) {
      if (!REQUIRED_ROW_FIELDS[patch.type].includes(field))
        changes[field] = null;
      continue;
    }
    const canonical = isDevelopment()
      ? normalized
      : sanitizeDecoration(normalized, field);
    if (canonical !== undefined) changes[field] = canonical;
    else if (field === 'titleImage') changes[field] = null;
  }
  if (
    patch.type === 'market' &&
    Array.isArray(changes.priceSegments) &&
    changes.priceSegments.length > 0 &&
    (typeof changes.price !== 'string' || !changes.price.trim())
  )
    fail(`${path}.changes.price`, 'is required when priceSegments is updated');
  if (!isDevelopment()) {
    const sanitized = sanitizeRowForProduction({
      type: patch.type,
      ...changes,
    } as unknown as RowModel) as unknown as Record<string, unknown>;
    for (const [field, value] of Object.entries(changes)) {
      if (value === null) continue;
      if (sanitized[field] === undefined) delete changes[field];
      else changes[field] = sanitized[field];
    }
  }
  const values = Object.fromEntries(
    Object.entries(changes).filter(([, value]) => value !== null)
  );
  assertPlainSerializable(values, `${path}.changes`, new Set<object>());
  assertProtocolFields(values, `${path}.changes`);
  if (isDevelopment())
    assertPatchChanges({ ...patch, changes: values } as PatchValues, index);
  const normalizedPatch = {
    type: patch.type,
    key: patch.key,
    changes,
  } as RowPatch;
  return Object.keys(changes).length > 0
    ? normalizePatchStyles([normalizedPatch])[0]
    : undefined;
}

function reportRejectedPatch(index: number): void {
  console.warn(`NativeList rejected invalid patch unit at patches[${index}]`);
}

export function validatePatches(
  patches: readonly RowPatch[]
): readonly RowPatch[] {
  if (!Array.isArray(patches)) fail('patches', 'must be an array');
  const units = new Map<string, { patch: RowPatch; index: number }>();
  const rejectedKeys = new Set<string>();
  const reject = (key: string, index: number, error: unknown) => {
    if (isDevelopment()) throw error;
    rejectedKeys.add(key);
    reportRejectedPatch(index);
  };
  patches.forEach((input, index) => {
    const key =
      isRecord(input) && typeof input.key === 'string' ? input.key : '';
    try {
      protocolObject(input, `patches[${index}]`);
      protocolKey(input.key, `patches[${index}].key`);
      assertRowType(input.type, `patches[${index}]`);
      const changes = capturePlainData(
        protocolObject(input.changes, `patches[${index}].changes`)
      );
      assertPlainSerializable(
        changes,
        `patches[${index}].changes`,
        new Set<object>(),
        false
      );
      const previous = units.get(key);
      if (previous && previous.patch.type !== input.type)
        fail(`patches[${index}].type`, 'conflicting row types');
      const defined = Object.fromEntries(
        Object.entries(changes).filter(([, value]) => value !== undefined)
      );
      units.set(key, {
        index,
        patch: {
          key,
          type: input.type,
          changes: { ...previous?.patch.changes, ...defined },
        } as RowPatch,
      });
    } catch (error) {
      reject(key, index, error);
    }
  });
  const accepted: RowPatch[] = [];
  for (const [key, unit] of units) {
    if (rejectedKeys.has(key)) continue;
    try {
      const patch = normalizePatch(unit.patch, unit.index);
      if (patch) accepted.push(patch);
    } catch (error) {
      reject(key, unit.index, error);
    }
  }
  return accepted;
}

/** Patches reach the native side without passing through a snapshot. */
function normalizePatchStyles(
  patches: readonly RowPatch[]
): readonly RowPatch[] {
  let changed = false;
  const next = patches.map((patch) => {
    const style = (patch.changes as { style?: Record<string, unknown> }).style;
    if (!style) return patch;
    const resolved = resolveStyleTokens(style, patch.type);
    if (resolved === style) return patch;
    changed = true;
    return {
      ...patch,
      changes: { ...patch.changes, style: resolved },
    } as RowPatch;
  });
  return changed ? next : patches;
}

const EMPTY_ROW_PATCHES: readonly RowPatch[] = Object.freeze([]);

function sameJsonValue(left: unknown, right: unknown): boolean {
  if (left === right) return true;
  if (left === null || right === null) return false;
  if (typeof left !== 'object' || typeof right !== 'object') return false;
  if (Array.isArray(left) || Array.isArray(right)) {
    return (
      Array.isArray(left) &&
      Array.isArray(right) &&
      left.length === right.length &&
      left.every((value, index) => sameJsonValue(value, right[index]))
    );
  }
  const leftObject = left as Record<string, unknown>;
  const rightObject = right as Record<string, unknown>;
  const leftKeys = Object.keys(leftObject).filter(
    (key) => leftObject[key] !== undefined
  );
  const rightKeys = Object.keys(rightObject).filter(
    (key) => rightObject[key] !== undefined
  );
  return (
    leftKeys.length === rightKeys.length &&
    leftKeys.every((key) => sameJsonValue(leftObject[key], rightObject[key]))
  );
}

function sameOptionalPresence(left: unknown, right: unknown): boolean {
  return (left === undefined) === (right === undefined);
}

/** Plan value-only patches; callers own valid immutable snapshots and commit state. */
export function planNativeListPatches(
  previous: NativeListSnapshot,
  next: NativeListSnapshot
): readonly RowPatch[] | undefined {
  if (previous === next) return EMPTY_ROW_PATCHES;
  const { rows: previousRows, ...previousMetadata } = previous;
  const { rows: nextRows, ...nextMetadata } = next;
  if (
    previousRows.length !== nextRows.length ||
    !sameJsonValue(previousMetadata, nextMetadata)
  )
    return undefined;
  if (previousRows === nextRows) return EMPTY_ROW_PATCHES;

  const patches: RowPatch[] = [];
  for (let index = 0; index < nextRows.length; index += 1) {
    const oldRow = previousRows[index];
    const row = nextRows[index];
    if (oldRow.key !== row.key || oldRow.type !== row.type) return undefined;
    if (oldRow === row) continue;

    if (oldRow.type === 'identity' && row.type === 'identity') {
      const {
        subtitle: oldSubtitle,
        subtitleSegments: oldSubtitleSegments,
        trailing: oldTrailing,
        ...oldMetadata
      } = oldRow;
      const { subtitle, subtitleSegments, trailing, ...metadata } = row;
      if (
        !sameOptionalPresence(oldSubtitle, subtitle) ||
        !sameOptionalPresence(oldSubtitleSegments, subtitleSegments) ||
        !sameOptionalPresence(oldTrailing, trailing) ||
        !sameJsonValue(oldMetadata, metadata)
      )
        return undefined;
      if (
        !sameJsonValue(oldSubtitle, subtitle) ||
        !sameJsonValue(oldSubtitleSegments, subtitleSegments) ||
        !sameJsonValue(oldTrailing, trailing)
      ) {
        patches.push({
          type: 'identity',
          key: row.key,
          changes: { subtitle, subtitleSegments, trailing },
        });
      }
    } else if (oldRow.type === 'dataRow' && row.type === 'dataRow') {
      const {
        columns: oldColumns,
        trailing: oldTrailing,
        ...oldMetadata
      } = oldRow;
      const { columns, trailing, ...metadata } = row;
      if (
        !sameOptionalPresence(oldTrailing, trailing) ||
        !sameJsonValue(oldMetadata, metadata) ||
        !sameJsonValue(oldColumns[0], columns[0])
      )
        return undefined;
      if (
        !sameJsonValue(oldColumns.slice(1), columns.slice(1)) ||
        !sameJsonValue(oldTrailing, trailing)
      ) {
        patches.push({
          type: 'dataRow',
          key: row.key,
          changes: { columns, trailing },
        });
      }
    } else if (!sameJsonValue(oldRow, row)) {
      return undefined;
    }
  }
  return patches.length === 0 ? EMPTY_ROW_PATCHES : patches;
}

function mergeAndValidateSingleRow(
  previous: RowModel,
  patch: RowPatch,
  index: number
): { row: RowModel; patch: RowPatch } {
  if (previous.key !== patch.key)
    fail(`patches[${index}].key`, 'does not match row identity');
  if (previous.type !== patch.type)
    fail(`patches[${index}].type`, 'does not match row type');
  const changes = { ...patch.changes } as Record<string, unknown>;
  if (previous.type === 'market') {
    if (
      typeof changes.price === 'string' &&
      !('priceSegments' in changes) &&
      previous.priceSegments !== undefined
    )
      changes.priceSegments = null;
    if (
      (previous.webTable !== undefined || 'webTable' in changes) &&
      ['price', 'priceSegments', 'change'].some((field) => field in changes) &&
      changes.webTable === undefined
    )
      fail(
        `patches[${index}].changes.webTable`,
        'table quote changes require visible table cells'
      );
  }
  if (previous.type === 'identity' || previous.type === 'market') {
    if (
      typeof changes.subtitle === 'string' &&
      !('subtitleSegments' in changes) &&
      previous.subtitleSegments !== undefined
    )
      changes.subtitleSegments = null;
    if (
      Array.isArray(changes.subtitleSegments) &&
      changes.subtitleSegments.length > 0 &&
      !('subtitle' in changes) &&
      previous.subtitle !== undefined
    )
      changes.subtitle = null;
  }
  const merged = { ...previous } as unknown as Record<string, unknown>;
  for (const [field, value] of Object.entries(changes)) {
    if (value === null) delete merged[field];
    else if (value !== undefined) merged[field] = value;
  }
  const next = normalizeRowStyles(merged as unknown as RowModel);
  assertPlainSerializable(next, `rows[${index}]`, new Set<object>());
  if (isDevelopment()) assertRow(next, index);
  assertRowProtocol(next, `rows[${index}]`);
  return {
    row: next,
    patch: { type: patch.type, key: patch.key, changes } as RowPatch,
  };
}

/** One canonical batch owns both accepted rows and the native wire payload. */
export function applyValidatedRowPatches(
  snapshot: NativeListSnapshot,
  patches: readonly RowPatch[]
): {
  patches: readonly RowPatch[];
  snapshot: NativeListSnapshot;
  json: string;
} {
  if (!Array.isArray(patches)) fail('patches', 'must be an array');
  const owned = ownedNativeSnapshots.has(snapshot);
  const rowIndexes =
    ownedNativeSnapshots.get(snapshot) ??
    validatedRowIndexes.get(snapshot) ??
    new Map(snapshot.rows.map((row, index) => [row.key, index]));
  validatedRowIndexes.set(snapshot, rowIndexes);
  const units = new Map<
    string,
    { index: number; changes: Record<string, unknown>; type: RowPatch['type'] }
  >();
  const rejected = new Set<string>();
  const rejectedGroups = new Set<string>();
  const reject = (key: string, index: number, error: unknown) => {
    if (isDevelopment()) throw error;
    rejected.add(key);
    const rowIndex = rowIndexes.get(key);
    const group =
      rowIndex === undefined ? undefined : snapshot.rows[rowIndex].groupId;
    if (group) rejectedGroups.add(group);
    reportRejectedPatch(index);
  };
  patches.forEach((input, index) => {
    const key =
      isRecord(input) && typeof input.key === 'string' ? input.key : '';
    try {
      protocolObject(input, `patches[${index}]`);
      protocolKey(input.key, `patches[${index}].key`);
      const rowIndex = rowIndexes.get(input.key);
      if (rowIndex === undefined)
        fail(`patches[${index}].key`, 'unknown row identity');
      if (snapshot.rows[rowIndex].type !== input.type)
        fail(`patches[${index}].type`, 'does not match row type');
      const changes = protocolObject(
        input.changes,
        `patches[${index}].changes`
      );
      const captured = capturePlainData(changes);
      assertPlainSerializable(
        captured,
        `patches[${index}].changes`,
        new Set<object>(),
        false
      );
      const previous = units.get(input.key);
      // Undefined is omitted at every merge, so it cannot overwrite an earlier change.
      const defined = Object.fromEntries(
        Object.entries(captured).filter(([, value]) => value !== undefined)
      );
      units.set(input.key, {
        index,
        type: input.type,
        changes: { ...previous?.changes, ...defined },
      });
    } catch (error) {
      reject(key, index, error);
    }
  });
  const pending = new Map<string, { row: RowModel; patch: RowPatch }>();
  for (const [key, unit] of units) {
    if (rejected.has(key)) continue;
    try {
      const patch = normalizePatch(
        { key, type: unit.type, changes: unit.changes } as RowPatch,
        unit.index
      );
      if (!patch) continue;
      const merged = mergeAndValidateSingleRow(
        snapshot.rows[rowIndexes.get(key)!],
        patch,
        rowIndexes.get(key)!
      );
      if (merged.row.disabled && snapshot.selection?.selectedKeys.includes(key))
        fail(`patches[${unit.index}]`, 'cannot disable a selected row');
      pending.set(key, merged);
    } catch (error) {
      reject(key, unit.index, error);
    }
  }
  const accepted: RowPatch[] = [];
  let rows: RowModel[] | undefined;
  for (const [key, value] of pending) {
    const index = rowIndexes.get(key)!;
    const group = snapshot.rows[index].groupId;
    if (rejected.has(key) || (group && rejectedGroups.has(group))) continue;
    if (!rows) rows = snapshot.rows.slice();
    rows[index] = owned ? deepFreeze(value.row) : value.row;
    accepted.push(owned ? deepFreeze(value.patch) : value.patch);
  }
  if (!rows) return { patches: EMPTY_ROW_PATCHES, snapshot, json: '[]' };
  const next = owned
    ? Object.freeze({ ...snapshot, rows: Object.freeze(rows) })
    : { ...snapshot, rows };
  if (owned) ownedNativeSnapshots.set(next, rowIndexes);
  else validatedRowIndexes.set(next, rowIndexes);
  return { patches: accepted, snapshot: next, json: JSON.stringify(accepted) };
}

const validatedRowIndexes = new WeakMap<
  NativeListSnapshot,
  ReadonlyMap<string, number>
>();

export function applyOwnedNativePatches(
  snapshot: NativeListSnapshot,
  patches: readonly RowPatch[]
) {
  const owned = ownedNativeSnapshots.has(snapshot)
    ? snapshot
    : ownValidatedNativeSnapshot(validateSnapshot(capturePlainData(snapshot)));
  return applyValidatedRowPatches(owned, patches);
}

export function applyRowPatches(
  snapshot: NativeListSnapshot,
  patches: readonly RowPatch[]
): NativeListSnapshot {
  const baseline = ownedNativeSnapshots.has(snapshot)
    ? snapshot
    : validateSnapshot(snapshot);
  return applyValidatedRowPatches(baseline, patches).snapshot;
}

export function serializeSnapshot(snapshot: NativeListSnapshot): string {
  return JSON.stringify(validateSnapshot(snapshot));
}

export function serializePatches(patches: readonly RowPatch[]): string {
  return JSON.stringify(validatePatches(patches));
}
