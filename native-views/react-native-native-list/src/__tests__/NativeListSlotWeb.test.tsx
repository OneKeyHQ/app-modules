import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import TestRenderer, { act } from 'react-test-renderer';
import { View } from 'react-native';

import { NativeList } from '../NativeList.web';
import * as validation from '../validation';
import type { NativeListRef } from '../NativeList.types';
import type { NativeListSnapshot } from '../models';

jest.mock('react-native', () => ({
  View: require('react-native-web').View,
}));
jest.mock('../web/NativeListWebAvatarCache', () => ({
  acquireNativeListAvatar: jest.fn(),
  canonicalNativeListAvatarUri: jest.fn(),
}));

const snapshot: NativeListSnapshot = {
  schemaVersion: 1,
  generation: 1,
  layout: { kind: 'linear' },
  rows: [],
};

it('validates a snapshot once while preparing React empty-slot layout', () => {
  const validate = jest.spyOn(validation, 'validateSnapshot');
  try {
    renderToStaticMarkup(
      <NativeList snapshot={snapshot} listEmpty={<span>Empty</span>} />
    );
    expect(validate).toHaveBeenCalledTimes(1);
  } finally {
    validate.mockRestore();
  }
});

it.each([true, false])(
  'rejects an invalid empty state before React empty-slot normalization (development=%s)',
  (development) => {
    const environment = globalThis as typeof globalThis & { __DEV__: boolean };
    const previous = environment.__DEV__;
    environment.__DEV__ = development;
    const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
    const rejected: NativeListSnapshot = {
      ...snapshot,
      emptyState: {
        type: 'system',
        key: 'space',
        variant: 'spacer',
        height: -1,
      },
    };
    try {
      const markup = renderToStaticMarkup(
        <NativeList snapshot={rejected} listEmpty={<span>Empty</span>} />
      );
      expect(warning).toHaveBeenCalledWith(
        expect.stringContaining('emptyState.height')
      );
      expect(markup).toContain('Empty');
    } finally {
      warning.mockRestore();
      environment.__DEV__ = previous;
    }
  }
);

it('keeps the accepted rows when a new invalid input adds an empty slot', async () => {
  const ref = React.createRef<NativeListRef>();
  const accepted: NativeListSnapshot = {
    ...snapshot,
    rows: [
      {
        type: 'identity',
        key: 'wallet',
        title: 'Accepted',
        leading: { kind: 'icon', name: 'wallet' },
      },
    ],
  };
  const rejected: NativeListSnapshot = {
    ...snapshot,
    generation: 2,
    emptyState: { type: 'system', key: 'space', variant: 'spacer', height: -1 },
  };
  const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
  const failed = jest.fn();
  let renderer!: TestRenderer.ReactTestRenderer;
  try {
    await act(async () => {
      renderer = TestRenderer.create(
        <NativeList
          ref={ref}
          snapshot={accepted}
          onScrollToIndexFailed={failed}
        />
      );
    });
    await act(async () =>
      renderer.update(
        <NativeList
          ref={ref}
          snapshot={rejected}
          listEmpty={<span>Empty</span>}
          onScrollToIndexFailed={failed}
        />
      )
    );
    expect(warning).toHaveBeenCalledWith(
      expect.stringContaining('emptyState.height')
    );
    expect(renderer.root.findAllByType('span')).toHaveLength(0);
    act(() => ref.current?.scrollToIndex(0));
    expect(failed).not.toHaveBeenCalled();
  } finally {
    if (renderer) await act(async () => renderer.unmount());
    warning.mockRestore();
  }
});

it.each([true, false])(
  'keeps the accepted empty snapshot when invalid input adds an empty slot (development=%s)',
  async (development) => {
    const ref = React.createRef<NativeListRef>();
    const emptySlot = <span>Empty</span>;
    const environment = globalThis as typeof globalThis & { __DEV__: boolean };
    const previous = environment.__DEV__;
    environment.__DEV__ = development;
    const accepted: NativeListSnapshot = {
      ...snapshot,
      layout: { kind: 'linear', contentPaddingBottom: 24 },
      emptyState: {
        type: 'system',
        key: 'space',
        variant: 'spacer',
        height: 24,
      },
    };
    const rejected: NativeListSnapshot = {
      ...accepted,
      generation: 2,
      emptyState: {
        type: 'system',
        key: 'space',
        variant: 'spacer',
        height: -1,
      },
    };
    const imperative: NativeListSnapshot = {
      ...snapshot,
      generation: 3,
      rows: [
        {
          type: 'identity',
          key: 'wallet',
          title: 'Imperative',
          leading: { kind: 'icon', name: 'wallet' },
        },
      ],
    };
    const recovered: NativeListSnapshot = { ...accepted, generation: 4 };
    const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
    let renderer!: TestRenderer.ReactTestRenderer;
    try {
      await act(async () => {
        renderer = TestRenderer.create(
          <NativeList ref={ref} snapshot={accepted} />
        );
      });
      await act(async () =>
        renderer.update(
          <NativeList ref={ref} snapshot={rejected} listEmpty={emptySlot} />
        )
      );
      expect(warning).toHaveBeenCalledWith(
        expect.stringContaining('emptyState.height')
      );
      expect(renderer.root.findAllByType('span')).toHaveLength(1);
      expect(
        renderer.root.findAll((node) => node.props.style?.paddingBottom === 0)
      ).not.toHaveLength(0);
      await act(async () =>
        renderer.update(
          <NativeList ref={ref} snapshot={rejected} listEmpty={emptySlot} />
        )
      );
      expect(renderer.root.findAllByType('span')).toHaveLength(1);
      act(() => ref.current?.applySnapshot(imperative));
      expect(renderer.root.findAllByType('span')).toHaveLength(0);
      await act(async () =>
        renderer.update(
          <NativeList ref={ref} snapshot={recovered} listEmpty={emptySlot} />
        )
      );
      expect(renderer.root.findAllByType('span')).toHaveLength(1);
    } finally {
      if (renderer) await act(async () => renderer.unmount());
      warning.mockRestore();
      environment.__DEV__ = previous;
    }
  }
);

it('keeps an imperative patch when only the sidecar layout object changes', async () => {
  const ref = React.createRef<NativeListRef>();
  const input: NativeListSnapshot = {
    ...snapshot,
    rows: [
      {
        type: 'identity',
        key: 'wallet',
        title: 'A',
        leading: { kind: 'icon', name: 'wallet' },
        style: { title: { token: '$bodySm' } },
      },
    ],
  };
  const sidecar = <span>Sidecar</span>;
  const firstLayout = { width: 400, gap: 24, maxContentWidth: 1140 };
  const nextLayout = { ...firstLayout };
  const patch = jest.spyOn(validation, 'applyRowPatches');
  let renderer!: TestRenderer.ReactTestRenderer;
  try {
    await act(async () => {
      renderer = TestRenderer.create(
        <NativeList
          ref={ref}
          snapshot={input}
          listSidecar={sidecar}
          sidecarLayout={firstLayout}
        />
      );
    });
    act(() =>
      ref.current?.applyPatches([
        { type: 'identity', key: 'wallet', changes: { title: 'Patched' } },
      ])
    );
    await act(async () =>
      renderer.update(
        <NativeList
          ref={ref}
          snapshot={input}
          listSidecar={sidecar}
          sidecarLayout={nextLayout}
        />
      )
    );
    act(() =>
      ref.current?.applyPatches([
        { type: 'identity', key: 'wallet', changes: { subtitle: 'After' } },
      ])
    );
    expect(patch).toHaveBeenCalledTimes(2);
    expect(patch.mock.calls[1][0].rows[0]).toMatchObject({ title: 'Patched' });
  } finally {
    patch.mockRestore();
    if (renderer) await act(async () => renderer.unmount());
  }
});

it('rejects mutated raw layout on an empty-slot rerender and keeps the imperative rows', async () => {
  const ref = React.createRef<NativeListRef>();
  const rawLayout = { kind: 'linear' as const, contentPaddingBottom: 24 };
  const raw: NativeListSnapshot = { ...snapshot, layout: rawLayout };
  const imperative: NativeListSnapshot = {
    ...snapshot,
    generation: 2,
    rows: [
      {
        type: 'identity',
        key: 'wallet',
        title: 'Accepted',
        leading: { kind: 'icon', name: 'wallet' },
      },
    ],
  };
  const failed = jest.fn();
  const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
  let renderer!: TestRenderer.ReactTestRenderer;
  try {
    await act(async () => {
      renderer = TestRenderer.create(
        <NativeList ref={ref} snapshot={raw} onScrollToIndexFailed={failed} />
      );
    });
    act(() => ref.current?.applySnapshot(imperative));
    rawLayout.contentPaddingBottom = -1;
    await act(async () =>
      renderer.update(
        <NativeList
          ref={ref}
          snapshot={raw}
          listEmpty={<span>Empty slot</span>}
          onScrollToIndexFailed={failed}
        />
      )
    );
    expect(warning).toHaveBeenCalledWith(
      expect.stringContaining('snapshot.layout.contentPaddingBottom')
    );
    expect(renderer.root.findAllByType('span')).toHaveLength(0);
    act(() => ref.current?.scrollToIndex(0));
    expect(failed).not.toHaveBeenCalled();
  } finally {
    if (renderer) await act(async () => renderer.unmount());
    warning.mockRestore();
  }
});

it('marks the React footer slot in the rendered Web DOM', () => {
  const markup = renderToStaticMarkup(
    <NativeList snapshot={snapshot} listFooter={<span>Show more</span>} />
  );
  expect(markup).toContain('data-testid="native-list-footer-slot"');
  expect(markup).toContain('Show more');
});

it('keeps disclosure content and toggle in one ordered Web footer band', () => {
  const markup = renderToStaticMarkup(
    <NativeList
      snapshot={{
        ...snapshot,
        disclosure: { expanded: false, expandedTrailingInset: 20 },
      }}
      listFooter={<span>Web footer</span>}
      listExpandedContent={<button>Expanded content</button>}
      listStructureToggle={<button>Toggle</button>}
      onStructuralTransitionSettled={() => {}}
    />
  );
  expect(markup.match(/data-testid="native-list-footer-slot"/g)).toHaveLength(
    1
  );
  expect(markup.indexOf('Expanded content')).toBeLessThan(
    markup.indexOf('Toggle')
  );
  expect(markup.indexOf('Toggle')).toBeLessThan(
    markup.indexOf('native-list-trailing-inset-slot')
  );
  expect(markup.indexOf('native-list-trailing-inset-slot')).toBeLessThan(
    markup.indexOf('Web footer')
  );
  expect(markup).toContain('data-testid="native-list-expanded-content-slot"');
  expect(markup).toContain('aria-hidden="true"');
  expect(markup).toContain('display:none');
  expect(markup).toContain('Web footer');
  expect(markup).not.toContain('onStructuralTransitionSettled');
});

it('keeps one expanded subtree across collapse and follows accepted snapshots', async () => {
  const ref = React.createRef<NativeListRef>();
  const mount = jest.fn();
  const unmount = jest.fn();
  const Expanded = () => {
    React.useEffect(() => {
      mount();
      return unmount;
    }, []);
    return <span>Expanded content</span>;
  };
  const expandedContent = <Expanded />;
  const expanded: NativeListSnapshot = {
    ...snapshot,
    layout: { ...snapshot.layout, contentPaddingHorizontal: 8 },
    disclosure: { expanded: true, expandedTrailingInset: 20 },
  };
  const collapsed: NativeListSnapshot = {
    ...expanded,
    disclosure: { expanded: false, expandedTrailingInset: 20 },
  };
  const renderList = (next: NativeListSnapshot) => (
    <NativeList
      ref={ref}
      snapshot={next}
      listExpandedContent={expandedContent}
      listStructureToggle={<button>Toggle</button>}
    />
  );
  let renderer!: TestRenderer.ReactTestRenderer;
  await act(async () => {
    renderer = TestRenderer.create(renderList(expanded));
  });
  const content = () =>
    renderer.root.findByProps({
      testID: 'native-list-expanded-content-slot',
    });
  const inset = () =>
    renderer.root.findByProps({ testID: 'native-list-trailing-inset-slot' });
  const toggle = () =>
    renderer.root.findByProps({ testID: 'native-list-structure-toggle-slot' });
  const footerBand = () =>
    renderer.root.findByProps({ testID: 'native-list-footer-slot' });
  expect(mount).toHaveBeenCalledTimes(1);
  expect(footerBand().props.style.paddingHorizontal).toBe(8);
  expect(content().props['aria-hidden']).toBe(false);
  expect(inset().props.style.height).toBe(20);

  await act(async () => renderer.update(renderList(collapsed)));
  expect(mount).toHaveBeenCalledTimes(1);
  expect(unmount).not.toHaveBeenCalled();
  expect(content().props['aria-hidden']).toBe(true);
  expect(content().props.pointerEvents).toBe('none');
  expect(content().props.style.display).toBe('none');
  expect(inset().props.style.height).toBe(0);

  await act(async () => ref.current?.applySnapshot(expanded));
  expect(content().props['aria-hidden']).toBe(false);
  expect(inset().props.style.height).toBe(20);
  const rejected: NativeListSnapshot = {
    ...collapsed,
    disclosure: { expanded: false, expandedTrailingInset: -1 },
  };
  await act(async () => renderer.update(renderList(rejected)));
  expect(content().props['aria-hidden']).toBe(false);
  expect(inset().props.style.height).toBe(20);
  expect(mount).toHaveBeenCalledTimes(1);

  await act(async () => ref.current?.applySnapshot(snapshot));
  expect(content().props.style.display).toBe('none');
  expect(content().props['aria-hidden']).toBe(true);
  expect(toggle().props.style.display).toBe('none');
  expect(toggle().props['aria-hidden']).toBe(true);
  expect(toggle().props.pointerEvents).toBe('none');
  expect(mount).toHaveBeenCalledTimes(1);
  await act(async () => ref.current?.applySnapshot(expanded));
  expect(content().props['aria-hidden']).toBe(false);
  expect(toggle().props['aria-hidden']).toBe(false);
  await act(async () => renderer.unmount());
  expect(unmount).toHaveBeenCalledTimes(1);
});

it('hides supplied disclosure slots when the accepted snapshot has no disclosure', () => {
  const markup = renderToStaticMarkup(
    <NativeList
      snapshot={snapshot}
      listExpandedContent={<button>Expanded content</button>}
      listStructureToggle={<button>Toggle</button>}
    />
  );
  expect(markup.match(/aria-hidden="true"/g)).toHaveLength(2);
  expect(markup.match(/display:none/g)).toHaveLength(2);
  expect(markup).toContain('data-testid="native-list-footer-slot"');
});

it('uses the sole footer band for a toggle without a caller footer', () => {
  const markup = renderToStaticMarkup(
    <NativeList
      snapshot={{ ...snapshot, disclosure: { expanded: false } }}
      listStructureToggle={<button>Toggle</button>}
    />
  );
  expect(markup.match(/data-testid="native-list-footer-slot"/g)).toHaveLength(
    1
  );
  expect(markup).toContain('data-testid="native-list-structure-toggle-slot"');
  expect(markup).toContain('Toggle');
});

it('keeps the sidecar and left footer in one scroll content tree', () => {
  const markup = renderToStaticMarkup(
    <NativeList
      snapshot={snapshot}
      listFooter={<span>Left footer</span>}
      listSidecar={<span>Recent activity</span>}
      sidecarLayout={{ width: 400, gap: 24, maxContentWidth: 1140 }}
    />
  );
  expect(markup).toContain('Left footer');
  expect(markup).toContain('Recent activity');
  expect(markup.match(/Recent activity/g)).toHaveLength(1);
});

it('uses logical sidecar insets so an RTL host mirrors both columns', () => {
  const WebView = View as React.ComponentType<
    React.ComponentProps<typeof View> & { dir: 'rtl' }
  >;
  const markup = renderToStaticMarkup(
    <WebView dir="rtl">
      <NativeList
        snapshot={snapshot}
        listSidecar={<span>Recent activity</span>}
        sidecarLayout={{ width: 400, gap: 24, maxContentWidth: 1140 }}
      />
    </WebView>
  );
  expect(markup).toContain('margin-right:0px');
  expect(markup).toContain('left:0px;top:0px;width:400px');
});
