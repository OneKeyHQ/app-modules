import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useMemo,
  useRef,
} from 'react';
import {
  OneKeyImageCache,
  OneKeyImageCachePolicy,
} from '@onekeyfe/react-native-image';
import {
  NativeAvatarPrefetchModel,
  NativeAvatarPrefetchQueue,
} from './avatarPrefetch';
import { callback, getHostComponent } from 'react-native-nitro-modules';
import type {
  NativeListMethods,
  NativeListNativeProps,
} from './NativeList.nitro';
import type {
  ActionAnchorInvalidatedEvent,
  RowActionEvent,
  SelectionDeltaEvent,
  ReorderEvent,
  EndReachedEvent,
  VisibleRangeChangedEvent,
} from './models';
import type { NativeListProps, NativeListRef } from './NativeList.types';
import { isSelectableRow } from './selection';
import {
  normalizeIndexScroll,
  normalizeKeyScroll,
  normalizePositionScroll,
  resolveLocationIndex,
  scrollFailure,
  validateOffset,
  type NormalizedPositionScroll,
} from './scrolling';
import {
  applyRowPatches,
  serializePatches,
  serializeSnapshot,
} from './validation';

export type { NativeListProps, NativeListRef } from './NativeList.types';
export type {
  ActionAnchorState,
  ScrollAlignment,
  ScrollPositionOptions,
  ScrollToEndParams,
  ScrollToIndexFailedInfo,
  ScrollToIndexParams,
  ScrollToItemParams,
  ScrollToKeyParams,
  ScrollToLocationParams,
  ScrollToOffsetParams,
} from './NativeList.types';

const NativeListConfig = require('../nitrogen/generated/shared/json/NativeListConfig.json');

const NativeListHost = getHostComponent<
  NativeListNativeProps,
  NativeListMethods
>('NativeList', () => NativeListConfig);

function parsePayload<T>(payloadJson: string): T {
  return JSON.parse(payloadJson) as T;
}

export const NativeList = forwardRef<NativeListRef, NativeListProps>(
  function NativeList(
    {
      snapshot,
      webVirtualizationEnabled: _webVirtualizationEnabled,
      onRowAction,
      onActionAnchorInvalidated,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onRefresh,
      onScrollToIndexFailed,
      initialScrollIndex,
      initialScrollKey,
      initialScrollViewPosition,
      initialScrollViewOffset,
      ...viewProps
    },
    forwardedRef
  ) {
    const nativeRef = useRef<NativeListMethods | null>(null);
    const callbacksRef = useRef({
      onRowAction,
      onActionAnchorInvalidated,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onRefresh,
      onScrollToIndexFailed,
    });
    callbacksRef.current = {
      onRowAction,
      onActionAnchorInvalidated,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onRefresh,
      onScrollToIndexFailed,
    };
    const snapshotJson = useMemo(() => serializeSnapshot(snapshot), [snapshot]);
    const snapshotRef = useRef(snapshot);
    const previousSnapshotJsonRef = useRef(snapshotJson);
    const snapshotChanged = previousSnapshotJsonRef.current !== snapshotJson;
    if (snapshotChanged) {
      snapshotRef.current = snapshot;
      previousSnapshotJsonRef.current = snapshotJson;
    }
    // OneKey patch: range events already cross the bridge only when row indices
    // change. Prefetch is optional work; scrolling and visible loads stay native.
    const avatarQueueRef = useRef<NativeAvatarPrefetchQueue | null>(null);
    const avatarRangeRef = useRef<
      { first: number; last: number; direction: number } | undefined
    >(undefined);
    // OneKey patch: a changed prop is a complete snapshot; unrelated renders must
    // not erase image patches already dispatched through the imperative handle.
    // const avatarPrefetchPaused = useRef(false);
    const avatarModelRef = useRef<NativeAvatarPrefetchModel | null>(null);
    const avatarLifecycle = useRef<'pending' | 'mounted' | 'unmounted'>(
      'pending'
    );
    if (!avatarModelRef.current)
      avatarModelRef.current = new NativeAvatarPrefetchModel(snapshot.rows);
    else if (snapshotChanged)
      avatarModelRef.current.replaceSnapshot(snapshot.rows);
    const updateAvatarPrefetch = () => {
      const range = avatarRangeRef.current;
      if (!range) return;
      avatarQueueRef.current?.update(
        avatarModelRef.current?.window(
          range.first,
          range.last,
          range.direction
        ) ?? []
      );
    };
    const updateAvatarPrefetchRef = useRef(updateAvatarPrefetch);
    updateAvatarPrefetchRef.current = updateAvatarPrefetch;
    useEffect(() => {
      avatarLifecycle.current = 'mounted';
      const queue = new NativeAvatarPrefetchQueue((source) =>
        OneKeyImageCache.preload([
          {
            uri: source.uri,
            headers: source.headers,
            resizeWidth: source.width,
            resizeHeight: source.height,
            optimizeTos: false,
            cachePolicy:
              source.cachePolicy === 'memory'
                ? OneKeyImageCachePolicy.MEMORY
                : source.cachePolicy === 'disk'
                ? OneKeyImageCachePolicy.DISK
                : OneKeyImageCachePolicy.MEMORY_DISK,
          },
        ])
      );
      avatarQueueRef.current = queue;
      updateAvatarPrefetchRef.current();
      return () => {
        queue.dispose();
        avatarQueueRef.current = null;
        avatarLifecycle.current = 'unmounted';
      };
    }, []);
    useEffect(() => {
      updateAvatarPrefetchRef.current();
    }, [snapshot]);
    const initialScrollRef = useRef<
      | Readonly<{
          index?: number;
          key?: string;
          scroll: NormalizedPositionScroll;
        }>
      | undefined
    >(
      initialScrollIndex === undefined && initialScrollKey === undefined
        ? undefined
        : initialScrollIndex !== undefined
        ? normalizeIndexScroll({
            index: initialScrollIndex,
            animated: false,
            viewPosition: initialScrollViewPosition,
            viewOffset: initialScrollViewOffset,
          })
        : {
            key: initialScrollKey,
            scroll: normalizePositionScroll(
              {
                animated: false,
                viewPosition: initialScrollViewPosition,
                viewOffset: initialScrollViewOffset,
              },
              'start'
            ),
          }
    );
    const didApplyInitialScroll = useRef(false);

    const emitIndexFailure = (
      index: number,
      reason: Parameters<typeof scrollFailure>[2]
    ) => {
      callbacksRef.current.onScrollToIndexFailed?.(
        scrollFailure(snapshotRef.current.rows, index, reason)
      );
    };

    const dispatchIndexScroll = (
      index: number,
      scroll: NormalizedPositionScroll
    ) => {
      if (index >= snapshotRef.current.rows.length) {
        emitIndexFailure(index, 'index-out-of-range');
        return;
      }
      nativeRef.current?.scrollToIndex(
        index,
        scroll.animated,
        scroll.alignment,
        scroll.viewPosition,
        scroll.viewOffset
      );
    };

    const dispatchKeyScroll = (
      key: string,
      scroll: NormalizedPositionScroll
    ) => {
      nativeRef.current?.scrollToKey(
        key,
        scroll.animated,
        scroll.alignment,
        scroll.viewPosition,
        scroll.viewOffset
      );
    };

    const applyInitialScroll = () => {
      if (didApplyInitialScroll.current || !nativeRef.current) return;
      didApplyInitialScroll.current = true;
      const initial = initialScrollRef.current;
      if (!initial) return;
      if (initial.index !== undefined)
        dispatchIndexScroll(initial.index, initial.scroll);
      else if (initial.key !== undefined)
        dispatchKeyScroll(initial.key, initial.scroll);
    };
    const applyInitialScrollCallbackRef = useRef(applyInitialScroll);
    applyInitialScrollCallbackRef.current = applyInitialScroll;

    useImperativeHandle(forwardedRef, () => ({
      applySnapshot(nextSnapshot) {
        const nextSnapshotJson = serializeSnapshot(nextSnapshot);
        nativeRef.current?.applySnapshot(nextSnapshotJson);
        snapshotRef.current = nextSnapshot;
        if (nativeRef.current && avatarLifecycle.current !== 'unmounted') {
          avatarModelRef.current?.replaceSnapshot(nextSnapshot.rows);
          updateAvatarPrefetchRef.current();
        }
      },
      applyPatches(patches) {
        if (patches.length === 0) return;
        const nextSnapshot = applyRowPatches(snapshotRef.current, patches);
        const native = nativeRef.current;
        const dispatch = native
          ? () => {
              native.applyPatches(serializePatches(patches));
              snapshotRef.current = nextSnapshot;
            }
          : undefined;
        if (avatarLifecycle.current === 'unmounted') {
          dispatch?.();
          return;
        }
        if (avatarModelRef.current?.applyPatches(patches, dispatch))
          updateAvatarPrefetchRef.current();
      },
      reconcileSelection(selectedKeys) {
        nativeRef.current?.reconcileSelection(JSON.stringify(selectedKeys));
        const current = snapshotRef.current;
        if (
          current.selection &&
          (current.selection.mode !== 'single' || selectedKeys.length <= 1) &&
          selectedKeys.every((key) =>
            current.rows.some((row) => row.key === key && isSelectableRow(row))
          )
        ) {
          snapshotRef.current = {
            ...current,
            selection: { ...current.selection, selectedKeys },
          };
        }
      },
      scrollToKey(paramsOrKey, animated, alignment) {
        const { key, scroll } = normalizeKeyScroll(
          paramsOrKey,
          animated,
          alignment
        );
        dispatchKeyScroll(key, scroll);
      },
      scrollToIndex(paramsOrIndex, animated, alignment) {
        const { index, scroll } = normalizeIndexScroll(
          paramsOrIndex,
          animated,
          alignment
        );
        dispatchIndexScroll(index, scroll);
      },
      scrollToItem(params) {
        const index = snapshotRef.current.rows.findIndex(
          (row) => row.key === params.item.key
        );
        if (index < 0) {
          emitIndexFailure(-1, 'item-not-found');
          return;
        }
        dispatchKeyScroll(
          params.item.key,
          normalizePositionScroll(params, 'start')
        );
      },
      scrollToOffset({ offset, animated = true }) {
        validateOffset(offset);
        nativeRef.current?.scrollToOffset(offset, animated);
      },
      scrollToEnd({ animated = true } = {}) {
        nativeRef.current?.scrollToEnd(animated);
      },
      scrollToLocation(params) {
        const index = resolveLocationIndex(snapshotRef.current.rows, params);
        if (index === undefined) {
          const sectionCount = snapshotRef.current.rows.filter(
            (row) => row.type === 'sectionHeader' && row.variant !== 'summary'
          ).length;
          emitIndexFailure(
            params.itemIndex,
            params.sectionIndex >= sectionCount
              ? 'section-out-of-range'
              : 'item-out-of-range'
          );
          return;
        }
        dispatchIndexScroll(index, normalizePositionScroll(params, 'start'));
      },
      setActionAnchorState(state) {
        nativeRef.current?.setActionAnchorState(JSON.stringify(state));
      },
      setRefreshing(refreshing) {
        nativeRef.current?.setRefreshing(refreshing);
      },
    }));

    const nativeCallbacks = useMemo(
      () => ({
        hybridRef: callback((ref: NativeListMethods) => {
          nativeRef.current = ref;
          applyInitialScrollCallbackRef.current();
        }),
        onRowAction: callback((payloadJson: string) => {
          const payload = parsePayload<RowActionEvent>(payloadJson);
          if (payload.actionKey === 'nativeList.refresh')
            callbacksRef.current.onRefresh?.();
          callbacksRef.current.onRowAction?.(payload);
        }),
        onActionAnchorInvalidated: callback((payloadJson: string) => {
          callbacksRef.current.onActionAnchorInvalidated?.(
            parsePayload<ActionAnchorInvalidatedEvent>(payloadJson)
          );
        }),
        onSelectionDelta: callback((payloadJson: string) => {
          const payload = parsePayload<SelectionDeltaEvent>(payloadJson);
          const current = snapshotRef.current;
          if (current.selection) {
            const selectedKeys = new Set(current.selection.selectedKeys);
            payload.removedKeys.forEach((key) => selectedKeys.delete(key));
            payload.addedKeys.forEach((key) => selectedKeys.add(key));
            snapshotRef.current = {
              ...current,
              selection: {
                ...current.selection,
                selectedKeys: [...selectedKeys],
              },
            };
          }
          callbacksRef.current.onSelectionDelta?.(payload);
        }),
        onReorder: callback((payloadJson: string) => {
          callbacksRef.current.onReorder?.(
            parsePayload<ReorderEvent>(payloadJson)
          );
        }),
        onEndReached: callback((payloadJson: string) => {
          callbacksRef.current.onEndReached?.(
            parsePayload<EndReachedEvent>(payloadJson)
          );
        }),
        onVisibleRangeChanged: callback((payloadJson: string) => {
          const payload = parsePayload<VisibleRangeChangedEvent>(payloadJson);
          const previous = avatarRangeRef.current;
          avatarRangeRef.current = {
            first: payload.firstIndex,
            last: payload.lastIndex,
            direction:
              previous && payload.firstIndex !== previous.first
                ? Math.sign(payload.firstIndex - previous.first)
                : previous?.direction ?? 1,
          };
          updateAvatarPrefetchRef.current();
          callbacksRef.current.onVisibleRangeChanged?.(payload);
        }),
      }),
      []
    );

    return (
      <NativeListHost
        {...viewProps}
        {...nativeCallbacks}
        snapshotJson={snapshotJson}
      />
    );
  }
);
