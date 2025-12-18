import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, TextInput } from 'react-native';
import { BackgroundThreadTestPage } from './pages/BackgroundThreadTestPage';
import { BiometricAuthTestPage } from './pages/BiometricAuthTestPage';
import { CloudKitTestPage } from './pages/CloudKitTestPage';
import { KeychainTestPage } from './pages/KeychainTestPage';
import { LiteCardTestPage } from './pages/LiteCardTestPage';
import { GetRandomValuesTestPage } from './pages/GetRandomValuesTestPage';
import { DeviceUtilsTestPage } from './pages/DeviceUtilsTestPage';
import { SkeletonTestPage } from './pages/SkeletonTestPage';

export type RouteScreen = 
  | 'home'
  | 'background-thread'
  | 'biometric-auth'
  | 'cloud-kit'
  | 'keychain'
  | 'lite-card'
  | 'get-random-values'
  | 'device-utils'
  | 'skeleton';

interface RouterProps {
  safeAreaInsets: any;
}

const modules = [
  {
    id: 'background-thread' as RouteScreen,
    name: 'Background Thread',
    description: 'Test background thread messaging and processing',
  },
  {
    id: 'biometric-auth' as RouteScreen,
    name: 'Biometric Auth Changed',
    description: 'Check if biometric authentication has changed',
  },
  {
    id: 'cloud-kit' as RouteScreen,
    name: 'CloudKit Module',
    description: 'Test iCloud storage operations and sync',
  },
  {
    id: 'device-utils' as RouteScreen,
    name: 'Device Utils',
    description: 'Test dual screen detection, spanning, and device utilities',
  },
  {
    id: 'keychain' as RouteScreen,
    name: 'Keychain Module',
    description: 'Test secure storage operations',
  },
  {
    id: 'lite-card' as RouteScreen,
    name: 'Lite Card',
    description: 'Test NFC card operations and management',
  },
  {
    id: 'get-random-values' as RouteScreen,
    name: 'Get Random Values',
    description: 'Generate cryptographically secure random values',
  },
  {
    id: 'skeleton' as RouteScreen,
    name: 'Skeleton View',
    description: 'Animated skeleton loading components for better UX',
  },
];

export function Router({ safeAreaInsets }: RouterProps) {
  const [currentScreen, setCurrentScreen] = useState<RouteScreen>('home');
  const [searchQuery, setSearchQuery] = useState<string>('');

  const navigateTo = (screen: RouteScreen) => {
    setCurrentScreen(screen);
  };

  const filteredModules = modules.filter((module) => {
    const query = searchQuery.toLowerCase();
    return (
      module.name.toLowerCase().includes(query) ||
      module.description.toLowerCase().includes(query)
    );
  });

  const renderHomeScreen = () => (
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
              key={module.id}
              style={styles.moduleCard}
              onPress={() => navigateTo(module.id)}
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

  const renderTestScreen = () => {
    const commonProps = { 
      onGoHome: () => navigateTo('home'),
      safeAreaInsets 
    };

    switch (currentScreen) {
      case 'background-thread':
        return <BackgroundThreadTestPage {...commonProps} />;
      case 'biometric-auth':
        return <BiometricAuthTestPage {...commonProps} />;
      case 'cloud-kit':
        return <CloudKitTestPage {...commonProps} />;
      case 'device-utils':
        return <DeviceUtilsTestPage {...commonProps} />;
      case 'keychain':
        return <KeychainTestPage {...commonProps} />;
      case 'lite-card':
        return <LiteCardTestPage {...commonProps} />;
      case 'get-random-values':
        return <GetRandomValuesTestPage {...commonProps} />;
      case 'skeleton':
        return <SkeletonTestPage {...commonProps} />;
      default:
        return renderHomeScreen();
    }
  };

  return (
    <View style={styles.wrapper}>
      {currentScreen === 'home' ? renderHomeScreen() : renderTestScreen()}
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  container: {
    flex: 1,
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