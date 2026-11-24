import { JSDOM } from 'jsdom';

import type { IdentityRow } from '../models';
import { identityRowRenderer } from '../web/templates/IdentityRowRenderer';
import type { RowPrimitives } from '../web/templates/RowVisual';

const segments: NonNullable<IdentityRow['titleSegments']> = [
  {
    text: 'Main wallet',
    tone: 'secondary',
    fontWeight: 'regular',
    shrink: true,
  },
  { text: ' / ', tone: 'secondary', fontWeight: 'regular' },
  {
    text: 'Account #1',
    tone: 'primary',
    fontWeight: 'medium',
    maxWidthRatio: 0.7,
  },
];

const recipient = (patch: Partial<IdentityRow> = {}): IdentityRow => ({
  type: 'identity',
  key: 'recipient',
  title: 'Main wallet / Account #1',
  leading: { kind: 'icon', name: 'WalletOutline' },
  subtitle: '0x1234...5678',
  ...patch,
});

function setup() {
  const document = new JSDOM('<!doctype html><body></body>').window.document;
  const primitives = {
    visual: () => undefined,
    // As the engine's factory: 24px host artwork for a name it knows, a text
    // glyph for one it does not, and an image role with a label.
    iconAction: (
      name: string,
      _key: unknown,
      _disabled: unknown,
      tint?: string,
      label?: string
    ) => {
      const icon = document.createElement('span');
      icon.dataset.icon = name;
      if (name === 'UnknownExampleGlyph') icon.textContent = 'U';
      else {
        const svg = document.createElementNS(
          'http://www.w3.org/2000/svg',
          'svg'
        );
        svg.setAttribute('width', '24');
        svg.setAttribute('height', '24');
        icon.appendChild(svg);
      }
      if (label) {
        icon.setAttribute('aria-label', label);
        icon.setAttribute('role', 'img');
      }
      if (tint) icon.style.color = tint;
      return icon;
    },
    dispose: () => undefined,
  } as unknown as RowPrimitives;
  return {
    render: (row: IdentityRow) =>
      identityRowRenderer.create(document, row, primitives),
    rebind: (body: HTMLElement, row: IdentityRow) =>
      identityRowRenderer.bind(body, row, primitives),
  };
}

const runsOf = (body: HTMLElement) =>
  Array.from(
    body.querySelectorAll<HTMLElement>('.ok-native-list-identity-title-run')
  );

describe('Web identity title segments', () => {
  it('draws the runs in place of the title and reads the title instead', () => {
    const body = setup().render(recipient({ titleSegments: segments }));
    const runs = runsOf(body);

    expect(runs.map((run) => run.textContent)).toEqual([
      'Main wallet',
      ' / ',
      'Account #1',
    ]);
    expect(runs.map((run) => run.style.flex)).toEqual([
      '0 1 auto',
      '0 0 auto',
      '0 0 auto',
    ]);
    expect(runs[0]?.style.minWidth).toBe('0');
    expect(runs[2]?.style.maxWidth).toBe('70%');
    expect(runs[0]?.style.color).toBe('var(--nl-secondary)');
    expect(runs[2]?.style.color).toBe('var(--nl-primary)');
    expect(runs[0]?.style.fontWeight).toBe('400');
    expect(runs[2]?.style.fontWeight).toBe('500');
    expect(runs.every((run) => run.style.whiteSpace === 'pre')).toBe(true);
    expect(
      body
        .querySelector('.ok-native-list-identity-title-runs')
        ?.getAttribute('aria-hidden')
    ).toBe('true');
    expect(
      body.querySelector('.ok-native-list-identity-title-spoken')?.textContent
    ).toBe('Main wallet / Account #1');
  });

  it('fills what the title line leaves, so a cap is a share of the line', () => {
    const body = setup().render(
      recipient({ titleSegments: segments, titleIcon: { name: 'GasSolid' } })
    );
    const runs = body.querySelector<HTMLElement>(
      '.ok-native-list-identity-title-runs'
    );
    const titleText = runs?.parentElement;

    expect(runs?.style.flex).toBe('1 1 auto');
    expect(titleText?.style.flex).toBe('1 1 auto');
    expect(titleText?.style.minWidth).toBe('0');
    // The title icon follows at the line's end.
    expect(titleText?.nextElementSibling?.className).toBe(
      'ok-native-list-identity-title-icon'
    );
  });

  it('leaves tone and weight to the title when a run omits them', () => {
    const body = setup().render(
      recipient({ titleSegments: [{ text: 'Alice' }] })
    );
    const [run] = runsOf(body);

    expect(run?.style.color).toBe('');
    expect(run?.style.fontWeight).toBe('');
    expect(run?.style.maxWidth).toBe('');
  });

  it('keeps the plain title on a highlighted row and in the wallet sidebar', () => {
    const { render } = setup();
    const highlighted = render(
      recipient({ titleSegments: segments, titleMatch: [{ start: 0, end: 4 }] })
    );
    const sidebar = render(
      recipient({ titleSegments: segments, presentation: 'walletSidebar' })
    );

    expect(runsOf(highlighted)).toHaveLength(0);
    expect(highlighted.querySelector('.ok-native-list-info')?.textContent).toBe(
      'Main'
    );
    expect(runsOf(sidebar)).toHaveLength(0);
  });

  it('ignores title lines for runs', () => {
    const body = setup().render(
      recipient({ titleSegments: segments, titleLines: 2 })
    );
    const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]');

    expect(title?.style.webkitLineClamp ?? '').toBe('');
    expect(runsOf(body)).toHaveLength(3);
  });

  it('applies the title style as what runs inherit, not over their own tone and weight', () => {
    const body = setup().render(
      recipient({
        titleSegments: [...segments, { text: ' (2)' }],
        style: {
          title: {
            color: '#123456',
            fontWeight: 'bold',
            fontSize: 18,
            lines: 2,
            offsetY: 3,
          },
        },
      })
    );
    const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]');
    const runs = runsOf(body);

    expect(title?.style.color).toBe('rgb(18, 52, 86)');
    expect(title?.style.fontWeight).toBe('700');
    expect(title?.style.fontSize).toBe('18px');
    expect(runs[0]?.style.color).toBe('var(--nl-secondary)');
    expect(runs[0]?.style.fontWeight).toBe('400');
    expect(runs[2]?.style.color).toBe('var(--nl-primary)');
    expect(runs[2]?.style.fontWeight).toBe('500');
    // A run without its own tone and weight takes the title's.
    expect(runs[3]?.style.color).toBe('');
    expect(runs[3]?.style.fontWeight).toBe('');
    // The optical offset moves the runs without touching the layout.
    expect(
      body.querySelector<HTMLElement>('.ok-native-list-identity-title-runs')
        ?.style.transform
    ).toBe('translateY(3px)');
    // Still one line of runs.
    expect(title?.style.display).toBe('flex');
    expect(title?.style.webkitLineClamp ?? '').toBe('');
    expect(runs.every((run) => run.style.whiteSpace === 'pre')).toBe(true);
  });

  it('folds a hard line break in a run into a space', () => {
    const body = setup().render(
      recipient({
        title: 'Main\nWallet',
        titleSegments: [{ text: 'Main\nWallet' }, { text: 'a\r\nb' }],
      })
    );

    expect(runsOf(body).map((run) => run.textContent)).toEqual([
      'Main Wallet',
      'a b',
    ]);
    // The spoken label is the title as given.
    expect(
      body.querySelector('.ok-native-list-identity-title-spoken')?.textContent
    ).toBe('Main\nWallet');
  });
});

describe('Web identity title leading icon', () => {
  it('puts the glyph before the title text with a 6px gap, hidden from assistive technology', () => {
    const body = setup().render(
      recipient({ titleLeadingIcon: { name: 'BookOpenOutline' } })
    );
    const titleLine = body.querySelector<HTMLElement>('.ok-native-list-title');
    const icon = titleLine?.firstElementChild as HTMLElement | null;

    expect(icon?.className).toBe('ok-native-list-identity-title-leading-icon');
    expect(icon?.dataset.icon).toBe('BookOpenOutline');
    expect(icon?.getAttribute('aria-hidden')).toBe('true');
    expect(icon?.style.marginInlineEnd).toBe('6px');
    expect(titleLine?.style.display).toBe('flex');
    // The text keeps its own slot, so title text styles skip the glyph.
    expect(
      titleLine?.querySelector('[data-nl-slot="title"]')?.textContent
    ).toBe('Main wallet / Account #1');
  });

  it('labels the glyph when asked and keeps 6px next to a title icon', () => {
    const body = setup().render(
      recipient({
        titleLeadingIcon: {
          name: 'BookOpenOutline',
          tintColor: '#8D8D8D',
          accessibilityLabel: 'Address book',
        },
        titleIcon: { name: 'GasSolid' },
      })
    );
    const icon = body.querySelector<HTMLElement>(
      '.ok-native-list-identity-title-leading-icon'
    );

    expect(icon?.getAttribute('aria-label')).toBe('Address book');
    expect(icon?.getAttribute('role')).toBe('img');
    expect(icon?.getAttribute('aria-hidden')).toBeNull();
    expect(icon?.style.color).toBe('rgb(141, 141, 141)');
    // The title line already spaces its items 4px apart.
    expect(icon?.style.marginInlineEnd).toBe('2px');
  });

  it('draws the glyph ahead of the runs', () => {
    const body = setup().render(
      recipient({
        titleSegments: segments,
        titleLeadingIcon: { name: 'BookOpenOutline' },
      })
    );
    const titleLine = body.querySelector<HTMLElement>('.ok-native-list-title');

    expect(titleLine?.firstElementChild?.className).toBe(
      'ok-native-list-identity-title-leading-icon'
    );
    expect(runsOf(body)).toHaveLength(3);
    // The text beside the glyph takes the rest of the line, so a cap stays a
    // share of the line.
    const titleText = titleLine?.querySelector<HTMLElement>(
      '.ok-native-list-identity-title-text'
    );
    expect(titleText?.style.flex).toBe('1 1 auto');
    expect(titleText?.style.minWidth).toBe('0');
  });

  it('draws host artwork at 16px and leaves the slot empty without it', () => {
    const { render } = setup();
    const known = render(
      recipient({ titleLeadingIcon: { name: 'BookOpenOutline' } })
    ).querySelector<HTMLElement>('.ok-native-list-identity-title-leading-icon');
    const unknown = render(
      recipient({ titleLeadingIcon: { name: 'UnknownExampleGlyph' } })
    ).querySelector<HTMLElement>('.ok-native-list-identity-title-leading-icon');

    expect(known?.querySelector('svg')?.getAttribute('width')).toBe('16');
    expect(known?.querySelector('svg')?.getAttribute('height')).toBe('16');
    expect(unknown).not.toBeNull();
    expect(unknown?.textContent).toBe('');
    expect(unknown?.childElementCount).toBe(0);
  });
});

describe('Web identity title runs on rebind', () => {
  it('drops the runs and the glyph when the next row has none', () => {
    const { render, rebind } = setup();
    const body = render(
      recipient({
        titleSegments: segments,
        titleLeadingIcon: { name: 'BookOpenOutline' },
      })
    );

    rebind(body, recipient({ key: 'plain', title: 'Plain' }));

    expect(runsOf(body)).toHaveLength(0);
    expect(
      body.querySelector('.ok-native-list-identity-title-leading-icon')
    ).toBeNull();
    expect(body.querySelector('.ok-native-list-title')?.textContent).toBe(
      'Plain'
    );
  });
});
