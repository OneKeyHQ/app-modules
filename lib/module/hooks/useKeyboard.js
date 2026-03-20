import { useEffect, useState } from 'react';
import { Keyboard } from 'react-native';
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
export function useKeyboard() {
  const [shown, setShown] = useState(false);
  const [coordinates, setCoordinates] = useState(initialValue);
  const [keyboardHeight, setKeyboardHeight] = useState(0);
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
  useEffect(() => {
    const subscriptions = [Keyboard.addListener('keyboardWillShow', handleKeyboardWillShow), Keyboard.addListener('keyboardDidShow', handleKeyboardDidShow), Keyboard.addListener('keyboardWillHide', handleKeyboardWillHide), Keyboard.addListener('keyboardDidHide', handleKeyboardDidHide)];
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