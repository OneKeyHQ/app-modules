import { useMemo, useState } from 'react';
import {
  Image,
  Pressable,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import {
  NativeList,
  type ReorderEvent,
  type RowActionEvent,
} from '@onekeyfe/react-native-native-list';

import {
  OK_62492_FOCUSED_WALLET_ID,
  OK_62492_WALLETS,
  WALLET_SIDEBAR_LIGHT_THEME,
  buildWalletSidebarSnapshot,
  parseHiddenWalletActionKey,
  reorderWalletSidebarWallets,
  walletSidebarAvatarUri,
} from './nativeListWalletSidebarFixture';

// app-monorepo AccountSelectorWalletListSideBarV2 uses w="$24" on phones.
const SIDEBAR_WIDTH = 96;
const MAX_EVENT_LINES = 12;

export function NativeListWalletSidebarReorderPage() {
  const navigation = useNavigation();
  const insets = useSafeAreaInsets();
  const [wallets, setWallets] = useState(OK_62492_WALLETS);
  const [focusedWalletId, setFocusedWalletId] = useState<string>(
    OK_62492_FOCUSED_WALLET_ID,
  );
  const [listInstance, setListInstance] = useState(0);
  const [events, setEvents] = useState<readonly string[]>([]);
  const snapshot = useMemo(
    () => buildWalletSidebarSnapshot({ wallets, focusedWalletId }),
    [focusedWalletId, wallets],
  );
  const focusedWallet = wallets.find(wallet => wallet.id === focusedWalletId);

  const appendEvent = (line: string) => {
    console.info('[NativeListWalletSidebarReorder]', line);
    setEvents(previous => [line, ...previous].slice(0, MAX_EVENT_LINES));
  };

  const handleRowAction = (event: RowActionEvent) => {
    if (!event.rowKey || event.actionKey !== 'press') return;
    const hiddenWalletParentId = parseHiddenWalletActionKey(event.rowKey);
    if (hiddenWalletParentId) {
      appendEvent(`add hidden wallet · ${hiddenWalletParentId}`);
      return;
    }
    if (wallets.some(wallet => wallet.id === event.rowKey)) {
      setFocusedWalletId(event.rowKey);
    }
  };

  const handleReorder = (event: ReorderEvent) => {
    appendEvent(`reorder ${event.key} → ${event.toIndex}`);
    setWallets(previous => reorderWalletSidebarWallets(previous, event));
  };

  const resetFixture = () => {
    setWallets(OK_62492_WALLETS);
    setFocusedWalletId(OK_62492_FOCUSED_WALLET_ID);
    setEvents([]);
    // Remount so native reorder state and scroll offset match the recording again.
    setListInstance(instance => instance + 1);
  };

  return (
    <View style={styles.screen}>
      <StatusBar
        barStyle="dark-content"
        backgroundColor="transparent"
        translucent
      />
      <View style={[styles.sheet, { marginTop: insets.top }]}>
        <View style={styles.sidebar}>
          <NativeList
            key={listInstance}
            testID="wallet-sidebar-reorder-list"
            style={styles.list}
            snapshot={snapshot}
            onRowAction={handleRowAction}
            onReorder={handleReorder}
          />
          <View
            style={[
              styles.sidebarFooter,
              { marginBottom: Math.max(insets.bottom, 8) },
            ]}
          >
            <View style={styles.createWallet}>
              <Pressable
                accessibilityRole="button"
                accessibilityLabel="Add wallet"
                testID="wallet-sidebar-reorder-add-wallet"
                onPress={() => appendEvent('add wallet')}
                style={({ pressed }) => [
                  styles.createWalletButton,
                  pressed && styles.pressed,
                ]}
              >
                <Text style={styles.createWalletIcon}>+</Text>
              </Pressable>
              <Text style={styles.createWalletLabel}>Wallet</Text>
            </View>
          </View>
        </View>

        <View style={styles.details}>
          <View style={styles.header}>
            {focusedWallet ? (
              <Image
                accessibilityIgnoresInvertColors
                source={{ uri: walletSidebarAvatarUri(focusedWallet.avatar) }}
                style={styles.headerAvatar}
              />
            ) : null}
            <Text numberOfLines={1} style={styles.headerTitle}>
              {focusedWallet?.name ?? '—'}
            </Text>
            <View style={styles.headerSpacer} />
            <Pressable
              accessibilityRole="button"
              testID="wallet-sidebar-reorder-back"
              onPress={() => navigation.goBack()}
              style={({ pressed }) => [
                styles.textButton,
                pressed && styles.pressed,
              ]}
            >
              <Text style={styles.textButtonLabel}>Back</Text>
            </Pressable>
          </View>
          <Text style={styles.caption}>
            OK-62492: long-press “abandon”, drag it slowly up past “然 1s” and
            the QR wallet, then back down.
          </Text>
          <Pressable
            accessibilityRole="button"
            testID="wallet-sidebar-reorder-reset"
            onPress={resetFixture}
            style={({ pressed }) => [
              styles.resetButton,
              pressed && styles.pressed,
            ]}
          >
            <Text style={styles.resetButtonLabel}>Reset recording order</Text>
          </Pressable>
          <ScrollView
            style={styles.log}
            contentContainerStyle={styles.logContent}
          >
            <Text style={styles.sectionTitle}>Order</Text>
            {wallets.map((wallet, index) => (
              <Text key={wallet.id} numberOfLines={1} style={styles.logLine}>
                {`${index + 1}. ${wallet.name}${
                  wallet.showAddHiddenWalletButton ||
                  (wallet.hiddenWallets?.length ?? 0) > 0
                    ? '  [group]'
                    : ''
                }`}
              </Text>
            ))}
            <Text style={styles.sectionTitle}>Events</Text>
            {events.map((line, index) => (
              <Text key={`${index}-${line}`} style={styles.logLine}>
                {line}
              </Text>
            ))}
          </ScrollView>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: '#CFCFCF' },
  sheet: {
    flex: 1,
    flexDirection: 'row',
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 12,
    borderTopRightRadius: 12,
    borderCurve: 'continuous',
    overflow: 'hidden',
  },
  sidebar: {
    width: SIDEBAR_WIDTH,
    backgroundColor: WALLET_SIDEBAR_LIGHT_THEME.background,
    borderRightWidth: StyleSheet.hairlineWidth,
    borderRightColor: '#0000000f',
  },
  list: { flex: 1 },
  sidebarFooter: {
    padding: 8,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: WALLET_SIDEBAR_LIGHT_THEME.separator,
  },
  createWallet: { padding: 4, alignItems: 'center' },
  createWalletButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#202020',
  },
  createWalletIcon: {
    color: '#FFFFFF',
    fontSize: 26,
    lineHeight: 28,
  },
  createWalletLabel: {
    marginTop: 4,
    color: '#000000df',
    fontSize: 12,
    lineHeight: 16,
    textAlign: 'center',
  },
  details: { flex: 1, backgroundColor: '#FFFFFF' },
  header: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    paddingLeft: 20,
    paddingRight: 12,
    gap: 8,
  },
  headerAvatar: { width: 32, height: 32 },
  headerTitle: {
    maxWidth: 140,
    color: '#000000df',
    fontSize: 16,
    fontWeight: '600',
  },
  headerSpacer: { flex: 1 },
  textButton: { paddingHorizontal: 8, paddingVertical: 6 },
  textButtonLabel: { color: '#006dcbf2', fontSize: 15 },
  caption: {
    marginHorizontal: 20,
    color: '#0000009b',
    fontSize: 13,
    lineHeight: 18,
  },
  resetButton: {
    alignSelf: 'flex-start',
    marginTop: 12,
    marginHorizontal: 20,
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 16,
    backgroundColor: '#0000000f',
  },
  resetButtonLabel: { color: '#000000df', fontSize: 13, fontWeight: '600' },
  log: { flex: 1, marginTop: 12 },
  logContent: { paddingHorizontal: 20, paddingBottom: 24 },
  sectionTitle: {
    marginTop: 8,
    marginBottom: 4,
    color: '#000000df',
    fontSize: 13,
    fontWeight: '600',
  },
  logLine: {
    color: '#0000009b',
    fontSize: 12,
    lineHeight: 17,
    fontVariant: ['tabular-nums'],
  },
  pressed: { opacity: 0.6 },
});
