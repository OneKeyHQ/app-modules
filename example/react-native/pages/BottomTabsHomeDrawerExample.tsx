import React, { useState } from 'react';
import {
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import {
  SafeAreaProvider,
  useSafeAreaInsets,
} from 'react-native-safe-area-context';
import BottomTabs, {
  useBottomTabBarHeight,
} from '@onekeyfe/react-native-bottom-tabs';
import { CollapsiblePagerView } from '@onekeyfe/react-native-pager-view';

// A static fixture of Home's shared header and five tabs. It uses this module's
// native pager to exercise drawer arbitration; it does not import business code.
const HOME_TABS = ['Spot', 'Perps', 'Earn', 'NFT', 'History'] as const;
const ASSETS = [
  ['₿', 'Bitcoin', 'BTC', '$67,205.32', '+2.41%', '#F7931A'],
  ['Ξ', 'Ethereum', 'ETH', '$2,486.70', '+1.18%', '#7584EF'],
  ['◎', 'Solana', 'SOL', '$173.82', '+4.72%', '#A594FF'],
  ['₮', 'Tether', 'USDT', '$1.00', '+0.01%', '#40BA93'],
] as const;
const BOTTOM_ROUTES = [
  {
    key: 'wallet',
    title: 'Wallet',
    focusedIcon:
      Platform.OS === 'ios'
        ? { sfSymbol: 'wallet.bifold.fill' as const }
        : require('./bottomTabsAssets/home.png'),
  },
  {
    key: 'market',
    title: 'Market',
    focusedIcon:
      Platform.OS === 'ios'
        ? { sfSymbol: 'chart.xyaxis.line' as const }
        : require('./bottomTabsAssets/search.png'),
  },
  {
    key: 'trade',
    title: 'Trade',
    focusedIcon:
      Platform.OS === 'ios'
        ? { sfSymbol: 'arrow.left.arrow.right' as const }
        : require('./bottomTabsAssets/settings.png'),
  },
  {
    key: 'discover',
    title: 'Discover',
    focusedIcon:
      Platform.OS === 'ios'
        ? { sfSymbol: 'safari' as const }
        : require('./bottomTabsAssets/profile.png'),
  },
];

function WalletHeader() {
  return (
    <View testID="home-shared-header" style={styles.sharedHeader}>
      <Text style={styles.balanceLabel}>Total balance ◉</Text>
      <Text style={styles.balance}>$24,681.50</Text>
      <Text style={styles.balanceChange}>+$486.28 (2.01%) today</Text>
      <View style={styles.actions}>
        {[
          ['↑', 'Send'],
          ['↓', 'Receive'],
          ['⇄', 'Swap'],
          ['+', 'Buy'],
        ].map(([icon, label]) => (
          <Pressable
            key={label}
            accessibilityLabel={label}
            style={styles.action}
          >
            <View style={styles.actionIcon}>
              <Text style={styles.actionSymbol}>{icon}</Text>
            </View>
            <Text style={styles.actionLabel}>{label}</Text>
          </Pressable>
        ))}
      </View>
      <ScrollView
        horizontal
        testID="wallet-banner-scroll"
        accessibilityLabel="Wallet banners"
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.bannerContent}
      >
        {[
          [
            'Make every asset count',
            'Explore opportunities with OneKey',
            '#182A21',
          ],
          [
            'Your keys. Your peace of mind.',
            'Keep your crypto close',
            '#24223A',
          ],
          [
            'A simpler way to get started',
            'Discover your next move',
            '#1A2937',
          ],
        ].map(([title, subtitle, backgroundColor]) => (
          <View key={title} style={[styles.banner, { backgroundColor }]}>
            <View style={{ flex: 1 }}>
              <Text style={styles.bannerTitle}>{title}</Text>
              <Text style={styles.bannerSubtitle}>{subtitle}</Text>
            </View>
            <Text style={styles.bannerArrow}>↗</Text>
          </View>
        ))}
      </ScrollView>
    </View>
  );
}

function AssetList({ page }: { page: (typeof HOME_TABS)[number] }) {
  const barHeight = useBottomTabBarHeight() ?? 0;
  return (
    <ScrollView
      style={styles.list}
      testID={`wallet-list-${page.toLowerCase()}`}
      accessibilityLabel={`${page} assets`}
      contentContainerStyle={{ paddingBottom: barHeight + 28 }}
      showsVerticalScrollIndicator={false}
      contentInsetAdjustmentBehavior="never"
    >
      {page === 'Spot' ? (
        <View style={styles.cardsSection}>
          <Text style={[styles.sectionTitle, { marginHorizontal: 20 }]}>Watchlist</Text>
          <ScrollView
            horizontal
            testID="wallet-horizontal-cards"
            accessibilityLabel="Watchlist cards"
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.cardsContent}
          >
            {ASSETS.slice(0, 3).map(
              ([symbol, name, ticker, value, change, color]) => (
                <View key={ticker} style={styles.marketCard}>
                  <View style={styles.cardHeading}>
                    <Text style={[styles.cardSymbol, { color }]}>{symbol}</Text>
                    <Text style={styles.cardTicker}>{ticker}</Text>
                    <Text style={styles.green}>{change}</Text>
                  </View>
                  <Text style={styles.cardValue}>{value}</Text>
                  <Text style={styles.cardName}>{name}</Text>
                </View>
              ),
            )}
          </ScrollView>
        </View>
      ) : (
        <View style={styles.pageIntro}>
          <Text style={styles.sectionTitle}>{page}</Text>
          <Text style={styles.secondary}>Your portfolio, all in one place</Text>
        </View>
      )}
      <View style={styles.listHeading}>
        <Text style={styles.sectionTitle}>
          {page === 'History' ? 'Recent activity' : 'Assets'}
        </Text>
        <Text style={styles.secondary}>All networks ⌄</Text>
      </View>
      {Array.from({ length: 18 }, (_, index) => {
        const [symbol, name, ticker, value, change, color] =
          ASSETS[index % ASSETS.length]!;
        return (
          <Pressable
            key={index}
            style={styles.assetRow}
            accessibilityLabel={`${name} ${page} asset ${index + 1}`}
          >
            <View style={[styles.tokenIcon, { backgroundColor: color }]}>
              <Text style={styles.tokenSymbol}>{symbol}</Text>
            </View>
            <View style={styles.assetName}>
              <Text style={styles.primary}>{name}</Text>
              <Text style={styles.secondary}>
                {index === 0 ? '0.2481' : (index * 0.168 + 0.02).toFixed(4)}{' '}
                {ticker}
              </Text>
            </View>
            <View style={styles.assetAmount}>
              <Text style={styles.primary}>{value}</Text>
              <Text style={styles.green}>{change}</Text>
            </View>
          </Pressable>
        );
      })}
    </ScrollView>
  );
}

function WalletHome({ onMenu }: { onMenu: () => void }) {
  const insets = useSafeAreaInsets();
  const [activePage, setActivePage] = useState(0);
  return (
    <View style={styles.scene}>
      <View style={[styles.toolbar, { paddingTop: insets.top + 8 }]}>
        <Pressable
          onPress={onMenu}
          accessibilityLabel="Open wallet menu"
          testID="open-wallet-menu"
          style={styles.walletButton}
        >
          <View style={styles.avatar}>
            <Text style={styles.avatarLetter}>J</Text>
          </View>
          <View>
            <Text style={styles.walletName}>My wallet ⌄</Text>
            <Text style={styles.walletAddress} testID="home-active-page">0x71C7…a42E · {HOME_TABS[activePage]}</Text>
          </View>
        </Pressable>
        <Pressable
          accessibilityLabel="Search wallet"
          style={styles.toolbarButton}
        >
          <Text style={styles.toolbarSymbol}>⌕</Text>
        </Pressable>
        <Pressable accessibilityLabel="Scan code" style={styles.toolbarButton}>
          <Text style={styles.toolbarSymbol}>▣</Text>
        </Pressable>
      </View>
      <CollapsiblePagerView
        style={styles.pager}
        testID="wallet-home-pager"
        header={<WalletHeader />}
        headerHeight={292}
        stickyHeader={null}
        stickyHeaderHeight={48}
        nativeSmoothHeaderScrollEnabled
        onPageSelected={event => setActivePage(event.nativeEvent.position)}
        nativeTabBar={{
          items: HOME_TABS.map(title => ({
            key: title.toLowerCase(),
            title,
            testID: `home-tab-${title.toLowerCase()}`,
          })),
          style: {
            height: 48,
            backgroundColor: '#0C0D0F',
            activeTextColor: '#F4F5F6',
            inactiveTextColor: '#777C85',
            indicatorColor: '#00D097',
            contentPaddingHorizontal: 20,
            itemSpacing: 28,
            fontSize: 16,
          },
        }}
      >
        {HOME_TABS.map(page => (
          <View key={page} style={styles.page}>
            <AssetList page={page} />
          </View>
        ))}
      </CollapsiblePagerView>
    </View>
  );
}

function HomeDrawerFixture() {
  const insets = useSafeAreaInsets();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [tabIndex, setTabIndex] = useState(0);
  return (
    <View style={styles.container}>
      <BottomTabs
        navigationState={{ index: tabIndex, routes: BOTTOM_ROUTES }}
        onIndexChange={setTabIndex}
        renderScene={({ route }) =>
          route.key === 'wallet' ? (
            <WalletHome onMenu={() => setDrawerOpen(true)} />
          ) : (
            <View style={[styles.scene, styles.placeholder]}>
              <Text style={styles.placeholderTitle}>{route.title}</Text>
              <Text style={styles.secondary}>
                Your next opportunity starts here.
              </Text>
            </View>
          )
        }
        drawerOpen={drawerOpen}
        onDrawerOpenChange={setDrawerOpen}
        drawerSwipeEnabled
        hapticFeedbackEnabled
        androidTabBarStyle="floating"
        tabBarStyle={{ backgroundColor: '#15171A' }}
        tabBarActiveTintColor="#F4F5F6"
        tabBarInactiveTintColor="#777C85"
        renderDrawerContent={() => (
          <View style={[styles.drawer, { paddingTop: insets.top + 28 }]}>
            <Text
              style={styles.drawerHeading}
              accessibilityLabel="Wallet menu"
              testID="wallet-menu-title"
            >
              Wallets
            </Text>
            <Text style={styles.secondary}>Your assets. Your control.</Text>
            {['My wallet', 'Hardware wallet', 'Add wallet'].map(
              (name, index) => (
                <Pressable
                  key={name}
                  onPress={() => setDrawerOpen(false)}
                  style={styles.drawerWallet}
                  accessibilityLabel={name}
                >
                  <View
                    style={[
                      styles.avatar,
                      index === 0 && { backgroundColor: '#4D3A67' },
                    ]}
                  >
                    <Text style={styles.avatarLetter}>
                      {index === 2 ? '+' : index === 0 ? 'J' : '◇'}
                    </Text>
                  </View>
                  <View>
                    <Text style={styles.primary}>{name}</Text>
                    <Text style={styles.secondary}>
                      {index === 0
                        ? 'Selected wallet'
                        : index === 1
                        ? 'Connect securely'
                        : 'Create or import'}
                    </Text>
                  </View>
                  {index === 0 ? <Text style={styles.selected}>✓</Text> : null}
                </Pressable>
              ),
            )}
            <View style={styles.drawerFooter}>
              <Pressable
                onPress={() => setDrawerOpen(false)}
                accessibilityLabel="Close wallet menu"
                testID="close-wallet-menu"
              >
                <Text style={styles.primary}>Settings and preferences ›</Text>
              </Pressable>
              <Text style={[styles.secondary, { marginTop: 24 }]}>OneKey</Text>
            </View>
          </View>
        )}
      />
    </View>
  );
}

export function BottomTabsHomeDrawerExample() {
  return (
    <SafeAreaProvider>
      <HomeDrawerFixture />
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0C0D0F' },
  scene: { flex: 1, backgroundColor: '#0C0D0F' },
  toolbar: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingBottom: 14,
  },
  walletButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  avatar: {
    width: 34,
    height: 34,
    borderRadius: 17,
    backgroundColor: '#2D4943',
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarLetter: { color: '#E5FFF4', fontSize: 17, fontWeight: '600' },
  walletName: { color: '#F4F5F6', fontSize: 16, fontWeight: '600' },
  walletAddress: { color: '#777C85', fontSize: 11, marginTop: 3 },
  toolbarButton: { paddingLeft: 20, paddingVertical: 8 },
  toolbarSymbol: { color: '#F4F5F6', fontSize: 26 },
  pager: { flex: 1 },
  page: { flex: 1, backgroundColor: '#0C0D0F' },
  sharedHeader: { height: 292, backgroundColor: '#0C0D0F', paddingTop: 8 },
  balanceLabel: { color: '#9297A1', fontSize: 13, paddingHorizontal: 20 },
  balance: {
    color: '#F4F5F6',
    fontSize: 48,
    fontWeight: '600',
    letterSpacing: -1.5,
    marginTop: 4,
    paddingHorizontal: 20,
  },
  balanceChange: {
    color: '#00D097',
    fontSize: 13,
    marginTop: 4,
    paddingHorizontal: 20,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 20,
    marginBottom: 18,
    paddingHorizontal: 8,
  },
  action: { alignItems: 'center', gap: 7 },
  actionIcon: {
    width: 44,
    height: 40,
    borderRadius: 14,
    backgroundColor: '#202327',
    alignItems: 'center',
    justifyContent: 'center',
  },
  actionSymbol: { color: '#F4F5F6', fontSize: 25, lineHeight: 29 },
  actionLabel: { color: '#D7DADF', fontSize: 12 },
  bannerContent: { paddingHorizontal: 20, gap: 12 },
  banner: {
    width: 300,
    height: 76,
    borderRadius: 14,
    padding: 14,
    flexDirection: 'row',
    alignItems: 'center',
  },
  bannerTitle: { color: '#EAF4EE', fontSize: 14, fontWeight: '600' },
  bannerSubtitle: { color: '#8EA499', fontSize: 11, marginTop: 6 },
  bannerArrow: { color: '#CBECDB', fontSize: 28, paddingLeft: 8 },
  list: { flex: 1, backgroundColor: '#0C0D0F' },
  cardsSection: { paddingTop: 18 },
  sectionTitle: { color: '#F4F5F6', fontSize: 16, fontWeight: '600' },
  cardsContent: {
    paddingHorizontal: 20,
    gap: 12,
    paddingTop: 14,
    paddingBottom: 8,
  },
  marketCard: {
    width: 204,
    padding: 14,
    backgroundColor: '#191C20',
    borderRadius: 14,
  },
  cardHeading: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  cardSymbol: { fontSize: 20 },
  cardTicker: { color: '#D7DADF', fontSize: 13, flex: 1 },
  cardValue: {
    color: '#F4F5F6',
    fontSize: 22,
    fontWeight: '600',
    marginTop: 12,
  },
  cardName: { color: '#777C85', fontSize: 11, marginTop: 5 },
  pageIntro: { padding: 20, gap: 8 },
  listHeading: {
    paddingHorizontal: 20,
    paddingTop: 22,
    paddingBottom: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  assetRow: {
    minHeight: 78,
    paddingHorizontal: 20,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  tokenIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    alignItems: 'center',
    justifyContent: 'center',
  },
  tokenSymbol: { color: 'white', fontSize: 25, fontWeight: '600' },
  assetName: { flex: 1, gap: 6 },
  assetAmount: { alignItems: 'flex-end', gap: 6 },
  primary: { color: '#F4F5F6', fontSize: 15, fontWeight: '500' },
  secondary: { color: '#777C85', fontSize: 12 },
  green: { color: '#00D097', fontSize: 11 },
  placeholder: { justifyContent: 'center', alignItems: 'center', gap: 12 },
  placeholderTitle: { color: '#F4F5F6', fontSize: 30, fontWeight: '600' },
  drawer: { flex: 1, backgroundColor: '#101114', paddingHorizontal: 22 },
  drawerHeading: {
    color: '#F4F5F6',
    fontSize: 28,
    fontWeight: '600',
    marginBottom: 8,
  },
  drawerWallet: {
    flexDirection: 'row',
    gap: 12,
    alignItems: 'center',
    paddingVertical: 20,
  },
  selected: { color: '#00D097', fontSize: 20, marginLeft: 'auto' },
  drawerFooter: { marginTop: 'auto', paddingBottom: 40 },
});
