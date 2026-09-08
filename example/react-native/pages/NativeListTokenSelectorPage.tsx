import { useMemo, useState } from 'react';
import {
  Image,
  Pressable,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import {
  NativeList,
  type NativeListSnapshot,
  type NativeListTheme,
  type RowActionEvent,
} from '@onekeyfe/react-native-native-list';

import {
  ETHEREUM_NETWORK_LOGO,
  POPULAR_TOKENS,
  buildTokenSelectorRows,
} from './nativeListTokenSelectorData';

const darkTheme: NativeListTheme = {
  background: '#0F0F0F',
  rowBackground: '#0F0F0F',
  rowSelectedBackground: '#FFFFFF1B',
  rowPressedBackground: '#FFFFFF1B',
  subduedBackground: '#202020',
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

const networkFilters: readonly {
  id: string;
  name: string;
  logoURI: string;
  sameChain?: boolean;
  allNetworks?: boolean;
}[] = [
  {
    id: 'evm--1',
    name: 'Ethereum',
    logoURI: ETHEREUM_NETWORK_LOGO,
    sameChain: true,
  },
  {
    id: 'onekeyall--0',
    name: '所有网络',
    logoURI: 'https://uni.onekey-asset.com/static/logo/chain_selector_logo.png',
    allNetworks: true,
  },
  {
    id: 'evm--56',
    name: 'BNB Chain',
    logoURI: 'https://uni.onekey-asset.com/static/chain/bsc.png',
  },
  {
    id: 'kaspa--kaspa',
    name: 'Kaspa',
    logoURI: 'https://uni.onekey-asset.com/static/chain/kas.png',
  },
  {
    id: 'sui--mainnet',
    name: 'SUI',
    logoURI: 'https://uni.onekey-asset.com/static/chain/sui.png',
  },
];

export function NativeListTokenSelectorPage() {
  const navigation = useNavigation();
  const insets = useSafeAreaInsets();
  const [searchText, setSearchText] = useState('');
  const [showDeFiOnly, setShowDeFiOnly] = useState(false);
  const [selectedNetworkId, setSelectedNetworkId] = useState('evm--1');
  const tokenRows = useMemo(
    () => buildTokenSelectorRows(searchText, showDeFiOnly),
    [searchText, showDeFiOnly],
  );
  const rows = useMemo(
    () =>
      selectedNetworkId === 'evm--1' || selectedNetworkId === 'onekeyall--0'
        ? tokenRows
        : [],
    [selectedNetworkId, tokenRows],
  );

  const snapshot = useMemo<NativeListSnapshot>(
    () => ({
      schemaVersion: 1,
      generation: 1,
      theme: darkTheme,
      layout: {
        kind: 'linear',
        contentPaddingHorizontal: 8,
        contentPaddingTop: 0,
        contentPaddingBottom: Math.max(insets.bottom, 8),
        itemSpacing: 0,
      },
      rows,
      selection: { mode: 'none', selectedKeys: [] },
      emptyState: {
        type: 'system',
        key: 'token-selector-empty',
        variant: 'noMatch',
        message: '无结果',
      },
    }),
    [insets.bottom, rows],
  );

  const handleRowAction = (event: RowActionEvent) => {
    console.info(
      '[NativeListTokenSelector] action',
      event.actionKey,
      event.rowKey ?? '',
    );
  };

  return (
    <View style={styles.screen}>
      <StatusBar
        barStyle="light-content"
        backgroundColor="transparent"
        translucent
      />
      <View
        style={[styles.sheet, { marginTop: Math.max(insets.top, 58) }]}
        testID="token-selector-sheet"
      >
        <View style={styles.header}>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel="关闭"
            testID="token-selector-close"
            hitSlop={4}
            onPress={() => {
              if (navigation.canGoBack()) navigation.goBack();
            }}
            style={({ pressed }) => [
              styles.closeButton,
              pressed && styles.pressed,
            ]}
          >
            <View style={[styles.closeLine, styles.closeLineForward]} />
            <View style={[styles.closeLine, styles.closeLineBackward]} />
          </Pressable>
          <Text style={styles.title}>选择代币</Text>
          <View style={styles.headerSpacer} />
        </View>

        <View style={styles.searchArea}>
          <View style={styles.searchBox}>
            <View style={styles.searchGlyph}>
              <View style={styles.searchCircle} />
              <View style={styles.searchHandle} />
            </View>
            <TextInput
              accessibilityLabel="搜索代币名称或合约地址"
              testID="token-selector-search"
              value={searchText}
              onChangeText={setSearchText}
              placeholder="搜索代币名称或合约地址"
              placeholderTextColor="#FFFFFF64"
              selectionColor="#FFFFFFAF"
              autoCapitalize="none"
              autoCorrect={false}
              returnKeyType="search"
              style={styles.searchInput}
            />
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="从剪贴板粘贴"
              testID="token-selector-paste"
              hitSlop={4}
              onPress={() =>
                console.info('[NativeListTokenSelector] paste pressed')
              }
              style={({ pressed }) => [
                styles.clipboardButton,
                pressed && styles.pressed,
              ]}
            >
              <View style={styles.clipboardBody} />
              <View style={styles.clipboardTab} />
            </Pressable>
          </View>
        </View>

        <View style={styles.filterRow}>
          <View style={styles.networkNameRow}>
            <Text style={styles.filterLabel}>网络:</Text>
            <Text style={styles.filterValue}>Ethereum</Text>
          </View>
          <Pressable
            accessibilityRole="switch"
            accessibilityLabel="DeFi 代币"
            accessibilityState={{ checked: showDeFiOnly }}
            testID="token-selector-defi-switch"
            onPress={() => setShowDeFiOnly(value => !value)}
            style={styles.defiControl}
          >
            <Text style={styles.filterValue}>DeFi 代币</Text>
            <View
              style={[
                styles.switchTrack,
                showDeFiOnly && styles.switchTrackEnabled,
              ]}
            >
              <View
                style={[
                  styles.switchThumb,
                  showDeFiOnly && styles.switchThumbEnabled,
                ]}
              />
            </View>
          </Pressable>
        </View>

        <View style={styles.networkFilters}>
          {networkFilters.map((network, index) => {
            const selected = selectedNetworkId === network.id;
            return (
              <View key={network.id} style={styles.networkFilterGroup}>
                {index === 1 ? <View style={styles.networkDivider} /> : null}
                <Pressable
                  accessibilityRole="button"
                  accessibilityLabel={network.name}
                  accessibilityState={{ selected }}
                  testID={`token-selector-network-${network.id}`}
                  onPress={() => setSelectedNetworkId(network.id)}
                  style={({ pressed }) => [
                    styles.networkChip,
                    network.sameChain && styles.sameChainChip,
                    selected && styles.networkChipSelected,
                    pressed && styles.pressed,
                  ]}
                >
                  {network.allNetworks ? (
                    <View style={styles.allNetworksLogo}>
                      <View style={styles.allNetworksDots}>
                        <View style={styles.allNetworksDot} />
                        <View style={styles.allNetworksDot} />
                        <View style={styles.allNetworksDot} />
                        <View style={styles.allNetworksDot} />
                      </View>
                    </View>
                  ) : (
                    <Image
                      source={{ uri: network.logoURI }}
                      style={styles.networkLogo}
                    />
                  )}
                  {network.sameChain ? (
                    <Text style={styles.sameChainText}>同链</Text>
                  ) : null}
                </Pressable>
              </View>
            );
          })}
          <Pressable
            accessibilityRole="button"
            accessibilityLabel="更多网络"
            testID="token-selector-network-more"
            onPress={() =>
              console.info('[NativeListTokenSelector] more networks')
            }
            style={({ pressed }) => [
              styles.moreChip,
              pressed && styles.pressed,
            ]}
          >
            <Text style={styles.moreText}>32+</Text>
          </Pressable>
        </View>

        <View style={styles.divider} />

        {!searchText && !showDeFiOnly && selectedNetworkId === 'evm--1' ? (
          <View style={styles.popularArea}>
            <Text style={styles.popularLabel}>热门代币</Text>
            <View style={styles.popularTokens}>
              {POPULAR_TOKENS.map(token => (
                <Pressable
                  key={token.symbol}
                  accessibilityRole="button"
                  accessibilityLabel={token.symbol}
                  testID={`token-selector-popular-${token.symbol}`}
                  onPress={() =>
                    console.info(
                      '[NativeListTokenSelector] popular token',
                      token.symbol,
                    )
                  }
                  style={({ pressed }) => [
                    styles.popularChip,
                    pressed && styles.pressed,
                  ]}
                >
                  <Image
                    source={{ uri: token.logoURI }}
                    style={styles.popularLogo}
                  />
                  <Text style={styles.popularSymbol}>{token.symbol}</Text>
                </Pressable>
              ))}
            </View>
          </View>
        ) : null}

        <NativeList
          testID="token-selector-native-list"
          style={styles.list}
          snapshot={snapshot}
          webVirtualizationEnabled={false}
          onRowAction={handleRowAction}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: '#080808' },
  sheet: {
    flex: 1,
    overflow: 'hidden',
    backgroundColor: '#0F0F0F',
    borderTopLeftRadius: 45,
    borderTopRightRadius: 45,
    borderCurve: 'continuous',
  },
  header: {
    height: 64,
    alignItems: 'center',
    justifyContent: 'space-between',
    flexDirection: 'row',
    paddingHorizontal: 16,
    paddingTop: 4,
  },
  closeButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#202020',
  },
  closeLine: {
    position: 'absolute',
    width: 20,
    height: 2,
    borderRadius: 1,
    backgroundColor: '#FFFFFFED',
  },
  closeLineForward: { transform: [{ rotate: '45deg' }] },
  closeLineBackward: { transform: [{ rotate: '-45deg' }] },
  title: {
    color: '#FFFFFFED',
    fontSize: 18,
    lineHeight: 24,
    fontFamily: 'Roobert-SemiBold',
  },
  headerSpacer: { width: 40, height: 40 },
  pressed: { opacity: 0.65 },
  searchArea: {
    height: 56,
    paddingHorizontal: 20,
    paddingTop: 6,
    paddingBottom: 14,
  },
  searchBox: {
    height: 36,
    borderRadius: 10,
    justifyContent: 'center',
    backgroundColor: '#202020',
  },
  searchGlyph: {
    position: 'absolute',
    left: 14,
    top: 9,
    width: 18,
    height: 18,
  },
  searchCircle: {
    width: 14,
    height: 14,
    borderRadius: 7,
    borderWidth: 2,
    borderColor: '#FFFFFF64',
  },
  searchHandle: {
    position: 'absolute',
    right: 0,
    bottom: 1,
    width: 7,
    height: 2,
    borderRadius: 1,
    backgroundColor: '#FFFFFF64',
    transform: [{ rotate: '45deg' }],
  },
  searchInput: {
    height: 36,
    paddingVertical: 0,
    paddingLeft: 40,
    paddingRight: 44,
    color: '#FFFFFFED',
    fontSize: 16,
    lineHeight: 24,
    fontFamily: 'Roobert-Regular',
  },
  clipboardButton: {
    position: 'absolute',
    right: 8,
    width: 32,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  clipboardBody: {
    width: 14,
    height: 17,
    borderColor: '#FFFFFF64',
    borderRadius: 3,
    borderWidth: 1.5,
  },
  clipboardTab: {
    position: 'absolute',
    top: 8,
    width: 7,
    height: 4,
    borderColor: '#FFFFFF64',
    borderRadius: 2,
    borderWidth: 1.5,
    backgroundColor: '#202020',
  },
  filterRow: {
    height: 40,
    paddingHorizontal: 20,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  networkNameRow: { flexDirection: 'row', alignItems: 'center' },
  filterLabel: {
    color: '#FFFFFFAF',
    fontSize: 14,
    lineHeight: 20,
    fontFamily: 'Roobert-Regular',
    marginRight: 8,
  },
  filterValue: {
    color: '#FFFFFFED',
    fontSize: 14,
    lineHeight: 20,
    fontFamily: 'Roobert-Regular',
  },
  defiControl: {
    minHeight: 32,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  switchTrack: {
    width: 36,
    height: 20,
    borderRadius: 10,
    padding: 2,
    backgroundColor: '#FFFFFF2E',
  },
  switchTrackEnabled: { backgroundColor: '#73C76A' },
  switchThumb: {
    width: 16,
    height: 16,
    borderRadius: 8,
    backgroundColor: '#202020',
  },
  switchThumbEnabled: {
    backgroundColor: '#FFFFFFED',
    transform: [{ translateX: 16 }],
  },
  networkFilters: {
    height: 48,
    paddingHorizontal: 20,
    paddingTop: 4,
    paddingBottom: 8,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 3,
  },
  networkFilterGroup: { flexDirection: 'row', alignItems: 'center', gap: 3 },
  networkDivider: {
    width: StyleSheet.hairlineWidth,
    height: 24,
    marginHorizontal: 3,
    backgroundColor: '#FFFFFF1B',
  },
  networkChip: {
    height: 36,
    minWidth: 36,
    paddingHorizontal: 8,
    borderWidth: 1,
    borderColor: '#FFFFFF12',
    borderRadius: 18,
    alignItems: 'center',
    justifyContent: 'center',
    flexDirection: 'row',
    backgroundColor: '#0F0F0F',
  },
  sameChainChip: { paddingHorizontal: 8, gap: 6 },
  networkChipSelected: { borderColor: '#FFFFFFAF' },
  networkLogo: { width: 20, height: 20, borderRadius: 10 },
  allNetworksLogo: {
    width: 20,
    height: 20,
    borderRadius: 10,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FFFFFFED',
  },
  allNetworksDots: {
    width: 10,
    height: 10,
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 2,
  },
  allNetworksDot: {
    width: 4,
    height: 4,
    borderRadius: 2,
    backgroundColor: '#0F0F0F',
  },
  sameChainText: {
    color: '#FFFFFFED',
    fontSize: 12,
    lineHeight: 16,
    fontFamily: 'Roobert-Medium',
  },
  moreChip: {
    height: 36,
    minWidth: 45,
    flex: 1,
    borderWidth: 1,
    borderColor: '#FFFFFF12',
    borderRadius: 10,
    alignItems: 'center',
    justifyContent: 'center',
  },
  moreText: {
    color: '#FFFFFFAF',
    fontSize: 14,
    lineHeight: 20,
    fontFamily: 'Roobert-Medium',
  },
  divider: { height: 1, backgroundColor: '#FFFFFF12', marginTop: 8 },
  popularArea: { paddingHorizontal: 20, paddingTop: 12, paddingBottom: 12 },
  popularLabel: {
    color: '#FFFFFFAF',
    fontSize: 14,
    lineHeight: 20,
    fontFamily: 'Roobert-Regular',
    marginBottom: 8,
  },
  popularTokens: { flexDirection: 'row', flexWrap: 'wrap', gap: 6 },
  popularChip: {
    height: 36,
    paddingHorizontal: 8,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#FFFFFF12',
    borderRadius: 18,
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#0F0F0F',
  },
  popularLogo: { width: 18, height: 18, borderRadius: 9 },
  popularSymbol: {
    color: '#FFFFFFED',
    fontSize: 16,
    lineHeight: 24,
    fontFamily: 'Roobert-Medium',
    marginLeft: 4,
  },
  list: { flex: 1 },
});
