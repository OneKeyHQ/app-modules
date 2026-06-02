import { useCallback, useRef } from 'react';
import { callback, getHostComponent } from 'react-native-nitro-modules';

import type {
  PerpDepthBarsMethods,
  PerpDepthBarsProps,
} from './PerpDepthBars.nitro';
import type {
  PerpSideRatioMethods,
  PerpSideRatioProps,
} from './PerpSideRatio.nitro';

const PerpDepthBarsConfig = require('../nitrogen/generated/shared/json/PerpDepthBarsConfig.json');
const PerpSideRatioConfig = require('../nitrogen/generated/shared/json/PerpSideRatioConfig.json');

export type {
  PerpDepthBarsMethods,
  PerpDepthBarsProps,
  PerpSideRatioMethods,
  PerpSideRatioProps,
};

const PerpDepthBarsHost = getHostComponent<
  PerpDepthBarsProps,
  PerpDepthBarsMethods
>('PerpDepthBars', () => PerpDepthBarsConfig);

/**
 * Thin wrapper so consumers pass a plain `onRowPress` function. Nitro view
 * callbacks must be wrapped with `callback(...)` to cross the JSI boundary;
 * we encapsulate that here (with a ref so the underlying callback stays
 * stable across re-renders) instead of leaking it to every call site.
 */
export function PerpDepthBarsView({
  onRowPress,
  ...props
}: PerpDepthBarsProps) {
  const onRowPressRef = useRef(onRowPress);
  onRowPressRef.current = onRowPress;

  const stableOnRowPress = useCallback((rowIndex: number) => {
    onRowPressRef.current?.(rowIndex);
  }, []);

  return (
    <PerpDepthBarsHost
      {...props}
      onRowPress={onRowPress ? callback(stableOnRowPress) : undefined}
    />
  );
}

export const PerpSideRatioView = getHostComponent<
  PerpSideRatioProps,
  PerpSideRatioMethods
>('PerpSideRatio', () => PerpSideRatioConfig);
