"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.useDebounceCallback = useDebounceCallback;
var _react = require("react");
function useDebounceCallback({
  callback,
  delay = 250
}) {
  const timeoutRef = (0, _react.useRef)(null);
  const callbackRef = (0, _react.useRef)(callback);
  (0, _react.useEffect)(() => {
    callbackRef.current = callback;
  }, [callback]);
  const abort = (0, _react.useCallback)(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
      timeoutRef.current = null;
    }
  }, []);
  const debouncedCallback = (0, _react.useCallback)(args => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    timeoutRef.current = setTimeout(() => {
      callbackRef.current(args);
    }, delay);
  }, [delay]);
  (0, _react.useEffect)(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);
  return {
    debouncedCallback,
    abort
  };
}
//# sourceMappingURL=useDebounceCallback.js.map