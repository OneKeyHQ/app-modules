import { useNativeListFontFamiliesJson } from './fonts';
import React, {
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useLayoutEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { View } from 'react-native';
import type { NativeListProps, NativeListRef } from './NativeList.types';
import type { ImageSource, NativeListSnapshot } from './models';
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
  containNativeListInput,
  emptyNativeListSnapshot,
  validatePatches,
  validateSnapshot,
} from './validation';
import {
  NativeListWebEngine,
  type NativeListWebCallbacks,
} from './web/NativeListWebEngine';
import {
  acquireNativeListAvatar,
  canonicalNativeListAvatarUri,
} from './web/NativeListWebAvatarCache';

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

export function preloadNativeListAvatarImages(
  sources: readonly ImageSource[]
): Promise<boolean> {
  if (typeof document === 'undefined') return Promise.resolve(false);
  const uris = [
    ...new Set(
      sources
        .map((source) => canonicalNativeListAvatarUri(source.uri))
        .filter((uri): uri is string => uri !== undefined)
    ),
  ];
  if (!uris.length) return Promise.resolve(false);
  return Promise.all(
    uris.map(
      (uri) =>
        new Promise<boolean>((resolve) => {
          let release: (() => void) | undefined;
          const settle = (success: boolean) => {
            resolve(success);
            queueMicrotask(() => release?.());
          };
          release = acquireNativeListAvatar(
            document,
            uri,
            () => settle(true),
            () => settle(false),
            0
          );
        })
    )
  ).then((results) => results.every(Boolean));
}

export const NativeList = forwardRef<NativeListRef, NativeListProps>(
  function NativeList(
    {
      snapshot: inputSnapshot,
      listHeader,
      listEmpty,
      listFooter,
      listExpandedContent: _listExpandedContent,
      listStructureToggle: _listStructureToggle,
      listSidecar,
      sidecarLayout,
      topOverlay: _topOverlay,
      topScrollEdgeEffect: _topScrollEdgeEffect,
      webScrollHost,
      webStickyInset: inputStickyInset = 0,
      scrollPositionThresholds: _scrollPositionThresholds,
      onScrollPositionThresholdChange: _onScrollPositionThresholdChange,
      webVirtualizationEnabled = true,
      webSectionIndexContainerRef,
      keyboardDismissMode: _keyboardDismissMode,
      keyboardShouldPersistTaps: _keyboardShouldPersistTaps,
      onRowAction,
      onActionAnchorInvalidated,
      onSelectionDelta,
      onReorder,
      onEndReached,
      onVisibleRangeChanged,
      onStructuralTransitionSettled: _onStructuralTransitionSettled,
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
    const acceptedSnapshotRef = useRef<NativeListSnapshot>(
      emptyNativeListSnapshot()
    );
    const initializedSnapshot = useRef(false);
    const webStickyInset = useMemo(
      () =>
        containNativeListInput(() => {
          if (!Number.isFinite(inputStickyInset) || inputStickyInset < 0)
            throw new Error('webStickyInset must be finite and non-negative');
          return inputStickyInset;
        }) ?? 0,
      [inputStickyInset]
    );
    const preparedSnapshot = useMemo(
      () => containNativeListInput(() => validateSnapshot(inputSnapshot)),
      [inputSnapshot]
    );
    const snapshot = preparedSnapshot ?? acceptedSnapshotRef.current;
    const fontFamiliesJson = useNativeListFontFamiliesJson();
    const fontFamiliesJsonRef = useRef(fontFamiliesJson);
    fontFamiliesJsonRef.current = fontFamiliesJson;
    const hasEmptySlot = listEmpty !== undefined;
    const hasEmptySlotRef = useRef(hasEmptySlot);
    hasEmptySlotRef.current = hasEmptySlot;
    const [slotState, setSlotState] = useState(() => ({
      hasRows: snapshot.rows.length > 0,
      emptyBottomPadding:
        !snapshot.rows.length && hasEmptySlot
          ? snapshot.layout.contentPaddingBottom ??
            snapshot.layout.contentPadding ??
            0
          : 0,
      padding:
        snapshot.layout.contentPaddingHorizontal ??
        snapshot.layout.contentPadding ??
        0,
    }));
    const [viewportWidth, setViewportWidth] = useState(0);
    const [sidecarHeight, setSidecarHeight] = useState(0);
    const synchronizeSlots = (next: NativeListSnapshot) => {
      const hasRows = next.rows.length > 0;
      const padding =
        next.layout.contentPaddingHorizontal ?? next.layout.contentPadding ?? 0;
      const emptyBottomPadding =
        !hasRows && hasEmptySlotRef.current
          ? next.layout.contentPaddingBottom ?? next.layout.contentPadding ?? 0
          : 0;
      setSlotState((previous) =>
        previous.hasRows === hasRows &&
        previous.padding === padding &&
        previous.emptyBottomPadding === emptyBottomPadding
          ? previous
          : { hasRows, padding, emptyBottomPadding }
      );
    };
    const normalizeWebSnapshot = (
      next: NativeListSnapshot,
      hasReactEmpty = hasEmptySlotRef.current,
      hasSlotLayout = useSlotLayoutRef.current
    ) => {
      if (hasSlotLayout && next.layout.orientation === 'horizontal')
        throw new Error(
          'Web parent scrolling and container slots support vertical lists only'
        );
      const emptySlot = hasReactEmpty && next.rows.length === 0;
      const validated = validateSnapshot(
        emptySlot ? { ...next, emptyState: undefined } : next
      );
      return emptySlot
        ? {
            ...validated,
            layout: { ...validated.layout, contentPaddingBottom: 0 },
          }
        : validated;
    };
    const [host, setHost] = useState<HTMLElement | null>(null);
    const [contentBounds, setContentBounds] = useState<HTMLElement | null>(
      null
    );
    const [ownedScrollHost, setOwnedScrollHost] = useState<HTMLElement | null>(
      null
    );
    const useSlotLayoutRef = useRef(false);
    useSlotLayoutRef.current ||=
      webScrollHost !== undefined ||
      listHeader !== undefined ||
      listEmpty !== undefined ||
      listFooter !== undefined;
    useSlotLayoutRef.current ||= listSidecar !== undefined;
    const useSlotLayout = useSlotLayoutRef.current;
    const resolvedScrollHost =
      webScrollHost === undefined
        ? useSlotLayout
          ? ownedScrollHost
          : undefined
        : webScrollHost;
    const engineRef = useRef<NativeListWebEngine | undefined>(undefined);
    const webVirtualizationEnabledRef = useRef(webVirtualizationEnabled);
    webVirtualizationEnabledRef.current = webVirtualizationEnabled;
    const validatedSnapshot = useMemo(
      () =>
        containNativeListInput(() => {
          if ((listSidecar !== undefined) !== (sidecarLayout !== undefined))
            throw new Error(
              'listSidecar and sidecarLayout must be supplied together'
            );
          if (sidecarLayout) {
            const { width, gap, maxContentWidth } = sidecarLayout;
            if (
              snapshot.layout.kind !== 'linear' ||
              !Number.isFinite(width) ||
              width <= 0 ||
              !Number.isFinite(gap) ||
              gap < 0 ||
              !Number.isFinite(maxContentWidth) ||
              maxContentWidth <= width + gap
            )
              throw new Error('Invalid NativeList sidecar layout');
          }
          return normalizeWebSnapshot(snapshot, hasEmptySlot, useSlotLayout);
        }) ?? acceptedSnapshotRef.current,
      [snapshot, hasEmptySlot, useSlotLayout, listSidecar, sidecarLayout]
    );
    const snapshotRef = useRef(validatedSnapshot);
    const snapshotPropRef = useRef(validatedSnapshot);
    // An imperative snapshot survives host mounting until the snapshot prop changes.
    if (snapshotPropRef.current !== validatedSnapshot) {
      snapshotPropRef.current = validatedSnapshot;
      snapshotRef.current = validatedSnapshot;
      acceptedSnapshotRef.current = validatedSnapshot;
      synchronizeSlots(snapshot);
    }
    if (!initializedSnapshot.current) {
      acceptedSnapshotRef.current = validatedSnapshot;
      initializedSnapshot.current = true;
    }
    const appliedSnapshotRef = useRef<NativeListSnapshot | undefined>(
      undefined
    );
    const callbacksRef = useRef<NativeListWebCallbacks>({});
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
    const scrollOptionsRef = useRef({
      stickyInset: webStickyInset,
      ownsRefresh: webScrollHost === undefined,
    });
    scrollOptionsRef.current = {
      stickyInset: webStickyInset,
      ownsRefresh: webScrollHost === undefined,
    };

    useEffect(() => {
      if (
        !host ||
        resolvedScrollHost === null ||
        (useSlotLayout && !contentBounds)
      )
        return;
      const engine = containNativeListInput(
        () =>
          new NativeListWebEngine(
            host,
            snapshotRef.current,
            callbacksRef.current,
            webVirtualizationEnabledRef.current,
            webSectionIndexContainerRef?.current as unknown as HTMLElement | null,
            fontFamiliesJsonRef.current,
            {
              scrollHost: resolvedScrollHost as HTMLElement | undefined,
              contentBounds: contentBounds ?? undefined,
              ...scrollOptionsRef.current,
            }
          )
      );
      if (!engine) return;
      host.style.height = '';
      engineRef.current = engine;
      appliedSnapshotRef.current = snapshotRef.current;
      const initial = initialScrollRef.current;
      if (initial && !didApplyInitialScroll.current) {
        didApplyInitialScroll.current = true;
        if (initial.index !== undefined)
          engine.scrollToIndex(initial.index, initial.scroll);
        else if (initial.key !== undefined)
          engine.scrollToKey(initial.key, initial.scroll);
      }
      return () => {
        // Keep the paused tab in normal flow until its engine is restored.
        host.style.height = `${engine.currentContentHeight()}px`;
        engine.destroy();
        if (engineRef.current === engine) engineRef.current = undefined;
      };
    }, [
      host,
      webSectionIndexContainerRef,
      resolvedScrollHost,
      contentBounds,
      useSlotLayout,
    ]);

    useEffect(() => {
      engineRef.current?.setStickyInset(webStickyInset);
    }, [webStickyInset]);

    useEffect(() => {
      engineRef.current?.setFontFamiliesJson(fontFamiliesJson);
    }, [fontFamiliesJson]);

    useEffect(() => {
      engineRef.current?.setVirtualizationEnabled(webVirtualizationEnabled);
    }, [webVirtualizationEnabled]);

    useEffect(() => {
      engineRef.current?.updateCallbacks(callbacksRef.current);
    });

    useLayoutEffect(() => {
      const engine = engineRef.current;
      if (!engine || appliedSnapshotRef.current === validatedSnapshot) return;
      engine.applySnapshot(validatedSnapshot);
      appliedSnapshotRef.current = validatedSnapshot;
    }, [validatedSnapshot]);

    const emitIndexFailure = (
      index: number,
      reason: Parameters<typeof scrollFailure>[2]
    ) => {
      callbacksRef.current.onScrollToIndexFailed?.(
        scrollFailure(snapshotRef.current.rows, index, reason)
      );
    };

    useImperativeHandle(forwardedRef, () => ({
      applySnapshot(nextSnapshot) {
        const next = containNativeListInput(() =>
          normalizeWebSnapshot(nextSnapshot)
        );
        if (!next) return;
        engineRef.current?.applySnapshot(next);
        snapshotRef.current = next;
        acceptedSnapshotRef.current = next;
        appliedSnapshotRef.current = next;
        synchronizeSlots(next);
      },
      applyPatches(patches) {
        const accepted = containNativeListInput(() => validatePatches(patches));
        if (accepted && accepted.length > 0) {
          const engine = engineRef.current;
          if (engine) {
            engine.applyPatches(accepted);
            snapshotRef.current = engine.currentSnapshot();
          } else {
            const next = containNativeListInput(() =>
              applyRowPatches(snapshotRef.current, accepted)
            );
            if (next) snapshotRef.current = next;
          }
          acceptedSnapshotRef.current = snapshotRef.current;
        }
      },
      reconcileSelection(selectedKeys) {
        engineRef.current?.reconcileSelection(selectedKeys);
      },
      scrollToKey(paramsOrKey, animated, alignment) {
        const { key, scroll } = normalizeKeyScroll(
          paramsOrKey,
          animated,
          alignment
        );
        engineRef.current?.scrollToKey(key, scroll);
      },
      scrollToIndex(paramsOrIndex, animated, alignment) {
        const { index, scroll } = normalizeIndexScroll(
          paramsOrIndex,
          animated,
          alignment
        );
        if (index >= snapshotRef.current.rows.length) {
          emitIndexFailure(index, 'index-out-of-range');
          return;
        }
        engineRef.current?.scrollToIndex(index, scroll);
      },
      scrollToItem(params) {
        const index = snapshotRef.current.rows.findIndex(
          (row) => row.key === params.item.key
        );
        if (index < 0) {
          emitIndexFailure(-1, 'item-not-found');
          return;
        }
        engineRef.current?.scrollToKey(
          params.item.key,
          normalizePositionScroll(params, 'start')
        );
      },
      scrollToOffset({ offset, animated = true }) {
        validateOffset(offset);
        engineRef.current?.scrollToOffset(offset, animated);
      },
      scrollToEnd({ animated = true } = {}) {
        engineRef.current?.scrollToEnd(animated);
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
        engineRef.current?.scrollToIndex(
          index,
          normalizePositionScroll(params, 'start')
        );
      },
      setActionAnchorState(state) {
        engineRef.current?.setActionAnchorState(state);
      },
      setRefreshing(refreshing) {
        engineRef.current?.setRefreshing(refreshing);
      },
    }));

    const setHostRef = useCallback(
      (value: React.ComponentRef<typeof View> | null) => {
        setHost(value as unknown as HTMLElement | null);
      },
      []
    );

    const setBoundsRef = useCallback(
      (value: React.ComponentRef<typeof View> | null) => {
        setContentBounds(value as unknown as HTMLElement | null);
      },
      []
    );
    const ownedScrollElementRef = useRef<HTMLElement | null>(null);
    const setScrollHostRef = useCallback(
      (value: React.ComponentRef<typeof View> | null) => {
        const element = value as unknown as HTMLElement | null;
        if (ownedScrollElementRef.current !== element)
          ownedScrollElementRef.current?.removeAttribute(
            'data-collapsible-pager-scroll'
          );
        element?.setAttribute('data-collapsible-pager-scroll', 'true');
        ownedScrollElementRef.current = element;
        setOwnedScrollHost(element);
      },
      []
    );
    if (!useSlotLayout) return <View {...viewProps} ref={setHostRef} />;
    const padding = slotState.padding;
    const content = (
      <View
        ref={sidecarLayout ? undefined : setBoundsRef}
        style={{ position: 'relative', flexShrink: 0, minWidth: 0 }}
      >
        {listHeader !== undefined ? (
          <View style={{ paddingHorizontal: padding }}>{listHeader}</View>
        ) : null}
        <View ref={setHostRef} style={{ flexShrink: 0, minWidth: 0 }} />
        {!slotState.hasRows && hasEmptySlot ? (
          <View
            style={{
              paddingHorizontal: padding,
              paddingBottom: slotState.emptyBottomPadding,
            }}
          >
            {listEmpty}
          </View>
        ) : null}
        {listFooter !== undefined ? (
          <View
            testID="native-list-footer-slot"
            style={{ paddingHorizontal: padding }}
          >
            {listFooter}
          </View>
        ) : null}
      </View>
    );
    const bandWidth = sidecarLayout
      ? Math.min(viewportWidth, sidecarLayout.maxContentWidth)
      : 0;
    const sidecarMargin = Math.max(0, (viewportWidth - bandWidth) / 2);
    const leftWidth = Math.max(
      0,
      bandWidth - (sidecarLayout?.width ?? 0) - (sidecarLayout?.gap ?? 0)
    );
    const scrollContent = sidecarLayout ? (
      <View
        ref={setBoundsRef}
        style={{ position: 'relative', minHeight: sidecarHeight }}
      >
        <View style={{ marginStart: sidecarMargin, width: leftWidth }}>
          {content}
        </View>
        <View
          style={{
            position: 'absolute',
            end: sidecarMargin,
            top: 0,
            width: sidecarLayout.width,
          }}
          onLayout={(event) =>
            setSidecarHeight(event.nativeEvent.layout.height)
          }
        >
          {listSidecar}
        </View>
      </View>
    ) : (
      content
    );
    return (
      <View
        {...viewProps}
        onLayout={(event) => {
          setViewportWidth(event.nativeEvent.layout.width);
          viewProps.onLayout?.(event);
        }}
        style={[viewProps.style, { position: 'relative', minHeight: 0 }]}
      >
        {webScrollHost === undefined ? (
          <View
            ref={setScrollHostRef}
            style={{ position: 'absolute', inset: 0, overflow: 'scroll' }}
          >
            {scrollContent}
          </View>
        ) : (
          scrollContent
        )}
      </View>
    );
  }
);
