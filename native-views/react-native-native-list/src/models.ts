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
  loadingStrategy?: ImageLoadingStrategy;
}>;

export type BadgeModel = Readonly<{
  key: string;
  text: string;
  tone?: 'neutral' | 'info' | 'success' | 'warning' | 'danger';
}>;

type VisualWithImage = Readonly<{
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

export type TrailingAccessory =
  | Readonly<{ kind: 'value'; text: string; secondary?: boolean }>
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
  }>;

export type RailRow = RowBase &
  Readonly<{
    type: 'rail';
    visual: LeadingVisual;
    title: string;
    status?: 'none' | 'online' | 'warning' | 'error';
    badge?: BadgeModel;
    draggable?: boolean;
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
    footerActions?: readonly FooterAction[];
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
  }>;

export type MediaTileRow = RowBase &
  Readonly<{
    type: 'mediaTile';
    variant: 'gallery' | 'browserPreview';
    image?: ImageSource;
    imageState?: 'empty' | 'error';
    networkImage?: ImageSource;
    title: string;
    subtitle?: string;
    badge?: BadgeModel;
    closeActionKey?: string;
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
  }>;

export type SectionHeaderRow = RowBase &
  Readonly<{
    type: 'sectionHeader';
    sectionKey: string;
    indexTitle?: string;
    variant?: 'summary' | 'gallery' | 'history';
    title: string;
    subtitle?: string;
    value?: string;
    valueActionKey?: string;
    titleIcon?: Extract<TrailingAccessory, { kind: 'icon' }>;
    valueIcon?: Extract<TrailingAccessory, { kind: 'icon' }>;
    checkbox?: Extract<TrailingAccessory, { kind: 'checkbox' }>;
  }>;

export type ActionRow = RowBase &
  Readonly<{
    type: 'action';
    title: string;
    actionKey: string;
    tone?: 'primary' | 'neutral' | 'danger';
    icon?: Extract<LeadingVisual, { kind: 'icon' }>;
    checkbox?: Extract<TrailingAccessory, { kind: 'checkbox' }>;
    trailing?: readonly TrailingAccessory[];
  }>;

export type SystemRow = RowBase &
  (
    | Readonly<{ type: 'system'; variant: 'loading'; message?: string }>
    | Readonly<{
        type: 'system';
        variant: 'retry';
        message: string;
        actionKey: string;
      }>
    | Readonly<{ type: 'system'; variant: 'noMatch'; message: string }>
    | Readonly<{ type: 'system'; variant: 'end'; message?: string }>
    | Readonly<{ type: 'system'; variant: 'spacer'; height: number }>
  );

export type RowModel =
  | IdentityRow
  | RailRow
  | ActivityRow
  | MessageRow
  | DataRow
  | MediaTileRow
  | MetricCardRow
  | SectionHeaderRow
  | ActionRow
  | SystemRow;

export type NativeListTheme = Readonly<{
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
}>;

export type SectionIndexConfig = Readonly<{
  enabled: boolean;
  hapticsEnabled?: boolean;
}>;

export type NativeListSnapshot = Readonly<{
  schemaVersion: 1;
  generation: number;
  layout: Readonly<{
    kind: NativeListLayout;
    orientation?: NativeListOrientation;
    gridColumns?: 2 | 3 | 4;
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
    loadMore?: boolean;
    endReachedThreshold?: number;
    sectionIndex?: SectionIndexConfig;
  }>;
  emptyState?: ActionRow | SystemRow;
  fixedFooter?: ActionRow | SystemRow;
  theme?: NativeListTheme;
}>;

type CommonPatchFields = 'revision' | 'disabled' | 'selected' | 'separator';

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
      type: 'mediaTile';
      key: string;
      changes: Partial<
        Pick<
          MediaTileRow,
          | CommonPatchFields
          | 'image'
          | 'imageState'
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
          | 'title'
          | 'subtitle'
          | 'value'
          | 'valueActionKey'
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

export type RowActionEvent = Readonly<{
  rowKey?: string;
  actionKey: string;
  sectionKey?: string;
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
