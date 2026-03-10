import { useState, useRef, useCallback } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton } from './TestPageBase';
import { AutoSizeInputView } from '@onekeyfe/react-native-auto-size-input';
import type { AutoSizeInputMethods } from '@onekeyfe/react-native-auto-size-input';
import { callback } from 'react-native-nitro-modules';

export function AutoSizeInputTestPage() {
  const [singleLineText, setSingleLineText] = useState('');
  const [multiLineText, setMultiLineText] = useState('');
  const [autoWidthText, setAutoWidthText] = useState('');
  const [focusStatus, setFocusStatus] = useState('');
  const [editable, setEditable] = useState(true);

  const singleLineRef = useRef<AutoSizeInputMethods>(null);
  const multiLineRef = useRef<AutoSizeInputMethods>(null);

  return (
    <TestPageBase
      title="Auto Size Input Test"
    >
      {/* Section: Basic Single Line */}
      <Text style={styles.sectionTitle}>Single Line - Basic</Text>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          ref={singleLineRef}
          style={styles.input}
          text={singleLineText}
          placeholder="Type to see font shrink..."
          fontSize={36}
          minFontSize={12}
          textColor="#333333"
          placeholderColor="#AAAAAA"
          selectionColor="#007AFF"
          onChangeText={callback(useCallback((text: string) => setSingleLineText(text), []))}
          onFocus={callback(useCallback(() => setFocusStatus('Single line focused'), []))}
          onBlur={callback(useCallback(() => setFocusStatus('Single line blurred'), []))}
        />
      </View>
      <Text style={styles.statusText}>Status: {focusStatus || 'idle'}</Text>
      <Text style={styles.statusText}>Text: "{singleLineText}"</Text>

      {/* Section: New Prop - showBorder */}
      <Text style={styles.sectionTitle}>New Prop - showBorder (default: no border)</Text>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          style={styles.input}
          text=""
          placeholder="Default is no border"
          fontSize={28}
          minFontSize={12}
          textColor="#333333"
          placeholderColor="#AAAAAA"
          onChangeText={callback(useCallback(() => {}, []))}
        />
      </View>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          style={styles.input}
          text=""
          showBorder={true}
          placeholder="showBorder=true"
          fontSize={28}
          minFontSize={12}
          textColor="#333333"
          placeholderColor="#AAAAAA"
          onChangeText={callback(useCallback(() => {}, []))}
        />
      </View>

      {/* Section: New Prop - inputBackgroundColor */}
      <Text style={styles.sectionTitle}>New Prop - inputBackgroundColor (default: transparent)</Text>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          style={styles.input}
          text=""
          inputBackgroundColor="#E8F4FF"
          placeholder="Custom background color"
          fontSize={28}
          minFontSize={12}
          textColor="#333333"
          placeholderColor="#7A8796"
          onChangeText={callback(useCallback(() => {}, []))}
        />
      </View>

      {/* Section: New Prop - contentAutoWidth */}
      <Text style={styles.sectionTitle}>New Prop - contentAutoWidth (suffix follows text)</Text>
      <View style={styles.autoWidthContainer}>
        <AutoSizeInputView
          style={styles.autoWidthInput}
          text={autoWidthText}
          contentAutoWidth={true}
          prefix="$"
          suffix="USD"
          placeholder=""
          fontSize={34}
          minFontSize={14}
          textColor="#333333"
          prefixColor="#007AFF"
          suffixColor="#999999"
          prefixMarginRight={4}
          suffixMarginLeft={8}
          keyboardType="decimalPad"
          onChangeText={callback(useCallback((text: string) => setAutoWidthText(text), []))}
        />
      </View>

      {/* Section: With Prefix & Suffix */}
      <Text style={styles.sectionTitle}>Single Line - Prefix & Suffix</Text>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          style={styles.input}
          text=""
          prefix="$"
          suffix="USD"
          placeholder="0.00"
          fontSize={48}
          minFontSize={14}
          textColor="#333333"
          prefixColor="#007AFF"
          suffixColor="#999999"
          placeholderColor="#CCCCCC"
          prefixMarginRight={4}
          suffixMarginLeft={8}
          keyboardType="decimalPad"
          textAlign="center"
          selectionColor="#007AFF"
          onChangeText={callback(useCallback((text: string) => {console.log(text)}, []))}
        />
      </View>

      {/* Section: Font Weight & Family */}
      <Text style={styles.sectionTitle}>Single Line - Bold + Right Aligned</Text>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          style={styles.input}
          text=""
          placeholder="Bold text here"
          fontSize={32}
          minFontSize={10}
          fontWeight="bold"
          textAlign="right"
          textColor="#FF3B30"
          placeholderColor="#FFAAAA"
          selectionColor="#FF3B30"
          onChangeText={callback(useCallback(() => {}, []))}
        />
      </View>

      {/* Section: Multi Line */}
      <Text style={styles.sectionTitle}>Multi Line (max 3 lines)</Text>
      <View style={[styles.inputContainer, styles.multiLineContainer]}>
        <AutoSizeInputView
          ref={multiLineRef}
          style={styles.multiLineInput}
          text={multiLineText}
          multiline={true}
          maxNumberOfLines={3}
          placeholder="Type a long message..."
          fontSize={28}
          minFontSize={12}
          textColor="#333333"
          placeholderColor="#AAAAAA"
          selectionColor="#34C759"
          onChangeText={callback(useCallback((text: string) => setMultiLineText(text), []))}
          onFocus={callback(useCallback(() => setFocusStatus('Multi line focused'), []))}
          onBlur={callback(useCallback(() => setFocusStatus('Multi line blurred'), []))}
        />
      </View>

      {/* Section: Multi Line with Prefix/Suffix */}
      <Text style={styles.sectionTitle}>Multi Line - Prefix & Suffix</Text>
      <View style={[styles.inputContainer, styles.multiLineContainer]}>
        <AutoSizeInputView
          style={styles.multiLineInput}
          text=""
          prefix="Note:"
          suffix="..."
          multiline={true}
          maxNumberOfLines={4}
          placeholder="Write your notes"
          fontSize={24}
          minFontSize={10}
          textColor="#333333"
          prefixColor="#FF9500"
          suffixColor="#8E8E93"
          placeholderColor="#CCCCCC"
          prefixMarginRight={8}
          suffixMarginLeft={4}
          selectionColor="#FF9500"
          onChangeText={callback(useCallback(() => {}, []))}
        />
      </View>

      {/* Section: Keyboard & Input Behavior */}
      <Text style={styles.sectionTitle}>Email Input (autoCorrect off)</Text>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          style={styles.input}
          text=""
          placeholder="email@example.com"
          fontSize={28}
          minFontSize={12}
          keyboardType="emailAddress"
          returnKeyType="done"
          autoCorrect={false}
          autoCapitalize="none"
          textColor="#333333"
          placeholderColor="#AAAAAA"
          selectionColor="#5856D6"
          onChangeText={callback(useCallback(() => {}, []))}
        />
      </View>

      {/* Section: Sentences autoCapitalize */}
      <Text style={styles.sectionTitle}>Sentences Auto-Capitalize</Text>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          style={styles.input}
          text=""
          placeholder="Start typing a sentence..."
          fontSize={24}
          minFontSize={12}
          autoCapitalize="sentences"
          autoCorrect={true}
          returnKeyType="next"
          textColor="#333333"
          placeholderColor="#AAAAAA"
          selectionColor="#007AFF"
          onChangeText={callback(useCallback(() => {}, []))}
        />
      </View>

      {/* Section: Editable Toggle */}
      <Text style={styles.sectionTitle}>Editable Toggle</Text>
      <View style={styles.inputContainer}>
        <AutoSizeInputView
          style={styles.input}
          text="This text is controlled"
          editable={editable}
          fontSize={24}
          minFontSize={12}
          textColor={editable ? '#333333' : '#999999'}
          selectionColor="#007AFF"
          onChangeText={callback(useCallback(() => {}, []))}
        />
      </View>
      <TestButton
        title={editable ? 'Disable Editing' : 'Enable Editing'}
        onPress={() => setEditable(!editable)}
      />

      {/* Section: Focus / Blur Methods */}
      <Text style={styles.sectionTitle}>Focus / Blur Methods</Text>
      <View style={styles.row}>
        <TestButton
          title="Focus Single"
          onPress={() => singleLineRef.current?.focus()}
          style={styles.halfButton}
        />
        <TestButton
          title="Blur Single"
          onPress={() => singleLineRef.current?.blur()}
          style={styles.halfButton}
        />
      </View>
      <View style={styles.row}>
        <TestButton
          title="Focus Multi"
          onPress={() => multiLineRef.current?.focus()}
          style={styles.halfButton}
        />
        <TestButton
          title="Blur Multi"
          onPress={() => multiLineRef.current?.blur()}
          style={styles.halfButton}
        />
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginTop: 10,
    marginBottom: 8,
  },
  inputContainer: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 12,
    borderWidth: 1,
    borderColor: '#E0E0E0',
  },
  multiLineContainer: {
    minHeight: 100,
  },
  autoWidthContainer: {
    padding: 12,
    borderWidth: 0,
    width: '100%',
    height: 74,
    justifyContent: 'center',
  },
  input: {
    width: '100%',
    height: 50,
  },
  autoWidthInput: {
    height: 50,
  },
  multiLineInput: {
    width: '100%',
    height: 90,
  },
  statusText: {
    fontSize: 13,
    color: '#666',
    marginTop: 4,
  },
  row: {
    flexDirection: 'row',
    gap: 10,
  },
  halfButton: {
    flex: 1,
  },
});
