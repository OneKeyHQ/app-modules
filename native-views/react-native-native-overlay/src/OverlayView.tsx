import { useCallback, useMemo, useState } from 'react';

import {
  Pressable,
  StatusBar,
  StyleSheet,
  View,
  useWindowDimensions,
} from 'react-native';

import NativeOverlay from './NativeOverlayNativeComponent';
import { OverlayLevelProvider } from './OverlayLevelContext';
import { isBlockingLevel } from './OverlayLevels';
import { resolveSheetWidth } from './sheetWidth';
import { useOverlayController } from './useOverlayController';

import type {
  IOverlayRequestDismissReason,
  IOverlayViewProps,
} from './OverlayViewTypes';
import type { LayoutChangeEvent, NativeSyntheticEvent } from 'react-native';

const DEFAULT_BACKDROP_COLOR = 'rgba(0, 0, 0, 0.4)';
const DEFAULT_SHEET_MAX_RATIO = 0.92;
const DEFAULT_SHEET_CORNER_RADIUS = 24;
// Taller than any bottom safe area; the sheet clips the rest.
const SHEET_SURFACE_TAIL_HEIGHT = 120;

const REQUEST_REASONS: Record<string, IOverlayRequestDismissReason> = {
  back: 'back',
  backdrop: 'backdrop',
  pan: 'pan',
};

const styles = StyleSheet.create({
  // The host view never draws: native moves its child into the level host
  // and clips it (zero width) while hidden. The height stays auto on
  // purpose; a fixed 0 makes Yoga measure a fitted sheet's content at most
  // 0 tall, so the sheet would never learn its height.
  host: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: 0,
  },
  sheetMarginLeft: { position: 'absolute', top: 0, bottom: 0, left: 0 },
  sheetMarginRight: { position: 'absolute', top: 0, bottom: 0, right: 0 },
  sheetSurface: { borderCurve: 'continuous', overflow: 'hidden', flexGrow: 1 },
  // The platform sheet extends below the content (home indicator area); the
  // drawn surface continues into it so no backdrop shows under the content.
  sheetSurfaceTail: {
    position: 'absolute',
    top: '100%',
    height: SHEET_SURFACE_TAIL_HEIGHT,
  },
});

export function OverlayView(props: IOverlayViewProps) {
  const {
    level = 'modal',
    presentation = 'center',
    backdrop,
    blocking,
    dismissOnBackPress,
    sheet,
    keepContentMounted = false,
    statusBarStyle,
    children,
    testID,
  } = props;
  const isSheet = presentation === 'sheet';
  const controller = useOverlayController(props);
  const { width, height } = useWindowDimensions();
  const {
    entry,
    mounted,
    presented,
    ownsStatusBar,
    scope,
    hostKey,
    ownerKey,
    animation,
    onHostPresented,
    onHostDismissed,
    onHostRequestDismiss,
  } = controller;

  const [measuredSheetHeight, setMeasuredSheetHeight] = useState(0);
  // iOS sizes a sheet itself (narrower than the window on wide windows), so
  // the content follows the width native reports; Android sheets span the
  // window and never report.
  const [reportedSheetWidth, setReportedSheetWidth] = useState<number>();
  const { containerWidth: sheetContainerWidth, inset: sheetInset } =
    resolveSheetWidth({
      windowWidth: width,
      reportedWidth: reportedSheetWidth,
      maxWidth: sheet?.maxWidth,
    });
  // `maxWidth` turns on the drawn surface even while it spans the whole sheet,
  // so a width change never swaps who paints the background.
  const hasSheetSurface = isSheet && sheet?.maxWidth !== undefined;
  const dismissOnBackdropPress = backdrop ? !!backdrop.dismissOnPress : false;
  const onSheetMarginPress = useCallback(() => {
    // The margins beside a max-width surface stand in for the backdrop.
    if (dismissOnBackdropPress) onHostRequestDismiss('backdrop');
  }, [dismissOnBackdropPress, onHostRequestDismiss]);
  const onSheetLayoutReport = useCallback(
    (event: NativeSyntheticEvent<{ width: number }>) => {
      setReportedSheetWidth(event.nativeEvent.width);
    },
    []
  );
  const maxSheetHeight = sheet?.maxHeight ?? height * DEFAULT_SHEET_MAX_RATIO;
  const sheetHeight = Math.min(
    sheet?.height ?? measuredSheetHeight,
    maxSheetHeight
  );
  const onSheetLayout = useCallback((event: LayoutChangeEvent) => {
    setMeasuredSheetHeight(event.nativeEvent.layout.height);
  }, []);

  const animationConfig = useMemo(() => JSON.stringify(animation), [animation]);
  const isBlocking = blocking ?? isBlockingLevel(level);

  const onDismissed = useCallback(() => onHostDismissed(), [onHostDismissed]);
  const onRequestDismiss = useCallback(
    (event: NativeSyntheticEvent<{ reason: string }>) => {
      onHostRequestDismiss(REQUEST_REASONS[event.nativeEvent.reason] ?? 'back');
    },
    [onHostRequestDismiss]
  );

  if (!keepContentMounted && (!mounted || !entry)) {
    return null;
  }

  // The app drives the status bar through React Native's StatusBar stack
  // (iOS runs without view-controller-based appearance), so the owning
  // overlay pushes onto the same stack.
  const statusBar =
    ownsStatusBar && statusBarStyle ? (
      <StatusBar
        animated
        barStyle={statusBarStyle === 'light' ? 'light-content' : 'dark-content'}
      />
    ) : null;

  return (
    <>
      {statusBar}
      <NativeOverlay
        style={styles.host}
        // A fitted sheet presents once its content height is known.
        visible={presented && (!isSheet || sheetHeight > 0)}
        level={level}
        scope={scope}
        hostKey={hostKey ?? ''}
        ownerKey={ownerKey ?? ''}
        presentation={presentation}
        stackOrder={entry?.seq ?? 0}
        blocking={isBlocking}
        dismissOnBackPress={dismissOnBackPress ?? isBlocking}
        dismissOnBackdropPress={backdrop ? !!backdrop.dismissOnPress : false}
        backdropColor={
          backdrop ? backdrop.color ?? DEFAULT_BACKDROP_COLOR : undefined
        }
        sheetHeight={isSheet ? sheetHeight : 0}
        sheetCornerRadius={sheet?.cornerRadius}
        showHandle={sheet?.showHandle}
        // A max-width sheet draws its own surface, so the platform sheet
        // stays transparent beside it.
        sheetBackgroundColor={
          hasSheetSurface ? 'transparent' : sheet?.backgroundColor
        }
        dismissOnPanDown={sheet?.dismissOnPanDown ?? true}
        animationConfig={animationConfig}
        onPresented={onHostPresented}
        onDismissed={onDismissed}
        onRequestDismiss={onRequestDismiss}
        onSheetLayout={isSheet ? onSheetLayoutReport : undefined}
        testID={testID}
      >
        {isSheet ? (
          <View
            collapsable={false}
            onLayout={onSheetLayout}
            style={{
              width: sheetContainerWidth,
              paddingHorizontal: sheetInset,
              height: sheet?.height,
              maxHeight: maxSheetHeight,
              // The host is 0x0; without this Yoga shrinks the content to 0 and
              // a fitted sheet never learns its height.
              flexShrink: 0,
            }}
          >
            {hasSheetSurface ? (
              <>
                {sheetInset > 0 ? (
                  <>
                    <Pressable
                      accessible={false}
                      importantForAccessibility="no-hide-descendants"
                      onPress={onSheetMarginPress}
                      style={[styles.sheetMarginLeft, { width: sheetInset }]}
                    />
                    <Pressable
                      accessible={false}
                      importantForAccessibility="no-hide-descendants"
                      onPress={onSheetMarginPress}
                      style={[styles.sheetMarginRight, { width: sheetInset }]}
                    />
                  </>
                ) : null}
                <View
                  style={[
                    styles.sheetSurfaceTail,
                    {
                      left: sheetInset,
                      right: sheetInset,
                      backgroundColor: sheet?.backgroundColor,
                    },
                  ]}
                />
                <View
                  style={[
                    styles.sheetSurface,
                    {
                      backgroundColor: sheet?.backgroundColor,
                      borderTopLeftRadius:
                        sheet?.cornerRadius ?? DEFAULT_SHEET_CORNER_RADIUS,
                      borderTopRightRadius:
                        sheet?.cornerRadius ?? DEFAULT_SHEET_CORNER_RADIUS,
                    },
                  ]}
                >
                  <OverlayLevelProvider value={level}>
                    {children}
                  </OverlayLevelProvider>
                </View>
              </>
            ) : (
              <OverlayLevelProvider value={level}>
                {children}
              </OverlayLevelProvider>
            )}
          </View>
        ) : (
          <View
            collapsable={false}
            pointerEvents="box-none"
            style={{ width, height, flexShrink: 0 }}
          >
            <OverlayLevelProvider value={level}>
              {children}
            </OverlayLevelProvider>
          </View>
        )}
      </NativeOverlay>
    </>
  );
}
