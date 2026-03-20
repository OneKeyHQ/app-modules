import { ScrollView } from 'react-native';
import { useSnapshot } from 'valtio';
import { FlexView, Text, Banner, NetworkImage } from '@reown/appkit-ui-react-native';
import { AccountController, ApiController, AssetController, AssetUtil, NetworkController } from '@reown/appkit-core-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import styles from './styles';
export function WalletCompatibleNetworks() {
  const {
    padding
  } = useCustomDimensions();
  const {
    preferredAccountType
  } = useSnapshot(AccountController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const isSmartAccount = preferredAccountType === 'smartAccount' && NetworkController.checkIfSmartAccountEnabled();
  const approvedNetworks = isSmartAccount ? NetworkController.getSmartAccountEnabledNetworks() : NetworkController.getApprovedCaipNetworks();
  const imageHeaders = ApiController._getApiHeaders();
  return /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    style: {
      paddingHorizontal: padding
    },
    fadingEdgeLength: 20
  }, /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xl', 's', '2xl', 's']
  }, /*#__PURE__*/React.createElement(Banner, {
    icon: "warningCircle",
    text: "You can only receive assets on these networks."
  }), approvedNetworks.map((network, index) => /*#__PURE__*/React.createElement(FlexView, {
    key: network?.id ?? index,
    flexDirection: "row",
    alignItems: "center",
    padding: ['s', 's', 's', 's']
  }, /*#__PURE__*/React.createElement(NetworkImage, {
    imageSrc: AssetUtil.getNetworkImage(network, networkImages),
    imageHeaders: imageHeaders,
    size: "sm",
    style: styles.image
  }), /*#__PURE__*/React.createElement(Text, {
    color: "fg-100",
    variant: "paragraph-500"
  }, network?.name ?? 'Unknown Network')))));
}
//# sourceMappingURL=index.js.map