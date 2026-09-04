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
import { serializePatches, serializeSnapshot } from './validation';

export type { NativeListProps, NativeListRef } from './NativeList.types';
export type { ScrollAlignment } from './NativeList.types';

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
      onRowAction,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onRefresh,
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
    });
    callbacksRef.current = {
      onRowAction,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onRefresh,
    };
    const snapshotJson = useMemo(() => serializeSnapshot(snapshot), [snapshot]);

    useImperativeHandle(
      forwardedRef,
      () => ({
        applySnapshot(nextSnapshot) {
          nativeRef.current?.applySnapshot(serializeSnapshot(nextSnapshot));
        },
        applyPatches(patches) {
          if (patches.length > 0)
            nativeRef.current?.applyPatches(serializePatches(patches));
        },
        reconcileSelection(selectedKeys) {
          nativeRef.current?.reconcileSelection(JSON.stringify(selectedKeys));
        },
        scrollToKey(key, animated = true, alignment = 'nearest') {
          nativeRef.current?.scrollToKey(key, animated, alignment);
        },
        scrollToIndex(index, animated = true, alignment = 'nearest') {
          if (!Number.isInteger(index) || index < 0) {
            throw new Error(
              'NativeList scrollToIndex index must be a non-negative integer'
            );
          }
          nativeRef.current?.scrollToIndex(index, animated, alignment);
        },
        setRefreshing(refreshing) {
          nativeRef.current?.setRefreshing(refreshing);
        },
      }),
      []
    );

    const nativeCallbacks = useMemo(
      () => ({
        hybridRef: callback((ref: NativeListMethods) => {
          nativeRef.current = ref;
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
