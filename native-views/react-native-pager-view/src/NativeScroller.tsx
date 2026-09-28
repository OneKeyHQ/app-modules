import * as React from "react";
import {
  findNodeHandle,
  Keyboard,
  Platform,
  StyleSheet,
  TextInput,
  View,
  type GestureResponderEvent,
  type NativeMethods,
  type RefreshControlProps,
  type ScrollView,
  type ScrollViewProps,
  type ViewProps,
} from "react-native";

import NativeScrollerHost, { Commands } from "./NativeScrollerNativeComponent";

export const NATIVE_SCROLLER_NATIVE_ID_PREFIX =
  "rnc-collapsible-pager-native-scroller";

/** Supported vertical subset; horizontal, snapping, zoom and sticky rows are not supported. */
export interface NativeScrollerProps
  extends ViewProps,
    Pick<
      ScrollViewProps,
      | "contentContainerStyle"
      | "onContentSizeChange"
      | "scrollEnabled"
      | "nestedScrollEnabled"
      | "showsVerticalScrollIndicator"
      | "showsHorizontalScrollIndicator"
      | "bounces"
      | "alwaysBounceVertical"
      | "contentInset"
      | "keyboardDismissMode"
      | "keyboardShouldPersistTaps"
      | "scrollEventThrottle"
      | "decelerationRate"
      | "onScroll"
      | "onScrollBeginDrag"
      | "onScrollEndDrag"
      | "onMomentumScrollBegin"
      | "onMomentumScrollEnd"
    > {
  pagerScrollKey?: string;
  /** iOS only. Android retains AndroidX's native fling physics. */
  decelerationRate?: ScrollViewProps["decelerationRate"];
  /** Standard RefreshControl props are mapped to the independently owned native controller. */
  refreshControl?: React.ReactElement<RefreshControlProps>;
}

type HostRef = React.ElementRef<typeof NativeScrollerHost>;

export interface NativeScrollerRef
  extends NativeMethods,
    Pick<
      React.ElementRef<typeof ScrollView>,
      "scrollTo" | "scrollToEnd" | "flashScrollIndicators"
    > {
  getNativeScrollRef(): HostRef | null;
  getScrollableNode(): number | null;
}

// The RN 0.86 JS registry distinguishes another input from an unhandled tap.
// TextInput.State's public facade does not export this query.
const textInputState =
  require("react-native/Libraries/Components/TextInput/TextInputState")
    .default as {
    isTextInput(target: GestureResponderEvent["target"]): boolean;
  };

const styles = StyleSheet.create({
  viewport: { flexGrow: 1, flexShrink: 1, overflow: "hidden" },
  content: { position: "absolute", top: 0, left: 0, right: 0, flexShrink: 0 },
});

export const NativeScroller = React.forwardRef<
  NativeScrollerRef,
  NativeScrollerProps
>(function NativeScroller(
  {
    children,
    nativeID,
    pagerScrollKey,
    style,
    contentContainerStyle,
    onLayout,
    onContentSizeChange,
    refreshControl,
    nestedScrollEnabled = true,
    contentInset,
    decelerationRate = "normal",
    keyboardShouldPersistTaps = "never",
    onScroll,
    onScrollBeginDrag,
    onMomentumScrollBegin,
    onMomentumScrollEnd,
    onStartShouldSetResponder,
    onStartShouldSetResponderCapture,
    onResponderGrant,
    onResponderRelease,
    onResponderTerminationRequest,
    ...props
  },
  forwardedRef
) {
  const hostRef = React.useRef<HostRef>(null);
  const generatedId = React.useId();
  const [contentViewportHeight, setContentViewportHeight] = React.useState(0);
  const [contentSize, setContentSize] = React.useState({ width: 0, height: 0 });
  const [refreshEpoch, requestRefreshSync] = React.useReducer(
    (value: number) => value + 1,
    0
  );
  const contentSizeRef = React.useRef(contentSize);
  const scrolledSinceGrant = React.useRef(false);
  const momentum = React.useRef(false);
  const grantedDuringMomentum = React.useRef(false);
  const persistTaps =
    keyboardShouldPersistTaps === true
      ? "always"
      : keyboardShouldPersistTaps === false
      ? "never"
      : keyboardShouldPersistTaps;
  const contentStyle = StyleSheet.flatten(contentContainerStyle);
  const refresh = refreshControl?.props;
  const rate =
    typeof decelerationRate === "number"
      ? decelerationRate
      : decelerationRate === "fast"
      ? Platform.OS === "ios"
        ? 0.99
        : 0.9
      : Platform.OS === "ios"
      ? 0.998
      : 0.985;

  React.useEffect(() => {
    if (hostRef.current)
      Commands.setRefreshState(hostRef.current, refresh?.refreshing ?? false);
  }, [refresh?.refreshing, refreshEpoch]);

  React.useImperativeHandle(
    forwardedRef,
    () => ({
      scrollTo(options, x, animated) {
        const host = hostRef.current;
        if (!host) return;
        if (typeof options === "number") {
          Commands.scrollTo(host, x ?? 0, options, animated !== false);
        } else {
          Commands.scrollTo(
            host,
            options?.x ?? 0,
            options?.y ?? 0,
            options?.animated !== false
          );
        }
      },
      scrollToEnd(options) {
        if (hostRef.current)
          Commands.scrollToEnd(hostRef.current, options?.animated !== false);
      },
      flashScrollIndicators() {
        if (hostRef.current) Commands.flashScrollIndicators(hostRef.current);
      },
      getNativeScrollRef: () => hostRef.current,
      getScrollableNode: () => findNodeHandle(hostRef.current) ?? null,
      measure: (...args) => hostRef.current?.measure(...args),
      measureInWindow: (...args) => hostRef.current?.measureInWindow(...args),
      measureLayout: (...args) => hostRef.current?.measureLayout(...args),
      setNativeProps: (nativeProps) =>
        hostRef.current?.setNativeProps(nativeProps),
      focus: () => hostRef.current?.focus(),
      blur: () => hostRef.current?.blur(),
    }),
    []
  );

  const keyboardIsDismissible = () =>
    Boolean(
      TextInput.State.currentlyFocusedInput() &&
        (Keyboard.isVisible() ||
          (Platform.OS === "android" && Number(Platform.Version) < 30)) &&
        Keyboard.metrics()?.height !== 0
    );

  return (
    <NativeScrollerHost
      {...props}
      ref={hostRef}
      nativeID={`${NATIVE_SCROLLER_NATIVE_ID_PREFIX}:${
        pagerScrollKey ?? nativeID ?? generatedId
      }`}
      style={[styles.viewport, style]}
      nestedScrollEnabled={nestedScrollEnabled}
      contentWidth={contentSize.width}
      contentHeight={contentSize.height}
      contentInsetTop={contentInset?.top ?? 0}
      contentInsetRight={contentInset?.right ?? 0}
      contentInsetBottom={contentInset?.bottom ?? 0}
      contentInsetLeft={contentInset?.left ?? 0}
      decelerationRate={rate}
      keyboardShouldPersistTaps={persistTaps}
      refreshEnabled={Boolean(refresh) && refresh?.enabled !== false}
      refreshing={refresh?.refreshing ?? false}
      refreshTintColor={refresh?.tintColor}
      refreshColors={refresh?.colors}
      refreshProgressBackgroundColor={refresh?.progressBackgroundColor}
      refreshProgressViewOffset={refresh?.progressViewOffset ?? 0}
      refreshTitle={refresh?.title}
      refreshTitleColor={refresh?.titleColor}
      refreshSize={refresh?.size === "large" ? "large" : "default"}
      onRefresh={() => {
        try {
          refresh?.onRefresh?.();
        } finally {
          requestRefreshSync();
        }
      }}
      onLayout={onLayout}
      onContentViewportChange={(event) => {
        const height = event.nativeEvent.height;
        if (!Number.isFinite(height) || height < 0) return;
        setContentViewportHeight((previous) =>
          previous === height ? previous : height
        );
      }}
      onScroll={(event) => {
        scrolledSinceGrant.current = true;
        onScroll?.(event);
      }}
      onScrollBeginDrag={(event) => {
        scrolledSinceGrant.current = true;
        onScrollBeginDrag?.(event);
      }}
      onMomentumScrollBegin={(event) => {
        momentum.current = true;
        onMomentumScrollBegin?.(event);
      }}
      onMomentumScrollEnd={(event) => {
        momentum.current = false;
        onMomentumScrollEnd?.(event);
      }}
      onStartShouldSetResponder={(event) =>
        onStartShouldSetResponder?.(event) ??
        (persistTaps === "handled" &&
          keyboardIsDismissible() &&
          event.target !== TextInput.State.currentlyFocusedInput())
      }
      onStartShouldSetResponderCapture={(event) =>
        onStartShouldSetResponderCapture?.(event) ??
        (momentum.current ||
          (persistTaps === "never" &&
            keyboardIsDismissible() &&
            typeof event.target !== "number" &&
            !textInputState.isTextInput(event.target)))
      }
      onResponderGrant={(event) => {
        scrolledSinceGrant.current = false;
        grantedDuringMomentum.current = momentum.current;
        onResponderGrant?.(event);
      }}
      onResponderRelease={(event) => {
        onResponderRelease?.(event);
        if (
          persistTaps !== "always" &&
          keyboardIsDismissible() &&
          event.target !== TextInput.State.currentlyFocusedInput() &&
          !scrolledSinceGrant.current &&
          !grantedDuringMomentum.current
        )
          Keyboard.dismiss();
      }}
      onResponderTerminationRequest={(event) =>
        onResponderTerminationRequest?.(event) ?? true
      }
    >
      <View
        collapsable={false}
        style={[
          contentContainerStyle,
          (contentStyle?.flexGrow ?? 0) > 0 &&
          (contentStyle?.minHeight === undefined ||
            typeof contentStyle.minHeight === "number")
            ? {
                minHeight: Math.max(
                  contentViewportHeight,
                  contentStyle?.minHeight ?? 0
                ),
              }
            : undefined,
          styles.content,
        ]}
        onLayout={(event) => {
          const { width, height } = event.nativeEvent.layout;
          const previous = contentSizeRef.current;
          if (previous.width === width && previous.height === height) return;
          const next = { width, height };
          contentSizeRef.current = next;
          setContentSize(next);
          onContentSizeChange?.(width, height);
        }}
      >
        {children}
      </View>
    </NativeScrollerHost>
  );
});

NativeScroller.displayName = "NativeScroller";
