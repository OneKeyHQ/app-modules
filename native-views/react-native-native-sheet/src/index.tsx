import {
  createContext,
  type ReactNode,
  type PropsWithChildren,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
  useSyncExternalStore,
} from 'react';

import {
  type ColorValue,
  type NativeSyntheticEvent,
  type LayoutChangeEvent,
  StyleSheet,
  useWindowDimensions,
  View,
} from 'react-native';

import RNCNativeSheet, {
  type NativeSheetDismissEvent,
  type NativeSheetPresentedEvent,
} from './NativeSheetNativeComponent';
import {
  resolveNativeSheetHeight,
  resolveNativeSheetLayoutConstraints,
} from './NativeSheetHeight';
import {
  addNativeSheetRegistryEntry,
  closeNativeSheetRegistryEntry,
  finishNativeSheetRegistryEntry,
  getNativeSheetRegistrySnapshot,
  markNativeSheetRegistryEntryPresentationRequested,
  setNativeSheetRegistryBlocked,
  subscribeNativeSheetRegistry,
} from './NativeSheetRegistry';

export type NativeSheetDismissReason =
  | 'back'
  | 'backdrop'
  | 'pan'
  | 'programmatic'
  | 'security'
  | 'system';

export interface NativeSheetProps extends PropsWithChildren {
  /** Controls whether the sheet is presented. */
  open: boolean;
  /**
   * Outer height in points/dp. Changes while open animate the native surface
   * from its bottom edge. When omitted, settled content-size changes update the
   * native height automatically.
   */
  height?: number;
  /** Maximum auto-fit height. Defaults to 92% of the current window height. */
  maxHeight?: number;
  onDismiss?: (reason: NativeSheetDismissReason) => void;
  onPresented?: (height: number) => void;
  onAnimationComplete?: (info: NativeSheetAnimationInfo) => void;
  /** Matches the React Native Sheet callback for controlled open state. */
  onOpenChange?: (open: boolean) => void;
  /** Matches the React Native Sheet drag-dismiss option. */
  dismissOnSnapToBottom?: boolean;
  /** Disables the native drag gesture. */
  disableDrag?: boolean;
  /** @deprecated Use dismissOnSnapToBottom. */
  dismissOnPanDown?: boolean;
  /** Matches the React Native Sheet overlay-dismiss option. */
  dismissOnOverlayPress?: boolean;
  /** @deprecated Use dismissOnOverlayPress. */
  dismissOnBackdropPress?: boolean;
  dismissOnBackPress?: boolean;
  showHandle?: boolean;
  cornerRadius?: number;
  dimAmount?: number;
  backgroundColor?: ColorValue;
  testID?: string;
}

export type NativeSheetAnimationInfo = Readonly<{ open: boolean }>;

export interface NativeSheetShowControls {
  close: () => void;
}

export interface NativeSheetShowOptions
  extends Omit<
    NativeSheetProps,
    'children' | 'open' | 'onDismiss' | 'onOpenChange'
  > {
  renderContent: ReactNode | ((controls: NativeSheetShowControls) => ReactNode);
  onOpenChange?: (open: boolean) => void;
  onDismiss?: (reason: NativeSheetDismissReason) => void;
  onAnimationComplete?: (info: NativeSheetAnimationInfo) => void;
}

export interface NativeSheetShowHandle {
  close: () => void;
}

interface NativeSheetInternalProps extends NativeSheetProps {
  onPresentationRequested?: () => void;
}

const NativeSheetSecurityContext = createContext(false);

/**
 * Blocks every descendant sheet while security UI is visible. Consumers must
 * place this provider above all NativeSheet instances and bind `blocked` to the
 * app-lock state. A blocked transition is dismissed natively without animation.
 */
export function NativeSheetSecurityProvider({
  blocked,
  children,
}: PropsWithChildren<{ blocked: boolean }>) {
  const parentBlocked = useContext(NativeSheetSecurityContext);
  const blockerId = useRef(Symbol('NativeSheetSecurityProvider')).current;
  const effectiveBlocked = parentBlocked || blocked;
  useEffect(() => {
    setNativeSheetRegistryBlocked(blockerId, blocked);
    return () => setNativeSheetRegistryBlocked(blockerId, false);
  }, [blocked, blockerId]);
  return (
    <NativeSheetSecurityContext.Provider value={effectiveBlocked}>
      {children}
    </NativeSheetSecurityContext.Provider>
  );
}

/**
 * Presents arbitrary React Native children inside a native sheet container.
 * The native component is staged off-screen before presentation; once opened,
 * Fabric mounts the same child view into the platform sheet without serializing
 * business content through the native bridge.
 */
function NativeSheetComponent({
  open,
  height,
  maxHeight,
  children,
  onDismiss,
  onPresented,
  onAnimationComplete,
  onOpenChange,
  dismissOnSnapToBottom,
  disableDrag = false,
  dismissOnPanDown,
  dismissOnOverlayPress,
  dismissOnBackdropPress,
  dismissOnBackPress = true,
  showHandle = true,
  cornerRadius = 32,
  dimAmount = 0.4,
  backgroundColor,
  testID,
  onPresentationRequested,
}: NativeSheetInternalProps) {
  const securityBlocked = useContext(NativeSheetSecurityContext);
  const { height: windowHeight, width } = useWindowDimensions();
  const [measurement, setMeasurement] = useState<{
    height: number;
    openCycle: number;
  }>();
  const lockedHeightRef = useRef<number | undefined>(undefined);
  const dismissNotifiedRef = useRef(false);
  const wasOpenRef = useRef(false);
  const openCycleRef = useRef(0);
  const explicitHeightForOpenRef = useRef(false);
  if (open && !wasOpenRef.current) {
    openCycleRef.current += 1;
    dismissNotifiedRef.current = false;
    explicitHeightForOpenRef.current = height !== undefined;
  } else if (open && height !== undefined) {
    explicitHeightForOpenRef.current = true;
  }
  wasOpenRef.current = open;
  const shouldAutoMeasure = !explicitHeightForOpenRef.current;
  const resolvedMaxHeight = maxHeight ?? Math.floor(windowHeight * 0.92);
  if (open) {
    lockedHeightRef.current = resolveNativeSheetHeight({
      currentHeight: lockedHeightRef.current,
      explicitHeight: height,
      measuredHeight:
        measurement?.openCycle === openCycleRef.current
          ? measurement.height
          : undefined,
      maxHeight: resolvedMaxHeight,
      shouldAutoMeasure,
    });
  }
  const lockedHeight = lockedHeightRef.current;
  const resolvedDismissOnPanDown =
    !disableDrag && (dismissOnSnapToBottom ?? dismissOnPanDown ?? true);
  const resolvedDismissOnBackdropPress =
    dismissOnOverlayPress ?? dismissOnBackdropPress ?? false;
  const layoutConstraints = useMemo(
    () =>
      resolveNativeSheetLayoutConstraints({
        lockedHeight,
        maxHeight: resolvedMaxHeight,
        shouldAutoMeasure,
      }),
    [lockedHeight, resolvedMaxHeight, shouldAutoMeasure]
  );

  const notifyDismiss = useCallback(
    (reason: NativeSheetDismissReason) => {
      if (dismissNotifiedRef.current) {
        return;
      }
      dismissNotifiedRef.current = true;
      lockedHeightRef.current = undefined;
      onOpenChange?.(false);
      onDismiss?.(reason);
      onAnimationComplete?.({ open: false });
    },
    [onAnimationComplete, onDismiss, onOpenChange]
  );
  const handleDismiss = useCallback(
    (event: NativeSyntheticEvent<NativeSheetDismissEvent>) => {
      notifyDismiss(event.nativeEvent.reason as NativeSheetDismissReason);
    },
    [notifyDismiss]
  );
  useEffect(() => {
    if (!securityBlocked || !open) {
      return;
    }
    // Native normally acknowledges the no-animation security dismissal. This
    // fallback also covers an open request blocked before native presentation.
    const timer = setTimeout(() => notifyDismiss('security'), 500);
    return () => clearTimeout(timer);
  }, [notifyDismiss, open, securityBlocked]);
  const handlePresented = useCallback(
    (event: NativeSyntheticEvent<NativeSheetPresentedEvent>) => {
      onPresentationRequested?.();
      onPresented?.(event.nativeEvent.height);
      onAnimationComplete?.({ open: true });
    },
    [onAnimationComplete, onPresentationRequested, onPresented]
  );
  const handleContentLayout = useCallback(
    (event: LayoutChangeEvent) => {
      if (!open || !shouldAutoMeasure) {
        return;
      }
      const nextHeight = Math.ceil(event.nativeEvent.layout.height);
      if (nextHeight > 0) {
        const nextMeasurement = {
          height: Math.min(nextHeight, resolvedMaxHeight),
          openCycle: openCycleRef.current,
        };
        setMeasurement((currentMeasurement) => {
          if (
            currentMeasurement?.height === nextMeasurement.height &&
            currentMeasurement.openCycle === nextMeasurement.openCycle
          ) {
            return currentMeasurement;
          }
          return nextMeasurement;
        });
      }
    },
    [open, resolvedMaxHeight, shouldAutoMeasure]
  );
  const hostStyle = useMemo(
    () => [
      styles.stagingHost,
      {
        width,
        ...(layoutConstraints.hostHeight !== undefined
          ? { height: layoutConstraints.hostHeight }
          : { maxHeight: layoutConstraints.hostMaxHeight }),
      },
    ],
    [layoutConstraints, width]
  );

  return (
    <RNCNativeSheet
      testID={testID}
      open={open && Boolean(lockedHeight)}
      sheetHeight={lockedHeight ?? 1}
      securityBlocked={securityBlocked}
      dismissOnPanDown={resolvedDismissOnPanDown}
      dismissOnBackdropPress={resolvedDismissOnBackdropPress}
      dismissOnBackPress={dismissOnBackPress}
      showHandle={showHandle}
      cornerRadius={cornerRadius}
      dimAmount={dimAmount}
      sheetBackgroundColor={backgroundColor}
      onDismiss={handleDismiss}
      onPresented={handlePresented}
      style={hostStyle}
    >
      <View
        key={`open-cycle-${openCycleRef.current}`}
        pointerEvents="auto"
        style={layoutConstraints.shouldFillHost ? styles.content : undefined}
        collapsable={false}
      >
        {shouldAutoMeasure ? (
          <View
            collapsable={false}
            style={styles.autoMeasureContent}
            onLayout={handleContentLayout}
          >
            {children}
          </View>
        ) : (
          children
        )}
      </View>
    </RNCNativeSheet>
  );
}

function showNativeSheet(
  options: NativeSheetShowOptions
): NativeSheetShowHandle {
  let id: number | undefined;
  let closeRequestedBeforeRegistration = false;
  const close = () => {
    if (id === undefined) {
      closeRequestedBeforeRegistration = true;
      return;
    }
    closeNativeSheetRegistryEntry(id);
  };
  const content =
    typeof options.renderContent === 'function'
      ? options.renderContent({ close })
      : options.renderContent;
  id = addNativeSheetRegistryEntry(options, content);
  if (closeRequestedBeforeRegistration) {
    closeNativeSheetRegistryEntry(id);
  }
  return { close };
}

function NativeSheetHostEntry({
  entry,
}: {
  entry: ReturnType<typeof getNativeSheetRegistrySnapshot>[number];
}) {
  const handleOpenChange = useCallback(
    (nextOpen: boolean) => {
      if (!nextOpen) {
        closeNativeSheetRegistryEntry(entry.id);
      }
    },
    [entry.id]
  );
  const handleDismiss = useCallback(
    (reason: NativeSheetDismissReason) =>
      finishNativeSheetRegistryEntry(entry.id, reason),
    [entry.id]
  );
  const handlePresentationRequested = useCallback(
    () => markNativeSheetRegistryEntryPresentationRequested(entry.id),
    [entry.id]
  );
  return (
    <NativeSheetComponent
      {...entry.options}
      open={entry.open}
      onOpenChange={handleOpenChange}
      onPresented={entry.options.onPresented}
      onPresentationRequested={handlePresentationRequested}
      onDismiss={handleDismiss}
    >
      {entry.content}
    </NativeSheetComponent>
  );
}

export function NativeSheetHost() {
  const entries = useSyncExternalStore(
    subscribeNativeSheetRegistry,
    getNativeSheetRegistrySnapshot,
    getNativeSheetRegistrySnapshot
  );
  return (
    <>
      {entries.map((entry) => (
        <NativeSheetHostEntry key={entry.id} entry={entry} />
      ))}
    </>
  );
}

export const NativeSheet: ((props: NativeSheetProps) => ReactNode) & {
  show: typeof showNativeSheet;
} = Object.assign(NativeSheetComponent, { show: showNativeSheet });

const styles = StyleSheet.create({
  stagingHost: {
    position: 'absolute',
    left: -100_000,
    bottom: 0,
    opacity: 0,
  },
  content: {
    flex: 1,
  },
  autoMeasureContent: {
    alignSelf: 'stretch',
    flexShrink: 0,
  },
});

export type {
  NativeSheetDismissEvent,
  NativeSheetNativeProps,
  NativeSheetPresentedEvent,
} from './NativeSheetNativeComponent';
