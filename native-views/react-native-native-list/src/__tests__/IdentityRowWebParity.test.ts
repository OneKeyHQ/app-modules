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
    dispose: () => undefined,
  } as unknown as RowPrimitives;
  return identityRowRenderer.create(document, row, primitives);
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
    const body = renderIdentity(row);
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

  it('allows an unbounded subtitle and measures its rendered height when opted in', () => {
    const row: IdentityRow = {
      type: 'identity',
      key: 'long-token',
      title: 'TOKEN',
      subtitle: 'A long token name that wraps across multiple lines',
      subtitleLines: 0,
      contentHeight: 'auto',
      leading: { kind: 'token', image: mainImage },
    };
    const body = renderIdentity(row);
    const subtitle = body.querySelector<HTMLElement>(
      '[data-nl-slot="subtitle"]'
    );
    expect(subtitle?.style.whiteSpace).toBe('pre-wrap');
    expect(subtitle?.style.getPropertyValue('-webkit-line-clamp')).toBe('');
    body.getBoundingClientRect = () => ({ height: 128 } as DOMRect);
    expect(identityRowRenderer.measureRendered(body, row)).toBe(128);
    expect(
      identityRowRenderer.measureRendered(body, {
        ...row,
        contentHeight: undefined,
      })
    ).toBeUndefined();
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
    const body = renderIdentity(row);
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
});
