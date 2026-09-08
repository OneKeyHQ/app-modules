import type { IdentityRow, NativeListSnapshot } from '../models';
import {
  checkboxStateForKeys,
  checkboxStateForSection,
  reduceSelection,
  selectionStateFromSnapshot,
} from '../selection';

const rows: IdentityRow[] = [
  {
    type: 'identity',
    key: 'a1',
    sectionKey: 'A',
    leading: { kind: 'icon', name: 'a' },
    title: 'A1',
  },
  {
    type: 'identity',
    key: 'a2',
    sectionKey: 'A',
    leading: { kind: 'icon', name: 'a' },
    title: 'A2',
  },
  {
    type: 'identity',
    key: 'b1',
    sectionKey: 'B',
    leading: { kind: 'icon', name: 'b' },
    title: 'B1',
  },
  {
    type: 'identity',
    key: 'b2',
    sectionKey: 'B',
    disabled: true,
    leading: { kind: 'icon', name: 'b' },
    title: 'B2',
  },
];

const snapshot: NativeListSnapshot = {
  schemaVersion: 1,
  generation: 1,
  layout: { kind: 'sectioned' },
  rows,
  selection: { mode: 'multiple', selectedKeys: [] },
};

describe('NativeList native-style selection reducer', () => {
  it('returns one incremental row delta', () => {
    const result = reduceSelection(
      selectionStateFromSnapshot(snapshot),
      { scope: 'row', key: 'a1' },
      rows
    );
    expect(result.delta).toEqual({
      addedKeys: ['a1'],
      removedKeys: [],
      source: 'row',
      sourceKey: 'a1',
    });
  });

  it('selects and deselects a section in one operation', () => {
    const first = reduceSelection(
      selectionStateFromSnapshot(snapshot),
      { scope: 'section', sectionKey: 'A' },
      rows
    );
    expect([...first.state.selectedKeys]).toEqual(['a1', 'a2']);
    const second = reduceSelection(
      first.state,
      { scope: 'section', sectionKey: 'A' },
      rows
    );
    expect(second.delta.removedKeys).toEqual(['a1', 'a2']);
  });

  it('excludes disabled rows from global selection', () => {
    const result = reduceSelection(
      selectionStateFromSnapshot(snapshot),
      { scope: 'list' },
      rows
    );
    expect([...result.state.selectedKeys]).toEqual(['a1', 'a2', 'b1']);
  });

  it('derives three-state group and list checkboxes', () => {
    expect(checkboxStateForSection('A', rows, new Set())).toBe('unchecked');
    expect(checkboxStateForSection('A', rows, new Set(['a1']))).toBe(
      'indeterminate'
    );
    expect(checkboxStateForSection('A', rows, new Set(['a1', 'a2']))).toBe(
      'checked'
    );
    expect(checkboxStateForKeys(['a1', 'a2', 'b1'], new Set(['a1']))).toBe(
      'indeterminate'
    );
  });

  it('does not clear single selection for an invalid target', () => {
    const state = { mode: 'single' as const, selectedKeys: new Set(['a1']) };
    const result = reduceSelection(state, { scope: 'row', key: 'b2' }, rows);
    expect([...result.state.selectedKeys]).toEqual(['a1']);
    expect(result.delta).toEqual({
      addedKeys: [],
      removedKeys: [],
      source: 'row',
      sourceKey: 'b2',
    });
  });

  it('keeps only one key when replacing single selection', () => {
    const state = { mode: 'single' as const, selectedKeys: new Set<string>() };
    const result = reduceSelection(
      state,
      { scope: 'replace', selectedKeys: ['a1', 'a2'] },
      rows
    );
    expect([...result.state.selectedKeys]).toEqual(['a1']);
  });
});
