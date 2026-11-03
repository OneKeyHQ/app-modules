import type { ViewProps } from 'react-native';

export interface OneKeyIconProps extends Omit<ViewProps, 'children'> {
  name: string;
  color?: string;
}

export function normalizeIconColor(input = '#000000'): string | undefined {
  const hex = /^#([\da-f]{3}|[\da-f]{4}|[\da-f]{6}|[\da-f]{8})$/i.exec(input);
  if (hex) {
    let value = hex[1]!;
    if (value.length < 5)
      value = [...value].map((part) => part + part).join('');
    const channels = [0, 2, 4].map((start) =>
      parseInt(value.slice(start, start + 2), 16)
    );
    const alpha = value.length === 8 ? parseInt(value.slice(6), 16) / 255 : 1;
    return `rgba(${channels.join(',')},${alpha})`;
  }
  const rgb =
    /^(rgb|rgba)\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*(?:,\s*((?:\d+(?:\.\d*)?|\.\d+))\s*)?\)$/.exec(
      input
    );
  if (!rgb || (rgb[1] === 'rgba') !== (rgb[5] !== undefined)) return undefined;
  const channels = rgb.slice(2, 5).map(Number);
  const alpha = Number(rgb[5] ?? 1);
  if (
    channels.some((value) => value > 255) ||
    !Number.isFinite(alpha) ||
    alpha > 1
  )
    return undefined;
  return `rgba(${channels.join(',')},${Math.round(alpha * 255) / 255})`;
}

export function isIconName(name: string): boolean {
  return /^[A-Z][A-Za-z0-9]*$/.test(name);
}
