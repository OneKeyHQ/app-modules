import { createRef, type ElementRef } from 'react';
import {
  Text as ReactNativeText,
  type TextProps as ReactNativeTextProps,
} from 'react-native';

import { Text as AndroidText } from '../Text.android';
import { Text, type TextProps } from '..';

type Assert<T extends true> = T;
type OneKeyPropsCoverReactNative = Assert<
  TextProps extends ReactNativeTextProps ? true : false
>;
type ReactNativePropsCoverOneKey = Assert<
  ReactNativeTextProps extends TextProps ? true : false
>;
type OneKeyTextRef = ElementRef<typeof Text>;
type AndroidTextRef = ElementRef<typeof AndroidText>;
type ReactNativeTextRef = ElementRef<typeof ReactNativeText>;
type OneKeyRefCoversReactNative = Assert<
  OneKeyTextRef extends ReactNativeTextRef ? true : false
>;
type ReactNativeRefCoversOneKey = Assert<
  ReactNativeTextRef extends OneKeyTextRef ? true : false
>;
type AndroidRefCoversReactNative = Assert<
  AndroidTextRef extends ReactNativeTextRef ? true : false
>;
type ReactNativeRefCoversAndroid = Assert<
  ReactNativeTextRef extends AndroidTextRef ? true : false
>;

const compileTimeAssertions: Readonly<
  [
    OneKeyPropsCoverReactNative,
    ReactNativePropsCoverOneKey,
    OneKeyRefCoversReactNative,
    ReactNativeRefCoversOneKey,
    AndroidRefCoversReactNative,
    ReactNativeRefCoversAndroid
  ]
> = [true, true, true, true, true, true];

describe('Text type parity', () => {
  it('accepts the React Native Text ref target', () => {
    const ref = createRef<ReactNativeTextRef>();
    const element = <Text ref={ref}>Parity</Text>;

    expect(compileTimeAssertions).toEqual([true, true, true, true, true, true]);
    expect(element.props.children).toBe('Parity');
  });
});
