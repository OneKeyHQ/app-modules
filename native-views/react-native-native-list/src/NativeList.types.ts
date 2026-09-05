import type { ViewProps } from 'react-native';
import type {
  EndReachedEvent,
  NativeListSnapshot,
  ReorderEvent,
  RowActionEvent,
  RowModel,
  RowPatch,
  SelectionDeltaEvent,
  VisibleRangeChangedEvent,
} from './models';

export type ScrollAlignment = 'start' | 'center' | 'end' | 'nearest';

export type ScrollPositionOptions = Readonly<{
  animated?: boolean;
  alignment?: ScrollAlignment;
  viewPosition?: number;
  viewOffset?: number;
}>;

export type ScrollToIndexParams = ScrollPositionOptions &
  Readonly<{ index: number }>;

export type ScrollToKeyParams = ScrollPositionOptions &
  Readonly<{ key: string }>;

export type ScrollToItemParams = ScrollPositionOptions &
  Readonly<{ item: RowModel }>;

export type ScrollToOffsetParams = Readonly<{
  offset: number;
  animated?: boolean;
}>;

export type ScrollToEndParams = Readonly<{
  animated?: boolean;
}>;

export type ScrollToLocationParams = ScrollPositionOptions &
  Readonly<{
    sectionIndex: number;
    itemIndex: number;
  }>;

export type ScrollToIndexFailedInfo = Readonly<{
  index: number;
  itemCount: number;
  highestMeasuredFrameIndex: number;
  averageItemLength: number;
  reason:
    | 'index-out-of-range'
    | 'section-out-of-range'
    | 'item-out-of-range'
    | 'item-not-found';
}>;

type InitialScrollProps =
  | Readonly<{
      initialScrollIndex?: never;
      initialScrollKey?: never;
      initialScrollViewPosition?: never;
      initialScrollViewOffset?: never;
    }>
  | Readonly<{
      initialScrollIndex: number;
      initialScrollKey?: never;
      initialScrollViewPosition?: number;
      initialScrollViewOffset?: number;
    }>
  | Readonly<{
      initialScrollIndex?: never;
      initialScrollKey: string;
      initialScrollViewPosition?: number;
      initialScrollViewOffset?: number;
    }>;

type ScrollToIndex = (
  paramsOrIndex: ScrollToIndexParams | number,
  animated?: boolean,
  alignment?: ScrollAlignment
) => void;

type ScrollToKey = (
  paramsOrKey: ScrollToKeyParams | string,
  animated?: boolean,
  alignment?: ScrollAlignment
) => void;

export type NativeListRef = Readonly<{
  applySnapshot(snapshot: NativeListSnapshot): void;
  applyPatches(patches: readonly RowPatch[]): void;
  reconcileSelection(selectedKeys: readonly string[]): void;
  scrollToKey: ScrollToKey;
  scrollToIndex: ScrollToIndex;
  scrollToItem(params: ScrollToItemParams): void;
  scrollToOffset(params: ScrollToOffsetParams): void;
  scrollToEnd(params?: ScrollToEndParams): void;
  scrollToLocation(params: ScrollToLocationParams): void;
  setRefreshing(refreshing: boolean): void;
}>;

export type NativeListProps = Omit<ViewProps, 'children'> &
  Readonly<{
    snapshot: NativeListSnapshot;
    /** Web only. Defaults to true and is ignored by the native host. */
    webVirtualizationEnabled?: boolean;
    onRowAction?: (event: RowActionEvent) => void;
    onSelectionDelta?: (event: SelectionDeltaEvent) => void;
    onReorder?: (event: ReorderEvent) => void;
    onEndReached?: (event: EndReachedEvent) => void;
    onVisibleRangeChanged?: (event: VisibleRangeChangedEvent) => void;
    onRefresh?: () => void;
    onScrollToIndexFailed?: (info: ScrollToIndexFailedInfo) => void;
  }> &
  InitialScrollProps;
