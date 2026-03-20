import { useSnapshot } from 'valtio';
import { ScrollView } from 'react-native';
import { Avatar, Button, FlexView, Icon, Image, Text, UiUtil } from '@reown/appkit-ui-react-native';
import { NumberUtil } from '@reown/appkit-common-react-native';
import { NetworkController, RouterController, SendController } from '@reown/appkit-core-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import { PreviewSendPill } from './components/preview-send-pill';
import styles from './styles';
import { PreviewSendDetails } from './components/preview-send-details';
export function WalletSendPreviewView() {
  const {
    padding
  } = useCustomDimensions();
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    token,
    receiverAddress,
    receiverProfileName,
    receiverProfileImageUrl,
    gasPriceInUSD,
    loading
  } = useSnapshot(SendController.state);
  const getSendValue = () => {
    if (SendController.state.token && SendController.state.sendTokenAmount) {
      const price = SendController.state.token.price;
      const totalValue = price * SendController.state.sendTokenAmount;
      return totalValue.toFixed(2);
    }
    return null;
  };
  const getTokenAmount = () => {
    const value = SendController.state.sendTokenAmount ? NumberUtil.roundNumber(SendController.state.sendTokenAmount, 6, 5) : 'unknown';
    return `${value} ${SendController.state.token?.symbol}`;
  };
  const formattedAddress = receiverProfileName ? UiUtil.getTruncateString({
    string: receiverProfileName,
    charsStart: 20,
    charsEnd: 0,
    truncate: 'end'
  }) : UiUtil.getTruncateString({
    string: receiverAddress || '',
    charsStart: 4,
    charsEnd: 4,
    truncate: 'middle'
  });
  const onSend = () => {
    SendController.sendToken();
  };
  const onCancel = () => {
    RouterController.goBack();
    SendController.setLoading(false);
  };
  return /*#__PURE__*/React.createElement(ScrollView, {
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(FlexView, {
    padding: ['l', 'xl', '3xl', 'xl']
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(FlexView, null, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Send"), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-100"
  }, "$", getSendValue())), /*#__PURE__*/React.createElement(PreviewSendPill, {
    text: getTokenAmount()
  }, token?.iconUrl ? /*#__PURE__*/React.createElement(Image, {
    source: token?.iconUrl,
    style: styles.tokenLogo
  }) : /*#__PURE__*/React.createElement(Icon, {
    name: "coinPlaceholder",
    height: 32,
    width: 32,
    style: styles.tokenLogo,
    color: "fg-200"
  }))), /*#__PURE__*/React.createElement(Icon, {
    name: "arrowBottom",
    height: 14,
    width: 14,
    color: "fg-200",
    style: styles.arrow
  }), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, "To"), /*#__PURE__*/React.createElement(PreviewSendPill, {
    text: formattedAddress
  }, /*#__PURE__*/React.createElement(Avatar, {
    address: receiverAddress,
    imageSrc: receiverProfileImageUrl,
    size: 32,
    borderWidth: 0,
    style: styles.avatar
  }))), /*#__PURE__*/React.createElement(PreviewSendDetails, {
    style: styles.details,
    networkFee: gasPriceInUSD,
    address: receiverAddress,
    name: receiverProfileName,
    caipNetwork: caipNetwork
  }), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "warningCircle",
    size: "sm",
    color: "fg-200",
    style: styles.reviewIcon
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200"
  }, "Review transaction carefully")), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    margin: ['l', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Button, {
    variant: "shade",
    style: styles.cancelButton,
    onPress: onCancel
  }, "Cancel"), /*#__PURE__*/React.createElement(Button, {
    variant: "fill",
    style: styles.sendButton,
    onPress: onSend,
    loading: loading
  }, "Send"))));
}
//# sourceMappingURL=index.js.map