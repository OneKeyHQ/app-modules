import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

export interface AutoSizeInputProps extends HybridViewProps {
  // Text content
  text?: string;
  prefix?: string;
  suffix?: string;
  placeholder?: string;

  // Font size control
  fontSize?: number;
  minFontSize?: number;

  // Multiline support
  multiline?: boolean;
  maxNumberOfLines?: number;

  // Styles
  textColor?: string;
  prefixColor?: string;
  suffixColor?: string;
  placeholderColor?: string;
  textAlign?: string;
  fontFamily?: string;
  fontWeight?: string;

  // Editable
  editable?: boolean;

  // Keyboard & input behavior
  keyboardType?: string;
  returnKeyType?: string;
  autoCorrect?: boolean;
  autoCapitalize?: string;
  selectionColor?: string;

  // Spacing
  prefixMarginRight?: number;
  suffixMarginLeft?: number;

  // Input box appearance
  showBorder?: boolean;
  inputBackgroundColor?: string;

  // Let input width grow with content and push suffix to the right
  contentAutoWidth?: boolean;

  // Event callbacks
  onChangeText?: (text: string) => void;
  onFocus?: () => void;
  onBlur?: () => void;
}

export interface AutoSizeInputMethods extends HybridViewMethods {
  focus(): void;
  blur(): void;
}

export type AutoSizeInput = HybridView<AutoSizeInputProps, AutoSizeInputMethods>;
