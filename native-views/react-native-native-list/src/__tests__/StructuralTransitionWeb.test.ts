import type { NativeListSnapshot, RowModel } from '../models';
import { NativeListWebEngine } from '../web/NativeListWebEngine';

jest.mock('../web/NativeListWebAvatarCache', () => ({
  acquireNativeListAvatar: jest.fn(),
  canonicalNativeListAvatarUri: jest.fn(),
}));

const { JSDOM } = require('jsdom') as {
  JSDOM: new (html: string) => { window: Window & typeof globalThis };
};

function snapshot(keys: readonly string[], id?: string): NativeListSnapshot {
  const rows: readonly RowModel[] = keys.map((key) => ({
    type: 'dataRow',
    key,
    height: 60,
    columns: [
      { key: 'asset', text: key },
      { key: 'price', text: '$1' },
    ],
  }));
  return {
    schemaVersion: 1,
    generation: 1,
    layout: { kind: 'linear' },
    rows,
    selection: { mode: 'multiple', selectedKeys: [] },
    ...(id ? { structuralTransition: { id, durationMs: 220 } } : {}),
  };
}

describe('NativeList Web structural transition', () => {
  it('commits grid and horizontal source layouts without a structural frame', () => {
    for (const [source, target] of [
      [{ kind: 'grid', gridColumns: 2 }, { kind: 'linear' }],
      [{ kind: 'linear', orientation: 'horizontal' }, { kind: 'table' }],
    ] as const) {
      const { window } = new JSDOM('<!doctype html><body></body>');
      const frames = jest.fn(() => 1);
      window.requestAnimationFrame = frames;
      const host = window.document.createElement('div');
      window.document.body.appendChild(host);
      const old = { ...snapshot(['a', 'b']), layout: source };
      const engine = new NativeListWebEngine(host, old, {}, false);
      try {
        frames.mockClear();
        engine.applySnapshot({
          ...snapshot(['a', 'b', 'c'], 'change-layout'),
          generation: 2,
          layout: target,
        });
        expect(frames).not.toHaveBeenCalled();
        expect(engine.currentSnapshot().rows).toHaveLength(3);
      } finally {
        engine.destroy();
      }
    }
  });

  it('keeps viewport-near retiring tail rows at full height along a shared-owner collapse', async () => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    let reduceMotion = false;
    Object.defineProperty(window, 'matchMedia', {
      configurable: true,
      value: () => ({ matches: reduceMotion }),
    });
    const callbacks = new Map<number, FrameRequestCallback>();
    let nextFrame = 0;
    window.requestAnimationFrame = (callback) => {
      const id = ++nextFrame;
      callbacks.set(id, callback);
      return id;
    };
    window.cancelAnimationFrame = (id) => {
      if (typeof id === 'number') callbacks.delete(id);
    };
    const runFrame = (elapsedMs: number) => {
      const pending = [...callbacks.values()];
      callbacks.clear();
      pending.forEach((callback) =>
        callback(window.performance.now() + elapsedMs)
      );
    };
    const owner = window.document.createElement('div');
    const bounds = window.document.createElement('div');
    const host = window.document.createElement('div');
    const footer = window.document.createElement('div');
    footer.dataset.testid = 'native-list-footer-slot';
    bounds.append(host, footer);
    owner.append(bounds);
    window.document.body.append(owner);
    Object.defineProperty(owner, 'clientHeight', { value: 180 });
    Object.defineProperty(owner, 'clientWidth', { value: 320 });
    Object.defineProperty(owner, 'scrollHeight', {
      get: () =>
        Math.max(
          2200,
          Number.parseFloat(
            host.querySelector<HTMLElement>('.ok-native-list-content')?.style
              .height ?? '0'
          ) + 52
        ),
    });
    owner.getBoundingClientRect = () =>
      ({
        top: 0,
        bottom: 180,
        left: 0,
        right: 320,
        width: 320,
        height: 180,
      } as DOMRect);
    bounds.getBoundingClientRect = () =>
      ({
        top: -owner.scrollTop,
        bottom: owner.scrollHeight - owner.scrollTop,
        left: 0,
        right: 320,
        width: 320,
        height: owner.scrollHeight,
      } as DOMRect);
    footer.getBoundingClientRect = () => {
      const top =
        Number.parseFloat(
          host.querySelector<HTMLElement>('.ok-native-list-content')?.style
            .height ?? '0'
        ) - owner.scrollTop;
      return { top, bottom: top + 52, height: 52 } as DOMRect;
    };
    const keys = Array.from({ length: 50 }, (_, index) => `row-${index}`);
    const engine = new NativeListWebEngine(
      host,
      snapshot(keys),
      {},
      true,
      null,
      '{}',
      { scrollHost: owner, contentBounds: bounds }
    );
    const viewport = host.querySelector<HTMLElement>(
      '.ok-native-list-viewport'
    )!;
    viewport.getBoundingClientRect = () =>
      ({
        top: -owner.scrollTop,
        bottom: 3000 - owner.scrollTop,
        left: 0,
        right: 320,
        width: 320,
        height: 3000,
      } as DOMRect);
    const content = host.querySelector<HTMLElement>('.ok-native-list-content')!;
    try {
      owner.scrollTop = 2700;
      owner.dispatchEvent(new window.Event('scroll'));
      engine.applySnapshot({
        ...snapshot(keys.slice(0, 6), 'collapse'),
        structuralTransition: {
          id: 'collapse',
          durationMs: 220,
          anchorKey: 'row-5',
        },
      });
      expect(engine.currentSnapshot().rows).toHaveLength(6);
      const tail = content.querySelector<HTMLElement>(
        '.ok-native-list-structural-tail'
      );
      expect(tail?.getAttribute('inert')).toBe('');
      expect(tail?.getAttribute('aria-hidden')).toBe('true');
      expect(tail?.style.pointerEvents).toBe('none');
      expect(content.style.overflow).toBe('clip');
      expect(
        tail?.querySelectorAll('.ok-native-list-item').length
      ).toBeLessThan(20);

      runFrame(110);
      const midway = [
        ...(tail?.querySelectorAll<HTMLElement>('.ok-native-list-item') ?? []),
      ];
      expect(
        midway.some((element) => element.textContent?.includes('row-25'))
      ).toBe(true);
      expect(midway.every((element) => element.style.height === '60px')).toBe(
        true
      );
      expect(midway.length).toBeLessThan(20);
      expect(owner.scrollTop).toBeGreaterThan(300);
      expect(owner.scrollTop).toBeLessThan(2700);

      await new Promise((resolve) => window.setTimeout(resolve, 280));
      expect(footer.getBoundingClientRect().top).toBeGreaterThanOrEqual(0);
      expect(footer.getBoundingClientRect().bottom).toBeLessThanOrEqual(180);
      expect(
        content.querySelector('.ok-native-list-structural-tail')
      ).toBeNull();
      expect(content.style.overflow).toBe('');
      expect(engine.currentSnapshot().rows).toHaveLength(6);

      owner.scrollTop = 1500;
      engine.applySnapshot({
        ...snapshot(keys, 'expand'),
        structuralTransition: {
          id: 'expand',
          durationMs: 220,
          anchorKey: 'row-5',
        },
      });
      runFrame(110);
      expect(owner.scrollTop).toBe(1500);
      expect(engine.currentSnapshot().rows).toHaveLength(50);
      reduceMotion = true;
      runFrame(160);
      expect(owner.scrollTop).toBe(1500);
      expect(content.style.height).toBe(`${engine.currentContentHeight()}px`);

      reduceMotion = false;
      engine.applySnapshot({
        ...snapshot(keys.slice(0, 6), 'collapse-reduced'),
        structuralTransition: {
          id: 'collapse-reduced',
          durationMs: 220,
          anchorKey: 'row-5',
        },
      });
      reduceMotion = true;
      runFrame(20);
      expect(footer.getBoundingClientRect().top).toBeGreaterThanOrEqual(0);
      expect(footer.getBoundingClientRect().bottom).toBeLessThanOrEqual(180);
      expect(
        content.querySelector('.ok-native-list-structural-tail')
      ).toBeNull();
    } finally {
      engine.destroy();
    }
  });

  it('keeps a changing React footer on the same height timeline and caps an offscreen fallback', () => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    const callbacks = new Map<number, FrameRequestCallback>();
    let nextFrame = 0;
    window.requestAnimationFrame = (callback) => {
      const id = ++nextFrame;
      callbacks.set(id, callback);
      return id;
    };
    window.cancelAnimationFrame = (id) => {
      if (typeof id === 'number') callbacks.delete(id);
    };
    let resize: ResizeObserverCallback | undefined;
    Object.defineProperty(window, 'ResizeObserver', {
      configurable: true,
      value: class {
        constructor(callback: ResizeObserverCallback) {
          resize = callback;
        }
        observe() {}
        disconnect() {}
      },
    });
    const runNextFrame = (elapsedMs: number) => {
      const pending = [...callbacks.values()];
      expect(pending.length).toBeGreaterThan(0);
      callbacks.clear();
      pending.forEach((callback) =>
        callback(window.performance.now() + elapsedMs)
      );
    };
    const owner = window.document.createElement('div');
    const bounds = window.document.createElement('div');
    const host = window.document.createElement('div');
    const footer = window.document.createElement('div');
    footer.dataset.testid = 'native-list-footer-slot';
    let footerHeight = 244;
    footer.getBoundingClientRect = () => {
      const top =
        Number.parseFloat(
          host.querySelector<HTMLElement>('.ok-native-list-content')?.style
            .height ?? '0'
        ) - owner.scrollTop;
      return {
        top,
        bottom: top + footerHeight,
        height: footerHeight,
      } as DOMRect;
    };
    bounds.append(host, footer);
    owner.append(bounds);
    window.document.body.append(owner);
    Object.defineProperty(owner, 'clientHeight', { value: 180 });
    Object.defineProperty(owner, 'clientWidth', { value: 320 });
    owner.style.setProperty('--ok-collapsible-pager-sticky-inset', '52px');
    Object.defineProperty(owner, 'scrollHeight', {
      get: () =>
        Number.parseFloat(
          host.querySelector<HTMLElement>('.ok-native-list-content')?.style
            .height ?? '0'
        ) +
        footerHeight +
        500,
    });
    const keys = Array.from({ length: 55 }, (_, index) => `row-${index}`);
    const engine = new NativeListWebEngine(
      host,
      snapshot(keys),
      {},
      false,
      null,
      '{}',
      { scrollHost: owner, contentBounds: bounds }
    );
    const content = host.querySelector<HTMLElement>('.ok-native-list-content');
    const viewport = host.querySelector<HTMLElement>(
      '.ok-native-list-viewport'
    );
    viewport!.getBoundingClientRect = () =>
      ({
        top: -owner.scrollTop,
        bottom: 3_300 - owner.scrollTop,
        left: 0,
        right: 320,
        width: 320,
        height: 3_300,
      } as DOMRect);
    try {
      const initialHeight = Number.parseFloat(content!.style.height);
      owner.scrollTop = 3200;
      owner.dispatchEvent(new window.Event('scroll'));
      footerHeight = 80;
      engine.applySnapshot({
        ...snapshot(keys.slice(0, 6), 'collapse'),
        structuralTransition: {
          id: 'collapse',
          durationMs: 220,
          anchorKey: 'row-5',
        },
      });
      expect(Number.parseFloat(content!.style.height)).toBe(
        initialHeight + 164
      );
      runNextFrame(110);
      expect(Number.parseFloat(content!.style.height)).toBeLessThan(
        initialHeight + 164
      );
      const midwayHeight = Number.parseFloat(content!.style.height);
      footerHeight = 60;
      resize?.([], {} as ResizeObserver);
      expect(Number.parseFloat(content!.style.height)).toBeGreaterThan(
        midwayHeight
      );
      runNextFrame(220);
      expect(footer.getBoundingClientRect().top).toBeGreaterThanOrEqual(52);
      expect(footer.getBoundingClientRect().bottom).toBeLessThanOrEqual(180);
      const collapsedHeight = Number.parseFloat(content!.style.height);
      footerHeight = 244;
      engine.applySnapshot(snapshot(keys, 'expand'));
      expect(Number.parseFloat(content!.style.height)).toBeGreaterThanOrEqual(
        collapsedHeight
      );
      expect(Number.parseFloat(footer.style.marginBottom)).toBeLessThan(0);
      runNextFrame(110);
      expect(Number.parseFloat(content!.style.height)).toBeGreaterThan(
        collapsedHeight
      );
    } finally {
      engine.destroy();
    }
  });

  it('commits initial and reduced-motion updates without intermediate height', () => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    Object.defineProperty(window, 'matchMedia', {
      configurable: true,
      value: () => ({ matches: true }),
    });
    const owner = window.document.createElement('div');
    const host = window.document.createElement('div');
    owner.append(host);
    window.document.body.append(owner);
    const engine = new NativeListWebEngine(
      host,
      snapshot(['a', 'b', 'c', 'd'], 'initial'),
      {},
      false,
      null,
      '{}',
      { scrollHost: owner }
    );
    const content = host.querySelector<HTMLElement>('.ok-native-list-content');
    try {
      expect(content?.style.height).toBe(`${engine.currentContentHeight()}px`);
      engine.applySnapshot(snapshot(['a', 'b'], 'collapse'));
      expect(content?.style.height).toBe(`${engine.currentContentHeight()}px`);
    } finally {
      engine.destroy();
    }
  });

  it('preserves the rendered height across a row patch and ResizeObserver during collapse', async () => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    const callbacks = new Map<number, FrameRequestCallback>();
    let nextFrame = 0;
    window.requestAnimationFrame = (callback) => {
      const id = ++nextFrame;
      callbacks.set(id, callback);
      return id;
    };
    window.cancelAnimationFrame = (id) => {
      if (id !== null && id !== undefined) callbacks.delete(id);
    };
    const runNextFrame = (elapsedMs: number) => {
      const entry = callbacks.entries().next().value as
        | [number, FrameRequestCallback]
        | undefined;
      expect(entry).toBeDefined();
      if (!entry) return;
      callbacks.delete(entry[0]);
      entry[1](window.performance.now() + elapsedMs);
    };
    let resize: ResizeObserverCallback | undefined;
    Object.defineProperty(window, 'ResizeObserver', {
      configurable: true,
      value: class {
        constructor(callback: ResizeObserverCallback) {
          resize = callback;
        }
        observe() {}
        disconnect() {}
      },
    });
    const owner = window.document.createElement('div');
    const host = window.document.createElement('div');
    owner.append(host);
    window.document.body.append(owner);
    Object.defineProperty(owner, 'clientHeight', { value: 180 });
    Object.defineProperty(owner, 'clientWidth', { value: 320 });
    const engine = new NativeListWebEngine(
      host,
      snapshot(['a', 'b', 'c', 'd', 'e', 'f']),
      {},
      false,
      null,
      '{}',
      { scrollHost: owner }
    );
    const content = host.querySelector<HTMLElement>('.ok-native-list-content');
    expect(content).not.toBeNull();
    try {
      const initialHeight = content!.style.height;
      engine.applySnapshot(snapshot(['a', 'b'], 'collapse-1'));
      expect(content!.style.height).toBe(initialHeight);
      runNextFrame(110);
      const midwayHeight = content!.style.height;
      expect(Number.parseFloat(midwayHeight)).toBeLessThan(
        Number.parseFloat(initialHeight)
      );
      expect(Number.parseFloat(midwayHeight)).toBeGreaterThan(
        engine.currentContentHeight()
      );

      engine.applyPatches([
        {
          type: 'dataRow',
          key: 'a',
          changes: {
            columns: [
              { key: 'asset', text: 'a' },
              { key: 'price', text: '$2' },
            ],
          },
        },
      ]);
      expect(content!.style.height).toBe(midwayHeight);
      resize?.([], {} as ResizeObserver);
      expect(content!.style.height).toBe(midwayHeight);
      engine.applySnapshot(snapshot(['a', 'b'], 'collapse-1'));
      expect(content!.style.height).toBe(midwayHeight);
      owner.scrollTop = 48;
      owner.dispatchEvent(new window.Event('wheel'));
      expect(callbacks.size).toBe(0);
      expect(content!.style.height).toBe(`${engine.currentContentHeight()}px`);
      expect(owner.scrollTop).toBe(48);
      await new Promise((resolve) => window.setTimeout(resolve, 280));
      expect(owner.scrollTop).toBe(48);

      const reverseStart = content!.style.height;
      engine.applySnapshot(
        snapshot(['a', 'b', 'c', 'd', 'e', 'f'], 'expand-2')
      );
      expect(content!.style.height).toBe(reverseStart);
      runNextFrame(110);
      expect(Number.parseFloat(content!.style.height)).toBeGreaterThan(
        Number.parseFloat(reverseStart)
      );
      expect(Number.parseFloat(content!.style.height)).toBeLessThan(
        engine.currentContentHeight()
      );

      engine.applySnapshot(snapshot(['a', 'b', 'c']));
      expect(content!.style.height).toBe(`${engine.currentContentHeight()}px`);
    } finally {
      engine.destroy();
    }
  });
});
