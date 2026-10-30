import type {
  ActionRow,
  ActivityRow,
  ButtonGroupRow,
  DataRow,
  IdentityRow,
  MarketRow,
  MetricCardRow,
  NativeListSnapshot,
  NativeListTheme,
  RowModel,
  RowPatch,
} from '../models';
import {
  applyOwnedNativePatches,
  applyRowPatches,
  captureNativeListInput,
  ownValidatedNativeSnapshot,
  planNativeListPatches,
  serializePatches,
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

const marketRow = (key = 'market-btc'): MarketRow => ({
  type: 'market',
  key,
  variant: 'token',
  leading: {
    kind: 'token',
    image: {
      uri: 'https://example.com/btc.png',
      width: 32,
      height: 32,
    },
    networkImage: {
      uri: 'https://example.com/bitcoin.png',
      width: 16,
      height: 16,
    },
  },
  title: 'BTC',
  leadingAction: {
    kind: 'icon',
    name: 'StarOutline',
    actionKey: 'market.favorite',
    accessibilityLabel: 'Add to favorites',
  },
  subtitle: '$1.23B',
  subtitlePrefix: {
    text: 'Bitcoin',
    gap: 4,
    maxWidth: 120,
    style: { fontSize: 12, lineHeight: 16, fontWeight: 'regular' },
  },
  price: '$64,230.00',
  change: { text: '+2.40%', tone: 'positive' },
  badges: [
    {
      key: 'community',
      iconName: 'verified',
      tone: 'success',
      actionKey: 'market.communityInfo',
      accessibilityLabel: 'Community recognized',
      style: {
        fontSize: 10,
        fontWeight: 'regular',
        lineHeight: 16,
        height: 16,
        horizontalPadding: 4,
      },
    },
  ],
  pressActionKey: 'market.open',
  pressInActionKey: 'market.prewarm',
  longPressActionKey: 'market.menu',
  diagnostics: { imageBindActionKey: 'market.imageBind' },
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

describe.each([true, false])(
  'identity title image protocol in development=%s',
  (development) => {
    const environment = globalThis as typeof globalThis & { __DEV__: boolean };
    const titleImage: NonNullable<IdentityRow['titleImage']> = {
      image: { uri: 'https://example.com/issuer.png', width: 14, height: 14 },
      accessibilityLabel: 'Issuer',
      hoverActionKey: 'issuer-info',
    };
    it('serializes and replaces title images without losing omitted patch data', () => {
      const original = environment.__DEV__;
      environment.__DEV__ = development;
      try {
        const initial = snapshot([{ ...row('stock'), titleImage }]);
        expect(
          JSON.parse(serializeSnapshot(initial)).rows[0].titleImage
        ).toEqual(titleImage);
        const unchanged = applyRowPatches(initial, [
          { type: 'identity', key: 'stock', changes: { title: 'New title' } },
        ]);
        expect(unchanged.rows[0]).toMatchObject({ titleImage });
        const replacement = {
          image: { ...titleImage.image, uri: 'https://example.com/next.png' },
        };
        const patched = applyRowPatches(initial, [
          {
            type: 'identity',
            key: 'stock',
            changes: { titleImage: replacement },
          },
        ]);
        expect(patched.rows[0]).toMatchObject({ titleImage: replacement });
        expect(
          (patched.rows[0] as IdentityRow).titleImage?.hoverActionKey
        ).toBeUndefined();
        expect(
          planNativeListPatches(initial, snapshot([row('stock')]))
        ).toBeUndefined();
      } finally {
        environment.__DEV__ = original;
      }
    });
    it('rejects malformed image descriptors in snapshots and patches', () => {
      const original = environment.__DEV__;
      environment.__DEV__ = development;
      try {
        const invalid = [
          null,
          {},
          { image: null },
          { ...titleImage, image: { ...titleImage.image, uri: '' } },
          { ...titleImage, image: { ...titleImage.image, width: 0 } },
          {
            ...titleImage,
            image: { ...titleImage.image, width: 0.5, height: 0.5 },
          },
          {
            ...titleImage,
            image: { ...titleImage.image, width: 41, height: 41 },
          },
          { ...titleImage, image: { ...titleImage.image, height: 15 } },
          { ...titleImage, hoverActionKey: '' },
          { ...titleImage, accessibilityLabel: 3 },
        ];
        for (const descriptor of invalid) {
          const value = descriptor as unknown as IdentityRow['titleImage'];
          expect(() =>
            validateSnapshot(snapshot([{ ...row('stock'), titleImage: value }]))
          ).toThrow();
          expect(() =>
            validatePatches([
              {
                type: 'identity',
                key: 'stock',
                changes: { titleImage: value },
              },
            ])
          ).toThrow();
        }
      } finally {
        environment.__DEV__ = original;
      }
    });
  }
);

describe('public NativeList patch planner', () => {
  it('shares an empty batch for equal JSON values regardless of property order or omitted undefined', () => {
    const oldRow = row('btc');
    const previous = snapshot([oldRow]);
    const equal = snapshot([
      {
        ...oldRow,
        leading: {
          name: 'coin',
          tintColor: undefined,
          kind: 'icon',
        },
      },
    ]);
    expect(planNativeListPatches(previous, equal)).toBe(
      planNativeListPatches(previous, previous)
    );
    expect(planNativeListPatches(previous, equal)).toEqual([]);
    expect(planNativeListPatches(previous, { ...previous })).toBe(
      planNativeListPatches(previous, previous)
    );
    expect(
      planNativeListPatches(previous, { ...previous, generation: 2 })
    ).toBeUndefined();
  });

  it('plans only present identity and non-asset dataRow value changes', () => {
    const identity = { ...row('btc'), subtitle: '$1', trailing: undefined };
    const data: DataRow = {
      type: 'dataRow',
      key: 'data',
      columns: [
        { key: 'asset', text: 'BTC' },
        { key: 'balance', text: '$1' },
      ],
    };
    const nextIdentity = {
      ...identity,
      subtitle: '$2',
      trailing: [{ kind: 'value' as const, text: '$2' }],
    };
    const nextData: DataRow = {
      ...data,
      columns: [data.columns[0], { key: 'balance', text: '$2' }],
    };
    const previous = snapshot([identity, data]);
    const next = snapshot([nextIdentity, nextData]);
    expect(planNativeListPatches(previous, next)).toBeUndefined();
    const withExistingTrailing = snapshot([
      { ...identity, trailing: [{ kind: 'value', text: '$1' }] },
      data,
    ]);
    expect(planNativeListPatches(withExistingTrailing, next)).toEqual([
      {
        type: 'identity',
        key: 'btc',
        changes: {
          subtitle: '$2',
          subtitleSegments: undefined,
          trailing: nextIdentity.trailing,
        },
      },
      {
        type: 'dataRow',
        key: 'data',
        changes: { columns: nextData.columns, trailing: undefined },
      },
    ]);
    expect(previous.rows[0]).toBe(identity);
    expect(next.rows[0]).toBe(nextIdentity);
  });

  it('requires a snapshot for top-level, row identity, asset and optional-field changes', () => {
    const identity = { ...row('btc'), subtitle: '$1' };
    const data: DataRow = {
      type: 'dataRow',
      key: 'data',
      columns: [
        { key: 'asset', text: 'BTC' },
        { key: 'balance', text: '$1' },
      ],
    };
    const previous = snapshot([identity, data]);
    expect(
      planNativeListPatches(previous, { ...previous, generation: 2 })
    ).toBeUndefined();
    expect(
      planNativeListPatches(previous, {
        ...previous,
        selection: { mode: 'multiple', selectedKeys: ['btc'] },
      })
    ).toBeUndefined();
    expect(
      planNativeListPatches(previous, {
        ...previous,
        listStyle: { groupCornerRadius: 8 },
      })
    ).toBeUndefined();
    expect(
      planNativeListPatches(previous, {
        ...previous,
        fixedFooter: {
          type: 'action',
          key: 'footer',
          title: 'Done',
          actionKey: 'done',
        },
      })
    ).toBeUndefined();
    expect(
      planNativeListPatches(previous, {
        ...previous,
        rows: [identity, { ...data, key: 'other' }],
      })
    ).toBeUndefined();
    expect(
      planNativeListPatches(previous, {
        ...previous,
        rows: [
          identity,
          {
            ...data,
            columns: [{ key: 'asset', text: 'ETH' }, data.columns[1]],
          },
        ],
      })
    ).toBeUndefined();
    expect(
      planNativeListPatches(previous, {
        ...previous,
        rows: [{ ...identity, subtitle: undefined }, data],
      })
    ).toBeUndefined();
  });

  it('uses array order and rejects changed metadata in one batch', () => {
    const identity = {
      ...row('btc'),
      subtitle: '$1',
      subtitleSegments: [{ text: 'first' }, { text: 'second' }],
    };
    const previous = snapshot([identity, row('eth')]);
    expect(
      planNativeListPatches(previous, snapshot([row('eth'), identity]))
    ).toBeUndefined();
    expect(
      planNativeListPatches(
        previous,
        snapshot([
          {
            ...identity,
            subtitleSegments: [...identity.subtitleSegments].reverse(),
          },
          row('eth'),
        ])
      )
    ).toEqual([
      {
        type: 'identity',
        key: 'btc',
        changes: {
          subtitle: '$1',
          subtitleSegments: [...identity.subtitleSegments].reverse(),
          trailing: identity.trailing,
        },
      },
    ]);
    expect(
      planNativeListPatches(
        previous,
        snapshot([
          { ...identity, subtitle: '$2' },
          { ...row('eth'), title: 'Changed metadata' },
        ])
      )
    ).toBeUndefined();
  });
});

describe('native owned snapshot patch boundary', () => {
  it.each([
    [true, 'identity'],
    [true, 'dataRow'],
    [false, 'identity'],
    [false, 'dataRow'],
  ] as const)(
    'matches full validation in dev=%s for %s value patches',
    (development, family) => {
      const environment = globalThis as typeof globalThis & {
        __DEV__: boolean;
      };
      const previous = environment.__DEV__;
      environment.__DEV__ = development;
      try {
        const image = {
          uri: development ? 'https://example.com/token.png' : ' ',
          width: 32,
          height: 32,
        };
        const base: IdentityRow | DataRow =
          family === 'identity'
            ? {
                ...row('token'),
                subtitle: 'Old price',
                leading: { kind: 'token', image },
                style: { subtitle: { token: '$bodySm' } },
              }
            : {
                type: 'dataRow',
                key: 'token',
                leading: { kind: 'token', image },
                columns: [
                  { key: 'asset', text: 'Token' },
                  { key: 'value', text: '$1' },
                ],
                style: { columns: { token: '$bodyLg' } },
              };
        const patches: RowPatch[] = [
          family === 'identity'
            ? {
                type: 'identity',
                key: 'token',
                changes: {
                  subtitle: 'New price',
                  subtitleSegments: undefined,
                  trailing: [{ kind: 'value', text: '$2' }],
                },
              }
            : {
                type: 'dataRow',
                key: 'token',
                changes: {
                  columns: [
                    { key: 'asset', text: 'Token' },
                    { key: 'value', text: '$2' },
                  ],
                  trailing: undefined,
                },
              },
        ];
        const owned = ownValidatedNativeSnapshot(
          validateSnapshot(
            captureNativeListInput(snapshot([base, row('untouched')]))
          )
        );
        const accepted = applyOwnedNativePatches(owned, patches);
        const full = applyRowPatches(owned, patches);
        expect(JSON.stringify(accepted.snapshot)).toBe(JSON.stringify(full));
        expect(accepted.json).toBe(serializePatches(patches));
        expect(accepted.snapshot.rows[1]).toBe(owned.rows[1]);
        if (family === 'identity') {
          expect(
            Object.hasOwn(accepted.snapshot.rows[0], 'subtitleSegments')
          ).toBe(true);
        }
        expect(JSON.stringify(accepted.snapshot)).not.toContain('$bodySm');
        expect(JSON.stringify(accepted.snapshot)).not.toContain('$bodyLg');
        if (!development) {
          expect(
            (accepted.snapshot.rows[0] as IdentityRow | DataRow).leading
          ).toHaveProperty('image', undefined);
        }
      } finally {
        environment.__DEV__ = previous;
      }
    }
  );

  it('does not inspect unchanged rows for selector-shaped undefined fields', () => {
    const owned = ownValidatedNativeSnapshot(
      validateSnapshot(
        captureNativeListInput(
          snapshot(
            Array.from({ length: 1000 }, (_, index) => row(`token-${index}`))
          )
        )
      )
    );
    const unchanged = owned.rows[500];
    const entries = jest.spyOn(Object, 'entries');
    try {
      const accepted = applyOwnedNativePatches(owned, [
        {
          type: 'identity',
          key: 'token-0',
          changes: {
            subtitle: 'Price',
            subtitleSegments: undefined,
            trailing: [{ kind: 'value', text: '$2' }],
          },
        },
      ]);
      expect(accepted.snapshot.rows[500]).toBe(unchanged);
      expect(accepted.json).not.toContain('subtitleSegments');
      expect(
        entries.mock.calls.filter(([value]) => value === unchanged)
      ).toHaveLength(0);
    } finally {
      entries.mockRestore();
    }
  });

  it.each(['absent', 'own undefined', 'defined'] as const)(
    'preserves full patch semantics when subtitleSegments starts %s',
    (initial) => {
      const base: IdentityRow =
        initial === 'own undefined'
          ? { ...row('token'), subtitleSegments: undefined }
          : initial === 'defined'
          ? { ...row('token'), subtitleSegments: [{ text: 'Old' }] }
          : row('token');
      const owned = ownValidatedNativeSnapshot(
        validateSnapshot(captureNativeListInput(snapshot([base, row('other')])))
      );
      const patches: RowPatch[] = [
        {
          type: 'identity',
          key: 'token',
          changes: { subtitleSegments: undefined },
        },
      ];
      const unchanged = owned.rows[1];
      const entries = jest.spyOn(Object, 'entries');
      let accepted: ReturnType<typeof applyOwnedNativePatches>;
      let unchangedReads = 0;
      try {
        accepted = applyOwnedNativePatches(owned, patches);
        unchangedReads = entries.mock.calls.filter(
          ([value]) => value === unchanged
        ).length;
      } finally {
        entries.mockRestore();
      }
      const full = applyRowPatches(owned, patches);
      expect(accepted.json).toBe(serializePatches(patches));
      expect(JSON.stringify(accepted.snapshot)).toBe(JSON.stringify(full));
      expect(Object.hasOwn(accepted.snapshot.rows[0], 'subtitleSegments')).toBe(
        Object.hasOwn(full.rows[0], 'subtitleSegments')
      );
      if (initial === 'defined') {
        expect(unchangedReads).toBeGreaterThan(0);
      } else {
        expect(unchangedReads).toBe(0);
      }
    }
  );

  it('captures plain data without losing sharing, sparse arrays, or null prototypes', () => {
    const shared = { text: 'value' };
    const sparse = new Array<string | undefined>(3);
    sparse[0] = 'first';
    sparse[2] = undefined;
    const special = new Array<string>(2);
    Object.defineProperty(special, '0', {
      value: 'non-enumerable',
      configurable: true,
    });
    const arrayPrototype = Object.create(Array.prototype);
    arrayPrototype[1] = 'inherited';
    Object.setPrototypeOf(special, arrayPrototype);
    const nullPrototype = Object.assign(Object.create(null), { value: null });
    const caller = {
      first: shared,
      second: shared,
      sparse,
      special,
      nullPrototype,
      missing: undefined,
    };
    const captured = captureNativeListInput(caller);
    expect(captured).not.toBe(caller);
    expect(captured.first).toBe(captured.second);
    expect(captured.first).not.toBe(shared);
    expect(captured.sparse).toHaveLength(3);
    expect(1 in captured.sparse).toBe(false);
    expect(2 in captured.sparse).toBe(true);
    expect(captured.special[0]).toBe('non-enumerable');
    expect(captured.special[1]).toBe('inherited');
    expect(Object.hasOwn(captured.special, '1')).toBe(true);
    expect(Object.getPrototypeOf(captured.nullPrototype)).toBe(null);
    expect(captured.nullPrototype.value).toBeNull();
    expect('missing' in captured).toBe(true);
  });

  it('matches public full patch semantics for owned value rows', () => {
    const base = ownValidatedNativeSnapshot(
      validateSnapshot(captureNativeListInput(snapshot([row('btc')])))
    );
    const patches: RowPatch[] = [
      {
        type: 'identity',
        key: 'btc',
        changes: {
          subtitle: 'Price',
          trailing: [{ kind: 'value', text: '$2' }],
        },
      },
    ];
    const accepted = applyOwnedNativePatches(base, patches);
    const full = applyRowPatches(base, patches);
    expect(JSON.stringify(accepted.snapshot)).toBe(JSON.stringify(full));
    expect(accepted.json).toBe(serializePatches(patches));
    expect(Object.isFrozen(accepted.snapshot)).toBe(true);
    expect(Object.isFrozen(accepted.snapshot.rows[0])).toBe(true);
    expect(base.rows[0]).toEqual(row('btc'));
  });

  it('fully validates unowned baselines and rejected batches leave owned rows intact', () => {
    const input = snapshot([row('btc'), row('eth')]);
    const owned = ownValidatedNativeSnapshot(
      validateSnapshot(captureNativeListInput(input))
    );
    const before = JSON.stringify(owned);
    expect(() =>
      applyOwnedNativePatches(owned, [
        { type: 'identity', key: 'btc', changes: { subtitle: 'Valid' } },
        { type: 'identity', key: 'absent', changes: { subtitle: 'Invalid' } },
      ])
    ).toThrow();
    expect(JSON.stringify(owned)).toBe(before);

    const unowned = captureNativeListInput(input);
    (
      unowned.rows[1] as unknown as { subtitle: string | number }
    ).subtitle = 123;
    expect(() =>
      applyOwnedNativePatches(unowned, [
        { type: 'identity', key: 'btc', changes: { subtitle: 'Valid' } },
      ])
    ).toThrow('rows[1].subtitle');

    const validUnowned = captureNativeListInput(input);
    const fallback = applyOwnedNativePatches(validUnowned, [
      { type: 'identity', key: 'btc', changes: { title: 'Updated' } },
    ]);
    expect(Object.isFrozen(validUnowned.rows[1])).toBe(false);
    (validUnowned.rows[1] as unknown as { title: string }).title =
      'Caller mutation';
    expect((fallback.snapshot.rows[1] as IdentityRow).title).toBe('eth');
  });
});

describe('NativeList model validation', () => {
  it('validates one-shot structural transitions and title-adjacent data column icons', () => {
    const withIcons: RowModel = {
      type: 'dataRow',
      key: 'asset',
      columns: [
        {
          key: 'asset',
          text: 'ETH',
          titleIcon: { name: 'GasSolid' },
          secondaryTitleIcon: {
            name: 'TicketOutline',
            hoverActionKey: 'asset.receiptHelp',
          },
        },
        { key: 'balance', text: '1.00' },
      ],
    };
    const valid = {
      ...snapshot([withIcons]),
      structuralTransition: {
        id: 'show-more-1',
        durationMs: 220,
        anchorKey: 'asset',
      },
    } as const;
    expect(JSON.parse(serializeSnapshot(valid)).structuralTransition).toEqual(
      valid.structuralTransition
    );
    expect(() =>
      validateSnapshot({
        ...valid,
        structuralTransition: { ...valid.structuralTransition, durationMs: -1 },
      })
    ).toThrow('durationMs');
    expect(() =>
      validateSnapshot({
        ...valid,
        rows: [
          {
            ...withIcons,
            columns: [
              { ...withIcons.columns[0], titleIcon: { name: '' } },
              withIcons.columns[1],
            ],
          },
        ],
      } as NativeListSnapshot)
    ).toThrow('titleIcon.name');
  });

  it('validates native disclosure targets independently of transition ids', () => {
    const disclosed = {
      ...snapshot([]),
      disclosure: { expanded: false, expandedTrailingInset: 20 },
    } as const;
    expect(JSON.parse(serializeSnapshot(disclosed)).disclosure).toEqual(
      disclosed.disclosure
    );
    expect(() =>
      validateSnapshot({
        ...disclosed,
        disclosure: { expanded: 'yes' },
      } as unknown as NativeListSnapshot)
    ).toThrow('disclosure.expanded');
    for (const inset of [-1, Number.NaN, Number.POSITIVE_INFINITY]) {
      expect(() =>
        validateSnapshot({
          ...disclosed,
          disclosure: { expanded: true, expandedTrailingInset: inset },
        })
      ).toThrow('expandedTrailingInset');
    }
  });

  it('accepts complete recipient addresses and inline warnings on snapshot and patch paths', () => {
    const recipient: IdentityRow = {
      ...row('recipient'),
      subtitle: 'bc1qverylongaddresswithmemoandwrappedcontent',
      subtitleLines: 0,
      contentHeight: 'auto',
      longPressActionKey: 'recipient.menu',
      leading: {
        kind: 'wallet',
        fallbackText: 'A',
        fallbackTextColor: '#ffffff',
        backgroundColor: '#000000',
      },
    };
    const warning: RowModel = {
      type: 'message',
      key: 'warning',
      title: '',
      body: 'Unverified token warning',
      bodyLines: 0,
      presentation: 'inlineWarning',
      time: '',
    };
    expect(validateSnapshot(snapshot([recipient, warning])).rows).toHaveLength(
      2
    );
    expect(
      applyRowPatches(snapshot([recipient, warning]), [
        {
          type: 'identity',
          key: 'recipient',
          changes: {
            subtitleLines: 0,
            contentHeight: 'auto',
            longPressActionKey: 'open.menu',
          },
        },
        {
          type: 'message',
          key: 'warning',
          changes: { bodyLines: 0, presentation: 'inlineWarning' },
        },
      ]).rows
    ).toMatchObject([
      {
        subtitleLines: 0,
        contentHeight: 'auto',
        longPressActionKey: 'open.menu',
      },
      { bodyLines: 0, presentation: 'inlineWarning' },
    ]);
    expect(() =>
      validateSnapshot(
        snapshot([
          { ...recipient, contentHeight: 'fixed' } as unknown as IdentityRow,
        ])
      )
    ).toThrow('contentHeight');
    expect(() =>
      validateSnapshot(
        snapshot([{ ...recipient, subtitleLines: 3 } as unknown as IdentityRow])
      )
    ).toThrow('subtitleLines');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...recipient,
            leading: { kind: 'wallet', fallbackTextColor: 'white' },
          },
        ])
      )
    ).toThrow('fallbackTextColor');
    expect(() =>
      validateSnapshot(
        snapshot([
          { ...warning, presentation: 'dialog' } as unknown as RowModel,
        ])
      )
    ).toThrow('presentation');
  });
  it('validates DeFi button groups and explicit help without imposing an action count', () => {
    const group: ButtonGroupRow = {
      type: 'buttonGroup',
      key: 'actions',
      buttons: Array.from({ length: 40 }, (_, index) => ({
        key: `action-${index}`,
        label: `Action ${index}`,
        actionKey: `press-${index}`,
      })),
    };
    const heading: RowModel = {
      type: 'sectionHeader',
      key: 'heading',
      sectionKey: 'position',
      title: 'Pool',
      leadingBadge: { key: 'staking', text: 'Staking', tone: 'success' },
      value: '$1',
      valueHelp: {
        actionKey: 'price-help',
        presentation: 'popover',
        underline: 'dashed',
      },
      style: { valuePlacement: 'afterTitle', titleDecoration: 'none' },
    };
    const asset: IdentityRow = {
      ...row('asset'),
      trailing: [
        {
          kind: 'valuePair',
          primary: '--',
          secondary: '1 ETH',
          primaryHelp: { actionKey: 'missing-price', presentation: 'tooltip' },
        },
      ],
    };
    expect(
      validateSnapshot(snapshot([heading, asset, group])).rows
    ).toHaveLength(3);
    expect(() =>
      validateSnapshot({
        ...snapshot([heading]),
        theme: { successBackground: 'not-a-color' },
      } as unknown as NativeListSnapshot)
    ).toThrow('theme.successBackground');
    expect(() =>
      validateSnapshot(snapshot([{ ...group, buttons: [] }]))
    ).toThrow('buttons');
    expect(() =>
      validateSnapshot(
        snapshot([
          { ...group, buttons: [group.buttons[0]!, group.buttons[0]!] },
        ])
      )
    ).toThrow('duplicate button key');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...group,
            buttons: [{ ...group.buttons[0]!, loading: 'yes' }],
          } as unknown as ButtonGroupRow,
        ])
      )
    ).toThrow('loading');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...heading,
            valueHelp: {
              actionKey: 'price-help',
              underline: 'dashed',
            },
          } as unknown as RowModel,
        ])
      )
    ).toThrow('presentation');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...asset,
            trailing: [
              {
                kind: 'valuePair',
                primary: '--',
                secondary: '1 ETH',
                primaryHelp: {
                  actionKey: 'missing-price',
                  presentation: 'hover',
                },
              },
            ],
          } as unknown as RowModel,
        ])
      )
    ).toThrow('presentation');
    expect(
      applyRowPatches(snapshot([group]), [
        {
          type: 'buttonGroup',
          key: 'actions',
          changes: {
            buttons: [
              {
                key: 'withdraw',
                label: 'Withdraw',
                actionKey: 'withdraw',
                loading: true,
              },
            ],
          },
        },
      ]).rows[0]
    ).toMatchObject({ buttons: [{ loading: true }] });
  });
  it('serializes Activity hideLeading in snapshots and patches and rejects non-booleans', () => {
    const activity: ActivityRow = {
      type: 'activity',
      key: 'tx',
      leading: { kind: 'icon', name: 'ArrowTopOutline' },
      title: 'Send',
      amounts: [{ key: 'send', text: '-1 ETH' }],
      hideLeading: true,
    };
    expect(
      JSON.parse(serializeSnapshot(snapshot([activity]))).rows[0].hideLeading
    ).toBe(true);
    const patch: RowPatch = {
      type: 'activity',
      key: 'tx',
      changes: { hideLeading: false },
    };
    expect(JSON.parse(serializePatches([patch]))[0].changes.hideLeading).toBe(
      false
    );
    expect(
      applyRowPatches(snapshot([activity]), [patch]).rows[0]
    ).toMatchObject({
      hideLeading: false,
      leading: activity.leading,
      amounts: activity.amounts,
    });
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...activity,
            hideLeading: 'false',
          } as unknown as ActivityRow,
        ])
      )
    ).toThrow('hideLeading');
    expect(() =>
      validatePatches([
        {
          type: 'activity',
          key: 'tx',
          changes: { hideLeading: 1 },
        } as unknown as RowPatch,
      ])
    ).toThrow('hideLeading');
  });
  it('keeps identity button, title icon and leading width through snapshots and patches', () => {
    const identity: IdentityRow = {
      ...row('risk'),
      titleIcon: {
        name: 'GasSolid',
        tintColor: '#888888',
        hoverActionKey: 'native-token-info',
      },
      leading: {
        kind: 'token',
        fallbackIcon: {
          name: 'CryptoCoinOutline',
          size: 28,
          backgroundColor: '#eeeeee',
        },
        overlays: [
          {
            position: 'bottomRight',
            image: { uri: 'https://example.com/n.png', width: 16, height: 16 },
            fallbackIcon: { name: 'GlobusOutline' },
          },
        ],
      },
      trailing: [
        {
          kind: 'button',
          label: 'Show',
          actionKey: 'show',
          backgroundColor: '#111111',
        },
      ],
      style: { leadingContentMaxWidthRatio: 0.6 },
    };
    const parsed = JSON.parse(
      serializeSnapshot({
        ...snapshot([identity]),
        theme: {
          background: '#ffffff',
          rowBackground: '#ffffff',
          rowSelectedBackground: '#eeeeee',
          rowHoverBackground: '#dddddd',
          primaryText: '#111111',
          secondaryText: '#222222',
          separator: '#333333',
          accent: '#444444',
          positive: '#555555',
          negative: '#666666',
        },
      })
    );
    expect(parsed.rows[0]).toMatchObject({
      titleIcon: { name: 'GasSolid', hoverActionKey: 'native-token-info' },
      trailing: [{ kind: 'button', actionKey: 'show' }],
      style: { leadingContentMaxWidthRatio: 0.6 },
      leading: {
        fallbackIcon: { size: 28 },
        overlays: [{ fallbackIcon: { name: 'GlobusOutline' } }],
      },
    });
    expect(parsed.theme.rowHoverBackground).toBe('#dddddd');
    expect(
      validatePatches([
        {
          type: 'identity',
          key: identity.key,
          changes: {
            titleIcon: {
              name: 'GasSolid',
              hoverActionKey: 'native-token-info',
            },
            trailing: [{ kind: 'button', label: 'Hide', actionKey: 'hide' }],
            style: { leadingContentMaxWidthRatio: 1 },
          },
        },
      ])
    ).toHaveLength(1);
    for (const invalid of [
      {
        ...identity,
        trailing: [{ kind: 'button', label: '', actionKey: 'show' }],
      },
      {
        ...identity,
        trailing: [{ kind: 'button', label: 'Show', actionKey: '' }],
      },
      {
        ...identity,
        trailing: [
          {
            kind: 'button',
            label: 'Show',
            actionKey: 'show',
            textColor: 'red',
          },
        ],
      },
      { ...identity, titleIcon: { name: '' } },
      { ...identity, titleIcon: { name: 'GasSolid', hoverActionKey: '' } },
      { ...identity, style: { leadingContentMaxWidthRatio: 0 } },
      {
        ...identity,
        leading: {
          kind: 'token',
          overlays: [
            { position: 'bottomRight', text: 'E', fontWeight: 'heavy' },
          ],
        },
      },
      {
        ...identity,
        leading: {
          kind: 'token',
          fallbackIcon: { name: 'CryptoCoinOutline', size: -1 },
        },
      },
      {
        ...identity,
        leading: {
          kind: 'token',
          fallbackIcon: { name: 'CryptoCoinOutline', backgroundColor: 'red' },
        },
      },
      {
        ...identity,
        leading: {
          kind: 'token',
          overlays: [
            {
              position: 'bottomRight',
              fallbackIcon: { name: 'GlobusOutline' },
            },
          ],
        },
      },
      {
        ...identity,
        trailing: [{ kind: 'icon', name: 'EditOutline', buttonStyle: 'other' }],
      },
    ]) {
      expect(() =>
        validateSnapshot(snapshot([invalid as IdentityRow]))
      ).toThrow();
    }
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: identity.key,
          changes: {
            trailing: [{ kind: 'button', label: '', actionKey: 'show' }],
          },
        } as RowPatch,
      ])
    ).toThrow('label');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: identity.key,
          changes: { titleIcon: { name: 'GasSolid', hoverActionKey: '' } },
        },
      ])
    ).toThrow('hoverActionKey');
    expect(() =>
      validateSnapshot({
        ...snapshot([identity]),
        theme: { rowHoverBackground: 'red' } as NativeListSnapshot['theme'],
      })
    ).toThrow('rowHoverBackground');
  });
  it('accepts the bounded Market row, verified glyph, styles, and states', () => {
    const styled: MarketRow = {
      ...marketRow(),
      style: {
        horizontalPadding: 20,
        verticalPadding: 12,
        leadingGap: 14,
        titleBadgeGap: 4,
        titleBadgeLayout: 'inline',
        trailingGap: 8,
        contentTrailingGap: 12,
        subtitleTrailingPadding: 8,
        image: {
          width: 32,
          height: 32,
          shape: 'circle',
          contentFit: 'cover',
        },
        title: {
          fontSize: 16,
          fontWeight: 'medium',
          lineHeight: 24,
          lines: 1,
          alignment: 'start',
        },
        change: { fontSize: 14, fontWeight: 'medium', lineHeight: 20 },
        changeWidth: 80,
        lineGap: 4,
        changeHeight: 32,
        changeCornerRadius: 8,
      },
    };
    const states: RowModel[] = [
      styled,
      {
        type: 'system',
        key: 'loading',
        variant: 'loading',
        presentation: 'market',
      },
      {
        type: 'system',
        key: 'empty',
        variant: 'noMatch',
        presentation: 'market',
        message: 'No assets',
      },
      {
        type: 'system',
        key: 'error',
        variant: 'retry',
        presentation: 'market',
        message: 'Try again',
        actionKey: 'market.retry',
        actionText: 'Retry',
      },
      { type: 'system', key: 'end', variant: 'end', presentation: 'market' },
      {
        type: 'system',
        key: 'skeleton',
        variant: 'loading',
        loadingStyle: 'skeleton',
      },
      {
        type: 'system',
        key: 'spinner',
        variant: 'loading',
        loadingStyle: 'spinner',
      },
    ];
    expect(validateSnapshot(snapshot(states)).rows).toEqual(states);
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            type: 'system',
            key: 'invalid',
            variant: 'loading',
            loadingStyle: 'unknown',
          } as unknown as RowModel,
        ])
      )
    ).toThrow('loadingStyle');
  });

  it('rejects arbitrary Market glyphs and out-of-range styles', () => {
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...marketRow(),
            leadingAction: {
              kind: 'icon',
              name: 'StarOutline',
              actionKey: '',
            },
          },
        ])
      )
    ).toThrow('leadingAction');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...marketRow(),
            badges: [{ key: 'bad', iconName: 'star' }],
          } as unknown as MarketRow,
        ])
      )
    ).toThrow('iconName');
    expect(() =>
      validateSnapshot(
        snapshot([{ ...marketRow(), style: { changeWidth: 240 } } as MarketRow])
      )
    ).toThrow('changeWidth');
    expect(() =>
      validateSnapshot(
        snapshot([{ ...marketRow(), style: { lineGap: 17 } } as MarketRow])
      )
    ).toThrow('lineGap');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...marketRow(),
            style: { titleBadgeLayout: 'stacked' },
          } as unknown as MarketRow,
        ])
      )
    ).toThrow('titleBadgeLayout');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...marketRow(),
            subtitlePrefix: { text: 'Bitcoin', maxWidth: 321 },
          },
        ])
      )
    ).toThrow('subtitlePrefix.maxWidth');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...marketRow(),
            badges: [
              {
                key: 'oversized',
                text: 'DEX',
                style: { height: 65 },
              },
            ],
          },
        ])
      )
    ).toThrow('badges[0].style.height');
  });

  it('accepts amount plus checkbox on identity rows', () => {
    expect(validateSnapshot(snapshot())).toBeDefined();
  });

  it('accepts only the supported identity presentations', () => {
    expect(
      validateSnapshot(
        snapshot([
          {
            ...row('wallet'),
            presentation: 'walletSidebar',
            draggable: true,
          },
        ])
      ).rows[0]
    ).toMatchObject({ presentation: 'walletSidebar', draggable: true });
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

  it('accepts a deterministic hardware wallet group', () => {
    const parent = {
      ...row('hardware'),
      presentation: 'walletSidebar' as const,
    };
    const child = {
      ...row('hidden-1'),
      presentation: 'walletSidebar' as const,
    };
    expect(
      validateSnapshot(
        snapshot([
          {
            type: 'walletGroup',
            key: parent.key,
            parent,
            children: [child],
            draggable: true,
          },
        ])
      ).rows[0]
    ).toMatchObject({ type: 'walletGroup', key: 'hardware' });
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            type: 'walletGroup',
            key: parent.key,
            parent,
            children: [],
            draggable: true,
          },
        ])
      )
    ).toThrow('must contain at least one child wallet');
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
        sectionIndex: {
          enabled: true,
          hapticsEnabled: false,
          centeredInWindow: true,
        },
      },
    });

    expect(indexed.capabilities?.sectionIndex).toEqual({
      enabled: true,
      hapticsEnabled: false,
      centeredInWindow: true,
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
        ...snapshot([header]),
        layout: { kind: 'sectioned' },
        capabilities: {
          sectionIndex: {
            enabled: true,
            centeredInWindow: 'true' as unknown as boolean,
          },
        },
      })
    ).toThrow('sectionIndex.centeredInWindow');
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

  it.each([' btc', 'btc ', '\tbtc\n', '\u00a0btc\u3000'])(
    'preserves surrounding whitespace in opaque keys: %j',
    (key) => {
      const initial = {
        ...snapshot([row(key), row('btc')]),
        selection: {
          mode: 'multiple' as const,
          selectedKeys: [key],
          rowPressToggles: true,
        },
      };
      expect(validateSnapshot(initial)).toBe(initial);
      expect(JSON.parse(serializeSnapshot(initial)).rows[0].key).toBe(key);
      const next = applyRowPatches(initial, [
        { type: 'identity', key, changes: { title: 'Updated' } },
      ]);
      expect(next.rows[0]).toMatchObject({ key, title: 'Updated' });
      expect(next.rows[1]).toBe(initial.rows[1]);
      expect(next.selection?.selectedKeys).toEqual([key]);
      expect(() => validateSnapshot(snapshot([row(key), row(key)]))).toThrow(
        'duplicate key'
      );
    }
  );

  it.each(['', ' ', '\t\n', '\u00a0\u3000', 'x'.repeat(257)])(
    'rejects blank or oversized keys: %j',
    (key) => {
      expect(() => validateSnapshot(snapshot([row(key)]))).toThrow(
        'rows[0].key'
      );
      expect(() =>
        validatePatches([
          { type: 'identity', key, changes: { title: 'Updated' } },
        ])
      ).toThrow('patches[0].key');
    }
  );

  it('preserves the 256-code-unit key limit with surrounding whitespace', () => {
    const key = ` ${'x'.repeat(254)} `;
    expect(validateSnapshot(snapshot([row(key)])).rows[0]?.key).toBe(key);
  });

  it('rejects duplicate stable keys', () => {
    expect(() => validateSnapshot(snapshot([row('btc'), row('btc')]))).toThrow(
      'duplicate key'
    );
  });

  it('rejects an unknown row type before renderer lookup', () => {
    const unknown = {
      type: 'unknown',
      key: 'unknown-row',
    } as unknown as RowModel;

    expect(() => validateSnapshot(snapshot([unknown]))).toThrow(
      'rows[0].type: is not supported'
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

  it('accepts medium only for a value-pair primary font weight', () => {
    const pair = {
      kind: 'valuePair' as const,
      primary: '$3.00',
      secondary: '1 ETH',
      primaryFontWeight: 'medium' as const,
    };
    expect(
      JSON.parse(
        serializeSnapshot(
          snapshot([
            {
              ...row('weighted-pair'),
              trailing: [pair],
            },
          ])
        )
      ).rows[0].trailing[0].primaryFontWeight
    ).toBe('medium');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...row('invalid-weight'),
            trailing: [{ ...pair, primaryFontWeight: 'regular' }],
          } as unknown as IdentityRow,
        ])
      )
    ).toThrow('primaryFontWeight');
  });

  it('validates independent segmented value-pair lines in snapshots and patches', () => {
    const pair = {
      kind: 'valuePair' as const,
      primary: '0.001',
      secondary: '$0.01',
      primaryTextSegments: [
        { text: '0.00' },
        { text: '1', style: 'subscript' as const },
      ],
      secondaryTextSegments: [
        { text: '$0.0' },
        { text: '1', style: 'subscript' as const },
      ],
    };
    const item = { ...row('segmented-pair'), trailing: [pair] };
    expect(
      JSON.parse(serializeSnapshot(snapshot([item]))).rows[0].trailing[0]
    ).toMatchObject(pair);
    const patch: RowPatch = {
      type: 'identity',
      key: item.key,
      changes: {
        revision: 2,
        trailing: [{ ...pair, primaryTextSegments: [] }],
      },
    };
    expect(
      JSON.parse(serializePatches([patch]))[0].changes.trailing[0]
        .primaryTextSegments
    ).toEqual([]);
    expect(applyRowPatches(snapshot([item]), [patch]).rows[0]).toMatchObject({
      trailing: [{ ...pair, primaryTextSegments: [] }],
    });
    for (const field of [
      'primaryTextSegments',
      'secondaryTextSegments',
    ] as const) {
      for (const invalid of [
        null,
        {},
        [{ text: 4 }],
        [{ text: '1', style: 'super' }],
        Array.from({ length: 65 }, () => ({ text: '1' })),
        [{ text: 'x'.repeat(4097) }],
        [{ text: 'x'.repeat(2049) }, { text: 'x'.repeat(2049) }],
      ]) {
        const malformed = { ...pair, [field]: invalid };
        expect(() =>
          validateSnapshot(
            snapshot([
              {
                ...item,
                trailing: [malformed],
              } as unknown as IdentityRow,
            ])
          )
        ).toThrow(field);
        expect(() =>
          validatePatches([
            {
              type: 'identity',
              key: item.key,
              changes: { revision: 3, trailing: [malformed] },
            } as unknown as RowPatch,
          ])
        ).toThrow(field);
      }
    }
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
  it('applies a Market quote patch without replacing its image descriptor', () => {
    const initial = snapshot([marketRow()]);
    const leading =
      initial.rows[0]?.type === 'market' ? initial.rows[0].leading : undefined;
    const next = applyRowPatches(initial, [
      {
        type: 'market',
        key: 'market-btc',
        changes: {
          revision: 2,
          price: '$64,240.00',
          change: { text: '+2.41%', tone: 'positive' },
        },
      },
    ]);
    expect(next.rows[0]).toMatchObject({
      revision: 2,
      price: '$64,240.00',
      change: { text: '+2.41%', tone: 'positive' },
    });
    expect(next.rows[0]?.type === 'market' && next.rows[0].leading).toBe(
      leading
    );
  });

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

describe('NativeList production validation', () => {
  const environment = globalThis as typeof globalThis & { __DEV__: boolean };
  const development = environment.__DEV__;
  beforeEach(() => {
    environment.__DEV__ = false;
  });
  afterEach(() => {
    environment.__DEV__ = development;
  });

  it('rejects malformed identity action and overlay fields in production snapshots and patches', () => {
    const invalid = {
      ...row('risk'),
      titleIcon: { name: 'GasSolid' },
      trailing: [
        { kind: 'button', label: 'Show', actionKey: 'show', textColor: 'red' },
      ],
      style: { leadingContentMaxWidthRatio: 2 },
    } as unknown as IdentityRow;
    expect(() => validateSnapshot(snapshot([invalid]))).toThrow();
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: { trailing: invalid.trailing },
        },
      ])
    ).toThrow('textColor');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: { style: invalid.style },
        },
      ])
    ).toThrow('leadingContentMaxWidthRatio');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...invalid,
            titleIcon: { name: 'GasSolid', hoverActionKey: '' },
            trailing: [],
            style: undefined,
          },
        ])
      )
    ).toThrow('hoverActionKey');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: { titleIcon: { name: 'GasSolid', hoverActionKey: '' } },
        },
      ])
    ).toThrow('hoverActionKey');
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...row('risk'),
            secondaryTitleIcon: { name: 'TicketSolid', tintColor: 'red' },
          } as unknown as IdentityRow,
        ])
      )
    ).toThrow('secondaryTitleIcon.tintColor');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: {
            secondaryTitleIcon: { name: 'TicketSolid', hoverActionKey: '' },
          },
        },
      ])
    ).toThrow('secondaryTitleIcon.hoverActionKey');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: { secondaryTitleIcon: null },
        } as unknown as RowPatch,
      ])
    ).toThrow('secondaryTitleIcon');
    expect(() =>
      validateSnapshot({
        ...snapshot(),
        theme: { rowHoverBackground: 'bad' } as NativeListSnapshot['theme'],
      })
    ).toThrow('rowHoverBackground');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: {
            leading: {
              kind: 'token',
              overlays: [
                { position: 'bottomRight', text: 'E', fontWeight: 'heavy' },
              ],
            },
          },
        } as unknown as RowPatch,
      ])
    ).toThrow('fontWeight');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: {
            leading: {
              kind: 'token',
              fallbackIcon: {
                name: 'CryptoCoinOutline',
                size: 0,
              },
            },
          },
        } as RowPatch,
      ])
    ).toThrow('size');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: {
            leading: {
              kind: 'token',
              overlays: [
                {
                  position: 'bottomRight',
                  fallbackIcon: { name: 'GlobusOutline' },
                },
              ],
            },
          },
        } as RowPatch,
      ])
    ).toThrow('requires image');
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: invalid.key,
          changes: {
            trailing: [
              { kind: 'icon', name: 'EditOutline', buttonStyle: 'other' },
            ],
          },
        } as unknown as RowPatch,
      ])
    ).toThrow('buttonStyle');
  });

  it('keeps production identity and text without enforcing display budgets', () => {
    const key = ` ${'k'.repeat(257)} `;
    const input = snapshot([
      {
        ...row(key),
        title: 'T'.repeat(4097),
        style: { title: { token: '$bodySm' } },
      },
    ]);
    const next = validateSnapshot(input);
    expect(next.rows[0]).toMatchObject({
      key,
      title: input.rows[0].type === 'identity' ? input.rows[0].title : '',
      style: { title: { fontSize: 12, lineHeight: 16 } },
    });
    expect(input.rows[0]).toMatchObject({
      style: { title: { token: '$bodySm' } },
    });
  });

  it.each([
    { ...row('account'), presentation: 'accountSelector', height: 56 },
    marketRow(),
  ] as const)('rejects missing required $type leading visuals', (validRow) => {
    const input = snapshot([validRow]);
    expect(validateSnapshot(input)).toEqual(input);
    const invalidRow = { ...validRow };
    Reflect.deleteProperty(invalidRow, 'leading');
    expect(() => validateSnapshot(snapshot([invalidRow as RowModel]))).toThrow(
      'rows[0].leading: must be an object'
    );
  });

  it('normalizes absent images and patch typography before serialization', () => {
    const input = snapshot([
      {
        ...row('image'),
        leading: {
          kind: 'token',
          fallbackText: 'I',
          image: { uri: ' ', width: 32, height: 32 },
        },
      },
    ]);
    expect(validateSnapshot(input).rows[0]).toMatchObject({
      leading: { fallbackText: 'I', image: undefined },
    });
    const patches: RowPatch[] = [
      {
        type: 'identity',
        key: 'image',
        changes: {
          style: { title: { token: '$bodyLg' } },
          leading: { kind: 'token', image: { uri: '', width: 32, height: 32 } },
        },
      },
    ];
    expect(validatePatches(patches)[0]).toMatchObject({
      changes: {
        style: { title: { fontSize: 16, lineHeight: 24 } },
        leading: { image: undefined },
      },
    });
    expect(
      applyRowPatches(validateSnapshot(input), patches).rows[0]
    ).toMatchObject({ style: { title: { fontSize: 16 } } });
  });

  it('allows display index titles and empty patch no-ops', () => {
    const headers: RowModel[] = ['a', 'b'].map((key) => ({
      type: 'sectionHeader',
      key,
      sectionKey: key,
      indexTitle: ' Same title ',
      title: key,
    }));
    expect(validateSnapshot(snapshot(headers)).rows).toHaveLength(2);
    expect(
      validatePatches([{ type: 'identity', key: 'a', changes: {} }])
    ).toEqual([]);
  });

  it('keeps empty image patches consistent with their native JSON payload', () => {
    const initial = snapshot([
      {
        type: 'mediaTile',
        key: 'media',
        variant: 'gallery',
        title: 'Image',
        image: { uri: 'https://example.com/image.png', width: 32, height: 32 },
      },
    ]);
    const patches: RowPatch[] = [
      {
        type: 'mediaTile',
        key: 'media',
        changes: { image: { uri: '', width: 32, height: 32 } },
      },
    ];
    const nativePatches = JSON.parse(serializePatches(patches)) as RowPatch[];
    expect(serializeSnapshot(applyRowPatches(initial, patches))).toBe(
      serializeSnapshot(applyRowPatches(initial, nativePatches))
    );
  });

  it('still rejects duplicate identities, unserializable input and invalid renderer shapes', () => {
    expect(() =>
      validateSnapshot(snapshot([row('same'), row('same')]))
    ).toThrow('duplicate');
    expect(() =>
      validateSnapshot(snapshot([{ ...row('x'), height: Number.NaN }]))
    ).toThrow('finite');
    expect(() =>
      validateSnapshot(
        snapshot([{ ...row('x'), type: 'unknown' } as unknown as RowModel])
      )
    ).toThrow('supported');
    expect(() =>
      validateSnapshot(
        snapshot([{ ...row('x'), trailing: [null] } as unknown as RowModel])
      )
    ).toThrow('object');
    expect(() =>
      applyRowPatches(snapshot(), [
        { type: 'identity', key: 'missing', changes: { title: 'No' } },
      ])
    ).toThrow('unknown');
  });

  it('retains complete assertions in development', () => {
    environment.__DEV__ = true;
    expect(() => validateSnapshot(snapshot([row('k'.repeat(257))]))).toThrow(
      '256'
    );
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...row('x'),
            leading: {
              kind: 'token',
              image: { uri: '', width: 32, height: 32 },
            },
          },
        ])
      )
    ).toThrow('empty');
    expect(() =>
      validatePatches([{ type: 'identity', key: 'x', changes: {} }])
    ).toThrow('at least one');
  });
});

describe('NativeList style contract', () => {
  const styled = (style: unknown): NativeListSnapshot =>
    snapshot([{ ...row('btc'), style } as RowModel]);

  it('resolves a typography token to numbers before serialization', () => {
    const [first] = validateSnapshot(
      styled({ title: { token: '$bodyLg' }, horizontalPadding: 16 })
    ).rows;
    expect((first as IdentityRow).style).toEqual({
      title: { fontSize: 16, lineHeight: 24, fontWeight: 'regular' },
      horizontalPadding: 16,
    });
    expect(
      serializeSnapshot(styled({ title: { token: '$bodyLg' } }))
    ).not.toContain('$bodyLg');
  });

  it('lets an explicit value override the token it resolves', () => {
    const [first] = validateSnapshot(
      styled({ title: { token: '$bodyLg', fontSize: 15 } })
    ).rows;
    expect((first as IdentityRow).style?.title).toEqual({
      fontSize: 15,
      lineHeight: 24,
      fontWeight: 'regular',
    });
  });

  it('keeps snapshot identity when nothing needs resolving', () => {
    const input = snapshot();
    expect(validateSnapshot(input)).toBe(input);
  });

  it('rejects a style key the template does not declare', () => {
    expect(() => validateSnapshot(styled({ price: { fontSize: 12 } }))).toThrow(
      'is not a style key of the "identity" template'
    );
  });

  it.each([
    ['title', null],
    ['title', 'large'],
    ['title', []],
    ['image', null],
    ['image', 32],
    ['image', []],
  ])('rejects malformed nested %s styles', (field, value) => {
    expect(() => validateSnapshot(styled({ [field]: value }))).toThrow(
      `style.${field}: must be an object`
    );
  });

  it('rejects arbitrary nested layout styles in snapshots and patches', () => {
    for (const style of [
      { title: { position: 'absolute' } },
      { image: { transform: 'scale(2)' } },
      { subtitle: { color: 123 } },
    ]) {
      expect(() => validateSnapshot(styled(style))).toThrow('style.');
      expect(() =>
        validatePatches([
          { type: 'identity', key: 'btc', changes: { style } },
        ] as unknown as RowPatch[])
      ).toThrow('style.');
    }
  });

  it('rejects an unknown token and out-of-range metrics', () => {
    expect(() =>
      validateSnapshot(styled({ title: { token: '$displayXl' } }))
    ).toThrow('token');
    expect(() => validateSnapshot(styled({ title: { fontSize: 72 } }))).toThrow(
      'fontSize'
    );
    expect(() => validateSnapshot(styled({ lineGap: 17 }))).toThrow('lineGap');
  });

  it.each(['red', '#abc', 'rgb(1, 2, 3)', '$text', '#123456789'])(
    'rejects nonportable style color %s',
    (color) => {
      expect(() => validateSnapshot(styled({ title: { color } }))).toThrow(
        'must be #RRGGBB or #RRGGBBAA'
      );
    }
  );

  it.each(['#123456', '#aAbBcC80'])(
    'accepts shared style color %s',
    (color) => {
      expect(() =>
        validateSnapshot(styled({ title: { color } }))
      ).not.toThrow();
    }
  );

  it('rejects box parameters for template parts that do not exist, including patches', () => {
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            type: 'rail',
            key: 'r',
            title: 'Rail',
            style: { lineGap: 3 },
          } as unknown as RowModel,
        ])
      )
    ).toThrow('style.lineGap');
    expect(() =>
      validatePatches([
        {
          type: 'sectionHeader',
          key: 'h',
          changes: { style: { image: { width: 20 } } },
        } as unknown as RowPatch,
      ])
    ).toThrow('style.image');
    expect(() =>
      validatePatches([
        {
          type: 'dataRow',
          key: 'd',
          changes: { style: { trailingGap: 4 } },
        } as unknown as RowPatch,
      ])
    ).toThrow('style.trailingGap');
  });

  it('validates and resolves a style carried by a patch', () => {
    const patches = validatePatches([
      {
        type: 'identity',
        key: 'btc',
        changes: { style: { subtitle: { token: '$bodySm' } } },
      } as unknown as RowPatch,
    ]);
    expect(
      (patches[0] as unknown as { changes: { style: { subtitle: unknown } } })
        .changes.style.subtitle
    ).toEqual({ fontSize: 12, lineHeight: 16, fontWeight: 'regular' });
    expect(() =>
      validatePatches([
        {
          type: 'identity',
          key: 'btc',
          changes: { style: { change: {} } },
        } as unknown as RowPatch,
      ])
    ).toThrow('is not a style key of the "identity" template');
  });

  it('accepts list chrome and rejects anything outside it', () => {
    const withChrome = (listStyle: unknown): NativeListSnapshot =>
      ({ ...snapshot(), listStyle } as NativeListSnapshot);
    expect(
      validateSnapshot(
        withChrome({
          separator: { inset: 16, color: '#E0E0E0' },
          groupCornerRadius: 8,
        })
      ).listStyle
    ).toEqual({
      separator: { inset: 16, color: '#E0E0E0' },
      groupCornerRadius: 8,
    });
    expect(() =>
      validateSnapshot(withChrome({ separator: { inset: 65 } }))
    ).toThrow('separator.inset');
    expect(() =>
      validateSnapshot(withChrome({ groupCornerRadius: 41 }))
    ).toThrow('groupCornerRadius');
    // Chrome the platforms cannot all honour is rejected rather than ignored.
    expect(() => validateSnapshot(withChrome({ pullToRefresh: {} }))).toThrow(
      'is not a list style key'
    );
    expect(() =>
      validateSnapshot(withChrome({ separator: { thickness: 2 } }))
    ).toThrow('is not a separator style key');
  });

  it('validates the separator color with the shared row color format', () => {
    const withChrome = (listStyle: unknown): NativeListSnapshot =>
      ({ ...snapshot(), listStyle } as NativeListSnapshot);
    expect(
      validateSnapshot(withChrome({ separator: { color: '#11223344' } }))
        .listStyle
    ).toEqual({ separator: { color: '#11223344' } });
    for (const color of ['red', '#fff', 'rgba(0,0,0,0.1)', '#1122334', 42]) {
      expect(() =>
        validateSnapshot(withChrome({ separator: { color } }))
      ).toThrow('listStyle.separator.color: must be #RRGGBB or #RRGGBBAA');
    }
  });

  it('accepts every style key sent by the app Market list builder', () => {
    // Mirrors TOKEN_ROW_STYLE, rowStyleForChange, subtitle prefixes and the
    // token/perps badge styles in app-monorepo marketNativeListRows.ts.
    const style = {
      horizontalPadding: 20,
      verticalPadding: 12,
      leadingGap: 14,
      lineGap: 4,
      titleBadgeGap: 4,
      titleBadgeLayout: 'inline',
      trailingGap: 8,
      image: {
        width: 32,
        height: 32,
        shape: 'circle',
        cornerRadius: 16,
        contentFit: 'cover',
      },
      title: {
        fontSize: 16,
        fontWeight: 'medium',
        lineHeight: 24,
        lines: 1,
        alignment: 'start',
      },
      subtitle: {
        fontSize: 12,
        fontWeight: 'regular',
        lineHeight: 16,
        lines: 1,
        alignment: 'start',
      },
      price: {
        fontSize: 16,
        fontWeight: 'medium',
        lineHeight: 24,
        lines: 1,
        alignment: 'end',
      },
      change: {
        fontSize: 13,
        fontWeight: 'medium',
        lineHeight: 20,
        lines: 1,
        alignment: 'center',
      },
      changeWidth: 80,
      changeHeight: 32,
      changeCornerRadius: 8,
    } as const;
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...marketRow(),
            style,
            badges: [
              {
                key: 'stock-source',
                icon: {
                  uri: 'https://example.com/s.png',
                  width: 14,
                  height: 14,
                },
                style: { height: 14 },
                tone: 'neutral',
              },
              {
                key: 'leverage',
                text: '10x',
                tone: 'info',
                style: {
                  fontSize: 10,
                  fontWeight: 'regular',
                  lineHeight: 16,
                  height: 16,
                  horizontalPadding: 6,
                },
              },
            ],
          } as MarketRow,
        ])
      )
    ).not.toThrow();
  });

  it('keeps the Market style surface intact', () => {
    const [first] = validateSnapshot(
      snapshot([
        {
          ...marketRow(),
          style: { title: { token: '$headingSm' }, changeWidth: 80 },
        } as MarketRow,
      ])
    ).rows;
    expect((first as MarketRow).style?.title).toEqual({
      fontSize: 16,
      lineHeight: 24,
      fontWeight: 'medium',
    });
    expect((first as MarketRow).style?.changeWidth).toBe(80);
  });
});

describe('row container and text layout contract', () => {
  const style = {
    container: {
      height: 120.5,
      backgroundColor: '#12345678',
      opacity: 0.8,
      cornerRadius: 9,
      borderWidth: 1.5,
      borderColor: '#654321',
      contentVerticalAlignment: 'top' as const,
    },
    title: {
      lines: 3 as const,
      truncate: 'clip' as const,
      alignment: 'end' as const,
      verticalAlignment: 'bottom' as const,
      offsetY: -2,
    },
  };

  it('round-trips the same nested surface through snapshots and whole-style replacement patches', () => {
    const base = {
      ...row('text-layout'),
      height: 80,
      backgroundColor: '#FFFFFF',
      opacity: 0.4,
      style,
    };
    expect(
      JSON.parse(serializeSnapshot(snapshot([base]))).rows[0].style
    ).toEqual(style);
    expect(
      validatePatches([{ key: base.key, type: 'identity', changes: { style } }])
    ).toHaveLength(1);
    const cleared = applyRowPatches(snapshot([base]), [
      { key: base.key, type: 'identity', changes: { style: {} } },
    ]);
    expect(cleared.rows[0]).toMatchObject({
      style: {},
      height: 80,
      backgroundColor: '#FFFFFF',
      opacity: 0.4,
    });
  });

  it.each([
    ['container', { width: 120 }],
    ['container', { height: -1 }],
    ['container', { height: 4097 }],
    ['container', { height: NaN }],
    ['container', { height: Infinity }],
    ['container', { opacity: 1.1 }],
    ['container', { borderWidth: -1 }],
    ['container', { backgroundColor: 'red' }],
    ['container', { contentVerticalAlignment: 'stretch' }],
    ['title', { lines: 4 }],
    ['title', { lines: 1.5 }],
    ['title', { truncate: 'middle' }],
    ['title', { verticalAlignment: 'baseline' }],
    ['title', { offsetY: 9 }],
  ])('rejects invalid %s on snapshot and patch paths', (field, value) => {
    const invalid = { [field as string]: value };
    expect(() =>
      validateSnapshot(
        snapshot([{ ...row('invalid'), style: invalid } as IdentityRow])
      )
    ).toThrow('style');
    expect(() =>
      validatePatches([
        {
          key: 'invalid',
          type: 'identity',
          changes: { style: invalid },
        } as RowPatch,
      ])
    ).toThrow('style');
  });
});

describe('declared nested style and line-limit boundaries', () => {
  it.each([
    'color',
    'lines',
    'truncate',
    'alignment',
    'verticalAlignment',
    'offsetY',
    'token',
  ])(
    'rejects undeclared Market badge property %s in snapshots and patches',
    (key) => {
      const values: Record<string, unknown> = {
        color: '#123456',
        lines: 2,
        truncate: 'clip',
        alignment: 'end',
        verticalAlignment: 'top',
        offsetY: 1,
        token: '$bodySm',
      };
      const badges = [
        { key: 'badge', text: 'Badge', style: { [key]: values[key] } },
      ];
      expect(() =>
        validateSnapshot(snapshot([{ ...marketRow(), badges } as MarketRow]))
      ).toThrow('Market badge style key');
      expect(() =>
        validatePatches([
          { key: 'market', type: 'market', changes: { badges } } as RowPatch,
        ])
      ).toThrow('Market badge style key');
    }
  );
  it.each([1.5, NaN, Infinity])(
    'rejects non-integral message bodyLines %s on both update paths',
    (bodyLines) => {
      const message = {
        type: 'message',
        key: 'message',
        title: 'Title',
        body: 'Body',
        time: 'Now',
        bodyLines,
      } as RowModel;
      expect(() => validateSnapshot(snapshot([message]))).toThrow('bodyLines');
      expect(() =>
        validatePatches([
          {
            key: 'message',
            type: 'message',
            changes: { bodyLines },
          } as RowPatch,
        ])
      ).toThrow('bodyLines');
    }
  );
});

describe('Web Market table presentation contract', () => {
  const tableRow = (key = 'table'): MarketRow => ({
    ...marketRow(key),
    webTable: {
      columns: [{ key: 'price', width: 120 }],
      cells: [{ key: 'price', kind: 'text', primary: [{ text: '$42' }] }],
    },
  });

  it('accepts complete Web tables without adding fields to ordinary Market rows', () => {
    const ordinary = marketRow();
    expect(ordinary.webTable).toBeUndefined();
    expect(() =>
      validateSnapshot(snapshot([ordinary, tableRow()]))
    ).not.toThrow();
    expect(serializeSnapshot(snapshot([ordinary]))).not.toContain('webTable');
  });

  it('rejects mismatched columns, non-finite widths, and undeclared cell fields', () => {
    const base = tableRow();
    const invalid = [
      { columns: [{ key: 'other' }], cells: base.webTable!.cells },
      {
        columns: [{ key: 'price', width: Infinity }],
        cells: base.webTable!.cells,
      },
      {
        columns: base.webTable!.columns,
        cells: [
          {
            key: 'price',
            kind: 'text',
            primary: [{ text: '$42' }],
            renderItem: 'unsupported',
          },
        ],
      },
    ];
    for (const webTable of invalid) {
      expect(() =>
        validateSnapshot({
          ...snapshot(),
          rows: [{ ...base, webTable }],
        } as NativeListSnapshot)
      ).toThrow();
    }
  });

  it('rejects the entire late-invalid table patch batch', () => {
    const initial = snapshot([tableRow('a'), tableRow('b')]);
    const before = serializeSnapshot(initial);
    expect(() =>
      applyRowPatches(initial, [
        {
          type: 'market',
          key: 'a',
          changes: {
            price: '$43',
            webTable: {
              columns: [{ key: 'price', width: 120 }],
              cells: [
                { key: 'price', kind: 'text', primary: [{ text: '$43' }] },
              ],
            },
          },
        },
        { type: 'market', key: 'b', changes: { price: '$44' } },
      ])
    ).toThrow();
    expect(serializeSnapshot(initial)).toBe(before);
  });

  it('validates every ordered fallback URI without truncating the chain', () => {
    const base = marketRow();
    const leading = {
      kind: 'token' as const,
      image: {
        uri: 'https://example.com/first.png',
        width: 32,
        height: 32,
        fallbackUris: [
          'https://example.com/second.png',
          'https://example.com/third.png',
        ],
      },
    };
    const serialized = serializeSnapshot(snapshot([{ ...base, leading }]));
    const decoded = JSON.parse(serialized) as { rows: readonly MarketRow[] };
    const decodedLeading = decoded.rows[0]!.leading;
    if (decodedLeading.kind !== 'token')
      throw new Error('Expected token leading');
    expect(decodedLeading.image?.fallbackUris).toEqual(
      leading.image.fallbackUris
    );
    expect(() =>
      validateSnapshot(
        snapshot([
          {
            ...base,
            leading: {
              ...leading,
              image: { ...leading.image, fallbackUris: ['valid', 3] },
            },
          },
        ] as unknown as readonly RowModel[])
      )
    ).toThrow();
  });
});
