import { JSDOM } from 'jsdom';

import type { DataRow } from '../models';
import { dataRowRenderer } from '../web/templates/DataRowRenderer';
import { applyTextStyleToSlot } from '../web/templates/RowText';
import type { RowPrimitives } from '../web/templates/RowVisual';

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
