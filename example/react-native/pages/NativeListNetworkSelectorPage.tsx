import { useMemo, useState } from 'react';
import {
  Alert,
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
  type SelectionDeltaEvent,
} from '@onekeyfe/react-native-native-list';

import {
  NETWORK_SELECTOR_DEFAULT_ENABLED_IDS,
  NETWORK_SELECTOR_MAINNET_COUNT,
  NETWORK_SELECTOR_NETWORKS,
  buildNetworkSelectorRows,
  type NetworkSelectorMode,
} from './nativeListNetworkSelectorData';

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

const mainnetIds = NETWORK_SELECTOR_NETWORKS.filter(
  network => !network.isTestnet,
).map(network => network.id);

export function NativeListNetworkSelectorPage() {
  const navigation = useNavigation();
  const insets = useSafeAreaInsets();
  const [mode, setMode] = useState<NetworkSelectorMode>('network');
  const [searchText, setSearchText] = useState('');
  const [selectedKeys, setSelectedKeys] = useState<readonly string[]>(
    NETWORK_SELECTOR_DEFAULT_ENABLED_IDS,
  );
  const selectedSet = useMemo(() => new Set(selectedKeys), [selectedKeys]);
  const rows = useMemo(
    () => buildNetworkSelectorRows(mode, searchText, selectedSet),
    [mode, searchText, selectedSet],
  );
  const visibleKeys = useMemo(() => new Set(rows.map(row => row.key)), [rows]);

  const snapshot = useMemo<NativeListSnapshot>(
    () => ({
      schemaVersion: 1,
      generation: 1,
      theme: darkTheme,
      layout: {
        kind: 'sectioned',
        stickyHeaders: false,
        contentPaddingHorizontal: 8,
        contentPaddingTop: 0,
        contentPaddingBottom: Math.max(insets.bottom, 8),
        itemSpacing: 0,
      },
      rows,
      selection: {
        mode: mode === 'portfolio' ? 'multiple' : 'none',
        selectedKeys:
          mode === 'portfolio'
            ? selectedKeys.filter(key => visibleKeys.has(key))
            : [],
        rowPressToggles: mode === 'portfolio',
      },
      emptyState: {
        type: 'system',
        key: 'network-selector-empty',
        variant: 'noMatch',
        message: '无结果',
      },
    }),
    [insets.bottom, mode, rows, selectedKeys, visibleKeys],
  );

  const handleSelectionDelta = (event: SelectionDeltaEvent) => {
    setSelectedKeys(current => {
      const next = new Set(current);
      event.removedKeys.forEach(key => next.delete(key));
      event.addedKeys.forEach(key => next.add(key));
      return Array.from(next);
    });
  };

  const handleRowAction = (event: RowActionEvent) => {
    if (event.actionKey === 'network.toggleAll') {
      setSelectedKeys(current =>
        current.length === NETWORK_SELECTOR_MAINNET_COUNT ? [] : mainnetIds,
      );
      return;
    }
    console.info(
      '[NativeListNetworkSelector] action',
      event.actionKey,
      event.rowKey ?? '',
    );
  };

  const selectMode = (nextMode: NetworkSelectorMode) => {
    setMode(nextMode);
    console.info('[NativeListNetworkSelector] mode', nextMode);
  };

  return (
    <View style={styles.screen}>
      <StatusBar
        barStyle="light-content"
        backgroundColor="transparent"
        translucent
      />
      <View
        style={[styles.sheet, { marginTop: Math.max(insets.top, 16) }]}
        testID="network-selector-sheet"
      >
        <View style={styles.header}>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel="关闭"
            testID="network-selector-close"
            hitSlop={4}
            onPress={() => {
              console.info('[NativeListNetworkSelector] close');
              if (navigation.canGoBack()) navigation.goBack();
            }}
            style={({ pressed }) => [
              styles.headerButton,
              styles.closeButton,
              pressed && styles.pressed,
            ]}
          >
            <View style={[styles.closeLine, styles.closeLineForward]} />
            <View style={[styles.closeLine, styles.closeLineBackward]} />
          </Pressable>

          <View
            accessibilityRole="tablist"
            accessibilityLabel="网络类型"
            style={styles.segment}
          >
            <Pressable
              accessibilityRole="tab"
              accessibilityState={{ selected: mode === 'portfolio' }}
              testID="network-selector-all-networks-tab"
              onPress={() => selectMode('portfolio')}
              style={[
                styles.segmentItem,
                mode === 'portfolio' && styles.segmentItemActive,
              ]}
            >
              <Text
                style={[
                  styles.segmentText,
                  mode === 'portfolio' && styles.segmentTextActive,
                ]}
              >
                所有网络
              </Text>
            </Pressable>
            <Pressable
              accessibilityRole="tab"
              accessibilityState={{ selected: mode === 'network' }}
              testID="network-selector-single-network-tab"
              onPress={() => selectMode('network')}
              style={[
                styles.segmentItem,
                mode === 'network' && styles.segmentItemActive,
              ]}
            >
              <Text
                style={[
                  styles.segmentText,
                  mode === 'network' && styles.segmentTextActive,
                ]}
              >
                单一网络
              </Text>
            </Pressable>
          </View>

          <Pressable
            accessibilityRole="button"
            accessibilityLabel="添加网络"
            testID="network-selector-add"
            hitSlop={4}
            onPress={() => {
              console.info('[NativeListNetworkSelector] add network');
              Alert.alert('添加网络', '添加网络操作已触发');
            }}
            style={({ pressed }) => [
              styles.headerButton,
              styles.addButton,
              pressed && styles.pressed,
            ]}
          >
            <View style={styles.plusHorizontal} />
            <View style={styles.plusVertical} />
          </Pressable>
        </View>

        <View style={styles.searchArea}>
          <View style={styles.searchBox}>
            <View style={styles.searchGlyph}>
              <View style={styles.searchCircle} />
              <View style={styles.searchHandle} />
            </View>
            <TextInput
              accessibilityLabel="搜索网络"
              testID="network-selector-search"
              value={searchText}
              onChangeText={setSearchText}
              placeholder="搜索"
              placeholderTextColor="#FFFFFF64"
              selectionColor="#FFFFFFAF"
              autoCorrect={false}
              returnKeyType="search"
              style={styles.searchInput}
            />
            {searchText ? (
              <Pressable
                accessibilityRole="button"
                accessibilityLabel="清除搜索"
                testID="network-selector-search-clear"
                onPress={() => setSearchText('')}
                style={styles.clearSearch}
              >
                <Text style={styles.clearSearchText}>×</Text>
              </Pressable>
            ) : null}
          </View>
        </View>

        <NativeList
          testID="network-selector-native-list"
          style={styles.list}
          snapshot={snapshot}
          onRowAction={handleRowAction}
          onSelectionDelta={handleSelectionDelta}
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
    paddingTop: 16,
  },
  headerButton: {
    width: 44,
    height: 44,
    borderRadius: 22,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#161616',
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#FFFFFF12',
  },
  closeButton: { position: 'relative' },
  addButton: { position: 'relative' },
  closeLine: {
    position: 'absolute',
    width: 23,
    height: 2,
    borderRadius: 1,
    backgroundColor: '#FFFFFFED',
  },
  closeLineForward: { transform: [{ rotate: '45deg' }] },
  closeLineBackward: { transform: [{ rotate: '-45deg' }] },
  plusHorizontal: {
    position: 'absolute',
    width: 24,
    height: 2,
    borderRadius: 1,
    backgroundColor: '#FFFFFFED',
  },
  plusVertical: {
    position: 'absolute',
    width: 2,
    height: 24,
    borderRadius: 1,
    backgroundColor: '#FFFFFFED',
  },
  segment: {
    position: 'absolute',
    left: '50%',
    top: 23,
    width: 166,
    height: 32,
    marginLeft: -67,
    flexDirection: 'row',
    borderRadius: 16,
    backgroundColor: '#202020',
  },
  segmentItem: {
    flex: 1,
    height: 32,
    borderRadius: 16,
    alignItems: 'center',
    justifyContent: 'center',
  },
  segmentItemActive: { backgroundColor: '#EEEEEE' },
  segmentText: {
    color: '#FFFFFFAF',
    fontSize: 16,
    lineHeight: 24,
    fontFamily: 'Roobert-Medium',
  },
  segmentTextActive: { color: '#0F0F0F' },
  pressed: { opacity: 0.65 },
  searchArea: {
    height: 56,
    paddingHorizontal: 20,
    paddingTop: 4,
    paddingBottom: 16,
  },
  searchBox: {
    height: 36,
    borderRadius: 18,
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
    paddingLeft: 36,
    paddingRight: 40,
    color: '#FFFFFFED',
    fontSize: 16,
    lineHeight: 24,
    fontFamily: 'Roobert-Regular',
  },
  clearSearch: {
    position: 'absolute',
    right: 8,
    width: 28,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  clearSearchText: {
    color: '#FFFFFF64',
    fontSize: 22,
    lineHeight: 24,
    fontFamily: 'Roobert-Regular',
  },
  list: { flex: 1 },
});
