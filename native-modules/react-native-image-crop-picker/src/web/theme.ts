/// <reference lib="dom" />
import type {
  ImageCropperAppearance,
  ImageCropperColorScheme,
} from '../ReactNativeImageCropPicker.nitro';

// The same fallback palette as ios/ImageCropperTheme.swift and Android's
// ImageCropperTheme, so an app that passes no colors gets one look everywhere.
const PALETTES = {
  light: {
    backgroundColor: '#FFFFFF',
    titleColor: '#000000DF',
    iconColor: '#0000009B',
    cancelButtonColor: '#0000000F',
    cancelButtonPressedColor: '#0000001F',
    cancelButtonTextColor: '#000000DF',
    confirmButtonColor: '#000000DF',
    confirmButtonPressedColor: '#0000009B',
    confirmButtonTextColor: '#FFFFFFED',
  },
  dark: {
    backgroundColor: '#0F0F0F',
    titleColor: '#FFFFFFED',
    iconColor: '#FFFFFFAF',
    cancelButtonColor: '#FFFFFF12',
    cancelButtonPressedColor: '#FFFFFF22',
    cancelButtonTextColor: '#FFFFFFED',
    confirmButtonColor: '#FFFFFFED',
    confirmButtonPressedColor: '#FFFFFF72',
    confirmButtonTextColor: '#000000DF',
  },
} as const;

type PaletteKey = keyof (typeof PALETTES)['light'];

export type WebCropperTheme = Record<PaletteKey, string> & {
  colorScheme: ImageCropperColorScheme;
  // The area outside the crop box: the background at 70% opacity.
  dimmedColor: string;
  titleFontFamily?: string;
  buttonFontFamily?: string;
  scale: number;
};

const HEX_COLOR = /^#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})$/i;

function systemColorScheme(): ImageCropperColorScheme {
  if (
    typeof window !== 'undefined' &&
    typeof window.matchMedia === 'function' &&
    window.matchMedia('(prefers-color-scheme: dark)').matches
  ) {
    return 'dark';
  }
  return 'light';
}

// '#RGB' / '#RRGGBB' / '#RRGGBBAA' to [r, g, b, a] with a in 0...1.
export function parseHexColor(
  value: string
): [number, number, number, number] | undefined {
  if (!HEX_COLOR.test(value)) {
    return undefined;
  }
  let hex = value.slice(1);
  if (hex.length === 3) {
    hex = hex
      .split('')
      .map((char) => char + char)
      .join('');
  }
  const r = parseInt(hex.slice(0, 2), 16);
  const g = parseInt(hex.slice(2, 4), 16);
  const b = parseInt(hex.slice(4, 6), 16);
  const a = hex.length === 8 ? parseInt(hex.slice(6, 8), 16) / 255 : 1;
  return [r, g, b, a];
}

// CSS reads '#RRGGBBAA' natively; rgba() keeps older engines working.
function toCssColor(value: string): string {
  const rgba = parseHexColor(value);
  if (!rgba) {
    return value;
  }
  const [r, g, b, a] = rgba;
  return `rgba(${r}, ${g}, ${b}, ${Number(a.toFixed(3))})`;
}

function withAlpha(value: string, alpha: number): string {
  const rgba = parseHexColor(value);
  if (!rgba) {
    return value;
  }
  const [r, g, b] = rgba;
  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}

export function resolveTheme(
  appearance: ImageCropperAppearance | undefined
): WebCropperTheme {
  const colorScheme = appearance?.colorScheme ?? systemColorScheme();
  const palette = PALETTES[colorScheme];
  const color = (key: PaletteKey) => {
    const value = appearance?.[key];
    return toCssColor(value && HEX_COLOR.test(value) ? value : palette[key]);
  };
  const backgroundColor = color('backgroundColor');
  const scale = appearance?.scale;
  return {
    colorScheme,
    backgroundColor,
    titleColor: color('titleColor'),
    iconColor: color('iconColor'),
    cancelButtonColor: color('cancelButtonColor'),
    cancelButtonPressedColor: color('cancelButtonPressedColor'),
    cancelButtonTextColor: color('cancelButtonTextColor'),
    confirmButtonColor: color('confirmButtonColor'),
    confirmButtonPressedColor: color('confirmButtonPressedColor'),
    confirmButtonTextColor: color('confirmButtonTextColor'),
    dimmedColor: withAlpha(
      appearance?.backgroundColor && HEX_COLOR.test(appearance.backgroundColor)
        ? appearance.backgroundColor
        : palette.backgroundColor,
      0.7
    ),
    titleFontFamily: appearance?.titleFontFamily || undefined,
    buttonFontFamily: appearance?.buttonFontFamily || undefined,
    scale: scale !== undefined && scale > 0 ? scale : 1,
  };
}
