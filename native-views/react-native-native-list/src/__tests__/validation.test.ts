import type {
  ActionRow,
  IdentityRow,
  MetricCardRow,
  NativeListSnapshot,
  NativeListTheme,
  RowModel,
  RowPatch,
} from '../models';
import {
  applyRowPatches,
  serializeSnapshot,
  validatePatches,
  validateSnapshot,
} from '../validation';

const row = (key: string): IdentityRow => ({
  type: 'identity',
  key,
  leading: { kind: 'icon', name: 'coin' },
  title: key,
  trailing: [
    { kind: 'value', text: '$1.00' },
    { kind: 'checkbox', state: 'unchecked', target: { scope: 'row' } },
  ],
});

const snapshot = (
  rows: readonly RowModel[] = [row('btc'), row('eth')]
): NativeListSnapshot => ({
  schemaVersion: 1,
  generation: 1,
  layout: { kind: 'linear' },
  rows,
  selection: { mode: 'multiple', selectedKeys: [], rowPressToggles: true },
});

describe('NativeList model validation', () => {
  it('accepts amount plus checkbox on identity rows', () => {
    expect(validateSnapshot(snapshot())).toBeDefined();
  });

  it('accepts only the supported identity presentations', () => {
    expect(
      validateSnapshot(
        snapshot([{ ...row('wallet'), presentation: 'walletSidebar' }])
      ).rows[0]
    ).toMatchObject({ presentation: 'walletSidebar' });
    expect(
      validateSnapshot(
        snapshot([{ ...row('account'), presentation: 'accountSelector' }])
      ).rows[0]
    ).toMatchObject({ presentation: 'accountSelector' });
    expect(
      validateSnapshot(
        snapshot([{ ...row('network'), presentation: 'networkSelector' }])
      ).rows[0]
    ).toMatchObject({ presentation: 'networkSelector' });
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...row('invalid-wallet'),
            presentation: 'unsupported',
          } as unknown as IdentityRow,
        ])
      )
    ).toThrow('presentation');
  });

  it('accepts the account-selector action presentation', () => {
    const action: ActionRow = {
      type: 'action',
      key: 'add-account',
      presentation: 'accountSelector',
      title: 'Add account',
      actionKey: 'account.add',
    };
    expect(validateSnapshot(snapshot([action])).rows[0]).toMatchObject({
      presentation: 'accountSelector',
    });
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...action,
            presentation: 'unsupported',
          } as unknown as ActionRow,
        ])
      )
    ).toThrow('presentation');
  });

  it('accepts only the network-selector section-header presentation', () => {
    const header = {
      type: 'sectionHeader',
      key: 'assets',
      sectionKey: 'assets',
      presentation: 'networkSelector',
      title: 'Networks with assets',
    } as const;
    expect(validateSnapshot(snapshot([header])).rows[0]).toMatchObject({
      presentation: 'networkSelector',
    });
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...header,
            presentation: 'unsupported',
          } as unknown as RowModel,
        ])
      )
    ).toThrow('presentation');
  });

  it('accepts the metric-card contract in grid snapshots', () => {
    const metric: MetricCardRow = {
      type: 'metricCard',
      key: 'balance',
      title: 'Portfolio Value',
      value: '$13.66',
      trend: 'Total P&L +$8.00',
      trendTone: 'positive',
      visual: { kind: 'icon', name: 'ChartTrendingUpOutline' },
    };
    expect(
      validateSnapshot({
        ...snapshot([metric]),
        layout: { kind: 'grid', gridColumns: 2 },
      }).rows[0]
    ).toEqual(metric);
  });

  it('accepts source-backed activity and performance metric cards', () => {
    const metrics: MetricCardRow[] = [
      {
        type: 'metricCard',
        key: 'activity',
        variant: 'activity',
        title: 'ACTIVITY',
        value: '',
        metrics: [
          { key: 'volume', label: 'Volume', value: '$5.13K' },
          {
            key: 'most-traded',
            label: 'Most Traded',
            value: 'BTC',
            visual: {
              kind: 'token',
              image: {
                uri: 'https://uni.onekey-asset.com/static/hyperliquid/BTC.png',
                width: 16,
                height: 16,
              },
              fallbackText: 'BTC',
              backgroundColor: '#FFFFFF',
            },
          },
          { key: 'fees', label: 'Fees Paid', value: '$1.50' },
          { key: 'deposits', label: 'Net Deposits', value: '$13.66' },
          { key: 'trades', label: 'Total Trades', value: '3' },
        ],
      },
      {
        type: 'metricCard',
        key: 'performance',
        variant: 'performance',
        title: 'PERFORMANCE',
        value: '',
        progress: 0.5,
        metrics: [
          { key: 'win-rate', label: 'Win Rate', value: '50.0%' },
          { key: 'profit-factor', label: 'Profit Factor', value: '4.00' },
          { key: 'avg-win', label: 'Avg Win', value: '$20.00' },
          { key: 'avg-loss', label: 'Avg Loss', value: '-$5.00' },
        ],
      },
    ];

    const serialized = serializeSnapshot(snapshot(metrics));
    const rows = JSON.parse(serialized).rows;
    expect(rows[0].metrics).toHaveLength(5);
    expect(rows[0].metrics[1]).toMatchObject({
      value: 'BTC',
      visual: {
        kind: 'token',
        image: {
          uri: 'https://uni.onekey-asset.com/static/hyperliquid/BTC.png',
          width: 16,
          height: 16,
        },
      },
    });
    expect(rows[1]).toMatchObject({ variant: 'performance', progress: 0.5 });
  });

  it('accepts the test-suite matrix of four layouts and ten row models', () => {
    const rows: RowModel[] = [
      row('identity'),
      {
        type: 'rail',
        key: 'rail',
        visual: { kind: 'token', fallbackText: 'BTC' },
        title: 'BTC',
        badge: { key: 'price', text: '80,922.00', tone: 'success' },
        draggable: true,
      },
      {
        type: 'activity',
        key: 'activity',
        leading: { kind: 'token', fallbackText: 'ETH' },
        title: 'Send',
        status: 'Pending',
        footerActions: [{ key: 'speed-up', label: 'Speed up' }],
      },
      {
        type: 'message',
        key: 'message',
        title: 'Sent',
        body: 'Wallet 2 / EVM #1 0x5618...b4b7 sent 0.012 USDG',
        bodyLines: 3,
        time: '1 day ago',
      },
      {
        type: 'dataRow',
        key: 'data',
        columns: [
          {
            key: 'asset',
            text: 'BTC',
            secondaryText: 'Bitcoin $4.35B',
            weight: 3,
          },
          {
            key: 'price',
            text: '80,922.00',
            secondaryText: '+4.45%',
            secondaryTone: 'positive',
            alignment: 'end',
          },
        ],
      },
      {
        type: 'mediaTile',
        key: 'media',
        variant: 'gallery',
        image: {
          uri: 'https://example.com/nft.png',
          width: 640,
          height: 640,
        },
        title: 'Lido Withdrawal NFT…',
        subtitle: 'Lido: stETH Withdrawal NFT',
      },
      {
        type: 'metricCard',
        key: 'metric',
        variant: 'performance',
        title: 'PERFORMANCE',
        value: '',
        progress: 0.5,
        metrics: [
          { key: 'win-rate', label: 'Win Rate', value: '50.0%' },
          { key: 'profit-factor', label: 'Profit Factor', value: '4.00' },
        ],
      },
      {
        type: 'sectionHeader',
        key: 'header',
        sectionKey: 'assets',
        title: 'Networks with assets',
      },
      {
        type: 'action',
        key: 'action',
        title: 'Custom token',
        actionKey: 'token.custom',
      },
      {
        type: 'system',
        key: 'system',
        variant: 'retry',
        message: 'Search failed for other networks',
        actionKey: 'search.retry',
      },
    ];

    expect(rows.map((item) => item.type)).toEqual([
      'identity',
      'rail',
      'activity',
      'message',
      'dataRow',
      'mediaTile',
      'metricCard',
      'sectionHeader',
      'action',
      'system',
    ]);

    const layouts: NativeListSnapshot['layout'][] = [
      { kind: 'linear' },
      { kind: 'sectioned', stickyHeaders: true },
      { kind: 'grid', gridColumns: 2 },
      { kind: 'table' },
    ];
    layouts.forEach((layout) => {
      expect(validateSnapshot({ ...snapshot(rows), layout }).rows).toHaveLength(
        10
      );
    });
  });

  it('keeps rowPressedBackground optional and serializes it when configured', () => {
    const legacyTheme: NativeListTheme = {
      background: '#F7F7F7',
      rowBackground: '#FFFFFF',
      rowSelectedBackground: '#EAF2FF',
      primaryText: '#111111',
      secondaryText: '#6B7280',
      separator: '#E5E7EB',
      accent: '#2F6BFF',
      positive: '#15803D',
      negative: '#DC2626',
    };
    expect(
      validateSnapshot({ ...snapshot(), theme: legacyTheme }).theme
        ?.rowPressedBackground
    ).toBeUndefined();

    const serialized = serializeSnapshot({
      ...snapshot(),
      theme: { ...legacyTheme, rowPressedBackground: '#FFD6A5' },
    });
    expect(JSON.parse(serialized).theme.rowPressedBackground).toBe('#FFD6A5');
  });

  it('preserves source layout padding and spacing semantics', () => {
    const serialized = serializeSnapshot({
      ...snapshot(),
      layout: {
        kind: 'linear',
        contentPadding: 20,
        contentPaddingHorizontal: 20,
        contentPaddingTop: 0,
        contentPaddingBottom: 20,
        itemSpacing: 16,
      },
    });
    expect(JSON.parse(serialized).layout).toMatchObject({
      contentPaddingHorizontal: 20,
      contentPaddingTop: 0,
      contentPaddingBottom: 20,
      itemSpacing: 16,
    });
    expect(() =>
      validateSnapshot({
        ...snapshot(),
        layout: { kind: 'linear', contentPaddingTop: -1 },
      })
    ).toThrow('contentPaddingTop');
  });

  it('accepts history headers and caps message bodies at three lines', () => {
    const history: RowModel = {
      type: 'sectionHeader',
      key: 'history-header',
      sectionKey: 'history-2026-09-03',
      variant: 'history',
      title: '09/03/2026',
    };
    expect(validateSnapshot(snapshot([history])).rows[0]).toEqual(history);
    expect(
      validatePatches([
        {
          type: 'sectionHeader',
          key: 'history-header',
          changes: { variant: 'history' },
        },
      ])
    ).toHaveLength(1);

    const invalidMessage = {
      type: 'message',
      key: 'message-four-lines',
      title: 'Sent',
      body: 'Body',
      bodyLines: 4,
      time: '1 day ago',
    } as unknown as RowModel;
    expect(() => validateSnapshot(snapshot([invalidMessage]))).toThrow(
      'bodyLines'
    );
  });

  it('preserves no-match and end as distinct system states', () => {
    const states: RowModel[] = [
      {
        type: 'system',
        key: 'no-match',
        variant: 'noMatch',
        message: 'No matching tokens found on Ethereum',
      },
      { type: 'system', key: 'end', variant: 'end' },
    ];
    expect(JSON.parse(serializeSnapshot(snapshot(states))).rows).toEqual(
      states
    );
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            type: 'system',
            key: 'unknown-system',
            variant: 'unknown',
          } as unknown as RowModel,
        ])
      )
    ).toThrow('variant');
  });

  it('accepts the fixed summary-header template and validates its action', () => {
    const summary: RowModel = {
      type: 'sectionHeader',
      key: 'network-summary',
      sectionKey: 'network-summary',
      variant: 'summary',
      title: '2 networks selected',
      value: 'Select all',
      valueActionKey: 'selection.all',
    };
    const serialized = serializeSnapshot(snapshot([summary]));
    expect(JSON.parse(serialized).rows[0]).toMatchObject({
      variant: 'summary',
      valueActionKey: 'selection.all',
    });

    expect(() =>
      validateSnapshot(
        snapshot([{ ...summary, variant: 'custom' } as unknown as RowModel])
      )
    ).toThrow('variant');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...summary,
            checkbox: { kind: 'checkbox', state: 'unchecked' },
          },
        ])
      )
    ).toThrow('summary headers');
  });

  it('accepts an indexed vertical sectioned snapshot', () => {
    const indexedRows: RowModel[] = [
      {
        type: 'sectionHeader',
        key: 'section-a',
        sectionKey: 'a',
        indexTitle: 'A',
        title: 'A',
      },
      row('account-a'),
      {
        type: 'sectionHeader',
        key: 'section-b',
        sectionKey: 'b',
        indexTitle: 'B',
        title: 'B',
      },
      row('account-b'),
    ];
    const indexed = validateSnapshot({
      ...snapshot(indexedRows),
      layout: { kind: 'sectioned', stickyHeaders: true },
      capabilities: {
        sectionIndex: { enabled: true, hapticsEnabled: false },
      },
    });

    expect(indexed.capabilities?.sectionIndex).toEqual({
      enabled: true,
      hapticsEnabled: false,
    });
  });

  it('rejects invalid section-index configuration and titles', () => {
    const header: RowModel = {
      type: 'sectionHeader',
      key: 'section-a',
      sectionKey: 'a',
      indexTitle: 'A',
      title: 'A',
    };
    expect(() =>
      validateSnapshot({
        ...snapshot([header]),
        capabilities: { sectionIndex: { enabled: true } },
      })
    ).toThrow('requires a vertical sectioned layout');
    expect(() =>
      validateSnapshot({
        ...snapshot([header]),
        layout: { kind: 'sectioned', orientation: 'horizontal' },
        capabilities: { sectionIndex: { enabled: true } },
      })
    ).toThrow('requires a vertical sectioned layout');
    expect(() =>
      validateSnapshot({
        ...snapshot([
          header,
          {
            ...header,
            key: 'section-a-duplicate',
            sectionKey: 'a-duplicate',
            indexTitle: 'A\u030A',
          },
          {
            ...header,
            key: 'section-a-ring',
            sectionKey: 'a-ring',
            indexTitle: '\u00C5',
          },
        ]),
        layout: { kind: 'sectioned' },
      })
    ).toThrow('duplicate index title');
    expect(() =>
      validateSnapshot({
        ...snapshot([{ ...header, indexTitle: ' A ' }]),
        layout: { kind: 'sectioned' },
      })
    ).toThrow('leading or trailing whitespace');
    expect(() =>
      validateSnapshot({
        ...snapshot([{ ...header, indexTitle: '123456789' }]),
        layout: { kind: 'sectioned' },
      })
    ).toThrow('8 Unicode code points');
  });

  it('preserves OneKey palette tokens and semantic trailing icons', () => {
    const themed = serializeSnapshot({
      ...snapshot([
        {
          ...row('eth'),
          trailing: [
            {
              kind: 'icon',
              name: 'MinusCircleOutline',
              tintColor: '#CE2C31',
              actionKey: 'token.remove',
            },
          ],
        },
      ]),
      theme: {
        background: '#FFFFFF',
        rowBackground: '#FFFFFF',
        rowSelectedBackground: '#F0F0F0',
        rowPressedBackground: '#E8E8E8',
        subduedBackground: '#F9F9F9',
        strongBackground: '#F0F0F0',
        primaryText: '#202020',
        secondaryText: '#646464',
        disabledText: '#8D8D8D',
        icon: '#646464',
        iconSubdued: '#8D8D8D',
        separator: '#E0E0E0',
        accent: '#108303',
        positive: '#218358',
        negative: '#CE2C31',
        criticalBackground: '#E5484D',
        inverseBackground: '#202020',
        inverseText: '#FCFCFC',
      },
    });
    const parsed = JSON.parse(themed);
    expect(parsed.theme.strongBackground).toBe('#F0F0F0');
    expect(parsed.rows[0].trailing[0]).toEqual(
      expect.objectContaining({
        kind: 'icon',
        name: 'MinusCircleOutline',
        actionKey: 'token.remove',
      })
    );
  });

  it('rejects duplicate stable keys', () => {
    expect(() => validateSnapshot(snapshot([row('btc'), row('btc')]))).toThrow(
      'duplicate key'
    );
  });

  it('rejects accessory and badge counts above their hard caps', () => {
    const invalid = row('btc') as unknown as {
      trailing: unknown[];
      badges: unknown[];
    };
    invalid.trailing = [
      { kind: 'value', text: '1' },
      { kind: 'value', text: '2' },
      { kind: 'value', text: '3' },
    ];
    invalid.badges = [
      { key: '1', text: '1' },
      { key: '2', text: '2' },
      { key: '3', text: '3' },
    ];
    expect(() =>
      validateSnapshot(snapshot([invalid as unknown as IdentityRow]))
    ).toThrow('supports at most');
  });

  it('requires contiguous group positions', () => {
    const grouped: IdentityRow[] = [
      { ...row('a'), groupId: 'g', groupPosition: 'first' },
      { ...row('b'), groupId: 'g', groupPosition: 'last' },
      row('break'),
      { ...row('c'), groupId: 'g', groupPosition: 'single' },
    ];
    expect(() => validateSnapshot(snapshot(grouped))).toThrow(
      'must be contiguous'
    );
  });

  it('validates OneKeyImage descriptors and header values', () => {
    const invalidUri: IdentityRow = {
      ...row('bad-uri'),
      leading: {
        kind: 'image',
        image: { uri: '   ', width: 40, height: 40 },
      },
    };
    expect(() => validateSnapshot(snapshot([invalidUri]))).toThrow('uri');

    const invalidOverscan: IdentityRow = {
      ...row('bad-overscan'),
      leading: {
        kind: 'image',
        image: {
          uri: 'https://example.com/image.png',
          width: 40,
          height: 40,
          overscan: 5,
        },
      },
    };
    expect(() => validateSnapshot(snapshot([invalidOverscan]))).toThrow(
      'overscan'
    );

    const invalidHeader = {
      ...row('bad-header'),
      leading: {
        kind: 'image',
        image: {
          uri: 'https://example.com/image.png',
          width: 40,
          height: 40,
          headers: { Authorization: 123 },
        },
      },
    } as unknown as IdentityRow;
    expect(() => validateSnapshot(snapshot([invalidHeader]))).toThrow(
      'headers'
    );
  });

  it('preserves image overlays and semantic value-pair tones', () => {
    const tokenWithNetwork: IdentityRow = {
      ...row('token-with-network'),
      leading: {
        kind: 'token',
        image: {
          uri: 'https://example.com/token.png',
          width: 40,
          height: 40,
        },
        networkImage: {
          uri: 'https://example.com/network.png',
          width: 16,
          height: 16,
        },
        cornerIcon: {
          name: 'ErrorSolid',
          tintColor: '#CE2C31',
          backgroundColor: '#FFFFFF',
        },
      },
      trailing: [
        {
          kind: 'valuePair',
          primary: '$902,617.17',
          secondary: '+4.32%',
          secondaryTone: 'positive',
        },
        {
          kind: 'menu',
          actionKey: 'token.menu',
        },
      ],
    };
    const media: RowModel = {
      type: 'mediaTile',
      key: 'critter-ywin',
      variant: 'gallery',
      image: { uri: 'https://example.com/nft.png', width: 160, height: 160 },
      networkImage: {
        uri: 'https://example.com/solana.png',
        width: 14,
        height: 14,
      },
      title: 'Critter Ywin',
      subtitle: '-',
    };
    const serialized = serializeSnapshot(snapshot([tokenWithNetwork, media]));
    const parsed = JSON.parse(serialized);
    expect(parsed.rows[0].leading.networkImage).toMatchObject({
      uri: 'https://example.com/network.png',
      width: 16,
      height: 16,
    });
    expect(parsed.rows[0].leading.cornerIcon).toEqual({
      name: 'ErrorSolid',
      tintColor: '#CE2C31',
      backgroundColor: '#FFFFFF',
    });
    expect(parsed.rows[0].trailing[0].secondaryTone).toBe('positive');
    expect(parsed.rows[0].trailing[1]).toEqual({
      kind: 'menu',
      actionKey: 'token.menu',
    });
    expect(parsed.rows[1].networkImage.uri).toBe(
      'https://example.com/solana.png'
    );
  });

  it('accepts reserved and error media image states without invented images', () => {
    const mediaRows: RowModel[] = [
      {
        type: 'mediaTile',
        key: 'reserved-nft',
        variant: 'gallery',
        imageState: 'empty',
        title: 'Lido Withdrawal NFT...',
      },
      {
        type: 'mediaTile',
        key: 'failed-nft',
        variant: 'gallery',
        imageState: 'error',
        title: '-',
      },
    ];
    expect(JSON.parse(serializeSnapshot(snapshot(mediaRows))).rows).toEqual(
      mediaRows
    );
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            type: 'mediaTile',
            key: 'missing-image',
            variant: 'gallery',
            title: 'Missing image',
          } as RowModel,
        ])
      )
    ).toThrow('image');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            type: 'mediaTile',
            key: 'invalid-image-state',
            variant: 'gallery',
            imageState: 'loading',
            title: 'Invalid image state',
          } as unknown as RowModel,
        ])
      )
    ).toThrow('imageState');
  });

  it('rejects unsupported value-pair tones', () => {
    const invalid = {
      ...row('invalid-tone'),
      trailing: [
        {
          kind: 'valuePair',
          primary: '$3,836.97',
          secondary: '+4.32%',
          secondaryTone: 'gain',
        },
      ],
    } as unknown as IdentityRow;
    expect(() => validateSnapshot(snapshot([invalid]))).toThrow(
      'secondaryTone'
    );
  });

  it('rejects selection of disabled and structural rows', () => {
    const disabled = { ...row('disabled'), disabled: true };
    expect(() =>
      validateSnapshot({
        ...snapshot([disabled]),
        selection: { mode: 'single', selectedKeys: ['disabled'] },
      })
    ).toThrow('not selectable');
  });

  it('validates empty-state and fixed-footer descriptors', () => {
    const invalid = {
      type: 'system',
      key: 'space',
      variant: 'spacer',
      height: 513,
    } as const;
    expect(() =>
      validateSnapshot({ ...snapshot([]), emptyState: invalid })
    ).toThrow('emptyState.height');
    expect(() =>
      validateSnapshot({ ...snapshot(), fixedFooter: invalid })
    ).toThrow('fixedFooter.height');
  });
});

describe('NativeList patches', () => {
  it('applies a keyed batch without changing row order or untouched identity', () => {
    const initial = snapshot();
    const patches: RowPatch[] = [
      {
        type: 'identity',
        key: 'eth',
        changes: { subtitle: 'Updated', revision: 2 },
      },
    ];
    const next = applyRowPatches(initial, patches);
    expect(next.rows.map((item) => item.key)).toEqual(['btc', 'eth']);
    expect(next.rows[0]).toBe(initial.rows[0]);
    expect(next.rows[1]).toMatchObject({
      key: 'eth',
      subtitle: 'Updated',
      revision: 2,
    });
  });

  it('rejects duplicate patch keys and type mismatches', () => {
    const patch: RowPatch = {
      type: 'identity',
      key: 'btc',
      changes: { title: 'BTC' },
    };
    expect(() => validatePatches([patch, patch])).toThrow('duplicate key');
    const mismatch = {
      type: 'rail',
      key: 'btc',
      changes: { title: 'BTC' },
    } as RowPatch;
    expect(() => applyRowPatches(snapshot(), [mismatch])).toThrow('not rail');
  });

  it('rejects invalid image descriptors before sending a patch', () => {
    const patch = {
      type: 'identity',
      key: 'btc',
      changes: {
        leading: {
          kind: 'image',
          image: {
            uri: 'https://example.com/image.png',
            width: 0,
            height: 40,
          },
        },
      },
    } as RowPatch;
    expect(() => validatePatches([patch])).toThrow('width and height');
  });

  it('validates and applies metric-card patches', () => {
    const metric: MetricCardRow = {
      type: 'metricCard',
      key: 'balance',
      title: 'Total balance',
      value: '$100',
    };
    const initial = snapshot([metric]);
    const patch: RowPatch = {
      type: 'metricCard',
      key: 'balance',
      changes: { value: '$120', trend: '+20%', trendTone: 'positive' },
    };
    expect(applyRowPatches(initial, [patch]).rows[0]).toMatchObject({
      value: '$120',
      trend: '+20%',
      trendTone: 'positive',
    });
  });

  it('validates message body-line patches against the source cap', () => {
    const initial = snapshot([
      {
        type: 'message',
        key: 'notification',
        title: 'Sent',
        body: 'Wallet 2 / EVM #1 sent 0.012 USDG',
        time: '1 day ago',
      },
    ]);
    const patch: RowPatch = {
      type: 'message',
      key: 'notification',
      changes: { bodyLines: 3 },
    };
    expect(applyRowPatches(initial, [patch]).rows[0]).toMatchObject({
      bodyLines: 3,
    });
    expect(() =>
      validatePatches([
        {
          type: 'message',
          key: 'notification',
          changes: { bodyLines: 4 },
        } as unknown as RowPatch,
      ])
    ).toThrow('bodyLines');
  });
});
