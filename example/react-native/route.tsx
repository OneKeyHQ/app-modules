import { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, TextInput } from 'react-native';
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
import { PerfMemoryTestPage } from './pages/PerfMemoryTestPage';
import { BundleUpdateTestPage } from './pages/BundleUpdateTestPage';
import { AppUpdateTestPage } from './pages/AppUpdateTestPage';
import { SplashScreenTestPage } from './pages/SplashScreenTestPage';
import { TabViewTestPage } from './pages/TabViewTestPage';

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
  PerfMemory: undefined;
  Skeleton: undefined;
  SplashScreen: undefined;
  TabView: undefined;
};

export type RootStackNavigationProp = NativeStackNavigationProp<RootStackParamList>;

const Stack = createNativeStackNavigator<RootStackParamList>();

const modules: { screen: keyof RootStackParamList; name: string; description: string }[] = [
  {
    screen: 'AutoSizeInput',
    name: 'Auto Size Input',
    description: 'Input with auto-shrinking font size, prefix/suffix, single & multi-line',
  },
  {
    screen: 'AppUpdate',
    name: 'App Update',
    description: 'APK download, verification, and installation (Android)',
  },
  {
    screen: 'BackgroundThread',
    name: 'Background Thread',
    description: 'Test background thread messaging and processing',
  },
  {
    screen: 'BiometricAuth',
    name: 'Biometric Auth Changed',
    description: 'Check if biometric authentication has changed',
  },
  {
    screen: 'BundleUpdate',
    name: 'Bundle Update',
    description: 'JS bundle download, verification, install, and path management',
  },
  {
    screen: 'CloudKit',
    name: 'CloudKit Module',
    description: 'Test iCloud storage operations and sync',
  },
  {
    screen: 'DeviceUtils',
    name: 'Device Utils',
    description: 'Test dual screen detection, spanning, and device utilities',
  },
  {
    screen: 'Keychain',
    name: 'Keychain Module',
    description: 'Test secure storage operations',
  },
  {
    screen: 'LiteCard',
    name: 'Lite Card',
    description: 'Test NFC card operations and management',
  },
  {
    screen: 'GetRandomValues',
    name: 'Get Random Values',
    description: 'Generate cryptographically secure random values',
  },
  {
    screen: 'NativeLogger',
    name: 'Native Logger',
    description: 'File-based logging with log directory viewer',
  },
  {
    screen: 'PerfMemory',
    name: 'Perf Memory',
    description: 'Read process memory usage (RSS) for performance monitoring',
  },
  {
    screen: 'Skeleton',
    name: 'Skeleton View',
    description: 'Animated skeleton loading components for better UX',
  },
  {
    screen: 'SplashScreen',
    name: 'Splash Screen',
    description: 'Legacy splash screen for Android < 12',
  },
  {
    screen: 'TabView',
    name: 'Tab View',
    description: 'Native tab bar with UIKit (iOS) / Material (Android), badges, icons, liquid glass',
  },
];

function HomeScreen() {
  const navigation = useNavigation<RootStackNavigationProp>();
  const [searchQuery, setSearchQuery] = useState<string>('');

  const filteredModules = modules.filter((module) => {
    const query = searchQuery.toLowerCase();
    return (
      module.name.toLowerCase().includes(query) ||
      module.description.toLowerCase().includes(query)
    );
  });

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <View style={styles.header}>
        <Text style={styles.title}>Native Modules Test Suite</Text>
        <Text style={styles.subtitle}>Test all available native modules and their APIs</Text>
      </View>

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

      <View style={styles.moduleList}>
        {filteredModules.length > 0 ? (
          filteredModules.map((module) => (
            <TouchableOpacity
              key={module.screen}
              style={styles.moduleCard}
              onPress={() => navigation.navigate(module.screen)}
              activeOpacity={0.7}
            >
              <Text style={styles.moduleName}>{module.name}</Text>
              <Text style={styles.moduleDescription}>{module.description}</Text>
              <Text style={styles.tapHint}>Tap to test →</Text>
            </TouchableOpacity>
          ))
        ) : (
          <View style={styles.noResultsContainer}>
            <Text style={styles.noResultsText}>No modules found</Text>
            <Text style={styles.noResultsSubtext}>Try a different search term</Text>
          </View>
        )}
      </View>
    </ScrollView>
  );
}

export function AppNavigator() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="Home" component={HomeScreen} />
      <Stack.Screen name="AutoSizeInput" component={AutoSizeInputTestPage} />
      <Stack.Screen name="AppUpdate" component={AppUpdateTestPage} />
      <Stack.Screen name="BackgroundThread" component={BackgroundThreadTestPage} />
      <Stack.Screen name="BiometricAuth" component={BiometricAuthTestPage} />
      <Stack.Screen name="BundleUpdate" component={BundleUpdateTestPage} />
      <Stack.Screen name="CloudKit" component={CloudKitTestPage} />
      <Stack.Screen name="DeviceUtils" component={DeviceUtilsTestPage} />
      <Stack.Screen name="Keychain" component={KeychainTestPage} />
      <Stack.Screen name="LiteCard" component={LiteCardTestPage} />
      <Stack.Screen name="GetRandomValues" component={GetRandomValuesTestPage} />
      <Stack.Screen name="NativeLogger" component={NativeLoggerTestPage} />
      <Stack.Screen name="PerfMemory" component={PerfMemoryTestPage} />
      <Stack.Screen name="Skeleton" component={SkeletonTestPage} />
      <Stack.Screen name="SplashScreen" component={SplashScreenTestPage} />
      <Stack.Screen name="TabView" component={TabViewTestPage} />
    </Stack.Navigator>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  contentContainer: {
    padding: 20,
  },
  header: {
    marginBottom: 30,
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  },
  searchContainer: {
    marginBottom: 20,
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
    gap: 15,
  },
  moduleCard: {
    backgroundColor: '#fff',
    padding: 20,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  moduleName: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  moduleDescription: {
    fontSize: 14,
    color: '#666',
    lineHeight: 20,
    marginBottom: 12,
  },
  tapHint: {
    fontSize: 14,
    color: '#007AFF',
    fontWeight: '500',
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
});
