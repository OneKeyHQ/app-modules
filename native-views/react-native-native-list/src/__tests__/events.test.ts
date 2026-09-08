import type {
  ActionAnchorInvalidatedEvent,
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
      | ActionAnchorInvalidatedEvent
      | SelectionDeltaEvent
      | ReorderEvent
      | EndReachedEvent
      | VisibleRangeChangedEvent
    )[] = [
      {
        rowKey: 'btc',
        actionKey: 'open',
        anchor: {
          token: 'list:4:1:7',
          windowRect: { x: 10, y: 20, width: 36, height: 36 },
          source: 'trailingAccessory',
          slot: 0,
          generation: 4,
          layoutDirection: 'ltr',
        },
      },
      { token: 'list:4:1:7', reason: 'scroll' },
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
