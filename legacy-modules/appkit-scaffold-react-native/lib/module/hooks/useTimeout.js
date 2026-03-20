import { useCallback, useEffect, useRef, useState } from 'react';
function useTimeout(delay) {
  const timeLeftRef = useRef(delay);
  const [timeLeft, setTimeLeft] = useState(delay);
  const interval = useRef();
  const startTimer = useCallback(newDelay => {
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
  useEffect(() => {
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
export default useTimeout;
//# sourceMappingURL=useTimeout.js.map