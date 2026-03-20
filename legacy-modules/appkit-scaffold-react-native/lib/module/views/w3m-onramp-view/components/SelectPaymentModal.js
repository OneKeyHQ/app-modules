/* eslint-disable valtio/state-snapshot-rule */
import { useSnapshot } from 'valtio';
import { useRef, useState, useMemo, useEffect } from 'react';
import Modal from 'react-native-modal';
import { FlatList, StyleSheet, View } from 'react-native';
import { FlexView, IconLink, Spacing, Text, useTheme, Separator, BorderRadius } from '@reown/appkit-ui-react-native';
import { OnRampController } from '@reown/appkit-core-react-native';
import { Quote, ITEM_HEIGHT as QUOTE_ITEM_HEIGHT } from './Quote';
import { PaymentMethod } from './PaymentMethod';
const SEPARATOR_HEIGHT = Spacing.s;
export function SelectPaymentModal({
  title,
  visible,
  onClose
}) {
  const Theme = useTheme();
  const {
    selectedQuote,
    quotes,
    selectedPaymentMethod
  } = useSnapshot(OnRampController.state);
  const paymentMethodsRef = useRef(null);
  const [paymentMethods, setPaymentMethods] = useState(OnRampController.state.paymentMethods);
  const [activePaymentMethod, setActivePaymentMethod] = useState(OnRampController.state.selectedPaymentMethod);
  const availablePaymentMethods = useMemo(() => {
    return paymentMethods.filter(paymentMethod => quotes?.some(quote => quote.paymentMethodType === paymentMethod.paymentMethod));
  }, [paymentMethods, quotes]);
  const availableQuotes = useMemo(() => {
    return quotes?.filter(quote => activePaymentMethod?.paymentMethod === quote.paymentMethodType);
  }, [quotes, activePaymentMethod]);
  const sortedQuotes = useMemo(() => {
    if (!selectedQuote || selectedQuote.paymentMethodType !== activePaymentMethod?.paymentMethod) {
      return availableQuotes;
    }
    return [selectedQuote, ...(availableQuotes?.filter(quote => quote.serviceProvider !== selectedQuote.serviceProvider) ?? [])];
  }, [availableQuotes, selectedQuote, activePaymentMethod]);
  const renderSeparator = () => {
    return /*#__PURE__*/React.createElement(View, {
      style: {
        height: SEPARATOR_HEIGHT
      }
    });
  };
  const handleQuotePress = quote => {
    if (activePaymentMethod) {
      OnRampController.clearError();
      OnRampController.setSelectedQuote(quote);
      OnRampController.setSelectedPaymentMethod(activePaymentMethod);
    }
    onClose();
  };
  const handlePaymentMethodPress = paymentMethod => {
    setActivePaymentMethod(paymentMethod);
  };
  const renderQuote = ({
    item,
    index
  }) => {
    const logoURL = OnRampController.getServiceProviderImage(item.serviceProvider);
    const isSelected = item.serviceProvider === OnRampController.state.selectedQuote?.serviceProvider && item.paymentMethodType === OnRampController.state.selectedQuote?.paymentMethodType;
    const isRecommended = availableQuotes?.findIndex(quote => quote.serviceProvider === item.serviceProvider) === 0 && availableQuotes?.length > 1;
    const tagText = isRecommended ? 'Recommended' : item.lowKyc ? 'Low KYC' : undefined;
    return /*#__PURE__*/React.createElement(Quote, {
      item: item,
      selected: isSelected,
      logoURL: logoURL,
      onQuotePress: () => handleQuotePress(item),
      tagText: tagText,
      testID: `quote-item-${index}`
    });
  };
  const renderPaymentMethod = ({
    item
  }) => {
    const parsedItem = item;
    const isSelected = parsedItem.paymentMethod === activePaymentMethod?.paymentMethod;
    return /*#__PURE__*/React.createElement(PaymentMethod, {
      item: parsedItem,
      onPress: () => handlePaymentMethodPress(parsedItem),
      selected: isSelected,
      testID: `payment-method-item-${parsedItem.paymentMethod}`
    });
  };
  useEffect(() => {
    if (visible && OnRampController.state.selectedPaymentMethod) {
      const methods = [OnRampController.state.selectedPaymentMethod, ...OnRampController.state.paymentMethods.filter(m => m.paymentMethod !== OnRampController.state.selectedPaymentMethod?.paymentMethod)];
      //Update payment methods order
      setPaymentMethods(methods);
      setActivePaymentMethod(OnRampController.state.selectedPaymentMethod);
    }
  }, [visible]);
  return /*#__PURE__*/React.createElement(Modal, {
    isVisible: visible,
    useNativeDriver: true,
    useNativeDriverForBackdrop: true,
    statusBarTranslucent: true,
    hideModalContentWhileAnimating: true,
    onBackdropPress: onClose,
    onDismiss: onClose,
    style: styles.modal
  }, /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.container, {
      backgroundColor: Theme['bg-100']
    }]
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "space-between",
    flexDirection: "row",
    style: styles.header
  }, /*#__PURE__*/React.createElement(IconLink, {
    icon: "arrowLeft",
    onPress: onClose,
    testID: "payment-modal-button-back"
  }), !!title && /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-600"
  }, title), /*#__PURE__*/React.createElement(View, {
    style: styles.iconPlaceholder
  })), /*#__PURE__*/React.createElement(Text, {
    variant: "small-500",
    color: "fg-150",
    style: styles.subtitle
  }, "Pay with"), /*#__PURE__*/React.createElement(FlexView, null, /*#__PURE__*/React.createElement(FlatList, {
    data: availablePaymentMethods,
    renderItem: renderPaymentMethod,
    ref: paymentMethodsRef,
    style: styles.paymentMethodsContainer,
    contentContainerStyle: styles.paymentMethodsContent,
    fadingEdgeLength: 20,
    keyExtractor: item => item.paymentMethod,
    horizontal: true,
    showsHorizontalScrollIndicator: false
  })), /*#__PURE__*/React.createElement(Separator, {
    style: styles.separator,
    color: "gray-glass-010"
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "small-500",
    color: "fg-150",
    style: styles.subtitle
  }, "Providers"), /*#__PURE__*/React.createElement(FlatList, {
    data: sortedQuotes,
    bounces: false,
    renderItem: renderQuote,
    extraData: selectedPaymentMethod,
    contentContainerStyle: styles.listContent,
    ItemSeparatorComponent: renderSeparator,
    fadingEdgeLength: 20,
    keyExtractor: item => `${item.serviceProvider}-${item.paymentMethodType}`,
    getItemLayout: (_, index) => ({
      length: QUOTE_ITEM_HEIGHT + SEPARATOR_HEIGHT,
      offset: (QUOTE_ITEM_HEIGHT + SEPARATOR_HEIGHT) * index,
      index
    })
  })));
}
const styles = StyleSheet.create({
  modal: {
    margin: 0,
    justifyContent: 'flex-end'
  },
  header: {
    marginBottom: Spacing.l,
    paddingHorizontal: Spacing.m,
    paddingTop: Spacing.m
  },
  container: {
    height: '80%',
    borderTopLeftRadius: BorderRadius.l,
    borderTopRightRadius: BorderRadius.l
  },
  separator: {
    width: undefined,
    marginVertical: Spacing.m,
    marginHorizontal: Spacing.m
  },
  listContent: {
    paddingTop: Spacing['3xs'],
    paddingBottom: Spacing['4xl'],
    paddingHorizontal: Spacing.m
  },
  iconPlaceholder: {
    height: 32,
    width: 32
  },
  subtitle: {
    marginBottom: Spacing.xs,
    marginHorizontal: Spacing.m
  },
  emptyContainer: {
    height: 150
  },
  paymentMethodsContainer: {
    paddingHorizontal: Spacing['3xs']
  },
  paymentMethodsContent: {
    paddingLeft: Spacing.xs
  }
});
//# sourceMappingURL=SelectPaymentModal.js.map