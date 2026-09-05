import { useEffect, useMemo, useRef, useState } from 'react';
import {
  Image,
  Pressable,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import {
  NativeList,
  type NativeListSnapshot,
  type NativeListTheme,
  type RowActionEvent,
} from '@onekeyfe/react-native-native-list';

import {
  ACCOUNT_SELECTOR_ACCOUNTS_PER_WALLET,
  ACCOUNT_SELECTOR_LOGICAL_ACCOUNT_COUNT,
  ACCOUNT_SELECTOR_WALLET_COUNT,
  AccountRowsCache,
  accountKey,
  buildVisibleAccountRows,
  buildWalletRows,
  parseAccountKey,
  parseWalletKey,
  walletAvatarUri,
  walletEmoji,
  walletKey,
  walletName,
} from './nativeListAccountSelectorData';

const DEFAULT_WALLET_INDEX = 2;
const iconUris = {
  pencil:
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAASKADAAQAAAABAAAASAAAAACQMUbvAAAB6klEQVR4Ae3YO27DMAwG4LhTpl4nN+ja0/UgGdtDdU03lyzMIAgq2rIepMVfgEAksmXpi+yYOp1QIAABCECgSGCe59eiDkY+mXDeqH5zHHmeu+a24NwocuEIJJFkjAWFwr0AiYGI4z8cUYqNtIITG2kjTkykTJxYSDtxYiDRLC9U+cFbUsZ9cJPKmepnic5yLpA2IAIJSJFvN/r1+Q35SvUsKcVz5Daq8Z5JNOnH9IEBgCSr4wmHPv4VIDEQUTyunMXmHmIjreCIUi8k3nTzszO5EacXkq/3o0yc1khD4LRCGgqnNtKQOLWQhsYpRQqBsxcpFE4uUkicHKSLpDbmkUatpQ8yqdpRfeM2R5EBGOEItm8kYxzfSE5wBOkqK7o0vpR2wOczDgUeVHKTi4/rVH7oOh+drrV+GWcrJ/Rfudw+qQiclAx9DxzgKAJKE1YOcBQBpQkrBziKgNKElQMcRUBpMls5UyqZoMHeqM1LbvU+TdNXaqwtv9eA5pYX3tg3J55mODzGKtn8xsnmHmaO4xnIBY5XIDc4HoFc4XgDcofjCcgljhcgtzgegFzjWAO5x7EEOgSOFdBhcCyADoXTG+hwOD2BDonDQMmibF7lNpltdiUnV6MhVyFx/Jg4NYDRBwQgAAEIQKC5wC+MXBEepqP9zwAAAABJRU5ErkJggg==',
  more: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAASKADAAQAAAABAAAASAAAAACQMUbvAAAAn0lEQVR4Ae3SMQoAIRADQPX/f1bB1iWtwmyZgNwNac0RIECAAAECBAgQIECAAAECBAgQIECAAAECBAgQIECAAAECBAi8K9CrT5v7qu6W9323vMp+eX9UPyA/AoDCEgABCgKhtiBAQSDUFgQoCITaggAFgVBbUABSEyBAgAABAgQIECBAgAABAgQIECBAgAABAgQIECBAgAABAgQIvCywAFfDDBgsa53VAAAAAElFTkSuQmCC',
  search:
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAASKADAAQAAAABAAAASAAAAACQMUbvAAAERklEQVR4Ae2bP2sUQRjGc0pIJWiEYBpBMKhFOhsFCxuDnanyDSwklvkaKRX8AElpp2hACwuNgpWigkZJZxQEYxRBPX+P3MG5mXduLruzOxvmgYe7nZl93+ee29v5tzc2lpEdyA5kB7ID2YHsQHYgO5AdcDjQcZRFKep2u1MEvgjPwVPwJDwCD0FhG36Bb+Fr+AQ+7HQ6W7zuT2DKJFyE63Cv0LmKMblvXOLDTMNluAOrgmIp5nRrjUL8OFyC32AsKLZyjLfKKATPwOewLijXTCtMQugV+LUuZwbyKOeVmCaV7sUQeBWBN+GBAKEbtLkPH8FXcBOq9xLUmx2HZ+AFOAdPwGH4TYNr9Ha3hjWsvV7mDHyb1ttfVKzC86MK1Dm9cxVjGPRFpQPU6mc1TPgabU6XVa0YULF8kJaoP7fgz4EQ3ZB995wf1Ff+jSomVGwL0tTsjRsB6sp9vdUW9WeD3R6xoWJD5bAgbc0NAUiuMYgFCdc0IiqUA/pMWooqwAqOKI2QrUGgLv1oV05Rk3JB6+cmjfWPuEm6DC1Ufs8pmlI8RoivF10uto96jJhJqPmQC2tRk3uCI8bq3aS1vgkuyTSjdkHda+mu3OOBt0q5oTXcWPSeXGUlIqwli5Uq8+wlFto0EHVhfS/xRj6HzFOu7L2ykUfIIwsYcgI6NOK2oIW6uCDzgpH9XdzM4dHRt2FoXAiPsrtlyARTZ2mZ1AVNPFPBPUOIpd1o/n9xqEHW4E+z8lRgabG0B+kONUgL7C5oySIVWFos7UG6Qw3S7oMLm67ChsosLZb2IJmhBvW3ZopB+4tdxfImji0tlvYgjaEGBQVLtFGpVdNQg6J8OxUbal0pO2XyhBqkHU8XtIacCiwtn8sIDDVI28EuaIE9FVha3pcRGGrQGyOJdh9SgaXlZRmBoQY9NpJcMsqbKJ4zksafsDLHaetk9Q/ajxnGVVtMImu5Y7XaTKNHQ5u13KFHaOoBItq4YHa9HnfIgkFtXHI9XJtBSoRJedHe5zgGtWXbZxut9dyci4aROG8cFk0ZPMag1Leen0njoOba3yMg1YcXkNadrd0QV0KEpPb4i8wR7sAJl+bayxDi2/qVWEGbeitw5O0hnQM1CLQ2BqlyojKTSi0m6RtBnvbkb8CDOh6CGI/gWSnvUjHPo3k/rQa1lWNSUw9xOi+fgcLKrqTSZiJKN27fg1UDuit5q95qFsoEH5IySUMAjZOsZ4h8HyS0ToNA5fjXlfM6Adtjki5FBMf8K8KuEXIrTeoZdRTxZf7MovWc/p9ZvPtbsUwq3YuF3rz4AL6/Q0nHd/gJfoAv4FP4gF7oI69BkEk0vA0ve05Ip3fziIxWFetKiia4icDZpADXqzIpdFcjQFJaTXoj6HlU6Z6TYTnguZLSGUBa4usqd5iUzSmaP2BSNqdoTv+4Z1Iaa0V9Ufk1O5AdyA5kB7ID2YGmHPgLw4IkbiDFVkQAAAAASUVORK5CYII=',
  plusSmall:
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAASKADAAQAAAABAAAASAAAAACQMUbvAAAA3UlEQVR4Ae3YQQqEMBBEUfX+d57xAOp30QrCy9ISG15KAlkWiwABAgQIECBAgAABAgQIECBAgAABAncF1rsvPvneb19H31/3dfT8zWfbm8O+OAtQ7BogQCEQsQYBCoGINQhQCESsQYBCIGINAhQCEWsQoBCIeOw64ezKIuY/Fk9dlfjFYosAAQqBiDUIUAhEPHaKxZzL+OwEnDqJLodH6BcDFAIRaxCgEIhYgwCFQMQaBCgEItYgQCEQsQYBCoGINQhQCIgJECBAgAABAgQIECBAgAABAgQIECAwJvAHEn8MRrKxAJMAAAAASUVORK5CYII=',
  plusLarge:
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAASKADAAQAAAABAAAASAAAAACQMUbvAAAA90lEQVR4Ae3ZQQqDQBQFQc3972w8wJceEIYEyuU0KFRecOFxuAgQIECAAAECBAgMAudwtv3ouq/poed9Tec7zz47H/aPzwIUvxogQCEQ2YIAhUBkCwIUApEtCFAIRLYgQCEQ2YIAhUBkCwIUApEtCFAIRLYgQCEQ2YIAhUBkCwqgpe9OT9+t4t4/n1e+u1lQ/IyAAIVAZAsCFAKRl95icY/X+ektufKWef3wuIG/GKAQiGxBgEIgsgUBCoHIFgQoBCJbEKAQiGxBgEIgsgUBCoHIFgQoBCJbEKAQiGxBgEIgsgUBCoHIFhRAMgECBAgQIECAAIFR4AsJWQxqLlO68QAAAABJRU5ErkJggg==',
  menuCircle:
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAASKADAAQAAAABAAAASAAAAACQMUbvAAAEYUlEQVR4Ae2cv2sUQRTH70QNCEZsjIf2wVJsFGKSA4vYChG0UZD0Frb6D6hYmVSCNgYUJGBhqngYQVOElCFVmkBUAv4CwUSI36/MgFxm3r7dZDc7c/PgMdn58ea9z83b3dudS6ORJBFIBBKBRCARCJVAs2rHt7e3j2DOIeg56KDRkyiPGkXR+Gn0E8oVo4so3zebzV8o4xJAGYDehr6D/oYWFY6lDdo6ETwlBDEMfQ3dgu610CZtDwcHCk63ofPQqoRzjdYeFJxsQZ9XRcUxD+du1RIUHBuHfnM4XXUVfRivDSQ40wd9XDUFxXz0qW9fQcGBfuicwtn96kLf+ncDqfB9ECbmZXYWenY3DlQwdglzjOH+6UuRuQoBMp9KBxPWHY5lQkijgPTDVmjLA9qOth/gMK9noKHAoev0dcb4zmO15AYEy4+gbfUM9elIn+l7LsmVYvgEePl8kWuG+nW+ilR7qXVLDQhweAO2DD2mNV7Tft/h1xlAWtf4lyfFHsJg6HDIhDEwFpWoVhBWD/N3TmUxnE5trKJOlrtaQPMwxGc4MQmfLV3MCigzxbB6RmAkNjjkMmRiExllAsLoO6KFsBszYxNTDIQHEP8a9GDYHLze/0HLKaSa92tI1gq6FjEcUuMHf51/+CQL0BXfwIjqxRi9KYb04tuHr9DDEcFwhbKJyuNIM+fbEmkF8coVOxwCY4zeq7QEiO+tekW8sUqABnuFDuL0xipdvr2DFOC20OcJ9I3pexnlLeghc9xdlN2/e77u4/yx4iS9Ci0imxh0qdsD1kHZ1i1l9++ez3W82u1v5jGsbLgsKeqmfMYxdsoxvuz+jil3VG34fJbOQdxMUERsWrnGutpcdXasq81VJ/W3bVLpjVUCJBnsmTYJELegFBGekH3ianPV2fGuNled1N+2SWX+WJGlqzsyVVdR9kk3r32N196TtPRV4wOQn5ewC21lX7bz2hdc/df0EV81Lrg6SYCeYsAN16AI654B0E1XXNI5aMU1INI6b6wSoMVIYbjC8sYqpVh63AGU3hVkno8suHBHVrdgYnWG5QVker9yjoqrUozRm2JkgBuIARRrUOlbP7uGKnxofxor6LMvAHEFmYHcJBWrzEpwGLQIyFB5ECsdxHU/KzYxxexgpFp69WxheMp7nvqQq+9qnNekWAN5+hbGpjUGA+kzjZg6Gl9VKUZDSLMWimVo6HuEytlABeLrgDMBDV0mTCyqOFQpZi3BMPf2TdrjAMtJE4PadXWKWYtItT78zefCbVsXSMnzKDeU81WzWnIDomVA4vb+DjSUvdJL8LWajeSYiFc17lgfg3Liugt95MrJvcuegeU6B/1PAhNy09EolEu3rkLfuHK8G6RKd5znJOgktG5Cn+qzOwXOpB/UZS1HQEo/ycyCxHaASj/qVYIaAayyfxY+ovGl1n0AqYx/LMCnnaVLoRvF3XgFWHxbwj2B3PY2aLS3/zUFICRJBBKBRCARSAT2nsBf4+A5+NpmulcAAAAASUVORK5CYII=',
} as const;

const darkListTheme: NativeListTheme = {
  background: '#0F0F0F',
  rowBackground: '#0F0F0F',
  rowSelectedBackground: '#FFFFFF1B',
  rowPressedBackground: '#FFFFFF1B',
  subduedBackground: '#191919',
  strongBackground: '#FFFFFF12',
  primaryText: '#FFFFFFED',
  secondaryText: '#FFFFFFAF',
  disabledText: '#FFFFFF64',
  icon: '#FFFFFFAF',
  iconSubdued: '#FFFFFF64',
  separator: '#FFFFFF12',
  accent: '#73C76A',
  positive: '#3DD68C',
  negative: '#FF6369',
  criticalBackground: '#FF173D26',
  inverseBackground: '#FFFFFFED',
  inverseText: '#0F0F0F',
  info: '#70B8FF',
};

const walletListTheme: NativeListTheme = {
  ...darkListTheme,
  background: '#191919',
  rowBackground: '#191919',
};

type PendingSwitch = Readonly<{
  walletIndex: number;
  startedAt: number;
  rowsBuildMs: number;
}>;

export function NativeListAccountSelectorPage() {
  const insets = useSafeAreaInsets();
  const accountCacheRef = useRef<AccountRowsCache | null>(null);
  if (!accountCacheRef.current) {
    accountCacheRef.current = new AccountRowsCache();
  }
  const accountCache = accountCacheRef.current;
  const mountedAt = useRef(Date.now());
  const firstVisibleRecorded = useRef(false);
  const pendingSwitch = useRef<PendingSwitch | undefined>(undefined);
  const selectedAccounts = useRef(new Map<number, number>());
  const walletRows = useMemo(() => buildWalletRows(), []);
  const [selectedWalletIndex, setSelectedWalletIndex] =
    useState(DEFAULT_WALLET_INDEX);
  const [selectedAccountIndex, setSelectedAccountIndex] = useState(0);
  const [accountRows, setAccountRows] = useState(() =>
    accountCache.get(DEFAULT_WALLET_INDEX),
  );
  const [searchInput, setSearchInput] = useState('');
  const [searchText, setSearchText] = useState('');
  const [accountGeneration, setAccountGeneration] = useState(1);
  const selectedWalletAvatarUri = walletAvatarUri(selectedWalletIndex);

  useEffect(() => {
    const timer = setTimeout(() => setSearchText(searchInput.trim()), 300);
    return () => clearTimeout(timer);
  }, [searchInput]);

  useEffect(() => {
    console.info(
      '[NativeListAccountSelector] logicalScale',
      JSON.stringify({
        wallets: ACCOUNT_SELECTOR_WALLET_COUNT,
        accountsPerWallet: ACCOUNT_SELECTOR_ACCOUNTS_PER_WALLET,
        logicalAccounts: ACCOUNT_SELECTOR_LOGICAL_ACCOUNT_COUNT,
        materializedAccountRows: accountRows.length,
        cachedWallets: accountCache.cachedWalletCount,
        cachedAccountRows: accountCache.cachedRowCount,
      }),
    );
  }, [accountCache, accountRows]);

  const visibleAccountRows = useMemo(
    () => buildVisibleAccountRows(accountRows, searchText),
    [accountRows, searchText],
  );

  const walletSnapshot = useMemo<NativeListSnapshot>(
    () => ({
      schemaVersion: 1,
      generation: 1,
      theme: walletListTheme,
      layout: {
        kind: 'linear',
        contentPaddingHorizontal: 8,
        contentPaddingTop: 8,
        contentPaddingBottom: 8,
        itemSpacing: 12,
      },
      rows: walletRows,
      selection: {
        mode: 'single',
        selectedKeys: [walletKey(selectedWalletIndex)],
        rowPressToggles: false,
      },
    }),
    [selectedWalletIndex, walletRows],
  );

  const accountSnapshot = useMemo<NativeListSnapshot>(() => {
    const selectedKey = accountKey(selectedWalletIndex, selectedAccountIndex);
    return {
      schemaVersion: 1,
      generation: accountGeneration,
      theme: darkListTheme,
      layout: {
        kind: 'linear',
        contentPaddingHorizontal: 8,
        contentPaddingTop: 0,
        contentPaddingBottom: 12,
        itemSpacing: 0,
      },
      rows: visibleAccountRows,
      selection: {
        mode: 'single',
        selectedKeys: visibleAccountRows.some(row => row.key === selectedKey)
          ? [selectedKey]
          : [],
        rowPressToggles: false,
      },
      emptyState: {
        type: 'system',
        key: 'account-empty',
        variant: 'noMatch',
        message: '没有匹配的账户',
      },
    };
  }, [
    accountGeneration,
    selectedAccountIndex,
    selectedWalletIndex,
    visibleAccountRows,
  ]);

  const selectWallet = (walletIndex: number) => {
    if (walletIndex === selectedWalletIndex) return;
    selectedAccounts.current.set(selectedWalletIndex, selectedAccountIndex);
    const startedAt = Date.now();
    const nextRows = accountCache.get(walletIndex);
    pendingSwitch.current = {
      walletIndex,
      startedAt,
      rowsBuildMs: Date.now() - startedAt,
    };
    setSelectedWalletIndex(walletIndex);
    setSelectedAccountIndex(selectedAccounts.current.get(walletIndex) ?? 0);
    setAccountRows(nextRows);
    setSearchInput('');
    setSearchText('');
    setAccountGeneration(generation => generation + 1);
  };

  const handleWalletAction = (event: RowActionEvent) => {
    if (!event.rowKey || event.actionKey !== 'press') return;
    const walletIndex = parseWalletKey(event.rowKey);
    if (walletIndex !== undefined) selectWallet(walletIndex);
  };

  const handleAccountAction = (event: RowActionEvent) => {
    if (!event.rowKey || event.actionKey !== 'press') {
      console.info(
        '[NativeListAccountSelector] action',
        event.actionKey,
        event.rowKey ?? '',
      );
      return;
    }
    const parsed = parseAccountKey(event.rowKey);
    if (!parsed || parsed.walletIndex !== selectedWalletIndex) return;
    selectedAccounts.current.set(selectedWalletIndex, parsed.accountIndex);
    setSelectedAccountIndex(parsed.accountIndex);
  };

  return (
    <View style={styles.screen}>
      <StatusBar
        barStyle="light-content"
        backgroundColor="transparent"
        translucent
      />
      <View
        style={[styles.selectorSheet, { marginTop: Math.max(insets.top, 16) }]}
      >
        <View style={styles.sidebar}>
          <NativeList
            testID="account-selector-wallet-list"
            style={styles.nativeList}
            snapshot={walletSnapshot}
            onRowAction={handleWalletAction}
          />
          <View
            style={[
              styles.walletFooter,
              { paddingBottom: Math.max(insets.bottom + 12, 20) },
            ]}
          >
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="添加钱包"
              testID="account-selector-add-wallet"
              onPress={() =>
                console.info('[NativeListAccountSelector] add wallet')
              }
              style={({ pressed }) => [
                styles.walletAddButton,
                pressed && styles.buttonPressed,
              ]}
            >
              <Image
                accessibilityIgnoresInvertColors
                source={{ uri: iconUris.plusLarge }}
                style={styles.walletAddIcon}
              />
            </Pressable>
            <Text style={styles.walletFooterLabel}>钱包</Text>
          </View>
        </View>

        <View style={styles.details}>
          <View
            style={styles.walletHeader}
            accessibilityLabel={`${walletName(
              selectedWalletIndex,
            )}, ${ACCOUNT_SELECTOR_WALLET_COUNT} wallets, ${ACCOUNT_SELECTOR_ACCOUNTS_PER_WALLET} accounts per wallet`}
          >
            <View style={styles.headerAvatarWrap}>
              {selectedWalletAvatarUri ? (
                <Image
                  accessibilityIgnoresInvertColors
                  source={{ uri: selectedWalletAvatarUri }}
                  style={styles.headerAvatar}
                />
              ) : (
                <Text style={styles.headerAvatarFallback}>
                  {walletEmoji(selectedWalletIndex)}
                </Text>
              )}
              <View style={styles.headerAvatarBadge}>
                <Image
                  accessibilityIgnoresInvertColors
                  source={{ uri: iconUris.menuCircle }}
                  style={styles.headerAvatarBadgeIcon}
                />
              </View>
            </View>
            <Text style={styles.headerTitle} numberOfLines={1}>
              {walletName(selectedWalletIndex)}
            </Text>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="编辑钱包名称"
              testID="account-selector-edit-wallet"
              onPress={() =>
                console.info('[NativeListAccountSelector] edit wallet')
              }
              style={({ pressed }) => [
                styles.headerEditButton,
                pressed && styles.buttonPressed,
              ]}
            >
              <Image
                accessibilityIgnoresInvertColors
                source={{ uri: iconUris.pencil }}
                style={styles.pencilIcon}
              />
            </Pressable>
            <View style={styles.headerSpacer} />
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="钱包更多"
              testID="account-selector-more"
              onPress={() =>
                console.info('[NativeListAccountSelector] wallet more')
              }
              style={({ pressed }) => [
                styles.headerIconButton,
                pressed && styles.buttonPressed,
              ]}
            >
              <Image
                accessibilityIgnoresInvertColors
                source={{ uri: iconUris.more }}
                style={styles.moreIcon}
              />
            </Pressable>
          </View>

          <View style={styles.searchToolbar}>
            <View style={styles.searchContainer}>
              <Image
                accessibilityIgnoresInvertColors
                source={{ uri: iconUris.search }}
                style={styles.searchIcon}
              />
              <TextInput
                testID="account-selector-search"
                accessibilityLabel="搜索账户"
                value={searchInput}
                onChangeText={setSearchInput}
                placeholder="搜索"
                placeholderTextColor="#FFFFFF64"
                selectionColor="#FFFFFFAF"
                style={styles.searchInput}
                autoCorrect={false}
                returnKeyType="search"
              />
              {searchInput ? (
                <Pressable
                  accessibilityRole="button"
                  accessibilityLabel="清除搜索"
                  onPress={() => setSearchInput('')}
                  style={styles.clearSearch}
                >
                  <Text style={styles.clearSearchText}>×</Text>
                </Pressable>
              ) : null}
            </View>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="添加账户"
              testID="account-selector-add-account"
              onPress={() =>
                console.info('[NativeListAccountSelector] add account')
              }
              style={({ pressed }) => [
                styles.smallAddButton,
                pressed && styles.buttonPressed,
              ]}
            >
              <Image
                accessibilityIgnoresInvertColors
                source={{ uri: iconUris.plusSmall }}
                style={styles.smallAddIcon}
              />
            </Pressable>
          </View>

          <NativeList
            testID="account-selector-account-list"
            style={styles.nativeList}
            snapshot={accountSnapshot}
            onRowAction={handleAccountAction}
            onVisibleRangeChanged={event => {
              if (!firstVisibleRecorded.current && event.firstIndex >= 0) {
                firstVisibleRecorded.current = true;
                console.info(
                  '[NativeListAccountSelector] firstVisible',
                  JSON.stringify({
                    elapsedMs: Date.now() - mountedAt.current,
                    firstIndex: event.firstIndex,
                    lastIndex: event.lastIndex,
                  }),
                );
              }
              const pending = pendingSwitch.current;
              if (
                !pending ||
                !event.firstKey?.startsWith(`account-${pending.walletIndex}-`)
              ) {
                return;
              }
              console.info(
                '[NativeListAccountSelector] walletSwitch',
                JSON.stringify({
                  walletIndex: pending.walletIndex,
                  rowsBuildMs: pending.rowsBuildMs,
                  firstVisibleMs: Date.now() - pending.startedAt,
                  cachedWallets: accountCache.cachedWalletCount,
                  cachedAccountRows: accountCache.cachedRowCount,
                }),
              );
              pendingSwitch.current = undefined;
            }}
          />
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: '#080808' },
  selectorSheet: {
    flex: 1,
    flexDirection: 'row',
    borderTopLeftRadius: 28,
    borderTopRightRadius: 28,
    borderCurve: 'continuous',
    overflow: 'hidden',
  },
  sidebar: {
    width: 96,
    backgroundColor: '#191919',
    borderRightColor: '#FFFFFF12',
    borderRightWidth: StyleSheet.hairlineWidth,
  },
  details: { flex: 1, backgroundColor: '#0F0F0F' },
  nativeList: { flex: 1 },
  walletFooter: {
    alignItems: 'center',
    borderTopColor: '#FFFFFF22',
    borderTopWidth: StyleSheet.hairlineWidth,
    paddingTop: 12,
    paddingHorizontal: 8,
  },
  walletAddButton: {
    width: 42,
    height: 42,
    borderRadius: 21,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FFFFFFED',
  },
  walletAddIcon: {
    width: 24,
    height: 24,
    tintColor: '#111111',
  },
  walletFooterLabel: {
    color: '#FFFFFFED',
    fontSize: 12,
    fontFamily: 'Roobert-Regular',
    lineHeight: 16,
    marginTop: 4,
  },
  walletHeader: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    paddingLeft: 20,
    paddingRight: 12,
    gap: 6,
  },
  headerAvatar: { width: 32, height: 32 },
  headerAvatarWrap: { width: 32, height: 32 },
  headerAvatarFallback: { width: 32, fontSize: 26, textAlign: 'center' },
  headerAvatarBadge: {
    position: 'absolute',
    right: -4,
    bottom: -4,
    width: 20,
    height: 20,
    padding: 1,
    borderRadius: 10,
    backgroundColor: '#0F0F0F',
  },
  headerAvatarBadgeIcon: {
    width: 18,
    height: 18,
    tintColor: '#FFFFFF64',
  },
  headerTitle: {
    color: '#FFFFFFED',
    fontSize: 16,
    fontFamily: 'Roobert-Medium',
    lineHeight: 24,
    maxWidth: 132,
  },
  headerIconButton: {
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  headerEditButton: {
    width: 16,
    height: 24,
    alignItems: 'center',
    justifyContent: 'center',
  },
  headerSpacer: { flex: 1 },
  pencilIcon: { width: 16, height: 16, tintColor: '#FFFFFF64' },
  moreIcon: {
    width: 24,
    height: 24,
    tintColor: '#FFFFFF64',
  },
  searchToolbar: {
    height: 44,
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: 8,
    paddingHorizontal: 20,
    paddingTop: 8,
    paddingBottom: 8,
    marginBottom: 8,
    borderBottomColor: '#FFFFFF12',
    borderBottomWidth: StyleSheet.hairlineWidth,
  },
  searchContainer: {
    flex: 1,
    height: 28,
    borderRadius: 14,
    backgroundColor: '#FFFFFF12',
    justifyContent: 'center',
  },
  searchInput: {
    height: 28,
    paddingVertical: 0,
    paddingLeft: 32,
    paddingRight: 34,
    color: '#FFFFFFED',
    fontSize: 14,
    fontFamily: 'Roobert-Regular',
  },
  searchIcon: {
    position: 'absolute',
    left: 5,
    top: 4,
    width: 20,
    height: 20,
    tintColor: '#FFFFFF64',
  },
  clearSearch: {
    position: 'absolute',
    right: 7,
    width: 24,
    height: 24,
    alignItems: 'center',
    justifyContent: 'center',
  },
  clearSearchText: { color: '#FFFFFF64', fontSize: 20, lineHeight: 22 },
  smallAddButton: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: '#FFFFFF12',
    alignItems: 'center',
    justifyContent: 'center',
  },
  smallAddIcon: {
    width: 20,
    height: 20,
    tintColor: '#FFFFFFAF',
  },
  buttonPressed: { opacity: 0.65 },
});
