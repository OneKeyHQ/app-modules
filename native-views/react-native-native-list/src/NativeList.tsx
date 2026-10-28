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
import type {
  NativeListProps,
  NativeListRef,
  StructuralTransitionSettledEvent,
} from './NativeList.types';
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

export type {
  NativeListProps,
  NativeListRef,
  StructuralTransitionSettledEvent,
} from './NativeList.types';
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
const clearedStructuralTransitionSettled = { f: undefined } as const;
const disclosureSlotMeasurementStyle = { alignSelf: 'stretch' } as const;

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
      listExpandedContent,
      listStructureToggle,
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
      onStructuralTransitionSettled,
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
    const renderedSnapshotRef = useRef<NativeListSnapshot | undefined>(
      undefined
    );
    const initializedSnapshot = useRef(false);
    const pendingDisclosureSnapshotRef = useRef<
      | {
          source: 'prop' | 'imperative';
          input: NativeListSnapshot;
          owned: NativeListSnapshot;
          expandedEpoch: number;
          toggleEpoch: number;
        }
      | undefined
    >(undefined);
    const previousPropInputRef = useRef(inputSnapshot);
    const [pendingDisclosureRevision, setPendingDisclosureRevision] =
      useState(0);
    const disclosureSlotMounts = useRef({
      expandedPresent: false,
      togglePresent: false,
      expandedEpoch: 0,
      toggleEpoch: 0,
    });
    const expandedPresent = listExpandedContent != null;
    const togglePresent = listStructureToggle != null;
    if (disclosureSlotMounts.current.expandedPresent !== expandedPresent) {
      disclosureSlotMounts.current.expandedPresent = expandedPresent;
      disclosureSlotMounts.current.expandedEpoch += 1;
    }
    if (disclosureSlotMounts.current.togglePresent !== togglePresent) {
      disclosureSlotMounts.current.togglePresent = togglePresent;
      disclosureSlotMounts.current.toggleEpoch += 1;
    }
    const expandedEpoch = disclosureSlotMounts.current.expandedEpoch;
    const toggleEpoch = disclosureSlotMounts.current.toggleEpoch;
    const [slotMeasurements, setSlotMeasurements] = useState({
      heights: [0, 0, 0, 0, 0, 0],
      readyEpochs: [-1, -1, -1, -1, -1, -1],
    });
    const hasSidecar = listSidecar !== undefined;
    const sidecarWidth = sidecarLayout?.width;
    const sidecarGap = sidecarLayout?.gap;
    const sidecarMaxContentWidth = sidecarLayout?.maxContentWidth;
    const hasContainerSlots = Boolean(
      listHeader ||
        listEmpty ||
        listFooter ||
        listSidecar ||
        listExpandedContent ||
        listStructureToggle
    );
    const validateContainerConfiguration = useCallback(
      (accepted: NativeListSnapshot) => {
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
      },
      [
        hasContainerSlots,
        hasSidecar,
        sidecarWidth,
        sidecarGap,
        sidecarMaxContentWidth,
      ]
    );
    const prepareSnapshot = useCallback(
      (next: NativeListSnapshot) => {
        const accepted = validateSnapshot(captureNativeListInput(next));
        validateContainerConfiguration(accepted);
        return ownValidatedNativeSnapshot(accepted);
      },
      [validateContainerConfiguration]
    );
    const expandedReadyEpoch = slotMeasurements.readyEpochs[4];
    const toggleReadyEpoch = slotMeasurements.readyEpochs[5];
    const snapshot = useMemo(() => {
      // Imperative pending snapshots use this revision to retry the readiness gate.
      // eslint-disable-next-line no-void -- Reading this revision invalidates the readiness memo.
      void pendingDisclosureRevision;
      const propReplaced = previousPropInputRef.current !== inputSnapshot;
      previousPropInputRef.current = inputSnapshot;
      const pending = pendingDisclosureSnapshotRef.current;
      const retained =
        pending &&
        !propReplaced &&
        (pending.source === 'imperative' || pending.input === inputSnapshot) &&
        pending.expandedEpoch === expandedEpoch &&
        pending.toggleEpoch === toggleEpoch
          ? pending
          : undefined;
      const candidate =
        retained?.owned ??
        (!propReplaced && initializedSnapshot.current
          ? renderedSnapshotRef.current ?? snapshotRef.current
          : containNativeListInput(() => prepareSnapshot(inputSnapshot)) ??
            snapshotRef.current);
      const acceptedCandidate =
        !propReplaced && initializedSnapshot.current
          ? containNativeListInput(() => {
              validateContainerConfiguration(candidate);
              return candidate;
            })
          : candidate;
      if (!acceptedCandidate) {
        pendingDisclosureSnapshotRef.current = undefined;
        return renderedSnapshotRef.current ?? snapshotRef.current;
      }
      const transitionId = acceptedCandidate.structuralTransition?.id;
      const newDisclosureCommand =
        initializedSnapshot.current &&
        acceptedCandidate.disclosure !== undefined &&
        transitionId !== undefined &&
        transitionId !== snapshotRef.current.structuralTransition?.id;
      if (
        newDisclosureCommand &&
        ((expandedPresent && expandedReadyEpoch !== expandedEpoch) ||
          (togglePresent && toggleReadyEpoch !== toggleEpoch))
      ) {
        pendingDisclosureSnapshotRef.current = retained ?? {
          source: 'prop',
          input: inputSnapshot,
          owned: acceptedCandidate,
          expandedEpoch,
          toggleEpoch,
        };
        return snapshotRef.current;
      }
      pendingDisclosureSnapshotRef.current = undefined;
      return acceptedCandidate;
    }, [
      inputSnapshot,
      prepareSnapshot,
      validateContainerConfiguration,
      expandedPresent,
      togglePresent,
      expandedEpoch,
      toggleEpoch,
      expandedReadyEpoch,
      toggleReadyEpoch,
      pendingDisclosureRevision,
    ]);
    renderedSnapshotRef.current = snapshot;
    const fontFamiliesJson = useNativeListFontFamiliesJson();
    const slotHeights = slotMeasurements.heights;
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
      ? Math.max(0, (viewportWidth - bandWidth) / 2) + defaultSlotInset
      : defaultSlotInset;
    const sidecarSpace = sidecarLayout
      ? sidecarLayout.width + sidecarLayout.gap
      : 0;
    const slotContents = [
      listHeader,
      listEmpty,
      listFooter,
      listSidecar,
      listExpandedContent,
      listStructureToggle,
    ];
    const recordSlotLayout = (index: number, height: number, epoch: number) => {
      if (
        (index === 4 && epoch !== disclosureSlotMounts.current.expandedEpoch) ||
        (index === 5 && epoch !== disclosureSlotMounts.current.toggleEpoch)
      ) {
        return;
      }
      setSlotMeasurements((previous) => {
        if (
          previous.heights[index] === height &&
          previous.readyEpochs[index] === epoch
        ) {
          return previous;
        }
        const heights = [...previous.heights];
        const readyEpochs = [...previous.readyEpochs];
        heights[index] = height;
        readyEpochs[index] = epoch;
        return { heights, readyEpochs };
      });
    };
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
      onStructuralTransitionSettled,
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
      onStructuralTransitionSettled,
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
        const transitionId = accepted.structuralTransition?.id;
        const waitsForSlots =
          accepted.disclosure !== undefined &&
          transitionId !== undefined &&
          transitionId !== snapshotRef.current.structuralTransition?.id &&
          ((expandedPresent &&
            slotMeasurements.readyEpochs[4] !== expandedEpoch) ||
            (togglePresent && slotMeasurements.readyEpochs[5] !== toggleEpoch));
        if (waitsForSlots) {
          pendingDisclosureSnapshotRef.current = {
            source: 'imperative',
            input: nextSnapshot,
            owned: accepted,
            expandedEpoch,
            toggleEpoch,
          };
          setPendingDisclosureRevision((value) => value + 1);
          return;
        }
        pendingDisclosureSnapshotRef.current = undefined;
        const nextSnapshotJson = JSON.stringify(accepted);
        nativeRef.current?.applySnapshot(nextSnapshotJson);
        snapshotRef.current = accepted;
        if (nativeRef.current && avatarLifecycle.current !== 'unmounted') {
          avatarModelRef.current?.replaceSnapshot(accepted.rows);
          updateAvatarPrefetchRef.current();
        }
      },
      applyPatches(patches) {
        const pending = pendingDisclosureSnapshotRef.current;
        const accepted = containNativeListInput(() =>
          applyOwnedNativePatches(
            pending?.owned ?? snapshotRef.current,
            patches
          )
        );
        if (!accepted || accepted.patches.length === 0) return;
        const nextSnapshot = accepted.snapshot;
        if (pending) {
          pendingDisclosureSnapshotRef.current = {
            ...pending,
            owned: nextSnapshot,
          };
          return;
        }
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
        const pending = pendingDisclosureSnapshotRef.current;
        if (!pending)
          nativeRef.current?.reconcileSelection(JSON.stringify(capturedKeys));
        const current = pending?.owned ?? snapshotRef.current;
        if (
          current.selection &&
          (current.selection.mode !== 'single' || capturedKeys.length <= 1) &&
          capturedKeys.every((key) =>
            current.rows.some((row) => row.key === key && isSelectableRow(row))
          )
        ) {
          const next = {
            ...current,
            selection: { ...current.selection, selectedKeys: capturedKeys },
          };
          if (pending)
            pendingDisclosureSnapshotRef.current = { ...pending, owned: next };
          else snapshotRef.current = next;
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
        onStructuralTransitionSettled: callback((payloadJson: string) => {
          callbacksRef.current.onStructuralTransitionSettled?.(
            parsePayload<StructuralTransitionSettledEvent>(payloadJson)
          );
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
        onStructuralTransitionSettled={
          onStructuralTransitionSettled
            ? nativeCallbacks.onStructuralTransitionSettled
            : clearedStructuralTransitionSettled
        }
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
              if (index < 4) {
                recordSlotLayout(
                  index,
                  Math.ceil(event.nativeEvent.layout.height),
                  0
                );
              }
            }}
          >
            {index >= 4 ? (
              <View
                key={
                  index === 4
                    ? `expanded-${expandedEpoch}`
                    : `toggle-${toggleEpoch}`
                }
                collapsable={false}
                style={disclosureSlotMeasurementStyle}
                onLayout={(event) =>
                  recordSlotLayout(
                    index,
                    Math.ceil(event.nativeEvent.layout.height),
                    index === 4 ? expandedEpoch : toggleEpoch
                  )
                }
              >
                {content}
              </View>
            ) : (
              content
            )}
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
