import { useRef } from 'react';
import { TextInput } from 'react-native';
import { FlexView, useTheme, TokenButton, Shimmer, Text, UiUtil, Link } from '@reown/appkit-ui-react-native';
import styles from './styles';
import { NumberUtil } from '@reown/appkit-common-react-native';
const MINIMUM_USD_VALUE_TO_CONVERT = 0.00005;
export function SwapInput({
  token,
  value,
  style,
  loading,
  onTokenPress,
  onMaxPress,
  onChange,
  marketValue,
  editable,
  autoFocus
}) {
  const Theme = useTheme();
  const valueInputRef = useRef(null);
  const isMarketValueGreaterThanZero = !!marketValue && NumberUtil.bigNumber(marketValue).isGreaterThan('0');
  const maxAmount = UiUtil.formatNumberToLocalString(token?.quantity.numeric, 3);
  const maxError = Number(value) > Number(token?.quantity.numeric);
  const showMax = onMaxPress && !!token?.quantity.numeric && NumberUtil.multiply(token?.quantity.numeric, token?.price).isGreaterThan(MINIMUM_USD_VALUE_TO_CONVERT);
  const handleInputChange = _value => {
    const formattedValue = _value.replace(/,/g, '.');
    if (Number(formattedValue) >= 0 || formattedValue === '') {
      onChange?.(formattedValue);
    }
  };
  const handleMaxPress = () => {
    if (valueInputRef.current) {
      valueInputRef.current.blur();
    }
    onMaxPress?.();
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.container, {
      backgroundColor: Theme['gray-glass-005'],
      borderColor: Theme['gray-glass-005']
    }, style],
    justifyContent: "center",
    padding: ['xl', 'l', 'l', 'l']
  }, loading ? /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(Shimmer, {
    height: 36,
    width: 80,
    borderRadius: 12
  }), /*#__PURE__*/React.createElement(Shimmer, {
    height: 36,
    width: 80,
    borderRadius: 18
  })) : /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(TextInput, {
    ref: valueInputRef,
    placeholder: "0",
    placeholderTextColor: Theme['fg-275'],
    returnKeyType: "done",
    style: [styles.input, {
      color: Theme['fg-100']
    }],
    autoCapitalize: "none",
    autoCorrect: false,
    value: value,
    onChangeText: handleInputChange,
    keyboardType: "decimal-pad",
    inputMode: "decimal",
    autoComplete: "off",
    spellCheck: false,
    selectionColor: Theme['accent-100'],
    underlineColorAndroid: "transparent",
    selectTextOnFocus: false,
    numberOfLines: 1,
    editable: editable,
    autoFocus: autoFocus
  }), /*#__PURE__*/React.createElement(TokenButton, {
    text: token?.symbol,
    imageUrl: token?.logoUri,
    onPress: onTokenPress,
    chevron: true
  })), (showMax || isMarketValueGreaterThanZero) && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['3xs', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200",
    numberOfLines: 1
  }, isMarketValueGreaterThanZero ? `~$${UiUtil.formatNumberToLocalString(marketValue, 2)}` : ''), showMax && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: maxError ? 'error-100' : 'fg-200',
    numberOfLines: 1
  }, showMax ? maxAmount : ''), /*#__PURE__*/React.createElement(Link, {
    onPress: handleMaxPress
  }, "Max")))));
}
//# sourceMappingURL=index.js.map