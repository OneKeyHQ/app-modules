import { useSnapshot } from 'valtio';
import Modal from 'react-native-modal';
import { FlatList, View } from 'react-native';
import { FlexView, IconBox, IconLink, Image, SearchBar, Separator, Spacing, Text, useTheme } from '@reown/appkit-ui-react-native';
import styles from './styles';
import { AssetController, AssetUtil, NetworkController } from '@reown/appkit-core-react-native';
const SEPARATOR_HEIGHT = Spacing.s;
export function SelectorModal({
  title,
  visible,
  onClose,
  items,
  selectedItem,
  renderItem,
  onSearch,
  searchPlaceholder,
  keyExtractor,
  itemHeight,
  showNetwork
}) {
  const Theme = useTheme();
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const renderSeparator = () => {
    return /*#__PURE__*/React.createElement(View, {
      style: {
        height: SEPARATOR_HEIGHT
      }
    });
  };
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
    icon: "chevronLeft",
    onPress: onClose,
    testID: "selector-modal-button-back"
  }), !!title && /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-600"
  }, title), showNetwork ? networkImage ? /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: styles.iconPlaceholder
  }, /*#__PURE__*/React.createElement(Image, {
    source: networkImage,
    style: styles.networkImage
  })) : /*#__PURE__*/React.createElement(IconBox, {
    style: styles.iconPlaceholder,
    icon: "networkPlaceholder",
    background: true,
    iconColor: "fg-200",
    size: "sm"
  }) : /*#__PURE__*/React.createElement(View, {
    style: styles.iconPlaceholder
  })), /*#__PURE__*/React.createElement(SearchBar, {
    onChangeText: onSearch,
    style: styles.searchBar,
    placeholder: searchPlaceholder
  }), selectedItem && /*#__PURE__*/React.createElement(FlexView, {
    style: styles.selectedContainer
  }, renderItem({
    item: selectedItem
  }), /*#__PURE__*/React.createElement(Separator, {
    style: styles.separator,
    color: "gray-glass-020"
  })), /*#__PURE__*/React.createElement(FlatList, {
    data: items,
    renderItem: renderItem,
    fadingEdgeLength: 20,
    contentContainerStyle: styles.listContent,
    ItemSeparatorComponent: renderSeparator,
    keyExtractor: keyExtractor,
    getItemLayout: itemHeight ? (_, index) => ({
      length: itemHeight + SEPARATOR_HEIGHT,
      offset: (itemHeight + SEPARATOR_HEIGHT) * index,
      index
    }) : undefined
  })));
}
//# sourceMappingURL=index.js.map