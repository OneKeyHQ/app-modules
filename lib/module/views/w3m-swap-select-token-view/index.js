import { useState } from 'react';
import { useSnapshot } from 'valtio';
import { ScrollView, SectionList } from 'react-native';
import { FlexView, InputText, ListToken, ListTokenTotalHeight, Separator, Text, TokenButton, useTheme } from '@reown/appkit-ui-react-native';
import { AssetController, AssetUtil, NetworkController, RouterController, SwapController } from '@reown/appkit-core-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import { Placeholder } from '../../partials/w3m-placeholder';
import styles from './styles';
import { createSections } from './utils';
export function SwapSelectTokenView() {
  const {
    padding
  } = useCustomDimensions();
  const Theme = useTheme();
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    sourceToken,
    suggestedTokens
  } = useSnapshot(SwapController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const [tokenSearch, setTokenSearch] = useState('');
  const isSourceToken = RouterController.state.data?.swapTarget === 'sourceToken';
  const [filteredTokens, setFilteredTokens] = useState(createSections(isSourceToken, tokenSearch));
  const suggestedList = suggestedTokens?.filter(token => token.address !== SwapController.state.sourceToken?.address).slice(0, 8);
  const onSearchChange = value => {
    setTokenSearch(value);
    setFilteredTokens(createSections(isSourceToken, value));
  };
  const onTokenPress = token => {
    if (isSourceToken) {
      SwapController.setSourceToken(token);
    } else {
      SwapController.setToToken(token);
      if (SwapController.state.sourceToken && SwapController.state.sourceTokenAmount) {
        SwapController.swapTokens();
      }
    }
    RouterController.goBack();
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    margin: ['l', '0', '2xl', '0'],
    style: [styles.container, {
      paddingHorizontal: padding
    }]
  }, /*#__PURE__*/React.createElement(FlexView, null, /*#__PURE__*/React.createElement(InputText, {
    value: tokenSearch,
    icon: "search",
    placeholder: "Search token",
    onChangeText: onSearchChange,
    clearButtonMode: "while-editing",
    style: styles.input
  }), !isSourceToken && /*#__PURE__*/React.createElement(ScrollView, {
    horizontal: true,
    showsHorizontalScrollIndicator: false,
    bounces: false,
    fadingEdgeLength: 20,
    style: styles.suggestedList,
    contentContainerStyle: styles.suggestedListContent
  }, suggestedList?.map((token, index) => /*#__PURE__*/React.createElement(TokenButton, {
    key: token.name,
    text: token.symbol,
    imageUrl: token.logoUri,
    onPress: () => onTokenPress(token),
    style: index !== suggestedList.length - 1 ? styles.suggestedToken : undefined
  })))), /*#__PURE__*/React.createElement(Separator, {
    style: styles.suggestedSeparator,
    color: "gray-glass-020"
  }), /*#__PURE__*/React.createElement(SectionList, {
    sections: filteredTokens,
    bounces: false,
    fadingEdgeLength: 20,
    contentContainerStyle: styles.tokenList,
    renderSectionHeader: ({
      section: {
        title
      }
    }) => /*#__PURE__*/React.createElement(Text, {
      variant: "paragraph-500",
      color: "fg-200",
      style: [{
        backgroundColor: Theme['bg-100']
      }, styles.title]
    }, title),
    ListEmptyComponent: /*#__PURE__*/React.createElement(Placeholder, {
      icon: "coinPlaceholder",
      title: "No tokens found",
      description: "Your tokens will appear here"
    }),
    getItemLayout: (_, index) => ({
      length: ListTokenTotalHeight,
      offset: ListTokenTotalHeight * index,
      index
    }),
    renderItem: ({
      item
    }) => /*#__PURE__*/React.createElement(ListToken, {
      key: item.name,
      name: item.name,
      imageSrc: item.logoUri,
      networkSrc: networkImage,
      value: item.value,
      amount: item.quantity.numeric,
      currency: item.symbol,
      onPress: () => onTokenPress(item),
      disabled: item.address === sourceToken?.address
    })
  }));
}
//# sourceMappingURL=index.js.map