import { ConstantsUtil } from '@reown/appkit-common-react-native';
export const HelpersUtil = {
  getCaipTokens(tokens) {
    if (!tokens) {
      return undefined;
    }
    const caipTokens = {};
    Object.entries(tokens).forEach(([id, token]) => {
      caipTokens[`${ConstantsUtil.EIP155}:${id}`] = token;
    });
    return caipTokens;
  }
};
//# sourceMappingURL=HelpersUtil.js.map