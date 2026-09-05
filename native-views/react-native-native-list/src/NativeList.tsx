import React, { forwardRef, useImperativeHandle, useMemo, useRef } from 'react';
import { callback, getHostComponent } from 'react-native-nitro-modules';
import type {
  NativeListMethods,
  NativeListNativeProps,
} from './NativeList.nitro';
import type {
  RowActionEvent,
  SelectionDeltaEvent,
  ReorderEvent,
  EndReachedEvent,
  VisibleRangeChangedEvent,
} from './models';
import type { NativeListProps, NativeListRef } from './NativeList.types';
import {
  normalizeIndexScroll,
  normalizeKeyScroll,
  normalizePositionScroll,
  resolveLocationIndex,
  scrollFailure,
  validateOffset,
  type NormalizedPositionScroll,
} from './scrolling';
import { serializePatches, serializeSnapshot } from './validation';

export type { NativeListProps, NativeListRef } from './NativeList.types';
export type {
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
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onRefresh,
      onScrollToIndexFailed,
    });
    callbacksRef.current = {
      onRowAction,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onRefresh,
      onScrollToIndexFailed,
    };
    const snapshotRef = useRef(snapshot);
    snapshotRef.current = snapshot;
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
    const snapshotJson = useMemo(() => serializeSnapshot(snapshot), [snapshot]);

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
        snapshotRef.current = nextSnapshot;
        nativeRef.current?.applySnapshot(serializeSnapshot(nextSnapshot));
      },
      applyPatches(patches) {
        if (patches.length > 0)
          nativeRef.current?.applyPatches(serializePatches(patches));
      },
      reconcileSelection(selectedKeys) {
        nativeRef.current?.reconcileSelection(JSON.stringify(selectedKeys));
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
        dispatchIndexScroll(index, normalizePositionScroll(params, 'start'));
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
        onSelectionDelta: callback((payloadJson: string) => {
          callbacksRef.current.onSelectionDelta?.(
            parsePayload<SelectionDeltaEvent>(payloadJson)
          );
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
          callbacksRef.current.onVisibleRangeChanged?.(
            parsePayload<VisibleRangeChangedEvent>(payloadJson)
          );
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
