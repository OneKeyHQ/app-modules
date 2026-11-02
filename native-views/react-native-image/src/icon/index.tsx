import { createElement } from 'react';
import {
  getHostComponent,
  type HybridViewMethods,
} from 'react-native-nitro-modules';
import type { OneKeyIconNativeProps } from '../OneKeyIcon.nitro';
import { isIconName, normalizeIconColor, type OneKeyIconProps } from './props';

const config = require('../../nitrogen/generated/shared/json/OneKeyIconConfig.json');
const NativeIcon = getHostComponent<OneKeyIconNativeProps, HybridViewMethods>(
  'OneKeyIcon',
  () => config
);

export type { OneKeyIconProps } from './props';

export function OneKeyIcon({
  name,
  color,
  style,
  ...viewProps
}: OneKeyIconProps) {
  const resolvedColor = normalizeIconColor(color);
  return createElement(NativeIcon, {
    ...viewProps,
    iconName: resolvedColor && isIconName(name) ? name : '',
    resolvedColor: resolvedColor ?? '',
    style: [{ width: 24, height: 24 }, style],
  });
}
