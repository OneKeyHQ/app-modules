import { useSnapshot } from 'valtio';
import { ScrollView, StyleSheet } from 'react-native';
import { Chip, CompatibleNetwork, FlexView, QrCode, Spacing, Text, UiUtil } from '@reown/appkit-ui-react-native';
import { AccountController, ApiController, AssetController, AssetUtil, NetworkController, OptionsController, RouterController, SnackController } from '@reown/appkit-core-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
export function WalletReceiveView() {
  const {
    address,
    profileName,
    preferredAccountType
  } = useSnapshot(AccountController.state);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const {
    padding
  } = useCustomDimensions();
  const canCopy = OptionsController.isClipboardAvailable();
  const isSmartAccount = preferredAccountType === 'smartAccount' && NetworkController.checkIfSmartAccountEnabled();
  const networks = isSmartAccount ? NetworkController.getSmartAccountEnabledNetworks() : NetworkController.getApprovedCaipNetworks();
  const imagesArray = networks.filter(network => network?.imageId).slice(0, 5).map(network => AssetUtil.getNetworkImage(network, AssetController.state.networkImages)).filter(Boolean);
  const label = UiUtil.getTruncateString({
    string: profileName ?? address ?? '',
    charsStart: profileName ? 30 : 4,
    charsEnd: profileName ? 0 : 4,
    truncate: profileName ? 'end' : 'middle'
  });
  const onNetworkPress = () => {
    RouterController.push('WalletCompatibleNetworks');
  };
  const onCopyAddress = () => {
    if (canCopy && AccountController.state.address) {
      OptionsController.copyToClipboard(AccountController.state.address);
      SnackController.showSuccess('Address copied');
    }
  };
  if (!address) return;
  return /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xl', 'xl', '2xl', 'xl'],
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Chip, {
    label: label,
    rightIcon: canCopy ? 'copy' : undefined,
    imageSrc: networkImage,
    variant: "transparent",
    onPress: onCopyAddress
  }), /*#__PURE__*/React.createElement(QrCode, {
    uri: address,
    size: 232,
    arenaClear: true,
    style: styles.qrContainer
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    color: "fg-100"
  }, canCopy ? 'Copy your address or scan this QR code' : 'Scan this QR code'), /*#__PURE__*/React.createElement(CompatibleNetwork, {
    text: "Only receive from networks",
    onPress: onNetworkPress,
    networkImages: imagesArray,
    imageHeaders: ApiController._getApiHeaders(),
    style: styles.networksButton
  })));
}
const styles = StyleSheet.create({
  qrContainer: {
    marginVertical: Spacing.xl
  },
  networksButton: {
    marginTop: Spacing.l
  }
});
//# sourceMappingURL=index.js.map