import { ROW_BOX_STYLE_KEYS_BY_TYPE } from './models';
import type {
  ActivityRow,
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
  seen: Set<object>
): void {
  if (value === null || value === undefined) return;
  const valueType = typeof value;
  if (valueType === 'string' || valueType === 'boolean') return;
  if (valueType === 'number') {
    if (!Number.isFinite(value)) fail(path, 'numbers must be finite');
    return;
  }
  if (valueType !== 'object')
    fail(path, 'must contain only plain serializable data');

  const objectValue = value as object;
  if (seen.has(objectValue)) fail(path, 'must not contain circular references');
  seen.add(objectValue);
  if (Array.isArray(value)) {
    value.forEach((item, index) =>
      assertPlainSerializable(item, `${path}[${index}]`, seen)
    );
  } else {
    const prototype = Object.getPrototypeOf(value);
    if (prototype !== Object.prototype && prototype !== null) {
      fail(path, 'must contain plain objects only');
    }
    Object.entries(value as Record<string, unknown>).forEach(([key, item]) =>
      assertPlainSerializable(item, `${path}.${key}`, seen)
    );
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
    if (ratio !== undefined && (!Number.isFinite(ratio) || ratio <= 0 || ratio > 1))
      fail(`${path}.style.leadingContentMaxWidthRatio`, 'must be within (0, 1]');
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
          'kind', 'label', 'actionKey', 'disabled', 'testID',
          'accessibilityLabel', 'textColor', 'backgroundColor',
          'pressedBackgroundColor', 'hoverBackgroundColor', 'focusRingColor',
        ],
        accessoryPath,
        'button key'
      );
      if (typeof accessory.label !== 'string' || !accessory.label.trim())
        fail(`${accessoryPath}.label`, 'must be non-blank');
      assertKey(accessory.actionKey, `${accessoryPath}.actionKey`);
      if (accessory.disabled !== undefined && typeof accessory.disabled !== 'boolean')
        fail(`${accessoryPath}.disabled`, 'must be boolean');
      for (const field of ['textColor', 'backgroundColor', 'pressedBackgroundColor',
        'hoverBackgroundColor', 'focusRingColor'] as const) {
        const value = accessory[field];
        if (value !== undefined &&
          (typeof value !== 'string' || !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(value)))
          fail(`${accessoryPath}.${field}`, 'must be #RRGGBB or #RRGGBBAA');
      }
    }
    if (accessory.kind === 'icon' && accessory.buttonStyle !== undefined &&
      accessory.buttonStyle !== 'tertiary')
      fail(`${accessoryPath}.buttonStyle`, 'must be tertiary');
    if (
      accessory.kind === 'progress' &&
      (accessory.value < 0 || accessory.value > 1)
    ) {
      fail(`${accessoryPath}.value`, 'must be within 0...1');
    }
    if (accessory.kind === 'valuePair') {
      assertTextTone(accessory.primaryTone, `${accessoryPath}.primaryTone`);
      assertTextTone(accessory.secondaryTone, `${accessoryPath}.secondaryTone`);
      for (const field of ['primaryTextSegments', 'secondaryTextSegments'] as const) {
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
          if (typeof entry.text !== 'string' || entry.text.length > MAX_TEXT_LENGTH)
            fail(`${path}.text`, `must be a string no longer than ${MAX_TEXT_LENGTH} characters`);
          if (entry.style !== undefined && entry.style !== 'subscript')
            fail(`${path}.style`, 'must be subscript when provided');
          textLength += entry.text.length;
        });
        if (textLength > MAX_TEXT_LENGTH)
          fail(segmentPath, `total text must be at most ${MAX_TEXT_LENGTH} characters`);
      }
    }
  });
}

function assertIdentityExtras(
  row: Pick<IdentityRow, 'titleIcon' | 'secondaryTitleIcon' | 'trailing' | 'style'>,
  path: string
): void {
  for (const field of ['titleIcon', 'secondaryTitleIcon'] as const) {
    const titleIcon = row[field];
    if (titleIcon === undefined) continue;
    const iconPath = `${path}.${field}`;
    const icon = protocolObject(titleIcon, iconPath);
    assertUnknownKeys(icon, ['name', 'tintColor', 'accessibilityLabel', 'hoverActionKey'],
      iconPath, 'title icon key');
    if (typeof titleIcon.name !== 'string' || !titleIcon.name.trim())
      fail(`${iconPath}.name`, 'must be non-blank');
    if (titleIcon.tintColor !== undefined &&
      (typeof titleIcon.tintColor !== 'string' ||
        !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(titleIcon.tintColor)))
      fail(`${iconPath}.tintColor`, 'must be #RRGGBB or #RRGGBBAA');
    if (titleIcon.accessibilityLabel !== undefined &&
      typeof titleIcon.accessibilityLabel !== 'string')
      fail(`${iconPath}.accessibilityLabel`, 'must be a string');
    if (titleIcon.hoverActionKey !== undefined)
      assertKey(titleIcon.hoverActionKey, `${iconPath}.hoverActionKey`);
  }
  const ratio = row.style?.leadingContentMaxWidthRatio;
  if (ratio !== undefined && (typeof ratio !== 'number' ||
    !Number.isFinite(ratio) || ratio <= 0 || ratio > 1))
    fail(`${path}.style.leadingContentMaxWidthRatio`, 'must be within (0, 1]');
  assertTrailingAccessories(row.trailing, `${path}.trailing`);
}

function assertImage(image: ImageSource | undefined, path: string): void {
  if (!image) return;
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
    if (visual.fallbackIcon?.size !== undefined &&
      (!Number.isFinite(visual.fallbackIcon.size) || visual.fallbackIcon.size <= 0 ||
        visual.fallbackIcon.size > 40))
      fail(`${path}.fallbackIcon.size`, 'must be within (0, 40]');
    if (visual.fallbackIcon?.backgroundColor !== undefined &&
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(visual.fallbackIcon.backgroundColor))
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
      if (overlay.fallbackIcon !== undefined &&
        (typeof overlay.fallbackIcon.name !== 'string' || !overlay.fallbackIcon.name.trim()))
        fail(`${path}.overlays[${index}].fallbackIcon.name`, 'must be non-blank');
      if (overlay.fallbackIcon?.tintColor !== undefined &&
        !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(overlay.fallbackIcon.tintColor))
        fail(`${path}.overlays[${index}].fallbackIcon.tintColor`, 'must be a hex color');
      assertText(overlay.text, `${path}.overlays[${index}].text`);
      if (overlay.fontWeight !== undefined &&
        !['regular', 'medium', 'semibold', 'bold'].includes(overlay.fontWeight))
        fail(`${path}.overlays[${index}].fontWeight`,
          'must be regular, medium, semibold, or bold');
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
      assertText(row.title, `${path}.title`);
      assertText(row.body, `${path}.body`);
      if (![1, 2, 3].includes(row.bodyLines ?? 3)) {
        fail(`${path}.bodyLines`, 'must be 1, 2, or 3');
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
  if (snapshot.theme?.rowHoverBackground !== undefined &&
    (typeof snapshot.theme.rowHoverBackground !== 'string' ||
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(snapshot.theme.rowHoverBackground)))
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
  'cells',
  'points',
  'images',
  'overlays',
  'badges',
  'trailing',
  'footerActions',
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
    if (object.size !== undefined &&
      (typeof object.size !== 'number' || !Number.isFinite(object.size) ||
        object.size <= 0 || object.size > 40))
      fail(`${path}.size`, 'must be within (0, 40]');
    for (const field of ['tintColor', 'backgroundColor'] as const) {
      const color = object[field];
      if (color !== undefined &&
        (typeof color !== 'string' || !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(color)))
        fail(`${path}.${field}`, 'must be a hex color');
    }
  }
  if (parentField === 'overlays' && object.fallbackIcon !== undefined && !object.image)
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
    assertIdentityExtras(row, path);
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
  if (row.type === 'dataRow' && !Array.isArray(row.columns))
    fail(`${path}.columns`, 'must be an array');
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
  if (snapshot.theme?.rowHoverBackground !== undefined &&
    (typeof snapshot.theme.rowHoverBackground !== 'string' ||
      !/^#[0-9a-f]{6}([0-9a-f]{2})?$/i.test(snapshot.theme.rowHoverBackground)))
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

/** Empty optional image descriptors must take the same path as absent images. */
function normalizeEmptyImages(value: unknown, field = ''): unknown {
  if (!value || typeof value !== 'object') return value;
  if (Array.isArray(value)) {
    const next = value.map((item) => normalizeEmptyImages(item));
    const filtered =
      field === 'images'
        ? next.filter((item) => item !== undefined)
        : next.map((item) => (item === undefined ? null : item));
    return filtered.length === value.length &&
      filtered.every((item, index) => item === value[index])
      ? value
      : filtered;
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
  const next = { ...object };
  Object.entries(object).forEach(([key, item]) => {
    const normalized = normalizeEmptyImages(item, key);
    if (normalized !== item) {
      // Omitted JSON patch fields must also be absent from the JS merge.
      if (field === 'changes' && normalized === undefined) delete next[key];
      else next[key] = normalized;
      changed = true;
    }
  });
  if (field === 'media' && next.source === undefined) return undefined;
  return changed ? next : value;
}

export function validateSnapshot(
  snapshot: NativeListSnapshot
): NativeListSnapshot {
  assertPlainSerializable(snapshot, 'snapshot', new Set<object>());
  if (isDevelopment()) assertSnapshotContract(snapshot);
  const normalized = isDevelopment()
    ? snapshot
    : (normalizeEmptyImages(snapshot) as NativeListSnapshot);
  assertSnapshotProtocol(normalized);
  return normalizeSnapshotStyles(normalized);
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
  return { ...snapshot, rows, emptyState, fixedFooter };
}

function assertPatchChanges(patch: RowPatch, index: number): void {
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
      assertText(patch.changes.title, `${path}.title`);
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
      assertText(patch.changes.title, `${path}.title`);
      assertText(patch.changes.body, `${path}.body`);
      assertText(patch.changes.time, `${path}.time`);
      if (
        patch.changes.bodyLines !== undefined &&
        ![1, 2, 3].includes(patch.changes.bodyLines)
      ) {
        fail(`${path}.bodyLines`, 'must be 1, 2, or 3');
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
    case 'system':
      assertText(patch.changes.message, `${path}.message`);
      break;
    default:
      fail(`patches[${index}].type`, 'is not supported');
  }
}

export function validatePatches(
  patches: readonly RowPatch[]
): readonly RowPatch[] {
  assertPlainSerializable(patches, 'patches', new Set<object>());
  if (!Array.isArray(patches)) fail('patches', 'must be an array');
  const keys = new Set<string>();
  for (const [index, patch] of patches.entries()) {
    protocolObject(patch, `patches[${index}]`);
    protocolKey(patch.key, `patches[${index}].key`);
    if (keys.has(patch.key))
      fail(`patches[${index}].key`, `duplicate key "${patch.key}"`);
    keys.add(patch.key);
    protocolObject(patch.changes, `patches[${index}].changes`);
    assertRowType(patch.type, `patches[${index}]`);
    assertProtocolFields(patch.changes, `patches[${index}].changes`);
    if (patch.type === 'identity')
      assertIdentityExtras(patch.changes, `patches[${index}].changes`);
    if (!isDevelopment()) continue;
    assertKey(patch.key, `patches[${index}].key`);
    if (Object.keys(patch.changes).length === 0) {
      fail(`patches[${index}].changes`, 'must contain at least one field');
    }
    assertPatchChanges(patch, index);
  }
  const normalized = isDevelopment()
    ? patches
    : (normalizeEmptyImages(patches) as readonly RowPatch[]);
  return normalizePatchStyles(
    normalized.filter((patch) => Object.keys(patch.changes).length > 0)
  );
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

export function applyRowPatches(
  snapshot: NativeListSnapshot,
  patches: readonly RowPatch[]
): NativeListSnapshot {
  const normalizedPatches = validatePatches(patches);
  const patchByKey = new Map(
    normalizedPatches.map((patch) => [patch.key, patch])
  );
  const knownKeys = new Set(snapshot.rows.map((row) => row.key));
  normalizedPatches.forEach((patch) => {
    if (!knownKeys.has(patch.key))
      fail('patches', `unknown row key "${patch.key}"`);
  });
  const rows = snapshot.rows.map((row): RowModel => {
    const patch = patchByKey.get(row.key);
    if (!patch) return row;
    if (patch.type !== row.type) {
      fail('patches', `row "${row.key}" is ${row.type}, not ${patch.type}`);
    }
    if (
      row.type === 'market' &&
      row.webTable &&
      patch.type === 'market' &&
      (patch.changes.price !== undefined ||
        patch.changes.priceSegments !== undefined ||
        patch.changes.change !== undefined) &&
      patch.changes.webTable === undefined
    )
      fail(
        'patches',
        'table quote changes must include the visible table cells'
      );
    return {
      ...row,
      ...patch.changes,
      key: row.key,
      type: row.type,
    } as RowModel;
  });
  return validateSnapshot({ ...snapshot, rows });
}

export function serializeSnapshot(snapshot: NativeListSnapshot): string {
  return JSON.stringify(validateSnapshot(snapshot));
}

export function serializePatches(patches: readonly RowPatch[]): string {
  return JSON.stringify(validatePatches(patches));
}
