import React, { useState, useEffect } from 'react';
import { View, Text, Alert } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { BackgroundThread } from '@onekeyfe/react-native-background-thread';

interface BackgroundThreadTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function BackgroundThreadTestPage({ onGoHome, safeAreaInsets }: BackgroundThreadTestPageProps) {
  const [message, setMessage] = useState('');
  const [entryURL, setEntryURL] = useState('https://example.com/background.js');
  const [receivedMessages, setReceivedMessages] = useState<string[]>([]);
  const [isListening, setIsListening] = useState(false);

  useEffect(() => {
    if (isListening) {
      // Setup event listener for background messages
      const listener = BackgroundThread.onBackgroundMessage?.addListener?.((receivedMessage: string) => {
        setReceivedMessages(prev => [...prev, `${new Date().toLocaleTimeString()}: ${receivedMessage}`]);
      });

      return () => {
        listener?.remove?.();
      };
    }
  }, [isListening]);

  const postMessage = () => {
    if (!message.trim()) {
      Alert.alert('Error', 'Please enter a message to send');
      return;
    }

    try {
      BackgroundThread.postBackgroundMessage(message);
      setMessage('');
      Alert.alert('Success', 'Message sent to background thread');
    } catch (error) {
      Alert.alert('Error', `Failed to send message: ${error}`);
    }
  };

  const startBackgroundRunner = () => {
    if (!entryURL.trim()) {
      Alert.alert('Error', 'Please enter a valid entry URL');
      return;
    }

    try {
      BackgroundThread.startBackgroundRunnerWithEntryURL(entryURL);
      Alert.alert('Success', 'Background runner started');
    } catch (error) {
      Alert.alert('Error', `Failed to start background runner: ${error}`);
    }
  };

  const toggleListener = () => {
    setIsListening(!isListening);
    if (!isListening) {
      setReceivedMessages([]);
    }
  };

  const clearMessages = () => {
    setReceivedMessages([]);
  };

  return (
    <TestPageBase title="Background Thread Test" onGoHome={onGoHome} safeAreaInsets={safeAreaInsets}>
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Post Background Message
        </Text>
        
        <TestInput
          placeholder="Enter message to send to background thread"
          value={message}
          onChangeText={setMessage}
        />
        
        <TestButton
          title="Send Message"
          onPress={postMessage}
        />
      </View>

      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Start Background Runner
        </Text>
        
        <TestInput
          placeholder="Entry URL (e.g., https://example.com/background.js)"
          value={entryURL}
          onChangeText={setEntryURL}
        />
        
        <TestButton
          title="Start Background Runner"
          onPress={startBackgroundRunner}
        />
      </View>

      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Message Listener
        </Text>
        
        <TestButton
          title={isListening ? 'Stop Listening' : 'Start Listening'}
          onPress={toggleListener}
          style={{ backgroundColor: isListening ? '#ff3b30' : '#007AFF' }}
        />
        
        {receivedMessages.length > 0 && (
          <TestButton
            title="Clear Messages"
            onPress={clearMessages}
            style={{ backgroundColor: '#ff9500', marginTop: 10 }}
          />
        )}
      </View>

      {receivedMessages.length > 0 && (
        <TestResult
          result={{
            listening: isListening,
            messageCount: receivedMessages.length,
            messages: receivedMessages
          }}
        />
      )}

      <View style={{ marginTop: 20, padding: 15, backgroundColor: '#fff', borderRadius: 8 }}>
        <Text style={{ fontSize: 14, color: '#666', lineHeight: 20 }}>
          <Text style={{ fontWeight: '600' }}>Instructions:</Text>{'\n'}
          • Use "Post Background Message" to send messages to the background thread{'\n'}
          • Use "Start Background Runner" to initialize a background runner with a JavaScript entry point{'\n'}
          • Use "Message Listener" to listen for messages from the background thread{'\n'}
          • All received messages will be displayed with timestamps
        </Text>
      </View>
    </TestPageBase>
  );
}
