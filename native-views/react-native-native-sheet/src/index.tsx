import {
  createContext,
  type PropsWithChildren,
  useCallback,
  useContext,
} from 'react';

import {
  type ColorValue,
  type NativeSyntheticEvent,
  StyleSheet,
  useWindowDimensions,
  View,
} from 'react-native';

import RNCNativeSheet, {
  type NativeSheetDismissEvent,
  type NativeSheetPresentedEvent,
} from './NativeSheetNativeComponent';

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
   * Fixed outer height for this presentation, in points/dp. Content updates do
   * not change this value, so asynchronously loaded children cannot resize the
   * native sheet mid-transition.
   */
  height: number;
  onDismiss?: (reason: NativeSheetDismissReason) => void;
  onPresented?: (height: number) => void;
  dismissOnPanDown?: boolean;
  dismissOnBackdropPress?: boolean;
  dismissOnBackPress?: boolean;
  showHandle?: boolean;
  cornerRadius?: number;
  dimAmount?: number;
  backgroundColor?: ColorValue;
  testID?: string;
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
  return (
    <NativeSheetSecurityContext.Provider value={blocked}>
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
export function NativeSheet({
  open,
  height,
  children,
  onDismiss,
  onPresented,
  dismissOnPanDown = true,
  dismissOnBackdropPress = false,
  dismissOnBackPress = true,
  showHandle = true,
  cornerRadius = 32,
  dimAmount = 0.4,
  backgroundColor,
  testID,
}: NativeSheetProps) {
  const securityBlocked = useContext(NativeSheetSecurityContext);
  const { width } = useWindowDimensions();

  const handleDismiss = useCallback(
    (event: NativeSyntheticEvent<NativeSheetDismissEvent>) => {
      onDismiss?.(event.nativeEvent.reason as NativeSheetDismissReason);
    },
    [onDismiss]
  );
  const handlePresented = useCallback(
    (event: NativeSyntheticEvent<NativeSheetPresentedEvent>) => {
      onPresented?.(event.nativeEvent.height);
    },
    [onPresented]
  );

  return (
    <RNCNativeSheet
      testID={testID}
      open={open}
      sheetHeight={height}
      securityBlocked={securityBlocked}
      dismissOnPanDown={dismissOnPanDown}
      dismissOnBackdropPress={dismissOnBackdropPress}
      dismissOnBackPress={dismissOnBackPress}
      showHandle={showHandle}
      cornerRadius={cornerRadius}
      dimAmount={dimAmount}
      sheetBackgroundColor={backgroundColor}
      onDismiss={handleDismiss}
      onPresented={handlePresented}
      style={[styles.stagingHost, { width, height }]}
    >
      <View pointerEvents="auto" style={styles.content} collapsable={false}>
        {children}
      </View>
    </RNCNativeSheet>
  );
}

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
});

export type {
  NativeSheetDismissEvent,
  NativeSheetNativeProps,
  NativeSheetPresentedEvent,
} from './NativeSheetNativeComponent';
