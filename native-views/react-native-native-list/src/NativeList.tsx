import { useNativeListFontFamiliesJson } from './fonts';
import React, {
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useMemo,
  useRef,
  useState,
} from 'react';
import {
  OneKeyImageCache,
  OneKeyImageCachePolicy,
} from '@onekeyfe/react-native-image';
import {
  NativeAvatarPrefetchModel,
  NativeAvatarPrefetchQueue,
} from './avatarPrefetch';
import { Platform, View } from 'react-native';
import { callback, getHostComponent } from 'react-native-nitro-modules';
import type {
  NativeListMethods,
  NativeListNativeProps,
} from './NativeList.nitro';
import type {
  ActionAnchorInvalidatedEvent,
  ImageSource,
  NativeListSnapshot,
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
  applyOwnedNativePatches,
  captureNativeListInput,
  containNativeListInput,
  emptyNativeListSnapshot,
  ownValidatedNativeSnapshot,
  validateSnapshot,
} from './validation';

export type { NativeListProps, NativeListRef } from './NativeList.types';
export type {
  ActionAnchorState,
  NativeListKeyboardDismissMode,
  NativeListKeyboardShouldPersistTaps,
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

export function preloadNativeListAvatarImages(
  sources: readonly ImageSource[]
): Promise<boolean> {
  return OneKeyImageCache.preload(
    sources.map((source) => ({
      uri: source.uri,
      headers: source.headers,
      resizeWidth: source.width,
      resizeHeight: source.height,
      optimizeTos: source.optimizeTos !== false,
      overscan: source.overscan,
      cachePolicy:
        source.cachePolicy === 'memory'
          ? OneKeyImageCachePolicy.MEMORY
          : source.cachePolicy === 'disk'
          ? OneKeyImageCachePolicy.DISK
          : OneKeyImageCachePolicy.MEMORY_DISK,
    }))
  );
}

function parsePayload<T>(payloadJson: string): T {
  return JSON.parse(payloadJson) as T;
}

export const NativeList = forwardRef<NativeListRef, NativeListProps>(
  function NativeList(
    {
      snapshot: inputSnapshot,
      listHeader,
      listFooter,
      listEmpty,
      listSidecar,
      sidecarLayout,
      topOverlay,
      topScrollEdgeEffect,
      scrollPositionThresholds,
      webVirtualizationEnabled: _webVirtualizationEnabled,
      webScrollHost: _webScrollHost,
      webStickyInset: _webStickyInset,
      webSectionIndexContainerRef: _webSectionIndexContainerRef,
      keyboardDismissMode = 'none',
      keyboardShouldPersistTaps = 'never',
      onRowAction,
      onActionAnchorInvalidated,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onScrollPositionThresholdChange,
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
    const snapshotRef = useRef<NativeListSnapshot>(emptyNativeListSnapshot());
    const initializedSnapshot = useRef(false);
    const hasSidecar = listSidecar !== undefined;
    const sidecarWidth = sidecarLayout?.width;
    const sidecarGap = sidecarLayout?.gap;
    const sidecarMaxContentWidth = sidecarLayout?.maxContentWidth;
    const hasContainerSlots = Boolean(
      listHeader || listEmpty || listFooter || listSidecar
    );
    const prepareSnapshot = useCallback(
      (next: NativeListSnapshot) => {
        const accepted = validateSnapshot(captureNativeListInput(next));
        if (hasContainerSlots && accepted.layout.orientation === 'horizontal')
          throw new Error(
            'NativeList container slots require vertical orientation'
          );
        if (hasSidecar !== (sidecarWidth !== undefined))
          throw new Error(
            'listSidecar and sidecarLayout must be supplied together'
          );
        if (sidecarWidth !== undefined) {
          const width = sidecarWidth;
          const gap = sidecarGap ?? NaN;
          const maxContentWidth = sidecarMaxContentWidth ?? NaN;
          if (
            accepted.layout.kind !== 'linear' ||
            !Number.isFinite(width) ||
            width <= 0 ||
            !Number.isFinite(gap) ||
            gap < 0 ||
            !Number.isFinite(maxContentWidth) ||
            maxContentWidth <= width + gap
          )
            throw new Error('Invalid NativeList sidecar layout');
        }
        return ownValidatedNativeSnapshot(accepted);
      },
      [
        hasContainerSlots,
        hasSidecar,
        sidecarWidth,
        sidecarGap,
        sidecarMaxContentWidth,
      ]
    );
    const snapshot = useMemo(
      () =>
        containNativeListInput(() => prepareSnapshot(inputSnapshot)) ??
        snapshotRef.current,
      [inputSnapshot, prepareSnapshot]
    );
    const fontFamiliesJson = useNativeListFontFamiliesJson();
    const [slotHeights, setSlotHeights] = useState([0, 0, 0, 0]);
    const [viewportWidth, setViewportWidth] = useState(0);
    const [topOverlayHeight, setTopOverlayHeight] = useState(0);
    const hasTopOverlay =
      Platform?.OS === 'ios' &&
      parseInt(String(Platform.Version), 10) >= 26 &&
      (topScrollEdgeEffect === 'hard' || topScrollEdgeEffect === 'soft') &&
      topOverlay != null;
    const containerSlotHeightsJson = JSON.stringify(slotHeights);
    const defaultSlotInset =
      snapshot.layout.contentPaddingHorizontal ??
      snapshot.layout.contentPadding ??
      0;
    const bandWidth = sidecarLayout
      ? Math.min(viewportWidth, sidecarLayout.maxContentWidth)
      : 0;
    const slotInset = sidecarLayout
      ? Math.max(0, (viewportWidth - bandWidth) / 2)
      : defaultSlotInset;
    const sidecarSpace = sidecarLayout
      ? sidecarLayout.width + sidecarLayout.gap
      : 0;
    const slotContents = [listHeader, listEmpty, listFooter, listSidecar];
    const sidecarLayoutJson = sidecarLayout
      ? JSON.stringify(sidecarLayout)
      : '';
    const normalizedKeyboardShouldPersistTaps =
      keyboardShouldPersistTaps === true
        ? 'always'
        : keyboardShouldPersistTaps === false
        ? 'never'
        : keyboardShouldPersistTaps;
    const nativeRef = useRef<NativeListMethods | null>(null);
    const callbacksRef = useRef({
      onRowAction,
      onActionAnchorInvalidated,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onScrollPositionThresholdChange,
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
      onScrollPositionThresholdChange,
      onRefresh,
      onScrollToIndexFailed,
    };
    const scrollPositionThresholdsJson = useMemo(
      () =>
        containNativeListInput(() => {
          if (
            !scrollPositionThresholds ||
            scrollPositionThresholds.enabled === false
          )
            return '';
          const { start, end } = scrollPositionThresholds;
          if (
            !Number.isFinite(start) ||
            !Number.isFinite(end) ||
            start < 0 ||
            end <= start
          ) {
            throw new Error(
              'scrollPositionThresholds requires 0 <= start < end and finite values'
            );
          }
          return JSON.stringify({ start, end });
        }) ?? '',
      [scrollPositionThresholds]
    );
    const snapshotJson = useMemo(() => JSON.stringify(snapshot), [snapshot]);
    const previousSnapshotJsonRef = useRef(snapshotJson);
    const snapshotChanged = previousSnapshotJsonRef.current !== snapshotJson;
    if (snapshotChanged) {
      snapshotRef.current = snapshot;
      previousSnapshotJsonRef.current = snapshotJson;
    }
    if (!initializedSnapshot.current) {
      snapshotRef.current = snapshot;
      initializedSnapshot.current = true;
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
        preloadNativeListAvatarImages([source])
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
      containNativeListInput(() =>
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
      )
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
        const accepted = containNativeListInput(() =>
          prepareSnapshot(nextSnapshot)
        );
        if (!accepted) return;
        const nextSnapshotJson = JSON.stringify(accepted);
        nativeRef.current?.applySnapshot(nextSnapshotJson);
        snapshotRef.current = accepted;
        if (nativeRef.current && avatarLifecycle.current !== 'unmounted') {
          avatarModelRef.current?.replaceSnapshot(accepted.rows);
          updateAvatarPrefetchRef.current();
        }
      },
      applyPatches(patches) {
        const accepted = containNativeListInput(() =>
          applyOwnedNativePatches(snapshotRef.current, patches)
        );
        if (!accepted || accepted.patches.length === 0) return;
        const nextSnapshot = accepted.snapshot;
        const native = nativeRef.current;
        const dispatch = native
          ? () => {
              native.applyPatches(accepted.json);
              snapshotRef.current = nextSnapshot;
            }
          : undefined;
        if (avatarLifecycle.current === 'unmounted') {
          dispatch?.();
          return;
        }
        if (avatarModelRef.current?.applyPatches(accepted.patches, dispatch))
          updateAvatarPrefetchRef.current();
      },
      reconcileSelection(selectedKeys) {
        const capturedKeys = captureNativeListInput(selectedKeys);
        nativeRef.current?.reconcileSelection(JSON.stringify(capturedKeys));
        const current = snapshotRef.current;
        if (
          current.selection &&
          (current.selection.mode !== 'single' || capturedKeys.length <= 1) &&
          capturedKeys.every((key) =>
            current.rows.some((row) => row.key === key && isSelectableRow(row))
          )
        ) {
          snapshotRef.current = {
            ...current,
            selection: { ...current.selection, selectedKeys: capturedKeys },
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
          if (
            payload.actionKey === 'nativeList.refresh' &&
            payload.rowKey === undefined
          ) {
            callbacksRef.current.onRefresh?.();
            return;
          }
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
        onScrollPositionThresholdChange: callback(
          (isBeyondThreshold: boolean) => {
            callbacksRef.current.onScrollPositionThresholdChange?.({
              isBeyondThreshold,
            });
          }
        ),
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
        onLayout={(event) => {
          setViewportWidth(event.nativeEvent.layout.width);
          viewProps.onLayout?.(event);
        }}
        {...nativeCallbacks}
        fontFamiliesJson={fontFamiliesJson}
        snapshotJson={snapshotJson}
        containerSlotHeightsJson={containerSlotHeightsJson}
        sidecarLayoutJson={sidecarLayoutJson}
        topScrollEdgeEffect={
          hasTopOverlay ? topScrollEdgeEffect ?? 'none' : 'none'
        }
        topOverlayHeight={hasTopOverlay ? topOverlayHeight : 0}
        scrollPositionThresholdsJson={scrollPositionThresholdsJson}
        keyboardDismissMode={keyboardDismissMode}
        keyboardShouldPersistTaps={normalizedKeyboardShouldPersistTaps}
      >
        {slotContents.map((content, index) => (
          <View
            key={index}
            collapsable={false}
            style={{
              position: 'absolute',
              start: index === 3 ? 0 : slotInset,
              end: index === 3 ? undefined : slotInset + sidecarSpace,
              width: index === 3 ? sidecarLayout?.width : undefined,
              top: 0,
            }}
            onLayout={(event) => {
              const height = Math.ceil(event.nativeEvent.layout.height);
              setSlotHeights((previous) =>
                previous[index] === height
                  ? previous
                  : previous.map((value, slot) =>
                      slot === index ? height : value
                    )
              );
            }}
          >
            {content}
          </View>
        ))}
        {hasTopOverlay ? (
          <View
            collapsable={false}
            style={{ position: 'absolute', top: 0, left: 0, right: 0 }}
            onLayout={(event) => {
              const height = Math.ceil(event.nativeEvent.layout.height);
              setTopOverlayHeight((previous) =>
                previous === height ? previous : height
              );
            }}
          >
            {topOverlay}
          </View>
        ) : null}
      </NativeListHost>
    );
  }
);
