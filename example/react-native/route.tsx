import { useCallback, useMemo, useRef, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, TextInput, PanResponder, type LayoutChangeEvent } from 'react-native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { useNavigation } from '@react-navigation/native';
import { AutoSizeInputTestPage } from './pages/AutoSizeInputTestPage';
import { BackgroundThreadTestPage } from './pages/BackgroundThreadTestPage';
import { BiometricAuthTestPage } from './pages/BiometricAuthTestPage';
import { CloudKitTestPage } from './pages/CloudKitTestPage';
import { KeychainTestPage } from './pages/KeychainTestPage';
import { LiteCardTestPage } from './pages/LiteCardTestPage';
import { GetRandomValuesTestPage } from './pages/GetRandomValuesTestPage';
import { DeviceUtilsTestPage } from './pages/DeviceUtilsTestPage';
import { SkeletonTestPage } from './pages/SkeletonTestPage';
import { NativeLoggerTestPage } from './pages/NativeLoggerTestPage';
import { PagerViewTestPage } from './pages/PagerViewTestPage';
import { PerfMemoryTestPage } from './pages/PerfMemoryTestPage';
import { BundleUpdateTestPage } from './pages/BundleUpdateTestPage';
import { AppUpdateTestPage } from './pages/AppUpdateTestPage';
import { SplashScreenTestPage } from './pages/SplashScreenTestPage';
import { TabViewTestPage } from './pages/TabViewTestPage';
import { TabViewSettingsPage } from './pages/TabViewSettingsPage';

export type TabViewSettingsState = {
  showBadge: boolean;
  labeled: boolean;
  translucent: boolean;
  tabBarHidden: boolean;
  sidebarAdaptable: boolean;
  hapticFeedback: boolean;
};

export type RootStackParamList = {
  Home: undefined;
  AutoSizeInput: undefined;
  AppUpdate: undefined;
  BackgroundThread: undefined;
  BiometricAuth: undefined;
  BundleUpdate: undefined;
  CloudKit: undefined;
  DeviceUtils: undefined;
  Keychain: undefined;
  LiteCard: undefined;
  GetRandomValues: undefined;
  NativeLogger: undefined;
  PagerView: undefined;
  PerfMemory: undefined;
  Skeleton: undefined;
  SplashScreen: undefined;
  TabView: undefined;
  TabViewSettings: undefined;
};

export type RootStackNavigationProp = NativeStackNavigationProp<RootStackParamList>;

const Stack = createNativeStackNavigator<RootStackParamList>();

const modules: { screen: keyof RootStackParamList; name: string; description: string; icon: string }[] = [
  {
    screen: 'AutoSizeInput',
    name: 'Auto Size Input',
    description: 'Input with auto-shrinking font size, prefix/suffix, single & multi-line',
    icon: '⌨️',
  },
  {
    screen: 'AppUpdate',
    name: 'App Update',
    description: 'APK download, verification, and installation (Android)',
    icon: '📦',
  },
  {
    screen: 'BackgroundThread',
    name: 'Background Thread',
    description: 'Test background thread messaging and processing',
    icon: '⚙️',
  },
  {
    screen: 'BiometricAuth',
    name: 'Biometric Auth Changed',
    description: 'Check if biometric authentication has changed',
    icon: '🔐',
  },
  {
    screen: 'BundleUpdate',
    name: 'Bundle Update',
    description: 'JS bundle download, verification, install, and path management',
    icon: '📥',
  },
  {
    screen: 'CloudKit',
    name: 'CloudKit Module',
    description: 'Test iCloud storage operations and sync',
    icon: '☁️',
  },
  {
    screen: 'DeviceUtils',
    name: 'Device Utils',
    description: 'Test dual screen detection, spanning, and device utilities',
    icon: '📱',
  },
  {
    screen: 'Keychain',
    name: 'Keychain Module',
    description: 'Test secure storage operations',
    icon: '🔑',
  },
  {
    screen: 'LiteCard',
    name: 'Lite Card',
    description: 'Test NFC card operations and management',
    icon: '💳',
  },
  {
    screen: 'GetRandomValues',
    name: 'Get Random Values',
    description: 'Generate cryptographically secure random values',
    icon: '🎲',
  },
  {
    screen: 'NativeLogger',
    name: 'Native Logger',
    description: 'File-based logging with log directory viewer',
    icon: '📝',
  },
  {
    screen: 'PagerView',
    name: 'Pager View',
    description: 'Native page container with swipe navigation and imperative controls',
    icon: '📄',
  },
  {
    screen: 'PerfMemory',
    name: 'Perf Memory',
    description: 'Read process memory usage (RSS) for performance monitoring',
    icon: '📊',
  },
  {
    screen: 'Skeleton',
    name: 'Skeleton View',
    description: 'Animated skeleton loading components for better UX',
    icon: '💀',
  },
  {
    screen: 'SplashScreen',
    name: 'Splash Screen',
    description: 'Legacy splash screen for Android < 12',
    icon: '🚀',
  },
  {
    screen: 'TabView',
    name: 'Tab View',
    description: 'Native tab bar with UIKit (iOS) / Material (Android), badges, icons, liquid glass',
    icon: '📑',
  },
];

const ALPHABET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');

function HomeScreen() {
  const navigation = useNavigation<RootStackNavigationProp>();
  const [searchQuery, setSearchQuery] = useState<string>('');
  const scrollViewRef = useRef<ScrollView>(null);
  const sectionYRef = useRef<Record<string, number>>({});
  const sidebarRef = useRef<View>(null);
  const sidebarLayoutRef = useRef({ y: 0, height: 0 });
  const [activeLetterState, setActiveLetterState] = useState<string | null>(null);

  const filteredModules = useMemo(() => {
    const sorted = [...modules].sort((a, b) => a.name.localeCompare(b.name));
    if (!searchQuery) return sorted;
    const query = searchQuery.toLowerCase();
    return sorted.filter(
      (m) => m.name.toLowerCase().includes(query) || m.description.toLowerCase().includes(query),
    );
  }, [searchQuery]);

  const grouped = useMemo(() => {
    const map: Record<string, typeof modules> = {};
    for (const m of filteredModules) {
      const letter = m.name[0]!.toUpperCase();
      if (!map[letter]) map[letter] = [];
      map[letter]!.push(m);
    }
    return map;
  }, [filteredModules]);

  const activeLetters = useMemo(() => new Set(Object.keys(grouped)), [grouped]);

  const scrollToLetter = useCallback(
    (letter: string) => {
      const y = sectionYRef.current[letter];
      if (y != null && scrollViewRef.current) {
        scrollViewRef.current.scrollTo({ y, animated: true });
      }
    },
    [],
  );

  const getLetterFromTouch = useCallback((pageY: number) => {
    const { y, height } = sidebarLayoutRef.current;
    const relativeY = pageY - y;
    const idx = Math.floor((relativeY / height) * ALPHABET.length);
    return ALPHABET[Math.max(0, Math.min(idx, ALPHABET.length - 1))];
  }, []);

  const panResponder = useMemo(
    () =>
      PanResponder.create({
        onStartShouldSetPanResponder: () => true,
        onMoveShouldSetPanResponder: () => true,
        onPanResponderGrant: (evt) => {
          const letter = getLetterFromTouch(evt.nativeEvent.pageY);
          if (letter && activeLetters.has(letter)) {
            setActiveLetterState(letter);
            scrollToLetter(letter);
          }
        },
        onPanResponderMove: (evt) => {
          const letter = getLetterFromTouch(evt.nativeEvent.pageY);
          if (letter && activeLetters.has(letter)) {
            setActiveLetterState(letter);
            scrollToLetter(letter);
          }
        },
        onPanResponderRelease: () => {
          setActiveLetterState(null);
        },
      }),
    [activeLetters, getLetterFromTouch, scrollToLetter],
  );

  const handleSectionLayout = useCallback((letter: string, event: LayoutChangeEvent) => {
    sectionYRef.current[letter] = event.nativeEvent.layout.y;
  }, []);

  const handleSidebarLayout = useCallback(() => {
    sidebarRef.current?.measureInWindow((_x, y, _w, height) => {
      sidebarLayoutRef.current = { y, height };
    });
  }, []);

  const sections = Object.keys(grouped).sort();

  return (
    <View style={styles.outerContainer}>
      <ScrollView
        ref={scrollViewRef}
        style={styles.container}
        contentContainerStyle={styles.contentContainer}
      >
        <View style={styles.searchContainer}>
          <TextInput
            style={styles.searchInput}
            placeholder="Search modules..."
            placeholderTextColor="#999"
            value={searchQuery}
            onChangeText={setSearchQuery}
            autoCapitalize="none"
            autoCorrect={false}
          />
        </View>

        {sections.length > 0 ? (
          sections.map((letter) => (
            <View
              key={letter}
              onLayout={(e) => handleSectionLayout(letter, e)}
            >
              <Text style={styles.sectionHeader}>{letter}</Text>
              <View style={styles.moduleList}>
                {grouped[letter]!.map((module) => (
                  <TouchableOpacity
                    key={module.screen}
                    style={styles.moduleRow}
                    onPress={() => navigation.navigate(module.screen)}
                    activeOpacity={0.7}
                  >
                    <Text style={styles.moduleIcon}>{module.icon}</Text>
                    <View style={styles.moduleRowContent}>
                      <Text style={styles.moduleName}>{module.name}</Text>
                      <Text style={styles.moduleDescription}>{module.description}</Text>
                    </View>
                    <Text style={styles.chevron}>›</Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>
          ))
        ) : (
          <View style={styles.noResultsContainer}>
            <Text style={styles.noResultsText}>No modules found</Text>
            <Text style={styles.noResultsSubtext}>Try a different search term</Text>
          </View>
        )}
      </ScrollView>

      {/* Floating alphabet sidebar */}
      <View
        ref={sidebarRef}
        style={styles.sidebar}
        onLayout={handleSidebarLayout}
        {...panResponder.panHandlers}
      >
        {ALPHABET.map((letter) => {
          const isActive = activeLetters.has(letter);
          const isHighlighted = activeLetterState === letter;
          return (
            <View key={letter} style={styles.sidebarLetterContainer}>
              <Text
                style={[
                  styles.sidebarLetter,
                  !isActive && styles.sidebarLetterDisabled,
                  isHighlighted && styles.sidebarLetterHighlighted,
                ]}
              >
                {letter}
              </Text>
            </View>
          );
        })}
      </View>
    </View>
  );
}

export function AppNavigator() {
  return (
    <Stack.Navigator screenOptions={{ statusBarTranslucent: true }}>
      <Stack.Screen name="Home" component={HomeScreen} options={{ title: 'Native Modules Test Suite' }} />
      <Stack.Screen name="AutoSizeInput" component={AutoSizeInputTestPage} options={{ title: 'Auto Size Input' }} />
      <Stack.Screen name="AppUpdate" component={AppUpdateTestPage} options={{ title: 'App Update' }} />
      <Stack.Screen name="BackgroundThread" component={BackgroundThreadTestPage} options={{ title: 'Background Thread' }} />
      <Stack.Screen name="BiometricAuth" component={BiometricAuthTestPage} options={{ title: 'Biometric Auth' }} />
      <Stack.Screen name="BundleUpdate" component={BundleUpdateTestPage} options={{ title: 'Bundle Update' }} />
      <Stack.Screen name="CloudKit" component={CloudKitTestPage} options={{ title: 'CloudKit' }} />
      <Stack.Screen name="DeviceUtils" component={DeviceUtilsTestPage} options={{ title: 'Device Utils' }} />
      <Stack.Screen name="Keychain" component={KeychainTestPage} options={{ title: 'Keychain' }} />
      <Stack.Screen name="LiteCard" component={LiteCardTestPage} options={{ title: 'Lite Card' }} />
      <Stack.Screen name="GetRandomValues" component={GetRandomValuesTestPage} options={{ title: 'Get Random Values' }} />
      <Stack.Screen name="NativeLogger" component={NativeLoggerTestPage} options={{ title: 'Native Logger' }} />
      <Stack.Screen name="PagerView" component={PagerViewTestPage} options={{ title: 'Pager View' }} />
      <Stack.Screen name="PerfMemory" component={PerfMemoryTestPage} options={{ title: 'Perf Memory' }} />
      <Stack.Screen name="Skeleton" component={SkeletonTestPage} options={{ title: 'Skeleton' }} />
      <Stack.Screen name="SplashScreen" component={SplashScreenTestPage} options={{ title: 'Splash Screen' }} />
      <Stack.Screen name="TabView" component={TabViewTestPage} options={{ title: 'Tab View' }} />
      <Stack.Screen name="TabViewSettings" component={TabViewSettingsPage} options={{ title: 'Tab View Settings' }} />
    </Stack.Navigator>
  );
}

const styles = StyleSheet.create({
  outerContainer: {
    flex: 1,
    flexDirection: 'row',
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  contentContainer: {
    paddingLeft: 20,
    paddingRight: 36,
    paddingTop: 20,
    paddingBottom: 20,
  },
  searchContainer: {
    marginBottom: 20,
  },
  sectionHeader: {
    fontSize: 13,
    fontWeight: '600',
    color: '#8E8E93',
    marginTop: 16,
    marginBottom: 8,
    marginLeft: 4,
  },
  searchInput: {
    backgroundColor: '#fff',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: 12,
    fontSize: 16,
    color: '#333',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  moduleList: {
    backgroundColor: '#fff',
    borderRadius: 10,
    overflow: 'hidden',
  },
  moduleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#E5E5EA',
  },
  moduleIcon: {
    fontSize: 24,
    marginRight: 12,
  },
  moduleRowContent: {
    flex: 1,
  },
  moduleName: {
    fontSize: 16,
    fontWeight: '500',
    color: '#000',
  },
  moduleDescription: {
    fontSize: 13,
    color: '#8E8E93',
    marginTop: 2,
  },
  chevron: {
    fontSize: 22,
    color: '#C7C7CC',
    marginLeft: 8,
  },
  noResultsContainer: {
    alignItems: 'center',
    paddingVertical: 40,
  },
  noResultsText: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  noResultsSubtext: {
    fontSize: 14,
    color: '#666',
  },
  sidebar: {
    position: 'absolute',
    right: 2,
    top: 0,
    bottom: 0,
    width: 28,
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 10,
  },
  sidebarLetterContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    width: 28,
  },
  sidebarLetter: {
    fontSize: 11,
    fontWeight: '600',
    color: '#007AFF',
  },
  sidebarLetterDisabled: {
    color: '#C7C7CC',
  },
  sidebarLetterHighlighted: {
    color: '#fff',
    backgroundColor: '#007AFF',
    borderRadius: 8,
    width: 16,
    height: 16,
    textAlign: 'center',
    lineHeight: 16,
    overflow: 'hidden',
  },
});
