"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.useCustomDimensions = useCustomDimensions;
var _react = require("react");
var _reactNative = require("react-native");
/**
 * Hook used to get the width of the screen and the padding needed to accomplish portrait and landscape modes.
 * @returns { width: number, isPortrait: boolean, isLandscape: boolean, padding: number }
 */
function useCustomDimensions() {
  const {
    width,
    height
  } = (0, _reactNative.useWindowDimensions)();
  const [maxWidth, setMaxWidth] = (0, _react.useState)(Math.min(width, height));
  const [isPortrait, setIsPortrait] = (0, _react.useState)(height > width);
  const [padding, setPadding] = (0, _react.useState)(0);
  (0, _react.useEffect)(() => {
    setMaxWidth(Math.min(width, height));
    setIsPortrait(height > width);
    setPadding(width < height ? 0 : (width - height) / 2);
  }, [width, height]);
  return {
    maxWidth,
    isPortrait,
    isLandscape: !isPortrait,
    padding
  };
}
//# sourceMappingURL=useCustomDimensions.js.map