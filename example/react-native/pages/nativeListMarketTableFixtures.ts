import type {
  DataRow,
  MarketRow,
  NativeListTableColumn,
  RowModel,
} from '@onekeyfe/react-native-native-list';

const value = (
  key: string,
  text: string,
  secondaryText?: string,
): NativeListTableColumn => ({ key, text, secondaryText, alignment: 'end' });
const address = (key: string, text: string): NativeListTableColumn => ({
  key,
  text,
  actionKey: key,
  accessibilityLabel:
    key === 'creator' ? 'Open creator address' : 'Copy address',
});
const asset: NativeListTableColumn = {
  key: 'asset',
  text: 'Ethereum',
  secondaryText: 'ETH',
  leading: { kind: 'token', fallbackText: 'ETH' },
};
const price = value('price', '$3,174.28');
const change: NativeListTableColumn = {
  ...value('change', '+1.37%'),
  tone: 'positive',
};
const volume = value('volume', '$18.2M');
const cap = value('cap', '$381B');
const liquidity = value('liquidity', '$11.3M');
const favorite: NativeListTableColumn = {
  key: 'favorite',
  text: '☆',
  width: 32,
  actionKey: 'favorite',
  accessibilityLabel: 'Add to favorites',
};

export function marketTableFixtures(
  wide: boolean,
  styled: boolean,
): RowModel[] {
  const rows: RowModel[] = [];
  function section(key: string) {
    rows.push({
      type: 'sectionHeader',
      key: 'table-header-' + key,
      sectionKey: key,
      title: key,
    });
  }
  function detail(
    key: string,
    compact: NativeListTableColumn[],
    full: NativeListTableColumn[],
  ) {
    section(key);
    const row: DataRow = {
      type: 'dataRow',
      key: 'table-' + key,
      sectionKey: key,
      columns: compact.map(
        ({
          key: columnKey,
          text,
          secondaryText,
          tone,
          secondaryTone,
          alignment,
          textSegments,
          secondaryTextSegments,
        }) => ({
          key: columnKey,
          text,
          secondaryText,
          tone,
          secondaryTone,
          alignment,
          textSegments,
          secondaryTextSegments,
        }),
      ),
      tableColumns: wide ? full : compact,
      style: styled
        ? { horizontalPadding: 8, columns: { fontSize: 14 }, lineGap: 2 }
        : undefined,
    };
    rows.push(row);
  }
  const buy: NativeListTableColumn = {
    key: 'type',
    text: 'Buy',
    secondaryText: '2 min ago',
    tone: 'positive',
  };
  const amounts: NativeListTableColumn = {
    ...value('amount', '+1 ETH', '−3,174 USDC'),
    secondaryTone: 'negative',
  };
  const total = value('value', '$3,174');
  const holder = address('address', '0x11A2…CC90');
  detail(
    'Transactions',
    [
      { ...buy, leading: asset.leading },
      amounts,
      { ...total, secondaryText: price.text },
    ],
    [value('time', '2 min ago'), buy, amounts, total, price, holder],
  );
  detail(
    'Holders',
    [{ ...holder, secondaryText: 'Rank 1' }, value('share', '2.45%'), total],
    [
      value('rank', '1'),
      holder,
      value('share', '2.45%'),
      value('quantity', '1,520 ETH'),
      total,
    ],
  );
  const balance = value('balance', '$6,348.56', '2 ETH');
  const pnl = { ...value('pnl', '−$74.30'), tone: 'negative' } as const;
  detail(
    'My Position',
    [balance, pnl, value('totalPnl', '—')],
    [asset, balance, pnl, value('totalPnl', '—')],
  );
  const pool = {
    ...asset,
    text: 'ETH / USDC',
    secondaryText: 'Concentrated liquidity',
  };
  const fee = value('fee', '0.30%');
  detail(
    'Liquidity',
    [pool, liquidity, fee],
    [pool, liquidity, amounts, fee, holder, address('creator', '0xCc32…079D')],
  );
  const compactQuote = {
    ...price,
    secondaryText: change.text,
    secondaryTone: change.tone,
  };
  const selectors: [string, NativeListTableColumn[]][] = [
    ['Favorites', [price, change, volume]],
    ['Coins', [price, change, volume, cap]],
    ['Trending', [cap, price, change, liquidity, volume]],
    [
      'Search',
      [
        liquidity,
        {
          ...compactQuote,
          text: '$0.000042',
          textSegments: [
            { text: '$0.0' },
            { text: '4', style: 'subscript' },
            { text: '42' },
          ],
        },
        cap,
        volume,
      ],
    ],
    ['Stock', [price, change, cap, volume]],
  ];
  for (const [key, metrics] of selectors) {
    section(key);
    const identity =
      key === 'Stock'
        ? {
            ...asset,
            text: 'Example Corporation Class A',
            secondaryText: 'EXM',
            leading: { kind: 'token' as const, fallbackText: 'EXM' },
          }
        : asset;
    const row: MarketRow = {
      type: 'market',
      key: 'table-' + key,
      sectionKey: key,
      variant: key === 'Stock' ? 'stock' : 'token',
      title: identity.text,
      leading: identity.leading!,
      pressActionKey: 'select',
      price: price.text,
      change: { text: change.text, tone: 'positive' },
      tableColumns: [favorite, identity, ...(wide ? metrics : [compactQuote])],
      style: styled
        ? { horizontalPadding: 8, image: { width: 28, height: 28 } }
        : undefined,
    };
    rows.push(row);
  }
  section('List states');
  rows.push(
    {
      key: 'table-loading',
      sectionKey: 'List states',
      type: 'system',
      variant: 'loading',
      presentation: 'market',
      loadingStyle: 'skeleton',
    },
    {
      key: 'table-empty',
      sectionKey: 'List states',
      type: 'system',
      variant: 'noMatch',
      presentation: 'market',
      message: 'No positions',
    },
  );
  return rows;
}
