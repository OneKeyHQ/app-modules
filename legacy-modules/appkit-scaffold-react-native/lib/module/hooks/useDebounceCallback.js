import { useCallback, useEffect, useRef } from 'react';
export function useDebounceCallback({
  callback,
  delay = 250
}) {
  const timeoutRef = useRef(null);
  const callbackRef = useRef(callback);
  useEffect(() => {
    callbackRef.current = callback;
  }, [callback]);
  const abort = useCallback(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
      timeoutRef.current = null;
    }
  }, []);
  const debouncedCallback = useCallback(args => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    timeoutRef.current = setTimeout(() => {
      callbackRef.current(args);
    }, delay);
  }, [delay]);
  useEffect(() => {
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