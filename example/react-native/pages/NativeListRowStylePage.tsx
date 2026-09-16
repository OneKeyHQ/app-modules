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
            title: { token: '$bodyMd' as const },
            subtitle: { fontSize: 12, lineHeight: 16, color: '#8D8D8D' },
            badge: { fontSize: 10 },
            value: { token: '$bodySm' as const },
            horizontalPadding: 20,
            lineGap: 2,
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
            body: { fontSize: 12, lineHeight: 16 },
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
