import { JSDOM } from 'jsdom';

import type { IdentityRow } from '../models';
import { identityRowRenderer } from '../web/templates/IdentityRowRenderer';
import type { RowPrimitives } from '../web/templates/RowVisual';

const mainImage = {
  uri: 'https://example.com/usdc.png',
  width: 40,
  height: 40,
} as const;
const networkImage = {
  uri: 'https://example.com/ethereum.png',
  width: 16,
  height: 16,
} as const;

function renderIdentity(row: IdentityRow) {
  const document = new JSDOM('<!doctype html><body></body>').window.document;
  const primitives = {
    visual: () => {
      const frame = document.createElement('span');
      frame.className = 'ok-native-list-visual';
      const main = document.createElement('img');
      main.className = 'ok-native-list-visual-main';
      const corner = document.createElement('img');
      corner.className = 'ok-native-list-visual-corner';
      frame.append(main, corner);
      return frame;
    },
    iconAction: () => document.createElement('span'),
    dispose: () => undefined,
  } as unknown as RowPrimitives;
  return {
    body: identityRowRenderer.create(document, row, primitives),
    primitives,
  };
}

describe('Web identity row visual and expanded text', () => {
  it('keeps a network corner at its own size when styling the main token image', () => {
    const row: IdentityRow = {
      type: 'identity',
      key: 'usdc',
      title: 'USDC',
      leading: { kind: 'token', image: mainImage, networkImage },
      style: { image: { width: 40, height: 40, shape: 'circle' } },
    };
    const { body } = renderIdentity(row);
    const main = body.querySelector<HTMLElement>('.ok-native-list-visual-main');
    const corner = body.querySelector<HTMLElement>(
      '.ok-native-list-visual-corner'
    );
    expect(main?.style.width).toBe('100%');
    expect(main?.style.height).toBe('100%');
    expect(corner?.style.width).toBe('');
    expect(corner?.style.height).toBe('');
    expect(corner?.style.borderRadius).toBe('');
  });

  it('wraps an unspaced subtitle and measures untransformed layout height when opted in', () => {
    const row: IdentityRow = {
      type: 'identity',
      key: 'long-token',
      title: 'TOKEN',
      subtitle: '0xc19f5C3b2471D36c7C164088297A674Afec0fD25',
      subtitleLines: 0,
      contentHeight: 'auto',
      leading: { kind: 'token', image: mainImage },
    };
    const { body } = renderIdentity(row);
    const subtitle = body.querySelector<HTMLElement>(
      '[data-nl-slot="subtitle"]'
    );
    expect(subtitle?.style.whiteSpace).toBe('pre-wrap');
    expect(subtitle?.style.overflowWrap).toBe('anywhere');
    expect(subtitle?.style.getPropertyValue('-webkit-line-clamp')).toBe('');
    expect(body.style.height).toBe('auto');
    let layoutHeight = 128;
    Object.defineProperty(body, 'offsetHeight', { get: () => layoutHeight });
    const visualBounds = jest
      .spyOn(body, 'getBoundingClientRect')
      .mockReturnValue({ height: 128 * 0.95 } as DOMRect);
    expect(identityRowRenderer.measureRendered(body, row)).toBe(128);
    visualBounds.mockReturnValue({ height: 128 * 1.004 } as DOMRect);
    expect(identityRowRenderer.measureRendered(body, row)).toBe(128);
    layoutHeight = 0;
    expect(identityRowRenderer.measureRendered(body, row)).toBeUndefined();
    layoutHeight = 128;
    expect(identityRowRenderer.measureRendered(body, row)).toBe(128);
    expect(visualBounds).not.toHaveBeenCalled();
    expect(
      identityRowRenderer.measureRendered(body, {
        ...row,
        contentHeight: undefined,
      })
    ).toBeUndefined();
  });

  it.each([
    { subtitleLines: undefined, style: undefined, wrap: '', whiteSpace: '' },
    { subtitleLines: 1, style: undefined, wrap: '', whiteSpace: 'nowrap' },
    {
      subtitleLines: 0,
      style: { subtitle: { lines: 1 } },
      wrap: '',
      whiteSpace: 'nowrap',
    },
    {
      subtitleLines: 2,
      style: undefined,
      wrap: 'anywhere',
      whiteSpace: 'pre-wrap',
    },
  ] as const)(
    'preserves subtitle line defaults and precedence: %j',
    (testCase) => {
      const { body } = renderIdentity({
        type: 'identity',
        key: 'subtitle-lines',
        title: 'Recipient',
        subtitle: '0xc19f5C3b2471D36c7C164088297A674Afec0fD25',
        leading: { kind: 'account' },
        subtitleLines: testCase.subtitleLines,
        style: testCase.style,
      });
      const subtitle = body.querySelector<HTMLElement>(
        '[data-nl-slot="subtitle"]'
      );
      expect(subtitle?.style.overflowWrap).toBe(testCase.wrap);
      expect(subtitle?.style.whiteSpace).toBe(testCase.whiteSpace);
      expect(body.style.height).toBe('100%');
    }
  );

  it('restores body sizing when auto-height rows are rebound with explicit or omitted height', () => {
    const row: IdentityRow = {
      type: 'identity',
      key: 'reused-recipient',
      title: 'Recipient',
      subtitle: '0xc19f5C3b2471D36c7C164088297A674Afec0fD25',
      subtitleLines: 0,
      contentHeight: 'auto',
      leading: { kind: 'account' },
    };
    const { body, primitives } = renderIdentity(row);
    Object.defineProperty(body, 'offsetHeight', { value: 128 });
    expect(body.style.height).toBe('auto');
    for (const fixed of [
      { ...row, height: 90 },
      { ...row, style: { container: { height: 80 } } },
      { ...row, height: 90, style: { container: { height: 80 } } },
      { ...row, contentHeight: undefined },
    ]) {
      identityRowRenderer.bind(body, fixed, primitives);
      expect(body.style.height).toBe('100%');
      expect(identityRowRenderer.measureRendered(body, fixed)).toBeUndefined();
      identityRowRenderer.bind(body, row, primitives);
      expect(body.style.height).toBe('auto');
      expect(identityRowRenderer.measureRendered(body, row)).toBe(128);
    }
    identityRowRenderer.bind(body, { ...row, subtitleLines: 1 }, primitives);
    expect(
      body.querySelector<HTMLElement>('[data-nl-slot="subtitle"]')?.style
        .overflowWrap
    ).toBe('');
    identityRowRenderer.bind(body, row, primitives);
    expect(
      body.querySelector<HTMLElement>('[data-nl-slot="subtitle"]')?.style
        .overflowWrap
    ).toBe('anywhere');
  });

  it('renders Recent metadata as independently truncated title text, not badges', () => {
    const row: IdentityRow = {
      type: 'identity',
      key: 'recent',
      title: 'Alice',
      leading: { kind: 'account', image: mainImage },
      titleSuffixes: [
        { text: 'Ethereum', tone: 'disabled', maxWidth: 96 },
        { text: '2 days ago', tone: 'disabled' },
      ],
    };
    const { body } = renderIdentity(row);
    const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]');
    const suffixes = body.querySelectorAll<HTMLElement>(
      '.ok-native-list-identity-title-suffix'
    );
    expect(title?.textContent).toBe('Alice');
    expect(title?.style.minWidth).toBe('min(80px, 100%)');
    expect(Array.from(suffixes).map((element) => element.textContent)).toEqual([
      'Ethereum',
      '2 days ago',
    ]);
    expect(suffixes[0]?.style.maxWidth).toBe('96px');
    expect(suffixes[0]?.style.fontSize).toBe('12px');
    expect(suffixes[0]?.style.lineHeight).toBe('16px');
    expect(suffixes[0]?.style.fontWeight).toBe('400');
    expect(suffixes[0]?.style.marginInlineStart).toBe('8px');
    expect(suffixes[0]?.style.color).toBe('var(--nl-disabled)');
    expect(body.querySelector('.ok-native-list-badge')).toBeNull();
  });

  it('keeps a single-line title with a help glyph centered on one flex row (OK-66402)', () => {
    const row: IdentityRow = {
      type: 'identity',
      key: 'eth',
      title: 'ETH',
      subtitle: '$2,289.00',
      leading: { kind: 'token', image: mainImage },
      titleIcon: { name: 'GasSolid', hoverActionKey: 'gas-info' },
      style: { title: { token: '$bodyLg', fontWeight: 'medium', lines: 1 } },
    };
    const { body } = renderIdentity(row);
    const line = body.querySelector<HTMLElement>('.ok-native-list-title')!;
    const text = body.querySelector<HTMLElement>(
      '.ok-native-list-identity-title-text'
    )!;
    const icon = body.querySelector<HTMLElement>(
      '.ok-native-list-identity-title-icon'
    )!;
    // The clamp lands on the text span; the line stays a centered flex row so
    // the 20px glyph is not baseline-aligned above a block-level title.
    expect(line.style.display).toBe('flex');
    expect(line.style.alignItems).toBe('center');
    expect(line.hasAttribute('data-nl-slot')).toBe(false);
    expect(text.dataset.nlSlot).toBe('title');
    expect(text.textContent).toBe('ETH');
    expect(text.style.whiteSpace).toBe('nowrap');
    expect(text.style.textOverflow).toBe('ellipsis');
    expect(text.style.fontWeight).toBe('500');
    expect(text.nextElementSibling).toBe(icon);
  });

  it('keeps the badge-carrying title slot inside the text span when a glyph is present', () => {
    const row: IdentityRow = {
      type: 'identity',
      key: 'usdc',
      title: 'USDC',
      leading: { kind: 'token', image: mainImage },
      titleIcon: { name: 'TicketOutline' },
      badges: [{ key: 'network', text: 'Ethereum' }],
      style: { title: { lines: 1 } },
    };
    const { body } = renderIdentity(row);
    const line = body.querySelector<HTMLElement>('.ok-native-list-title')!;
    const text = body.querySelector<HTMLElement>(
      '.ok-native-list-identity-title-text'
    )!;
    const slot = body.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
    expect(line.style.display).toBe('flex');
    expect(line.hasAttribute('data-nl-slot')).toBe(false);
    expect(text.contains(slot)).toBe(true);
    expect(slot.style.whiteSpace).toBe('nowrap');
    expect(line.querySelector('.ok-native-list-badges')).not.toBeNull();
  });
});
