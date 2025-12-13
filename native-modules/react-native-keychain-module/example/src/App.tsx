import { useState } from 'react';
import { Text, View, StyleSheet, Button, ScrollView } from 'react-native';
import { KeychainModule } from '@onekeyfe/react-native-keychain-module';

export default function App() {
  const [result, setResult] = useState<string>('Ready to test...');

  const appendResult = (text: string) => {
    setResult((prev) => prev + '\n' + text);
  };

  const clearResult = () => {
    setResult('');
  };

  const testSetItem = async () => {
    try {
      clearResult();
      appendResult('Testing setItem...');
      const result = await KeychainModule.setItem({
        key: 'test_key',
        value: 'test_value_123',
        label: 'Test Label',
        description: 'Test Description',
        enableSync: true,
      });
      appendResult(`Success: ${result}`);
    } catch (error) {
      appendResult(`Error: ${error}`);
    }
  };

  const testGetItem = async () => {
    try {
      clearResult();
      appendResult('Testing getItem...');
      const result = await KeychainModule.getItem({
        key: 'test_key',
      });
      appendResult(`Result: ${JSON.stringify(result, null, 2)}`);
    } catch (error) {
      appendResult(`Error: ${error}`);
    }
  };

  const testHasItem = async () => {
    try {
      clearResult();
      appendResult('Testing hasItem...');
      const result = await KeychainModule.hasItem({
        key: 'test_key',
      });
      appendResult(`Result: ${result}`);
    } catch (error) {
      appendResult(`Error: ${error}`);
    }
  };

  const testIsICloudSyncEnabled = async () => {
    try {
      clearResult();
      appendResult('Testing isICloudSyncEnabled...');
      const result = await KeychainModule.isICloudSyncEnabled();
      appendResult(`Result: ${result}`);
    } catch (error) {
      appendResult(`Error: ${error}`);
    }
  };

  const testRemoveItem = async () => {
    try {
      clearResult();
      appendResult('Testing removeItem...');
      const result = await KeychainModule.removeItem({
        key: 'test_key',
      });
      appendResult(`Success: ${result}`);
    } catch (error) {
      appendResult(`Error: ${error}`);
    }
  };

  const testAll = async () => {
    try {
      clearResult();
      
      appendResult('1. Testing setItem...');
      const setResult = await KeychainModule.setItem({
        key: 'test_key',
        value: 'test_value_123',
        label: 'Test Label',
        description: 'Test Description',
        enableSync: true,
      });
      appendResult(`   Result: ${setResult}\n`);

      appendResult('2. Testing getItem...');
      const getResult = await KeychainModule.getItem({
        key: 'test_key',
      });
      appendResult(`   Result: ${JSON.stringify(getResult)}\n`);

      appendResult('3. Testing hasItem...');
      const hasResult = await KeychainModule.hasItem({
        key: 'test_key',
      });
      appendResult(`   Result: ${hasResult}\n`);

      appendResult('4. Testing isICloudSyncEnabled...');
      const syncResult = await KeychainModule.isICloudSyncEnabled();
      appendResult(`   Result: ${syncResult}\n`);

      appendResult('5. Testing removeItem...');
      const removeResult = await KeychainModule.removeItem({
        key: 'test_key',
      });
      appendResult(`   Result: ${removeResult}\n`);

      appendResult('6. Verifying removal with hasItem...');
      const hasAfterRemove = await KeychainModule.hasItem({
        key: 'test_key',
      });
      appendResult(`   Result: ${hasAfterRemove}\n`);

      appendResult('All tests completed!');
    } catch (error) {
      appendResult(`Error: ${error}`);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.resultText}>{result}</Text>
      <ScrollView style={styles.scrollView}>
      <View style={styles.buttonContainer}>
        <Button title="Set Item" onPress={testSetItem} />
        <Button title="Get Item" onPress={testGetItem} />
        <Button title="Has Item" onPress={testHasItem} />
        <Button title="Check iCloud Sync" onPress={testIsICloudSyncEnabled} />
        <Button title="Remove Item" onPress={testRemoveItem} />
        <Button title="Run All Tests" onPress={testAll} />
        <Button title="Clear" onPress={clearResult} />
      </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    paddingTop: 60,
  },
  scrollView: {
    flex: 1,
    width: '100%',
    marginBottom: 20,
  },
  resultText: {
    paddingTop: 40,
    fontFamily: 'monospace',
    fontSize: 12,
  },
  buttonContainer: {
    gap: 10,
  },
});
