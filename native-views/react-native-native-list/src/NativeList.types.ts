import type { ViewProps } from 'react-native';
import type {
  EndReachedEvent,
  NativeListSnapshot,
  ReorderEvent,
  RowActionEvent,
  RowPatch,
  SelectionDeltaEvent,
  VisibleRangeChangedEvent,
} from './models';

export type ScrollAlignment = 'start' | 'center' | 'end' | 'nearest';

export type NativeListRef = Readonly<{
  applySnapshot(snapshot: NativeListSnapshot): void;
  applyPatches(patches: readonly RowPatch[]): void;
  reconcileSelection(selectedKeys: readonly string[]): void;
  scrollToKey(
    key: string,
    animated?: boolean,
    alignment?: ScrollAlignment
  ): void;
  scrollToIndex(
    index: number,
    animated?: boolean,
    alignment?: ScrollAlignment
  ): void;
  setRefreshing(refreshing: boolean): void;
}>;

export type NativeListProps = Omit<ViewProps, 'children'> &
  Readonly<{
    snapshot: NativeListSnapshot;
    onRowAction?: (event: RowActionEvent) => void;
    onSelectionDelta?: (event: SelectionDeltaEvent) => void;
    onReorder?: (event: ReorderEvent) => void;
    onEndReached?: (event: EndReachedEvent) => void;
    onVisibleRangeChanged?: (event: VisibleRangeChangedEvent) => void;
    onRefresh?: () => void;
  }>;
