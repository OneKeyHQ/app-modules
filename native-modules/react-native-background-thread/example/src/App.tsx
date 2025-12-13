import { Text, View, StyleSheet } from 'react-native';
import { BackgroundThread } from 'react-native-background-thread';

import { useState } from 'react';
import { Button } from 'react-native';

export default function App() {
  const [result, setResult] = useState<string>('');


  const handleTestBackgroundThread = () => {
    try {
      // Test background thread functionality
      BackgroundThread.postMessage(JSON.stringify({ type: 'test' }));
      setResult('Testing background thread...');
      // Add your test logic here
    } catch (error) {
      setResult(`Error: ${error}`);
    }
  };

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Button
        title="Test Background Thread"
        onPress={handleTestBackgroundThread}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 10,
  },
});
