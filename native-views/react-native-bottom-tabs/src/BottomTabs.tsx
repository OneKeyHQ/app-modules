import React, { useLayoutEffect, useRef } from "react";
import type { NativeSyntheticEvent } from "react-native";
import {
  type ColorValue,
  type DimensionValue,
  Image,
  type ImageSourcePropType,
  Platform,
  type StyleProp,
  StyleSheet,
  View,
  type ViewStyle,
  processColor,
} from "react-native";
import { BottomTabBarHeightContext } from "./utils/BottomTabBarHeightContext";

// eslint-disable-next-line @react-native/no-deep-imports
import type { ImageSource } from "react-native/Libraries/Image/ImageSource";
import NativeBottomTabs, {
  type BottomTabsItems,
} from "./BottomTabsNativeComponent";
import useLatestCallback from "use-latest-callback";
import type {
  AppleIcon,
  BaseRoute,
  NavigationState,
  TabBarPlacement,
  TabRole,
} from "./types";
import DelayedFreeze from "./DelayedFreeze";
import NativeBottomTabsDrawer from "./BottomTabsDrawerNativeComponent";
import {
  BottomAccessoryView,
  type RNBottomTabsAccessoryViewProps,
} from "./BottomAccessoryView";

const isAppleSymbol = (icon: any): icon is { sfSymbol: string } =>
  icon?.sfSymbol;

interface Props<Route extends BaseRoute> {
  /*
   * Whether to show labels in tabs. When false, only icons will be displayed.
   */
  labeled?: boolean;
  /** Optional physical-left menu; all motion and input are owned by native. */
  renderDrawerContent?: () => React.ReactNode;
  /** External drawer target. Mirror onDrawerOpenChange for user transitions. */
  drawerOpen?: boolean;
  /** Enables root-screen content swipes after inner horizontal views reach their boundary. */
  drawerSwipeEnabled?: boolean;
  /** Reports a completed user target so the external open state can follow it. */
  onDrawerOpenChange?: (open: boolean) => void;
  /**
   * A tab bar style that adapts to each platform.
   *
   * Tab views using the sidebar adaptable style have an appearance
   * that varies depending on the platform:
   * - iPadOS displays a top tab bar that can adapt into a sidebar.
   * - iOS displays a bottom tab bar.
   * - macOS and tvOS always show a sidebar.
   * - visionOS shows an ornament and also shows a sidebar for secondary tabs within a `TabSection`.
   */
  sidebarAdaptable?: boolean;
  /**
   * Whether to disable page animations between tabs. (iOS only) Defaults to `false`.
   */
  disablePageAnimations?: boolean;
  /**
   * Whether to enable haptic feedback. Defaults to `false`.
   */
  hapticFeedbackEnabled?: boolean;
  /**
   * Describes the appearance attributes for the tabBar to use when an observable scroll view is scrolled to the bottom. (iOS only)
   */
  scrollEdgeAppearance?: "default" | "opaque" | "transparent";

  /**
   * Behavior for minimizing the tab bar. (iOS 26+)
   */
  minimizeBehavior?: "automatic" | "onScrollDown" | "onScrollUp" | "never";
  /**
   * Active tab color.
   */
  tabBarActiveTintColor?: ColorValue;
  /**
   * Inactive tab color.
   */
  tabBarInactiveTintColor?: ColorValue;
  /**
   * State for the tab view.
   */
  navigationState: NavigationState<Route>;
  /**
   * Function which takes an object with the route and returns a React element.
   */
  renderScene: (props: {
    route: Route;
    jumpTo: (key: string) => void;
  }) => React.ReactNode | null;
  /**
   * Function which takes a route and returns a placeholder for an unloaded lazy scene.
   */
  renderLazyPlaceholder?: (props: { route: Route }) => React.ReactNode | null;
  /**
   * Callback which is called on tab change, receives the index of the new tab as argument.
   */
  onIndexChange: (index: number) => boolean | void;
  /**
   * Callback which is called when the same tab is pressed twice within the
   * platform double-tap timeout, receives the index of the tab as argument.
   * Each press still goes through `onIndexChange` / reselection first.
   */
  onTabDoublePress?: (index: number) => void;
  /**
   * Get lazy for the current screen. Uses true by default.
   */
  getLazy?: (props: { route: Route }) => boolean | undefined;
  /**
   * Get label text for the tab, uses `route.title` by default.
   */
  getLabelText?: (props: { route: Route }) => string | undefined;
  /**
   * Get badge for the tab, uses `route.badge` by default.
   */
  getBadge?: (props: { route: Route }) => string | undefined;
  /**
   * Get badge background color for the tab. (Android only)
   */
  getBadgeBackgroundColor?: (props: { route: Route }) => ColorValue | undefined;
  /**
   * Get badge text color for the tab. (Android only)
   */
  getBadgeTextColor?: (props: { route: Route }) => ColorValue | undefined;
  /**
   * Get active tint color for the tab.
   */
  getActiveTintColor?: (props: { route: Route }) => ColorValue | undefined;
  /**
   * Determines whether the tab prevents default action on press.
   */
  getPreventsDefault?: (props: { route: Route }) => boolean | undefined;
  /**
   * Get icon for the tab.
   */
  getIcon?: (props: {
    route: Route;
    focused: boolean;
  }) => ImageSource | AppleIcon | undefined | null;

  /**
   * Get hidden for the tab.
   */
  getHidden?: (props: { route: Route }) => boolean | undefined;

  /**
   * Get testID for the tab.
   */
  getTestID?: (props: { route: Route }) => string | undefined;

  /**
   * Get role for the tab. (iOS only)
   */
  getRole?: (props: { route: Route }) => TabRole | undefined;

  /**
   * Custom tab bar to render.
   */
  tabBar?: () => React.ReactNode;

  /**
   * Get freezeOnBlur for the current screen.
   */
  getFreezeOnBlur?: (props: { route: Route }) => boolean | undefined;

  /**
   * Get style for the scene.
   */
  getSceneStyle?: (props: { route: Route }) => StyleProp<ViewStyle>;

  tabBarStyle?: {
    backgroundColor?: ColorValue;
  };

  /**
   * A Boolean value that indicates whether the tab bar is translucent. (iOS only)
   */
  translucent?: boolean;
  /**
   * Live background blur, translucent tint, and a fade above the native Android
   * tab bar. Defaults to true on Android 16+ (API 36). Explicit true supports
   * Android 12+; older versions retain the standard bar. Ignored on iOS.
   * Pad scroll content with useBottomTabBarHeight when enabled.
   */
  androidBottomBlurEnabled?: boolean;
  rippleColor?: ColorValue;
  /**
   * Color of tab indicator. (Android only)
   */
  activeIndicatorColor?: ColorValue;
  tabLabelStyle?: {
    fontFamily?: string;
    fontWeight?: string;
    fontSize?: number;
  };
  /**
   * A function that returns a React element to display as bottom accessory view.
   * iOS 26+ only.
   */
  renderBottomAccessoryView?: RNBottomTabsAccessoryViewProps["renderBottomAccessoryView"];
  /**
   * Whether the tab bar is hidden.
   */
  tabBarHidden?: boolean;
  /**
   * Whether to ignore bottom system insets (navigation bar). Android only.
   */
  ignoreBottomInsets?: boolean;
  /**
   * Visual style of the Android tab bar. Android only.
   */
  androidTabBarStyle?: "default" | "floating";
  /**
   * Whether the tab bar ignores the soft keyboard. Defaults to true, so the
   * keyboard covers the tab bar as on iOS. Set to false to pad the tab bar by
   * the keyboard height so it rises above the keyboard. Android only.
   */
  ignoreKeyboardInsets?: boolean;
  /**
   * Whether to delay freeze/unfreeze by 200ms on tab switch.
   * Defaults to false (immediate freeze). Set to true to restore the
   * original delayed behavior.
   */
  delayedFreeze?: boolean;
  /**
   * Called when the system moves the tab bar between the bottom edge and a
   * vertical bar on the leading or trailing edge (iPhone Duo). iOS only.
   */
  onTabBarPlacementChange?: (placement: TabBarPlacement) => void;
  /**
   * Icon of a round button shown above a vertical tab bar. The button only
   * appears while the tab bar is vertical and visible. iOS only.
   */
  sideAccessoryIcon?: ImageSourcePropType;
  /**
   * Accessibility label of the side accessory button.
   */
  sideAccessoryLabel?: string;
  /**
   * Called when the side accessory button is pressed.
   */
  onSideAccessoryPress?: () => void;
}

const ANDROID_MAX_TABS = 100;

const BottomTabs = <Route extends BaseRoute>({
  navigationState,
  renderScene,
  renderLazyPlaceholder,
  onIndexChange,
  onTabDoublePress,
  renderDrawerContent,
  drawerOpen = false,
  drawerSwipeEnabled = false,
  onDrawerOpenChange,
  rippleColor,
  tabBarActiveTintColor: activeTintColor,
  tabBarInactiveTintColor: inactiveTintColor,
  getBadge = ({ route }: { route: Route }) => route.badge,
  getBadgeBackgroundColor = ({ route }: { route: Route }) =>
    route.badgeBackgroundColor,
  getBadgeTextColor = ({ route }: { route: Route }) => route.badgeTextColor,
  getLazy = ({ route }: { route: Route }) => route.lazy,
  getLabelText = ({ route }: { route: Route }) => route.title,
  getIcon = ({ route, focused }: { route: Route; focused: boolean }) =>
    route.unfocusedIcon
      ? focused
        ? route.focusedIcon
        : route.unfocusedIcon
      : route.focusedIcon,
  getHidden = ({ route }: { route: Route }) => route.hidden,
  getActiveTintColor = ({ route }: { route: Route }) => route.activeTintColor,
  getTestID = ({ route }: { route: Route }) => route.testID,
  getRole = ({ route }: { route: Route }) => route.role,
  getSceneStyle = ({ route }: { route: Route }) => route.style,
  getPreventsDefault = ({ route }: { route: Route }) => route.preventsDefault,
  hapticFeedbackEnabled = false,
  labeled = Platform.OS !== "android" ? true : undefined,
  getFreezeOnBlur = ({ route }: { route: Route }) => route.freezeOnBlur,
  tabBar: renderCustomTabBar,
  tabBarStyle,
  tabLabelStyle,
  renderBottomAccessoryView,
  activeIndicatorColor,
  androidBottomBlurEnabled = Platform.OS === "android" &&
    Number(Platform.Version) >= 36,
  delayedFreeze = false,
  onTabBarPlacementChange,
  sideAccessoryIcon,
  sideAccessoryLabel,
  onSideAccessoryPress,
  ...props
}: Props<Route>) => {
  // @ts-ignore
  const focusedKey = navigationState.routes[navigationState.index].key;
  const customTabBarWrapperRef = useRef<View>(null) as React.RefObject<any>;
  const nativeBottomTabsRef =
    useRef<React.ElementRef<typeof NativeBottomTabs>>(null);
  const [nativeTabBarHeight, setNativeTabBarHeight] = React.useState(0);
  const [customTabBarHeight, setCustomTabBarHeight] = React.useState(0);
  const tabBarHeight = renderCustomTabBar
    ? customTabBarHeight
    : props.tabBarHidden
    ? 0
    : nativeTabBarHeight;
  const [measuredDimensions, setMeasuredDimensions] = React.useState<
    { width: DimensionValue; height: DimensionValue } | undefined
  >({ width: "100%", height: "100%" });

  const trimmedRoutes = React.useMemo(() => {
    if (
      Platform.OS === "android" &&
      navigationState.routes.length > ANDROID_MAX_TABS
    ) {
      console.warn(
        `BottomTabs only supports up to ${ANDROID_MAX_TABS} tabs on Android`
      );
      return navigationState.routes.slice(0, ANDROID_MAX_TABS);
    }
    return navigationState.routes;
  }, [navigationState.routes]);

  const [loaded, setLoaded] = React.useState<string[]>([focusedKey]);

  if (!loaded.includes(focusedKey)) {
    setLoaded((loaded) => [...loaded, focusedKey]);
  }

  const isIOS = Platform.OS === "ios";

  // Unfocused (outline) icons — independent of focusedKey so the array stays
  // stable across tab switches and the native side never reloads on switch.
  const unfocusedIcons = React.useMemo(
    () => trimmedRoutes.map((route) => getIcon({ route, focused: false })),
    [getIcon, trimmedRoutes]
  );
  // Focused (solid) icons — also stable; handed to iOS as selectedImage.
  const focusedIcons = React.useMemo(
    () => trimmedRoutes.map((route) => getIcon({ route, focused: true })),
    [getIcon, trimmedRoutes]
  );
  // Legacy single array driven by focusedKey. Android still swaps the icon in
  // JS (it has no selectedImage equivalent).
  const focusKeyedIcons = React.useMemo(
    () =>
      trimmedRoutes.map((route) =>
        getIcon({
          route,
          focused: route.key === focusedKey,
        })
      ),
    [focusedKey, getIcon, trimmedRoutes]
  );

  // iOS hands both states to UIKit (image + selectedImage) so the focused
  // artwork swaps natively, in sync with the selection animation, instead of
  // waiting for a JS round-trip. Android keeps the focusedKey-driven array.
  const icons = isIOS ? unfocusedIcons : focusKeyedIcons;

  const items: BottomTabsItems[number][] = React.useMemo(
    () =>
      trimmedRoutes.map((route, index) => {
        const icon = icons[index];
        const isSfSymbol = isAppleSymbol(icon);

        if (Platform.OS === "android" && isSfSymbol) {
          console.warn(
            "SF Symbols are not supported on Android. Use require() or pass uri to load an image instead."
          );
        }

        const color = processColor(getActiveTintColor({ route }));
        const badgeBgColor = processColor(getBadgeBackgroundColor?.({ route }));
        const badgeTxtColor = processColor(getBadgeTextColor?.({ route }));

        return {
          key: route.key,
          title: getLabelText({ route }) ?? route.key,
          sfSymbol: isSfSymbol ? icon.sfSymbol : undefined,
          badge: getBadge?.({ route }),
          badgeBackgroundColor:
            typeof badgeBgColor === "number" ? badgeBgColor : undefined,
          badgeTextColor:
            typeof badgeTxtColor === "number" ? badgeTxtColor : undefined,
          activeTintColor: typeof color === "number" ? color : undefined,
          hidden: getHidden?.({ route }),
          testID: getTestID?.({ route }),
          role: getRole?.({ route }),
          preventsDefault: getPreventsDefault?.({ route }),
        };
      }),
    [
      trimmedRoutes,
      icons,
      getLabelText,
      getBadge,
      getBadgeBackgroundColor,
      getBadgeTextColor,
      getActiveTintColor,
      getHidden,
      getTestID,
      getRole,
      getPreventsDefault,
    ]
  );

  const resolvedIconAssets = React.useMemo(
    () =>
      icons.map((icon) => {
        if (icon && !isAppleSymbol(icon)) {
          // @ts-ignore - resolveAssetSource accepts ImageSourcePropType
          const resolved = Image.resolveAssetSource(icon);
          return {
            uri: resolved?.uri ?? "",
            width: resolved?.width ?? 0,
            height: resolved?.height ?? 0,
            scale: resolved?.scale ?? 1,
          };
        }
        return { uri: "", width: 0, height: 0, scale: 1 };
      }),
    [icons]
  );

  // Focused (solid) assets passed to iOS as selectedImage. iOS only — Android
  // ignores selectedIcons and keeps swapping via the focusedKey array above.
  const resolvedSelectedIconAssets = React.useMemo(
    () =>
      focusedIcons.map((icon) => {
        if (icon && !isAppleSymbol(icon)) {
          // @ts-ignore - resolveAssetSource accepts ImageSourcePropType
          const resolved = Image.resolveAssetSource(icon);
          return {
            uri: resolved?.uri ?? "",
            width: resolved?.width ?? 0,
            height: resolved?.height ?? 0,
            scale: resolved?.scale ?? 1,
          };
        }
        return { uri: "", width: 0, height: 0, scale: 1 };
      }),
    [focusedIcons]
  );

  const jumpTo = useLatestCallback((key: string) => {
    const index = trimmedRoutes.findIndex((route) => route.key === key);
    if (index === -1) {
      return;
    }
    return onIndexChange(index);
  });

  const handleTabDoublePress = React.useCallback(
    (event: NativeSyntheticEvent<{ key: string }>) => {
      const { key } = event.nativeEvent;
      const index = trimmedRoutes.findIndex((route) => route.key === key);
      if (index !== -1) {
        onTabDoublePress?.(index);
      }
    },
    [trimmedRoutes, onTabDoublePress]
  );

  const handlePageSelected = React.useCallback(
    (event: NativeSyntheticEvent<{ key: string }>) => {
      const { key } = event.nativeEvent;
      const accepted = jumpTo(key);
      if (accepted === false) {
        nativeBottomTabsRef.current?.setNativeProps({
          selectedPage: focusedKey,
        });
      }
    },
    [focusedKey, jumpTo]
  );

  const handleTabBarMeasured = React.useCallback(
    (event: NativeSyntheticEvent<{ height: number }>) => {
      setNativeTabBarHeight(event.nativeEvent.height);
    },
    [setNativeTabBarHeight]
  );

  const handleNativeLayout = React.useCallback(
    (event: NativeSyntheticEvent<{ width: number; height: number }>) => {
      const { width, height } = event.nativeEvent;
      setMeasuredDimensions({ width, height });
    },
    [setMeasuredDimensions]
  );

  const handleTabBarPlacementChange = React.useCallback(
    (event: NativeSyntheticEvent<{ placement: string }>) => {
      onTabBarPlacementChange?.(event.nativeEvent.placement as TabBarPlacement);
    },
    [onTabBarPlacementChange]
  );

  const handleSideAccessoryPress = React.useCallback(() => {
    onSideAccessoryPress?.();
  }, [onSideAccessoryPress]);

  const resolvedSideAccessoryIcon = React.useMemo(() => {
    if (!isIOS || !sideAccessoryIcon) {
      return undefined;
    }
    // @ts-ignore - resolveAssetSource accepts ImageSourcePropType
    const resolved = Image.resolveAssetSource(sideAccessoryIcon);
    return resolved?.uri
      ? {
          uri: resolved.uri,
          width: resolved.width ?? 0,
          height: resolved.height ?? 0,
          scale: resolved.scale ?? 1,
        }
      : undefined;
  }, [sideAccessoryIcon]);

  useLayoutEffect(() => {
    if (renderCustomTabBar && customTabBarWrapperRef.current) {
      customTabBarWrapperRef.current.measure(
        (_x: number, _y: number, _width: number, height: number) => {
          setCustomTabBarHeight(height);
        }
      );
    }
  }, [renderCustomTabBar]);

  return (
    <BottomTabBarHeightContext.Provider value={tabBarHeight}>
      <NativeBottomTabsDrawer
        style={styles.fullWidth}
        enabled={!!renderDrawerContent}
        open={drawerOpen}
        swipeEnabled={drawerSwipeEnabled}
        hapticFeedbackEnabled={hapticFeedbackEnabled}
        bottomBarHeight={tabBarHeight ?? 0}
        onDrawerOpenChange={(event) =>
          onDrawerOpenChange?.(event.nativeEvent.open)
        }
      >
        <View collapsable={false} style={styles.drawerMenu}>
          {Platform.OS !== "web" ? renderDrawerContent?.() : null}
        </View>
        <View collapsable={false} style={styles.fullWidth}>
          <NativeBottomTabs
            ref={nativeBottomTabsRef}
            {...props}
            {...tabLabelStyle}
            style={styles.fullWidth}
            items={items}
            icons={renderCustomTabBar ? undefined : resolvedIconAssets}
            selectedIcons={
              isIOS && !renderCustomTabBar
                ? resolvedSelectedIconAssets
                : undefined
            }
            selectedPage={focusedKey}
            tabBarHidden={props.tabBarHidden ?? !!renderCustomTabBar}
            onTabDoublePress={handleTabDoublePress}
            onPageSelected={handlePageSelected}
            onTabBarMeasured={handleTabBarMeasured}
            onNativeLayout={handleNativeLayout}
            onTabBarPlacementChange={handleTabBarPlacementChange}
            sideAccessoryIcon={
              renderCustomTabBar ? undefined : resolvedSideAccessoryIcon
            }
            sideAccessoryLabel={sideAccessoryLabel}
            onSideAccessoryPress={handleSideAccessoryPress}
            hapticFeedbackEnabled={hapticFeedbackEnabled}
            activeTintColor={activeTintColor}
            inactiveTintColor={inactiveTintColor}
            barTintColor={tabBarStyle?.backgroundColor}
            rippleColor={rippleColor}
            activeIndicatorColor={activeIndicatorColor}
            androidBottomBlurEnabled={
              Platform.OS === "android" &&
              !renderCustomTabBar &&
              androidBottomBlurEnabled
            }
            labeled={labeled}
          >
            {trimmedRoutes.map((route) => {
              if (getLazy({ route }) !== false && !loaded.includes(route.key)) {
                return (
                  <View
                    key={route.key}
                    collapsable={false}
                    style={styles.fullWidth}
                  >
                    {renderLazyPlaceholder?.({ route })}
                  </View>
                );
              }

              const focused = route.key === focusedKey;
              const freeze = !focused ? getFreezeOnBlur({ route }) : false;

              const customStyle = getSceneStyle({ route });

              return (
                <View
                  key={route.key}
                  style={[
                    styles.screen,
                    renderCustomTabBar ? styles.fullWidth : measuredDimensions,
                    customStyle,
                  ]}
                  collapsable={false}
                  pointerEvents={focused ? "auto" : "none"}
                  accessibilityElementsHidden={!focused}
                  importantForAccessibility={
                    focused ? "auto" : "no-hide-descendants"
                  }
                >
                  <DelayedFreeze
                    freeze={!!freeze}
                    delayedFreeze={delayedFreeze}
                  >
                    {renderScene({
                      route,
                      jumpTo,
                    })}
                  </DelayedFreeze>
                </View>
              );
            })}
            {Platform.OS === "ios" &&
            parseFloat(Platform.Version) >= 26 &&
            renderBottomAccessoryView &&
            !renderCustomTabBar ? (
              <BottomAccessoryView
                renderBottomAccessoryView={renderBottomAccessoryView}
              />
            ) : null}
          </NativeBottomTabs>
          {renderCustomTabBar ? (
            <View
              ref={customTabBarWrapperRef}
              onLayout={(event) =>
                setCustomTabBarHeight(event.nativeEvent.layout.height)
              }
            >
              {renderCustomTabBar()}
            </View>
          ) : null}
        </View>
      </NativeBottomTabsDrawer>
    </BottomTabBarHeightContext.Provider>
  );
};

const styles = StyleSheet.create({
  fullWidth: {
    width: "100%",
    height: "100%",
    flex: 1,
  },
  drawerMenu: {
    position: "absolute",
    width: "78%",
    height: "100%",
  },
  screen: {
    position: "absolute",
  },
});

export default BottomTabs;
