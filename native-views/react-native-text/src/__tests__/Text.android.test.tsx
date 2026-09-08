import { createRef } from 'react';
import { Text as ReactNativeText, View } from 'react-native';
import ReactTestRenderer, {
  type ReactTestInstance,
  type ReactTestRenderer as TestRenderer,
} from 'react-test-renderer';

jest.mock('../OneKeyTextNativeHost', () => ({
  __esModule: true,
  default: 'OneKeyText',
}));

import { Text } from '../Text.android';

const NativeTextTestComponent = 'OneKeyText' as React.ElementType;

function render(element: React.ReactElement) {
  let renderer: TestRenderer | undefined;
  ReactTestRenderer.act(() => {
    renderer = ReactTestRenderer.create(element);
  });
  if (renderer == null) {
    throw new Error('Renderer was not created');
  }
  return renderer.root;
}

function findNativeText(root: ReactTestInstance) {
  return root.findByType(NativeTextTestComponent);
}

describe('Android Text', () => {
  it('uses OneKeyText for the root paragraph and forwards paragraph props', () => {
    const onTextLayout = jest.fn();
    const ref = createRef<React.ElementRef<typeof ReactNativeText>>();
    const root = render(
      <Text
        ref={ref}
        allowFontScaling={false}
        android_hyphenationFrequency="full"
        dataDetectorType="link"
        ellipsizeMode="middle"
        numberOfLines={1}
        onTextLayout={onTextLayout}
        selectable
        selectionColor="red"
        style={{ fontWeight: 500, userSelect: 'text', verticalAlign: 'middle' }}
        testID="root-text"
      >
        自动
      </Text>
    );

    const nativeText = findNativeText(root);
    expect(nativeText.children).toHaveLength(1);
    expect(
      nativeText.children.every((child) => typeof child === 'object')
    ).toBe(true);
    expect(nativeText.props.allowFontScaling).toBe(false);
    expect(nativeText.props.android_hyphenationFrequency).toBe('full');
    expect(nativeText.props.dataDetectorType).toBe('link');
    expect(nativeText.props.ellipsizeMode).toBe('middle');
    expect(nativeText.props.numberOfLines).toBe(1);
    expect(nativeText.props.onTextLayout).toBe(onTextLayout);
    expect(nativeText.props.selectable).toBe(true);
    expect(nativeText.props.testID).toBe('root-text');
  });

  it('places raw text, nested text, and attachments below RN virtual text', () => {
    const root = render(
      <Text testID="root-content">
        Value {42}
        <Text testID="nested-content">Nested</Text>
        <View testID="text-attachment" />
      </Text>
    );

    const nativeText = findNativeText(root);
    expect(nativeText.children).toHaveLength(1);
    expect(
      nativeText.children.every(
        (child) => typeof child !== 'string' && typeof child !== 'number'
      )
    ).toBe(true);
    expect(
      root.findAllByProps({ testID: 'nested-content' }).length
    ).toBeGreaterThan(0);
    expect(
      root.findAllByProps({ testID: 'text-attachment' }).length
    ).toBeGreaterThan(0);
  });

  it('keeps nested text in the React Native virtual text stack', () => {
    const onPress = jest.fn();
    const root = render(
      <Text testID="root">
        Root
        <Text onPress={onPress} style={{ color: 'blue' }} testID="nested">
          Nested
        </Text>
      </Text>
    );

    expect(root.findAllByType(NativeTextTestComponent)).toHaveLength(1);
    expect(root.findAllByProps({ testID: 'nested' }).length).toBeGreaterThan(0);
    const nestedReactNativeText = root
      .findAllByType(ReactNativeText)
      .find((node) => node.props.testID === 'nested');
    expect(nestedReactNativeText).toBeDefined();
    expect(nestedReactNativeText?.props.onPress).toBe(onPress);
    expect(nestedReactNativeText?.props.children).toBe('Nested');
  });

  it('uses React Native pressability semantics on a pressable root', () => {
    const onPress = jest.fn();
    const onLongPress = jest.fn();
    const root = render(
      <Text onLongPress={onLongPress} onPress={onPress} testID="pressable">
        Press
      </Text>
    );

    const nativeText = findNativeText(root);
    expect(nativeText.props.accessible).toBe(true);
    expect(nativeText.props.accessibilityRole).toBe('link');
    expect(nativeText.props.isHighlighted).toBe(false);
    expect(nativeText.props.isPressable).toBe(true);
    expect(nativeText.props.onClick).toEqual(expect.any(Function));
    expect(nativeText.props.onResponderGrant).toEqual(expect.any(Function));
    expect(nativeText.props.onStartShouldSetResponder).toEqual(
      expect.any(Function)
    );
  });

  it('normalizes id, accessibility, invalid line counts, and text styles like RN Text', () => {
    const consoleError = jest
      .spyOn(console, 'error')
      .mockImplementation(() => undefined);
    const root = render(
      <Text
        accessibilityState={{ disabled: false }}
        aria-disabled
        aria-label="ARIA label"
        id="text-id"
        numberOfLines={-1}
        style={{ userSelect: 'none', verticalAlign: 'bottom' }}
      >
        Value
      </Text>
    );

    const nativeText = findNativeText(root);
    expect(nativeText.props.accessibilityLabel).toBe('ARIA label');
    expect(nativeText.props.accessibilityState.disabled).toBe(true);
    expect(nativeText.props.disabled).toBe(true);
    expect(nativeText.props.nativeID).toBe('text-id');
    expect(nativeText.props.numberOfLines).toBe(0);
    expect(nativeText.props.selectable).toBe(false);
    expect(consoleError).toHaveBeenCalledTimes(1);
    consoleError.mockRestore();
  });
});
