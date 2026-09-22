import { useMemo, useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import {
  NativeList,
  type NativeListSnapshot,
  type NativeListTheme,
  type RowModel,
} from '@onekeyfe/react-native-native-list';

// docs/STYLE_SPEC.md section 3.1 names; the list keys are the legacy aliases.
const THEME: NativeListTheme = {
  background: '#F5F5F5',
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
  inverseBackground: '#202020',
  inverseText: '#FCFCFC',
  info: '#0D74CE',
};

const header = (key: string, title: string, note: string): RowModel => ({
  type: 'sectionHeader',
  key: `header-${key}`,
  sectionKey: key,
  title,
  value: note,
});

/**
 * Each template appears twice: once untouched, once styled. With the toggle off
 * every row is untouched, so the whole page should be pixel-identical to the
 * build before the row style existed - that is the regression check.
 */
function buildRows(styled: boolean): RowModel[] {
  const rows: RowModel[] = [];

  rows.push(header('identity', 'identity', 'plain / styled'));
  const identity = {
    type: 'identity',
    sectionKey: 'identity',
    leading: { kind: 'icon', name: 'StarOutline' },
    title: 'Bitcoin',
    subtitle: 'BTC · Bitcoin',
    badges: [{ key: 'tag', text: 'Native' }],
    trailing: [{ kind: 'value' as const, text: '$64,230' }],
  } as const;
  rows.push({ ...identity, key: 'identity-plain' });
  rows.push({
    ...identity,
    key: 'identity-styled',
    ...(styled
      ? {
          style: {
            // A named step from the application's scale, resolved to numbers in
            // JavaScript before the snapshot is serialized.
            container: {
              backgroundColor: '#EDF6FF',
              borderWidth: 1,
              borderColor: '#8DB7E4',
              cornerRadius: 12,
              contentVerticalAlignment: 'top' as const,
            },
            title: {
              token: '$bodyMd' as const,
              lines: 1 as const,
              truncate: 'tail' as const,
              offsetY: -1,
            },
            subtitle: { fontSize: 12, lineHeight: 16, color: '#8D8D8D' },
            badge: { fontSize: 10 },
            value: { token: '$bodySm' as const },
            horizontalPadding: 20,
            lineGap: 2,
            leadingGap: 16,
            titleBadgeGap: 6,
            image: { width: 32, height: 32, shape: 'rounded' as const },
          },
        }
      : {}),
  });

  // The mapping is not one to one: metricCard draws `value` through the view
  // identity uses for `title`. Styling `value` must hit the large number.
  rows.push(header('metric', 'metricCard', 'value vs title'));
  const metric = {
    type: 'metricCard',
    sectionKey: 'metric',
    title: 'Volume',
    value: '$1.24B',
    subtitle: '24h',
    trend: '+2.4%',
    trendTone: 'positive',
  } as const;
  rows.push({ ...metric, key: 'metric-plain' });
  rows.push({
    ...metric,
    key: 'metric-styled',
    ...(styled
      ? {
          style: {
            title: { fontSize: 10, color: '#8D8D8D' },
            value: { token: '$headingLg' as const, color: '#108303' },
            trend: { fontSize: 11 },
          },
        }
      : {}),
  });

  rows.push(header('message', 'message', 'body / time'));
  const message = {
    type: 'message',
    sectionKey: 'message',
    title: 'Transfer confirmed',
    body: 'Your transfer of 0.5 BTC has been confirmed on-chain.',
    bodyLines: 2,
    time: '2m',
  } as const;
  rows.push({ ...message, key: 'message-plain' });
  rows.push({
    ...message,
    key: 'message-styled',
    ...(styled
      ? {
          style: {
            title: { token: '$bodyMd' as const },
            body: {
              fontSize: 12,
              lineHeight: 16,
              lines: 3 as const,
              truncate: 'clip' as const,
              verticalAlignment: 'top' as const,
            },
            container: { backgroundColor: '#FFF8E7', cornerRadius: 8 },
            time: { fontSize: 10, color: '#8D8D8D' },
          },
        }
      : {}),
  });

  rows.push(header('rail', 'rail', 'title / status / badge'));
  const rail = {
    type: 'rail',
    sectionKey: 'rail',
    visual: { kind: 'icon', name: 'StarOutline' },
    title: 'ETH',
    status: 'online',
    badge: { key: 'change', text: '+1.2%', tone: 'success' },
  } as const;
  rows.push({ ...rail, key: 'rail-plain' });
  rows.push({
    ...rail,
    key: 'rail-styled',
    ...(styled
      ? { style: { title: { fontSize: 11 }, status: { fontSize: 10 } } }
      : {}),
  });

  rows.push(header('action', 'action', 'title'));
  const action = {
    type: 'action',
    sectionKey: 'action',
    title: 'Add custom token',
    actionKey: 'demo.add',
    tone: 'primary',
  } as const;
  rows.push({ ...action, key: 'action-plain' });
  rows.push({
    ...action,
    key: 'action-styled',
    ...(styled
      ? { style: { title: { token: '$bodyMd' as const, color: '#0D74CE' } } }
      : {}),
  });

  // Cover remaining templates with local text/image/gap overrides. Outer row
  // allocation stays fixed; callers choose fitting dimensions.
  const additional: readonly [RowModel, RowModel['style']][] = [
    [
      {
        type: 'activity',
        key: 'activity',
        leading: { kind: 'icon', name: 'StarOutline' },
        title: 'Sent Bitcoin',
        description: 'Confirmed',
        primaryAmount: '-0.5 BTC',
        secondaryAmount: '$32,115',
      },
      { title: { color: '#0D74CE' }, primaryAmount: { color: '#CE2C31' } },
    ],
    [
      {
        type: 'dataRow',
        key: 'data',
        index: 1,
        columns: [
          { key: 'asset', text: 'Bitcoin', secondaryText: 'BTC' },
          { key: 'value', text: '$64,230', alignment: 'end' },
        ],
      },
      {
        index: { color: '#0D74CE' },
        columns: { fontSize: 16, color: '#0D74CE' },
        columnSecondary: { fontSize: 12, color: '#108303' },
        lineGap: 4,
      },
    ],
    [
      {
        type: 'market',
        key: 'market',
        variant: 'token',
        leading: { kind: 'token', fallbackText: 'BTC' },
        title: 'Bitcoin',
        subtitle: 'BTC',
        price: '$64,230',
        change: { text: '+2.4%', tone: 'positive' },
      },
      { title: { color: '#0D74CE' }, price: { color: '#108303' } },
    ],
    [
      {
        type: 'mediaTile',
        key: 'media',
        variant: 'gallery',
        imageState: 'empty',
        title: 'Collectible',
        subtitle: 'Ethereum',
      },
      {
        title: { color: '#0D74CE' },
        subtitle: { color: '#108303' },
        image: { width: 120, height: 80, shape: 'rounded' },
        leadingGap: 8,
        lineGap: 4,
      },
    ],
    [
      {
        type: 'sectionHeader',
        key: 'section',
        sectionKey: 'section',
        title: 'Section heading',
        subtitle: 'Shared list structure',
      },
      { title: { color: '#0D74CE' }, subtitle: { color: '#108303' } },
    ],
    [
      {
        type: 'system',
        key: 'system',
        variant: 'warning',
        title: 'Connection unavailable',
        message: 'Please try again later.',
      },
      { title: { color: '#CE2C31' }, message: { color: '#0D74CE' } },
    ],
  ];
  additional.forEach(([base, style]) => {
    rows.push(header(base.key, base.type, 'plain / styled'));
    rows.push({ ...base, key: `${base.key}-plain` });
    rows.push({
      ...base,
      key: `${base.key}-styled`,
      ...(styled ? { style } : {}),
    } as RowModel);
  });

  rows.push(header('wallet-group', 'walletGroup', 'member text styles'));
  for (const variant of ['plain', 'styled'] as const) {
    const key = `wallet-group-${variant}`;
    const memberStyle =
      styled && variant === 'styled'
        ? { title: { color: '#0D74CE' } }
        : undefined;
    rows.push({
      type: 'walletGroup',
      key,
      parent: {
        type: 'identity',
        key,
        presentation: 'walletSidebar',
        leading: { kind: 'wallet', fallbackText: 'A' },
        title: 'Wallet A',
        ...(memberStyle ? { style: memberStyle } : {}),
      },
      children: [
        {
          type: 'identity',
          key: `${key}-child`,
          presentation: 'walletSidebar',
          leading: { kind: 'wallet', fallbackText: 'B' },
          title: 'Unstyled child',
        },
      ],
    });
  }

  // listStyle is chrome, not a row: the separator inset and the group card
  // radius below come from the snapshot, not from these rows.
  rows.push(header('chrome', 'listStyle', 'separator / group'));
  rows.push({
    type: 'identity',
    key: 'chrome-separator',
    sectionKey: 'chrome',
    leading: { kind: 'icon', name: 'StarOutline' },
    title: 'Separator inset',
    subtitle: 'Inset comes from listStyle.separator',
    separator: true,
  });
  rows.push({
    type: 'identity',
    key: 'chrome-group-first',
    sectionKey: 'chrome',
    groupId: 'chrome-card',
    groupPosition: 'first',
    leading: { kind: 'icon', name: 'StarOutline' },
    title: 'Grouped card, first',
  });
  rows.push({
    type: 'identity',
    key: 'chrome-group-last',
    sectionKey: 'chrome',
    groupId: 'chrome-card',
    groupPosition: 'last',
    leading: { kind: 'icon', name: 'StarOutline' },
    title: 'Grouped card, last',
  });

  // Growing text needs an explicit height: row heights are not derived from the
  // style. See docs/STYLE_SPEC.md section 6.1.
  rows.push(header('height', 'explicit height', 'larger text'));
  rows.push({
    type: 'identity',
    key: 'identity-grown',
    sectionKey: 'height',
    leading: { kind: 'icon', name: 'StarOutline' },
    title: 'Larger title',
    subtitle: 'Row height is given, not inferred',
    ...(styled
      ? {
          height: 76,
          style: {
            title: { token: '$headingMd' as const },
            subtitle: { token: '$bodyMd' as const },
            verticalPadding: 12,
          },
        }
      : {}),
  });

  rows.push(
    header('text-layout', 'Text layout / row container', 'same allocation'),
  );
  for (const alignment of ['top', 'center', 'bottom'] as const) {
    rows.push({
      type: 'message',
      key: `text-layout-${alignment}`,
      sectionKey: 'text-layout',
      height: 180,
      title: `${alignment}: single line\nwith an explicit break`,
      body: 'First line\nSecond line\nThird line\nFourth line is truncated',
      time: '12:00',
      ...(styled
        ? {
            style: {
              horizontalPadding: 18,
              verticalPadding: 12,
              lineGap: 6,
              container: {
                backgroundColor: '#EDF6FF',
                opacity: 0.9,
                cornerRadius: 14,
                borderWidth: 2,
                borderColor: '#8DB7E4',
                contentVerticalAlignment: alignment,
              },
              title: {
                lines: 1 as const,
                truncate: 'tail' as const,
                alignment: 'center' as const,
              },
              body: {
                lines: 3 as const,
                truncate:
                  alignment === 'bottom'
                    ? ('clip' as const)
                    : ('tail' as const),
                lineHeight: 20,
              },
            },
          }
        : {}),
    });
  }

  return rows;
}

export function NativeListRowStylePage() {
  const [styled, setStyled] = useState(true);
  const snapshot = useMemo<NativeListSnapshot>(
    () => ({
      schemaVersion: 1,
      generation: styled ? 2 : 1,
      layout: { kind: 'sectioned', stickyHeaders: true, contentPadding: 8 },
      theme: THEME,
      // Absent by default, so the chrome falls back to each platform's own
      // numbers - which are not the same everywhere, see STYLE_SPEC section 6.2.
      listStyle: styled
        ? { separator: { inset: 20 }, groupCornerRadius: 16 }
        : undefined,
      rows: buildRows(styled),
    }),
    [styled],
  );

  return (
    <View style={styles.container}>
      <View style={styles.bar}>
        <Text style={styles.caption}>
          Every template appears twice: plain, then styled. Turn the style off
          and the page should match the build before the row style existed.
        </Text>
        <Pressable
          accessibilityRole="button"
          testID="native-list-row-style-toggle"
          onPress={() => setStyled(current => !current)}
          style={({ pressed }) => [styles.button, pressed && styles.pressed]}
        >
          <Text style={styles.buttonLabel}>
            {styled ? 'Style: on' : 'Style: off'}
          </Text>
        </Pressable>
      </View>
      <NativeList style={styles.list} snapshot={snapshot} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F5F5F5' },
  bar: {
    paddingHorizontal: 16,
    paddingTop: 12,
    paddingBottom: 10,
    gap: 10,
    backgroundColor: '#FFFFFF',
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#E0E0E0',
  },
  caption: { fontSize: 13, lineHeight: 18, color: '#646464' },
  button: {
    alignSelf: 'flex-start',
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 8,
    backgroundColor: '#202020',
  },
  pressed: { opacity: 0.8 },
  buttonLabel: { color: '#FCFCFC', fontSize: 14, fontWeight: '600' },
  list: { flex: 1 },
});
