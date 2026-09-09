import {
  codegenNativeComponent,
  type ColorValue,
  type HostComponent,
  type ViewProps,
} from 'react-native';
import type {
  DirectEventHandler,
  Float,
  Int32,
  WithDefault,
} from 'react-native/Libraries/Types/CodegenTypes';

export interface NativeProps extends ViewProps {
  adjustsFontSizeToFit?: WithDefault<boolean, false>;
  allowFontScaling?: WithDefault<boolean, true>;
  android_hyphenationFrequency?: WithDefault<
    'full' | 'none' | 'normal',
    'none'
  >;
  color?: ColorValue;
  dataDetectorType?: WithDefault<
    'all' | 'email' | 'link' | 'none' | 'phoneNumber',
    'none'
  >;
  disabled?: WithDefault<boolean, false>;
  dynamicTypeRamp?: WithDefault<
    | 'body'
    | 'callout'
    | 'caption1'
    | 'caption2'
    | 'footnote'
    | 'headline'
    | 'largeTitle'
    | 'subheadline'
    | 'title1'
    | 'title2'
    | 'title3',
    'body'
  >;
  ellipsizeMode?: WithDefault<'clip' | 'head' | 'middle' | 'tail', 'tail'>;
  fontFamily?: string;
  fontSize?: Float;
  fontStyle?: WithDefault<'italic' | 'normal', 'normal'>;
  fontVariant?: ReadonlyArray<string>;
  fontWeight?: string;
  includeFontPadding?: WithDefault<boolean, true>;
  isHighlighted?: WithDefault<boolean, false>;
  isPressable?: WithDefault<boolean, false>;
  letterSpacing?: Float;
  lineBreakModeIOS?: WithDefault<
    'char' | 'clip' | 'head' | 'middle' | 'tail' | 'word',
    'tail'
  >;
  lineBreakStrategyIOS?: WithDefault<
    'hangul-word' | 'none' | 'push-out' | 'standard',
    'none'
  >;
  lineHeight?: Float;
  maxFontSizeMultiplier?: Float;
  minimumFontScale?: Float;
  numberOfLines?: WithDefault<Int32, 0>;
  // The native descriptor uses ParagraphEventEmitter to supply RN's line payload.
  onTextLayout?: DirectEventHandler<Readonly<{}>>;
  selectable?: WithDefault<boolean, false>;
  selectionColor?: ColorValue;
  textAlign?: WithDefault<
    'auto' | 'center' | 'justify' | 'left' | 'right',
    'auto'
  >;
  textAlignVertical?: WithDefault<'auto' | 'bottom' | 'center' | 'top', 'auto'>;
  textBreakStrategy?: WithDefault<
    'balanced' | 'highQuality' | 'simple',
    'highQuality'
  >;
  textDecorationColor?: ColorValue;
  textDecorationLine?: WithDefault<
    'line-through' | 'none' | 'underline' | 'underline line-through',
    'none'
  >;
  textDecorationStyle?: WithDefault<
    'dashed' | 'dotted' | 'double' | 'solid',
    'solid'
  >;
  textShadowColor?: ColorValue;
  textShadowOffset?: Readonly<{
    height: Float;
    width: Float;
  }>;
  textShadowRadius?: Float;
  textTransform?: WithDefault<
    'capitalize' | 'lowercase' | 'none' | 'uppercase',
    'none'
  >;
  writingDirection?: WithDefault<'auto' | 'ltr' | 'rtl', 'auto'>;
}

export default codegenNativeComponent<NativeProps>(
  'OneKeyText'
) as HostComponent<NativeProps>;
