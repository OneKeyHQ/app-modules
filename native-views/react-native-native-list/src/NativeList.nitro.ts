import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

/**
 * Nitro owns only the React Native host boundary. Snapshot diffing, layout,
 * recycling, selection, and scrolling remain native UICollectionView /
 * RecyclerView work.
 */
export interface NativeListNativeProps extends HybridViewProps {
  snapshotJson: string;
  onRowAction?: (payloadJson: string) => void;
  onSelectionDelta?: (payloadJson: string) => void;
  onReorder?: (payloadJson: string) => void;
  onEndReached?: (payloadJson: string) => void;
  onVisibleRangeChanged?: (payloadJson: string) => void;
}

export type NativeListScrollAlignment = 'start' | 'center' | 'end' | 'nearest';

export interface NativeListMethods extends HybridViewMethods {
  applySnapshot(snapshotJson: string): void;
  applyPatches(patchesJson: string): void;
  reconcileSelection(selectedKeysJson: string): void;
  scrollToKey(
    key: string,
    animated: boolean,
    alignment: NativeListScrollAlignment
  ): void;
  scrollToIndex(
    index: number,
    animated: boolean,
    alignment: NativeListScrollAlignment
  ): void;
  setRefreshing(refreshing: boolean): void;
}

export type NativeList = HybridView<NativeListNativeProps, NativeListMethods>;
