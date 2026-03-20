import { useSnapshot } from 'valtio';
import { memo, useCallback, useEffect, useState } from 'react';
import { ScrollView } from 'react-native';
import { OnRampController, ThemeController, RouterController, NetworkController, AssetUtil, SnackController, ConstantsUtil, AssetController } from '@reown/appkit-core-react-native';
import { Button, FlexView, Image, Text, TokenButton, useTheme } from '@reown/appkit-ui-react-native';
import { NumberUtil, StringUtil } from '@reown/appkit-common-react-native';
import { SelectorModal } from '../../partials/w3m-selector-modal';
import { Currency, ITEM_HEIGHT as CURRENCY_ITEM_HEIGHT } from './components/Currency';
import { getPurchaseCurrencies, getQuotesDebounced } from './utils';
import { CurrencyInput } from './components/CurrencyInput';
import { SelectPaymentModal } from './components/SelectPaymentModal';
import { Header } from './components/Header';
import { LoadingView } from './components/LoadingView';
import PaymentButton from './components/PaymentButton';
import styles from './styles';
const MemoizedCurrency = /*#__PURE__*/memo(Currency);
export function OnRampView() {
  const {
    themeMode
  } = useSnapshot(ThemeController.state);
  const Theme = useTheme();
  const {
    purchaseCurrency,
    paymentCurrency,
    selectedPaymentMethod,
    paymentAmount,
    quotesLoading,
    quotes,
    selectedQuote,
    error,
    loading,
    initialLoading
  } = useSnapshot(OnRampController.state);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const [searchValue, setSearchValue] = useState('');
  const [isCurrencyModalVisible, setIsCurrencyModalVisible] = useState(false);
  const [isPaymentMethodModalVisible, setIsPaymentMethodModalVisible] = useState(false);
  const purchaseCurrencyCode = purchaseCurrency?.currencyCode?.split('_')[0] ?? purchaseCurrency?.currencyCode;
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const getQuotes = useCallback(() => {
    if (OnRampController.canGenerateQuote()) {
      OnRampController.getQuotes();
    }
  }, []);
  const getPaymentButtonTitle = () => {
    if (selectedPaymentMethod) {
      return selectedPaymentMethod.name;
    }
    if (quotesLoading) {
      return 'Loading quotes';
    }
    if (!paymentAmount || quotes?.length === 0) {
      return 'Enter a valid amount';
    }
    return '';
  };
  const getPaymentButtonSubtitle = () => {
    if (selectedQuote) {
      return StringUtil.capitalize(selectedQuote?.serviceProvider);
    }
    if (selectedPaymentMethod) {
      if (quotesLoading) {
        return 'Loading quotes';
      }
      if (!paymentAmount || quotes?.length === 0) {
        return 'Enter a valid amount';
      }
    }
    return undefined;
  };
  const onValueChange = value => {
    if (!value) {
      OnRampController.abortGetQuotes();
      OnRampController.setPaymentAmount(0);
      OnRampController.setSelectedQuote(undefined);
      OnRampController.clearError();
      return;
    }
    OnRampController.setPaymentAmount(value);
    getQuotesDebounced();
  };
  const handleSearch = value => {
    setSearchValue(value);
  };
  const handleContinue = async () => {
    if (OnRampController.state.selectedQuote) {
      RouterController.push('OnRampCheckout');
    }
  };
  const renderCurrencyItem = ({
    item
  }) => {
    return /*#__PURE__*/React.createElement(MemoizedCurrency, {
      item: item,
      onPress: onPressPurchaseCurrency,
      selected: item.currencyCode === purchaseCurrency?.currencyCode,
      title: item.name,
      subtitle: item.currencyCode.split('_')[0] ?? item.currencyCode,
      testID: `currency-item-${item.currencyCode}`
    });
  };
  const onPressPurchaseCurrency = item => {
    setIsCurrencyModalVisible(false);
    setIsPaymentMethodModalVisible(false);
    setSearchValue('');
    OnRampController.setPurchaseCurrency(item);
    getQuotes();
  };
  const onModalClose = () => {
    setSearchValue('');
    setIsCurrencyModalVisible(false);
    setIsPaymentMethodModalVisible(false);
  };
  useEffect(() => {
    if (error?.type === ConstantsUtil.ONRAMP_ERROR_TYPES.FAILED_TO_LOAD) {
      SnackController.showInternalError({
        shortMessage: 'Failed to load data. Please try again later.',
        longMessage: error?.message
      });
      RouterController.goBack();
    }
  }, [error]);
  useEffect(() => {
    if (OnRampController.state.countries.length === 0) {
      OnRampController.loadOnRampData();
    }
  }, []);
  if (initialLoading || OnRampController.state.countries.length === 0) {
    return /*#__PURE__*/React.createElement(LoadingView, null);
  }
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Header, {
    onSettingsPress: () => RouterController.push('OnRampSettings')
  }), /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false
  }, /*#__PURE__*/React.createElement(FlexView, {
    padding: ['s', 'l', '4xl', 'l']
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, "You Buy"), /*#__PURE__*/React.createElement(TokenButton, {
    placeholder: 'Select currency',
    imageUrl: purchaseCurrency?.symbolImageUrl,
    text: purchaseCurrencyCode,
    onPress: () => setIsCurrencyModalVisible(true),
    testID: "currency-selector",
    chevron: true,
    renderClip: networkImage ? /*#__PURE__*/React.createElement(Image, {
      source: networkImage,
      style: [styles.networkImage, {
        borderColor: Theme['bg-300']
      }]
    }) : null
  })), /*#__PURE__*/React.createElement(CurrencyInput, {
    value: paymentAmount?.toString(),
    symbol: paymentCurrency?.currencyCode,
    error: error?.message,
    isAmountError: error?.type === ConstantsUtil.ONRAMP_ERROR_TYPES.AMOUNT_TOO_LOW || error?.type === ConstantsUtil.ONRAMP_ERROR_TYPES.AMOUNT_TOO_HIGH || error?.type === ConstantsUtil.ONRAMP_ERROR_TYPES.INVALID_AMOUNT,
    loading: loading || quotesLoading,
    purchaseValue: `${selectedQuote?.destinationAmount ? NumberUtil.roundNumber(selectedQuote.destinationAmount, 6, 5)?.toString() : '0.00'} ${purchaseCurrencyCode ?? ''}`,
    onValueChange: onValueChange,
    style: styles.currencyInput
  }), /*#__PURE__*/React.createElement(PaymentButton, {
    title: getPaymentButtonTitle(),
    subtitle: getPaymentButtonSubtitle(),
    paymentLogo: selectedPaymentMethod?.logos[themeMode ?? 'light'],
    providerLogo: OnRampController.getServiceProviderImage(selectedQuote?.serviceProvider),
    disabled: !paymentAmount || quotes?.length === 0,
    loading: quotesLoading || loading,
    onPress: () => setIsPaymentMethodModalVisible(true),
    testID: "payment-method-button"
  }), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    margin: ['m', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Button, {
    variant: "shade",
    style: styles.cancelButton,
    onPress: RouterController.goBack
  }, "Cancel"), /*#__PURE__*/React.createElement(Button, {
    style: styles.continueButton,
    onPress: handleContinue,
    disabled: quotesLoading || loading || !selectedQuote,
    testID: "button-continue"
  }, "Continue")), /*#__PURE__*/React.createElement(SelectPaymentModal, {
    visible: isPaymentMethodModalVisible,
    onClose: onModalClose,
    title: "Payment"
  }), /*#__PURE__*/React.createElement(SelectorModal, {
    selectedItem: purchaseCurrency,
    visible: isCurrencyModalVisible,
    onClose: onModalClose,
    items: getPurchaseCurrencies(searchValue, true),
    onSearch: handleSearch,
    renderItem: renderCurrencyItem,
    keyExtractor: item => item.currencyCode,
    title: "Select token",
    itemHeight: CURRENCY_ITEM_HEIGHT,
    showNetwork: true
  }))));
}
//# sourceMappingURL=index.js.map