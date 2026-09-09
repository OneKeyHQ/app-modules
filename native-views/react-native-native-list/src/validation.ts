import type {
  ActivityRow,
  IdentityRow,
  ImageSource,
  LeadingVisual,
  MarketRow,
  MarketRowStyle,
  MarketTextStyle,
  NativeListSnapshot,
  RowModel,
  RowPatch,
  WalletGroupRow,
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

function fail(path: string, message: string): never {
  throw new Error(`NativeList ${path}: ${message}`);
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
    key.length === 0 ||
    key.length > MAX_KEY_LENGTH
  ) {
    fail(
      path,
      `must be a non-empty string no longer than ${MAX_KEY_LENGTH} characters`
    );
  }
  if (key.trim() !== key)
    fail(path, 'must not have leading or trailing whitespace');
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

function assertMarketTextStyle(
  style: MarketTextStyle | undefined,
  path: string
): void {
  if (!style) return;
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
  if (style.lines !== undefined && ![1, 2].includes(style.lines)) {
    fail(`${path}.lines`, 'must be 1 or 2');
  }
}

function assertMarketStyle(
  style: MarketRowStyle | undefined,
  path: string
): void {
  if (!style) return;
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
  assertBoundedStyleNumber(style.changeWidth, `${path}.changeWidth`, 1, 160);
  assertBoundedStyleNumber(style.changeHeight, `${path}.changeHeight`, 1, 160);
  assertBoundedStyleNumber(
    style.changeCornerRadius,
    `${path}.changeCornerRadius`,
    0,
    80
  );
  if (style.image) {
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
  assertMarketTextStyle(style.title, `${path}.title`);
  assertMarketTextStyle(style.subtitle, `${path}.subtitle`);
  assertMarketTextStyle(style.price, `${path}.price`);
  assertMarketTextStyle(style.change, `${path}.change`);
}

function assertMarketRow(row: MarketRow, path: string): void {
  if (!['token', 'stock', 'perp'].includes(row.variant)) {
    fail(`${path}.variant`, 'must be token, stock, or perp');
  }
  assertText(row.title, `${path}.title`);
  assertText(row.subtitle, `${path}.subtitle`);
  assertText(row.price, `${path}.price`);
  assertText(row.change.text, `${path}.change.text`);
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
  if ((row.badges?.length ?? 0) > MAX_MARKET_BADGES) {
    fail(`${path}.badges`, `supports at most ${MAX_MARKET_BADGES} badges`);
  }
  const badgeKeys = new Set<string>();
  row.badges?.forEach((badge, index) => {
    const badgePath = `${path}.badges[${index}]`;
    assertKey(badge.key, `${badgePath}.key`);
    if (badgeKeys.has(badge.key)) {
      fail(`${path}.badges`, `duplicate badge key "${badge.key}"`);
    }
    badgeKeys.add(badge.key);
    assertText(badge.text, `${badgePath}.text`);
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
    if (
      accessory.kind === 'progress' &&
      (accessory.value < 0 || accessory.value > 1)
    ) {
      fail(`${accessoryPath}.value`, 'must be within 0...1');
    }
    if (accessory.kind === 'valuePair') {
      assertTextTone(accessory.primaryTone, `${accessoryPath}.primaryTone`);
      assertTextTone(accessory.secondaryTone, `${accessoryPath}.secondaryTone`);
    }
  });
}

function assertImage(image: ImageSource | undefined, path: string): void {
  if (!image) return;
  if (!image.uri.trim()) fail(`${path}.uri`, 'must not be empty');
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
      assertText(overlay.text, `${path}.overlays[${index}].text`);
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

function assertActivity(row: ActivityRow, path: string): void {
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

function assertRow(
  row: RowModel,
  index: number,
  path = `rows[${index}]`
): void {
  assertKey(row.key, `${path}.key`);
  // OneKey patch: explicit dimensions and opacity cannot corrupt list layout.
  if (row.height !== undefined && (row.height < 0 || row.height > 4096))
    fail(`${path}.height`, 'must be within 0...4096');
  if (
    row.heightRounding !== undefined &&
    (row.height === undefined ||
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
      if ((row.bodyLines ?? 3) < 1 || (row.bodyLines ?? 3) > 3) {
        fail(`${path}.bodyLines`, 'must be within 1...3');
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
      assertImage(row.image, `${path}.image`);
      if (
        row.imageState !== undefined &&
        !['empty', 'error'].includes(row.imageState)
      ) {
        fail(`${path}.imageState`, 'must be empty or error when provided');
      }
      if (row.imageState === undefined && row.image === undefined) {
        fail(
          `${path}.image`,
          'is required unless imageState is empty or error'
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

export function validateSnapshot(
  snapshot: NativeListSnapshot
): NativeListSnapshot {
  assertPlainSerializable(snapshot, 'snapshot', new Set<object>());
  if (snapshot.schemaVersion !== 1) fail('snapshot.schemaVersion', 'must be 1');
  if (!Number.isSafeInteger(snapshot.generation) || snapshot.generation < 0) {
    fail('snapshot.generation', 'must be a non-negative safe integer');
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

function assertPatchChanges(patch: RowPatch, index: number): void {
  const path = `patches[${index}].changes`;
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
        (patch.changes.bodyLines < 1 || patch.changes.bodyLines > 3)
      ) {
        fail(`${path}.bodyLines`, 'must be within 1...3');
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
  const keys = new Set<string>();
  patches.forEach((patch, index) => {
    assertKey(patch.key, `patches[${index}].key`);
    if (keys.has(patch.key))
      fail(`patches[${index}].key`, `duplicate key "${patch.key}"`);
    keys.add(patch.key);
    if (Object.keys(patch.changes).length === 0) {
      fail(`patches[${index}].changes`, 'must contain at least one field');
    }
    assertPatchChanges(patch, index);
  });
  return patches;
}

export function applyRowPatches(
  snapshot: NativeListSnapshot,
  patches: readonly RowPatch[]
): NativeListSnapshot {
  validatePatches(patches);
  const patchByKey = new Map(patches.map((patch) => [patch.key, patch]));
  const knownKeys = new Set(snapshot.rows.map((row) => row.key));
  patches.forEach((patch) => {
    if (!knownKeys.has(patch.key))
      fail('patches', `unknown row key "${patch.key}"`);
  });
  const rows = snapshot.rows.map((row): RowModel => {
    const patch = patchByKey.get(row.key);
    if (!patch) return row;
    if (patch.type !== row.type) {
      fail('patches', `row "${row.key}" is ${row.type}, not ${patch.type}`);
    }
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
