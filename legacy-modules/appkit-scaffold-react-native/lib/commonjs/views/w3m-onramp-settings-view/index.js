"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.OnRampSettingsView = OnRampSettingsView;
var _valtio = require("valtio");
var _react = require("react");
var _reactNativeSvg = require("react-native-svg");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _w3mSelectorModal = require("../../partials/w3m-selector-modal");
var _Country = require("./components/Country");
var _Currency = require("../w3m-onramp-view/components/Currency");
var _utils = require("./utils");
var _styles = require("./styles");
const MemoizedCountry = /*#__PURE__*/(0, _react.memo)(_Country.Country);
const MemoizedCurrency = /*#__PURE__*/(0, _react.memo)(_Currency.Currency);
function OnRampSettingsView() {
  const {
    paymentCurrency,
    selectedCountry
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OnRampController.state);
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const [modalType, setModalType] = (0, _react.useState)();
  const [searchValue, setSearchValue] = (0, _react.useState)('');
  const onCountryPress = () => {
    setModalType('country');
  };
  const onPaymentCurrencyPress = () => {
    setModalType('paymentCurrency');
  };
  const onPressModalItem = async item => {
    setModalType(undefined);
    setSearchValue('');
    if (modalType === 'country') {
      await _appkitCoreReactNative.OnRampController.setSelectedCountry(item);
    } else if (modalType === 'paymentCurrency') {
      _appkitCoreReactNative.OnRampController.setPaymentCurrency(item);
    }
  };
  const renderModalItem = ({
    item
  }) => {
    if (modalType === 'country') {
      const parsedItem = item;
      return /*#__PURE__*/React.createElement(MemoizedCountry, {
        item: parsedItem,
        onPress: onPressModalItem,
        selected: parsedItem.countryCode === selectedCountry?.countryCode
      });
    }
    const parsedItem = item;
    return /*#__PURE__*/React.createElement(MemoizedCurrency, {
      item: parsedItem,
      onPress: onPressModalItem,
      selected: parsedItem.currencyCode === paymentCurrency?.currencyCode,
      title: parsedItem.name,
      subtitle: parsedItem.currencyCode
    });
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: {
      backgroundColor: Theme['bg-100']
    },
    padding: ['s', 'm', '4xl', 'm']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    onPress: onCountryPress,
    chevron: true,
    style: _styles.styles.firstItem,
    contentStyle: _styles.styles.itemContent
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: [_styles.styles.imageContainer, {
      backgroundColor: Theme['gray-glass-005']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.styles.imageBorder
  }, selectedCountry?.flagImageUrl && _reactNativeSvg.SvgUri ? /*#__PURE__*/React.createElement(_reactNativeSvg.SvgUri, {
    uri: selectedCountry?.flagImageUrl,
    style: _styles.styles.image
  }) : undefined)), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100"
  }, "Select Country"), selectedCountry?.name && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, selectedCountry?.name))), /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    onPress: onPaymentCurrencyPress,
    chevron: true,
    contentStyle: _styles.styles.itemContent
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: [_styles.styles.imageContainer, {
      backgroundColor: Theme['gray-glass-005']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.styles.imageBorder
  }, paymentCurrency?.symbolImageUrl ? /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: paymentCurrency.symbolImageUrl,
    style: _styles.styles.image
  }) : /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "currencyDollar",
    size: "md",
    color: "fg-100"
  }))), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100"
  }, "Select Currency"), paymentCurrency?.name && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, paymentCurrency?.name)))), /*#__PURE__*/React.createElement(_w3mSelectorModal.SelectorModal, {
    visible: !!modalType,
    onClose: () => setModalType(undefined),
    items: (0, _utils.getModalItems)(modalType, searchValue, true),
    selectedItem: modalType === 'country' ? selectedCountry : paymentCurrency,
    onSearch: setSearchValue,
    renderItem: renderModalItem,
    keyExtractor: (item, index) => (0, _utils.getModalItemKey)(modalType, index, item),
    title: (0, _utils.getModalTitle)(modalType),
    itemHeight: (0, _utils.getItemHeight)(modalType),
    searchPlaceholder: (0, _utils.getModalSearchPlaceholder)(modalType)
  }));
}
//# sourceMappingURL=index.js.map