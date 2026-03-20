import { useSnapshot } from 'valtio';
import { AssetController, AssetUtil } from '@reown/appkit-core-react-native';
import { BorderRadius, FlexView, NetworkImage, Spacing, Text, UiUtil, useTheme } from '@reown/appkit-ui-react-native';
import { StyleSheet } from 'react-native';
export function PreviewSendDetails({
  address,
  name,
  caipNetwork,
  networkFee,
  style
}) {
  const Theme = useTheme();
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const formattedName = UiUtil.getTruncateString({
    string: name ?? '',
    charsStart: 20,
    charsEnd: 0,
    truncate: 'end'
  });
  const formattedAddress = UiUtil.getTruncateString({
    string: address || '',
    charsStart: 6,
    charsEnd: 8,
    truncate: 'middle'
  });
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  return /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.container, {
      backgroundColor: Theme['gray-glass-002']
    }, style],
    padding: "s"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200",
    style: styles.title
  }, "Details"), /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Network cost"), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-100"
  }, "$", UiUtil.formatNumberToLocalString(networkFee, 2))), /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, formattedName || 'Address'), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-100"
  }, formattedAddress)), /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Network"), /*#__PURE__*/React.createElement(NetworkImage, {
    imageSrc: networkImage,
    size: "xs"
  })));
}
const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    borderRadius: BorderRadius.xxs
  },
  title: {
    marginBottom: Spacing.xs
  },
  item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: Spacing.s,
    borderRadius: BorderRadius.xxs,
    marginTop: Spacing['2xs']
  },
  networkImage: {
    height: 24,
    width: 24,
    borderRadius: BorderRadius.full
  }
});
//# sourceMappingURL=preview-send-details.js.map