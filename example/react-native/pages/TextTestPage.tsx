import { useCallback, useRef, useState } from 'react';
import {
  StyleSheet,
  Text as ReactNativeText,
  View,
  type TextLayoutEvent,
} from 'react-native';
import { Text } from '@onekeyfe/react-native-text';

import { TestPageBase } from './TestPageBase';

export function TextTestPage() {
  const textRef = useRef<React.ElementRef<typeof ReactNativeText>>(null);
  const [isRefAttached, setIsRefAttached] = useState(false);
  const [lineCount, setLineCount] = useState(0);
  const [nestedPressCount, setNestedPressCount] = useState(0);
  const [rootPressCount, setRootPressCount] = useState(0);

  const handleRef = useCallback(
    (instance: React.ElementRef<typeof ReactNativeText> | null) => {
      textRef.current = instance;
      if (instance != null) {
        setIsRefAttached(true);
      }
    },
    [],
  );
  const handleTextLayout = useCallback((event: TextLayoutEvent) => {
    setLineCount(event.nativeEvent.lines.length);
  }, []);
  const handleNestedPress = useCallback(() => {
    setNestedPressCount(count => count + 1);
  }, []);
  const handleRootPress = useCallback(() => {
    setRootPressCount(count => count + 1);
  }, []);

  return (
    <TestPageBase title="Text Test">
      <ReactNativeText style={styles.status} testID="text-parity-mounted">
        PASS: parity fixtures mounted
      </ReactNativeText>

      <ReactNativeText style={styles.heading}>Intrinsic width</ReactNativeText>
      <View style={styles.comparisonRow}>
        <View style={styles.comparisonColumn}>
          <ReactNativeText style={styles.caption}>RN Text</ReactNativeText>
          <ReactNativeText style={styles.value} testID="rn-text-plain">
            自动 · US · 永不 · {100}
          </ReactNativeText>
        </View>
        <View style={styles.comparisonColumn}>
          <ReactNativeText style={styles.caption}>OneKey Text</ReactNativeText>
          <Text ref={handleRef} style={styles.value} testID="onekey-text-plain">
            自动 · US · 永不 · {100}
          </Text>
        </View>
      </View>

      <ReactNativeText style={styles.heading}>
        Nested styled and pressable text
      </ReactNativeText>
      <Text style={styles.value} testID="onekey-text-nested-root">
        Root text{' '}
        <Text
          onPress={handleNestedPress}
          style={styles.nestedPressable}
          testID="onekey-text-nested-pressable"
        >
          tap nested text
        </Text>
      </Text>
      <ReactNativeText style={styles.status} testID="text-press-status">
        {nestedPressCount > 0 ? 'PASS' : 'WAIT'}: nested press callback ·
        presses {nestedPressCount}
      </ReactNativeText>
      <Text
        onPress={handleRootPress}
        style={[styles.value, styles.rootPressable]}
        testID="onekey-text-root-pressable"
      >
        tap root text
      </Text>
      <ReactNativeText style={styles.status} testID="text-root-press-status">
        {rootPressCount > 0 ? 'PASS' : 'WAIT'}: root press callback · presses{' '}
        {rootPressCount}
      </ReactNativeText>

      <ReactNativeText style={styles.heading}>
        numberOfLines and ellipsizeMode
      </ReactNativeText>
      <View style={styles.comparisonRow}>
        <ReactNativeText
          ellipsizeMode="middle"
          numberOfLines={1}
          style={styles.ellipsized}
          testID="rn-text-ellipsize"
        >
          React Native long text for middle ellipsis
        </ReactNativeText>
        <Text
          ellipsizeMode="middle"
          numberOfLines={1}
          style={styles.ellipsized}
          testID="onekey-text-ellipsize"
        >
          OneKey native long text for middle ellipsis
        </Text>
      </View>

      <ReactNativeText style={styles.heading}>
        Selection and text layout event
      </ReactNativeText>
      <Text
        onTextLayout={handleTextLayout}
        selectable
        selectionColor="#007AFF"
        style={styles.selectable}
        testID="onekey-text-selectable-layout"
      >
        Long press to select this text. It also reports its native line layout.
      </Text>
      <ReactNativeText style={styles.status} testID="text-ref-status">
        {isRefAttached ? 'PASS' : 'WAIT'}: native ref attached
      </ReactNativeText>
      <ReactNativeText style={styles.status} testID="text-layout-status">
        {lineCount > 0 ? 'PASS' : 'WAIT'}: onTextLayout lines {lineCount}
      </ReactNativeText>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  caption: {
    color: '#8E8E93',
    fontSize: 12,
    marginBottom: 6,
  },
  comparisonColumn: {
    backgroundColor: '#FFFFFF',
    borderRadius: 10,
    flex: 1,
    padding: 12,
  },
  comparisonRow: {
    flexDirection: 'row',
    gap: 12,
  },
  ellipsized: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    fontSize: 16,
    padding: 10,
    width: 150,
  },
  heading: {
    color: '#1C1C1E',
    fontSize: 17,
    fontWeight: '600',
    marginTop: 12,
  },
  nestedPressable: {
    color: '#007AFF',
    fontWeight: '600',
    textDecorationLine: 'underline',
  },
  rootPressable: {
    color: '#007AFF',
    textDecorationLine: 'underline',
  },
  selectable: {
    backgroundColor: '#FFFFFF',
    borderRadius: 10,
    color: '#1C1C1E',
    fontSize: 16,
    lineHeight: 24,
    padding: 12,
  },
  status: {
    color: '#248A3D',
    fontSize: 14,
  },
  value: {
    color: '#1C1C1E',
    fontSize: 16,
    fontWeight: '500',
  },
});
