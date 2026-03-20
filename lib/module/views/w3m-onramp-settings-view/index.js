import { useSnapshot } from 'valtio';
import { memo, useState } from 'react';
import { SvgUri } from 'react-native-svg';
import { FlexView, ListItem, Text, useTheme, Icon, Image } from '@reown/appkit-ui-react-native';
import { OnRampController } from '@reown/appkit-core-react-native';
import { SelectorModal } from '../../partials/w3m-selector-modal';
import { Country } from './components/Country';
import { Currency } from '../w3m-onramp-view/components/Currency';
import { getModalTitle, getItemHeight, getModalItems, getModalItemKey, getModalSearchPlaceholder } from './utils';
import { styles } from './styles';
const MemoizedCountry = /*#__PURE__*/memo(Country);
const MemoizedCurrency = /*#__PURE__*/memo(Currency);
export function OnRampSettingsView() {
  const {
    paymentCurrency,
    selectedCountry
  } = useSnapshot(OnRampController.state);
  const Theme = useTheme();
  const [modalType, setModalType] = useState();
  const [searchValue, setSearchValue] = useState('');
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
      await OnRampController.setSelectedCountry(item);
    } else if (modalType === 'paymentCurrency') {
      OnRampController.setPaymentCurrency(item);
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
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(FlexView, {
    style: {
      backgroundColor: Theme['bg-100']
    },
    padding: ['s', 'm', '4xl', 'm']
  }, /*#__PURE__*/React.createElement(ListItem, {
    onPress: onCountryPress,
    chevron: true,
    style: styles.firstItem,
    contentStyle: styles.itemContent
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: [styles.imageContainer, {
      backgroundColor: Theme['gray-glass-005']
    }]
  }, /*#__PURE__*/React.createElement(FlexView, {
    style: styles.imageBorder
  }, selectedCountry?.flagImageUrl && SvgUri ? /*#__PURE__*/React.createElement(SvgUri, {
    uri: selectedCountry?.flagImageUrl,
    style: styles.image
  }) : undefined)), /*#__PURE__*/React.createElement(FlexView, null, /*#__PURE__*/React.createElement(Text, {
    color: "fg-100"
  }, "Select Country"), selectedCountry?.name && /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200"
  }, selectedCountry?.name))), /*#__PURE__*/React.createElement(ListItem, {
    onPress: onPaymentCurrencyPress,
    chevron: true,
    contentStyle: styles.itemContent
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: [styles.imageContainer, {
      backgroundColor: Theme['gray-glass-005']
    }]
  }, /*#__PURE__*/React.createElement(FlexView, {
    style: styles.imageBorder
  }, paymentCurrency?.symbolImageUrl ? /*#__PURE__*/React.createElement(Image, {
    source: paymentCurrency.symbolImageUrl,
    style: styles.image
  }) : /*#__PURE__*/React.createElement(Icon, {
    name: "currencyDollar",
    size: "md",
    color: "fg-100"
  }))), /*#__PURE__*/React.createElement(FlexView, null, /*#__PURE__*/React.createElement(Text, {
    color: "fg-100"
  }, "Select Currency"), paymentCurrency?.name && /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200"
  }, paymentCurrency?.name)))), /*#__PURE__*/React.createElement(SelectorModal, {
    visible: !!modalType,
    onClose: () => setModalType(undefined),
    items: getModalItems(modalType, searchValue, true),
    selectedItem: modalType === 'country' ? selectedCountry : paymentCurrency,
    onSearch: setSearchValue,
    renderItem: renderModalItem,
    keyExtractor: (item, index) => getModalItemKey(modalType, index, item),
    title: getModalTitle(modalType),
    itemHeight: getItemHeight(modalType),
    searchPlaceholder: getModalSearchPlaceholder(modalType)
  }));
}
//# sourceMappingURL=index.js.map