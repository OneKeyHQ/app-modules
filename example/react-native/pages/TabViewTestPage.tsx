import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Switch, Platform } from 'react-native';
import TabView, { SceneMap, useBottomTabBarHeight } from '@anthropic/react-native-tab-view';
import type { BaseRoute, NavigationState } from '@anthropic/react-native-tab-view/src/types';

interface TabViewTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

// --- Tab content scenes ---

function HomeScene() {
  return (
    <View style={sceneStyles.container}>
      <Text style={sceneStyles.title}>Home Tab</Text>
      <Text style={sceneStyles.body}>This is the home tab content.</Text>
      <TabBarHeightDisplay />
    </View>
  );
}

function SearchScene() {
  return (
    <View style={sceneStyles.container}>
      <Text style={sceneStyles.title}>Search Tab</Text>
      <Text style={sceneStyles.body}>This is the search tab content.</Text>
      <TabBarHeightDisplay />
    </View>
  );
}

function SettingsScene() {
  return (
    <View style={sceneStyles.container}>
      <Text style={sceneStyles.title}>Settings Tab</Text>
      <Text style={sceneStyles.body}>This is the settings tab content.</Text>
      <TabBarHeightDisplay />
    </View>
  );
}

function ProfileScene() {
  return (
    <View style={sceneStyles.container}>
      <Text style={sceneStyles.title}>Profile Tab</Text>
      <Text style={sceneStyles.body}>This is the profile tab content.</Text>
      <TabBarHeightDisplay />
    </View>
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

export function TabViewTestPage({ onGoHome, safeAreaInsets }: TabViewTestPageProps) {
  const [index, setIndex] = useState(0);
  const [routes] = useState<Route[]>([
    {
      key: 'home',
      title: 'Home',
      focusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'house.fill' } : undefined,
      unfocusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'house' } : undefined,
    },
    {
      key: 'search',
      title: 'Search',
      focusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'magnifyingglass' } : undefined,
      role: 'search' as const,
    },
    {
      key: 'settings',
      title: 'Settings',
      focusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'gearshape.fill' } : undefined,
      unfocusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'gearshape' } : undefined,
      badge: '3',
    },
    {
      key: 'profile',
      title: 'Profile',
      focusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'person.fill' } : undefined,
      unfocusedIcon: Platform.OS === 'ios' ? { sfSymbol: 'person' } : undefined,
    },
  ]);

  // Toggle states for testing various props
  const [showBadge, setShowBadge] = useState(true);
  const [labeled, setLabeled] = useState(true);
  const [translucent, setTranslucent] = useState(true);
  const [tabBarHidden, setTabBarHidden] = useState(false);
  const [sidebarAdaptable, setSidebarAdaptable] = useState(false);
  const [hapticFeedback, setHapticFeedback] = useState(false);
  const [showControls, setShowControls] = useState(false);

  const navigationState: NavigationState<Route> = {
    index,
    routes,
  };

  return (
    <View style={styles.container}>
      {/* Back button overlay */}
      <View style={[styles.backButtonContainer, { top: safeAreaInsets.top + 4 }]}>
        <TouchableOpacity style={styles.backButton} onPress={onGoHome}>
          <Text style={styles.backButtonText}>← Back</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.controlsToggle}
          onPress={() => setShowControls(!showControls)}
        >
          <Text style={styles.controlsToggleText}>
            {showControls ? 'Hide Controls' : 'Controls'}
          </Text>
        </TouchableOpacity>
      </View>

      {/* Controls panel */}
      {showControls && (
        <View style={[styles.controlsPanel, { top: safeAreaInsets.top + 44 }]}>
          <ToggleRow label="Show Badge" value={showBadge} onToggle={setShowBadge} />
          <ToggleRow label="Labeled" value={labeled} onToggle={setLabeled} />
          <ToggleRow label="Translucent" value={translucent} onToggle={setTranslucent} />
          <ToggleRow label="Tab Bar Hidden" value={tabBarHidden} onToggle={setTabBarHidden} />
          <ToggleRow label="Sidebar Adaptable" value={sidebarAdaptable} onToggle={setSidebarAdaptable} />
          <ToggleRow label="Haptic Feedback" value={hapticFeedback} onToggle={setHapticFeedback} />
        </View>
      )}

      {/* TabView */}
      <TabView
        navigationState={navigationState}
        renderScene={renderScene}
        onIndexChange={setIndex}
        onTabLongPress={(tabIndex) => {
          console.log('Long pressed tab:', tabIndex);
        }}
        labeled={labeled}
        translucent={translucent}
        tabBarHidden={tabBarHidden}
        sidebarAdaptable={sidebarAdaptable}
        hapticFeedbackEnabled={hapticFeedback}
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

function ToggleRow({
  label,
  value,
  onToggle,
}: {
  label: string;
  value: boolean;
  onToggle: (v: boolean) => void;
}) {
  return (
    <View style={styles.toggleRow}>
      <Text style={styles.toggleLabel}>{label}</Text>
      <Switch value={value} onValueChange={onToggle} />
    </View>
  );
}

const sceneStyles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
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
});

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  backButtonContainer: {
    position: 'absolute',
    left: 0,
    right: 0,
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    zIndex: 100,
  },
  backButton: {
    backgroundColor: 'rgba(255,255,255,0.9)',
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 8,
  },
  backButtonText: {
    fontSize: 16,
    color: '#007AFF',
    fontWeight: '500',
  },
  controlsToggle: {
    backgroundColor: 'rgba(255,255,255,0.9)',
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 8,
  },
  controlsToggleText: {
    fontSize: 14,
    color: '#007AFF',
    fontWeight: '500',
  },
  controlsPanel: {
    position: 'absolute',
    left: 16,
    right: 16,
    backgroundColor: 'rgba(255,255,255,0.95)',
    borderRadius: 12,
    padding: 16,
    zIndex: 99,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 5,
  },
  toggleRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 6,
  },
  toggleLabel: {
    fontSize: 15,
    color: '#333',
  },
});
