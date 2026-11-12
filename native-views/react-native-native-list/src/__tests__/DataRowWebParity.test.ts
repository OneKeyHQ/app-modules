import { JSDOM } from 'jsdom';

import type { DataRow } from '../models';
import { dataRowRenderer } from '../web/templates/DataRowRenderer';
import { applyTextStyleToSlot } from '../web/templates/RowText';
import type { RowPrimitives } from '../web/templates/RowVisual';

it('restores generic weighted columns after reusing a four-column token table', () => {
  const document = new JSDOM('<!doctype html><body></body>').window.document;
  const visual = document.createElement('span');
  const primitives: RowPrimitives = {
    background: '#ffffff',
    visual: (leading) => (leading ? visual : undefined),
    thumbnail: () => undefined,
    icon: () => undefined,
    iconAction: () => document.createElement('button'),
    accessory: (_key, accessory) => {
      const button = document.createElement('button');
      if (accessory.kind === 'button') {
        button.textContent = accessory.label;
        button.disabled = !!accessory.disabled;
      }
      return button;
    },
    textStyle: applyTextStyleToSlot,
    dispose: () => undefined,
  };
  const token: DataRow = {
    type: 'dataRow',
    key: 'token',
    leading: { kind: 'icon', name: 'wallet' },
    columns: [
      { key: 'asset', text: 'TOKEN', weight: 2 },
      { key: 'balance', text: '10', alignment: 'end', weight: 3 },
      { key: 'price', text: '$1', alignment: 'end', weight: 3 },
      { key: 'swapSpacer', text: '', weight: 2 },
    ],
    trailing: [
      { kind: 'button', label: 'Swap', actionKey: 'swap', disabled: true },
    ],
    style: { image: { width: 32, height: 32 }, leadingGap: 12 },
  };
  const body = dataRowRenderer.create(document, token, primitives);
  const cells = () =>
    body.querySelectorAll<HTMLElement>('.ok-native-list-data-cell');
  const trailing = () =>
    body.querySelector<HTMLElement>('.ok-native-list-accessories')!;
  expect(body.style.gridTemplateColumns).toBe('repeat(4, minmax(0, 1fr))');
  expect(body.style.gap).toBe('0');
  expect(visual.style.gridArea).toBe('1 / 1');
  expect(cells()[0]?.style.marginInlineStart).toBe('44px');
  expect(trailing().style.gridArea).toBe('1 / 4');
  expect(body.querySelector('button')?.disabled).toBe(true);

  const controlledRows: DataRow[] = [
    { ...token, index: 0 },
    { ...token, favorite: true },
    {
      ...token,
      checkbox: {
        kind: 'checkbox',
        state: 'unchecked',
        target: { scope: 'row' },
      },
    },
  ];
  for (const row of controlledRows) {
    dataRowRenderer.bind(body, token, primitives);
    dataRowRenderer.bind(body, row, primitives);
    expect(body.style.display).toBe('');
    expect(body.style.gridTemplateColumns).toBe('');
    expect(body.style.gap).toBe('');
    expect(visual.style.gridArea).toBe('');
    expect(cells()[0]?.style.marginInlineStart).toBe('');
    expect(trailing().style.gridArea).toBe('');
  }

  dataRowRenderer.bind(
    body,
    { ...token, columns: token.columns.slice(0, 3) },
    primitives
  );
  expect(body.style.display).toBe('');
  expect(body.style.gridTemplateColumns).toBe('');
  expect(body.style.gap).toBe('');
  expect(visual.style.gridArea).toBe('');
  expect(cells()[0]?.style.marginInlineStart).toBe('');
  expect(cells()[1]?.style.flexGrow).toBe('3');
  expect(trailing().style.gridArea).toBe('');
  expect(trailing().style.justifySelf).toBe('');
  expect(body.contains(visual)).toBe(true);

  dataRowRenderer.bind(
    body,
    { ...token, style: { image: { width: 40, height: 40 }, leadingGap: 8 } },
    primitives
  );
  expect(body.style.display).toBe('grid');
  expect(body.style.gap).toBe('0');
  expect(cells()[0]?.style.marginInlineStart).toBe('48px');
  expect(body.querySelector('button')?.disabled).toBe(true);

  dataRowRenderer.bind(body, { ...token, leading: undefined }, primitives);
  expect(body.contains(visual)).toBe(false);
  expect(body.style.gap).toBe('0');
  expect(cells()[0]?.style.marginInlineStart).toBe('');
});

it('retains segmented table values and the final action column across row reuse', () => {
  const document = new JSDOM('<!doctype html><body></body>').window.document;
  const onAction = jest.fn();
  const primitives: RowPrimitives = {
    background: '#ffffff',
    visual: () => undefined,
    thumbnail: () => undefined,
    icon: () => undefined,
    iconAction: () => document.createElement('button'),
    accessory: (key, accessory) => {
      const button = document.createElement('button');
      if (accessory.kind === 'button') {
        button.textContent = accessory.label;
        button.disabled = !!accessory.disabled;
        button.onclick = () => onAction(key, accessory.actionKey);
      }
      return button;
    },
    textStyle: applyTextStyleToSlot,
    dispose: () => undefined,
  };
  const row: DataRow = {
    type: 'dataRow',
    key: 'asset',
    columns: [
      { key: 'asset', text: 'TOKEN' },
      {
        key: 'price',
        text: '$0.0000012',
        secondaryText: '+0.0000023',
        textSegments: [
          { text: '$0.0' },
          { text: '5', style: 'subscript' },
          { text: '12' },
        ],
        secondaryTextSegments: [
          { text: '+0.0' },
          { text: '5', style: 'subscript' },
          { text: '23' },
        ],
      },
    ],
    trailing: [{ kind: 'button', label: 'Swap', actionKey: 'swap' }],
    style: { columns: { fontSize: 18 }, columnSecondary: { fontSize: 14 } },
  };
  const body = dataRowRenderer.create(document, row, primitives);
  expect(body.lastElementChild?.textContent).toBe('Swap');
  const subscript = body.querySelectorAll<HTMLElement>(
    '[data-nl-segment="subscript"]'
  );
  expect([...subscript].map((node) => node.style.fontSize)).toEqual([
    '11px',
    '9px',
  ]);
  body.querySelector('button')?.click();
  expect(onAction).toHaveBeenCalledWith('asset', 'swap');

  dataRowRenderer.bind(
    body,
    {
      ...row,
      trailing: [
        { kind: 'button', label: 'Swap', actionKey: 'swap', disabled: true },
      ],
    },
    primitives
  );
  expect(body.lastElementChild?.textContent).toBe('Swap');
  body.querySelector('button')?.click();
  expect(onAction).toHaveBeenCalledTimes(1);

  dataRowRenderer.bind(
    body,
    {
      ...row,
      columns: [
        { key: 'asset', text: 'OTHER' },
        { key: 'price', text: '$1.25' },
      ],
      trailing: undefined,
      style: undefined,
    },
    primitives
  );
  expect(body.querySelector('[data-nl-segment]')).toBeNull();
  expect(body.querySelector('button')).toBeNull();
  expect(body.textContent).toContain('$1.25');
});

it('keeps first-column help icons anchored and clears them on row reuse', () => {
  const document = new JSDOM('<!doctype html><body></body>').window.document;
  const primitives: RowPrimitives = {
    background: '#ffffff',
    visual: () => undefined,
    thumbnail: () => undefined,
    icon: () => undefined,
    iconAction: (name) => {
      const button = document.createElement('button');
      button.textContent = name;
      return button;
    },
    accessory: () => document.createElement('button'),
    textStyle: applyTextStyleToSlot,
    dispose: () => undefined,
  };
  const row: DataRow = {
    type: 'dataRow',
    key: 'eth',
    columns: [
      {
        key: 'asset',
        text: 'ETH',
        titleIcon: {
          name: 'GasSolid',
          tintColor: '#888888',
          hoverActionKey: 'gas-info',
          accessibilityLabel: 'Gas token help',
        },
        secondaryTitleIcon: {
          name: 'TicketOutline',
          tintColor: '#888888',
          hoverActionKey: 'receipt-info',
        },
      },
    ],
  };
  const body = dataRowRenderer.create(document, row, primitives);
  const icons = body.querySelectorAll<HTMLElement>(
    '.ok-native-list-identity-title-icon'
  );
  expect(Array.from(icons).map((icon) => icon.textContent)).toEqual([
    'GasSolid',
    'TicketOutline',
  ]);
  expect(icons[0]?.dataset.nativeListHoverAction).toBe('gas-info');
  expect(icons[0]?.dataset.nativeListIconHelp).toBe('true');
  expect(icons[0]?.dataset.nativeListAnchorSource).toBe('leadingAction');
  expect(icons[0]?.getAttribute('aria-label')).toBe('Gas token help');
  expect(icons[1]?.dataset.nativeListHoverAction).toBe('receipt-info');
  expect(icons[1]?.getAttribute('aria-hidden')).toBe('true');
  expect(body.querySelector('[data-nl-slot="columns"]')?.textContent).toBe(
    'ETH'
  );

  dataRowRenderer.bind(
    body,
    { ...row, columns: [{ key: 'asset', text: 'USDC' }] },
    primitives
  );
  expect(body.querySelector('.ok-native-list-identity-title-icon')).toBeNull();
  expect(body.querySelector('[data-nl-slot="columns"]')?.textContent).toBe(
    'USDC'
  );
});
