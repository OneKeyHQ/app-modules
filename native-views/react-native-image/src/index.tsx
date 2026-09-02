import {
  createElement,
  useEffect,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from 'react';
import {
  Image,
  Platform,
  StyleSheet,
  View,
  type ImageSourcePropType,
  type ImageURISource,
  type ViewProps,
} from 'react-native';
import {
  callback,
  getHostComponent,
  NitroModules,
  type NitroViewWrappedCallback,
} from 'react-native-nitro-modules';
const OneKeyImageConfig = require('../nitrogen/generated/shared/json/OneKeyImageConfig.json');
import type {
  OneKeyImageCache as OneKeyImageCacheHybrid,
  OneKeyImageCacheType as OneKeyImageNativeCacheType,
  OneKeyImageMethods,
  OneKeyImageNativeProps,
} from './OneKeyImage.nitro';
import {
  OneKeyImageCachePolicy,
  OneKeyImageContentFit,
  OneKeyImageLoadingStrategy,
  OneKeyImageVariant,
} from './OneKeyImage.nitro';

export {
  OneKeyImageCachePolicy,
  OneKeyImageContentFit,
  OneKeyImageLoadingStrategy,
  OneKeyImageVariant,
};
export type {
  OneKeyImageMethods,
  OneKeyImageNativeProps,
  OneKeyImagePreloadSource,
} from './OneKeyImage.nitro';

export enum OneKeyImageCacheType {
  NONE = 'none',
  MEMORY = 'memory',
  DISK = 'disk',
}

type MaybeWrappedCallback<T extends (...args: any[]) => void> =
  | T
  | NitroViewWrappedCallback<T>;

export interface OneKeyImageSource extends ImageURISource {
  cachePolicy?: OneKeyImageCachePolicy;
}

export interface OneKeyImagePreloadInput extends OneKeyImageSource {
  resizeWidth?: number;
  resizeHeight?: number;
  pixelRatio?: number;
  overscan?: number;
  optimizeTos?: boolean;
}

export interface OneKeyImageLoadEvent {
  source: {
    url: string;
    width: number;
    height: number;
  };
  cacheType: OneKeyImageCacheType;
}

export interface OneKeyImageErrorEvent {
  error: string;
}

export interface OneKeyImageProps
  extends Omit<
      OneKeyImageNativeProps,
      | 'sourceUri'
      | 'sourceHeadersJson'
      | 'onLoadStart'
      | 'onLoad'
      | 'onDisplay'
      | 'onError'
      | 'onLoadEnd'
    >,
    ViewProps {
  source?: OneKeyImageSource | number;
  /** Content shown while a non-null source is loading. */
  placeholder?: ReactNode;
  /** Content shown when source is unavailable or fails to load. */
  fallback?: ReactNode;
  hybridRef?: MaybeWrappedCallback<(ref: OneKeyImageMethods) => void>;
  onLoadStart?: MaybeWrappedCallback<() => void>;
  onLoad?: MaybeWrappedCallback<(event: OneKeyImageLoadEvent) => void>;
  onDisplay?: MaybeWrappedCallback<() => void>;
  onError?: MaybeWrappedCallback<(event: OneKeyImageErrorEvent) => void>;
  onLoadEnd?: MaybeWrappedCallback<() => void>;
}

const NativeOneKeyImage = getHostComponent<
  OneKeyImageNativeProps,
  OneKeyImageMethods
>('OneKeyImage', () => OneKeyImageConfig);

const nativeCache =
  NitroModules.createHybridObject<OneKeyImageCacheHybrid>('OneKeyImageCache');

function unwrap<T extends (...args: any[]) => void>(
  value: MaybeWrappedCallback<T> | undefined
): T | undefined {
  return typeof value === 'function' ? value : value?.f;
}

function normalizeSource(
  source: OneKeyImageProps['source']
): OneKeyImageSource | undefined {
  if (source == null) return undefined;
  const resolved = Image.resolveAssetSource(source as ImageSourcePropType);
  if (resolved?.uri) {
    const original =
      typeof source === 'object' && !Array.isArray(source)
        ? (source as OneKeyImageSource)
        : undefined;
    return {
      uri: resolved.uri,
      width: resolved.width,
      height: resolved.height,
      scale: resolved.scale,
      headers: original?.headers,
      cachePolicy: original?.cachePolicy,
    };
  }
  return undefined;
}

export function OneKeyImage({
  source,
  placeholder,
  fallback,
  variant = OneKeyImageVariant.GENERIC,
  contentFit = OneKeyImageContentFit.COVER,
  cachePolicy,
  autoplay,
  recyclingKey,
  optimizeTos = true,
  overscan = 1.1,
  loadingStrategy = OneKeyImageLoadingStrategy.STATIC,
  hybridRef,
  onLoadStart,
  onLoad,
  onDisplay,
  onError,
  onLoadEnd,
  style,
  ...viewProps
}: OneKeyImageProps) {
  const normalized = useMemo(() => normalizeSource(source), [source]);
  const resolvedCachePolicy =
    cachePolicy ??
    normalized?.cachePolicy ??
    OneKeyImageCachePolicy.MEMORY_DISK;
  const identity = `${normalized?.uri ?? ''}|${JSON.stringify(
    normalized?.headers ?? {}
  )}|${resolvedCachePolicy}|${recyclingKey ?? ''}|${contentFit}|${
    optimizeTos ? '1' : '0'
  }|${overscan}`;
  const hasSource = normalized != null;
  const hasOverlay = placeholder != null || fallback != null;
  type LoadState = {
    identity: string;
    status: 'loading' | 'loaded' | 'fallback';
  };
  const [loadState, setLoadState] = useState<LoadState>({
    identity,
    status: hasSource ? 'loading' : 'fallback',
  });
  const effectiveState =
    loadState.identity === identity
      ? loadState.status
      : hasSource
      ? 'loading'
      : 'fallback';

  const refs = useRef({
    hybridRef: unwrap(hybridRef),
    onLoadStart: unwrap(onLoadStart),
    onLoad: unwrap(onLoad),
    onDisplay: unwrap(onDisplay),
    onError: unwrap(onError),
    onLoadEnd: unwrap(onLoadEnd),
  });
  refs.current = {
    hybridRef: unwrap(hybridRef),
    onLoadStart: unwrap(onLoadStart),
    onLoad: unwrap(onLoad),
    onDisplay: unwrap(onDisplay),
    onError: unwrap(onError),
    onLoadEnd: unwrap(onLoadEnd),
  };
  const nativeMethodsRef = useRef<OneKeyImageMethods | null>(null);
  const pendingOverlayReloadRef = useRef<string | null>(null);
  const previousRenderRef = useRef({ hasOverlay, identity });

  useEffect(() => {
    const previous = previousRenderRef.current;
    const identityChanged = previous.identity !== identity;
    if (hasOverlay && (!previous.hasOverlay || identityChanged)) {
      const status = hasSource ? 'loading' : 'fallback';
      setLoadState({ identity, status });
    }

    if (hasOverlay && !previous.hasOverlay && !identityChanged && hasSource) {
      pendingOverlayReloadRef.current = identity;
      const nativeMethods = nativeMethodsRef.current;
      if (nativeMethods != null) {
        pendingOverlayReloadRef.current = null;
        nativeMethods.reload();
      }
    } else if (!hasOverlay || identityChanged) {
      pendingOverlayReloadRef.current = null;
    }
    previousRenderRef.current = { hasOverlay, identity };
  }, [hasOverlay, hasSource, identity]);

  const needsHybridRef = hasOverlay || refs.current.hybridRef != null;
  const needsLoadStart = hasOverlay || refs.current.onLoadStart != null;
  const needsLoad = refs.current.onLoad != null;
  const needsDisplay = placeholder != null || refs.current.onDisplay != null;
  const needsError = hasOverlay || refs.current.onError != null;
  const needsLoadEnd = refs.current.onLoadEnd != null;

  const nativeCallbacks = useMemo(
    () => ({
      hybridRef: needsHybridRef
        ? callback((ref: OneKeyImageMethods) => {
            nativeMethodsRef.current = ref;
            if (pendingOverlayReloadRef.current === identity) {
              pendingOverlayReloadRef.current = null;
              ref.reload();
            }
            refs.current.hybridRef?.(ref);
          })
        : undefined,
      onLoadStart: needsLoadStart
        ? callback(() => {
            if (hasOverlay) {
              setLoadState({ identity, status: 'loading' });
            }
            refs.current.onLoadStart?.();
          })
        : undefined,
      onLoad: needsLoad
        ? callback(
            (
              width: number,
              height: number,
              cacheType: OneKeyImageNativeCacheType
            ) => {
              refs.current.onLoad?.({
                source: { url: normalized?.uri ?? '', width, height },
                cacheType: cacheType as OneKeyImageCacheType,
              });
            }
          )
        : undefined,
      onDisplay: needsDisplay
        ? callback(() => {
            if (placeholder != null) {
              setLoadState({ identity, status: 'loaded' });
            }
            refs.current.onDisplay?.();
          })
        : undefined,
      onError: needsError
        ? callback((message: string) => {
            if (hasOverlay) {
              setLoadState({ identity, status: 'fallback' });
            }
            refs.current.onError?.({ error: message });
          })
        : undefined,
      onLoadEnd: needsLoadEnd
        ? callback(() => refs.current.onLoadEnd?.())
        : undefined,
    }),
    [
      hasOverlay,
      identity,
      needsDisplay,
      needsError,
      needsHybridRef,
      needsLoad,
      needsLoadEnd,
      needsLoadStart,
      normalized?.uri,
      placeholder,
    ]
  );

  const native = createElement(NativeOneKeyImage, {
    ...viewProps,
    ...nativeCallbacks,
    style: hasOverlay ? StyleSheet.absoluteFill : style,
    sourceUri: normalized?.uri,
    sourceHeadersJson: normalized?.headers
      ? JSON.stringify(normalized.headers)
      : undefined,
    variant,
    contentFit,
    cachePolicy: resolvedCachePolicy,
    autoplay: autoplay ?? Platform.OS !== 'android',
    recyclingKey,
    optimizeTos,
    overscan,
    loadingStrategy:
      placeholder == null ? loadingStrategy : OneKeyImageLoadingStrategy.NONE,
  });

  if (!hasOverlay) return native;

  return (
    <View style={[style, styles.overlayContainer]} collapsable={false}>
      {native}
      {effectiveState === 'loading' && placeholder != null ? (
        <View pointerEvents="none" style={StyleSheet.absoluteFill}>
          {placeholder}
        </View>
      ) : null}
      {effectiveState === 'fallback' && fallback != null ? (
        <View pointerEvents="none" style={StyleSheet.absoluteFill}>
          {fallback}
        </View>
      ) : null}
    </View>
  );
}

export const OneKeyImageCache = {
  preload(sources: OneKeyImagePreloadInput[]) {
    return nativeCache.preload(
      sources
        .filter((source) => Boolean(source.uri))
        .map((source) => ({
          uri: source.uri!,
          headersJson: source.headers
            ? JSON.stringify(source.headers)
            : undefined,
          cachePolicy: source.cachePolicy ?? OneKeyImageCachePolicy.MEMORY_DISK,
          resizeWidth: source.resizeWidth,
          resizeHeight: source.resizeHeight,
          pixelRatio: source.pixelRatio,
          overscan: source.overscan,
          optimizeTos: source.optimizeTos,
        }))
    );
  },
  clearMemory: () => nativeCache.clearMemory(),
  clearDisk: () => nativeCache.clearDisk(),
  clearAll: () => nativeCache.clearAll(),
};

const styles = StyleSheet.create({
  overlayContainer: {
    overflow: 'hidden',
  },
});
