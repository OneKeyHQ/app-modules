import { useCallback, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import TextInput, {
  type IPasteEventParams,
} from '@onekeyfe/react-native-text-input';
import { TestPageBase } from './TestPageBase';

export function TextInputTestPage() {
  const [singleLineText, setSingleLineText] = useState('');
  const [multilineText, setMultilineText] = useState('');
  const [lastPaste, setLastPaste] = useState('No paste event yet');

  const handlePaste = useCallback((event: IPasteEventParams) => {
    const item = event.nativeEvent.items?.[0];
    setLastPaste(
      item == null
        ? 'Paste event contained no items'
        : `${item.type ?? 'unknown'}: ${item.data ?? ''}`,
    );
  }, []);

  return (
    <TestPageBase title="Text Input Test">
      <Text style={styles.label}>Single line</Text>
      <TextInput
        style={styles.input}
        value={singleLineText}
        onChangeText={setSingleLineText}
        onPaste={handlePaste}
        placeholder="Paste text or an image"
      />

      <Text style={styles.label}>Multiline</Text>
      <TextInput
        style={[styles.input, styles.multiline]}
        value={multilineText}
        onChangeText={setMultilineText}
        onPaste={handlePaste}
        placeholder="Paste text or an image"
        multiline
      />

      <View style={styles.result}>
        <Text style={styles.resultTitle}>Last paste</Text>
        <Text selectable>{lastPaste}</Text>
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  label: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 8,
    marginTop: 16,
  },
  input: {
    borderColor: '#C7C7CC',
    borderRadius: 10,
    borderWidth: 1,
    fontSize: 17,
    minHeight: 48,
    paddingHorizontal: 12,
    paddingVertical: 10,
  },
  multiline: {
    minHeight: 120,
    textAlignVertical: 'top',
  },
  result: {
    backgroundColor: '#F2F2F7',
    borderRadius: 10,
    marginTop: 24,
    padding: 12,
  },
  resultTitle: {
    fontWeight: '600',
    marginBottom: 8,
  },
});
