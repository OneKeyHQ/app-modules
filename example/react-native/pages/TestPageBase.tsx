import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, TextInput } from 'react-native';

interface TestPageBaseProps {
  title: string;
  onGoHome: () => void;
  safeAreaInsets: any;
  children: React.ReactNode;
}

export function TestPageBase({ title, onGoHome, safeAreaInsets, children }: TestPageBaseProps) {
  return (
    <View style={[styles.container, { paddingTop: safeAreaInsets.top + 10 }]}>
      <View style={styles.header}>
        <TouchableOpacity style={styles.backButton} onPress={onGoHome}>
          <Text style={styles.backButtonText}>← Back to Home</Text>
        </TouchableOpacity>
        <Text style={styles.title}>{title}</Text>
      </View>
      
      <ScrollView style={styles.content} contentContainerStyle={styles.contentContainer}>
        {children}
      </ScrollView>
    </View>
  );
}

interface TestButtonProps {
  title: string;
  onPress: () => void;
  disabled?: boolean;
  style?: any;
}

export function TestButton({ title, onPress, disabled = false, style }: TestButtonProps) {
  return (
    <TouchableOpacity
      style={[
        styles.testButton,
        disabled && styles.testButtonDisabled,
        style
      ]}
      onPress={onPress}
      disabled={disabled}
      activeOpacity={0.7}
    >
      <Text style={[
        styles.testButtonText,
        disabled && styles.testButtonTextDisabled
      ]}>
        {title}
      </Text>
    </TouchableOpacity>
  );
}

interface TestInputProps {
  placeholder: string;
  value: string;
  onChangeText: (text: string) => void;
  secureTextEntry?: boolean;
  multiline?: boolean;
}

export function TestInput({ placeholder, value, onChangeText, secureTextEntry = false, multiline = false }: TestInputProps) {
  return (
    <TextInput
      style={[styles.testInput, multiline && styles.testInputMultiline]}
      placeholder={placeholder}
      placeholderTextColor="#999"
      value={value}
      onChangeText={onChangeText}
      secureTextEntry={secureTextEntry}
      multiline={multiline}
    />
  );
}

interface TestResultProps {
  result: any;
  error?: string | null;
}

export function TestResult({ result, error }: TestResultProps) {
  if (error) {
    return (
      <View style={styles.resultContainer}>
        <Text style={styles.resultLabel}>Error:</Text>
        <Text style={styles.errorText}>{error}</Text>
      </View>
    );
  }

  if (result === null || result === undefined) {
    return null;
  }

  return (
    <View style={styles.resultContainer}>
      <Text style={styles.resultLabel}>Result:</Text>
      <Text style={styles.resultText}>
        {typeof result === 'object' ? JSON.stringify(result, null, 2) : String(result)}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    paddingHorizontal: 20,
    paddingBottom: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
    backgroundColor: '#fff',
  },
  backButton: {
    paddingVertical: 10,
    marginBottom: 10,
  },
  backButtonText: {
    fontSize: 16,
    color: '#007AFF',
    fontWeight: '500',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  content: {
    flex: 1,
  },
  contentContainer: {
    padding: 20,
    gap: 15,
  },
  testButton: {
    backgroundColor: '#007AFF',
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: 8,
    alignItems: 'center',
  },
  testButtonDisabled: {
    backgroundColor: '#ccc',
  },
  testButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '500',
  },
  testButtonTextDisabled: {
    color: '#999',
  },
  testInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    paddingHorizontal: 15,
    paddingVertical: 12,
    backgroundColor: '#fff',
    fontSize: 16,
    color: '#333',
  },
  testInputMultiline: {
    minHeight: 80,
    textAlignVertical: 'top',
  },
  resultContainer: {
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 8,
    marginTop: 10,
  },
  resultLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  resultText: {
    fontSize: 14,
    color: '#333',
    fontFamily: 'Courier New',
  },
  errorText: {
    fontSize: 14,
    color: '#ff3b30',
    fontFamily: 'Courier New',
  },
});
