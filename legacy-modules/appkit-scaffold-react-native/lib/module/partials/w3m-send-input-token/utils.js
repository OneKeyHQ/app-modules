import { NumberUtil } from '@reown/appkit-common-react-native';
import { UiUtil } from '@reown/appkit-ui-react-native';
export function getSendValue(token, sendTokenAmount) {
  if (token && sendTokenAmount) {
    const price = token.price;
    const totalValue = price * sendTokenAmount;
    return totalValue ? `$${UiUtil.formatNumberToLocalString(totalValue, 2)}` : 'Incorrect value';
  }
  return null;
}
export function getMaxAmount(token) {
  if (token) {
    return NumberUtil.roundNumber(Number(token.quantity.numeric), 6, 5);
  }
  return null;
}
//# sourceMappingURL=utils.js.map