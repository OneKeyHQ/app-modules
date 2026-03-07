import React, { useState } from 'react';
import { View, Text } from 'react-native';
import { TestPageBase, TestButton } from './TestPageBase';
import { BackgroundThread } from '@onekeyfe/react-native-background-thread';

BackgroundThread.initBackgroundThread();

export function BackgroundThreadTestPage() {
  const [result, setResult] = useState<string>('');


  const handlePostMessage = () => {
    const message = { type: 'test1' };
    BackgroundThread.onBackgroundMessage((event) => {
      setResult(`Message received from background thread: ${event}`);
    });
    BackgroundThread.postBackgroundMessage(JSON.stringify(message));
    setResult(`Message sent to background thread: ${JSON.stringify(message)}`);
  };

  return (
    <TestPageBase title="Background Thread Test">
      <View>
      <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>Result: {result}</Text>
        <TestButton
          title="Send Message"
          onPress={handlePostMessage}
        />
      </View>
    </TestPageBase>
  );
}
