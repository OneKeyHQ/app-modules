import { Text, View, StyleSheet } from 'react-native';
import { BackgroundThread } from 'react-native-background-thread';

import { useState } from 'react';
import { Button } from 'react-native';

BackgroundThread.startBackgroundRunnerWithEntryURL('http://localhost:8082/background.bundle?platform=ios&dev=true&lazy=true&minify=false&inlineSourceMap=false&modulesOnly=false&runModule=true&excludeSource=true&sourcePaths=url-server&app=backgroundthread.example');
export default function App() {
  const [result, setResult] = useState<string>('');


  const handleTestBackgroundThread = () => {
    const message = { type: 'test1' };
    BackgroundThread.onBackgroundMessage((event) => {
      setResult(`Message received from background thread: ${event}`);
    });
    BackgroundThread.postBackgroundMessage(JSON.stringify(message));
    setResult(`Message sent to background thread: ${JSON.stringify(message)}`);
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
