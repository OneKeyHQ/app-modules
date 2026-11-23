import { useMemo, useRef, useState } from 'react';
import { Pressable, RefreshControl, Text, View } from 'react-native';
import {
  NativeList,
  type NativeListRef,
  type NativeListSnapshot,
} from '@onekeyfe/react-native-native-list';
import { CollapsiblePagerView, NativeScroller } from '@onekeyfe/react-native-pager-view';

export function ScrollEdgeTestPage() {
  const listRef = useRef<NativeListRef>(null);
  const pagerRef = useRef<CollapsiblePagerView>(null);
  const [kind, setKind] = useState<'list' | 'scroller' | 'pager'>('list');
  const [enabled, setEnabled] = useState(false);
  const [tall, setTall] = useState(false);
  const [rowCount, setRowCount] = useState<32 | 2 | 0>(32);
  const [selectedPage, setSelectedPage] = useState(0);
  const [capable, setCapable] = useState(false);
  const [pressCount, setPressCount] = useState(0);
  const [stickyPressCount, setStickyPressCount] = useState(0);
  const [smooth, setSmooth] = useState(true);
  const [chromeMode, setChromeMode] = useState<'react' | 'native' | 'subheader'>('react');
  const [ownerVersion, setOwnerVersion] = useState(0);
  const [refreshing, setRefreshing] = useState(false);
  const snapshot = useMemo<NativeListSnapshot>(() => ({
    schemaVersion: 1,
    generation: 1,
    layout: { kind: 'linear', contentPaddingTop: 0 },
    rows: Array.from({ length: rowCount }, (_, index) => ({
      type: 'action' as const,
      key: `edge-row-${index}`,
      title: `COLOR ROW ${String(index + 1).padStart(2, '0')}`,
      actionKey: `edge-row-${index}`,
      tone: 'neutral' as const,
      style: {
        container: { backgroundColor: index % 2 ? '#FFAA00' : '#036BFF', height: 56 },
        title: { color: '#FFFFFF' },
      },
    })),
    capabilities: { pullToRefresh: true },
  }), [rowCount]);
  const rows = Array.from({ length: rowCount }, (_, index) => (
    <View
      key={index}
      style={{ height: 56, justifyContent: 'center', paddingHorizontal: 20,
        backgroundColor: index % 2 ? '#FFAA00' : '#036BFF' }}
    >
      <Text style={{ color: '#FFFFFF', fontWeight: '700' }}>
        COLOR ROW {String(index + 1).padStart(2, '0')}
      </Text>
    </View>
  ));
  const overlay = (
    <Pressable
      testID="scroll-edge-overlay-button"
      onPress={() => setPressCount(value => value + 1)}
      style={{
        height: tall ? 110 : 64,
        justifyContent: 'center',
        paddingHorizontal: 20,
        backgroundColor: 'transparent',
        borderBottomWidth: 1,
        borderBottomColor: '#111111',
      }}
    >
      <Text style={{ color: '#111111', fontWeight: '700' }}>
        FIXED TOOLBAR · taps {pressCount} · {tall ? 'tall' : 'short'}
      </Text>
    </Pressable>
  );
  const nativeItems = [
    { key: 'page-1', title: 'P1' },
    { key: 'page-2', title: 'P2' },
    { key: 'page-3', title: 'P3' },
  ];
  const onRefresh = () => {
    setRefreshing(true);
    setTimeout(() => {
      setRefreshing(false);
      listRef.current?.setRefreshing(false);
    }, 1600);
  };

  return (
    <View style={{ flex: 1, backgroundColor: '#FFFFFF' }}>
      <View style={{ flexDirection: 'row', padding: 8, gap: 8 }}>
        <Pressable testID="scroll-edge-kind" onPress={() => setKind(value => value === 'list' ? 'scroller' : value === 'scroller' ? 'pager' : 'list')}>
          <Text>{kind === 'list' ? 'List' : kind === 'scroller' ? 'Scroller' : 'Pager'}</Text>
        </Pressable>
        <Pressable testID="scroll-edge-toggle" onPress={() => setEnabled(value => !value)}>
          <Text>{enabled ? 'Effect ON' : 'Effect OFF'}</Text>
        </Pressable>
        <Pressable testID="scroll-edge-height" onPress={() => setTall(value => !value)}>
          <Text>{tall ? 'Tall' : 'Short'}</Text>
        </Pressable>
        <Pressable testID="scroll-edge-rows" onPress={() => setRowCount(value => value === 32 ? 2 : value === 2 ? 0 : 32)}>
          <Text>Rows {rowCount}</Text>
        </Pressable>
        <Text testID="scroll-edge-refresh-state">{refreshing ? 'Refreshing' : 'Idle'}</Text>
      </View>
      {kind === 'pager' ? (
        <View style={{ flexDirection: 'row', paddingHorizontal: 8, gap: 12 }}>
          <Pressable testID="scroll-edge-last-page" onPress={() => pagerRef.current?.setPageWithoutAnimation(2)}>
            <Text>Page {selectedPage + 1}</Text>
          </Pressable>
          <Pressable testID="scroll-edge-smooth" onPress={() => setSmooth(value => !value)}>
            <Text>Smooth {smooth ? 'ON' : 'OFF'}</Text>
          </Pressable>
          <Pressable testID="scroll-edge-replace-owner" onPress={() => setOwnerVersion(value => value + 1)}>
            <Text>Replace list {ownerVersion}</Text>
          </Pressable>
          <Pressable testID="scroll-edge-chrome-mode" onPress={() => setChromeMode(value => value === 'react' ? 'native' : value === 'native' ? 'subheader' : 'react')}>
            <Text>{chromeMode === 'react' ? 'React' : chromeMode === 'native' ? 'Native' : 'Sub'} {capable ? '✓' : '×'}</Text>
          </Pressable>
        </View>
      ) : null}
      {!enabled ? overlay : null}
      {kind === 'list' ? (
        <NativeList
          ref={listRef}
          style={{ flex: 1 }}
          snapshot={snapshot}
          topScrollEdgeEffect={enabled ? 'hard' : undefined}
          topOverlay={enabled ? overlay : undefined}
          onRefresh={onRefresh}
        />
      ) : kind === 'scroller' ? (
        <NativeScroller
          style={{ flex: 1 }}
          topScrollEdgeEffect={enabled ? 'hard' : undefined}
          topOverlay={enabled ? overlay : undefined}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        >
          {rows}
        </NativeScroller>
      ) : (
        <CollapsiblePagerView
          ref={pagerRef}
          style={{ flex: 1 }}
          initialPage={0}
          nestedScrollEnabled
          nativeSmoothHeaderScrollEnabled={smooth}
          headerHeight={100}
          stickyHeaderHeight={chromeMode === 'subheader' ? 64 : 40}
          nativeTabBar={chromeMode === 'native' ? { items: nativeItems, style: { height: 40, backgroundColor: 'rgba(232,236,242,0.25)' } } : undefined}
          nativeSubHeader={chromeMode === 'subheader' ? { items: nativeItems, selectedKey: `page-${selectedPage + 1}`, columns: { leading: 'L', middle: 'M', trailing: 'R' }, style: { height: 64, tabsHeight: 32, selectedBackgroundColor: 'transparent' } } : undefined}
          topScrollEdgeEffect={enabled ? 'hard' : undefined}
          topOverlay={enabled ? overlay : undefined}
          onScrollEdgeCapability={event => setCapable(event.nativeEvent.available)}
          onPageSelected={event => setSelectedPage(event.nativeEvent.position)}
          onNativeSubHeaderPress={event => pagerRef.current?.setPage(event.nativeEvent.position)}
          header={<View style={{ height: 100, backgroundColor: '#D8E8FF', justifyContent: 'center' }}><Text>COLLAPSIBLE ASSET HEADER</Text></View>}
          stickyHeader={chromeMode === 'react' ? <Pressable testID="scroll-edge-sticky-button" onPress={() => setStickyPressCount(value => value + 1)} style={{ height: 40, backgroundColor: 'rgba(232,236,242,0.25)', justifyContent: 'center' }}><Text>STICKY TABS · taps {stickyPressCount} · selected {selectedPage + 1}</Text></Pressable> : undefined}
        >
          {[0, 1, 2].map(page => (
            <View key={`edge-page-${page}`} style={{ flex: 1 }}>
              <NativeScroller
                key={`edge-owner-${page}-${ownerVersion}`}
                style={{ flex: 1 }}
                refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
              >
                <View style={{ height: 48, justifyContent: 'center', paddingHorizontal: 20 }}><Text>PAGE {page + 1}</Text></View>
                {rows}
              </NativeScroller>
            </View>
          ))}
        </CollapsiblePagerView>
      )}
    </View>
  );
}
