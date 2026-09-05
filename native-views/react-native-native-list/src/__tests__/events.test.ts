import type {
  EndReachedEvent,
  ReorderEvent,
  RowActionEvent,
  SelectionDeltaEvent,
  VisibleRangeChangedEvent,
} from '../models';

describe('NativeList event payloads', () => {
  it('remain plain, stable, key-based data', () => {
    const events: readonly (
      | RowActionEvent
      | SelectionDeltaEvent
      | ReorderEvent
      | EndReachedEvent
      | VisibleRangeChangedEvent
    )[] = [
      { rowKey: 'btc', actionKey: 'open' },
      { addedKeys: ['btc'], removedKeys: [], source: 'row', sourceKey: 'btc' },
      { key: 'wallet-2', fromIndex: 2, toIndex: 0, afterKey: 'wallet-1' },
      { generation: 4, lastKey: 'last' },
      { firstKey: 'a', lastKey: 'z', firstIndex: 0, lastIndex: 25 },
    ];
    const encoded = JSON.stringify(events);
    expect(JSON.parse(encoded)).toEqual(events);
    expect(encoded).not.toContain('function');
  });
});
