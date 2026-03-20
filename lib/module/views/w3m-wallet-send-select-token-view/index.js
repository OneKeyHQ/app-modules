import { useState } from 'react';
import { useSnapshot } from 'valtio';
import { ScrollView } from 'react-native';
import { FlexView, InputText, ListToken, Text } from '@reown/appkit-ui-react-native';
import { AccountController, AssetController, AssetUtil, NetworkController, RouterController, SendController } from '@reown/appkit-core-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import { Placeholder } from '../../partials/w3m-placeholder';
import styles from './styles';
export function WalletSendSelectTokenView() {
  const {
    padding
  } = useCustomDimensions();
  const {
    tokenBalance
  } = useSnapshot(AccountController.state);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    token
  } = useSnapshot(SendController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const [tokenSearch, setTokenSearch] = useState('');
  const [filteredTokens, setFilteredTokens] = useState(tokenBalance ?? []);
  const onSearchChange = value => {
    setTokenSearch(value);
    const filtered = AccountController.state.tokenBalance?.filter(_token => _token.name.toLowerCase().includes(value.toLowerCase()));
    setFilteredTokens(filtered ?? []);
  };
  const onTokenPress = _token => {
    SendController.setToken(_token);
    SendController.setTokenAmount(undefined);
    RouterController.goBack();
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    margin: ['l', '0', '2xl', '0'],
    style: [styles.container, {
      paddingHorizontal: padding
    }]
  }, /*#__PURE__*/React.createElement(FlexView, {
    margin: ['0', 'm', 'm', 'm']
  }, /*#__PURE__*/React.createElement(InputText, {
    value: tokenSearch,
    icon: "search",
    placeholder: "Search token",
    onChangeText: onSearchChange,
    clearButtonMode: "while-editing"
  })), /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    contentContainerStyle: styles.tokenList
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    color: "fg-200",
    style: styles.title
  }, "Your tokens"), filteredTokens.length ? filteredTokens.map((_token, index) => /*#__PURE__*/React.createElement(ListToken, {
    key: `${_token.name}${index}`,
    name: _token.name,
    imageSrc: _token.iconUrl,
    networkSrc: networkImage,
    value: _token.value,
    amount: _token.quantity.numeric,
    currency: _token.symbol,
    onPress: () => onTokenPress(_token),
    disabled: _token.address === token?.address
  })) : /*#__PURE__*/React.createElement(Placeholder, {
    icon: "coinPlaceholder",
    title: "No tokens found",
    description: "Your tokens will appear here"
  })));
}
//# sourceMappingURL=index.js.map