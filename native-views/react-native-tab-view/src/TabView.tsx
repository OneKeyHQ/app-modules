import React, { useLayoutEffect, useRef } from 'react';
import type { NativeSyntheticEvent } from 'react-native';
import {
  type ColorValue,
  type DimensionValue,
  Image,
  Platform,
  type StyleProp,
  StyleSheet,
  View,
  type ViewStyle,
  processColor,
} from 'react-native';
import { BottomTabBarHeightContext } from './utils/BottomTabBarHeightContext';

// eslint-disable-next-line @react-native/no-deep-imports
import type { ImageSource } from 'react-native/Libraries/Image/ImageSource';
import NativeTabView, {
  type TabViewItems,
} from './TabViewNativeComponent';
import useLatestCallback from 'use-latest-callback';
import type { AppleIcon, BaseRoute, NavigationState, TabRole } from './types';
import DelayedFreeze from './DelayedFreeze';
import {
  BottomAccessoryView,
  type BottomAccessoryViewProps,
} from './BottomAccessoryView';

const isAppleSymbol = (icon: any): icon is { sfSymbol: string } =>
  icon?.sfSymbol;

interface Props<Route extends BaseRoute> {
  /*
   * Whether to show labels in tabs. When false, only icons will be displayed.
   */
  labeled?: boolean;
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
  scrollEdgeAppearance?: 'default' | 'opaque' | 'transparent';

  /**
   * Behavior for minimizing the tab bar. (iOS 26+)
   */
  minimizeBehavior?: 'automatic' | 'onScrollDown' | 'onScrollUp' | 'never';
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
   * Callback which is called on tab change, receives the index of the new tab as argument.
   */
  onIndexChange: (index: number) => void;
  /**
   * Callback which is called on long press on tab, receives the index of the tab as argument.
   */
  onTabLongPress?: (index: number) => void;
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
  renderBottomAccessoryView?: BottomAccessoryViewProps['renderBottomAccessoryView'];
  /**
   * Whether the tab bar is hidden.
   */
  tabBarHidden?: boolean;
  /**
   * Whether to ignore bottom system insets (navigation bar). Android only.
   */
  ignoreBottomInsets?: boolean;
  /**
   * Whether to delay freeze/unfreeze by 200ms on tab switch.
   * Defaults to false (immediate freeze). Set to true to restore the
   * original delayed behavior.
   */
  delayedFreeze?: boolean;
}

const ANDROID_MAX_TABS = 100;

const TabView = <Route extends BaseRoute>({
  navigationState,
  renderScene,
  onIndexChange,
  onTabLongPress,
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
  labeled = Platform.OS !== 'android' ? true : undefined,
  getFreezeOnBlur = ({ route }: { route: Route }) => route.freezeOnBlur,
  tabBar: renderCustomTabBar,
  tabBarStyle,
  tabLabelStyle,
  renderBottomAccessoryView,
  activeIndicatorColor,
  delayedFreeze = false,
  ...props
}: Props<Route>) => {
  // @ts-ignore
  const focusedKey = navigationState.routes[navigationState.index].key;
  const customTabBarWrapperRef = useRef<View>(null) as React.RefObject<any>;
  const [tabBarHeight, setTabBarHeight] = React.useState<number | undefined>(0);
  const [measuredDimensions, setMeasuredDimensions] = React.useState<
    { width: DimensionValue; height: DimensionValue } | undefined
  >({ width: '100%', height: '100%' });

  const trimmedRoutes = React.useMemo(() => {
    if (
      Platform.OS === 'android' &&
      navigationState.routes.length > ANDROID_MAX_TABS
    ) {
      console.warn(
        `TabView only supports up to ${ANDROID_MAX_TABS} tabs on Android`
      );
      return navigationState.routes.slice(0, ANDROID_MAX_TABS);
    }
    return navigationState.routes;
  }, [navigationState.routes]);

  const [loaded, setLoaded] = React.useState<string[]>([focusedKey]);

  if (!loaded.includes(focusedKey)) {
    setLoaded((loaded) => [...loaded, focusedKey]);
  }

  const icons = React.useMemo(
    () =>
      trimmedRoutes.map((route) =>
        getIcon({
          route,
          focused: route.key === focusedKey,
        })
      ),
    [focusedKey, getIcon, trimmedRoutes]
  );

  const items: TabViewItems[number][] = React.useMemo(
    () =>
      trimmedRoutes.map((route, index) => {
        const icon = icons[index];
        const isSfSymbol = isAppleSymbol(icon);

        if (Platform.OS === 'android' && isSfSymbol) {
          console.warn(
            'SF Symbols are not supported on Android. Use require() or pass uri to load an image instead.'
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
            typeof badgeBgColor === 'number' ? badgeBgColor : undefined,
          badgeTextColor:
            typeof badgeTxtColor === 'number' ? badgeTxtColor : undefined,
          activeTintColor: typeof color === 'number' ? color : undefined,
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
            uri: resolved?.uri ?? '',
            width: resolved?.width ?? 0,
            height: resolved?.height ?? 0,
            scale: resolved?.scale ?? 1,
          };
        }
        return { uri: '', width: 0, height: 0, scale: 1 };
      }),
    [icons]
  );

  const jumpTo = useLatestCallback((key: string) => {
    const index = trimmedRoutes.findIndex((route) => route.key === key);
    if (index === -1) {
      return;
    }
    onIndexChange(index);
  });

  const handleTabLongPress = React.useCallback(
    (event: NativeSyntheticEvent<{ key: string }>) => {
      const { key } = event.nativeEvent;
      const index = trimmedRoutes.findIndex((route) => route.key === key);
      if (index !== -1) {
        onTabLongPress?.(index);
      }
    },
    [trimmedRoutes, onTabLongPress]
  );

  const handlePageSelected = React.useCallback(
    (event: NativeSyntheticEvent<{ key: string }>) => {
      const { key } = event.nativeEvent;
      jumpTo(key);
    },
    [jumpTo]
  );

  const handleTabBarMeasured = React.useCallback(
    (event: NativeSyntheticEvent<{ height: number }>) => {
      setTabBarHeight(event.nativeEvent.height);
    },
    [setTabBarHeight]
  );

  const handleNativeLayout = React.useCallback(
    (event: NativeSyntheticEvent<{ width: number; height: number }>) => {
      const { width, height } = event.nativeEvent;
      setMeasuredDimensions({ width, height });
    },
    [setMeasuredDimensions]
  );

  useLayoutEffect(() => {
    if (renderCustomTabBar && customTabBarWrapperRef.current) {
      customTabBarWrapperRef.current.measure((_x: number, _y: number, _width: number, height: number) => {
        setTabBarHeight(height);
      });
    }
  }, [renderCustomTabBar]);

  return (
    <BottomTabBarHeightContext.Provider value={tabBarHeight}>
      <NativeTabView
        {...props}
        {...tabLabelStyle}
        style={styles.fullWidth}
        items={items}
        icons={renderCustomTabBar ? undefined : resolvedIconAssets}
        selectedPage={focusedKey}
        tabBarHidden={props.tabBarHidden ?? !!renderCustomTabBar}
        onTabLongPress={handleTabLongPress}
        onPageSelected={handlePageSelected}
        onTabBarMeasured={handleTabBarMeasured}
        onNativeLayout={handleNativeLayout}
        hapticFeedbackEnabled={hapticFeedbackEnabled}
        activeTintColor={activeTintColor}
        inactiveTintColor={inactiveTintColor}
        barTintColor={tabBarStyle?.backgroundColor}
        rippleColor={rippleColor}
        activeIndicatorColor={activeIndicatorColor}
        labeled={labeled}
      >
        {trimmedRoutes.map((route) => {
          if (getLazy({ route }) !== false && !loaded.includes(route.key)) {
            return (
              <View
                key={route.key}
                collapsable={false}
                style={styles.fullWidth}
              />
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
              pointerEvents={focused ? 'auto' : 'none'}
              accessibilityElementsHidden={!focused}
              importantForAccessibility={
                focused ? 'auto' : 'no-hide-descendants'
              }
            >
              <DelayedFreeze freeze={!!freeze} delayedFreeze={delayedFreeze}>
                {renderScene({
                  route,
                  jumpTo,
                })}
              </DelayedFreeze>
            </View>
          );
        })}
        {Platform.OS === 'ios' &&
        parseFloat(Platform.Version) >= 26 &&
        renderBottomAccessoryView &&
        !renderCustomTabBar ? (
          <BottomAccessoryView
            renderBottomAccessoryView={renderBottomAccessoryView}
          />
        ) : null}
      </NativeTabView>
      {renderCustomTabBar ? (
        <View ref={customTabBarWrapperRef}>{renderCustomTabBar()}</View>
      ) : null}
    </BottomTabBarHeightContext.Provider>
  );
};

const styles = StyleSheet.create({
  fullWidth: {
    width: '100%',
    height: '100%',
    flex: 1,
  },
  screen: {
    position: 'absolute',
  },
});

export default TabView;
