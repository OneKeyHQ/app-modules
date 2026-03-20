"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _react = require("react");
function useTimeout(delay) {
  const timeLeftRef = (0, _react.useRef)(delay);
  const [timeLeft, setTimeLeft] = (0, _react.useState)(delay);
  const interval = (0, _react.useRef)();
  const startTimer = (0, _react.useCallback)(newDelay => {
    timeLeftRef.current = newDelay;
    setTimeLeft(newDelay);
    interval.current = setInterval(() => {
      if (timeLeftRef.current > 0) {
        timeLeftRef.current -= 1;
        setTimeLeft(timeLeftRef.current);
      } else {
        if (typeof interval.current === 'number') {
          clearInterval(interval.current);
        }
      }
    }, 1000);
  }, []);
  (0, _react.useEffect)(() => {
    return () => {
      if (typeof interval.current === 'number') {
        clearInterval(interval.current);
      }
    };
  }, [interval]);
  return {
    timeLeft,
    startTimer
  };
}
var _default = exports.default = useTimeout;
//# sourceMappingURL=useTimeout.js.map