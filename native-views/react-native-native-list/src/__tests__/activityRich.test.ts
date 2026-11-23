import type { ActivityRow, NativeListSnapshot } from '../models';
import {
  applyOwnedNativePatches,
  validatePatches,
  validateSnapshot,
} from '../validation';

const row: ActivityRow = {
  type: 'activity',
  key: 'record',
  title: 'Transfer',
  leading: { kind: 'icon', name: 'coin' },
};
const snapshot = (value: ActivityRow): NativeListSnapshot => ({
  schemaVersion: 1,
  generation: 0,
  layout: { kind: 'linear' },
  rows: [value],
});

test('legacy amounts remain valid and rich lines preserve explicit segments', () => {
  expect(() =>
    validateSnapshot(
      snapshot({ ...row, primaryAmount: '+1 ETH', secondaryAmount: '$2' })
    )
  ).not.toThrow();
  expect(() =>
    validateSnapshot(
      snapshot({
        ...row,
        presentation: 'table',
        amounts: [
          {
            key: 'receive',
            text: '+0.012 ETH',
            textSegments: [
              { text: '+0.0' },
              { text: '12', style: 'subscript' },
              { text: ' ETH' },
            ],
            tone: 'positive',
          },
        ],
        fee: { label: 'Fee', primary: '0.001 ETH', hidden: true },
        descriptionActionKey: 'open-address',
      })
    )
  ).not.toThrow();
});

test('rich amount limit is 32 without silent truncation', () => {
  const amounts = Array.from({ length: 32 }, (_, index) => ({
    key: String(index),
    text: '1 token',
  }));
  expect(() => validateSnapshot(snapshot({ ...row, amounts }))).not.toThrow();
  expect(() =>
    validateSnapshot(
      snapshot({
        ...row,
        amounts: [...amounts, { key: 'overflow', text: '1 token' }],
      })
    )
  ).toThrow(/32/);
});

test('duplicate amount identity is rejected for snapshot and patch', () => {
  const amounts = [
    { key: 'same', text: '1' },
    { key: 'same', text: '2' },
  ];
  expect(() => validateSnapshot(snapshot({ ...row, amounts }))).toThrow(
    /duplicate amount/
  );
  expect(() =>
    validatePatches([{ type: 'activity', key: row.key, changes: { amounts } }])
  ).toThrow(/duplicate amount/);
});

test('activity badges have bounded unique identities', () => {
  const badges = Array.from({ length: 4 }, (_, index) => ({
    key: String(index),
    text: 'Status',
  }));
  expect(() => validateSnapshot(snapshot({ ...row, badges }))).not.toThrow();
  expect(() =>
    validateSnapshot(
      snapshot({ ...row, badges: [...badges, { key: 'extra', text: 'Extra' }] })
    )
  ).toThrow(/badges/);
  expect(() =>
    validateSnapshot(snapshot({ ...row, badges: [badges[0]!, badges[0]!] }))
  ).toThrow(/duplicate badge/);
});

test('rich data fields are not style keys; activity accepts titleBadgeGap', () => {
  for (const key of [
    'amounts',
    'badges',
    'presentation',
    'fee',
    'descriptionActionKey',
  ]) {
    expect(() =>
      validateSnapshot(
        snapshot({
          ...row,
          style: { [key]: { fontSize: 12 } },
        } as unknown as ActivityRow)
      )
    ).toThrow(/is not a style key/);
  }
  expect(() =>
    validateSnapshot(snapshot({ ...row, style: { titleBadgeGap: 6 } }))
  ).not.toThrow();
  expect(() =>
    validateSnapshot({
      ...snapshot(row),
      rows: [
        {
          type: 'sectionHeader',
          key: 'header',
          title: 'Today',
          style: { titleLoading: { fontSize: 12 } },
        } as unknown as ActivityRow,
      ],
    })
  ).toThrow(/is not a style key/);
});

describe('descriptionActionStart', () => {
  // The caller that assembles the description states where its action text
  // starts. NativeList never reads that out of the displayed string.
  const labelled: ActivityRow = {
    ...row,
    amounts: [{ key: 'send', text: '-1 ETH' }],
    description: '08:48 • Router • V2',
    descriptionActionKey: 'open-address',
  };
  const patch = (changes: Record<string, unknown>) =>
    [{ type: 'activity', key: row.key, changes }] as never;

  test('accepts an offset anywhere in the description, both ends included', () => {
    for (const descriptionActionStart of [0, 8, labelled.description!.length])
      expect(() =>
        validateSnapshot(snapshot({ ...labelled, descriptionActionStart }))
      ).not.toThrow();
    // UTF-16 code units, as `String.prototype.length` counts them.
    const prefix = '🕘 08:48 • ';
    expect(prefix.length).toBe(11);
    expect(() =>
      validateSnapshot(
        snapshot({
          ...labelled,
          description: `${prefix}Router`,
          descriptionActionStart: prefix.length,
        })
      )
    ).not.toThrow();
  });

  test('rejects an offset that is not an integer inside the description', () => {
    for (const descriptionActionStart of [
      -1,
      1.5,
      '8',
      labelled.description!.length + 1,
    ])
      expect(() =>
        validateSnapshot(
          snapshot({ ...labelled, descriptionActionStart } as ActivityRow)
        )
      ).toThrow(/descriptionActionStart/);
    // Without a description there is nothing to offset into.
    expect(() =>
      validateSnapshot(snapshot({ ...row, descriptionActionStart: 1 }))
    ).toThrow(/descriptionActionStart/);
    expect(() =>
      validatePatches(patch({ descriptionActionStart: -1 }))
    ).toThrow(/descriptionActionStart/);
  });

  test('a patch that replaces the description resets an offset it does not restate', () => {
    const initial = snapshot({ ...labelled, descriptionActionStart: 8 });
    const renamed = applyOwnedNativePatches(
      initial,
      patch({ description: 'Router • V2' })
    );
    expect(renamed.snapshot.rows[0]).toMatchObject({
      description: 'Router • V2',
    });
    expect(renamed.snapshot.rows[0]).not.toHaveProperty(
      'descriptionActionStart'
    );
    expect(JSON.parse(renamed.json)[0].changes).toEqual({
      description: 'Router • V2',
      descriptionActionStart: null,
    });

    const restated = applyOwnedNativePatches(
      initial,
      patch({ description: '09:10 • Router', descriptionActionStart: 8 })
    );
    expect(restated.snapshot.rows[0]).toMatchObject({
      description: '09:10 • Router',
      descriptionActionStart: 8,
    });

    // The same string again, or a patch of other fields, keeps the offset.
    const untouched = applyOwnedNativePatches(
      initial,
      patch({ description: labelled.description, title: 'Swap' })
    );
    expect(untouched.snapshot.rows[0]).toMatchObject({
      title: 'Swap',
      descriptionActionStart: 8,
    });
    expect(JSON.parse(untouched.json)[0].changes).not.toHaveProperty(
      'descriptionActionStart'
    );

    // Clearing the description clears the offset with it.
    const cleared = applyOwnedNativePatches(
      initial,
      patch({ description: null })
    );
    expect(cleared.snapshot.rows[0]).not.toHaveProperty('description');
    expect(cleared.snapshot.rows[0]).not.toHaveProperty(
      'descriptionActionStart'
    );

    // The merged row is checked: this offset is past the current description.
    expect(() =>
      applyOwnedNativePatches(initial, patch({ descriptionActionStart: 40 }))
    ).toThrow(/descriptionActionStart/);
  });

  test('production ignores a malformed offset instead of forwarding it', () => {
    const environment = globalThis as typeof globalThis & { __DEV__: boolean };
    const original = environment.__DEV__;
    environment.__DEV__ = false;
    try {
      for (const descriptionActionStart of [-1, 1.5, '8']) {
        const [accepted] = validateSnapshot(
          snapshot({ ...labelled, descriptionActionStart } as ActivityRow)
        ).rows;
        expect(accepted).toMatchObject({ description: labelled.description });
        expect(accepted).not.toHaveProperty('descriptionActionStart');
      }
      expect(
        validateSnapshot(snapshot({ ...labelled, descriptionActionStart: 8 }))
          .rows[0]
      ).toMatchObject({ descriptionActionStart: 8 });
      expect(
        validatePatches(patch({ title: 'Swap', descriptionActionStart: -1 }))[0]
          ?.changes
      ).toEqual({ title: 'Swap' });
    } finally {
      environment.__DEV__ = original;
    }
  });
});
