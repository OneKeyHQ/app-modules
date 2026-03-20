"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.useKeyboard = useKeyboard;
var _react = require("react");
var _reactNative = require("react-native");
const emptyCoordinates = Object.freeze({
  screenX: 0,
  screenY: 0,
  width: 0,
  height: 0
});
const initialValue = {
  start: emptyCoordinates,
  end: emptyCoordinates
};
function useKeyboard() {
  const [shown, setShown] = (0, _react.useState)(false);
  const [coordinates, setCoordinates] = (0, _react.useState)(initialValue);
  const [keyboardHeight, setKeyboardHeight] = (0, _react.useState)(0);
  const handleKeyboardWillShow = e => {
    setCoordinates({
      start: e.startCoordinates,
      end: e.endCoordinates
    });
  };
  const handleKeyboardDidShow = e => {
    setShown(true);
    setCoordinates({
      start: e.startCoordinates,
      end: e.endCoordinates
    });
    setKeyboardHeight(e.endCoordinates.height);
  };
  const handleKeyboardWillHide = e => {
    setCoordinates({
      start: e.startCoordinates,
      end: e.endCoordinates
    });
  };
  const handleKeyboardDidHide = e => {
    setShown(false);
    if (e) {
      setCoordinates({
        start: e.startCoordinates,
        end: e.endCoordinates
      });
    } else {
      setCoordinates(initialValue);
      setKeyboardHeight(0);
    }
  };
  (0, _react.useEffect)(() => {
    const subscriptions = [_reactNative.Keyboard.addListener('keyboardWillShow', handleKeyboardWillShow), _reactNative.Keyboard.addListener('keyboardDidShow', handleKeyboardDidShow), _reactNative.Keyboard.addListener('keyboardWillHide', handleKeyboardWillHide), _reactNative.Keyboard.addListener('keyboardDidHide', handleKeyboardDidHide)];
    return () => {
      subscriptions.forEach(subscription => subscription.remove());
    };
  }, []);
  return {
    keyboardShown: shown,
    coordinates,
    keyboardHeight
  };
}
//# sourceMappingURL=useKeyboard.js.map