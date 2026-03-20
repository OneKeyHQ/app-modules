import { StyleSheet } from 'react-native';
import { Button, FlexView, useTheme, Text, LoadingSpinner, NumericKeyboard, Separator, Spacing, BorderRadius } from '@reown/appkit-ui-react-native';
import { useEffect, useState, useRef } from 'react';
export function CurrencyInput({
  value,
  loading,
  error,
  isAmountError,
  purchaseValue,
  onValueChange,
  onSuggestedValuePress,
  symbol,
  style,
  suggestedValues
}) {
  const Theme = useTheme();
  const [displayValue, setDisplayValue] = useState(value?.toString() || '0');
  const isInternalChange = useRef(false);
  const amountColor = isAmountError ? 'error-100' : value ? 'fg-100' : 'fg-200';
  const handleKeyPress = key => {
    isInternalChange.current = true;
    if (key === 'erase') {
      setDisplayValue(prev => {
        const newDisplay = prev.slice(0, -1) || '0';

        // If the previous value does not end with a comma, convert to numeric value
        if (!prev?.endsWith(',')) {
          const numericValue = Number(newDisplay.replace(',', '.'));
          onValueChange?.(numericValue);
        }
        return newDisplay;
      });
    } else if (key === ',') {
      setDisplayValue(prev => {
        if (prev.includes(',')) return prev; // Don't add multiple commas
        const newDisplay = prev + ',';
        return newDisplay;
      });
    } else {
      setDisplayValue(prev => {
        const newDisplay = prev === '0' ? key : prev + key;

        // Convert to numeric value
        const numericValue = Number(newDisplay.replace(',', '.'));
        onValueChange?.(numericValue);
        return newDisplay;
      });
    }
  };
  useEffect(() => {
    // Handle external value changes
    if (!isInternalChange.current && value !== undefined) {
      setDisplayValue(value.toString());
    }
    isInternalChange.current = false;
  }, [value]);
  return /*#__PURE__*/React.createElement(FlexView, {
    style: style,
    testID: "currency-input"
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    margin: ['m', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    style: [styles.input, {
      color: Theme[amountColor]
    }]
  }, displayValue), /*#__PURE__*/React.createElement(Text, {
    variant: "large-400",
    color: isAmountError ? 'error-100' : 'fg-200',
    testID: "currency-input-symbol"
  }, symbol || '')), /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: styles.bottomContainer
  }, loading ? /*#__PURE__*/React.createElement(LoadingSpinner, {
    size: "sm",
    color: "fg-150"
  }) : error ? /*#__PURE__*/React.createElement(Text, {
    variant: "small-500",
    color: "error-100",
    numberOfLines: 1,
    testID: "currency-input-error"
  }, error) : /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    color: "fg-150",
    numberOfLines: 1
  }, purchaseValue))), suggestedValues && suggestedValues.length > 0 && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    margin: ['s', '0', '0', '0']
  }, suggestedValues?.map(suggestion => {
    const isSelected = suggestion.toString() === value;
    return /*#__PURE__*/React.createElement(Button, {
      key: suggestion,
      testID: `suggested-value-${suggestion}`,
      style: [styles.suggestedValue, isSelected && {
        ...styles.selectedValue,
        backgroundColor: Theme['accent-glass-020'],
        borderColor: Theme['accent-100']
      }],
      variant: "shade",
      onPress: () => onSuggestedValuePress?.(suggestion)
    }, /*#__PURE__*/React.createElement(Text, {
      variant: "small-400",
      color: "fg-100"
    }, `${suggestion} ${symbol ?? ''}`));
  })), /*#__PURE__*/React.createElement(Separator, {
    color: "gray-glass-020",
    style: styles.separator
  }), /*#__PURE__*/React.createElement(NumericKeyboard, {
    onKeyPress: handleKeyPress
  }));
}
const styles = StyleSheet.create({
  input: {
    fontSize: 38,
    marginRight: Spacing['3xs']
  },
  bottomContainer: {
    height: 20
  },
  separator: {
    marginTop: 16
  },
  suggestedValue: {
    flex: 1,
    borderRadius: BorderRadius.xxs,
    marginRight: Spacing.xs,
    height: 40
  },
  selectedValue: {
    borderWidth: StyleSheet.hairlineWidth
  }
});
//# sourceMappingURL=CurrencyInput.js.map