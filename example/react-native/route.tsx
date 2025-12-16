import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView } from 'react-native';
import { BackgroundThreadTestPage } from './pages/BackgroundThreadTestPage';
import { BiometricAuthTestPage } from './pages/BiometricAuthTestPage';
import { CloudKitTestPage } from './pages/CloudKitTestPage';
import { KeychainTestPage } from './pages/KeychainTestPage';
import { LiteCardTestPage } from './pages/LiteCardTestPage';

export type RouteScreen = 
  | 'home'
  | 'background-thread'
  | 'biometric-auth'
  | 'cloud-kit'
  | 'keychain'
  | 'lite-card';

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
    id: 'keychain' as RouteScreen,
    name: 'Keychain Module',
    description: 'Test secure storage operations',
  },
  {
    id: 'lite-card' as RouteScreen,
    name: 'Lite Card',
    description: 'Test NFC card operations and management',
  },
];

export function Router({ safeAreaInsets }: RouterProps) {
  const [currentScreen, setCurrentScreen] = useState<RouteScreen>('home');

  const navigateTo = (screen: RouteScreen) => {
    setCurrentScreen(screen);
  };

  const renderHomeScreen = () => (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <View style={styles.header}>
        <Text style={styles.title}>Native Modules Test Suite</Text>
        <Text style={styles.subtitle}>Test all available native modules and their APIs</Text>
      </View>
      
      <View style={styles.moduleList}>
        {modules.map((module) => (
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
        ))}
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
      case 'keychain':
        return <KeychainTestPage {...commonProps} />;
      case 'lite-card':
        return <LiteCardTestPage {...commonProps} />;
      default:
        return renderHomeScreen();
    }
  };

  return (
    <View style={[styles.wrapper, { paddingTop: safeAreaInsets.top }]}>
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
});