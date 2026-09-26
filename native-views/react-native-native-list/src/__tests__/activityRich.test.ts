import type { ActivityRow, NativeListSnapshot } from '../models';
import { validatePatches, validateSnapshot } from '../validation';

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
