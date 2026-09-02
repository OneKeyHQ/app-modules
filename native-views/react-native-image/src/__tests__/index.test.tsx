import ReactTestRenderer, { act } from 'react-test-renderer';
import { Platform, StyleSheet } from 'react-native';

jest.mock('react-native-nitro-modules', () => {
  const cache = {
    preload: jest.fn(() => Promise.resolve(true)),
    clearMemory: jest.fn(() => Promise.resolve()),
    clearDisk: jest.fn(() => Promise.resolve()),
    clearAll: jest.fn(() => Promise.resolve()),
  };
  return {
    callback: (f: (...args: unknown[]) => void) => ({ f }),
    getHostComponent: () => 'NativeOneKeyImage',
    NitroModules: { createHybridObject: () => cache },
    __cache: cache,
  };
});

import {
  OneKeyImage,
  OneKeyImageCache,
  OneKeyImageCachePolicy,
  OneKeyImageCacheType,
  OneKeyImageContentFit,
  OneKeyImageLoadingStrategy,
  OneKeyImageVariant,
} from '../index';

const mockCache = jest.requireMock('react-native-nitro-modules').__cache as {
  preload: jest.Mock;
  clearMemory: jest.Mock;
};

describe('OneKeyImage wrapper', () => {
  it('normalizes a URI source and keeps RN overlays opt-in', async () => {
    let renderer: ReactTestRenderer.ReactTestRenderer;
    await act(() => {
      renderer = ReactTestRenderer.create(
        <OneKeyImage
          source={{
            uri: 'https://example.com/avatar.png',
            cachePolicy: OneKeyImageCachePolicy.DISK,
          }}
          variant={OneKeyImageVariant.AVATAR}
          style={{ width: 40, height: 40 }}
        />
      );
    });
    const native = renderer!.root.findByType('NativeOneKeyImage' as never);
    expect((renderer!.toJSON() as { type: string }).type).toBe(
      'NativeOneKeyImage'
    );
    expect(native.props.sourceUri).toBe('https://example.com/avatar.png');
    expect(native.props).not.toHaveProperty('sourceCacheKey');
    expect(native.props.cachePolicy).toBe(OneKeyImageCachePolicy.DISK);
    expect(native.props.loadingStrategy).toBe(
      OneKeyImageLoadingStrategy.STATIC
    );
    expect(native.props.autoplay).toBe(Platform.OS !== 'android');
    for (const eventProp of [
      'hybridRef',
      'onLoadStart',
      'onLoad',
      'onDisplay',
      'onError',
      'onLoadEnd',
    ]) {
      expect(native.props[eventProp]).toBeUndefined();
    }
    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' })
    ).toHaveLength(0);
  });

  it('forwards preload headers without a JS-owned cache key', async () => {
    const result = await OneKeyImageCache.preload([
      {
        uri: 'https://example.com/token.png',
        headers: { Authorization: 'Bearer test' },
        resizeWidth: 40,
        resizeHeight: 64,
        pixelRatio: 3,
        overscan: 1.1,
        optimizeTos: true,
      },
    ]);
    expect(result).toBe(true);
    expect(mockCache.preload).toHaveBeenCalledWith([
      expect.objectContaining({
        uri: 'https://example.com/token.png',
        headersJson: '{"Authorization":"Bearer test"}',
        resizeWidth: 40,
        resizeHeight: 64,
        pixelRatio: 3,
        overscan: 1.1,
        optimizeTos: true,
      }),
    ]);
    expect(mockCache.preload.mock.calls[0]?.[0]?.[0]).not.toHaveProperty(
      'cacheKey'
    );

    const clearResult = OneKeyImageCache.clearMemory();
    expect(clearResult).toBeInstanceOf(Promise);
    await expect(clearResult).resolves.toBeUndefined();
  });

  it.each([
    ['recycling key', { recyclingKey: 'cell-2' }],
    ['TOS optimization', { optimizeTos: false }],
    ['TOS overscan', { overscan: 1.2 }],
    ['content fit', { contentFit: OneKeyImageContentFit.CONTAIN }],
  ])('resets same-URL placeholder when %s changes', async (_, changedProps) => {
    const baseProps = {
      source: { uri: 'https://example.com/avatar.png' },
      placeholder: 'loading',
      recyclingKey: 'cell-1',
      optimizeTos: true,
      overscan: 1.1,
      contentFit: OneKeyImageContentFit.COVER,
      style: { width: 40, height: 40 },
    };
    let renderer: ReactTestRenderer.ReactTestRenderer;
    await act(() => {
      renderer = ReactTestRenderer.create(<OneKeyImage {...baseProps} />);
    });
    const firstNative = renderer!.root.findByType('NativeOneKeyImage' as never);
    const staleOnDisplay = firstNative.props.onDisplay.f as () => void;
    await act(() => {
      staleOnDisplay();
    });
    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' })
    ).toHaveLength(0);

    await act(() => {
      renderer!.update(<OneKeyImage {...baseProps} {...changedProps} />);
    });
    const updatedNative = renderer!.root.findByType(
      'NativeOneKeyImage' as never
    );
    expect(updatedNative.props.recyclingKey).toBe(
      'recyclingKey' in changedProps
        ? changedProps.recyclingKey
        : baseProps.recyclingKey
    );
    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' })
    ).not.toHaveLength(0);

    await act(() => {
      staleOnDisplay();
    });
    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' })
    ).not.toHaveLength(0);
  });

  it('does not let callbacks from an old source replace the new overlay state', async () => {
    let renderer: ReactTestRenderer.ReactTestRenderer;
    await act(() => {
      renderer = ReactTestRenderer.create(
        <OneKeyImage
          source={{ uri: 'https://example.com/first.png' }}
          placeholder="loading"
          style={{ width: 40, height: 40 }}
        />
      );
    });
    const oldNative = renderer!.root.findByType('NativeOneKeyImage' as never);
    const oldOnDisplay = oldNative.props.onDisplay.f as () => void;

    await act(() => {
      renderer!.update(
        <OneKeyImage
          source={{ uri: 'https://example.com/second.png' }}
          placeholder="loading"
          style={{ width: 40, height: 40 }}
        />
      );
      oldOnDisplay();
    });

    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' }).length
    ).toBeGreaterThan(0);
  });

  it('reloads when an overlay is added after an unobserved native load', async () => {
    let renderer: ReactTestRenderer.ReactTestRenderer;
    const source = { uri: 'https://example.com/avatar.png' };
    await act(() => {
      renderer = ReactTestRenderer.create(
        <OneKeyImage source={source} style={{ width: 40, height: 40 }} />
      );
    });

    await act(() => {
      renderer!.update(
        <OneKeyImage
          source={source}
          placeholder="loading"
          style={{ width: 40, height: 40 }}
        />
      );
    });
    const native = renderer!.root.findByType('NativeOneKeyImage' as never);
    expect(native.props.loadingStrategy).toBe(OneKeyImageLoadingStrategy.NONE);
    const reload = jest.fn();
    await act(() => {
      native.props.hybridRef.f({ reload, cancel: jest.fn() });
    });
    expect(reload).toHaveBeenCalledTimes(1);
    await act(() => {
      native.props.onDisplay.f();
    });
    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' })
    ).toHaveLength(0);
  });

  it('keeps placeholder visible until the decoded image is displayed', async () => {
    const onLoad = jest.fn();
    const onDisplay = jest.fn();
    let renderer: ReactTestRenderer.ReactTestRenderer;
    await act(() => {
      renderer = ReactTestRenderer.create(
        <OneKeyImage
          source={{ uri: 'https://example.com/avatar.png' }}
          placeholder="loading"
          onLoad={onLoad}
          onDisplay={onDisplay}
          style={{ width: 40, height: 40, borderRadius: 20 }}
        />
      );
    });
    const native = renderer!.root.findByType('NativeOneKeyImage' as never);

    await act(() => {
      native.props.onLoad.f(40, 42, OneKeyImageCacheType.MEMORY);
    });
    expect(onLoad).toHaveBeenCalledWith({
      source: {
        url: 'https://example.com/avatar.png',
        width: 40,
        height: 42,
      },
      cacheType: OneKeyImageCacheType.MEMORY,
    });
    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' })
    ).not.toHaveLength(0);

    await act(() => {
      native.props.onDisplay.f();
    });
    expect(onDisplay).toHaveBeenCalledTimes(1);
    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' })
    ).toHaveLength(0);

    const container = renderer!.root.findByProps({ collapsable: false });
    expect(StyleSheet.flatten(container.props.style)).toMatchObject({
      borderRadius: 20,
      overflow: 'hidden',
    });
  });

  it('uses the documented Android autoplay default', async () => {
    const originalOS = Platform.OS;
    Object.defineProperty(Platform, 'OS', {
      configurable: true,
      value: 'android',
    });

    try {
      let renderer: ReactTestRenderer.ReactTestRenderer;
      await act(() => {
        renderer = ReactTestRenderer.create(
          <OneKeyImage source={{ uri: 'https://example.com/animated.webp' }} />
        );
      });
      const native = renderer!.root.findByType('NativeOneKeyImage' as never);
      expect(native.props.autoplay).toBe(false);
    } finally {
      Object.defineProperty(Platform, 'OS', {
        configurable: true,
        value: originalOS,
      });
    }
  });

  it('uses fallback for both null sources and request failures', async () => {
    const onError = jest.fn();
    let renderer: ReactTestRenderer.ReactTestRenderer;
    await act(() => {
      renderer = ReactTestRenderer.create(
        <OneKeyImage
          fallback="unavailable-image"
          onError={onError}
          style={{ width: 40, height: 40 }}
        />
      );
    });
    expect(JSON.stringify(renderer!.toJSON())).toContain('unavailable-image');

    await act(() => {
      renderer!.update(
        <OneKeyImage
          source={{ uri: 'https://example.com/fails.png' }}
          placeholder="loading"
          fallback="unavailable-image"
          onError={onError}
          style={{ width: 40, height: 40 }}
        />
      );
    });
    const native = renderer!.root.findByType('NativeOneKeyImage' as never);
    expect(JSON.stringify(renderer!.toJSON())).toContain(
      '"children":["loading"]'
    );

    await act(() => {
      native.props.onError.f('failed');
    });
    expect(onError).toHaveBeenCalledWith({ error: 'failed' });
    expect(JSON.stringify(renderer!.toJSON())).toContain('unavailable-image');
    expect(JSON.stringify(renderer!.toJSON())).not.toContain(
      '"children":["loading"]'
    );
  });

  it('removes a loading placeholder when the request fails without custom fallback content', async () => {
    let renderer: ReactTestRenderer.ReactTestRenderer;
    await act(() => {
      renderer = ReactTestRenderer.create(
        <OneKeyImage
          source={{ uri: 'https://example.com/fails.png' }}
          placeholder="loading"
          style={{ width: 40, height: 40 }}
        />
      );
    });
    const native = renderer!.root.findByType('NativeOneKeyImage' as never);
    expect(JSON.stringify(renderer!.toJSON())).toContain('loading');

    await act(() => {
      native.props.onError.f('failed');
    });
    expect(
      renderer!.root.findAllByProps({ pointerEvents: 'none' })
    ).toHaveLength(0);
  });
});
