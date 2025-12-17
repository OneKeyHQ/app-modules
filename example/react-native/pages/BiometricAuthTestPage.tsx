import React, { useState } from 'react';
import { View, Text, Alert } from 'react-native';
import { TestPageBase, TestButton, TestResult } from './TestPageBase';
import { checkBiometricAuthChanged } from '@onekeyfe/react-native-check-biometric-auth-changed';

interface BiometricAuthTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function BiometricAuthTestPage({ onGoHome, safeAreaInsets }: BiometricAuthTestPageProps) {
  const [result, setResult] = useState<boolean | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [lastChecked, setLastChecked] = useState<string | null>(null);

  const checkBiometricChanged = async () => {
    setIsLoading(true);
    setError(null);
    setResult(null);

    try {
      const hasChanged = await checkBiometricAuthChanged();
      setResult(hasChanged);
      setLastChecked(new Date().toLocaleString());
      
      Alert.alert(
        'Biometric Auth Status',
        hasChanged 
          ? 'Biometric authentication has changed!' 
          : 'Biometric authentication has not changed.',
        [{ text: 'OK' }]
      );
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : String(err);
      setError(errorMessage);
      Alert.alert('Error', `Failed to check biometric auth: ${errorMessage}`);
    } finally {
      setIsLoading(false);
    }
  };

  const resetTest = () => {
    setResult(null);
    setError(null);
    setLastChecked(null);
  };

  return (
    <TestPageBase title="Biometric Auth Changed Test" onGoHome={onGoHome} safeAreaInsets={safeAreaInsets}>
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 15, color: '#333' }}>
          Check Biometric Authentication Status
        </Text>
        
        <TestButton
          title={isLoading ? 'Checking...' : 'Check Biometric Auth Changed'}
          onPress={checkBiometricChanged}
          disabled={isLoading}
        />
        
        {(result !== null || error) && (
          <TestButton
            title="Reset Test"
            onPress={resetTest}
            style={{ backgroundColor: '#ff9500', marginTop: 10 }}
          />
        )}
      </View>

      {lastChecked && (
        <View style={{ backgroundColor: '#fff', padding: 15, borderRadius: 8 }}>
          <Text style={{ fontSize: 14, color: '#666' }}>
            Last checked: {lastChecked}
          </Text>
        </View>
      )}

      <TestResult result={result} error={error} />

      <View style={{ marginTop: 20, padding: 15, backgroundColor: '#fff', borderRadius: 8 }}>
        <Text style={{ fontSize: 14, color: '#666', lineHeight: 20 }}>
          <Text style={{ fontWeight: '600' }}>About this test:</Text>{'\n'}
          • This module checks if biometric authentication settings have changed{'\n'}
          • Returns <Text style={{ fontFamily: 'Courier New' }}>true</Text> if biometric auth has been modified{'\n'}
          • Returns <Text style={{ fontFamily: 'Courier New' }}>false</Text> if no changes detected{'\n'}
          • Useful for security-sensitive apps that need to detect biometric changes{'\n'}
          {'\n'}
          <Text style={{ fontWeight: '600' }}>Note:</Text> This feature may require specific device permissions and biometric hardware support.
        </Text>
      </View>
    </TestPageBase>
  );
}
