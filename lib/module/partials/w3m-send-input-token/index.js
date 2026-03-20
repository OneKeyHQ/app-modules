import { useRef, useState } from 'react';
import { TextInput } from 'react-native';
import { FlexView, Link, Text, useTheme, TokenButton } from '@reown/appkit-ui-react-native';
import { NumberUtil } from '@reown/appkit-common-react-native';
import { ConstantsUtil, SendController } from '@reown/appkit-core-react-native';
import { getMaxAmount, getSendValue } from './utils';
import styles from './styles';
export function SendInputToken({
  token,
  sendTokenAmount,
  gasPrice,
  style,
  onTokenPress
}) {
  const Theme = useTheme();
  const valueInputRef = useRef(null);
  const [inputValue, setInputValue] = useState(sendTokenAmount?.toString());
  const sendValue = getSendValue(token, sendTokenAmount);
  const maxAmount = getMaxAmount(token);
  const maxError = token && sendTokenAmount && sendTokenAmount > Number(token.quantity.numeric);
  const onInputChange = value => {
    const formattedValue = value.replace(/,/g, '.');
    if (Number(formattedValue) >= 0 || formattedValue === '') {
      setInputValue(formattedValue);
      SendController.setTokenAmount(Number(formattedValue));
    }
  };
  const onMaxPress = () => {
    if (token && gasPrice) {
      const isNetworkToken = token.address === undefined || Object.values(ConstantsUtil.NATIVE_TOKEN_ADDRESS).some(nativeAddress => token?.address === nativeAddress);
      const numericGas = NumberUtil.bigNumber(gasPrice).shiftedBy(-token.quantity.decimals);
      const maxValue = isNetworkToken ? NumberUtil.bigNumber(token.quantity.numeric).minus(numericGas) : NumberUtil.bigNumber(token.quantity.numeric);
      SendController.setTokenAmount(Number(maxValue.toFixed(20)));
      setInputValue(maxValue.toFixed(20));
      valueInputRef.current?.blur();
    }
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.container, {
      backgroundColor: Theme['gray-glass-005'],
      borderColor: Theme['gray-glass-005']
    }, style],
    justifyContent: "center",
    padding: ['xl', 'l', 'l', 'l']
  }, /*#__PURE__*/React.createElement(FlexView, {
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
    value: inputValue,
    onChangeText: onInputChange,
    keyboardType: "decimal-pad",
    inputMode: "decimal",
    autoComplete: "off",
    spellCheck: false,
    selectionColor: Theme['accent-100'],
    underlineColorAndroid: "transparent",
    selectTextOnFocus: false,
    numberOfLines: 1,
    autoFocus: !!token
  }), /*#__PURE__*/React.createElement(TokenButton, {
    imageUrl: token?.iconUrl,
    text: token?.symbol,
    onPress: onTokenPress,
    chevron: true
  })), token && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['3xs', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200",
    style: styles.sendValue,
    numberOfLines: 1
  }, sendValue ?? ''), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: maxError ? 'error-100' : 'fg-200',
    numberOfLines: 1
  }, maxAmount ?? ''), /*#__PURE__*/React.createElement(Link, {
    onPress: onMaxPress
  }, "Max"))));
}
//# sourceMappingURL=index.js.map