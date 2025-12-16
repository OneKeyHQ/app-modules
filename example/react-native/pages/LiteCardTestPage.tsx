import React, { useState, useEffect } from 'react';
import { View, Text, Alert, Switch } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import onekeyLite, { CardErrors, type NfcConnectUiState } from '@onekeyfe/react-native-lite-card';

interface LiteCardTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function LiteCardTestPage({ onGoHome, safeAreaInsets }: LiteCardTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [connectionState, setConnectionState] = useState<NfcConnectUiState | null>(null);
  const [isListening, setIsListening] = useState(false);
  
  // Form inputs
  const [mnemonic, setMnemonic] = useState('abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about');
  const [pin, setPin] = useState('123456');
  const [newPin, setNewPin] = useState('654321');
  const [overwrite, setOverwrite] = useState(false);

  useEffect(() => {
    if (isListening) {
      const removeListener = onekeyLite.addConnectListener((event: NfcConnectUiState) => {
        setConnectionState(event);
      });

      return () => {
        removeListener?.();
      };
    }
  }, [isListening]);

  const executeLiteCardOperation = async (operation: () => Promise<any>, operationName: string) => {
    setIsLoading(true);
    setError(null);
    setResult(null);

    try {
      const res = await operation();
      
      if (res.error) {
        const errorCode = res.error.code;
        const errorName = Object.entries(CardErrors).find(([, code]) => code === errorCode)?.[0] || 'Unknown';
        throw new Error(`${errorName} (${errorCode}): ${res.error.message || 'Unknown error'}`);
      }
      
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

  const getLiteInfo = () => {
    executeLiteCardOperation(() => onekeyLite.getLiteInfo(), 'Get Lite Info');
  };

  const checkNFCPermission = () => {
    executeLiteCardOperation(() => onekeyLite.checkNFCPermission(), 'Check NFC Permission');
  };

  const setMnemonicOnCard = () => {
    if (!mnemonic.trim()) {
      Alert.alert('Error', 'Mnemonic is required');
      return;
    }
    if (!pin.trim()) {
      Alert.alert('Error', 'PIN is required');
      return;
    }

    executeLiteCardOperation(
      () => onekeyLite.setMnemonic(mnemonic.trim(), pin.trim(), overwrite),
      'Set Mnemonic'
    );
  };

  const getMnemonicFromCard = () => {
    if (!pin.trim()) {
      Alert.alert('Error', 'PIN is required');
      return;
    }

    executeLiteCardOperation(
      () => onekeyLite.getMnemonicWithPin(pin.trim()),
      'Get Mnemonic'
    );
  };

  const changePinOnCard = () => {
    if (!pin.trim() || !newPin.trim()) {
      Alert.alert('Error', 'Both old and new PINs are required');
      return;
    }

    executeLiteCardOperation(
      () => onekeyLite.changePin(pin.trim(), newPin.trim()),
      'Change PIN'
    );
  };

  const resetCard = () => {
    Alert.alert(
      'Reset Card',
      'This will permanently delete all data on the card. Are you sure?',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Reset',
          style: 'destructive',
          onPress: () => executeLiteCardOperation(() => onekeyLite.reset(), 'Reset Card')
        }
      ]
    );
  };

  const cancelOperation = () => {
    try {
      onekeyLite.cancel();
      Alert.alert('Info', 'Current operation cancelled');
    } catch (err) {
      Alert.alert('Error', `Failed to cancel: ${err}`);
    }
  };

  const openSettings = () => {
    try {
      onekeyLite.intoSetting();
      Alert.alert('Info', 'Opening device settings');
    } catch (err) {
      Alert.alert('Error', `Failed to open settings: ${err}`);
    }
  };

  const toggleListener = () => {
    setIsListening(!isListening);
    if (!isListening) {
      setConnectionState(null);
    }
  };

  const resetForm = () => {
    setResult(null);
    setError(null);
    setConnectionState(null);
    setMnemonic('abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about');
    setPin('123456');
    setNewPin('654321');
    setOverwrite(false);
  };

  return (
    <TestPageBase title="Lite Card Test" onGoHome={onGoHome} safeAreaInsets={safeAreaInsets}>
      {/* NFC Status and Permissions */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          NFC Status & Permissions
        </Text>
        
        <TestButton
          title={isLoading ? 'Checking...' : 'Check NFC Permission'}
          onPress={checkNFCPermission}
          disabled={isLoading}
        />
        
        <TestButton
          title="Open NFC Settings"
          onPress={openSettings}
          style={{ marginTop: 10, backgroundColor: '#ff9500' }}
        />
      </View>

      {/* Card Information */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Card Information
        </Text>
        
        <TestButton
          title={isLoading ? 'Reading...' : 'Get Lite Card Info'}
          onPress={getLiteInfo}
          disabled={isLoading}
        />
      </View>

      {/* Mnemonic Operations */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Mnemonic Operations
        </Text>
        
        <TestInput
          placeholder="Mnemonic (12 or 24 words)"
          value={mnemonic}
          onChangeText={setMnemonic}
          multiline
        />
        
        <TestInput
          placeholder="PIN (6 digits recommended)"
          value={pin}
          onChangeText={setPin}
          secureTextEntry
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
          <Text style={{ fontSize: 16, color: '#333' }}>Overwrite Existing</Text>
          <Switch
            value={overwrite}
            onValueChange={setOverwrite}
            trackColor={{ false: '#767577', true: '#81b0ff' }}
            thumbColor={overwrite ? '#007AFF' : '#f4f3f4'}
          />
        </View>
        
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>
          <TestButton
            title="Set Mnemonic"
            onPress={setMnemonicOnCard}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120 }}
          />
          <TestButton
            title="Get Mnemonic"
            onPress={getMnemonicFromCard}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120 }}
          />
        </View>
      </View>

      {/* PIN Management */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          PIN Management
        </Text>
        
        <TestInput
          placeholder="New PIN (6 digits recommended)"
          value={newPin}
          onChangeText={setNewPin}
          secureTextEntry
        />
        
        <TestButton
          title="Change PIN"
          onPress={changePinOnCard}
          disabled={isLoading}
        />
      </View>

      {/* Dangerous Operations */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Card Management
        </Text>
        
        <TestButton
          title="Reset Card (Danger!)"
          onPress={resetCard}
          disabled={isLoading}
          style={{ backgroundColor: '#ff3b30' }}
        />
        
        <TestButton
          title="Cancel Current Operation"
          onPress={cancelOperation}
          style={{ marginTop: 10, backgroundColor: '#ff9500' }}
        />
      </View>

      {/* Connection Monitoring */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Connection Monitoring
        </Text>
        
        <TestButton
          title={isListening ? 'Stop Monitoring' : 'Start Monitoring'}
          onPress={toggleListener}
          style={{ backgroundColor: isListening ? '#ff3b30' : '#007AFF' }}
        />
        
        {connectionState && (
          <View style={{ backgroundColor: '#fff', padding: 15, borderRadius: 8, marginTop: 10 }}>
            <Text style={{ fontSize: 14, fontWeight: '600', color: '#333' }}>Connection State:</Text>
            <Text style={{ fontSize: 14, color: '#666', marginTop: 5 }}>
              Code: {connectionState.code}{'\n'}
              Message: {connectionState.message}
            </Text>
          </View>
        )}
      </View>

      <TestButton
        title="Reset Form"
        onPress={resetForm}
        disabled={isLoading}
        style={{ backgroundColor: '#ff9500' }}
      />

      <TestResult result={result} error={error} />

      <View style={{ marginTop: 20, padding: 15, backgroundColor: '#fff', borderRadius: 8 }}>
        <Text style={{ fontSize: 14, color: '#666', lineHeight: 20 }}>
          <Text style={{ fontWeight: '600' }}>OneKey Lite Card Operations:</Text>{'\n'}
          • <Text style={{ fontWeight: '500' }}>NFC Permission:</Text> Check NFC hardware availability{'\n'}
          • <Text style={{ fontWeight: '500' }}>Card Info:</Text> Get card status and information{'\n'}
          • <Text style={{ fontWeight: '500' }}>Set Mnemonic:</Text> Store seed phrase on card{'\n'}
          • <Text style={{ fontWeight: '500' }}>Get Mnemonic:</Text> Retrieve seed phrase with PIN{'\n'}
          • <Text style={{ fontWeight: '500' }}>Change PIN:</Text> Update card access PIN{'\n'}
          • <Text style={{ fontWeight: '500' }}>Reset Card:</Text> Factory reset (deletes all data){'\n'}
          {'\n'}
          <Text style={{ fontWeight: '600' }}>Requirements:</Text>{'\n'}
          • NFC-enabled device{'\n'}
          • OneKey Lite hardware card{'\n'}
          • Proper NFC permissions
        </Text>
      </View>
    </TestPageBase>
  );
}
