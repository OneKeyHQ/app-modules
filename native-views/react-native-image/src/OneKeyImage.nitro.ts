import type {
  HybridObject,
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

export enum OneKeyImageVariant {
  GENERIC = 'generic',
  TOKEN = 'token',
  NETWORK = 'network',
  AVATAR = 'avatar',
}

export enum OneKeyImageContentFit {
  COVER = 'cover',
  CONTAIN = 'contain',
  FILL = 'fill',
  CENTER = 'center',
}

export enum OneKeyImageCachePolicy {
  MEMORY_DISK = 'memory-disk',
  MEMORY = 'memory',
  DISK = 'disk',
  NONE = 'none',
}

export type OneKeyImageCacheType = 'none' | 'memory' | 'disk';

export enum OneKeyImageLoadingStrategy {
  STATIC = 'static',
  SKELETON = 'skeleton',
  NONE = 'none',
}

export interface OneKeyImageNativeProps extends HybridViewProps {
  sourceUri?: string;
  sourceHeadersJson?: string;
  variant?: OneKeyImageVariant;
  contentFit?: OneKeyImageContentFit;
  cachePolicy?: OneKeyImageCachePolicy;
  autoplay?: boolean;
  recyclingKey?: string;
  optimizeTos?: boolean;
  overscan?: number;
  loadingStrategy?: OneKeyImageLoadingStrategy;
  onLoadStart?: () => void;
  onLoad?: (
    width: number,
    height: number,
    cacheType: OneKeyImageCacheType
  ) => void;
  onDisplay?: () => void;
  onError?: (message: string) => void;
  onLoadEnd?: () => void;
}

export interface OneKeyImageMethods extends HybridViewMethods {
  reload(): void;
  cancel(): void;
}

export interface OneKeyImagePreloadSource {
  uri: string;
  headersJson?: string;
  cachePolicy?: OneKeyImageCachePolicy;
  resizeWidth?: number;
  resizeHeight?: number;
  pixelRatio?: number;
  overscan?: number;
  optimizeTos?: boolean;
}

export interface OneKeyImageCache
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  preload(sources: OneKeyImagePreloadSource[]): Promise<boolean>;
  clearMemory(): Promise<void>;
  clearDisk(): Promise<void>;
  clearAll(): Promise<void>;
}

export type OneKeyImage = HybridView<
  OneKeyImageNativeProps,
  OneKeyImageMethods
>;
