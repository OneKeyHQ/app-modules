/* eslint-disable @react-native/no-deep-imports */
import { createViewConfig } from 'react-native/Libraries/NativeComponent/ViewConfig';
import createReactNativeComponentClass from 'react-native/Libraries/Renderer/shims/createReactNativeComponentClass';
import type { HostComponent } from 'react-native';

import type { NativeProps } from './OneKeyTextNativeComponent';

export const ONEKEY_TEXT_VIEW_CONFIG = {
  validAttributes: {
    isHighlighted: true,
    isPressable: true,
    numberOfLines: true,
    ellipsizeMode: true,
    allowFontScaling: true,
    dynamicTypeRamp: true,
    maxFontSizeMultiplier: true,
    disabled: true,
    selectable: true,
    selectionColor: true,
    adjustsFontSizeToFit: true,
    minimumFontScale: true,
    textBreakStrategy: true,
    onTextLayout: true,
    dataDetectorType: true,
    android_hyphenationFrequency: true,
    lineBreakStrategyIOS: true,
  },
  directEventTypes: {
    topTextLayout: {
      registrationName: 'onTextLayout',
    },
  },
  uiViewClassName: 'OneKeyText',
} as const;

const OneKeyTextNativeHost = createReactNativeComponentClass('OneKeyText', () =>
  createViewConfig(ONEKEY_TEXT_VIEW_CONFIG)
) as unknown as HostComponent<NativeProps>;

export default OneKeyTextNativeHost;
