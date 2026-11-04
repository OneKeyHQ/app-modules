import { createElement } from 'react';
import { View } from 'react-native';
import { isIconName, type OneKeyIconProps } from './props';
import type { TextStyle } from 'react-native';
import { iconResourceHref } from '#onekey-icon-assets';

export type { OneKeyIconProps } from './props';

export function OneKeyIcon({
  name,
  color,
  style,
  ...viewProps
}: OneKeyIconProps) {
  const defaultStyle: TextStyle = {
    width: 24,
    height: 24,
    color: color ?? '#000000',
  };
  const href = isIconName(name) ? iconResourceHref(name) : undefined;
  return createElement(
    View,
    { ...viewProps, style: [defaultStyle, style] },
    href
      ? createElement('svg', {
          'width': '100%',
          'height': '100%',
          'aria-hidden': true,
          'focusable': false,
          'children': createElement('use', {
            href,
          }),
        })
      : null
  );
}
