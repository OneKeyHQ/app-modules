import type {
  TrailingAccessory,
  ImageSource,
  LeadingVisual,
  NativeListTextStyle,
  NativeListImageStyle,
} from '../../models';

export type RowPrimitives = Readonly<{
  background: string;
  icon: (element: HTMLElement, name: string) => void;
  iconAction: (
    name: string,
    actionKey: string | undefined,
    disabled?: boolean,
    tintColor?: string
  ) => HTMLElement;
  accessory: (
    rowKey: string,
    descriptor: TrailingAccessory,
    slot: number
  ) => HTMLElement;
  visual: (
    source: LeadingVisual | undefined,
    presentation?: string
  ) => HTMLElement | undefined;
  thumbnail: (source: ImageSource) => HTMLElement | undefined;
  textStyle: (
    element: HTMLElement,
    style: NativeListTextStyle,
    defaultLines?: number
  ) => void;
  dispose: (element: HTMLElement) => void;
}>;

const properties = [
  'width',
  'height',
  'flex-basis',
  'border-radius',
  'overflow',
  'object-fit',
  'margin-inline-end',
  'margin-bottom',
  'align-self',
  'border',
  'box-sizing',
  'clip-path',
  'flex',
];

/** Restores factory geometry without touching async loading visibility. */
export class RowVisualStyle {
  private readonly defaults = new Map<HTMLElement, Map<string, string>>();
  constructor(private readonly view: HTMLElement) {
    [view, ...view.querySelectorAll<HTMLElement>('*')].forEach((element) => {
      this.defaults.set(
        element,
        new Map(
          properties.map((key) => [key, element.style.getPropertyValue(key)])
        )
      );
    });
  }
  restore() {
    this.defaults.forEach((style, element) =>
      style.forEach((value, key) => {
        element.style.removeProperty(key);
        if (value) element.style.setProperty(key, value);
      })
    );
  }
  apply(
    image: NativeListImageStyle | undefined,
    width: number,
    height: number,
    margin: number
  ) {
    this.restore();
    const leading = this.view;
    leading.style.width = `${width}px`;
    leading.style.height = `${height}px`;
    leading.style.flexBasis = `${width}px`;
    leading.style.marginInlineEnd = `${margin}px`;
    const radius =
      image?.cornerRadius !== undefined
        ? `${image.cornerRadius}px`
        : image?.shape === 'circle'
        ? '50%'
        : image?.shape === 'square'
        ? '0px'
        : image?.shape === 'rounded'
        ? '10px'
        : undefined;
    if (radius !== undefined) {
      leading.style.setProperty('border-radius', radius, 'important');
      leading.style.overflow = leading.matches('img') ? 'hidden' : 'visible';
    }
    const images = leading.matches('img')
      ? [leading]
      : leading.querySelectorAll<HTMLElement>(
          ':scope > img:not(.ok-native-list-visual-corner), :scope > .ok-native-list-visual-fallback:not(.ok-native-list-visual-corner)'
        );
    images.forEach((bitmap) => {
      if (bitmap !== leading) {
        if (image?.width !== undefined) bitmap.style.width = '100%';
        if (image?.height !== undefined) bitmap.style.height = '100%';
      }
      if (radius !== undefined) bitmap.style.borderRadius = radius;
      if (image?.contentFit)
        bitmap.style.objectFit =
          image.contentFit === 'center' ? 'none' : image.contentFit;
    });
  }
}
