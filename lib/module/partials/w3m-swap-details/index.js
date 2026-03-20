import { useSnapshot } from 'valtio';
import { useState } from 'react';
import { ConstantsUtil, NetworkController, SwapController } from '@reown/appkit-core-react-native';
import { FlexView, Text, UiUtil, Toggle, useTheme, Pressable, Icon } from '@reown/appkit-ui-react-native';
import { NumberUtil } from '@reown/appkit-common-react-native';
import { InformationModal } from '../w3m-information-modal';
import styles from './styles';
import { getModalData } from './utils';
// -- Constants ----------------------------------------- //
const slippageRate = ConstantsUtil.CONVERT_SLIPPAGE_TOLERANCE;
export function SwapDetails({
  initialOpen,
  canClose
}) {
  const Theme = useTheme();
  const {
    maxSlippage = 0,
    sourceToken,
    toToken,
    gasPriceInUSD = 0,
    priceImpact,
    toTokenAmount
  } = useSnapshot(SwapController.state);
  const [modalData, setModalData] = useState();
  const toTokenSwappedAmount = SwapController.state.sourceTokenPriceInUSD && SwapController.state.toTokenPriceInUSD ? 1 / SwapController.state.toTokenPriceInUSD * SwapController.state.sourceTokenPriceInUSD : 0;
  const renderTitle = () => /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "flex-start"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-100"
  }, "1 ", SwapController.state.sourceToken?.symbol, " = ", '', UiUtil.formatNumberToLocalString(toTokenSwappedAmount, 3), ' ', SwapController.state.toToken?.symbol), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200",
    style: styles.titlePrice
  }, "~$", UiUtil.formatNumberToLocalString(SwapController.state.sourceTokenPriceInUSD)));
  const minimumReceive = NumberUtil.parseLocalStringToNumber(toTokenAmount) - maxSlippage;
  const providerFee = SwapController.getProviderFeePrice();
  const onPriceImpactPress = () => {
    setModalData(getModalData('priceImpact'));
  };
  const onSlippagePress = () => {
    const minimumString = UiUtil.formatNumberToLocalString(minimumReceive, minimumReceive < 1 ? 8 : 2);
    setModalData(getModalData('slippage', {
      minimumReceive: minimumString,
      toTokenSymbol: SwapController.state.toToken?.symbol
    }));
  };
  const onNetworkCostPress = () => {
    setModalData(getModalData('networkCost', {
      networkSymbol: SwapController.state.networkTokenSymbol,
      networkName: NetworkController.state.caipNetwork?.name
    }));
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Toggle, {
    title: renderTitle(),
    style: [styles.container, {
      backgroundColor: Theme['gray-glass-005']
    }],
    initialOpen: initialOpen,
    canClose: canClose
  }, /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150",
    style: styles.detailTitle
  }, "Network cost"), /*#__PURE__*/React.createElement(Pressable, {
    onPress: onNetworkCostPress,
    style: styles.infoIcon,
    hitSlop: 10
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "infoCircle",
    size: "sm",
    color: "fg-150"
  }))), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-100"
  }, "$", UiUtil.formatNumberToLocalString(gasPriceInUSD, gasPriceInUSD < 1 ? 8 : 2))), !!priceImpact && /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150",
    style: styles.detailTitle
  }, "Price impact"), /*#__PURE__*/React.createElement(Pressable, {
    onPress: onPriceImpactPress,
    style: styles.infoIcon,
    hitSlop: 10
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "infoCircle",
    size: "sm",
    color: "fg-150"
  }))), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-100"
  }, "~", UiUtil.formatNumberToLocalString(priceImpact, 3), "%")), maxSlippage !== undefined && maxSlippage > 0 && !!sourceToken?.symbol && /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150",
    style: styles.detailTitle
  }, "Max. slippage"), /*#__PURE__*/React.createElement(Pressable, {
    onPress: onSlippagePress,
    style: styles.infoIcon,
    hitSlop: 10
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "infoCircle",
    size: "sm",
    color: "fg-150"
  }))), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200"
  }, UiUtil.formatNumberToLocalString(maxSlippage, 6), " ", toToken?.symbol, ' ', /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-100"
  }, slippageRate, "%"))), /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150",
    style: styles.detailTitle
  }, "Included provider fee"), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-100"
  }, "$", UiUtil.formatNumberToLocalString(providerFee, providerFee < 1 ? 8 : 2)))), /*#__PURE__*/React.createElement(InformationModal, {
    iconName: "infoCircle",
    title: modalData?.title,
    description: modalData?.description,
    visible: !!modalData,
    onClose: () => setModalData(undefined)
  }));
}
//# sourceMappingURL=index.js.map