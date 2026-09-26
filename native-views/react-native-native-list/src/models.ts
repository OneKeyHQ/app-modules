export type NativeListLayout = 'linear' | 'sectioned' | 'grid' | 'table';
export type NativeListOrientation = 'vertical' | 'horizontal';
export type RowSize = 'small' | 'medium' | 'large';
export type RowDensity = 'dense' | 'regular';
export type GroupPosition = 'first' | 'middle' | 'last' | 'single';
export type CheckboxState = 'checked' | 'unchecked' | 'indeterminate';
export type SelectionMode = 'none' | 'single' | 'multiple';
export type TextTone = 'primary' | 'secondary' | 'positive' | 'negative';

export type ImageContentFit = 'cover' | 'contain' | 'fill' | 'center';
export type ImageCachePolicy = 'memory-disk' | 'memory' | 'disk' | 'none';
export type ImageLoadingStrategy = 'static' | 'skeleton' | 'none';

export type ImageSource = Readonly<{
  uri: string;
  width: number;
  height: number;
  headers?: Readonly<Record<string, string>>;
  contentFit?: ImageContentFit;
  cachePolicy?: ImageCachePolicy;
  autoplay?: boolean;
  optimizeTos?: boolean;
  overscan?: number;
  /** Defaults to `none` in NativeList; use `static` or `skeleton` to opt into loading UI. */
  loadingStrategy?: ImageLoadingStrategy;
  // OneKey patch: fallbackUri is Web-only; retryTimes opts all platforms into terminal retries.
  fallbackUri?: string;
  retryTimes?: number;
}>;

export type BadgeModel = Readonly<{
  key: string;
  text: string;
  tone?: 'neutral' | 'info' | 'success' | 'warning' | 'danger';
}>;

/**
 * Named typography steps, shared with the application's design scale. Resolved
 * to fontSize/lineHeight/fontWeight in JavaScript before the snapshot is
 * serialized, so no native renderer needs to know the vocabulary.
 * See docs/STYLE_SPEC.md §3.2.
 */
export type NativeListTypographyToken =
  | '$headingXl'
  | '$headingLg'
  | '$headingMd'
  | '$headingSm'
  | '$headingXs'
  | '$bodyLg'
  | '$bodyMd'
  | '$bodySm'
  | '$bodyXs';

export type NativeListTextStyle = Readonly<{
  token?: NativeListTypographyToken;
  fontSize?: number;
  fontWeight?: 'regular' | 'medium' | 'semibold' | 'bold';
  color?: string;
  lineHeight?: number;
  /** Maximum visible lines; does not reserve empty lines or enlarge the row. */
  lines?: 1 | 2 | 3;
  truncate?: 'tail' | 'clip';
  alignment?: 'start' | 'center' | 'end';
  verticalAlignment?: 'top' | 'center' | 'bottom';
  /** Optical translation in logical units; does not affect layout. */
  offsetY?: number;
}>;

/** Retained alias: Market shipped this name before the style surface was shared. */
export type MarketTextStyle = NativeListTextStyle;

export type NativeListImageStyle = Readonly<{
  width?: number;
  height?: number;
  shape?: 'circle' | 'rounded' | 'square';
  cornerRadius?: number;
  contentFit?: ImageContentFit;
}>;

export type MarketImageStyle = NativeListImageStyle;

export type NativeListRowContainerStyle = Readonly<{
  /** Explicit row height in logical units; overrides RowBase.height. */
  height?: number;
  backgroundColor?: string;
  opacity?: number;
  cornerRadius?: number;
  borderWidth?: number;
  borderColor?: string;
  contentVerticalAlignment?: 'top' | 'center' | 'bottom';
}>;

/** Local box vocabulary. Each template exposes only parameters for its own slots. */
export type RowBoxStyle = Readonly<{
  container?: NativeListRowContainerStyle;
  horizontalPadding?: number;
  verticalPadding?: number;
  leadingGap?: number;
  /** Gap between the template's text rows. Omission preserves its default. */
  lineGap?: number;
  titleBadgeGap?: number;
  trailingGap?: number;
  image?: NativeListImageStyle;
}>;

/** Template-owned slots determine which local box parameters exist, on every platform. */
export const ROW_BOX_STYLE_KEYS_BY_TYPE = {
  identity: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'lineGap',
    'titleBadgeGap',
    'trailingGap',
    'image',
  ],
  walletGroup: ['horizontalPadding', 'verticalPadding'],
  rail: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'titleBadgeGap',
    'trailingGap',
    'image',
  ],
  activity: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'lineGap',
    'trailingGap',
    'image',
  ],
  message: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'lineGap',
    'image',
  ],
  dataRow: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'lineGap',
    'titleBadgeGap',
    'image',
  ],
  market: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'lineGap',
    'titleBadgeGap',
    'trailingGap',
    'image',
  ],
  mediaTile: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'lineGap',
    'image',
  ],
  metricCard: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'lineGap',
    'image',
  ],
  sectionHeader: [
    'horizontalPadding',
    'verticalPadding',
    'lineGap',
    'trailingGap',
  ],
  action: [
    'horizontalPadding',
    'verticalPadding',
    'leadingGap',
    'trailingGap',
    'image',
  ],
  system: ['horizontalPadding', 'verticalPadding', 'lineGap'],
} as const;

type TemplateBoxStyle<T extends keyof typeof ROW_BOX_STYLE_KEYS_BY_TYPE> = Pick<
  RowBoxStyle,
  (typeof ROW_BOX_STYLE_KEYS_BY_TYPE)[T][number] | 'container'
>;

/**
 * A style key names the model field it modifies, never the view that carries
 * it. Each template renderer owns its views and maps the key to whichever view
 * renders that field (for example metricCard `value` is its large number), so
 * the same key name keeps one meaning across platforms. See docs/STYLE_SPEC.md §4.
 */
export type IdentityRowStyle = TemplateBoxStyle<'identity'> &
  Readonly<{
    title?: NativeListTextStyle;
    subtitle?: NativeListTextStyle;
    tertiary?: NativeListTextStyle;
    badge?: NativeListTextStyle;
    value?: NativeListTextStyle;
    valueSecondary?: NativeListTextStyle;
  }>;

export type WalletGroupRowStyle = TemplateBoxStyle<'walletGroup'>;

export type RailRowStyle = TemplateBoxStyle<'rail'> &
  Readonly<{
    title?: NativeListTextStyle;
    badge?: NativeListTextStyle;
    status?: NativeListTextStyle;
  }>;

export type ActivityRowStyle = TemplateBoxStyle<'activity'> &
  Readonly<{
    title?: NativeListTextStyle;
    description?: NativeListTextStyle;
    status?: NativeListTextStyle;
    primaryAmount?: NativeListTextStyle;
    secondaryAmount?: NativeListTextStyle;
  }>;

export type MessageRowStyle = TemplateBoxStyle<'message'> &
  Readonly<{
    title?: NativeListTextStyle;
    body?: NativeListTextStyle;
    time?: NativeListTextStyle;
  }>;

export type DataRowStyle = TemplateBoxStyle<'dataRow'> &
  Readonly<{
    columns?: NativeListTextStyle;
    columnSecondary?: NativeListTextStyle;
    index?: NativeListTextStyle;
  }>;

export type MarketRowStyle = TemplateBoxStyle<'market'> &
  Readonly<{
    /** OneKey patch: keep badges next to the intrinsic title width. */
    titleBadgeLayout?: 'inline';
    /** OneKey patch: preserve separate Market content and subtitle insets. */
    contentTrailingGap?: number;
    subtitleTrailingPadding?: number;
    title?: NativeListTextStyle;
    subtitle?: NativeListTextStyle;
    price?: NativeListTextStyle;
    change?: NativeListTextStyle;
    changeWidth?: number;
    changeHeight?: number;
    changeCornerRadius?: number;
  }>;

export type MediaTileRowStyle = TemplateBoxStyle<'mediaTile'> &
  Readonly<{
    title?: NativeListTextStyle;
    subtitle?: NativeListTextStyle;
    badge?: NativeListTextStyle;
  }>;

export type MetricCardRowStyle = TemplateBoxStyle<'metricCard'> &
  Readonly<{
    /** Standard-card label or composite-card heading. */
    title?: NativeListTextStyle;
    /** Large number in the standard template; absent in composite variants. */
    value?: NativeListTextStyle;
    subtitle?: NativeListTextStyle;
    trend?: NativeListTextStyle;
  }>;

export type SectionHeaderRowStyle = TemplateBoxStyle<'sectionHeader'> &
  Readonly<{
    title?: NativeListTextStyle;
    subtitle?: NativeListTextStyle;
    value?: NativeListTextStyle;
  }>;

export type ActionRowStyle = TemplateBoxStyle<'action'> &
  Readonly<{
    title?: NativeListTextStyle;
    value?: NativeListTextStyle;
  }>;

export type SystemRowStyle = TemplateBoxStyle<'system'> &
  Readonly<{
    title?: NativeListTextStyle;
    message?: NativeListTextStyle;
    /**
     * Retry button text. Web draws a retry button only for the Market retry;
     * a non-Market Web retry is message-only, so this has no target there.
     */
    actionText?: NativeListTextStyle;
  }>;

export type MarketBadgeModel = Readonly<{
  key: string;
  text?: string;
  /** Finite native glyph set; currently only the community verified mark. */
  iconName?: 'verified';
  icon?: ImageSource;
  tone?: 'neutral' | 'info' | 'success' | 'warning' | 'danger';
  textColor?: string;
  backgroundColor?: string;
  actionKey?: string;
  accessibilityLabel?: string;
  /** OneKey patch: optional Market badge metrics; legacy native defaults remain unchanged. */
  style?: Readonly<{
    fontSize?: number;
    fontWeight?: MarketTextStyle['fontWeight'];
    lineHeight?: number;
    height?: number;
    horizontalPadding?: number;
  }>;
}>;

export type MarketChangeModel = Readonly<{
  text: string;
  textSegments?: readonly ValueTextSegment[];
  tone: 'positive' | 'negative' | 'neutral';
  textColor?: string;
  backgroundColor?: string;
}>;

// OneKey patch: keep selector decoration and text data serializable.
export type SelectorTextSegment = Readonly<{
  text: string;
  textSegments?: readonly ValueTextSegment[];
  tone?: TextTone | 'disabled' | 'caution';
  separatorBefore?: boolean;
}>;
export type VisualOverlay = Readonly<{
  position: 'topLeft' | 'bottomRight';
  size?: number;
  width?: number;
  height?: number;
  padding?: number;
  offset?: number;
  offsetX?: number;
  offsetY?: number;
  image?: ImageSource;
  name?: string;
  text?: string;
  tintColor?: string;
  backgroundColor?: string;
}>;

type VisualWithImage = Readonly<{
  fallbackIcon?: Readonly<{ name: string; tintColor?: string }>;
  overlays?: readonly VisualOverlay[];
  borderStyle?: 'dashed';
  borderColor?: string;
  image?: ImageSource;
  fallbackText?: string;
  backgroundColor?: string;
  shape?: 'circle' | 'rounded' | 'square';
  cornerIcon?: Readonly<{
    name: string;
    tintColor?: string;
    backgroundColor?: string;
  }>;
}>;

export type LeadingVisual =
  | (VisualWithImage & Readonly<{ kind: 'token'; networkImage?: ImageSource }>)
  | (VisualWithImage & Readonly<{ kind: 'account' }>)
  | (VisualWithImage & Readonly<{ kind: 'wallet' }>)
  | (VisualWithImage & Readonly<{ kind: 'network' }>)
  | Readonly<{
      kind: 'image';
      image: ImageSource;
      shape?: 'circle' | 'rounded' | 'square';
    }>
  | Readonly<{
      kind: 'icon';
      name: string;
      tintColor?: string;
      backgroundColor?: string;
    }>
  | Readonly<{
      kind: 'stackedImages';
      images:
        | readonly [ImageSource, ImageSource]
        | readonly [ImageSource, ImageSource, ImageSource];
    }>;

export type SelectionTarget =
  | Readonly<{ scope: 'row' }>
  | Readonly<{ scope: 'section'; sectionKey: string }>
  | Readonly<{ scope: 'list' }>;

// OneKey patch: preserve compact very-small-balance digit runs.
export type ValueTextSegment = Readonly<{ text: string; style?: 'subscript' }>;

export type TrailingAccessory =
  | Readonly<{
      kind: 'value';
      text: string;
      secondary?: boolean;
      textSegments?: readonly ValueTextSegment[];
    }>
  | Readonly<{
      kind: 'valuePair';
      primary: string;
      secondary: string;
      primaryTone?: TextTone;
      secondaryTone?: TextTone;
    }>
  | Readonly<{
      kind: 'checkbox';
      state: CheckboxState;
      disabled?: boolean;
      loading?: boolean;
      target?: SelectionTarget;
      actionKey?: string;
    }>
  | Readonly<{
      kind: 'radio';
      checked: boolean;
      disabled?: boolean;
      actionKey?: string;
    }>
  | Readonly<{
      kind: 'switch';
      value: boolean;
      disabled?: boolean;
      actionKey: string;
    }>
  | Readonly<{ kind: 'chevron'; actionKey?: string }>
  | Readonly<{ kind: 'menu'; actionKey: string }>
  | Readonly<{ kind: 'drag'; actionKey?: string }>
  | Readonly<{
      kind: 'icon';
      name: string;
      tintColor?: string;
      disabled?: boolean;
      actionKey?: string;
      testID?: string;
      hoverActionKey?: string;
      accessibilityLabel?: string;
    }>
  | Readonly<{ kind: 'spinner' }>
  | Readonly<{ kind: 'progress'; value: number }>;

export type FooterAction = Readonly<{
  key: string;
  label: string;
  tone?: 'primary' | 'neutral' | 'danger';
  disabled?: boolean;
}>;

export type RowBase = Readonly<{
  // OneKey patch: preserve measured selector geometry without changing defaults.
  height?: number;
  // OneKey patch: retain the source Android list's measured fractional-DP rounding.
  heightRounding?: 'floor' | 'nearest';
  testID?: string;
  opacity?: number;
  backgroundColor?: string;
  backgroundFullWidth?: boolean;
  pressDisabled?: boolean;
  key: string;
  revision?: number;
  sectionKey?: string;
  groupId?: string;
  groupPosition?: GroupPosition;
  size?: RowSize;
  density?: RowDensity;
  disabled?: boolean;
  selected?: boolean;
  accessibilityLabel?: string;
  separator?: boolean;
}>;

export type IdentityRow = RowBase &
  Readonly<{
    type: 'identity';
    // OneKey patch: UTF-16 ranges match Fuse indices and native attributed strings.
    titleMatch?: readonly Readonly<{ start: number; end: number }>[];
    titleActionKey?: string;
    titleActionOnHover?: boolean;
    subtitleSegments?: readonly SelectorTextSegment[];
    presentation?: 'walletSidebar' | 'accountSelector' | 'networkSelector';
    leading: LeadingVisual;
    leadingAction?: Extract<TrailingAccessory, { kind: 'icon' }>;
    title: string;
    subtitle?: string;
    tertiary?: string;
    tertiaryTone?: 'secondary' | 'info';
    titleLines?: 1 | 2;
    subtitleLines?: 1 | 2;
    badges?: readonly BadgeModel[];
    trailing?: readonly TrailingAccessory[];
    draggable?: boolean;
    style?: IdentityRowStyle;
  }>;

export type WalletGroupRow = RowBase &
  Readonly<{
    type: 'walletGroup';
    parent: IdentityRow;
    children: readonly IdentityRow[];
    draggable?: boolean;
    style?: WalletGroupRowStyle;
  }>;

export type RailRow = RowBase &
  Readonly<{
    type: 'rail';
    visual: LeadingVisual;
    title: string;
    status?: 'none' | 'online' | 'warning' | 'error';
    badge?: BadgeModel;
    draggable?: boolean;
    style?: RailRowStyle;
  }>;

export type ActivityAmount = Readonly<{
  key: string;
  text: string;
  textSegments?: readonly ValueTextSegment[];
  leading?: LeadingVisual;
  tone?: TextTone;
  secondaryText?: string;
  secondaryTextSegments?: readonly ValueTextSegment[];
}>;

export type ActivityRow = RowBase &
  Readonly<{
    type: 'activity';
    leading: LeadingVisual;
    secondaryLeading?: LeadingVisual;
    title: string;
    description?: string;
    status?: string;
    primaryAmount?: string;
    secondaryAmount?: string;
    amounts?: readonly ActivityAmount[];
    badges?: readonly BadgeModel[];
    presentation?: 'stacked' | 'table';
    fee?: Readonly<{
      label?: string;
      primary: string;
      primaryTextSegments?: readonly ValueTextSegment[];
      secondary?: string;
      secondaryTextSegments?: readonly ValueTextSegment[];
      hidden?: boolean;
    }>;
    descriptionActionKey?: string;
    footerActions?: readonly FooterAction[];
    style?: ActivityRowStyle;
  }>;

export type MessageRow = RowBase &
  Readonly<{
    type: 'message';
    unread?: boolean;
    leading?: LeadingVisual;
    title: string;
    body: string;
    bodyLines?: 1 | 2 | 3;
    time: string;
    thumbnail?: ImageSource;
    style?: MessageRowStyle;
  }>;

export type DataColumn = Readonly<{
  key: string;
  text: string;
  secondaryLeadingText?: string;
  secondaryText?: string;
  weight?: 1 | 2 | 3;
  alignment?: 'start' | 'center' | 'end';
  tone?: 'primary' | 'secondary' | 'positive' | 'negative';
  secondaryTone?: TextTone;
}>;

export type DataRow = RowBase &
  Readonly<{
    type: 'dataRow';
    leading?: LeadingVisual;
    columns: readonly DataColumn[];
    checkbox?: Extract<TrailingAccessory, { kind: 'checkbox' }>;
    index?: number;
    badge?: BadgeModel;
    badges?: readonly BadgeModel[];
    favorite?: boolean;
    favoriteActive?: boolean;
    style?: DataRowStyle;
  }>;

/** Native Market quote row. All values are already localized/formatted strings. */
export type MarketRow = RowBase &
  Readonly<{
    type: 'market';
    variant: 'token' | 'stock' | 'perp';
    leadingAction?: Extract<TrailingAccessory, { kind: 'icon' }>;
    leading: LeadingVisual;
    title: string;
    subtitle?: string;
    /** OneKey patch: localized name shrinks independently of the volume. */
    subtitlePrefix?: Readonly<{
      text: string;
      gap?: number;
      maxWidth?: number;
      style?: MarketTextStyle;
    }>;
    subtitleSegments?: readonly ValueTextSegment[];
    price: string;
    priceSegments?: readonly ValueTextSegment[];
    change: MarketChangeModel;
    badges?: readonly MarketBadgeModel[];
    pressActionKey?: string;
    pressInActionKey?: string;
    longPressActionKey?: string;
    diagnostics?: Readonly<{ imageBindActionKey?: string }>;
    style?: MarketRowStyle;
  }>;

export type MediaTileRow = RowBase &
  Readonly<{
    type: 'mediaTile';
    variant: 'gallery' | 'browserPreview';
    /** Native preview; video candidates display a silent first frame, never autoplay. */
    media?: Readonly<{
      source: ImageSource;
      probeOrder: readonly ('image' | 'video')[];
    }>;
    image?: ImageSource;
    imageState?: 'empty' | 'error';
    networkImage?: ImageSource;
    title: string;
    subtitle?: string;
    badge?: BadgeModel;
    closeActionKey?: string;
    style?: MediaTileRowStyle;
  }>;

/** Compact KPI card for dashboards and grids. */
export type MetricValue = Readonly<{
  key: string;
  label: string;
  value: string;
  tone?: TextTone;
  visual?: LeadingVisual;
}>;

export type MetricCardRow = RowBase &
  Readonly<{
    type: 'metricCard';
    variant?: 'standard' | 'activity' | 'performance';
    title: string;
    value: string;
    subtitle?: string;
    trend?: string;
    trendTone?: 'positive' | 'negative' | 'neutral';
    visual?: LeadingVisual;
    badge?: BadgeModel;
    metrics?: readonly MetricValue[];
    progress?: number;
    style?: MetricCardRowStyle;
  }>;

export type SectionHeaderRow = RowBase &
  Readonly<{
    type: 'sectionHeader';
    // OneKey patch: title help stays independent of section selection.
    sticky?: boolean;
    valueActionTestID?: string;
    valueSegments?: readonly ValueTextSegment[];
    titleActionKey?: string;
    titleActionOnHover?: boolean;
    sectionKey: string;
    presentation?: 'networkSelector';
    indexTitle?: string;
    variant?: 'summary' | 'gallery' | 'history';
    title: string;
    titleLoading?: boolean;
    subtitle?: string;
    value?: string;
    valueActionKey?: string;
    titleIcon?: Extract<TrailingAccessory, { kind: 'icon' }>;
    valueIcon?: Extract<TrailingAccessory, { kind: 'icon' }>;
    checkbox?: Extract<TrailingAccessory, { kind: 'checkbox' }>;
    style?: SectionHeaderRowStyle;
  }>;

export type ActionRow = RowBase &
  Readonly<{
    type: 'action';
    presentation?: 'accountSelector';
    title: string;
    actionKey: string;
    tone?: 'primary' | 'neutral' | 'danger';
    icon?: Extract<LeadingVisual, { kind: 'icon' }>;
    checkbox?: Extract<TrailingAccessory, { kind: 'checkbox' }>;
    trailing?: readonly TrailingAccessory[];
    style?: ActionRowStyle;
  }>;

export type SystemRow = RowBase &
  Readonly<{ style?: SystemRowStyle }> &
  (
    | Readonly<{
        type: 'system';
        variant: 'loading';
        presentation?: 'market';
        loadingStyle?: 'skeleton' | 'spinner';
        message?: string;
      }>
    | Readonly<{
        type: 'system';
        variant: 'retry';
        presentation?: 'market';
        message: string;
        actionKey: string;
        actionText?: string;
      }>
    | Readonly<{
        type: 'system';
        variant: 'warning';
        title: string;
        message: string;
        borderColor?: string;
      }>
    | Readonly<{
        type: 'system';
        variant: 'noMatch';
        presentation?: 'market';
        message: string;
      }>
    | Readonly<{
        type: 'system';
        variant: 'end';
        presentation?: 'market';
        message?: string;
      }>
    | Readonly<{ type: 'system'; variant: 'spacer'; height: number }>
  );

export type RowModel =
  | IdentityRow
  | WalletGroupRow
  | RailRow
  | ActivityRow
  | MessageRow
  | DataRow
  | MarketRow
  | MediaTileRow
  | MetricCardRow
  | SectionHeaderRow
  | ActionRow
  | SystemRow;

export type NativeListTheme = Readonly<{
  // OneKey patch: explicit selector controls use the original semantic theme tokens.
  checkboxBackground?: string;
  checkboxBorder?: string;
  checkboxIcon?: string;
  cautionBackground?: string;
  background: string;
  rowBackground: string;
  rowSelectedBackground: string;
  rowPressedBackground?: string;
  subduedBackground?: string;
  strongBackground?: string;
  primaryText: string;
  secondaryText: string;
  disabledText?: string;
  icon?: string;
  iconSubdued?: string;
  separator: string;
  accent: string;
  positive: string;
  negative: string;
  criticalBackground?: string;
  inverseBackground?: string;
  inverseText?: string;
  info?: string;
  // OneKey patch: match the existing account warning address tone.
  caution?: string;
}>;

export type SectionIndexConfig = Readonly<{
  enabled: boolean;
  hapticsEnabled?: boolean;
  centeredInWindow?: boolean;
}>;

export type NativeListSeparatorStyle = Readonly<{
  /**
   * Leading inset in logical pixels. Omitted keeps each platform's own default,
   * which is not the same everywhere — see docs/STYLE_SPEC.md §6.2.
   */
  inset?: number;
  /**
   * Overrides the `separator` theme token for this list only. Must be
   * `#RRGGBB` or `#RRGGBBAA`, like every row style color.
   */
  color?: string;
}>;

/**
 * List chrome that every platform can honour. Pull to refresh, the section index
 * rail and the reorder preview are deliberately absent: the first two are system
 * controls or three independent constant sets, and the third is drawn with
 * platform-specific primitives. See docs/STYLE_SPEC.md §5.
 */
export type NativeListListStyle = Readonly<{
  separator?: NativeListSeparatorStyle;
  /**
   * Corner radius of a `groupId` card, `0…40` logical units. Does not affect
   * rail or media tiles.
   */
  groupCornerRadius?: number;
}>;

export type NativeListSnapshot = Readonly<{
  schemaVersion: 1;
  generation: number;
  layout: Readonly<{
    kind: NativeListLayout;
    orientation?: NativeListOrientation;
    gridColumns?: 2 | 3 | 4 | 5 | 6 | 7;
    stickyHeaders?: boolean;
    contentPadding?: number;
    contentPaddingHorizontal?: number;
    contentPaddingTop?: number;
    contentPaddingBottom?: number;
    itemSpacing?: number;
  }>;
  rows: readonly RowModel[];
  selection?: Readonly<{
    mode: SelectionMode;
    selectedKeys: readonly string[];
    rowPressToggles?: boolean;
  }>;
  capabilities?: Readonly<{
    reorderable?: boolean;
    pullToRefresh?: boolean;
    refreshing?: boolean;
    refreshTriggerDistance?: number;
    loadMore?: boolean;
    endReachedThreshold?: number;
    sectionIndex?: SectionIndexConfig;
  }>;
  emptyState?: ActionRow | SystemRow;
  fixedFooter?: ActionRow | SystemRow;
  theme?: NativeListTheme;
  listStyle?: NativeListListStyle;
}>;

// OneKey patch: include selector-only mutable presentation fields.
// type CommonPatchFields = 'revision' | 'disabled' | 'selected' | 'separator';
// OneKey patch: balance patches also refresh the existing row's spoken content.
// type CommonPatchFields = 'revision' | 'disabled' | 'selected' | 'separator' | 'height' | 'heightRounding' | 'opacity' | 'pressDisabled';
type CommonPatchFields =
  | 'revision'
  | 'disabled'
  | 'selected'
  | 'separator'
  | 'height'
  | 'heightRounding'
  | 'opacity'
  | 'pressDisabled'
  | 'accessibilityLabel'
  | 'style';

export type RowPatch =
  | Readonly<{
      type: 'identity';
      key: string;
      changes: Partial<
        Pick<
          IdentityRow,
          | CommonPatchFields
          | 'title'
          | 'subtitle'
          | 'tertiary'
          | 'tertiaryTone'
          | 'badges'
          | 'trailing'
          | 'leading'
          | 'leadingAction'
          | 'titleMatch'
          | 'titleActionKey'
          | 'titleActionOnHover'
          | 'subtitleSegments'
        >
      >;
    }>
  | Readonly<{
      type: 'rail';
      key: string;
      changes: Partial<
        Pick<
          RailRow,
          CommonPatchFields | 'title' | 'status' | 'badge' | 'visual'
        >
      >;
    }>
  | Readonly<{
      type: 'activity';
      key: string;
      changes: Partial<
        Pick<
          ActivityRow,
          | CommonPatchFields
          | 'title'
          | 'description'
          | 'status'
          | 'amounts'
          | 'badges'
          | 'presentation'
          | 'fee'
          | 'descriptionActionKey'
          | 'primaryAmount'
          | 'secondaryAmount'
          | 'leading'
          | 'secondaryLeading'
          | 'footerActions'
        >
      >;
    }>
  | Readonly<{
      type: 'message';
      key: string;
      changes: Partial<
        Pick<
          MessageRow,
          | CommonPatchFields
          | 'unread'
          | 'title'
          | 'body'
          | 'bodyLines'
          | 'time'
          | 'thumbnail'
        >
      >;
    }>
  | Readonly<{
      type: 'dataRow';
      key: string;
      changes: Partial<
        Pick<
          DataRow,
          | CommonPatchFields
          | 'leading'
          | 'columns'
          | 'checkbox'
          | 'index'
          | 'badge'
          | 'badges'
          | 'favorite'
          | 'favoriteActive'
        >
      >;
    }>
  | Readonly<{
      type: 'market';
      key: string;
      changes: Partial<
        Pick<
          MarketRow,
          | CommonPatchFields
          | 'leadingAction'
          | 'leading'
          | 'title'
          | 'subtitle'
          | 'subtitleSegments'
          | 'price'
          | 'priceSegments'
          | 'change'
          | 'badges'
          | 'pressActionKey'
          | 'pressInActionKey'
          | 'longPressActionKey'
          | 'diagnostics'
          | 'style'
        >
      >;
    }>
  | Readonly<{
      type: 'mediaTile';
      key: string;
      changes: Partial<
        Pick<
          MediaTileRow,
          | CommonPatchFields
          | 'image'
          | 'imageState'
          | 'media'
          | 'networkImage'
          | 'title'
          | 'subtitle'
          | 'badge'
          | 'closeActionKey'
        >
      >;
    }>
  | Readonly<{
      type: 'metricCard';
      key: string;
      changes: Partial<
        Pick<
          MetricCardRow,
          | CommonPatchFields
          | 'variant'
          | 'title'
          | 'value'
          | 'subtitle'
          | 'trend'
          | 'trendTone'
          | 'visual'
          | 'badge'
          | 'metrics'
          | 'progress'
        >
      >;
    }>
  | Readonly<{
      type: 'sectionHeader';
      key: string;
      changes: Partial<
        Pick<
          SectionHeaderRow,
          | CommonPatchFields
          | 'variant'
          | 'titleLoading'
          | 'title'
          | 'subtitle'
          | 'value'
          | 'valueActionKey'
          | 'titleActionKey'
          | 'titleActionOnHover'
          | 'titleIcon'
          | 'valueIcon'
          | 'checkbox'
        >
      >;
    }>
  | Readonly<{
      type: 'action';
      key: string;
      changes: Partial<
        Pick<
          ActionRow,
          | CommonPatchFields
          | 'title'
          | 'tone'
          | 'icon'
          | 'checkbox'
          | 'trailing'
        >
      >;
    }>
  | Readonly<{
      type: 'system';
      key: string;
      changes: Readonly<{
        revision?: number;
        disabled?: boolean;
        message?: string;
      }>;
    }>;

export type NativeListActionSource =
  | 'row'
  | 'marketBadge'
  | 'leadingAction'
  | 'trailingAccessory'
  | 'footerAction'
  | 'description'
  | 'mediaClose';

export type NativeListWindowRect = Readonly<{
  x: number;
  y: number;
  width: number;
  height: number;
}>;

export type NativeListActionAnchor = Readonly<{
  /** Opaque identity for one concrete native/DOM control binding. */
  token: string;
  /** Window-relative logical units: CSS px on Web, points on iOS, dp on Android. */
  windowRect: NativeListWindowRect;
  /** Actual long-press point, in the same logical units as windowRect. */
  windowPoint?: Readonly<{ x: number; y: number }>;
  source: NativeListActionSource;
  slot?: number;
  generation: number;
  layoutDirection: 'ltr' | 'rtl';
}>;

export type RowActionEvent = Readonly<{
  rowKey?: string;
  actionKey: string;
  sectionKey?: string;
  anchor?: NativeListActionAnchor;
}>;

export type ActionAnchorInvalidationReason =
  // OneKey patch: close web title tooltips when their native list target is left.
  'pointerLeave' | 'scroll' | 'rebind' | 'snapshot' | 'layout' | 'destroy';

export type ActionAnchorInvalidatedEvent = Readonly<{
  token: string;
  reason: ActionAnchorInvalidationReason;
}>;

export type SelectionDeltaEvent = Readonly<{
  addedKeys: readonly string[];
  removedKeys: readonly string[];
  source: 'row' | 'section' | 'list' | 'nativeReorder';
  sourceKey?: string;
}>;

export type ReorderEvent = Readonly<{
  key: string;
  fromIndex: number;
  toIndex: number;
  beforeKey?: string;
  afterKey?: string;
}>;

export type EndReachedEvent = Readonly<{
  generation: number;
  lastKey?: string;
}>;

export type VisibleRangeChangedEvent = Readonly<{
  firstKey?: string;
  lastKey?: string;
  firstIndex: number;
  lastIndex: number;
}>;
