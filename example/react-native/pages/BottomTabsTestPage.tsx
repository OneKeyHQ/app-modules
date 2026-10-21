import React, { useLayoutEffect, useSyncExternalStore, useState } from 'react';
import { View, Text, StyleSheet, Platform, ScrollView, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import BottomTabs, { SceneMap, useBottomTabBarHeight } from '@onekeyfe/react-native-bottom-tabs';
import type { BaseRoute, NavigationState } from '@onekeyfe/react-native-bottom-tabs/src/types';
import type { RootStackNavigationProp } from '../route';
import { getBottomTabsSettings, setBottomTabsSettings, subscribeBottomTabsSettings } from './bottomTabsSettingsStore';

// --- Tab content scenes ---

const COLOR_STRIPS = [
  ['#FF3B30', '#FF9500', '#FFCC00', '#34C759', '#007AFF', '#5856D6', '#AF52DE', '#FF2D55'],
  ['#30D158', '#64D2FF', '#BF5AF2', '#FFD60A', '#FF453A', '#0A84FF', '#32D74B', '#FF6482'],
  ['#5E5CE6', '#FF375F', '#AC8E68', '#00C7BE', '#FF9F0A', '#30B0C7', '#FF6482', '#FFD426'],
  ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F'],
];

function ColorfulScene({ title, colorIndex }: { title: string; colorIndex: number }) {
  const colors = COLOR_STRIPS[colorIndex % COLOR_STRIPS.length]!;
  const height = useBottomTabBarHeight();
  const { androidBottomBlurEnabled } = useSyncExternalStore(subscribeBottomTabsSettings, getBottomTabsSettings);
  const bottomPadding = Platform.OS === 'android' && Number(Platform.Version) >= 31 && androidBottomBlurEnabled
    ? height ?? 0 : 0;
  return (
    <ScrollView style={sceneStyles.scrollContainer} contentContainerStyle={[sceneStyles.scrollContent, { paddingBottom: bottomPadding }]}>
      <View style={sceneStyles.topSection}>
        <Text style={sceneStyles.title}>{title}</Text>
        <Text style={sceneStyles.body}>Scroll down to see colors behind the tab bar</Text>
        <TabBarHeightDisplay />
        <PressStatsDisplay />
      </View>
      {/* Colorful blocks that extend to the bottom, behind the translucent tab bar */}
      {colors.map((color, i) => (
        <View key={i} style={[sceneStyles.colorBlock, { backgroundColor: color }]}>
          <Text style={sceneStyles.colorLabel}>{color}</Text>
        </View>
      ))}
      {/* Extra vibrant gradient blocks at the very bottom */}
      <View style={sceneStyles.gradientRow}>
        {colors.slice(0, 4).map((color, i) => (
          <View key={i} style={[sceneStyles.gradientCell, { backgroundColor: color }]} />
        ))}
      </View>
      <View style={sceneStyles.gradientRow}>
        {colors.slice(4).map((color, i) => (
          <View key={i} style={[sceneStyles.gradientCell, { backgroundColor: color }]} />
        ))}
      </View>
    </ScrollView>
  );
}

function HomeScene() {
  return <ColorfulScene title="Home Tab" colorIndex={0} />;
}

function SearchScene() {
  return <ColorfulScene title="Search Tab" colorIndex={1} />;
}

function SettingsScene() {
  return <ColorfulScene title="Settings Tab" colorIndex={2} />;
}

function ProfileScene() {
  return <ColorfulScene title="Profile Tab" colorIndex={3} />;
}

// Press stats shown in every scene so double press can be checked without logs.
type PressStats = { doublePresses: number; lastDoublePressTab: number | null; lastGapMs: number | null };
let pressStats: PressStats = { doublePresses: 0, lastDoublePressTab: null, lastGapMs: null };
let lastPressTime = 0;
const pressListeners = new Set<() => void>();
function updatePressStats(partial: Partial<PressStats>) {
  pressStats = { ...pressStats, ...partial };
  pressListeners.forEach(l => l());
}
function subscribePressStats(listener: () => void) {
  pressListeners.add(listener);
  return () => { pressListeners.delete(listener); };
}
function recordTabPress() {
  const now = Date.now();
  updatePressStats({ lastGapMs: lastPressTime ? now - lastPressTime : null });
  lastPressTime = now;
}

function PressStatsDisplay() {
  const stats = useSyncExternalStore(subscribePressStats, () => pressStats);
  return (
    <Text style={sceneStyles.info} testID="tab-press-stats">
      Double presses: {stats.doublePresses} (last tab: {stats.lastDoublePressTab ?? '-'}) · last press gap: {stats.lastGapMs ?? '-'} ms
    </Text>
  );
}

function TabBarHeightDisplay() {
  const height = useBottomTabBarHeight();
  return (
    <Text style={sceneStyles.info}>
      Tab bar height: {height ?? 'unknown'}
    </Text>
  );
}

const renderScene = SceneMap({
  home: HomeScene,
  search: SearchScene,
  settings: SettingsScene,
  profile: ProfileScene,
});

// --- Main test page ---

type Route = BaseRoute & {
  title: string;
};

export function BottomTabsTestPage() {
  const navigation = useNavigation<RootStackNavigationProp>();
  const settings = useSyncExternalStore(subscribeBottomTabsSettings, getBottomTabsSettings);
  const { showBadge, labeled, translucent, tabBarHidden, sidebarAdaptable, hapticFeedback, androidFloating } = settings;
  const [index, setIndex] = useState(0);
  const [routes] = useState<Route[]>([
    {
      key: 'home',
      title: 'Home',
      focusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'house.fill' } : require('./bottomTabsAssets/home.png'),
      unfocusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'house' } : undefined,
    },
    {
      key: 'search',
      title: 'Search',
      focusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'magnifyingglass' } : require('./bottomTabsAssets/search.png'),
      role: 'search' as const,
    },
    {
      key: 'settings',
      title: 'Settings',
      focusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'gearshape.fill' } : require('./bottomTabsAssets/settings.png'),
      unfocusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'gearshape' } : undefined,
      badge: '3',
    },
    {
      key: 'profile',
      title: 'Profile',
      focusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'person.fill' } : require('./bottomTabsAssets/profile.png'),
      unfocusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'person' } : undefined,
    },
  ]);

  const navigateToSettings = React.useCallback(() => {
    navigation.navigate('BottomTabsSettings');
  }, [navigation]);

  useLayoutEffect(() => {
    if (Platform.OS === 'ios') {
      navigation.setOptions({
        unstable_headerRightItems: () => [
          {
            type: 'menu',
            label: 'Controls',
            icon: { type: 'sfSymbol', name: 'slider.horizontal.3' },
            menu: {
              title: 'Controls',
              items: [
                { type: 'action', label: 'Show Badge', state: showBadge ? 'on' : 'off', keepsMenuPresented: true, onPress: () => setBottomTabsSettings({ showBadge: !showBadge }) },
                { type: 'action', label: 'Labeled', state: labeled ? 'on' : 'off', keepsMenuPresented: true, onPress: () => setBottomTabsSettings({ labeled: !labeled }) },
                { type: 'action', label: 'Translucent', state: translucent ? 'on' : 'off', keepsMenuPresented: true, onPress: () => setBottomTabsSettings({ translucent: !translucent }) },
                { type: 'action', label: 'Tab Bar Hidden', state: tabBarHidden ? 'on' : 'off', keepsMenuPresented: true, onPress: () => setBottomTabsSettings({ tabBarHidden: !tabBarHidden }) },
                { type: 'action', label: 'Sidebar Adaptable', state: sidebarAdaptable ? 'on' : 'off', keepsMenuPresented: true, onPress: () => setBottomTabsSettings({ sidebarAdaptable: !sidebarAdaptable }) },
                { type: 'action', label: 'Haptic Feedback', state: hapticFeedback ? 'on' : 'off', keepsMenuPresented: true, onPress: () => setBottomTabsSettings({ hapticFeedback: !hapticFeedback }) },
              ],
            },
          },
        ],
      });
    } else {
      navigation.setOptions({
        headerRight: () => (
          <TouchableOpacity onPress={navigateToSettings} style={{ paddingHorizontal: 12 }}>
            <Text style={{ fontSize: 16, color: '#007AFF' }}>Settings</Text>
          </TouchableOpacity>
        ),
      });
    }
  }, [navigation, navigateToSettings, showBadge, labeled, translucent, tabBarHidden, sidebarAdaptable, hapticFeedback]);

  const navigationState: NavigationState<Route> = {
    index,
    routes,
  };

  return (
    <View style={styles.container}>
      <BottomTabs
        navigationState={navigationState}
        renderScene={renderScene}
        onIndexChange={(nextIndex) => {
          recordTabPress();
          setIndex(nextIndex);
        }}
        onTabDoublePress={(tabIndex) => {
          console.log('Double pressed tab:', tabIndex);
          updatePressStats({
            doublePresses: pressStats.doublePresses + 1,
            lastDoublePressTab: tabIndex,
          });
        }}
        labeled={labeled}
        androidTabBarStyle={androidFloating ? 'floating' : 'default'}
        translucent={translucent}
        tabBarHidden={tabBarHidden}
        sidebarAdaptable={sidebarAdaptable}
        hapticFeedbackEnabled={hapticFeedback}
        androidBottomBlurEnabled={settings.androidBottomBlurEnabled}
        tabBarActiveTintColor="#007AFF"
        tabBarInactiveTintColor="#8E8E93"
        getBadge={({ route }) => {
          if (!showBadge) return undefined;
          if (route.key === 'settings') return '3';
          if (route.key === 'profile') return 'New';
          return undefined;
        }}
        getBadgeBackgroundColor={({ route }) => {
          if (route.key === 'profile') return '#FF3B30';
          return undefined;
        }}
        getBadgeTextColor={({ route }) => {
          if (route.key === 'profile') return '#FFFFFF';
          return undefined;
        }}
      />
    </View>
  );
}

const sceneStyles = StyleSheet.create({
  scrollContainer: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  scrollContent: {
    paddingBottom: 0,
  },
  topSection: {
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 60,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  body: {
    fontSize: 16,
    color: '#666',
    marginBottom: 16,
  },
  info: {
    fontSize: 14,
    color: '#999',
    marginTop: 8,
  },
  colorBlock: {
    height: 80,
    justifyContent: 'center',
    alignItems: 'center',
  },
  colorLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: 'rgba(255,255,255,0.9)',
    textShadowColor: 'rgba(0,0,0,0.3)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 2,
  },
  gradientRow: {
    flexDirection: 'row',
    height: 60,
  },
  gradientCell: {
    flex: 1,
  },
});

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
