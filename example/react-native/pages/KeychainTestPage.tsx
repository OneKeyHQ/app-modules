import React, { useState } from 'react';
import { View, Text, Alert, Switch } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { KeychainModule } from '@onekeyfe/react-native-keychain-module';
import type { SetItemParams, GetItemParams, RemoveItemParams, HasItemParams } from '@onekeyfe/react-native-keychain-module';

interface KeychainTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function KeychainTestPage({ onGoHome, safeAreaInsets }: KeychainTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  
  // Form inputs
  const [key, setKey] = useState('test-key');
  const [value, setValue] = useState('test-value-123');
  const [label, setLabel] = useState('Test Item');
  const [description, setDescription] = useState('Test keychain item description');
  const [enableSync, setEnableSync] = useState(true);

  const executeKeychainOperation = async (operation: () => Promise<any>, operationName: string) => {
    setIsLoading(true);
    setError(null);
    setResult(null);

    try {
      const res = await operation();
      setResult(res);
      Alert.alert('Success', `${operationName} completed successfully`);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : String(err);
      setError(errorMessage);
      Alert.alert('Error', `${operationName} failed: ${errorMessage}`);
    } finally {
      setIsLoading(false);
    }
  };

  const setItem = () => {
    if (!key.trim()) {
      Alert.alert('Error', 'Key is required');
      return;
    }

    const params: SetItemParams = {
      key: key.trim(),
      value: value.trim(),
      enableSync,
      label: label.trim() || undefined,
      description: description.trim() || undefined,
    };

    executeKeychainOperation(() => KeychainModule.setItem(params), 'Set Item');
  };

  const getItem = () => {
    if (!key.trim()) {
      Alert.alert('Error', 'Key is required');
      return;
    }

    const params: GetItemParams = {
      key: key.trim(),
    };

    executeKeychainOperation(() => KeychainModule.getItem(params), 'Get Item');
  };

  const removeItem = () => {
    if (!key.trim()) {
      Alert.alert('Error', 'Key is required');
      return;
    }

    const params: RemoveItemParams = {
      key: key.trim(),
    };

    executeKeychainOperation(() => KeychainModule.removeItem(params), 'Remove Item');
  };

  const hasItem = () => {
    if (!key.trim()) {
      Alert.alert('Error', 'Key is required');
      return;
    }

    const params: HasItemParams = {
      key: key.trim(),
    };

    executeKeychainOperation(() => KeychainModule.hasItem(params), 'Check Item Exists');
  };

  const checkiCloudSync = () => {
    executeKeychainOperation(() => KeychainModule.isICloudSyncEnabled(), 'Check iCloud Sync Status');
  };

  const resetForm = () => {
    setResult(null);
    setError(null);
    setKey('test-key');
    setValue('test-value-123');
    setLabel('Test Item');
    setDescription('Test keychain item description');
    setEnableSync(true);
  };

  return (
    <TestPageBase title="Keychain Module Test" onGoHome={onGoHome} safeAreaInsets={safeAreaInsets}>
      {/* iCloud Sync Status */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          iCloud Sync Status
        </Text>
        
        <TestButton
          title={isLoading ? 'Checking...' : 'Check iCloud Sync Enabled'}
          onPress={checkiCloudSync}
          disabled={isLoading}
        />
      </View>

      {/* Keychain Operations */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Keychain Operations
        </Text>
        
        <TestInput
          placeholder="Key (e.g., test-key)"
          value={key}
          onChangeText={setKey}
        />
        
        <TestInput
          placeholder="Value (for set operation)"
          value={value}
          onChangeText={setValue}
        />
        
        <TestInput
          placeholder="Label (optional)"
          value={label}
          onChangeText={setLabel}
        />
        
        <TestInput
          placeholder="Description (optional)"
          value={description}
          onChangeText={setDescription}
          multiline
        />
        
        <View style={{ 
          flexDirection: 'row', 
          alignItems: 'center', 
          justifyContent: 'space-between',
          backgroundColor: '#fff',
          padding: 15,
          borderRadius: 8,
          marginVertical: 10
        }}>
          <Text style={{ fontSize: 16, color: '#333' }}>Enable iCloud Sync</Text>
          <Switch
            value={enableSync}
            onValueChange={setEnableSync}
            trackColor={{ false: '#767577', true: '#81b0ff' }}
            thumbColor={enableSync ? '#007AFF' : '#f4f3f4'}
          />
        </View>
        
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10, marginTop: 10 }}>
          <TestButton
            title="Set Item"
            onPress={setItem}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120 }}
          />
          <TestButton
            title="Get Item"
            onPress={getItem}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120 }}
          />
        </View>
        
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10, marginTop: 10 }}>
          <TestButton
            title="Remove Item"
            onPress={removeItem}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120, backgroundColor: '#ff3b30' }}
          />
          <TestButton
            title="Has Item?"
            onPress={hasItem}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120 }}
          />
        </View>
        
        <TestButton
          title="Reset Form"
          onPress={resetForm}
          disabled={isLoading}
          style={{ marginTop: 10, backgroundColor: '#ff9500' }}
        />
      </View>

      <TestResult result={result} error={error} />

      <View style={{ marginTop: 20, padding: 15, backgroundColor: '#fff', borderRadius: 8 }}>
        <Text style={{ fontSize: 14, color: '#666', lineHeight: 20 }}>
          <Text style={{ fontWeight: '600' }}>Keychain Operations:</Text>{'\n'}
          • <Text style={{ fontWeight: '500' }}>Set Item:</Text> Store a secure key-value pair{'\n'}
          • <Text style={{ fontWeight: '500' }}>Get Item:</Text> Retrieve a stored value by key{'\n'}
          • <Text style={{ fontWeight: '500' }}>Remove Item:</Text> Delete a stored item{'\n'}
          • <Text style={{ fontWeight: '500' }}>Has Item:</Text> Check if a key exists{'\n'}
          • <Text style={{ fontWeight: '500' }}>iCloud Sync:</Text> Check if keychain syncing is enabled{'\n'}
          {'\n'}
          <Text style={{ fontWeight: '600' }}>Features:</Text>{'\n'}
          • Secure storage using system keychain{'\n'}
          • Optional iCloud keychain synchronization{'\n'}
          • Customizable labels and descriptions{'\n'}
          • Automatic encryption and access control
        </Text>
      </View>
    </TestPageBase>
  );
}
